package com.rc.ojcodesandbox;


import com.rc.ojcodesandbox.model.ExecuteCodeRequest;
import com.rc.ojcodesandbox.model.ExecuteCodeResponse;

/**
 * @Author：rancheng
 * @name：CodeSandBox
 * @Date：2024/6/13 19:54
 */
public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) throws InterruptedException;
}
