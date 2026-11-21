package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import java.math.BigDecimal;

/**
 * Fila del padrón de envío masivo de chequeras (ex {@code frmCheqMail}).
 *
 * <p>Es una serie «alta» de la vista {@code vw_chequera_alta} (la misma que usaba el legacy)
 * enriquecida con el estado de retención, que vive en {@code chequera_serie} y no en la vista.</p>
 *
 * <p>{@code emailPersonalValido}/{@code emailInstitucionalValido} los resuelve el adaptador con la
 * misma validación que usa el resto del sistema ({@code ToolService.mailValidate}); el dominio sólo
 * los consume.</p>
 */
public record ChequeraEnvioAlta(
        Long chequeraId,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer alternativaId,
        BigDecimal personaId,
        Integer documentoId,
        Integer lectivoId,
        Integer geograficaId,
        String apellido,
        String nombre,
        String tipoChequera,
        String arancelTipo,
        String emailPersonal,
        String emailInstitucional,
        boolean emailPersonalValido,
        boolean emailInstitucionalValido,
        boolean enviado,
        boolean retenida) {

    /** «Apellido, Nombre», igual que {@code apellidonombre} del legacy y {@code PersonaEntity}. */
    public String apellidoNombre() {
        String apellidoTexto = apellido == null ? "" : apellido.trim();
        String nombreTexto = nombre == null ? "" : nombre.trim();
        if (apellidoTexto.isEmpty() && nombreTexto.isEmpty()) {
            return "";
        }
        return apellidoTexto + ", " + nombreTexto;
    }

    /** Número de chequera que muestra la grilla del legacy: {@code facultad/tipo/serie}. */
    public String numero() {
        return facultadId + "/" + tipoChequeraId + "/" + chequeraSerieId;
    }

    /** Regla D6: si el domicilio no tiene ningún correo válido, la serie no se envía. */
    public boolean hayCorreoValido() {
        return emailPersonalValido || emailInstitucionalValido;
    }
}
