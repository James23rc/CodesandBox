package com.rc.ojcodesandbox.model;

import lombok.Data;

/**
 * @Author：rancheng
 * @name：ExecuteMessage
 * @Date：2024/6/16 22:39
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;

    private Long memory;

}
