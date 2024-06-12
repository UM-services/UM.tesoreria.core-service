package um.tesoreria.core.exception;

public class DependenciaException extends RuntimeException {

    public DependenciaException(Integer dependenciaId) {
        super("Cannot find Dependencia " + dependenciaId);
    }

}
