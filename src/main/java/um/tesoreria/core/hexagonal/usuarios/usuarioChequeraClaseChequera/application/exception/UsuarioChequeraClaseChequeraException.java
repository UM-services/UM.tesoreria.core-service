package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.exception;

public class UsuarioChequeraClaseChequeraException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioChequeraClaseChequeraException() {
        super("UsuarioChequeraClaseChequera not found");
    }

    public UsuarioChequeraClaseChequeraException(Long usuarioChequeraClaseChequeraId) {
        super("Could not find UsuarioChequeraClaseChequera with id: " + usuarioChequeraClaseChequeraId);
    }

    public UsuarioChequeraClaseChequeraException(String message) {
        super(message);
    }

}
