package um.tesoreria.core.hexagonal.chequera.baja.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.baja.application.exception.BajaException;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DatosBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DebitoBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.ResultadoAnulacionBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out.DebitoBajaRepository;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out.SeguroBajaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.exception.ChequeraTotalException;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Anulación completa de una baja: borra la fila de {@code baja}, desmarca las cuotas, limpia la
 * fecha de baja de los débitos, resetea el seguro y recalcula los totales, todo en una transacción.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnularBajaService {

    private final BajaService bajaService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraTotalService chequeraTotalService;
    private final DebitoBajaRepository debitoBajaRepository;
    private final SeguroBajaRepository seguroBajaRepository;

    @Transactional
    public ResultadoAnulacionBaja anular(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        DatosBaja baja = bajaService.findDatosByUnique(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new BajaException(facultadId, tipoChequeraId, chequeraSerieId));

        List<ChequeraCuota> cuotas = chequeraCuotaService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
        Set<ClaveProducto> productos = new LinkedHashSet<>();
        int debitosActualizados = 0;
        for (ChequeraCuota cuota : cuotas) {
            cuota.setBaja((byte) 0);
            chequeraCuotaService.update(cuota, cuota.getChequeraCuotaId());
            productos.add(new ClaveProducto(cuota.getProductoId(), cuota.getAlternativaId()));
            for (DebitoBaja debito : debitoBajaRepository.findAllAsociados(cuota.getFacultadId(),
                    cuota.getTipoChequeraId(), cuota.getChequeraSerieId(), cuota.getProductoId(),
                    cuota.getAlternativaId(), cuota.getCuotaId())) {
                debitoBajaRepository.limpiarFechaBaja(debito.getDebitoId());
                debitosActualizados++;
            }
        }

        seguroBajaRepository.resetBaja(baja.documentoId(), baja.personaId(), baja.lectivoId());
        productos.forEach(clave -> recalcularTotal(facultadId, tipoChequeraId, chequeraSerieId,
                clave.productoId(), clave.alternativaId()));
        bajaService.deleteByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        return new ResultadoAnulacionBaja(cuotas.size(), debitosActualizados, true);
    }

    private void recalcularTotal(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                 Integer productoId, Integer alternativaId) {
        try {
            ChequeraTotal total = chequeraTotalService.findByUnique(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId);
            total.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId, alternativaId));
            total.setPagado(chequeraCuotaService.calculateTotalCuotasPagadas(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId, alternativaId));
            chequeraTotalService.update(total, total.getChequeraTotalId());
        } catch (ChequeraTotalException e) {
            log.warn("No existe chequera_total para {}.{}.{}.{}; se omite el recálculo",
                    facultadId, tipoChequeraId, chequeraSerieId, productoId);
        }
    }

    private record ClaveProducto(Integer productoId, Integer alternativaId) {
    }

}
