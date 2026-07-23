package um.tesoreria.core.hexagonal.setup.application.exception;

public class SetupException extends RuntimeException {
    private static final long serialVersionUID = -5376030077391156839L;

    public SetupException() {
        super("Cannot find Setup");
    }

    public SetupException(Integer setupId) {
        super("Cannot find Setup with id: " + setupId);
    }
}
