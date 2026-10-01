package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CuotaEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.DebitoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.ProductoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.EstadoChequeraResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.CuotaEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.DebitoEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.ProductoEstadoResponse;

@Component
public class EstadoChequeraDtoMapper {

    public EstadoChequeraResponse toResponse(EstadoChequera estado) {
        if (estado == null) return null;
        return new EstadoChequeraResponse(
                estado.facultadId(),
                estado.facultadNombre(),
                estado.tipoChequeraId(),
                estado.tipoChequeraNombre(),
                estado.chequeraSerieId(),
                estado.personaId(),
                estado.personaApellido(),
                estado.personaNombre(),
                estado.arancelTipoDescripcion(),
                estado.lectivoNombre(),
                estado.becaPorcentaje(),
                estado.tipoImpresionNombre(),
                estado.alternativaId(),
                estado.hpum(),
                estado.productos().stream().map(this::toResponse).toList(),
                estado.debitos().stream().map(this::toResponse).toList());
    }

    private ProductoEstadoResponse toResponse(ProductoEstado producto) {
        return new ProductoEstadoResponse(
                producto.productoId(),
                producto.nombre(),
                producto.tituloCuota(),
                producto.totalCuotas(),
                producto.total(),
                producto.pagado(),
                producto.cuotas().stream().map(this::toResponse).toList());
    }

    private CuotaEstadoResponse toResponse(CuotaEstado cuota) {
        return new CuotaEstadoResponse(
                cuota.cuotaId(),
                cuota.mes(),
                cuota.anho(),
                cuota.primerVencimiento(),
                cuota.importe(),
                cuota.ordenPago(),
                cuota.fechaPago(),
                cuota.importePagado(),
                cuota.referenciaPago());
    }

    private DebitoEstadoResponse toResponse(DebitoEstado debito) {
        return new DebitoEstadoResponse(
                debito.cuotaId(),
                debito.importe(),
                debito.fechaVencimiento(),
                debito.cbu(),
                debito.tipoDebito(),
                debito.fechaEnvio(),
                debito.rechazado(),
                debito.motivoRechazo());
    }
}