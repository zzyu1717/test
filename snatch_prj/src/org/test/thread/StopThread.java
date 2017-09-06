package org.test.thread;

/**
 * 
 * @author zzy 2017年9月6日
 * @version
 */
public class StopThread {
	public static void main(String[] args) {
		
//		Worker worker = new Worker();
//		worker.start();
		
		Student stu = new Student();
		stu.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stu.interrupt();
	}

}
/**
 *1、 使用标志位结束线程
 * @author ZZY
 * @date   2017年9月6日
 */
class Worker extends Thread {
	/**
	 * 必须加上volatile
	 */
	private volatile boolean flag = true;


	@Override
	public void run() {
		while (true) {
			//...业务代码
			if (!flag) {
				break;
			}
		}
		System.out.println("线程结束");
	}
	
	public void shutdown() {
		flag = false;
	}

}

/**
 * 2、使用捕获Interupet方式
 * @author ZZY
 * @date   2017年9月6日
 */
class Student extends Thread {
	@Override
	public void run() {
		while (true) {
			if (Thread.interrupted()) {
				break;
			}
		}
		System.out.println("线程结束");
	}
}











