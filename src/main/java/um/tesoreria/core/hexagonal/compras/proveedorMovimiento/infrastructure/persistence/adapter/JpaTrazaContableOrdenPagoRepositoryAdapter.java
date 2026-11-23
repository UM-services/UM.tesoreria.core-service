package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion.TrazaLinea;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.TrazaContableOrdenPagoRepository;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.TrazaContableDarticulomovEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.TrazaContableDasigcostosEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.TrazaContableDcontraasientoEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.TrazaContableDdirectaEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.TrazaContableDvalormovEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.TrazaContableRamaEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository.JpaCuentaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Traza consolidada de la orden de pago: importe neto por cuenta de los asientos de la OP.
 *
 * <p>En lugar de la vista {@code vw_trazaContablePagosConsolidada} ({@code UNION} de 5 vistas + dos
 * {@code GROUP BY}, sin pushdown del filtro: ~10 s por OP), se consulta <b>cada rama</b> filtrando
 * por {@code MvP_ID} (~45 ms las cinco juntas) y se consolida acá, replicando la vista:</p>
 * <ol>
 *   <li>deduplicar por {@code id} — es el {@code DISTINCT} del {@code UNION};</li>
 *   <li>sumar {@code ±importe} por cuenta ({@code MCo_Debita = 1 → +, si no → −});</li>
 *   <li>descartar los netos en cero y ordenar debe primero, luego por cuenta;</li>
 *   <li>nombre de la cuenta en bloque desde {@code plancta}.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class JpaTrazaContableOrdenPagoRepositoryAdapter implements TrazaContableOrdenPagoRepository {

    private static final List<Class<? extends TrazaContableRamaEntity>> RAMAS = List.of(
            TrazaContableDvalormovEntity.class,
            TrazaContableDarticulomovEntity.class,
            TrazaContableDasigcostosEntity.class,
            TrazaContableDdirectaEntity.class,
            TrazaContableDcontraasientoEntity.class);

    private final EntityManager entityManager;
    private final JpaCuentaRepository cuentaRepository;

    @Override
    public List<TrazaLinea> findConsolidadaByOrdenPagoId(Long proveedorMovimientoId) {
        Map<Integer, Tuple> movimientos = unicos(proveedorMovimientoId);
        Map<BigDecimal, BigDecimal> netos = netosPorCuenta(movimientos.values());
        if (netos.isEmpty()) {
            return List.of();
        }
        Map<BigDecimal, String> nombres = nombres(netos.keySet());
        return netos.entrySet().stream()
                .filter(neto -> neto.getValue().signum() != 0)
                .map(neto -> linea(neto.getKey(), neto.getValue(), nombres.get(neto.getKey())))
                .sorted((a, b) -> {
                    int porSigno = Boolean.compare(esDebe(b), esDebe(a));
                    return porSigno != 0 ? porSigno : a.numeroCuenta().compareTo(b.numeroCuenta());
                })
                .toList();
    }

    /** Filas de las 5 ramas, deduplicadas por {@code id} (semántica del {@code UNION}). */
    private Map<Integer, Tuple> unicos(Long proveedorMovimientoId) {
        Map<Integer, Tuple> unicos = new LinkedHashMap<>();
        for (Class<? extends TrazaContableRamaEntity> rama : RAMAS) {
            for (Tuple fila : filas(rama, proveedorMovimientoId)) {
                Integer movimientoId = fila.get("movimientoId", Integer.class);
                if (movimientoId != null) {
                    unicos.putIfAbsent(movimientoId, fila);
                }
            }
        }
        return unicos;
    }

    private List<Tuple> filas(Class<? extends TrazaContableRamaEntity> rama, Long proveedorMovimientoId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<? extends TrazaContableRamaEntity> root = query.from(rama);
        query.multiselect(
                        root.get("movimientoId").alias("movimientoId"),
                        root.get("numeroCuenta").alias("numeroCuenta"),
                        root.get("debita").alias("debita"),
                        root.get("importe").alias("importe"))
                .where(builder.equal(root.get("proveedorMovimientoId"), proveedorMovimientoId));
        return entityManager.createQuery(query).getResultList();
    }

    private Map<BigDecimal, BigDecimal> netosPorCuenta(Iterable<Tuple> movimientos) {
        Map<BigDecimal, BigDecimal> netos = new LinkedHashMap<>();
        for (Tuple fila : movimientos) {
            BigDecimal cuenta = fila.get("numeroCuenta", BigDecimal.class);
            BigDecimal importe = fila.get("importe", BigDecimal.class);
            if (cuenta == null || importe == null) {
                continue;
            }
            Integer debita = fila.get("debita", Integer.class);
            BigDecimal firmado = debita != null && debita == 1 ? importe : importe.negate();
            netos.merge(cuenta, firmado, BigDecimal::add);
        }
        return netos;
    }

    private Map<BigDecimal, String> nombres(java.util.Set<BigDecimal> cuentas) {
        return cuentaRepository.findAllById(new LinkedHashSet<>(cuentas)).stream()
                .collect(Collectors.toMap(CuentaEntity::getNumeroCuenta, CuentaEntity::getNombre, (a, b) -> a));
    }

    private static TrazaLinea linea(BigDecimal cuenta, BigDecimal neto, String nombreCuenta) {
        boolean debita = neto.signum() > 0;
        return new TrazaLinea(cuenta, nombreCuenta,
                debita ? neto : BigDecimal.ZERO,
                debita ? BigDecimal.ZERO : neto.abs());
    }

    private static boolean esDebe(TrazaLinea linea) {
        return linea.debe().signum() > 0;
    }

}
