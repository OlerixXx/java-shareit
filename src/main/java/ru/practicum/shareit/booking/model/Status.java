package ru.practicum.shareit.booking.model;

import java.util.List;

public enum Status {
    APPROVED,
    WAITING,
    REJECTED,
    CANCELED;

    public static List<Status> toStatus(String state) {
        switch (state) {
            case "ALL":
                return List.of(APPROVED, WAITING, REJECTED, CANCELED);
            case "CURRENT":
                return List.of(APPROVED, REJECTED);
            case "WAITING":
                return List.of(WAITING);
            case "REJECTED":
                return List.of(REJECTED);
            case "PAST":
                return List.of(APPROVED);
            case "FUTURE":
                return List.of(WAITING, APPROVED);
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
