package config.exception;

import lombok.Getter;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String msg) {
        super(404,msg);
    }
}
