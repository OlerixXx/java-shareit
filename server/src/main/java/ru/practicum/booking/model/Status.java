package ru.practicum.booking.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Status {
    APPROVED,
    WAITING,
    REJECTED,
    CANCELED;

    private static final Map<String, List<Status>> statusMap = new HashMap<>() {{
        put("ALL", List.of(APPROVED, WAITING, REJECTED, CANCELED));
        put("CURRENT", List.of(APPROVED, REJECTED));
        put("WAITING", List.of(WAITING));
        put("REJECTED", List.of(REJECTED));
        put("PAST", List.of(APPROVED));
        put("FUTURE", List.of(WAITING, APPROVED));
    }};

    public static List<Status> toStatus(String state) {
        List<Status> result = statusMap.get(state);
        if (result == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        } else {
            return result;
        }
    }
}
