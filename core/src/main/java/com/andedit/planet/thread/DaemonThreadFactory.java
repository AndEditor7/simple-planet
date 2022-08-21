package com.andedit.planet.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory  {
	private final String name;
	private AtomicInteger num;
	
	public DaemonThreadFactory(String name) {
		this.name = name;
		num = new AtomicInteger(1);
	}
	
	@Override
	public Thread newThread(Runnable run) {
		Thread thread = new Thread(run, name + '-' + num.getAndIncrement());
		thread.setDaemon(true);
		return thread;
	}
}