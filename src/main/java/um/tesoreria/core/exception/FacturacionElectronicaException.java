package um.tesoreria.core.exception;

import java.text.MessageFormat;

public class FacturacionElectronicaException extends RuntimeException {

    public FacturacionElectronicaException() {
        super("Cannot find FacturacionElectronica");
    }

    public FacturacionElectronicaException(String key, Long id) {
        super(MessageFormat.format("Cannot find {0} -> {1}", key, id));
    }

}
