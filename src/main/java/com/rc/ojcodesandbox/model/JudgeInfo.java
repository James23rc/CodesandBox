package com.rc.ojcodesandbox.model;

import lombok.Data;

/**
 * @Author：rancheng
 * @name：JudgeConfig
 * @Date：2024/6/11 15:56
 * 判题信息
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;
    /**
     * 消耗内存kb
     */
    private Long memory;
    /**
     * 消耗时间
     */
    private Long time;
}
