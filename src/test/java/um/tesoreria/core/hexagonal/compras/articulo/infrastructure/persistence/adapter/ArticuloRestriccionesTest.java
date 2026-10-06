package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException.Motivo;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Excepciones SIMULADAS con el código y el mensaje de MySQL 5.7: las copias TEMPORARY del IT no tienen FK,
 * así que 1451/1452 no se pueden producir de verdad. El 1062 real está en {@code ArticuloDevDbIT}.
 */
class ArticuloRestriccionesTest {

    @Test
    void duplicadoPrimary_idDuplicado() {
        var traducida = ArticuloRestricciones.traducir(violacion(1062, "Duplicate entry '7' for key 'PRIMARY'", "PRIMARY"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class,
                ex -> assertThat(ex.getMotivo()).isEqualTo(Motivo.ID_DUPLICADO));
    }

    @Test
    void duplicadoConNombreDeMysql8_tambienSeReconoce() {
        var traducida = ArticuloRestricciones.traducir(violacion(1062, "Duplicate entry '7' for key 'articulos.PRIMARY'", "articulos.PRIMARY"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class,
                ex -> assertThat(ex.getMotivo()).isEqualTo(Motivo.ID_DUPLICADO));
    }

    @Test
    void duplicadoDeOtraClave_conflictoGenerico() {
        var traducida = ArticuloRestricciones.traducir(violacion(1062, "Duplicate entry 'x' for key 'otra'", "otra"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class,
                ex -> assertThat(ex.getMotivo()).isEqualTo(Motivo.CONFLICTO));
    }

    @Test
    void cuentaInexistente_400ConCampo() {
        var traducida = ArticuloRestricciones.traducir(violacion(1452,
                "Cannot add or update a child row: a foreign key constraint fails (`tesium`.`articulos`, CONSTRAINT `articulos_ibfk_1` FOREIGN KEY (`Art_Cuenta`) REFERENCES `plancta` (`Pla_Cuenta`))",
                "articulos_ibfk_1"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloValidationException.class,
                ex -> assertThat(ex.getCampo()).isEqualTo("numeroCuenta"));
    }

    @Test
    void fkPadreDesconocida_400SinCampo() {
        var traducida = ArticuloRestricciones.traducir(violacion(1452, "a foreign key constraint fails", "articulos_ibfk_9"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
    }

    @Test
    void bajaConEntregas_409ConLaTabla() {
        var traducida = ArticuloRestricciones.traducir(violacion(1451,
                "Cannot delete or update a parent row: a foreign key constraint fails (`tesium`.`entrega_detalle`, CONSTRAINT `entrega_detalle_ibfk_2` FOREIGN KEY (`NeD_Art_ID`) REFERENCES `articulos` (`Art_ID`))",
                "entrega_detalle_ibfk_2"), 7L, "baja");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
            assertThat(ex.getMotivo()).isEqualTo(Motivo.REFERENCIADO);
            assertThat(ex.getReferencias()).containsExactly(new ReferenciaArticulo("entrega_detalle", null));
        });
    }

    @Test
    void bajaConFkDesconocida_409SinTablas() {
        var traducida = ArticuloRestricciones.traducir(violacion(1451, "a foreign key constraint fails", "nueva_ibfk_1"), 7L, "baja");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
            assertThat(ex.getMotivo()).isEqualTo(Motivo.REFERENCIADO);
            assertThat(ex.getReferencias()).isEmpty();
        });
    }

    @Test
    void otroError_sigueSinTraducir() {
        var original = new DataIntegrityViolationException("x", new SQLException("Data too long", "22001", 1406));

        assertThat(ArticuloRestricciones.traducir(original, 7L, "alta")).isSameAs(original);
    }

    @Test
    void sinSqlException_sigueSinTraducir() {
        var original = new IllegalStateException("otra cosa");

        assertThat(ArticuloRestricciones.traducir(original, 7L, "alta")).isSameAs(original);
    }

    /** Como llega desde Spring Data o desde el flush: Hibernate envuelve la SQLException del driver. */
    private static RuntimeException violacion(int codigo, String mensaje, String restriccion) {
        var sql = new SQLException(mensaje, "23000", codigo);
        return new DataIntegrityViolationException(mensaje, new ConstraintViolationException(mensaje, sql, restriccion));
    }
}
