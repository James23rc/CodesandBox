
/**
 * 无线睡眠程序，(阻塞程序执行)，占有资源不释放
 * @Author：rancheng
 * @name：SleepError
 * @Date：2024/6/17 21:36
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        long ONE_HOUR = 60*60*1000L;
        Thread.sleep(ONE_HOUR);
        System.out.println("sleep Over");
    }
}
