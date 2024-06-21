package com.rc.ojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：rancheng
 * @name：ExecuteCodeRequest
 * @Date：2024/6/13 19:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    /**
     * 输入用例
     */
    private List<String> inputList;
    /**
     * 输入代码
     */
    private String code;
    /**
     * 编译语言
     */
    private String language;

}
