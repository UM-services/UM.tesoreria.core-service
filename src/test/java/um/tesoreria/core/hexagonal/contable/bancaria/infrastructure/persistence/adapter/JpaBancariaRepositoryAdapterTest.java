package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.BancariaEntity;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.mapper.BancariaMapper;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository.JpaBancariaRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Solo una clave foránea rota se traduce a 409; un NOT NULL, un duplicado o cualquier otra violación sigue como
 * error interno. Las excepciones se simulan; la clasificación real de MySQL 5.7 la confirma BancariaDevDbIT y el
 * E2E contra dev.
 */
class JpaBancariaRepositoryAdapterTest {
    JpaBancariaRepository jpa = mock(JpaBancariaRepository.class);
    EntityManager entityManager = mock(EntityManager.class);
    JpaBancariaRepositoryAdapter adapter = new JpaBancariaRepositoryAdapter(jpa, new BancariaMapper(), entityManager);
    Bancaria bancaria = Bancaria.builder().bancariaId(8L).banco("Santander").numeroCuenta(BigDecimal.TEN).build();

    static ConstraintViolationException violacion(ConstraintKind tipo) {
        return new ConstraintViolationException("violacion", new SQLException("violacion"), tipo, "bancaria_ibfk_1");
    }

    static DataIntegrityViolationException envuelta(Throwable causa) {
        return new DataIntegrityViolationException("integridad", causa);
    }

    @Test
    void altaConFkRotaEsConflictoSeaSpringOHibernateLaExcepcion() {
        doThrow(envuelta(violacion(ConstraintKind.FOREIGN_KEY))).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> adapter.create(bancaria)).isInstanceOfSatisfying(BancariaConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CONFLICTO));
        doThrow(violacion(ConstraintKind.FOREIGN_KEY)).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> adapter.create(bancaria)).isInstanceOfSatisfying(BancariaConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CONFLICTO));
    }

    @Test
    void altaConOtraViolacionDeIntegridadNoSeDisfrazaDeConflicto() {
        var notNull = envuelta(violacion(ConstraintKind.NOT_NULL));
        doThrow(notNull).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> adapter.create(bancaria)).isSameAs(notNull);
        var unica = envuelta(violacion(ConstraintKind.UNIQUE));
        doThrow(unica).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> adapter.create(bancaria)).isSameAs(unica);
        var sinClasificar = new DataIntegrityViolationException("sin causa");
        doThrow(sinClasificar).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> adapter.create(bancaria)).isSameAs(sinClasificar);
    }

    @Test
    void edicionConFkRotaEsConflictoYConNotNullSigueComoEstaba() {
        when(jpa.findById(8L)).thenReturn(Optional.of(BancariaEntity.builder().bancariaId(8L).build()));
        doThrow(envuelta(violacion(ConstraintKind.FOREIGN_KEY))).when(jpa).flush();
        assertThatThrownBy(() -> adapter.update(bancaria)).isInstanceOfSatisfying(BancariaConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CONFLICTO));
        var notNull = envuelta(violacion(ConstraintKind.NOT_NULL));
        doThrow(notNull).when(jpa).flush();
        assertThatThrownBy(() -> adapter.update(bancaria)).isSameAs(notNull);
    }

    @Test
    void bajaConFkRotaEsEnUsoYConOtraViolacionNo() {
        doThrow(envuelta(violacion(ConstraintKind.FOREIGN_KEY))).when(jpa).flush();
        assertThatThrownBy(() -> adapter.deleteByBancariaId(8L)).isInstanceOfSatisfying(BancariaConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(Codigo.EN_USO));
        var notNull = envuelta(violacion(ConstraintKind.NOT_NULL));
        doThrow(notNull).when(jpa).flush();
        assertThatThrownBy(() -> adapter.deleteByBancariaId(8L)).isSameAs(notNull);
        verify(jpa, org.mockito.Mockito.atLeastOnce()).deleteById(8L);
    }

    @Test
    void unaPersistenceExceptionGenericaSeRelanzaSinTraducir() {
        var otra = new PersistenceException("otra");
        doThrow(otra).when(jpa).saveAndFlush(any());
        assertThatThrownBy(() -> adapter.create(bancaria)).isSameAs(otra);
    }
}
