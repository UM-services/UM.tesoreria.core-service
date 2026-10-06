package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Excepciones SIMULADAS con el código de MySQL: las copias TEMPORARY del IT no tienen FK y una sola conexión no
 * produce interbloqueos. El 1062 real del par está en {@code UbicacionArticuloDevDbIT}.
 */
class UbicacionArticuloRestriccionesTest {

    @Test
    void duplicadoDelPar_reintentable() {
        var traducida = UbicacionArticuloRestricciones.traducir(violacion(1062, "ubicacion_id"), 1, 2L);

        assertThat(traducida).isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> assertThat(ex.isReintentable()).isTrue());
    }

    @Test
    void interbloqueo_reintentable() {
        var sql = new SQLException("Deadlock found when trying to get lock; try restarting transaction", "40001", 1213);
        var original = new CannotAcquireLockException("x", new LockAcquisitionException("x", sql));

        assertThat(UbicacionArticuloRestricciones.traducir(original, 1, 2L))
                .isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> assertThat(ex.isReintentable()).isTrue());
    }

    @Test
    void esperaDeBloqueoVencida_bloqueadoSinReintento() {
        var sql = new SQLException("Lock wait timeout exceeded; try restarting transaction", "HY000", 1205);
        var original = new CannotAcquireLockException("x", new LockAcquisitionException("x", sql));

        assertThat(UbicacionArticuloRestricciones.traducir(original, 1, 2L)).isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> {
            assertThat(ex.isReintentable()).isFalse();
            assertThat(ex.isBloqueado()).isTrue();
        });
    }

    @Test
    void duplicadoDeOtraClave_conflictoNoReintentable() {
        var traducida = UbicacionArticuloRestricciones.traducir(violacion(1062, "PRIMARY"), 1, 2L);

        assertThat(traducida).isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> assertThat(ex.isReintentable()).isFalse());
    }

    @ParameterizedTest
    @CsvSource({"ubicacion_articulo_ibfk_1,ubicacionId", "ubicacion_articulo_ibfk_2,articuloId", "ubicacion_articulo_ibfk_3,numeroCuenta"})
    void fkPadreInexistente_400ConElCampo(String restriccion, String campo) {
        var traducida = UbicacionArticuloRestricciones.traducir(violacion(1452, restriccion), 1, 2L);

        assertThat(traducida).isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo(campo));
    }

    @Test
    void fkPadreDesconocida_400SinCampo() {
        var traducida = UbicacionArticuloRestricciones.traducir(violacion(1452, "ubicacion_articulo_ibfk_9"), 1, 2L);

        assertThat(traducida).isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
    }

    @Test
    void fkSinNombreExtraido_respuestaGenericaSinNpe() {
        assertThat(UbicacionArticuloRestricciones.traducir(violacion(1452, null), 1, 2L))
                .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
    }

    @Test
    void otroError_sigueSinTraducir() {
        var original = new DataIntegrityViolationException("x", new SQLException("Data too long", "22001", 1406));

        assertThat(UbicacionArticuloRestricciones.traducir(original, 1, 2L)).isSameAs(original);
    }

    private static RuntimeException violacion(int codigo, String restriccion) {
        var sql = new SQLException("simulada", "23000", codigo);
        return new DataIntegrityViolationException("simulada", new ConstraintViolationException("simulada", sql, restriccion));
    }
}
