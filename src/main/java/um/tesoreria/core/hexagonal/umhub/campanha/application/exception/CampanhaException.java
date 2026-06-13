package um.tesoreria.core.hexagonal.umhub.campanha.application.exception;

import java.util.UUID;

public class CampanhaException extends RuntimeException {
    public CampanhaException(UUID id) {
        super("Could not find campanha with id: " + id);
    }
}
