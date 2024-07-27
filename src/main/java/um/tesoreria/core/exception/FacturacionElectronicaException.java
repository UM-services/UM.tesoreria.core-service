package um.tesoreria.core.exception;

import java.text.MessageFormat;

public class FacturacionElectronicaException extends RuntimeException {

    public FacturacionElectronicaException() {
        super("Cannot find FacturacionElectronica");
    }

    public FacturacionElectronicaException(Long facturacionElectronicaId) {
        super(MessageFormat.format("Cannot find FacturacionElectronica {0}", facturacionElectronicaId));
    }

}
