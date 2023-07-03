/**
 *
 */
package um.tesoreria.rest.service.view;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import um.tesoreria.rest.kotlin.model.ChequeraCuota;
import um.tesoreria.rest.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.rest.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.repository.view.IChequeraCuotaDeudaRepository;
import um.tesoreria.rest.util.Tool;
import um.tesoreria.rest.repository.view.IChequeraCuotaDeudaRepository;
import um.tesoreria.rest.service.ClaseChequeraService;
import um.tesoreria.rest.service.TipoChequeraService;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraCuotaDeudaService {

    @Autowired
    private IChequeraCuotaDeudaRepository repository;

    @Autowired
    private ClaseChequeraService claseChequeraService;

    @Autowired
    private TipoChequeraService tipoChequeraService;

    @Autowired
    private ChequeraCuotaService chequeraCuotaService;

    public List<ChequeraCuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced, Pageable pageable) {
        return repository.findAllByVencimiento1Between(Tool.firstTime(desde), Tool.lastTime(hasta), pageable).stream().map(cuotaDeuda -> {
            if (cuotaDeuda.getChequeraId() == null) {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(cuotaDeuda.getChequeraCuotaId());
                cuotaDeuda.setChequeraId(chequeraCuota.getChequeraId());
                cuotaDeuda.setChequeraSerie(chequeraCuota.getChequeraSerie());
            }
            if (reduced) {
                cuotaDeuda.setChequeraSerie(null);
                cuotaDeuda.setProducto(null);
            }
            return cuotaDeuda;
        }).toList();
    }

    public List<ChequeraCuotaDeuda> findAllPosgradoByRango(OffsetDateTime desde, OffsetDateTime hasta) {
        List<Integer> claseChequeraIds = claseChequeraService.findAllByPosgrado().stream().map(claseChequera -> claseChequera.getClaseChequeraId()).collect(Collectors.toList());
        List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequeraIds(claseChequeraIds).stream().map(tipoChequera -> tipoChequera.getTipoChequeraId()).collect(Collectors.toList());
        return repository.findAllByTipoChequeraIdInAndVencimiento1Between(tipoChequeraIds, Tool.firstTime(desde), Tool.lastTime(hasta)).stream().map(cuotaDeuda -> {
            if (cuotaDeuda.getChequeraId() == null) {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(cuotaDeuda.getChequeraCuotaId());
                cuotaDeuda.setChequeraId(chequeraCuota.getChequeraId());
                cuotaDeuda.setChequeraSerie(chequeraCuota.getChequeraSerie());
            }
            return cuotaDeuda;
        }).toList();
    }

}
