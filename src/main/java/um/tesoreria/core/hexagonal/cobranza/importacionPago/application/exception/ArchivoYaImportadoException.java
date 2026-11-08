package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception;

public class ArchivoYaImportadoException extends ImportacionPagoException {

    public ArchivoYaImportadoException(String filename) {
        super("El archivo ya fue importado: " + filename);
    }

}
