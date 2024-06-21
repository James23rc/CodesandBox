package com.rc.ojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.rc.ojcodesandbox.CodeSandbox;
import com.rc.ojcodesandbox.JavaNativeCodeSandbox;
import com.rc.ojcodesandbox.model.ExecuteCodeRequest;
import com.rc.ojcodesandbox.model.ExecuteCodeResponse;
import com.rc.ojcodesandbox.model.ExecuteMessage;
import com.rc.ojcodesandbox.model.JudgeInfo;
import com.rc.ojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：rancheng
 * @name：JavaCodeSandboxTemplate 代码沙箱模板模式实现
 * @Date：2024/6/20 16:37
 * /**
 * * 1. 把⽤户的代码保存为⽂件
 * * 2. 编译代码，得到 class ⽂件
 * * 3. 执⾏代码，得到输出结果
 * * 4. 收集整理输出结果
 * * 5. ⽂件清理，释放空间
 * * 6. 错误处理，提升程序健壮性
 */
@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {
    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";
    private static final long TIME_OUT = 5000L;
    public static final String GLOBAL_CODE_PATH_NAME;

    static {
        // 获取项目的根目录
        String userDir = System.getProperty("user.dir");
        GLOBAL_CODE_PATH_NAME = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码⽬录是否存在，没有则新建 /temp
        if (!FileUtil.exist(GLOBAL_CODE_PATH_NAME)) {
            FileUtil.mkdir(GLOBAL_CODE_PATH_NAME);
        }
    }

    /**
     * 代码沙箱执行用户提交代码
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        //1.将用户代码保存为文件
        File userCodeFile = saveCodeToFile(code);
        //2.编译代码，得到class文件，存放在.java文件目录下
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        System.out.println(compileFileExecuteMessage);
        //3.执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);
        //4.收集整理输出结果
        ExecuteCodeResponse executeCodeResponse = getOutputResponse(executeMessageList);
        //5.文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error,userCodeFilePath = {}", userCodeFile.getParent());
        }
        return executeCodeResponse;
    }

    /**
     * 1.将请求中的代码生成.java文件到指定目录
     *
     * @return
     */
    public File saveCodeToFile(String code) {
        //1.把⽤户的代码隔离存放,uuid生成的父目录，user.dir/tempCode/UUID
        String userCodeParentPath = GLOBAL_CODE_PATH_NAME + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2.编译上一步得到的文件,获得.class文件
     *
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        //2.编译代码，得到class文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        ExecuteMessage executeMessage;
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println("编译执行信息：" + executeMessage);
            if (executeMessage.getExitValue() != 0) {
                return executeMessage;
            }
        } catch (Exception e) {
//            return getErrorResponse(e);
            throw new RuntimeException(e);
        }
        return executeMessage;
    }

    /**
     * 3.执行文件获得 代码运行结果
     *
     * @param userCodeFile
     * @param inputList
     * @return List<ExecuteMessage>
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        //获得编译后代码的父路径
        String userCodeParentPath = userCodeFile.getParent();
        //3.执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArg : inputList) {
//            System.out.println("运行的绝对路径是:"+userCodeParentPath);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArg);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                //超时控制
                //定义一个守护线程，等待TimeOut时间，查看程序是否结束，如果运行超出时间，杀死该代码程序
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("代码运行超时，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println("代码执行信息：" + executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }

    /**
     * 4.根据执行信息列表，封装代码沙箱的执行结果的返回
     *
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            if (StrUtil.isNotBlank(executeMessage.getErrorMessage())) {
                String errorMessage = executeMessage.getErrorMessage();
                executeCodeResponse.setMessage(errorMessage);
                //用户代码执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            Long time = executeMessage.getTime();
            //获得所有程序中，执行的最大值，（多个执行命令中，有一个超时 证明算法超时）
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
            outputList.add(executeMessage.getMessage());
        }
        //状态为1，正常运行完成
        executeCodeResponse.setOutputList(outputList);
        if (outputList.size() == executeMessageList.size()) {
            //代码正确运行
            executeCodeResponse.setStatus(1);
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        executeCodeResponse.setJudgeInfo(judgeInfo);
        //代码沙箱只负责返回代码沙箱的运行信息，并不是判题信息,
        //要借助第三方库来获取内存占用
//        judgeInfo.setMemory();
        judgeInfo.setTime(maxTime);
        return executeCodeResponse;
    }

    /**
     * 5.删除用户文件
     *
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeFile.getParent());
            System.out.println("删除：" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }


    /**
     * 获得错误响应
     *
     * @param e
     * @return
     */
    //6.错误处理，提升程序健壮性
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        //表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
