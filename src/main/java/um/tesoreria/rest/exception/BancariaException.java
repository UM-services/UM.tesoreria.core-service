package um.tesoreria.rest.exception;

public class BancariaException extends RuntimeException {
    public BancariaException(Long bancariaId) {
        super("Cannot find Bacncaria " + bancariaId);
    }
}
