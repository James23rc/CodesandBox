package com.rc.ojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：rancheng
 * @name：ExecuteCodeResponse
 * @Date：2024/6/13 19:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 输出用例
     */
    private List<String> outputList;
    /**
     * 执行信息,代码沙箱的一些信息，代码沙箱实际
     */
    private String message;
    /**
     * 执行状态
     */
    private Integer status;
    /**
     * 代码执行信息，堆栈/内存的使用，代码执行时间
     */
    private JudgeInfo judgeInfo;
}
