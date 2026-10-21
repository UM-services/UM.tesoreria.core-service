package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity.MercadoPagoContextEntity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaMercadoPagoContextRepositoryCustomImpl implements JpaMercadoPagoContextRepositoryCustom {

    private static final byte ACTIVO = 1;
    private static final byte NO_ACTIVO = 0;
    private static final int DIAS_REINTENTO = 30;

    private final EntityManager entityManager;

    @Override
    public List<MercadoPagoContextEntity> findRenovaciones(OffsetDateTime desde,
                                                           OffsetDateTime hastaExclusivo,
                                                           boolean reintentos,
                                                           OffsetDateTime afterFecha,
                                                           Long afterId,
                                                           int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MercadoPagoContextEntity> cq = cb.createQuery(MercadoPagoContextEntity.class);
        Root<MercadoPagoContextEntity> contexto = cq.from(MercadoPagoContextEntity.class);
        Join<MercadoPagoContextEntity, ChequeraCuotaEntity> cuota = contexto.join("chequeraCuota", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(contexto.get("activo"), ACTIVO));
        predicates.add(cb.isNotNull(contexto.get("chequeraCuotaId")));

        // Dos pasadas separadas para que MySQL pueda recorrer el indice (activo, fecha_vencimiento):
        //  - ventana:   vencimiento publicado dentro de [desde, hasta)
        //  - reintento: marcado changed=1 en la banda anterior a la ventana
        if (reintentos) {
            predicates.add(cb.equal(contexto.get("changed"), (byte) 1));
            predicates.add(cb.greaterThanOrEqualTo(contexto.get("fechaVencimiento"), desde.minusDays(DIAS_REINTENTO)));
            predicates.add(cb.lessThan(contexto.get("fechaVencimiento"), desde));
        } else {
            predicates.add(cb.greaterThanOrEqualTo(contexto.get("fechaVencimiento"), desde));
            predicates.add(cb.lessThan(contexto.get("fechaVencimiento"), hastaExclusivo));
        }

        // Solo cuotas que siguen pendientes.
        predicates.add(cb.equal(cuota.get("pagado"), NO_ACTIVO));
        predicates.add(cb.equal(cuota.get("baja"), NO_ACTIVO));
        predicates.add(cb.equal(cuota.get("compensada"), NO_ACTIVO));

        // Debe existir un vencimiento posterior al publicado que ademas siga vigente:
        // nunca se publica un enlace con vencimiento en el pasado.
        predicates.add(cb.or(
                cb.and(
                        cb.greaterThan(cuota.get("vencimiento2"), contexto.get("fechaVencimiento")),
                        cb.greaterThanOrEqualTo(cuota.get("vencimiento2"), hastaExclusivo)),
                cb.and(
                        cb.greaterThan(cuota.get("vencimiento3"), contexto.get("fechaVencimiento")),
                        cb.greaterThanOrEqualTo(cuota.get("vencimiento3"), hastaExclusivo))));

        // Keyset sobre (fechaVencimiento, id): es el orden del indice, evita el filesort y
        // permite recorrer el rango sin escanear la tabla completa.
        if (afterFecha != null) {
            long cursor = afterId == null ? 0L : afterId;
            predicates.add(cb.or(
                    cb.greaterThan(contexto.get("fechaVencimiento"), afterFecha),
                    cb.and(cb.equal(contexto.get("fechaVencimiento"), afterFecha),
                            cb.greaterThan(contexto.get("mercadoPagoContextId"), cursor))));
        }

        // Una sola fila por cuota: el contexto activo mas reciente. Si hubiera duplicados,
        // el barrido no trabaja dos veces la misma cuota (la sanitizacion se hace al tocar la cuota).
        Subquery<Long> ultimoActivo = cq.subquery(Long.class);
        Root<MercadoPagoContextEntity> sub = ultimoActivo.from(MercadoPagoContextEntity.class);
        ultimoActivo.select(cb.max(sub.get("mercadoPagoContextId")))
                .where(cb.equal(sub.get("chequeraCuotaId"), contexto.get("chequeraCuotaId")),
                        cb.equal(sub.get("activo"), ACTIVO));
        predicates.add(cb.equal(contexto.get("mercadoPagoContextId"), ultimoActivo));

        cq.select(contexto)
                .where(cb.and(predicates.toArray(Predicate[]::new)))
                .orderBy(cb.asc(contexto.get("fechaVencimiento")),
                        cb.asc(contexto.get("mercadoPagoContextId")));

        return entityManager.createQuery(cq)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Long> findChequeraCuotaIdsWithMultipleActiveContexts() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<MercadoPagoContextEntity> contexto = cq.from(MercadoPagoContextEntity.class);

        cq.select(contexto.get("chequeraCuotaId"))
                .where(cb.equal(contexto.get("activo"), ACTIVO),
                        cb.isNotNull(contexto.get("chequeraCuotaId")))
                .groupBy(contexto.get("chequeraCuotaId"))
                .having(cb.greaterThan(cb.count(contexto), 1L));

        return entityManager.createQuery(cq).getResultList();
    }

}
