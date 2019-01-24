//package com.alibaba.metrics.reporter.bin;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.metrics.reporter.file.FileMetricManagerReporter;
//
//public final class SyncThreadPool {
//	
//	Map<String, Runnable> shutdownRun = new ConcurrentHashMap<String, Runnable>();
//	private final Logger logger = LoggerFactory.getLogger(SyncThreadPool.class);
//	
//	private final ScheduledExecutorService syncExecutor;
//    
//    public SyncThreadPool(int syncPoolSize) {
//        ThreadFactory poolThreadFactory = new ThreadFactory() {
//			
//			@Override
//			public Thread newThread(Runnable r) {
//				return new Thread(r);
//			}
//		};
//
//        this.syncExecutor = Executors.newScheduledThreadPool(syncPoolSize, poolThreadFactory);
//    }
//    
//    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
//        return syncExecutor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
//    }
//    
//    private class ShutdownThread extends Thread{
//    	
//        public ShutdownThread() {
//            super("Sync-ThreadPool-Shutdown for " + SyncThreadPool.this);
//        }
//
//        @Override
//        public void run(){
//        	logger.info("正在关闭落盘线程池...");
//            try {
//				syncExecutor.awaitTermination(10, TimeUnit.SECONDS);
//				//此处加上逐一force
//			} catch (InterruptedException e) {
//				logger.info("落盘线程池关闭超时");
//				e.printStackTrace();
//			}
//            logger.info("落盘线程池已关闭");
//        }
//    }
//    
//    public void regsister(String name, Runnable r){
//    	if (shutdownRun.containsKey(name)){
//    		logger.info("已存在名字为" + name + "的backend同步线程");
//    	}else{
//    		shutdownRun.put(name, r);
//    	}
//    	
//    }
//    
//    public void unResister(String name){
//    	shutdownRun.remove(name);
//    }
//    
//}
