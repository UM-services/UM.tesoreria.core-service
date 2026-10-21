package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.entity;

import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * La idempotencia de los pagos de Mercado Pago depende de que el mapeo declare el índice único
 * sobre {@code id_mercado_pago}: en producción lo crea {@code docs/sql/V415__uk_chequera_pago_id_mp.sql}
 * y en los tests (H2, ddl-auto: create-drop) lo crea Hibernate a partir de esta anotación.
 */
class ChequeraPagoEntityTest {

    @Test
    void declaraElIndiceUnicoDeIdMercadoPago() {
        Table table = ChequeraPagoEntity.class.getAnnotation(Table.class);

        assertThat(table).isNotNull();
        assertThat(Arrays.stream(table.uniqueConstraints()).map(UniqueConstraint::name))
                .contains("uk_chequera_pago_id_mp");
        assertThat(Arrays.stream(table.uniqueConstraints())
                .filter(unique -> "uk_chequera_pago_id_mp".equals(unique.name()))
                .findFirst()
                .orElseThrow()
                .columnNames())
                .containsExactly("id_mercado_pago");
    }

}
