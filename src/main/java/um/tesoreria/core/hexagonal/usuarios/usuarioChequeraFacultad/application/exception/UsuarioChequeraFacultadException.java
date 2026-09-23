package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.exception;

public class UsuarioChequeraFacultadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioChequeraFacultadException() {
        super("UsuarioChequeraFacultad not found");
    }

    public UsuarioChequeraFacultadException(Long usuarioChequeraFacultadId) {
        super("Could not find UsuarioChequeraFacultad with id: " + usuarioChequeraFacultadId);
    }

}
