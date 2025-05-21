package um.tesoreria.core.exception;

public class TipoChequeraMercadoPagoCreditCardException extends RuntimeException {

    public TipoChequeraMercadoPagoCreditCardException(Integer tipoChequeraId, Integer alternativaId) {
        super("Cannot find TipoChequeraMercadoPagoCreditCard " + tipoChequeraId + "/" + alternativaId);
    }

}
