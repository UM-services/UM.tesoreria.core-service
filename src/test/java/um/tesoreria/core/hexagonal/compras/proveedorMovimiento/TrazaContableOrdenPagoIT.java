package um.tesoreria.core.hexagonal.compras.proveedorMovimiento;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion.TrazaLinea;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.TrazaContableOrdenPagoRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contraste de la traza consolidada migrada a Criteria contra el SQL anterior (la vista + el
 * subquery {@code MAX(pla_nombre)}) usado como oráculo.
 */
@SpringBootTest
@ActiveProfiles("it")
@Transactional(readOnly = true)
class TrazaContableOrdenPagoIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TrazaContableOrdenPagoRepository repository;

    @Test
    void coincideConLaVistaYElNombreDePlancta() {
        Long ordenPago = jdbc.queryForList("""
                        SELECT MvP_ID FROM vw_trazaContablePagosConsolidada
                        GROUP BY MvP_ID ORDER BY COUNT(*) DESC LIMIT 1
                        """, Long.class)
                .stream().findFirst().orElse(null);
        assumeTrue(ordenPago != null, "La base de este entorno no tiene trazas contables");

        List<TrazaLinea> esperado = jdbc.query("""
                SELECT t.MCo_Pla_Cuenta AS numero_cuenta,
                       (SELECT MAX(p.pla_nombre) FROM plancta p WHERE p.pla_cuenta = t.MCo_Pla_Cuenta) AS nombre_cuenta,
                       t.MCo_Debita AS debita,
                       t.MCo_Importe AS importe
                  FROM vw_trazaContablePagosConsolidada t
                 WHERE t.MvP_ID = ?
                 ORDER BY t.MCo_Debita DESC, t.MCo_Pla_Cuenta
                """, (rs, rowNum) -> {
            BigDecimal importe = rs.getBigDecimal("importe");
            boolean debita = rs.getInt("debita") == 1;
            return new TrazaLinea(rs.getBigDecimal("numero_cuenta"), rs.getString("nombre_cuenta"),
                    debita ? importe : BigDecimal.ZERO, debita ? BigDecimal.ZERO : importe);
        }, ordenPago);

        List<TrazaLinea> obtenido = repository.findConsolidadaByOrdenPagoId(ordenPago);

        assertThat(obtenido).hasSameSizeAs(esperado);
        for (int i = 0; i < esperado.size(); i++) {
            TrazaLinea esperada = esperado.get(i);
            TrazaLinea real = obtenido.get(i);
            assertThat(real.numeroCuenta()).isEqualByComparingTo(esperada.numeroCuenta());
            assertThat(real.nombreCuenta()).isEqualTo(esperada.nombreCuenta());
            assertThat(real.debe()).isEqualByComparingTo(esperada.debe());
            assertThat(real.haber()).isEqualByComparingTo(esperada.haber());
        }
    }

    @Test
    void unaOrdenSinTrazaDevuelveListaVacia() {
        Long inexistente = jdbc.queryForObject("SELECT COALESCE(MAX(MvP_ID), 0) + 1 FROM vw_trazaContablePagosConsolidada",
                Long.class);
        assertThat(repository.findConsolidadaByOrdenPagoId(inexistente)).isEmpty();
    }

}
