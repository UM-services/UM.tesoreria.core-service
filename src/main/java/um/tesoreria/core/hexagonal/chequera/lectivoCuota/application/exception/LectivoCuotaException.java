package um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.exception;

public class LectivoCuotaException extends RuntimeException {

    public LectivoCuotaException() {
        super("Cannot find LectivoCuota");
    }

    public LectivoCuotaException(Long id) {
        super("Cannot find LectivoCuota with id: " + id);
    }

    public LectivoCuotaException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        super("Cannot find LectivoCuota for facultad: " + facultadId + ", lectivo: " + lectivoId + ", tipoChequera: " + tipoChequeraId);
    }

    public LectivoCuotaException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        super("Cannot find LectivoCuota for facultad: " + facultadId + ", lectivo: " + lectivoId + ", tipoChequera: " + tipoChequeraId + ", producto: " + productoId + ", alternativa: " + alternativaId + ", cuota: " + cuotaId);
    }

    public LectivoCuotaException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, String fecha) {
        super("Cannot find LectivoCuota for facultad: " + facultadId + ", lectivo: " + lectivoId + ", tipoChequera: " + tipoChequeraId + ", producto: " + productoId + ", alternativa: " + alternativaId + ", fecha: " + fecha);
    }
}
