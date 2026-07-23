package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.persona.domain.model.DeudaExamen;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.GetDeudaExamenUseCase;
import um.tesoreria.core.hexagonal.setup.application.service.SetupService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class GetDeudaExamenUseCaseImpl implements GetDeudaExamenUseCase {

    private static final Integer PRODUCTO_MATRICULA = 2;
    private static final Integer PRODUCTO_ARANCEL = 3;

    private final ChequeraSerieService chequeraSerieService;
    private final SetupService setupService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final LectivoService lectivoService;

    @Override
    public DeudaExamen getDeudaExamenByFacultadAndPersona(Integer facultadId, BigDecimal personaId, Integer documentoId, OffsetDateTime fechaExamen) {
        var lectivo = lectivoService.findByFecha(OffsetDateTime.now());
        var chequeras = chequeraSerieService.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(personaId, documentoId, facultadId, lectivo.getLectivoId());
        if (chequeras.isEmpty()) {
            return DeudaExamen.builder()
                    .autorizadoRendir(false)
                    .matriculaPagada(false)
                    .cuotasAdeudadas(1000)
                    .build();
        }
        var setup = setupService.findLastOrFail();
        var cuotasAdeudadas = 0;
        var matriculaPagada = true;
        for (var chequera : chequeras) {
            var cuotasNoPagadas = chequeraCuotaService.findAllDebidasByProducto(chequera.getFacultadId(), chequera.getTipoChequeraId(), chequera.getChequeraSerieId(), chequera.getAlternativaId(), PRODUCTO_ARANCEL, fechaExamen);
            cuotasAdeudadas += cuotasNoPagadas.size();
            var matriculasNoPagadas = chequeraCuotaService.findAllDebidasByProducto(chequera.getFacultadId(), chequera.getTipoChequeraId(), chequera.getChequeraSerieId(), chequera.getAlternativaId(), PRODUCTO_MATRICULA, fechaExamen);
            if (!matriculasNoPagadas.isEmpty()) {
                matriculaPagada = false;
            }
        }
        var autorizadoRendir = false;
        if (matriculaPagada) {
            if (cuotasAdeudadas <= setup.getCuotasPermitidas()) {
                autorizadoRendir = true;
            }
        }
        return DeudaExamen.builder()
                .autorizadoRendir(autorizadoRendir)
                .matriculaPagada(matriculaPagada)
                .cuotasAdeudadas(cuotasAdeudadas)
                .build();
    }
}
