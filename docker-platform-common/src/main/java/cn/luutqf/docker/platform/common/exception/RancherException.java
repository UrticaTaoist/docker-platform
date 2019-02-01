package cn.luutqf.docker.platform.common.exception;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
public class RancherException extends CustomException {
    public RancherException(String message, Integer code) {
        super(message, code);
    }

    public RancherException(Integer code) {
        super(code);
    }
}