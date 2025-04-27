package um.tesoreria.core.exception;

public class TipoChequeraMercadoPagoCreditCardException extends RuntimeException {

    public TipoChequeraMercadoPagoCreditCardException(Integer tipoChequeraId) {
        super("Cannot find TipoChequeraMercadoPagoCreditCard " + tipoChequeraId);
    }

}
