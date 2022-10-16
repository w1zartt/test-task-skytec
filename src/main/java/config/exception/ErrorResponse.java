package config.exception;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    int code;
    String message;
}
