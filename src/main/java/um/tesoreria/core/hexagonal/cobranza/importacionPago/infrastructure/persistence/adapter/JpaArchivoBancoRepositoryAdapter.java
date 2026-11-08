package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoBancoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ArchivoBancoEntity;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.mapper.ArchivoBancoMapper;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.repository.JpaArchivoBancoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JpaArchivoBancoRepositoryAdapter implements ArchivoBancoRepository {

    private final JpaArchivoBancoRepository repository;
    private final ArchivoBancoMapper mapper;

    @Override
    public ArchivoBanco save(ArchivoBanco archivoBanco) {
        return mapper.toDomain(repository.save(mapper.toEntity(archivoBanco)));
    }

    @Override
    public List<ArchivoBanco> saveAll(List<ArchivoBanco> filas) {
        return repository.saveAll(filas.stream().map(mapper::toEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ArchivoBanco> findAllByArchivo(String archivo) {
        return repository.findAllByArchivo(archivo).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ArchivoBanco> findAllByArchivoAndPasado(String archivo, Byte pasado) {
        return repository.findAllByArchivoAndPasado(archivo, pasado).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByArchivo(String archivo) {
        repository.deleteAllByArchivo(archivo);
    }

    @Override
    public List<String> findDistinctArchivoByTipoPagoIdIn(Collection<Integer> tipoPagoIds) {
        return repository.findAllByTipoPagoIdIn(tipoPagoIds).stream()
                .map(ArchivoBancoEntity::getArchivo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    @Override
    public boolean existsByPago(String codigoEmpresa, Integer identificacion, Integer productoId, Integer mesPago,
                                Integer anhoPago, Integer cuotaId, BigDecimal importe, OffsetDateTime fechaPago,
                                String marcaAcreditacion) {
        return repository
                .findAllByCodigoEmpresaAndIdentificacionAndProductoIdAndMesPagoAndAnhoPagoAndCuotaIdAndImporteAndFechaPago(
                        codigoEmpresa, identificacion, productoId, mesPago, anhoPago, cuotaId, importe, fechaPago)
                .stream()
                .anyMatch(entity -> Objects.equals(entity.getMarcaAcreditacion(), marcaAcreditacion));
    }

    @Override
    public boolean existsByPagoConVerificador(String codigoEmpresa, Integer identificacion, Integer productoId,
                                              Integer mesPago, Integer anhoPago, Integer cuotaId, BigDecimal importe,
                                              OffsetDateTime fechaPago, String verificador) {
        String buscado = Objects.toString(verificador, "").trim();
        return repository
                .findAllByCodigoEmpresaAndIdentificacionAndProductoIdAndMesPagoAndAnhoPagoAndCuotaIdAndImporteAndFechaPago(
                        codigoEmpresa, identificacion, productoId, mesPago, anhoPago, cuotaId, importe, fechaPago)
                .stream()
                .anyMatch(entity -> buscado.equals(Objects.toString(entity.getVerificador(), "").trim()));
    }

}
