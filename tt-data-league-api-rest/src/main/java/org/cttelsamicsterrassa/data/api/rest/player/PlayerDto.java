package org.cttelsamicsterrassa.data.api.rest.player;

import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;
import java.lang.reflect.Method;

/*
    Player ID -> SeasonPlayer.id
    Practitioner ID -> SeasonPlayer.clubMember.practicioner.id
    Full Name -> SeasonPlayer.clubMember.fullName
    Year Range -> SeasonPlayer.yearRange
*/

public record PlayerDto(
        UUID id,
        UUID practitionerId,
        String fullName,
        String clubName,
        String yearRange
        ) {

    public static PlayerDto fromObject(Object domainObj) {
        SeasonPlayer seasonPlayer = (SeasonPlayer) domainObj;
        if (seasonPlayer == null) return null;
        return new PlayerDto(
                seasonPlayer.getId(),
                seasonPlayer.getClubMember().getPracticioner().getId(),
                seasonPlayer.getClubMember().getFullName(),
                seasonPlayer.getClubMember().getClub().getName(),
                seasonPlayer.getYearRange()
        );
    }

    public static PlayerDto fromObjectBACK(Object domainObj) {
        if (domainObj == null) return null;

        // If it's already a PlayerDto, return as-is
        if (domainObj instanceof PlayerDto) return (PlayerDto) domainObj;

        // If it's a Map, try to extract fields by common keys
        if (domainObj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) domainObj;
            UUID id = parseUuid(map.get("id"));
            if (id == null) id = parseUuid(map.get("uuid"));
            String username = firstNonNullString(map.get("username"), map.get("name"), map.get("user"));
            Integer ranking = parseInteger(map.get("ranking"), map.get("rank"), map.get("position"));
            return null;
        }

        // Try to read common getters via reflection: getId/getUuid/getName/getUsername/getRanking/getRank
        try {
            UUID id = tryCallUuidGetter(domainObj, "getId", "getUuid", "id", "uuid");
            String username = tryCallStringGetter(domainObj, "getUsername", "getName", "username", "name");
            Integer ranking = tryCallIntegerGetter(domainObj, "getRanking", "getRank", "ranking", "rank");
            return null;
        } catch (Exception e) {
            // Fallback: use toString as username
            return null;
        }
    }

    public static List<PlayerDto> fromObjectList(Object payload) {
        if (payload == null) return Collections.emptyList();
        if (payload instanceof List) {
            List<?> raw = (List<?>) payload;
            return raw.stream().map(PlayerDto::fromObject).collect(Collectors.toList());
        }
        // If single object, return single-element list
        return List.of(fromObject(payload));
    }

    private static UUID parseUuid(Object val) {
        if (val == null) return null;
        if (val instanceof UUID) return (UUID) val;
        try {
            return UUID.fromString(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer parseInteger(Object... candidates) {
        for (Object c : candidates) {
            if (c == null) continue;
            if (c instanceof Integer) return (Integer) c;
            try {
                return Integer.valueOf(c.toString());
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static String firstNonNullString(Object... candidates) {
        for (Object c : candidates) {
            if (c == null) continue;
            String s = c.toString();
            if (!s.isBlank()) return s;
        }
        return null;
    }

    private static UUID tryCallUuidGetter(Object obj, String... methodNames) {
        for (String name : methodNames) {
            Object val = tryCallMethod(obj, name);
            UUID u = parseUuid(val);
            if (u != null) return u;
        }
        return null;
    }

    private static String tryCallStringGetter(Object obj, String... methodNames) {
        for (String name : methodNames) {
            Object val = tryCallMethod(obj, name);
            if (val != null) return val.toString();
        }
        return null;
    }

    private static Integer tryCallIntegerGetter(Object obj, String... methodNames) {
        for (String name : methodNames) {
            Object val = tryCallMethod(obj, name);
            if (val == null) continue;
            if (val instanceof Integer) return (Integer) val;
            try {
                return Integer.valueOf(val.toString());
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static Object tryCallMethod(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            return m.invoke(obj);
        } catch (NoSuchMethodException e) {
            // try without get prefix (field name)
            try {
                Method m = obj.getClass().getMethod(methodName);
                return m.invoke(obj);
            } catch (Exception ignored) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
