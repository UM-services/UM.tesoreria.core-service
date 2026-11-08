package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.DebitoCobranza;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.DebitoCobranzaRepository;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.repository.DebitoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador del slice sobre la tabla {@code debito}.
 *
 * <p>Reutiliza la entidad y el repositorio JPA existentes (la tabla ya está mapeada por el
 * legacy). Las actualizaciones cargan la entidad y cambian sólo los campos de la importación
 * (rechazo, motivo y fila de archivo), sin reconstruir el resto del débito.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaDebitoCobranzaRepositoryAdapter implements DebitoCobranzaRepository {

    private final DebitoRepository repository;

    @Override
    public Optional<DebitoCobranza> findById(Long debitoId) {
        return repository.findByDebitoId(debitoId).map(this::toDomain);
    }

    @Override
    public Optional<DebitoCobranza> findByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                Integer productoId, Integer alternativaId, Integer cuotaId,
                                                Integer debitoTipoId) {
        return repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdAndDebitoTipoId(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, debitoTipoId)
                .map(this::toDomain);
    }

    @Override
    public void marcarArchivoBanco(Long debitoId, Long archivoBancoId) {
        Debito debito = repository.findByDebitoId(debitoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el débito " + debitoId));
        debito.setArchivoBancoId(archivoBancoId);
        repository.save(debito);
    }

    @Override
    public void marcarRechazado(Long debitoId, String motivoRechazo, Long archivoBancoId) {
        Debito debito = repository.findByDebitoId(debitoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el débito " + debitoId));
        debito.setRechazado((byte) 1);
        debito.setMotivoRechazo(motivoRechazo);
        debito.setArchivoBancoId(archivoBancoId);
        repository.save(debito);
    }

    @Override
    public List<DebitoCobranza> findAllByArchivoBancoIds(Collection<Long> archivoBancoIds) {
        if (archivoBancoIds == null || archivoBancoIds.isEmpty()) {
            return List.of();
        }
        return repository.findAllByArchivoBancoIdIn(archivoBancoIds).stream()
                .map(this::toDomain)
                .toList();
    }

    private DebitoCobranza toDomain(Debito debito) {
        return DebitoCobranza.builder()
                .debitoId(debito.getDebitoId())
                .facultadId(debito.getFacultadId())
                .tipoChequeraId(debito.getTipoChequeraId())
                .chequeraSerieId(debito.getChequeraSerieId())
                .productoId(debito.getProductoId())
                .alternativaId(debito.getAlternativaId())
                .cuotaId(debito.getCuotaId())
                .debitoTipoId(debito.getDebitoTipoId())
                .rechazado(debito.getRechazado())
                .motivoRechazo(debito.getMotivoRechazo())
                .archivoBancoId(debito.getArchivoBancoId())
                .fechaVencimiento(debito.getFechaVencimiento())
                .fechaEnvio(debito.getFechaEnvio())
                .build();
    }

}
