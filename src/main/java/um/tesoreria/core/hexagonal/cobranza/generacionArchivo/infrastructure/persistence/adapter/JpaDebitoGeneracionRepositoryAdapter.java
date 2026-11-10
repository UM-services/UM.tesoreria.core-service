package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.DebitoGeneracionRepository;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.service.DebitoService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Adaptador del slice sobre la tabla {@code debito}. Reutiliza el servicio existente y sólo
 * modifica los campos de la generación (CBU, fecha de envío y fecha de vencimiento).
 */
@Component
@RequiredArgsConstructor
public class JpaDebitoGeneracionRepositoryAdapter implements DebitoGeneracionRepository {

    private final DebitoService debitoService;

    @Override
    public List<DebitoPendiente> findPendientes(OffsetDateTime desde, OffsetDateTime hasta,
                                                boolean soloSantander, boolean soloOtrosBancos,
                                                Integer debitoTipoId) {
        return debitoService.findAllPendientes(desde, hasta, soloSantander, soloOtrosBancos, debitoTipoId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<DebitoPendiente> findNoEnviadosAltas(OffsetDateTime desde, OffsetDateTime hasta,
                                                     Integer debitoTipoId) {
        return debitoService.findAllNoEnviadosAltas(desde, hasta, debitoTipoId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<DebitoPendiente> findByFechaEnvio(OffsetDateTime fechaEnvio, boolean soloSantander,
                                                  boolean soloOtrosBancos, Integer debitoTipoId) {
        return debitoService.findAllByFechaEnvio(fechaEnvio, soloSantander, soloOtrosBancos, debitoTipoId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<DebitoPendiente> findAllAsociados(Integer facultadId, Integer tipoChequeraId,
                                                  Long chequeraSerieId, Integer productoId,
                                                  Integer alternativaId, Integer cuotaId) {
        return debitoService.findAllAsociados(facultadId, tipoChequeraId, chequeraSerieId,
                        productoId, alternativaId, cuotaId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsEnviadoByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                        Integer productoId, Integer alternativaId, Integer cuotaId,
                                        Integer debitoTipoId) {
        return !debitoService.findAllByEnviados(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, debitoTipoId).isEmpty();
    }

    @Override
    public void marcarFechaEnvio(Long debitoId, OffsetDateTime fechaEnvio) {
        Debito debito = debitoService.findByDebitoId(debitoId);
        debito.setFechaEnvio(fechaEnvio);
        debitoService.update(debito, debitoId);
    }

    @Override
    public void actualizarCbu(Long debitoId, String cbu) {
        Debito debito = debitoService.findByDebitoId(debitoId);
        debito.setCbu(cbu);
        debitoService.update(debito, debitoId);
    }

    @Override
    public void actualizarFechaVencimiento(Long debitoId, OffsetDateTime fechaVencimiento) {
        Debito debito = debitoService.findByDebitoId(debitoId);
        debito.setFechaVencimiento(fechaVencimiento);
        debitoService.update(debito, debitoId);
    }

    private DebitoPendiente toDomain(Debito debito) {
        return DebitoPendiente.builder()
                .debitoId(debito.getDebitoId())
                .facultadId(debito.getFacultadId())
                .tipoChequeraId(debito.getTipoChequeraId())
                .chequeraSerieId(debito.getChequeraSerieId())
                .productoId(debito.getProductoId())
                .alternativaId(debito.getAlternativaId())
                .cuotaId(debito.getCuotaId())
                .debitoTipoId(debito.getDebitoTipoId())
                .cbu(debito.getCbu())
                .numeroTarjeta(debito.getNumeroTarjeta())
                .fechaVencimiento(debito.getFechaVencimiento())
                .fechaEnvio(debito.getFechaEnvio())
                .fechaBaja(debito.getFechaBaja())
                .rechazado(debito.getRechazado())
                .build();
    }

}
