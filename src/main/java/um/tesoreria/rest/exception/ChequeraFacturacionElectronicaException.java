package um.tesoreria.rest.exception;

import java.text.MessageFormat;

public class ChequeraFacturacionElectronicaException extends RuntimeException {

    public ChequeraFacturacionElectronicaException(Long chequeraId, Boolean chequera) {
        super(MessageFormat.format("Cannot find ChequeraFacturacionElectronica ChequeraId={0}", chequeraId));
    }

    public ChequeraFacturacionElectronicaException(Long chequeraFacturacionElectronicaId) {
        super(MessageFormat.format("Cannot find ChequeraFacturacionElectronica ChequeraFacturacionElectronicaId={0}", chequeraFacturacionElectronicaId));
    }
}
