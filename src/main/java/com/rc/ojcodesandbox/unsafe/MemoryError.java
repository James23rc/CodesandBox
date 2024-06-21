package com.rc.ojcodesandbox.unsafe;

import java.util.ArrayList;
import java.util.List;

/**
 * 无线占用空间（浪费系统内存）
 * @Author：rancheng
 * @name：MemoryError
 * @Date：2024/6/17 21:45
 * 通过设置程序启动的时候 Xmx 最大堆大小的限制，当程序达到最大堆大小的时候 程序oom
 *
 * 例如java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s
 */
public class MemoryError {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while(true){
            bytes.add(new byte[100000]);
        }
    }
}
