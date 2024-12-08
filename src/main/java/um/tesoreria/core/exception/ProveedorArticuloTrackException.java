package um.tesoreria.core.exception;

public class ProveedorArticuloTrackException extends RuntimeException {

    public ProveedorArticuloTrackException(Long proveedorArticuloTrackId) {
        super("No existe ProveedorArticuloTrack con ID " + proveedorArticuloTrackId);
    }

}
