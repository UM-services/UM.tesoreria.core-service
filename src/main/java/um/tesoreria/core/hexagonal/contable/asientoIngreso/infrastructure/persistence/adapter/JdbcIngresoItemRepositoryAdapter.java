package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoLinea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoItemRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Lee la tabla legacy {@code ingreso_items} (sólo lectura: ningún proceso del repo la escribe).
 * Se usa JDBC a propósito: la tabla no tiene PK simple ({@code iit_id} no es única) y
 * {@code iit_importe} es {@code double}; mapearla como entidad no aporta nada.
 */
@Component
@RequiredArgsConstructor
public class JdbcIngresoItemRepositoryAdapter implements IngresoItemRepository {

    private static final String SQL = """
            SELECT iit_pla_cuenta AS numero_cuenta,
                   iit_debita AS debita,
                   iit_importe AS importe
              FROM ingreso_items
             WHERE iit_acred = :fecha
               AND iit_tpa_id = :tipoPagoId
             ORDER BY iit_debita DESC, iit_pla_cuenta
            """;

    private final JdbcClient jdbcClient;

    @Override
    public List<AsientoIngresoLinea> findAllByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId) {
        return jdbcClient.sql(SQL)
                .param("fecha", LocalDate.from(fecha.withOffsetSameInstant(ZoneOffset.UTC)))
                .param("tipoPagoId", tipoPagoId)
                .query((rs, rowNum) -> {
                    BigDecimal importe = rs.getBigDecimal("importe");
                    return AsientoIngresoLinea.builder()
                            .numeroCuenta(rs.getBigDecimal("numero_cuenta"))
                            .debita(rs.getByte("debita"))
                            .importe(importe == null ? BigDecimal.ZERO : importe)
                            .build();
                })
                .list();
    }

}
