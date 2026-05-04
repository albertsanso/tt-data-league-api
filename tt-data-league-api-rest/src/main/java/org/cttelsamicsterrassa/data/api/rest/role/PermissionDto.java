package org.cttelsamicsterrassa.data.api.rest.role;

import java.util.UUID;

public record PermissionDto(UUID id, String resource, String action) {
}

