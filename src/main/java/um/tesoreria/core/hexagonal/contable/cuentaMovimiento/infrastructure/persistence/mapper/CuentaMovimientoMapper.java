package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;


@Component
public class CuentaMovimientoMapper {

    public CuentaMovimiento toDomainModel(CuentaMovimientoEntity entity) {
        if (entity == null) return null;
        return CuentaMovimiento.builder()
                .cuentaMovimientoId(entity.getCuentaMovimientoId())
                .fechaContable(entity.getFechaContable())
                .ordenContable(entity.getOrdenContable())
                .item(entity.getItem())
                .numeroCuenta(entity.getNumeroCuenta())
                .debita(entity.getDebita())
                .comprobanteId(entity.getComprobanteId())
                .concepto(entity.getConcepto())
                .importe(entity.getImporte())
                .proveedorId(entity.getProveedorId())
                .numeroAnulado(entity.getNumeroAnulado())
                .version(entity.getVersion())
                .proveedorMovimientoId(entity.getProveedorMovimientoId())
                .proveedorMovimientoIdOrdenPago(entity.getProveedorMovimientoIdOrdenPago())
                .apertura(entity.getApertura())
                .trackId(entity.getTrackId())
                .cuenta(toCuentaDomain(entity.getCuenta()))
                .proveedor(toProveedorDomain(entity.getProveedor()))
                .comprobante(toComprobanteDomain(entity.getComprobante()))
                .proveedorMovimiento(toProveedorMovimientoDomain(entity.getProveedorMovimiento()))
                .ordenPago(toProveedorMovimientoDomain(entity.getOrdenPago()))
                .track(toTrackDomain(entity.getTrack()))
                .build();
    }

    public CuentaMovimientoEntity toEntity(CuentaMovimiento domain) {
        if (domain == null) return null;
        CuentaMovimientoEntity.CuentaMovimientoEntityBuilder builder = CuentaMovimientoEntity.builder()
                .cuentaMovimientoId(domain.getCuentaMovimientoId())
                .fechaContable(domain.getFechaContable())
                .numeroCuenta(domain.getNumeroCuenta())
                .comprobanteId(domain.getComprobanteId())
                .proveedorId(domain.getProveedorId())
                .proveedorMovimientoId(domain.getProveedorMovimientoId())
                .proveedorMovimientoIdOrdenPago(domain.getProveedorMovimientoIdOrdenPago())
                .trackId(domain.getTrackId());

        if (domain.getOrdenContable() != null) builder.ordenContable(domain.getOrdenContable());
        if (domain.getItem() != null) builder.item(domain.getItem());
        if (domain.getDebita() != null) builder.debita(domain.getDebita());
        if (domain.getConcepto() != null) builder.concepto(domain.getConcepto());
        if (domain.getImporte() != null) builder.importe(domain.getImporte());
        if (domain.getNumeroAnulado() != null) builder.numeroAnulado(domain.getNumeroAnulado());
        if (domain.getVersion() != null) builder.version(domain.getVersion());
        if (domain.getApertura() != null) builder.apertura(domain.getApertura());

        return builder.build();
    }

    private Cuenta toCuentaDomain(CuentaEntity entity) {
        if (entity == null) return null;
        return Cuenta.builder()
                .numeroCuenta(entity.getNumeroCuenta())
                .nombre(entity.getNombre())
                .integradora(entity.getIntegradora())
                .grado(entity.getGrado())
                .grado1(entity.getGrado1())
                .grado2(entity.getGrado2())
                .grado3(entity.getGrado3())
                .grado4(entity.getGrado4())
                .geograficaId(entity.getGeograficaId())
                .fechaBloqueo(entity.getFechaBloqueo())
                .visible(entity.getVisible())
                .cuentaContableId(entity.getCuentaContableId())
                .build();
    }

    private Proveedor toProveedorDomain(ProveedorEntity entity) {
        if (entity == null) return null;
        return Proveedor.builder()
                .proveedorId(entity.getProveedorId())
                .cuit(entity.getCuit())
                .nombreFantasia(entity.getNombreFantasia())
                .razonSocial(entity.getRazonSocial())
                .ordenCheque(entity.getOrdenCheque())
                .domicilio(entity.getDomicilio())
                .telefono(entity.getTelefono())
                .fax(entity.getFax())
                .celular(entity.getCelular())
                .email(entity.getEmail())
                .emailInterno(entity.getEmailInterno())
                .numeroCuenta(entity.getNumeroCuenta())
                .habilitado(entity.getHabilitado())
                .cbu(entity.getCbu())
                .cuenta(toCuentaDomain(entity.getCuenta()))
                .build();
    }

    private Comprobante toComprobanteDomain(um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity.ComprobanteEntity entity) {
        if (entity == null) return null;
        return Comprobante.builder()
                .comprobanteId(entity.getComprobanteId())
                .descripcion(entity.getDescripcion())
                .tipoTransaccionId(entity.getTipoTransaccionId())
                .ordenPago(entity.getOrdenPago())
                .aplicaPendiente(entity.getAplicaPendiente())
                .cuentaCorriente(entity.getCuentaCorriente())
                .debita(entity.getDebita())
                .diasVigencia(entity.getDiasVigencia())
                .facturacionElectronica(entity.getFacturacionElectronica())
                .comprobanteAfipId(entity.getComprobanteAfipId())
                .puntoVenta(entity.getPuntoVenta())
                .letraComprobante(entity.getLetraComprobante())
                .build();
    }

    private ProveedorMovimiento toProveedorMovimientoDomain(um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity entity) {
        if (entity == null) return null;
        return ProveedorMovimiento.builder()
                .proveedorMovimientoId(entity.getProveedorMovimientoId())
                .proveedorId(entity.getProveedorId())
                .nombreBeneficiario(entity.getNombreBeneficiario())
                .comprobanteId(entity.getComprobanteId())
                .fechaComprobante(entity.getFechaComprobante())
                .fechaVencimiento(entity.getFechaVencimiento())
                .prefijo(entity.getPrefijo())
                .numeroComprobante(entity.getNumeroComprobante())
                .netoSinDescuento(entity.getNetoSinDescuento())
                .descuento(entity.getDescuento())
                .neto(entity.getNeto())
                .importe(entity.getImporte())
                .cancelado(entity.getCancelado())
                .fechaContable(entity.getFechaContable())
                .ordenContable(entity.getOrdenContable())
                .concepto(entity.getConcepto())
                .fechaAnulacion(entity.getFechaAnulacion())
                .conCargo(entity.getConCargo())
                .solicitaFactura(entity.getSolicitaFactura())
                .geograficaId(entity.getGeograficaId())
                .build();
    }

    private Track toTrackDomain(um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity.TrackEntity entity) {
        if (entity == null) return null;
        return Track.builder()
                .trackId(entity.getTrackId())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
