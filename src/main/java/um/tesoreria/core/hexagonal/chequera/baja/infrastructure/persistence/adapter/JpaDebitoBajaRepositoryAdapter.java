package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DebitoBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out.DebitoBajaRepository;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.service.DebitoService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaDebitoBajaRepositoryAdapter implements DebitoBajaRepository {

    private final DebitoService debitoService;

    @Override
    public List<DebitoBaja> findAllAsociados(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                             Integer productoId, Integer alternativaId, Integer cuotaId) {
        return debitoService.findAllAsociados(facultadId, tipoChequeraId, chequeraSerieId, productoId,
                        alternativaId, cuotaId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void limpiarFechaBaja(Long debitoId) {
        Debito debito = debitoService.findByDebitoId(debitoId);
        debito.setFechaBaja(null);
        debitoService.update(debito, debitoId);
    }

    private DebitoBaja toDomain(Debito debito) {
        return DebitoBaja.builder()
                .debitoId(debito.getDebitoId())
                .facultadId(debito.getFacultadId())
                .tipoChequeraId(debito.getTipoChequeraId())
                .chequeraSerieId(debito.getChequeraSerieId())
                .productoId(debito.getProductoId())
                .alternativaId(debito.getAlternativaId())
                .cuotaId(debito.getCuotaId())
                .fechaBaja(debito.getFechaBaja())
                .build();
    }

}
