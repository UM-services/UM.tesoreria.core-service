package um.tesoreria.core.hexagonal.chequera.emision.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.BorradorEmisionService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AlternativaEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorImpresion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ObtenerBorradorImpresionUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ObtenerBorradorImpresionUseCaseImpl implements ObtenerBorradorImpresionUseCase {

    private final BorradorEmisionService borradorEmisionService;
    private final PersonaService personaService;
    private final LectivoService lectivoService;

    @Override
    public BorradorImpresion obtener(Long borradorId) {
        BorradorChequera borrador = borradorEmisionService.obtenerBorrador(borradorId);
        if (borrador.getEstado() != EstadoBorrador.ABIERTO) {
            throw new EmisionException(ReglasEmision.BORRADOR_NO_ABIERTO,
                    "El borrador está " + borrador.getEstado() + ": sólo se imprime como borrador mientras está ABIERTO");
        }

        Persona persona = persona(borrador);
        Lectivo lectivo = lectivo(borrador.getLectivoId());
        String alternativaTitulo = tituloAlternativa(borrador.getAlternativas(), null, borrador.getAlternativaId());
        String arancelDescripcion = nombreArancel(borrador);

        List<BorradorImpresion.Cuota> cuotas = borrador.getCuotas().stream()
                .map(cuota -> cuota(borrador, cuota))
                .toList();

        return BorradorImpresion.builder()
                .borradorId(borrador.getBorradorId())
                .estado(borrador.getEstado().name())
                .personaId(borrador.getPersonaId())
                .documentoId(borrador.getDocumentoId())
                .apellido(persona == null ? null : persona.getApellido())
                .nombre(persona == null ? null : persona.getNombre())
                .facultadId(borrador.getFacultadId())
                .facultadNombre(borrador.getFacultadNombre())
                .tipoChequeraId(borrador.getTipoChequeraId())
                .tipoChequeraNombre(borrador.getTipoChequeraNombre())
                .lectivoId(borrador.getLectivoId())
                .lectivoNombre(lectivo == null ? null : lectivo.getNombre())
                .arancelTipoId(borrador.getArancelTipoId())
                .arancelTipoDescripcion(arancelDescripcion)
                .alternativaId(borrador.getAlternativaId())
                .alternativaTitulo(alternativaTitulo)
                .becaPorcentaje(borrador.getBecaPorcentaje())
                .hpum(borrador.getHpum())
                .fecha(borrador.getFecha())
                .total(total(cuotas))
                .cuotas(cuotas)
                .build();
    }

    private BorradorImpresion.Cuota cuota(BorradorChequera borrador, BorradorCuota cuota) {
        return BorradorImpresion.Cuota.builder()
                .productoId(cuota.getProductoId())
                .productoNombre(tituloAlternativa(borrador.getAlternativas(), cuota.getProductoId(),
                        cuota.getAlternativaId()))
                .alternativaId(cuota.getAlternativaId())
                .cuotaId(cuota.getCuotaId())
                .mes(cuota.getMes())
                .anio(cuota.getAnio())
                .vencimiento1(cuota.getVencimiento1())
                .vencimiento2(cuota.getVencimiento2())
                .vencimiento3(cuota.getVencimiento3())
                .importe1(cuota.getImporte1Borrador())
                .importe2(cuota.getImporte2Borrador())
                .importe3(cuota.getImporte3Borrador())
                .pagado(cuota.getPagado())
                .baja(cuota.getBaja())
                .manual(cuota.getManual())
                .compensada(cuota.getCompensada())
                .build();
    }

    /**
     * Total de lo que se imprime: sólo cuotas adeudadas (ni pagadas, ni de baja, ni compensadas).
     * Es el mismo criterio con el que el sender decide qué cuotas listar.
     */
    private BigDecimal total(List<BorradorImpresion.Cuota> cuotas) {
        return cuotas.stream()
                .filter(cuota -> !BorradorCuota.esVerdadero(cuota.getPagado())
                        && !BorradorCuota.esVerdadero(cuota.getBaja())
                        && !BorradorCuota.esVerdadero(cuota.getCompensada()))
                .map(BorradorImpresion.Cuota::getImporte1)
                .filter(Objects::nonNull)
                .filter(importe -> importe.signum() > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Persona persona(BorradorChequera borrador) {
        try {
            return personaService.findByUnique(borrador.getPersonaId(), borrador.getDocumentoId());
        } catch (RuntimeException e) {
            log.warn("Impresión de borrador {}: sin datos de persona {}/{}", borrador.getBorradorId(),
                    borrador.getPersonaId(), borrador.getDocumentoId());
            return null;
        }
    }

    private Lectivo lectivo(Integer lectivoId) {
        try {
            return lectivoService.findByLectivoId(lectivoId);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String nombreArancel(BorradorChequera borrador) {
        if (borrador.getArancelTipos() == null) {
            return null;
        }
        return borrador.getArancelTipos().stream()
                .filter(tipo -> Objects.equals(tipo.getArancelTipoId(), borrador.getArancelTipoId()))
                .map(tipo -> tipo.getNombre())
                .findFirst()
                .orElse(null);
    }

    private String tituloAlternativa(List<AlternativaEmision> alternativas, Integer productoId, Integer alternativaId) {
        if (alternativas == null || alternativaId == null) {
            return null;
        }
        return alternativas.stream()
                .filter(alternativa -> Objects.equals(alternativa.getAlternativaId(), alternativaId)
                        && (productoId == null || Objects.equals(alternativa.getProductoId(), productoId)))
                .map(AlternativaEmision::getTitulo)
                .findFirst()
                .orElse(null);
    }
}
