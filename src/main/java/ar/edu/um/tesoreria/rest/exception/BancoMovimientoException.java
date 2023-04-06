package ar.edu.um.tesoreria.rest.exception;


public class BancoMovimientoException extends RuntimeException {

    public BancoMovimientoException() {
        super("Cannot find BancoMovimiento");
    }

}
