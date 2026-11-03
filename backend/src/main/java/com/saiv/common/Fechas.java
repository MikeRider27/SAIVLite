package com.saiv.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Fechas {
    private Fechas() {}

    public static LocalDateTime inicio(LocalDate d) {
        return (d == null ? LocalDate.now() : d).atStartOfDay();
    }

    /** Límite exclusivo: inicio del día siguiente. */
    public static LocalDateTime fin(LocalDate d) {
        return (d == null ? LocalDate.now() : d).plusDays(1).atStartOfDay();
    }
}
