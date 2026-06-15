package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.exception;

import java.util.UUID;

public class ReservaVacanteException extends RuntimeException {
    public ReservaVacanteException(UUID id) {
        super("Could not find reserva vacante with id: " + id);
    }
}
