package rest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import config.exception.ExceptionHandler;
import config.exception.InvalidRequestException;
import io.vavr.control.Try;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

public abstract class Handler {
    private final ObjectMapper objectMapper;
    private final ExceptionHandler exceptionHandler;

    public Handler(ObjectMapper objectMapper, ExceptionHandler exceptionHandler) {
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    protected abstract void execute(HttpExchange exchange) throws Exception;

    public void handle(HttpExchange exchange) {
        Try.run(() -> execute(exchange))
                .onFailure(thr -> exceptionHandler.handle(thr, exchange));
    }

    protected <T> T readRequest(InputStream is, Class<T> type) {
        return Try.of(() -> objectMapper.readValue(is, type))
         .getOrElseThrow(() -> new InvalidRequestException());
    }

    protected <T> byte[] writeResponse(T response) {
        return Try.of(() -> objectMapper.writeValueAsBytes(response))
        .getOrElseThrow(() -> new InvalidRequestException());
    }

    protected Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }

    protected Long extractPathVariable(String path) {
        final String[] split = path.split("/");
        final String s1 = split[split.length - 1];
        return Try.of(() -> Long.parseLong(s1))
                .getOrElseThrow(() -> new InvalidRequestException());
    }

    protected Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }
}
