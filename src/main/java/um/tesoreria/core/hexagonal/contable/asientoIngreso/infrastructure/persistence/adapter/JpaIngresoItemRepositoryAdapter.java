package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoLinea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoItemRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.IngresoItemEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.repository.JpaIngresoItemRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Lee la tabla legacy {@code ingreso_items} (sólo lectura: ningún proceso del repo la escribe).
 * Se ordena por débito y cuenta igual que el legacy para que los ítems del asiento salgan en el
 * mismo orden.
 */
@Component
@RequiredArgsConstructor
public class JpaIngresoItemRepositoryAdapter implements IngresoItemRepository {

    private final JpaIngresoItemRepository repository;

    @Override
    public List<AsientoIngresoLinea> findAllByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId) {
        return repository
                .findAllByAcreditacionAndTipoPagoIdOrderByDebitaDescNumeroCuentaAsc(
                        fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate(), tipoPagoId)
                .stream()
                .map(JpaIngresoItemRepositoryAdapter::linea)
                .toList();
    }

    private static AsientoIngresoLinea linea(IngresoItemEntity item) {
        BigDecimal importe = item.getImporte();
        return AsientoIngresoLinea.builder()
                .numeroCuenta(item.getNumeroCuenta())
                .debita(item.getDebita())
                .importe(importe == null ? BigDecimal.ZERO : importe)
                .build();
    }

}
