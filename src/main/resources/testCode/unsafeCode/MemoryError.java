import java.util.ArrayList;
import java.util.List;
/**
 * 无线占用空间（浪费系统内存）
 * @Author：rancheng
 * @name：MemoryError
 * @Date：2024/6/17 21:45
 */
public class Main {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while(true){
            bytes.add(new byte[100000]);
        }
    }
}
