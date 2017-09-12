package org.test.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
* 
* @author zzy 2017年9月11日
* @version
*/
public class ControlThreadCount {
	
	private static LinkedList<Control> controls = new LinkedList<>();
	
	private static final int MAX_WORK = 5;
	
	public static void main(String[] args) {
		
		// 保存正在执行的线程
		List<Thread> worker = new ArrayList<>();
		/**
		 * 启动10个线程
		 */
		Arrays.asList("M1","M2","M3","M4","M5","M6","M7","M8","M9","M10").stream()
		.map(ControlThreadCount::createThread).forEach(t-> {
			t.start();
			worker.add(t);
		});
		
		worker.stream().forEach(t->{
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		// 等待所有子线程执行完毕之后执行
		Optional.of("All of work finished").ifPresent(System.out::println);
		
	}
	
	
	public static Thread createThread(String name) {
		return new Thread(()->{
			Optional.of("The Worker [" + Thread.currentThread().getName() + "] begin capture data!").ifPresent(System.out::println);
			
			// 只能同时运行5个线程
			synchronized (controls) {
				while (controls.size() >= MAX_WORK) {
					try {
						controls.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				controls.add(new Control());
			}
			// 模拟机器工作
			Optional.of("The Worker [" + Thread.currentThread().getName() + "] is working...").ifPresent(System.out::println);
			try {
				Thread.sleep(60_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 唤醒等待线程
			synchronized (controls) {
				Optional.of("The Worker [" + Thread.currentThread().getName() + "] is done").ifPresent(System.out::println);
				controls.removeFirst();
				controls.notifyAll();
			}
		},name);
		
	}
	
	private static class Control {
		
	}
	
	
}
