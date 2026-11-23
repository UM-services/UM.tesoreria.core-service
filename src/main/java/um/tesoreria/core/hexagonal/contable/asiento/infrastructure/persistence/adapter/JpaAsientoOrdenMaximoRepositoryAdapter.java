package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoOrdenMaximoRepository;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.entity.EjercicioEntity;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity.ValorMovimientoEntity;
import um.tesoreria.core.model.IngresoAsiento;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Siembra de la secuencia con el máximo que ya existe en cualquiera de las fuentes donde el
 * legacy reserva un orden antes de escribir {@code movcon}: {@code asiento}, {@code movcon},
 * {@code ingreso_asiento}, {@code valores} y los cierres del ejercicio.
 *
 * <p>Se resuelve con Criteria sobre las entidades ya mapeadas (incluido {@code ValorMovimientoEntity}
 * para {@code valores}), sin SQL escrito a mano.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaAsientoOrdenMaximoRepositoryAdapter implements AsientoOrdenMaximoRepository {

    private final EntityManager entityManager;

    @Override
    public int maxOrden(OffsetDateTime fecha) {
        OffsetDateTime contable = fecha.withOffsetSameInstant(ZoneOffset.UTC);
        return Math.max(
                Math.max(max(AsientoEntity.class, "fecha", "orden", contable),
                        max(CuentaMovimientoEntity.class, "fechaContable", "ordenContable", contable)),
                Math.max(
                        Math.max(max(IngresoAsiento.class, "fechaContable", "ordenContable", contable),
                                max(ValorMovimientoEntity.class, "fechaContable", "ordenContable", contable)),
                        maxCierres(contable)));
    }

    /** {@code MAX(orden)} de las filas de la entidad cuya fecha contable es la pedida. */
    private int max(Class<?> entidad, String campoFecha, String campoOrden, OffsetDateTime fecha) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<?> root = query.from(entidad);
        query.select(builder.max(root.get(campoOrden)));
        query.where(builder.equal(root.get(campoFecha), fecha));
        Integer valor = entityManager.createQuery(query).getSingleResult();
        return valor == null ? 0 : valor;
    }

    /**
     * {@code MAX(GREATEST(ordenContableResultado, ordenContableBienesUso))} de los ejercicios que
     * cierran en la fecha (los cierres reservan el orden del último día del ejercicio). Se
     * resuelve como el máximo de los dos máximos: {@code max_i max(a_i,b_i) = max(max_i a_i,
     * max_i b_i)}, sin depender de la función {@code GREATEST} de MySQL.
     */
    private int maxCierres(OffsetDateTime fecha) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<EjercicioEntity> root = query.from(EjercicioEntity.class);
        query.multiselect(
                builder.max(root.<Integer>get("ordenContableResultado")).alias("resultado"),
                builder.max(root.<Integer>get("ordenContableBienesUso")).alias("bienesUso"));
        query.where(builder.equal(root.get("fechaFinal"), fecha));
        Tuple maximos = entityManager.createQuery(query).getSingleResult();
        Integer resultado = maximos.get("resultado", Integer.class);
        Integer bienesUso = maximos.get("bienesUso", Integer.class);
        return Math.max(resultado == null ? 0 : resultado, bienesUso == null ? 0 : bienesUso);
    }

}
