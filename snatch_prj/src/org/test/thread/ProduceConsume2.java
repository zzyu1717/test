package org.test.thread;

import java.util.stream.Stream;

/**
 * 
 * @author zzy 2017年9月11日
 * @version
 */
public class ProduceConsume2 {

	private final Object lock = new Object();

	private volatile boolean isProduced = false;

	private int i;

	public void produce() {
		synchronized (lock) {
			while (isProduced) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			i++;
			System.out.println("P-> " + i);
			isProduced = true;
			lock.notifyAll();

		}
	}

	public void consume() {
		synchronized (lock) {
			while (!isProduced) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("\tC-> " + i);
			isProduced = false;
			lock.notifyAll();

		}
	}

	public static void main(String[] args) {
		ProduceConsume2 dc1 = new ProduceConsume2();

		Stream.of("p1", "p2","p3").forEach(p -> new Thread() {
			@Override
			public void run() {
				while (true) {
					dc1.produce();
				}
			}
		}.start());

		Stream.of("c1", "c2","c3").forEach(c -> new Thread() {
			@Override
			public void run() {
				while (true) {
					dc1.consume();
				}
			}
		}.start());
	}

}
