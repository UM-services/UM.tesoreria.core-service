package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository;

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
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.CreateBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.DeleteBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.UpdateBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaUsoAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.JpaBancariaRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.mapper.BancariaMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;
import um.tesoreria.core.kotlin.repository.BancariaRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Arranque de JPA con las tres entidades nuevas conviviendo con las Kotlin sobre bancaria, valores y libro_banco
 * (Hibernate no arranca si nombran distinto una columna), y casos de uso con transacciones, JPA e historial reales
 * sobre H2, sin transacción envolvente del test. La FK de bancaria a plancta existe en H2 (la crea Bancaria.kt): la
 * cuenta contable se inserta antes. La traducción de una FK rota está en JpaBancariaRepositoryAdapterTest y se
 * confirma contra MySQL en BancariaDevDbIT.
 */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateBancariaUseCaseImpl.class, UpdateBancariaUseCaseImpl.class, DeleteBancariaUseCaseImpl.class,
        JpaBancariaRepositoryAdapter.class, BancariaMapper.class, BancariaUsoAdapter.class, BancariaHistorialAdapter.class,
        EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class, BancariaPersistenceTest.Config.class})
public class BancariaPersistenceTest {
    @TestConfiguration
    @EnableJpaAuditing
    static class Config {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-10-23T15:00:00Z"), ZoneOffset.UTC);
        }
    }

    static final BigDecimal CUENTA = new BigDecimal("11101010001");

    @Autowired CreateBancariaUseCaseImpl crear;
    @Autowired UpdateBancariaUseCaseImpl editar;
    @Autowired DeleteBancariaUseCaseImpl borrar;
    @Autowired BancariaRepository legacy;
    @Autowired JdbcTemplate jdbc;
    @MockitoBean BancariaReferenciasPort referencias;
    @MockitoSpyBean BancariaHistorialAdapter historial;
    @Autowired EscrituraHistorialService historialService;
    @Autowired JpaBancariaRepositoryAdapter repository;
    @Autowired org.springframework.transaction.support.TransactionOperations tx;

    @BeforeEach
    void limpiar() {
        jdbc.update("DELETE FROM gestion_escritura_historial");
        jdbc.update("DELETE FROM libro_banco");
        jdbc.update("DELETE FROM valores");
        jdbc.update("DELETE FROM bancaria");
        jdbc.update("DELETE FROM plancta");
        jdbc.update("INSERT INTO plancta (pla_cuenta, pla_nombre, pla_integra, pla_grado, visible, created, updated) VALUES (11101010001, 'Banco', 0, 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        when(referencias.existeCuenta(any())).thenReturn(true);
        when(referencias.existeProveedor(any())).thenReturn(true);
    }

    int cuentas() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM bancaria", Integer.class);
    }

    int historiales() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class);
    }

    Bancaria nueva() {
        return crear.createBancaria(BancariaCambios.builder().banco(Campo.de("Santander")).numero(Campo.de("123-4"))
                .titular(Campo.de("Universidad")).cuit(Campo.de("30518594466")).numeroCuenta(Campo.de(CUENTA))
                .proveedorId(Campo.de(12)).build());
    }

    @Test
    void altaGuardaColumnasRealesCreatedUuidYHistorial() {
        Bancaria creada = nueva();
        assertThat(creada.getBancariaId()).isPositive();
        assertThat(jdbc.queryForObject("SELECT cub_banco FROM bancaria WHERE cub_id = ?", String.class, creada.getBancariaId()))
                .isEqualTo("Santander");
        assertThat(jdbc.queryForObject("SELECT cub_cuit FROM bancaria WHERE cub_id = ?", String.class, creada.getBancariaId()))
                .isEqualTo("30-51859446-6");
        assertThat(jdbc.queryForObject("SELECT cub_prv_id FROM bancaria WHERE cub_id = ?", Integer.class, creada.getBancariaId()))
                .isEqualTo(12);
        assertThat(jdbc.queryForObject("SELECT uuid FROM bancaria WHERE cub_id = ?", String.class, creada.getBancariaId()))
                .matches("[0-9a-f]{32}");
        assertThat(jdbc.queryForObject("SELECT created FROM bancaria WHERE cub_id = ?", java.sql.Timestamp.class, creada.getBancariaId()))
                .isEqualTo(java.sql.Timestamp.valueOf("2026-10-23 15:00:00"));
        assertThat(historiales()).isEqualTo(1);
    }

    @Test
    void laEntidadKotlinLegacyLeeLaFilaCreadaPorLaEntidadNueva() {
        Bancaria creada = nueva();
        var legada = legacy.findByBancariaId(creada.getBancariaId()).orElseThrow();
        assertThat(legada.getBanco()).isEqualTo("Santander");
        assertThat(legada.getCuit()).isEqualTo("30-51859446-6");
        assertThat(legada.getNumeroCuenta()).isEqualByComparingTo(CUENTA);
        assertThat(legada.getProveedorId()).isEqualTo(12);
        assertThat(legacy.findAll()).hasSize(1);
    }

    @Test
    void edicionPorPresenciaConservaLoAusenteYVaciaElNull() {
        Bancaria creada = nueva();
        jdbc.update("DELETE FROM gestion_escritura_historial");
        editar.updateBancaria(creada.getBancariaId(), BancariaCambios.builder().titular(Campo.de(null)).proveedorId(Campo.de(null)).build());
        assertThat(jdbc.queryForObject("SELECT cub_titular FROM bancaria WHERE cub_id = ?", String.class, creada.getBancariaId())).isEmpty();
        assertThat(jdbc.queryForObject("SELECT cub_prv_id FROM bancaria WHERE cub_id = ?", Integer.class, creada.getBancariaId())).isNull();
        assertThat(jdbc.queryForObject("SELECT cub_banco FROM bancaria WHERE cub_id = ?", String.class, creada.getBancariaId())).isEqualTo("Santander");
        assertThat(jdbc.queryForObject("SELECT created FROM bancaria WHERE cub_id = ?", java.sql.Timestamp.class, creada.getBancariaId()))
                .isEqualTo(java.sql.Timestamp.valueOf("2026-10-23 15:00:00"));
        assertThat(historiales()).isEqualTo(1);
        editar.updateBancaria(creada.getBancariaId(), BancariaCambios.builder().build());
        assertThat(historiales()).isEqualTo(1);
    }

    @Test
    void bajaDeCuentaLibreLaBorraYDejaHistorial() {
        Bancaria creada = nueva();
        jdbc.update("DELETE FROM gestion_escritura_historial");
        borrar.deleteBancaria(creada.getBancariaId());
        assertThat(cuentas()).isZero();
        assertThat(historiales()).isEqualTo(1);
    }

    @Test
    void bajaDeCuentaConValoresOMovimientosEsEnUsoConConteosYNoBorra() {
        Bancaria creada = nueva();
        Long id = creada.getBancariaId();
        jdbc.update("INSERT INTO valores (val_id, val_cuentaorigen, created, updated) VALUES (1, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", id);
        jdbc.update("INSERT INTO valores (val_id, val_cuentaorigen, created, updated) VALUES (2, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", id);
        jdbc.update("INSERT INTO libro_banco (lib_id, lib_cub_id, created, updated) VALUES (1, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", id);
        jdbc.update("DELETE FROM gestion_escritura_historial");
        assertThatThrownBy(() -> borrar.deleteBancaria(id)).isInstanceOfSatisfying(BancariaConflictoException.class,
                e -> assertThat(e.getMessage()).contains("2 valores").contains("1 movimientos"));
        assertThat(cuentas()).isEqualTo(1);
        assertThat(historiales()).isZero();
    }

    @Test
    void altaConCuentaContableQueDesaparecioEsConflictoPorLaFkReal() {
        assertThatThrownBy(() -> crear.createBancaria(BancariaCambios.builder().numeroCuenta(Campo.de(new BigDecimal("99999999999"))).build()))
                .isInstanceOfSatisfying(BancariaConflictoException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(BancariaConflictoException.Codigo.CONFLICTO));
        assertThat(cuentas()).isZero();
        assertThat(historiales()).isZero();
    }

    @Test
    void bajaConUnValorQueApareceDespuesDelChequeoEsEnUsoPorLaFkReal() {
        Bancaria creada = nueva();
        jdbc.update("INSERT INTO valores (val_id, val_cuentaorigen, created, updated) VALUES (1, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", creada.getBancariaId());
        assertThatThrownBy(() -> tx.execute(status -> {
            repository.deleteByBancariaId(creada.getBancariaId());
            return null;
        })).isInstanceOfSatisfying(BancariaConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(BancariaConflictoException.Codigo.EN_USO));
        assertThat(cuentas()).isEqualTo(1);
    }

    @Test
    void siFallaElHistorialDeAltaNoQuedaLaCuenta() {
        doThrow(new IllegalStateException("historial")).when(historial).registrarAlta(any());
        assertThatThrownBy(this::nueva).isInstanceOf(IllegalStateException.class);
        assertThat(cuentas()).isZero();
        assertThat(historiales()).isZero();
    }

    @Test
    void siFallaElHistorialDeEdicionLaCuentaQuedaComoEstaba() {
        Bancaria creada = nueva();
        jdbc.update("DELETE FROM gestion_escritura_historial");
        doThrow(new IllegalStateException("historial")).when(historial).registrarEdicion(any(), any());
        assertThatThrownBy(() -> editar.updateBancaria(creada.getBancariaId(),
                BancariaCambios.builder().banco(Campo.de("Otro")).build())).isInstanceOf(IllegalStateException.class);
        assertThat(jdbc.queryForObject("SELECT cub_banco FROM bancaria WHERE cub_id = ?", String.class, creada.getBancariaId()))
                .isEqualTo("Santander");
        assertThat(historiales()).isZero();
    }

    @Test
    void siFallaElHistorialDeBajaLaCuentaSigueExistiendo() {
        Bancaria creada = nueva();
        jdbc.update("DELETE FROM gestion_escritura_historial");
        doThrow(new IllegalStateException("historial")).when(historial).registrarBaja(any());
        assertThatThrownBy(() -> borrar.deleteBancaria(creada.getBancariaId())).isInstanceOf(IllegalStateException.class);
        assertThat(cuentas()).isEqualTo(1);
        assertThat(historiales()).isZero();
    }

    @Test
    void sinTransaccionActivaElHistorialSeRechaza() {
        assertThatThrownBy(() -> historialService.registrarAlta("bancaria", "1", java.util.Map.of("cub_id", 1)))
                .isInstanceOf(IllegalTransactionStateException.class);
        assertThat(historiales()).isZero();
    }
}
