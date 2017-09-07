package org.test.thread;
/**
* 
* @author zzy 2017年9月7日
* @version
*/
public class Test {
	public static void main(String[] args) {
		ThreadService t = new ThreadService();
		long start = System.currentTimeMillis();
		t.execute(()->{
			try {
				Thread.sleep(1000*50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}); 
		
		t.shutdown(1000*40L);
		long end = System.currentTimeMillis();
		System.out.println("线程执行总共花费了：" + (end-start) + "毫秒");
	}
}
