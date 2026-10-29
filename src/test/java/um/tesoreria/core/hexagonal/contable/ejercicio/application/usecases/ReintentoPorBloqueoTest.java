package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReintentoPorBloqueoTest {
    static CannotAcquireLockException conCodigo(int codigo, String sqlState) {
        return new CannotAcquireLockException("lock", new SQLException("x", sqlState, codigo));
    }

    <T extends Throwable> int intentosHastaFallar(T error) {
        var intentos = new AtomicInteger();
        assertThatThrownBy(() -> ReintentoPorBloqueo.ejecutar(() -> {
            intentos.incrementAndGet();
            throw (RuntimeException) error;
        })).isSameAs(error);
        return intentos.get();
    }

    @Test
    void elCodigo1213SeReintentaHastaTresVeces() {
        assertThat(intentosHastaFallar(conCodigo(1213, "40001"))).isEqualTo(3);
        assertThat(intentosHastaFallar(conCodigo(1213, "HY000"))).as("solo el código decide").isEqualTo(3);
    }

    @Test
    void elLockWaitTimeoutNoSeReintentaAunqueElDriverLoMapeeAl40001() {
        assertThat(intentosHastaFallar(conCodigo(1205, "40001"))).isEqualTo(1);
        assertThat(intentosHastaFallar(conCodigo(1205, "HY000"))).isEqualTo(1);
    }

    @Test
    void sinSqlExceptionEnLaCadenaNoSeReintenta() {
        assertThat(intentosHastaFallar(new CannotAcquireLockException("sin causa"))).isEqualTo(1);
    }

    @Test
    void elDeadlockAnidadoEnLaCadenaDeCausasSeDetecta() {
        var anidado = new PessimisticLockingFailureException("x", new RuntimeException("y", new SQLException("d", "40001", 1213)));
        assertThat(intentosHastaFallar(anidado)).isEqualTo(3);
    }

    @Test
    void siElDeadlockSeResuelveDevuelveElResultado() {
        var intentos = new AtomicInteger();
        String resultado = ReintentoPorBloqueo.ejecutar(() -> {
            if (intentos.incrementAndGet() == 1) throw conCodigo(1213, "40001");
            return "ok";
        });
        assertThat(resultado).isEqualTo("ok");
        assertThat(intentos).hasValue(2);
    }
}
