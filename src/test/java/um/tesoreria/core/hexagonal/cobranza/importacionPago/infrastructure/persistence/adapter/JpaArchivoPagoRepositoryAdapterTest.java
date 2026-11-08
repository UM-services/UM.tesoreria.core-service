package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoBancoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoImportadoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.mapper.ArchivoBancoMapper;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.mapper.ArchivoImportadoMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        ArchivoImportadoMapper.class,
        ArchivoBancoMapper.class,
        JpaArchivoImportadoRepositoryAdapter.class,
        JpaArchivoBancoRepositoryAdapter.class
})
class JpaArchivoPagoRepositoryAdapterTest {

    @Autowired
    private ArchivoImportadoRepository archivoImportadoRepository;

    @Autowired
    private ArchivoBancoRepository archivoBancoRepository;

    @Test
    void guardaYBuscaLaCabeceraPorNombre() {
        ArchivoImportado guardado = archivoImportadoRepository.save(ArchivoImportado.builder()
                .nombre("pmc.txt")
                .fecha(OffsetDateTime.parse("2026-01-20T00:00:00Z"))
                .registros(2)
                .build());

        assertThat(guardado.getId()).isNotNull();
        assertThat(archivoImportadoRepository.existsByNombre("pmc.txt")).isTrue();
        assertThat(archivoImportadoRepository.findByNombre("pmc.txt")).isPresent();
        assertThat(archivoImportadoRepository.findAllByNombreIn(List.of("pmc.txt"))).hasSize(1);
    }

    @Test
    void guardaFilasDeArchivoYDetectaPagosYPendientes() {
        OffsetDateTime fechaPago = OffsetDateTime.parse("2026-01-20T00:00:00Z");
        ArchivoBanco guardada = archivoBancoRepository.save(ArchivoBanco.builder()
                .codigoEmpresa("6")
                .identificacion(200123)
                .productoId(1)
                .mesPago(1)
                .anhoPago(2026)
                .cuotaId(1)
                .importe(new BigDecimal("123.45"))
                .fechaPago(fechaPago)
                .archivo("pmc.txt")
                .pasado((byte) 1)
                .tipoPagoId(4)
                .repetido((byte) 0)
                .build());

        assertThat(guardada.getId()).isNotNull();
        assertThat(archivoBancoRepository.findAllByArchivo("pmc.txt")).hasSize(1);
        assertThat(archivoBancoRepository.findAllByArchivoAndPasado("pmc.txt", (byte) 0)).isEmpty();
        assertThat(archivoBancoRepository.findDistinctArchivoByTipoPagoIdIn(List.of(4)))
                .containsExactly("pmc.txt");
        assertThat(archivoBancoRepository.existsByPago("6", 200123, 1, 1, 2026, 1,
                new BigDecimal("123.45"), fechaPago, null)).isTrue();
        assertThat(archivoBancoRepository.existsByPago("6", 200123, 1, 1, 2026, 1,
                new BigDecimal("999.99"), fechaPago, null)).isFalse();
    }

    @Test
    void borraLasFilasDeUnArchivo() {
        archivoBancoRepository.save(ArchivoBanco.builder()
                .codigoEmpresa("6")
                .identificacion(200123)
                .productoId(1)
                .mesPago(1)
                .anhoPago(2026)
                .cuotaId(1)
                .importe(new BigDecimal("123.45"))
                .fechaPago(OffsetDateTime.parse("2026-01-20T00:00:00Z"))
                .archivo("pmc.txt")
                .pasado((byte) 0)
                .tipoPagoId(4)
                .build());

        archivoBancoRepository.deleteByArchivo("pmc.txt");

        assertThat(archivoBancoRepository.findAllByArchivo("pmc.txt")).isEmpty();
    }

}
