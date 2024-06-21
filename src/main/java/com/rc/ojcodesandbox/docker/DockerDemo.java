package com.rc.ojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * @Author：rancheng
 * @name：DockerDemo
 * @Date：2024/6/20 12:29
 */
public class DockerDemo {
    public static void main(String[] args) {
        //获取默认的 Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        PingCmd pingCmd = dockerClient.pingCmd();
        pingCmd.exec();
    }
}
