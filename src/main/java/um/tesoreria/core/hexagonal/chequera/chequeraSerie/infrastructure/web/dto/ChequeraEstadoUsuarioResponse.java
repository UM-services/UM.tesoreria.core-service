package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.math.BigDecimal;

public record ChequeraEstadoUsuarioResponse(
        Long chequeraId,
        Integer facultadId,
        String facultad,
        Integer tipoChequeraId,
        String tipoChequera,
        Long chequeraSerieId,
        BigDecimal personaId,
        Integer documentoId,
        String titular,
        Integer lectivoId,
        Integer geograficaId,
        Integer alternativaId,
        BigDecimal importeDeuda,
        int cuotasDeuda,
        String estadoDeuda,
        Byte asentado,
        Byte enviado,
        Byte retenida) {

    public static ChequeraEstadoUsuarioResponse from(ChequeraSerie chequera) {
        BigDecimal importeDeuda = chequera.getImporteDeuda();
        return new ChequeraEstadoUsuarioResponse(
                chequera.getChequeraId(),
                chequera.getFacultadId(),
                chequera.getFacultad() == null ? null : chequera.getFacultad().getNombre(),
                chequera.getTipoChequeraId(),
                chequera.getTipoChequera() == null ? null : chequera.getTipoChequera().getNombre(),
                chequera.getChequeraSerieId(),
                chequera.getPersonaId(),
                chequera.getDocumentoId(),
                nombreTitular(chequera),
                chequera.getLectivoId(),
                chequera.getGeograficaId(),
                chequera.getAlternativaId(),
                importeDeuda,
                chequera.getCuotasDeuda(),
                importeDeuda.signum() > 0 ? "CON_DEUDA_VENCIDA" : "SIN_DEUDA_VENCIDA",
                chequera.getAsentado(),
                chequera.getEnviado(),
                chequera.getRetenida());
    }

    private static String nombreTitular(ChequeraSerie chequera) {
        if (chequera.getPersona() == null) return null;
        String apellido = chequera.getPersona().getApellido();
        String nombre = chequera.getPersona().getNombre();
        if (apellido == null || apellido.isBlank()) return nombre;
        if (nombre == null || nombre.isBlank()) return apellido;
        return apellido + ", " + nombre;
    }
}
