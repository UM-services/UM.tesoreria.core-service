package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DatosBaja;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerieResumen;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByNumberUseCase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Enriquecimiento de la consulta por número: agrega baja y deuda sin que el cliente haga N+1.
 */
@Service
@RequiredArgsConstructor
public class ChequeraSerieResumenService {

    private final GetChequeraSerieByNumberUseCase getChequeraSerieByNumberUseCase;
    private final BajaService bajaService;
    private final ChequeraCuotaService chequeraCuotaService;

    public List<ChequeraSerieResumen> resumen(Integer facultadId, Long chequeraSerieId) {
        return getChequeraSerieByNumberUseCase.getAllByNumber(facultadId, chequeraSerieId).stream()
                .map(this::toResumen).toList();
    }

    private ChequeraSerieResumen toResumen(ChequeraSerie serie) {
        boolean baja = false;
        OffsetDateTime bajaFecha = null;
        var bajaDatos = bajaService.findDatosByUnique(serie.getFacultadId(), serie.getTipoChequeraId(),
                serie.getChequeraSerieId());
        if (bajaDatos.isPresent()) {
            baja = true;
            bajaFecha = bajaDatos.get().fecha();
        }
        List<ChequeraCuota> pendientes = chequeraCuotaService.findAllPendientes(serie.getFacultadId(),
                serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        BigDecimal importeDeuda = pendientes.stream().map(ChequeraCuota::getImporte1)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ChequeraSerieResumen(
                serie.getChequeraId(),
                serie.getFacultadId(),
                serie.getTipoChequeraId(),
                serie.getChequeraSerieId(),
                serie.getPersonaId(),
                serie.getDocumentoId(),
                serie.getLectivoId(),
                serie.getAlternativaId(),
                serie.getFacultad() == null ? null : serie.getFacultad().getNombre(),
                serie.getTipoChequera() == null ? null : serie.getTipoChequera().getNombre(),
                serie.getArancelTipo() == null ? null : serie.getArancelTipo().getDescripcion(),
                serie.getGeografica() == null ? null : serie.getGeografica().getNombre(),
                serie.getLectivo() == null ? null : serie.getLectivo().getNombre(),
                nombrePersona(serie),
                baja,
                bajaFecha,
                pendientes.size(),
                importeDeuda);
    }

    private String nombrePersona(ChequeraSerie serie) {
        if (serie.getPersona() == null) {
            return null;
        }
        return (Objects.toString(serie.getPersona().getApellido(), "") + ", "
                + Objects.toString(serie.getPersona().getNombre(), "")).trim();
    }

}
