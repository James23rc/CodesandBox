package com.rc.ojcodesandbox.unsafe;

/**
 * 无线睡眠程序，(阻塞程序执行)，占有资源不释放
 * @Author：rancheng
 * @name：SleepError
 * @Date：2024/6/17 21:36
 *
 * 解决方式
 * //超时控制
 *                 //定义一个守护线程，等待TimeOut时间，查看程序是否结束，如果运行超出时间，杀死该代码程序
 *                 new Thread(() -> {
 *                     try {
 *                         Thread.sleep(TIME_OUT);
 *                         System.out.println("代码运行超时，中断");
 *                         runProcess.destroy();
 *                     } catch (InterruptedException e) {
 *                         throw new RuntimeException(e);
 *                     }
 *                 }).start();
 */
public class SleepError {
    public static void main(String[] args) throws InterruptedException {
        long ONE_HOUR = 60*60*1000L;
        Thread.sleep(ONE_HOUR);
        System.out.println("sleep Over");
    }
}
