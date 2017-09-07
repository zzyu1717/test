package org.test.thread;
/**
* 暴力停止线程
* @author zzy 2017年9月7日
* @version
*/
public class ThreadService {
	
	/**
	 * 定义关闭所需执行线程
	 */
	private Thread executeThread;
	
	/**
	 * true表示线程运行结束
	 */
	private boolean finished = false;
	
	public void execute(Runnable task) {
		
		executeThread = new Thread() {
			@Override
			public void run() {
				/**
				 * 定义任务执行线程，并设置其为守护线程。当executeThread关闭时，守护线程也随之关闭
				 */
				Thread runner = new Thread(task); 
				runner.setDaemon(true);
				runner.start();
				
				try {
					//表示executeThread线程只有等待runner线程死亡之后才可以继续执行
					runner.join();
					// 线程执行结束
					finished = true;
				} catch (InterruptedException e) {
//					e.printStackTrace();
				}
			}
		};
		
		executeThread.start();
	}
	
	/**
	 * 设置线程在限定时间内结束
	 * @param millis 最多执行时间
	 */
	public void shutdown(long millis) {
		long currentTime = System.currentTimeMillis();
		/**
		 * 线程还没执行结束
		 */
		while (!finished) {
			// 线程超时
			if (System.currentTimeMillis() - currentTime >= millis) {
				System.out.println("线程运行超时！需要结束");
				executeThread.interrupt();
				break;
			}
			
			// 若任务线程未超时，main线程休眠1ms,把资源让给执行线程
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
