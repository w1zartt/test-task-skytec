package config.exception;

public class InvalidRequestException extends ApplicationException {
    public InvalidRequestException(String msg) {
        super(400, msg);
    }

    public InvalidRequestException() {
        super(400, "Invalid request");
    }
}
