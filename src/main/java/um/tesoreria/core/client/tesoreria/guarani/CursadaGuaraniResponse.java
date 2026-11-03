package um.tesoreria.core.client.tesoreria.guarani;

/** Respuesta de B1. No se comparte el dominio del servicio remoto. */
public record CursadaGuaraniResponse(
        Integer responsableAcademica, Integer propuesta, String propuestaNombre, Integer planVersion,
        String actividadCodigo, String actividadNombre, Integer anioPlan, String periodo,
        Integer periodoLectivo, String periodoNombre, Short anioCursada) {
}
