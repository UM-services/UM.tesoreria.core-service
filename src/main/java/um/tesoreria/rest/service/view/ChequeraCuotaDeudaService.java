/**
 *
 */
package um.tesoreria.rest.service.view;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import um.tesoreria.rest.kotlin.model.ChequeraCuota;
import um.tesoreria.rest.kotlin.model.ClaseChequera;
import um.tesoreria.rest.kotlin.model.TipoChequera;
import um.tesoreria.rest.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.rest.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.repository.view.IChequeraCuotaDeudaRepository;
import um.tesoreria.rest.util.Tool;
import um.tesoreria.rest.service.ClaseChequeraService;
import um.tesoreria.rest.service.TipoChequeraService;

/**
 * @author daniel
 */
@Service
public class ChequeraCuotaDeudaService {

    private final IChequeraCuotaDeudaRepository repository;

    private final ClaseChequeraService claseChequeraService;

    private final TipoChequeraService tipoChequeraService;

    @Autowired
    public ChequeraCuotaDeudaService(IChequeraCuotaDeudaRepository repository, ClaseChequeraService claseChequeraService, TipoChequeraService tipoChequeraService) {
        this.repository = repository;
        this.claseChequeraService = claseChequeraService;
        this.tipoChequeraService = tipoChequeraService;
    }

    public List<ChequeraCuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced, Pageable pageable, ChequeraCuotaService chequeraCuotaService) {
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

    public List<ChequeraCuotaDeuda> findAllPosgradoByRango(OffsetDateTime desde, OffsetDateTime hasta, ChequeraCuotaService chequeraCuotaService) {
        List<Integer> claseChequeraIds = claseChequeraService.findAllByPosgrado().stream().map(ClaseChequera::getClaseChequeraId).toList();
        return findAllByFilter(claseChequeraIds, desde, hasta, chequeraCuotaService);
    }

    public List<ChequeraCuotaDeuda> findAllMacroByRango(OffsetDateTime desde, OffsetDateTime hasta, ChequeraCuotaService chequeraCuotaService) {
        List<Integer> claseChequeraIds = new ArrayList<>(claseChequeraService.findAllByPosgrado().stream().map(ClaseChequera::getClaseChequeraId).toList());
        claseChequeraIds.addAll(claseChequeraService.findAllByCurso().stream().map(ClaseChequera::getClaseChequeraId).toList());
        claseChequeraIds.addAll(claseChequeraService.findAllByTitulo().stream().map(ClaseChequera::getClaseChequeraId).toList());
        return findAllByFilter(claseChequeraIds, desde, hasta, chequeraCuotaService);
    }

    private List<ChequeraCuotaDeuda> findAllByFilter(List<Integer> claseChequeraIds, OffsetDateTime desde, OffsetDateTime hasta, ChequeraCuotaService chequeraCuotaService) {
        List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequeraIds(claseChequeraIds).stream().map(TipoChequera::getTipoChequeraId).toList();
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
