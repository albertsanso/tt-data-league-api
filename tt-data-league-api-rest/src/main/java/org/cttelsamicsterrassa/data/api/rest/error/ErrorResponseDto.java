package org.cttelsamicsterrassa.data.api.rest.error;

import java.util.Map;

public record ErrorResponseDto(
        String code,
        String message,
        Map<String, Object> details
) {
    public ErrorResponseDto(String code, String message) {
        this(code, message, Map.of());
    }
}

