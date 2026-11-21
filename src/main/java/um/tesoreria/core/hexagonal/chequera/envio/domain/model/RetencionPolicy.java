package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

/**
 * Regla de retención del legacy, literal: si la persona tiene **más de N cuotas impagas del lectivo
 * seleccionado**, no se le envía la chequera sino una notificación de deuda.
 *
 * <p>Dominio puro: no consulta nada, recibe la deuda ya calculada.</p>
 */
public final class RetencionPolicy {

    private RetencionPolicy() {
    }

    public static boolean retener(DeudaEnvio deuda, Integer lectivoId, int umbralCuotas) {
        if (deuda == null || deuda.getDeudas() == null || lectivoId == null) {
            return false;
        }
        return deuda.getDeudas().stream()
                .anyMatch(d -> lectivoId.equals(d.getLectivoId())
                        && d.getCuotas() != null
                        && d.getCuotas() > umbralCuotas);
    }

    /** Cuotas impagas del lectivo que dispararon la retención (la mayor, para el mensaje). */
    public static Integer cuotasDelLectivo(DeudaEnvio deuda, Integer lectivoId) {
        if (deuda == null || deuda.getDeudas() == null || lectivoId == null) {
            return null;
        }
        return deuda.getDeudas().stream()
                .filter(d -> lectivoId.equals(d.getLectivoId()))
                .map(DeudaEnvio.DeudaChequeraEnvio::getCuotas)
                .filter(c -> c != null)
                .max(Integer::compareTo)
                .orElse(null);
    }
}
