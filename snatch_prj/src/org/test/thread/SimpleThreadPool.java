package org.test.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 
 * @author zzy 2017年9月16日
 * @version
 */
public class SimpleThreadPool extends Thread{
	// 启动初始线程数
	private int size;
	// 任务队列能同时执行的任务数
	private final int queSize;
	private static final int DEFAULT_QUEUE_SIZE = 2000;
	private static volatile int seq = 0;
	private static final String THREAD_PREFIX = "SIMPLE_THREAD_POOL-";

	private static final ThreadGroup GROUP = new ThreadGroup("Pool_Group");

	private static final LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();

	private static final List<WorkTask> THREAD_QUEUE = new ArrayList<>();

	private final DiscardPolicy discardPolicy;

	private volatile boolean destroy = false;

	private int min;
	private int active;
	private int max;
	

	public static final DiscardPolicy DEFAULT_DISCARD_POLICY = () -> {
		throw new DiscardException("***the queue is full, discard this task");
	};

	public SimpleThreadPool() {
		this(4,8,12, DEFAULT_QUEUE_SIZE, DEFAULT_DISCARD_POLICY);
	}

	public SimpleThreadPool(int min,int active, int max, int queSize, DiscardPolicy discardPolicy) {
		this.min = min;
		this.active = active;
		this.max = max;
		this.queSize = queSize;
		this.discardPolicy = discardPolicy;
		init();
	}

	private void init() {
		for (int i = 0; i < min; i++) {
			createWorkTask();
		}
		this.size = min;
		this.start();
	}
	
	@Override
	public void run() {
		while (!destroy) {
			System.out.printf(">>>Pool#Min:%d,Active:%d,Max:%d,Current:%d,QueueSize:%d\n",
					min,active,max,size,TASK_QUEUE.size());
			try {
				Thread.sleep(2_000L);
				
				if (TASK_QUEUE.size() > active && size < active) {
					for (int i = size; i < active; i++) {
						createWorkTask();
					}
					System.out.println("The pool increment to active");
					size = active;
				} else if (TASK_QUEUE.size() > max && size < max) {
					for (int i = size; i < max; i++) {
						createWorkTask();
					}
					System.out.println("The pool increment to max");
					size = max;
					
				}
				
				synchronized (THREAD_QUEUE) {
					if (TASK_QUEUE.isEmpty() && size > active) {
						System.out.println("==========Reduce===========");
						int releaseSize = size - active;
						
						for (Iterator<WorkTask> iter = THREAD_QUEUE.iterator(); iter.hasNext();) {
							if (releaseSize <= 0) {
								break;
							}
							
							WorkTask wt = iter.next();
							if (wt.getTaskState() == TaskState.BLOCKED) {
								wt.interrupt();
								wt.close();
								iter.remove();
								releaseSize --;
							}
							
						}
						size = active;
					}
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createWorkTask() {
		WorkTask t = new WorkTask(GROUP, THREAD_PREFIX + (seq++));
		t.start();
		THREAD_QUEUE.add(t);
	}

	public void submit(Runnable task) {
		if (destroy) {
			throw new IllegalStateException("group pool is closed, not allow add task!");
		}
		synchronized (TASK_QUEUE) {
			if (TASK_QUEUE.size() > queSize) {
				try {
					discardPolicy.discard();
				} catch (DiscardException e) {
					e.printStackTrace();
				}
			}
			TASK_QUEUE.addLast(task);
			TASK_QUEUE.notifyAll();
		}
	}

	public void shutdown() {
		while (!TASK_QUEUE.isEmpty()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		synchronized (THREAD_QUEUE) {
			int initVal = THREAD_QUEUE.size();
			while (initVal > 0) {
				for (WorkTask taskThread : THREAD_QUEUE) {
					if (taskThread.getTaskState() == TaskState.BLOCKED) {
						taskThread.interrupt();
						taskThread.close();
						initVal--;
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		this.destroy = true;
		System.out.println("group pool is closed!");
	}

	public static class DiscardException extends RuntimeException {
		public DiscardException(String message) {
			super(message);
		}
	}

	public interface DiscardPolicy {
		void discard() throws DiscardException;
	}

	private static class WorkTask extends Thread {
		private volatile TaskState taskState = TaskState.FREE;

		public WorkTask(ThreadGroup tg, String name) {
			super(tg, name);
		}

		public TaskState getTaskState() {
			return this.taskState;
		}

		@Override
		public void run() {
			OUTER: while (this.taskState != TaskState.DEAD) {
				Runnable runnable;
				synchronized (TASK_QUEUE) {
					while (TASK_QUEUE.isEmpty()) {
						taskState = TaskState.BLOCKED;
						try {
							TASK_QUEUE.wait();
						} catch (InterruptedException e) {
							System.out.println("closed");
							break OUTER;
						}
					}
					runnable = TASK_QUEUE.removeFirst();
				}

				if (runnable != null) {
					taskState = TaskState.RUNNING;
					// execute task
					runnable.run();
					taskState = TaskState.FREE;
				}
			}
		}

		public void close() {
			this.taskState = TaskState.DEAD;
		}
	}

	private enum TaskState {
		FREE, RUNNING, BLOCKED, DEAD
	}

	public static void main(String[] args) {
//		SimpleThreadPool threadPool = new SimpleThreadPool(10, 6, SimpleThreadPool.DEFAULT_DISCARD_POLICY);
		 SimpleThreadPool threadPool = new SimpleThreadPool();

 
		IntStream.rangeClosed(1, 100).forEach(i -> threadPool.submit(() -> {
			System.out.println("the task [" + i + "] serviced by " + Thread.currentThread().getName() + " start!");
			// each task execute 10ms
			try {
				Thread.sleep(1_000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\tthe task [" + i + "] serviced by " + Thread.currentThread().getName() + " end!");
		})

		);
 
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		threadPool.shutdown();
		
//		threadPool.submit(()->{});
		System.out.println(">>>main ended");
	}

}
