package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieSpecialQueriesUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.BajaEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieSpecialQueriesUseCaseImpl implements GetChequeraSerieSpecialQueriesUseCase {

    private final ChequeraSerieRepository repository;
    private final TipoChequeraService tipoChequeraService;
    private final BajaService bajaService;

    @Override
    public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
            Integer tipoChequeraId) {
        return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
                personaId, documentoId, facultadId, lectivoId, geograficaId, tipoChequeraId);
    }

    @Override
    public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                personaId, documentoId, facultadId, lectivoId, geograficaId);
    }

    @Override
    public Optional<ChequeraSerie> findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequera(2).stream()
                .map(TipoChequera::getTipoChequeraId)
                .collect(Collectors.toList());
        return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
                personaId, documentoId, facultadId, lectivoId, geograficaId, tipoChequeraIds);
    }

    @Override
    public Optional<ChequeraSerie> findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequera(1).stream()
                .map(TipoChequera::getTipoChequeraId)
                .collect(Collectors.toList());
        return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
                personaId, documentoId, facultadId, lectivoId, geograficaId, tipoChequeraIds);
    }

    @Override
    public Optional<ChequeraSerie> findLastPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadId(
            BigDecimal personaId, Integer documentoId, Integer facultadId) {
        List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequera(1).stream()
                .map(TipoChequera::getTipoChequeraId)
                .toList();

        List<ChequeraSerie> chequeras = repository.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(
                personaId,
                documentoId,
                facultadId,
                tipoChequeraIds,
                Sort.by("lectivoId").descending().and(Sort.by("chequeraSerieId").descending())
        );

        List<Long> chequeraIds = chequeras.stream().map(ChequeraSerie::getChequeraId).toList();
        List<Long> bajas = bajaService.findAllByChequeraIdIn(chequeraIds).stream()
                .map(BajaEntity::getChequeraId)
                .toList();

        return chequeras.stream()
                .filter(chequera -> !bajas.contains(chequera.getChequeraId()))
                .findFirst();
    }
}
