package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ActualizarBorradorRequest(
        Integer version,
        Integer arancelTipoId,
        Integer alternativaId,
        Integer cursoId,
        OffsetDateTime fecha,
        Byte hpum,
        BigDecimal becaPorcentaje,
        String becaResolucion,
        OffsetDateTime becaFecha,
        Long becaUserId,
        Boolean calculaBeca,
        String emailPersonal,
        String emailInstitucional,
        Boolean incluyeBarras,
        Boolean copiaFacultad,
        List<CuotaRequest> cuotas) {

    public record CuotaRequest(
            Integer productoId,
            Integer alternativaId,
            Integer cuotaId,
            Integer mes,
            Integer anio,
            OffsetDateTime vencimiento1,
            OffsetDateTime vencimiento2,
            OffsetDateTime vencimiento3,
            BigDecimal importe1Borrador,
            BigDecimal importe2Borrador,
            BigDecimal importe3Borrador,
            Byte manual,
            Byte compensada,
            Byte baja) {
    }
}
