package config.exception;

public class MethodNotAllowedException extends ApplicationException {
    public MethodNotAllowedException(String msg) {
        super(405, msg);
    }

    public MethodNotAllowedException() {
        super(405, "Method is not allowed");
    }
}
