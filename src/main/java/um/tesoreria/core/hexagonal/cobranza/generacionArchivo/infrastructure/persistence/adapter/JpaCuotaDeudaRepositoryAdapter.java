package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaDeudaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CuotaDeuda;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.CuotaDeudaRepository;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.core.model.view.CuotaDeudaPayPerTic;
import um.tesoreria.core.service.view.CuotaDeudaPayPerTicService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Adaptador de las vistas de deuda que usa la generación.
 *
 * <p>Reutiliza los servicios existentes de {@code chequeraCuota} y de PayPerTIC desde la capa de
 * infraestructura; el dominio del slice sólo ve {@link CuotaDeuda}.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaCuotaDeudaRepositoryAdapter implements CuotaDeudaRepository {

    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final CuotaDeudaPayPerTicService cuotaDeudaPayPerTicService;

    @Override
    public List<CuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta) {
        return chequeraCuotaDeudaService.findAllByRango(desde, hasta, false, null, chequeraCuotaService)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<CuotaDeuda> findAllByGrupoRango(Integer grupo, OffsetDateTime desde, OffsetDateTime hasta) {
        return chequeraCuotaDeudaService.findAllByGrupoRango(grupo, desde, hasta, false, null, chequeraCuotaService)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<CuotaDeuda> findAllMacroByRango(OffsetDateTime desde, OffsetDateTime hasta) {
        return chequeraCuotaDeudaService.findAllMacroByRango(desde, hasta, chequeraCuotaService)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<CuotaDeuda> findPptByRango(OffsetDateTime desde, OffsetDateTime hasta) {
        return cuotaDeudaPayPerTicService.findAllByVencimiento1Between(desde, hasta).stream()
                .map(this::toDomain).toList();
    }

    private CuotaDeuda toDomain(ChequeraCuotaDeuda cuota) {
        ChequeraSerieEntity serie = cuota.getChequeraSerie();
        return CuotaDeuda.builder()
                .chequeraCuotaId(cuota.getChequeraCuotaId())
                .chequeraId(cuota.getChequeraId())
                .facultadId(cuota.getFacultadId())
                .tipoChequeraId(cuota.getTipoChequeraId())
                .chequeraSerieId(cuota.getChequeraSerieId())
                .productoId(cuota.getProductoId())
                .alternativaId(cuota.getAlternativaId())
                .cuotaId(cuota.getCuotaId())
                .mes(cuota.getMes())
                .anho(cuota.getAnho())
                .vencimiento1(cuota.getVencimiento1())
                .importe1(cuota.getImporte1())
                .vencimiento2(cuota.getVencimiento2())
                .importe2(cuota.getImporte2())
                .vencimiento3(cuota.getVencimiento3())
                .importe3(cuota.getImporte3())
                .codigoBarras(cuota.getCodigoBarras())
                .pagado(cuota.getPagado())
                .baja(cuota.getBaja())
                .facultadNombre(nombreFacultad(serie))
                .tipoChequeraNombre(nombreTipoChequera(serie))
                .geograficaNombre(nombreGeografica(serie))
                .personaNombre(nombrePersona(serie))
                .productoNombre(nombreProducto(cuota))
                .lectivoNombre(nombreLectivo(serie))
                .build();
    }

    private CuotaDeuda toDomain(CuotaDeudaPayPerTic cuota) {
        return CuotaDeuda.builder()
                .chequeraCuotaId(cuota.getChequeraCuotaId())
                .facultadId(cuota.getFacultadId())
                .tipoChequeraId(cuota.getTipoChequeraId())
                .chequeraSerieId(cuota.getChequeraSerieId())
                .productoId(cuota.getProductoId())
                .alternativaId(cuota.getAlternativaId())
                .cuotaId(cuota.getCuotaId())
                .mes(cuota.getMes())
                .anho(cuota.getAnho())
                .vencimiento1(cuota.getVencimiento1())
                .importe1(cuota.getImporte1())
                .vencimiento2(cuota.getVencimiento2())
                .importe2(cuota.getImporte2())
                .vencimiento3(cuota.getVencimiento3())
                .importe3(cuota.getImporte3())
                .codigoBarras(cuota.getCodigoBarras())
                .pagado(cuota.getPagado())
                .baja(cuota.getBaja())
                .build();
    }

    private String nombreFacultad(ChequeraSerieEntity serie) {
        return serie == null || serie.getFacultad() == null ? null : serie.getFacultad().getNombre();
    }

    private String nombreTipoChequera(ChequeraSerieEntity serie) {
        return serie == null || serie.getTipoChequera() == null ? null : serie.getTipoChequera().getNombre();
    }

    private String nombreGeografica(ChequeraSerieEntity serie) {
        return serie == null || serie.getGeografica() == null ? null : serie.getGeografica().getNombre();
    }

    private String nombreLectivo(ChequeraSerieEntity serie) {
        return serie == null || serie.getLectivo() == null ? null : serie.getLectivo().getNombre();
    }

    private String nombrePersona(ChequeraSerieEntity serie) {
        if (serie == null || serie.getPersona() == null) {
            return null;
        }
        return (Objects.toString(serie.getPersona().getApellido(), "") + ", "
                + Objects.toString(serie.getPersona().getNombre(), "")).trim();
    }

    private String nombreProducto(ChequeraCuotaDeuda cuota) {
        return cuota.getProducto() == null ? null : cuota.getProducto().getNombre();
    }

}
