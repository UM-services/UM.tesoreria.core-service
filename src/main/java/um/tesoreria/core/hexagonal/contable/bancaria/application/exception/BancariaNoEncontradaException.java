package um.tesoreria.core.hexagonal.contable.bancaria.application.exception;

public class BancariaNoEncontradaException extends RuntimeException {
    public BancariaNoEncontradaException(Long bancariaId) {
        super("Cuenta bancaria " + bancariaId + " no encontrada");
    }
}
