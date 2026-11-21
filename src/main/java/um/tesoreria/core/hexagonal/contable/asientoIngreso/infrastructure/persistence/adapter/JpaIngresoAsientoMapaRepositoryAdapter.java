package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoAsientoMapaRepository;
import um.tesoreria.core.model.IngresoAsiento;
import um.tesoreria.core.repository.IngresoAsientoRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Mapa {@code ingreso_asiento}: reutiliza la entidad y el repositorio que ya existían y agrega la
 * lectura con bloqueo y el alta/baja que el core no tenía.
 */
@Component
@RequiredArgsConstructor
public class JpaIngresoAsientoMapaRepositoryAdapter implements IngresoAsientoMapaRepository {

    private final IngresoAsientoRepository repository;

    @Override
    public Optional<AsientoIngresoRegistro> findByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId) {
        return repository.findByFechaContableAndTipoPagoId(fecha, tipoPagoId).map(this::toDomain);
    }

    @Override
    public Optional<AsientoIngresoRegistro> findWithLockByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId) {
        return repository.findFirstByFechaContableAndTipoPagoId(fecha, tipoPagoId).map(this::toDomain);
    }

    @Override
    public AsientoIngresoRegistro create(OffsetDateTime fecha, Integer tipoPagoId, Integer ordenContable) {
        IngresoAsiento entity = new IngresoAsiento(null, tipoPagoId, fecha, ordenContable);
        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    public AsientoIngresoRegistro updateOrden(AsientoIngresoRegistro registro, Integer ordenContable) {
        IngresoAsiento entity = repository.findById(registro.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el mapa ingreso_asiento " + registro.getId()));
        entity.setOrdenContable(ordenContable);
        return toDomain(repository.saveAndFlush(entity));
    }

    @Override
    public boolean existsOtherByFechaAndOrden(OffsetDateTime fecha, Integer ordenContable, Integer tipoPagoId) {
        return repository.existsByFechaContableAndOrdenContableAndTipoPagoIdNot(
                fecha, ordenContable, tipoPagoId);
    }

    @Override
    public void delete(AsientoIngresoRegistro registro) {
        if (registro.getId() != null) {
            repository.deleteById(registro.getId());
        }
    }

    private AsientoIngresoRegistro toDomain(IngresoAsiento entity) {
        return AsientoIngresoRegistro.builder()
                .id(entity.getIngresoAsientoId())
                .fechaContable(entity.getFechaContable())
                .tipoPagoId(entity.getTipoPagoId())
                .ordenContable(entity.getOrdenContable())
                .build();
    }

}
