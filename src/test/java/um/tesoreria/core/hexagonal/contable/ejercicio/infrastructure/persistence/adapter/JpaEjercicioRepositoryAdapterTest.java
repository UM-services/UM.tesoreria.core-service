package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.mapper.EjercicioMapper;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.repository.JpaEjercicioRepository;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class JpaEjercicioRepositoryAdapterTest {
    JpaEjercicioRepository jpa = mock(JpaEjercicioRepository.class);
    EntityManager entityManager = mock(EntityManager.class);
    JpaEjercicioRepositoryAdapter adapter = new JpaEjercicioRepositoryAdapter(jpa, new EjercicioMapper(), entityManager);
    Ejercicio ejercicio = Ejercicio.builder().ejercicioId(16).nombre("Ejercicio Económico 2027")
            .fechaInicio(LocalDate.parse("2027-03-01")).fechaFinal(LocalDate.parse("2028-02-29")).build();

    @Test
    void unaFkQueRechazaElDeleteSeTraduceAEnUso() {
        doThrow(new DataIntegrityViolationException("fk")).when(jpa).deleteById(16);
        assertThatThrownBy(() -> adapter.deleteByEjercicioId(16))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.EN_USO));
    }

    @Test
    void laPkRepetidaSeTraduceADuplicado() {
        var unica = new ConstraintViolationException("dup", new SQLException("dup"), ConstraintKind.UNIQUE, "PRIMARY");
        doThrow(unica).when(entityManager).persist(any());
        assertThatThrownBy(() -> adapter.create(ejercicio))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
    }

    @Test
    void unaViolacionQueNoEsUnicaNoSeDisfrazaDeDuplicado() {
        var notNull = new ConstraintViolationException("nn", new SQLException("nn"), ConstraintKind.NOT_NULL, "eje_nombre");
        doThrow(notNull).when(entityManager).persist(any());
        assertThatThrownBy(() -> adapter.create(ejercicio)).isSameAs(notNull);
    }

    @Test
    void unaPersistenceExceptionGenericaSeRelanzaSinTraducir() {
        var otra = new PersistenceException("otra");
        doThrow(otra).when(entityManager).flush();
        assertThatThrownBy(() -> adapter.create(ejercicio)).isSameAs(otra);
    }
}
