package um.tesoreria.core.audit;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * El diff no debe reportar cambios que no existen. El caso que lo motivó:
 * {@code importePagado} aparecía en {@code changedFields} de casi todos los updates de
 * {@code MercadoPagoContextEntity} aunque el valor anterior y el nuevo fueran iguales
 * ({@code 0} vs {@code 0.00}, escalas distintas de {@link BigDecimal}).
 */
class AuditDiffBuilderTest {

    private final AuditDiffBuilder builder = new AuditDiffBuilder(new AuditProperties(), new ObjectMapper());

    @Test
    void noMarcaUnBigDecimalQueSoloCambiaDeEscala() {
        var diff = builder.build(persister("importePagado"), state(new BigDecimal("0")),
                state(new BigDecimal("0.00")), AuditDiffBuilder.Operation.UPDATE);

        assertThat(diff.changedFields()).isEmpty();
        assertThat(diff.before()).isEmpty();
        assertThat(diff.after()).isEmpty();
    }

    @Test
    void siMarcaUnBigDecimalQueCambiaDeValor() {
        var diff = builder.build(persister("importePagado"), state(new BigDecimal("204500")),
                state(new BigDecimal("204500.01")), AuditDiffBuilder.Operation.UPDATE);

        assertThat(diff.changedFields()).containsExactly("importePagado");
        assertThat(diff.before()).containsKey("importePagado");
        assertThat(diff.after()).containsKey("importePagado");
    }

    @Test
    void dosOffsetDateTimeDelMismoInstanteNoSonUnCambio() {
        var diff = builder.build(persister("fechaVencimiento"),
                state(OffsetDateTime.of(2027, 1, 11, 0, 0, 0, 0, ZoneOffset.UTC)),
                state(OffsetDateTime.of(2027, 1, 10, 21, 0, 0, 0, ZoneOffset.ofHours(-3))),
                AuditDiffBuilder.Operation.UPDATE);

        assertThat(diff.changedFields()).isEmpty();
    }

    @Test
    void unCambioRealDeUnOffsetDateTimeSiSeMarca() {
        var diff = builder.build(persister("fechaVencimiento"),
                state(OffsetDateTime.of(2027, 1, 11, 0, 0, 0, 0, ZoneOffset.UTC)),
                state(OffsetDateTime.of(2027, 1, 12, 0, 0, 0, 0, ZoneOffset.UTC)),
                AuditDiffBuilder.Operation.UPDATE);

        assertThat(diff.changedFields()).containsExactly("fechaVencimiento");
    }

    private EntityPersister persister(String propertyName) {
        EntityPersister persister = mock(EntityPersister.class);
        Type type = mock(Type.class);
        when(persister.getEntityName()).thenReturn("um.tesoreria.core.model.TestEntity");
        when(persister.getPropertyNames()).thenReturn(new String[]{propertyName});
        when(persister.getPropertyTypes()).thenReturn(new Type[]{type});
        when(type.isAssociationType()).thenReturn(false);
        return persister;
    }

    private Object[] state(Object value) {
        return new Object[]{value};
    }

}
