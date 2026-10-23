package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases.CreateEjercicioUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases.DeleteEjercicioUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases.UpdateEjercicioUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioUsoPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter.EjercicioHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter.JpaEjercicioRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.mapper.EjercicioMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/** Casos de uso con transacciones, JPA e historial reales sobre H2; sin transacción envolvente del test. */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateEjercicioUseCaseImpl.class, UpdateEjercicioUseCaseImpl.class, DeleteEjercicioUseCaseImpl.class,
        JpaEjercicioRepositoryAdapter.class, EjercicioMapper.class, EjercicioHistorialAdapter.class,
        EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class, EjercicioPersistenceTest.Config.class})
public class EjercicioPersistenceTest {
    @TestConfiguration
    @EnableJpaAuditing
    static class Config {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-10-21T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        }
    }

    @Autowired CreateEjercicioUseCaseImpl crear;
    @Autowired UpdateEjercicioUseCaseImpl editar;
    @Autowired DeleteEjercicioUseCaseImpl borrar;
    @Autowired JpaEjercicioRepositoryAdapter repository;
    @Autowired JdbcTemplate jdbc;
    @Autowired org.springframework.transaction.support.TransactionOperations tx;
    @MockitoBean EjercicioUsoPort uso;
    @MockitoSpyBean EjercicioHistorialAdapter historial;

    @BeforeEach
    void limpiar() {
        jdbc.update("DELETE FROM gestion_escritura_historial");
        jdbc.update("DELETE FROM ejercicios");
        crear.createEjercicio(Ejercicio.builder().ejercicioId(15).nombre("Ejercicio Económico 2026")
                .fechaInicio(LocalDate.parse("2026-03-01")).fechaFinal(LocalDate.parse("2027-02-28")).build());
        jdbc.update("DELETE FROM gestion_escritura_historial");
    }

    int historiales() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class);
    }

    @Test
    void altaSiguienteMapeaColumnasRealesYFechasEnUtc() {
        Ejercicio creado = crear.createEjercicio(Ejercicio.builder().build());
        assertThat(creado.getEjercicioId()).isEqualTo(16);
        Ejercicio leido = tx.execute(status -> repository.findWithLockByEjercicioId(16).orElseThrow());
        assertThat(leido.getFechaInicio()).isEqualTo(LocalDate.parse("2027-03-01"));
        assertThat(leido.getFechaFinal()).isEqualTo(LocalDate.parse("2028-02-29"));
        assertThat(jdbc.queryForObject("SELECT Eje_Nombre FROM ejercicios WHERE Eje_ID = 16", String.class))
                .isEqualTo("Ejercicio Económico 2027");
        assertThat(jdbc.queryForObject("SELECT orden_contable_resultado FROM ejercicios WHERE Eje_ID = 16", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT orden_contable_bienes_uso FROM ejercicios WHERE Eje_ID = 16", Integer.class)).isZero();
        assertThat(historiales()).isEqualTo(1);
    }

    @Test
    void altaConIdExistenteFallaYNoActualizaLaFila() {
        // Se salta la derivación para ejercitar el adaptador: con id existente persist falla, save haría merge.
        var otro = Ejercicio.builder().ejercicioId(15).nombre("PISADO").fechaInicio(LocalDate.parse("2030-03-01"))
                .fechaFinal(LocalDate.parse("2031-02-28")).bloqueado((byte) 1).build();
        assertThatThrownBy(() -> tx.execute(status -> repository.create(otro)))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
        assertThat(jdbc.queryForObject("SELECT Eje_Nombre FROM ejercicios WHERE Eje_ID = 15", String.class))
                .isEqualTo("Ejercicio Económico 2026");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM ejercicios", Integer.class)).isEqualTo(1);
    }

    @Test
    void siFallaElHistorialNoQuedaElEjercicio() {
        doThrow(new IllegalStateException("historial")).when(historial).registrarAlta(any());
        assertThatThrownBy(() -> crear.createEjercicio(Ejercicio.builder().build())).isInstanceOf(IllegalStateException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM ejercicios", Integer.class)).isEqualTo(1);
    }

    @Test
    void bloquearConservaTodoLoDemasYDejaUnaEdicionEnElHistorial() {
        crear.createEjercicio(Ejercicio.builder().build());
        jdbc.update("UPDATE ejercicios SET orden_contable_resultado = 7 WHERE Eje_ID = 16");
        jdbc.update("DELETE FROM gestion_escritura_historial");
        editar.updateEjercicio(16, Ejercicio.builder().bloqueado((byte) 1).build());
        assertThat(jdbc.queryForObject("SELECT Eje_Bloqueado FROM ejercicios WHERE Eje_ID = 16", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT orden_contable_resultado FROM ejercicios WHERE Eje_ID = 16", Integer.class)).isEqualTo(7);
        assertThat(jdbc.queryForObject("SELECT Eje_Nombre FROM ejercicios WHERE Eje_ID = 16", String.class)).isEqualTo("Ejercicio Económico 2027");
        assertThat(historiales()).isEqualTo(1);
        editar.updateEjercicio(16, Ejercicio.builder().bloqueado((byte) 1).build());
        assertThat(historiales()).isEqualTo(1);
    }

    @Test
    void bajaDelUltimoFuturoLoBorraYDejaHistorial() {
        crear.createEjercicio(Ejercicio.builder().build());
        jdbc.update("DELETE FROM gestion_escritura_historial");
        borrar.deleteEjercicio(16);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM ejercicios", Integer.class)).isEqualTo(1);
        assertThat(historiales()).isEqualTo(1);
    }

    @Test
    void bajaRechazadaNoBorraNiDejaHistorial() {
        assertThatThrownBy(() -> borrar.deleteEjercicio(15))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.NO_FUTURO));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM ejercicios", Integer.class)).isEqualTo(1);
        assertThat(historiales()).isZero();
    }
}
