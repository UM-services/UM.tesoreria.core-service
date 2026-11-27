package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioSolicitudEntity;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository.JpaEnvioSolicitudRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifica el mapeo y la garantía dura del ledger en H2: una sola solicitud por chequera.
 */
@SpringBootTest
@Transactional
class EnvioSolicitudRepositoryTest {

    @Autowired
    private JpaEnvioSolicitudRepository repository;

    @Test
    void persisteYRecuperaPorChequera() {
        var solicitud = solicitud(UUID.randomUUID(), 900001L, "CHEQUERA:900001");

        repository.saveAndFlush(solicitud);

        assertThat(repository.findByChequeraId(900001L)).isPresent()
                .get()
                .extracting(EnvioSolicitudEntity::getEstado)
                .isEqualTo(EnvioSolicitudEstado.PENDIENTE);
        assertThat(repository.findBySolicitudId(solicitud.getSolicitudId())).isPresent();
        assertThat(repository.findByDedupeKey("CHEQUERA:900001")).isPresent();
    }

    @Test
    void noPermiteDosSolicitudesParaLaMismaChequera() {
        repository.saveAndFlush(solicitud(UUID.randomUUID(), 900002L, "CHEQUERA:900002"));

        assertThatThrownBy(() -> repository.saveAndFlush(
                solicitud(UUID.randomUUID(), 900002L, "CHEQUERA:900002-otra")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static EnvioSolicitudEntity solicitud(UUID id, Long chequeraId, String dedupeKey) {
        return EnvioSolicitudEntity.builder()
                .solicitudId(id)
                .chequeraId(chequeraId)
                .facultadId(15)
                .tipoChequeraId(400)
                .chequeraSerieId(1567L)
                .lectivoId(37)
                .origen("JOB")
                .dedupeKey(dedupeKey)
                .estado(EnvioSolicitudEstado.PENDIENTE)
                .intentos(0)
                .build();
    }

}
