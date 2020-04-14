package cn.wait.demo.exception;

/**
 * @description: 自定义异常
 * @author: maxiao1
 * @date: 2019/9/13 17:21
 */
public class TokenException extends Exception {


    public TokenException() {
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(Throwable cause) {
        super(cause);
    }

    public TokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
