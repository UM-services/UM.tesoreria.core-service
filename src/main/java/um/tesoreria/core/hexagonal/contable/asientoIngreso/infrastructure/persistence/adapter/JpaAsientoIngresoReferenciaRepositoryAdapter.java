package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.ClaveImputacion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.ClaveSerie;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoReferenciaRepository;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;
import um.tesoreria.core.kotlin.model.ChequeraSerieReemplazo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Lecturas de referencia de la regeneración, resueltas en bloque con Criteria y proyecciones de
 * tupla: un día con 500 pagos hacía 500 búsquedas de serie y 500 de imputación (con el grafo EAGER
 * de {@code chequera_serie} incluido). La tupla trae sólo las columnas necesarias y Hibernate no
 * hidrata entidades ni asociaciones.
 *
 * <p>El filtro es {@code (fac, tch, chs) IN ...} resuelto como tres {@code IN} simultáneos (mismo
 * resultado y sin cadenas de {@code OR}); las claves de más se descartan al armar el mapa.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaAsientoIngresoReferenciaRepositoryAdapter implements AsientoIngresoReferenciaRepository {

    private final EntityManager entityManager;

    @Override
    public Map<ClaveSerie, Integer> findLectivosBySeries(Collection<ClaveSerie> claves) {
        return lectivos(ChequeraSerieEntity.class, claves);
    }

    @Override
    public Map<ClaveSerie, Integer> findLectivosReemplazoBySeries(Collection<ClaveSerie> claves) {
        return lectivos(ChequeraSerieReemplazo.class, claves);
    }

    @Override
    public Map<ClaveImputacion, BigDecimal> findCuentasByFacultades(Collection<Integer> facultades) {
        Set<Integer> ids = distintas(facultades);
        if (ids.isEmpty()) {
            return Map.of();
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<LectivoTotalImputacionEntity> root = query.from(LectivoTotalImputacionEntity.class);
        Path<Integer> facultad = root.get("facultadId");
        query.multiselect(
                facultad.alias("facultadId"),
                root.<Integer>get("lectivoId").alias("lectivoId"),
                root.<Integer>get("tipoChequeraId").alias("tipoChequeraId"),
                root.<Integer>get("productoId").alias("productoId"),
                root.<BigDecimal>get("numeroCuenta").alias("numeroCuenta"));
        query.where(facultad.in(ids));

        Map<ClaveImputacion, BigDecimal> cuentas = new HashMap<>();
        for (Tuple fila : entityManager.createQuery(query).getResultList()) {
            BigDecimal cuenta = fila.get("numeroCuenta", BigDecimal.class);
            if (cuenta == null) {
                continue;
            }
            cuentas.putIfAbsent(new ClaveImputacion(fila.get("facultadId", Integer.class),
                    fila.get("lectivoId", Integer.class), fila.get("tipoChequeraId", Integer.class),
                    fila.get("productoId", Integer.class)), cuenta);
        }
        return cuentas;
    }

    private Map<ClaveSerie, Integer> lectivos(Class<?> entidad, Collection<ClaveSerie> claves) {
        Set<ClaveSerie> clavesDistintas = new LinkedHashSet<>(claves.stream().filter(Objects::nonNull).toList());
        if (clavesDistintas.isEmpty()) {
            return Map.of();
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<?> root = query.from(entidad);
        Path<Integer> facultad = root.get("facultadId");
        Path<Integer> tipoChequera = root.get("tipoChequeraId");
        Path<Long> serie = root.get("chequeraSerieId");
        Path<Integer> lectivo = root.get("lectivoId");
        query.multiselect(
                facultad.alias("facultadId"),
                tipoChequera.alias("tipoChequeraId"),
                serie.alias("chequeraSerieId"),
                lectivo.alias("lectivoId"));
        query.where(builder.and(
                facultad.in(distintas(clavesDistintas.stream().map(ClaveSerie::facultadId).toList())),
                tipoChequera.in(distintas(clavesDistintas.stream().map(ClaveSerie::tipoChequeraId).toList())),
                serie.in(distintas(clavesDistintas.stream().map(ClaveSerie::chequeraSerieId).toList()))));

        Map<ClaveSerie, Integer> lectivos = new HashMap<>();
        for (Tuple fila : entityManager.createQuery(query).getResultList()) {
            Integer id = fila.get("lectivoId", Integer.class);
            lectivos.putIfAbsent(new ClaveSerie(fila.get("facultadId", Integer.class),
                    fila.get("tipoChequeraId", Integer.class), fila.get("chequeraSerieId", Long.class)),
                    id == null ? 0 : id);
        }
        return lectivos;
    }

    private static <T> Set<T> distintas(Collection<T> valores) {
        Set<T> distintos = new LinkedHashSet<>();
        for (T valor : valores) {
            if (valor != null) {
                distintos.add(valor);
            }
        }
        return distintos;
    }

}
