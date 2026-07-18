/**
 *
 */
package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.core.service.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import um.tesoreria.core.repository.view.ChequeraCuotaDeudaRepository;
import um.tesoreria.core.util.Tool;
import um.tesoreria.core.hexagonal.chequera.claseChequera.application.service.ClaseChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;

/**
 * @author daniel
 */
@Service
@RequiredArgsConstructor
public class ChequeraCuotaDeudaService {

    private final ChequeraCuotaDeudaRepository repository;
    private final ClaseChequeraService claseChequeraService;
    private final TipoChequeraService tipoChequeraService;
    private final ChequeraSerieService chequeraSerieService;
    private final FacultadGrupoService facultadGrupoService;

    public List<ChequeraCuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced, Pageable pageable, ChequeraCuotaService chequeraCuotaService) {
        return repository.findAllByVencimiento1Between(Tool.firstTime(desde), Tool.lastTime(hasta), pageable).stream().peek(cuotaDeuda -> {
            if (cuotaDeuda.getChequeraId() == null) {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(cuotaDeuda.getChequeraCuotaId());
                cuotaDeuda.setChequeraId(chequeraCuota.getChequeraId());
            }
            if (reduced) {
                cuotaDeuda.setChequeraSerie(null);
                cuotaDeuda.setProducto(null);
            }
        }).toList();
    }

    public List<ChequeraCuotaDeuda> findQuinterosByRango(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced, Pageable pageable, ChequeraCuotaService chequeraCuotaService) {
        List<Long> chequeraIds = new ArrayList<>();
        chequeraIds.addAll(chequeraSerieService.findAllByPersona(new BigDecimal(45719365), 1).stream().map(ChequeraSerie::getChequeraId).toList());
        chequeraIds.addAll(chequeraSerieService.findAllByPersona(new BigDecimal(50478688), 1).stream().map(ChequeraSerie::getChequeraId).toList());
        return repository.findAllByVencimiento1BetweenAndChequeraIdIn(Tool.firstTime(desde), Tool.lastTime(hasta), pageable, chequeraIds).stream().peek(cuotaDeuda -> {
            if (cuotaDeuda.getChequeraId() == null) {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(cuotaDeuda.getChequeraCuotaId());
                cuotaDeuda.setChequeraId(chequeraCuota.getChequeraId());
            }
            if (reduced) {
                cuotaDeuda.setChequeraSerie(null);
                cuotaDeuda.setProducto(null);
            }
        }).toList();
    }

    public List<ChequeraCuotaDeuda> findAllByGrupoRango(Integer grupo, OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced, Pageable pageable, ChequeraCuotaService chequeraCuotaService) {
        List<Integer> facultadIds = facultadGrupoService.findAllByGrupo(grupo).stream().map(FacultadGrupo::getFacultadId).toList();
        return repository.findAllByVencimiento1BetweenAndFacultadIdIn(Tool.firstTime(desde), Tool.lastTime(hasta), facultadIds, pageable).stream().peek(cuotaDeuda -> {
            if (cuotaDeuda.getChequeraId() == null) {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(cuotaDeuda.getChequeraCuotaId());
                cuotaDeuda.setChequeraId(chequeraCuota.getChequeraId());
            }
            if (reduced) {
                cuotaDeuda.setChequeraSerie(null);
                cuotaDeuda.setProducto(null);
            }
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
        return repository.findAllByTipoChequeraIdInAndVencimiento1Between(tipoChequeraIds, Tool.firstTime(desde), Tool.lastTime(hasta)).stream().peek(cuotaDeuda -> {
            if (cuotaDeuda.getChequeraId() == null) {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(cuotaDeuda.getChequeraCuotaId());
                cuotaDeuda.setChequeraId(chequeraCuota.getChequeraId());
            }
        }).toList();
    }

}
