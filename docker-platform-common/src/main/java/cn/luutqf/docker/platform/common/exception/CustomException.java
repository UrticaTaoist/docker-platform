package cn.luutqf.docker.platform.common.exception;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
public class CustomException extends RuntimeException {

    private Integer code;

    public CustomException(Integer code) {
        this.code = code;
    }

    public CustomException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public CustomException() {
    }

    public Integer getCode() {
        return code;
    }
}