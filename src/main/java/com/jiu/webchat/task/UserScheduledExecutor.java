package com.jiu.webchat.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Package com.jiu.webchat.task
 * ClassName UserScheduledExecutor.java
 * Description 用户缓存线程池
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-15 13:54
 **/
public class UserScheduledExecutor {

    public static void main(String[] args) throws InterruptedException {
        // 创建大小为5的线程池
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        UserTask worker = new UserTask();
        // 周期性执行，每5秒执行一次
        scheduledThreadPool.scheduleAtFixedRate(worker, 0,5, TimeUnit.SECONDS);
        Thread.sleep(10000);
        System.out.println("Shutting down executor...");
        // 关闭线程池
        scheduledThreadPool.shutdown();
        boolean isDone;
        // 等待线程池终止
        do {
            isDone = scheduledThreadPool.awaitTermination(1, TimeUnit.DAYS);
            System.out.println("awaitTermination...");
        } while(!isDone);
        System.out.println("Finished all threads");
    }

}
