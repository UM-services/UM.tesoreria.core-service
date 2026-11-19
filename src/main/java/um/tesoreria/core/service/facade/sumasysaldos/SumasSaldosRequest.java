package um.tesoreria.core.service.facade.sumasysaldos;

import java.time.OffsetDateTime;

/** Pedido del balance de sumas y saldos: {@code desde} y {@code hasta} deben caer en un mismo ejercicio. */
public record SumasSaldosRequest(OffsetDateTime desde, OffsetDateTime hasta) {
}