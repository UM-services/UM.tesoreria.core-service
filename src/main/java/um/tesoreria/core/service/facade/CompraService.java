package um.tesoreria.core.service.facade;

import um.tesoreria.core.exception.BancoMovimientoException;
import um.tesoreria.core.exception.ProveedorValorException;
import um.tesoreria.core.exception.facade.ContableException;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.internal.AsientoInternal;
import um.tesoreria.core.model.Proveedor;
import um.tesoreria.core.service.*;
import um.tesoreria.core.util.Tool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CompraService {

    @Autowired
    private ProveedorPagoService proveedorPagoService;

    @Autowired
    private ProveedorMovimientoService proveedorMovimientoService;

    @Autowired
    private ProveedorValorService proveedorValorService;

    @Autowired
    private ProveedorArticuloService proveedorArticuloService;

    @Autowired
    private ValorMovimientoService valorMovimientoService;

    @Autowired
    private BancoMovimientoService bancoMovimientoService;

    @Autowired
    private ContableService contableService;

    @Autowired
    private EjercicioService ejercicioService;

    @Autowired
    private ValorService valorService;

    @Autowired
    private BancariaService bancariaService;

    @Transactional
    public void deleteComprobante(Long proveedorMovimientoId) {
        // Eliminar Aplicacion
        for (ProveedorPago proveedorPago : proveedorPagoService.findAllByPago(proveedorMovimientoId)) {
            ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorPago.getProveedorMovimientoIdFactura());
            BigDecimal cancelado = proveedorMovimiento.getCancelado();
            cancelado = cancelado.subtract(proveedorPago.getImporteAplicado()).setScale(2, RoundingMode.HALF_UP);
            proveedorMovimiento.setCancelado(cancelado);
            proveedorMovimientoService.update(proveedorMovimiento, proveedorMovimiento.getProveedorMovimientoId());
            proveedorPagoService.delete(proveedorPago.getProveedorPagoId());
        }
        // Eliminar Valores
        for (ProveedorValor proveedorValor : proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId)) {
            ValorMovimiento valorMovimiento = valorMovimientoService.findByValorMovimientoId(proveedorValor.getValorMovimientoId());
            if (valorMovimiento.getFechaContable() != null) {
                if (!contableService.deleteAsiento(valorMovimiento.getFechaContable(), valorMovimiento.getOrdenContable())) {
                    throw new ContableException(valorMovimiento.getFechaContable(), valorMovimiento.getOrdenContable());
                }
            }
            BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());
            if (bancoMovimiento != null) {
                bancoMovimientoService.deleteByBancoMovimientoId(bancoMovimiento.getBancoMovimientoId());
            }
            proveedorValorService.deleteByProveedorValorId(proveedorValor.getProveedorValorId());
            valorMovimientoService.deleteByValorMovimientoId(valorMovimiento.getValorMovimientoId());
        }
        // Eliminar Artículos
        for (ProveedorArticulo proveedorArticulo : proveedorArticuloService.findAllByProveedorMovimientoId(proveedorMovimientoId, false)) {
            proveedorArticuloService.deleteByProveedorArticuloId(proveedorArticulo.getProveedorArticuloId());
        }
        // Eliminar Asiento
        ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        if (proveedorMovimiento.getFechaContable() != null) {
            if (!contableService.deleteAsiento(proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable())) {
                throw new ContableException(proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable());
            }
        }
        // Eliminar Comprobante
        proveedorMovimientoService.deleteByProveedorMovimientoId(proveedorMovimiento.getProveedorMovimientoId());
    }

    @Transactional
    public void anulateValor(Long valorMovimientoId, OffsetDateTime fechaContableAnulacion) {
        ValorMovimiento valorMovimiento = valorMovimientoService.findByValorMovimientoId(valorMovimientoId);

        OffsetDateTime fechaContable = valorMovimiento.getFechaContable();
        Integer ordenContable = valorMovimiento.getOrdenContable();

        Ejercicio ejercicio = ejercicioService.findByFecha(fechaContable);

        valorMovimiento.setEstado("Anulado");

        // Para el caso que el ejercicio no esté bloqueado
        if (ejercicio.getBloqueado() == 0) {
            valorMovimiento.setFechaContable(null);
            valorMovimiento.setOrdenContable(null);
            valorMovimiento = valorMovimientoService.update(valorMovimiento, valorMovimiento.getValorMovimientoId());

            if (fechaContable != null) {
                contableService.deleteAsiento(fechaContable, ordenContable);
            }

            BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());

            if (bancoMovimiento.getBancoMovimientoId() != null) {
                bancoMovimiento.setAnulado((byte) 1);
                bancoMovimiento.setFechaContable(null);
                bancoMovimiento.setOrdenContable(0);
                bancoMovimiento = bancoMovimientoService.update(bancoMovimiento, bancoMovimiento.getBancoMovimientoId());
            }
        }

        // Para el caso que el ejercicio ya esté bloqueado
        if (ejercicio.getBloqueado() == 1) {
            AsientoInternal asientoInternal = new AsientoInternal(fechaContableAnulacion, 0, BigDecimal.ZERO, BigDecimal.ZERO);
            contableService.contraAsiento(valorMovimiento.getFechaContable(), valorMovimiento.getOrdenContable(), asientoInternal);

            valorMovimiento.setFechaContableAnulacion(asientoInternal.getFechaContable());
            valorMovimiento.setOrdenContableAnulacion(asientoInternal.getOrdenContable());
            valorMovimiento = valorMovimientoService.update(valorMovimiento, valorMovimiento.getValorMovimientoId());

            BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());

            if (bancoMovimiento.getBancoMovimientoId() != null) {
                bancoMovimiento.setAnulado((byte) 1);
                bancoMovimiento = bancoMovimientoService.update(bancoMovimiento, bancoMovimiento.getBancoMovimientoId());
            }
        }
    }

    @Transactional
    public void deleteValor(Long valorMovimientoId) {
        log.debug("Iniciando");
        ValorMovimiento valorMovimiento = valorMovimientoService.findByValorMovimientoId(valorMovimientoId);
        try {
            log.debug("ValorMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valorMovimiento));
        } catch (JsonProcessingException e) {
            log.debug("Sin Valor Movimiento");
        }
        OffsetDateTime fechaContable = valorMovimiento.getFechaContable();
        Integer ordenContable = valorMovimiento.getOrdenContable();

        for (ProveedorValor proveedorValor : proveedorValorService.findAllByValorMovimientoId(valorMovimientoId)) {
            try {
                log.debug("ProveedorValor={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorValor));
            } catch (JsonProcessingException e) {
                log.debug("Sin Proveedor Valor");
            }
            proveedorValorService.deleteByProveedorValorId(proveedorValor.getProveedorValorId());
            log.debug("Eliminado Proveedor Valor");
        }

        try {
            BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimientoId);
            try {
                log.debug("BancoMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(bancoMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("Sin Banco Movimiento");
            }
            bancoMovimientoService.deleteByBancoMovimientoId(bancoMovimiento.getBancoMovimientoId());
            log.debug("BancoMovimiento eliminado");
        } catch (BancoMovimientoException e) {

        }

        valorMovimientoService.deleteByValorMovimientoId(valorMovimientoId);
        log.debug("ValorMovimiento eliminado");

        if (fechaContable != null) {
            contableService.deleteAsiento(fechaContable, ordenContable);
            log.debug("Asiento contable eliminado");
        }

    }

    @Transactional
    public void anulateOPago(Long proveedorMovimientoId) {
        ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);

        proveedorMovimiento.setFechaAnulacion(Tool.dateAbsoluteArgentina());
        proveedorMovimiento = proveedorMovimientoService.update(proveedorMovimiento, proveedorMovimientoId);

        for (ValorMovimiento valorMovimiento : valorMovimientoService.findAllByOrdenPago(proveedorMovimientoId)) {
            anulateValor(valorMovimiento.getValorMovimientoId(), Tool.dateAbsoluteArgentina());
        }
    }

    @Transactional
    public void addValores(Long proveedorMovimientoId, List<ValorMovimiento> valorMovimientos) {

        ProveedorValor proveedorValor = null;
        try {
            proveedorValor = proveedorValorService.findLastByProveedorMovimientoId(proveedorMovimientoId);
        } catch (ProveedorValorException e) {
            proveedorValor = new ProveedorValor();
        }
        try {
            log.debug("ProveedorValor={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorValor));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorValor=JsonException");
        }
        Integer orden = proveedorValor.getOrden();
        ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        try {
            log.debug("ProveedorMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimiento));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorMovimiento=JsonException");
        }
        Comprobante comprobante = proveedorMovimiento.getComprobante();
        String datoProveedor = "";
        Proveedor proveedor = proveedorMovimiento.getProveedor();
        Integer proveedorId = null;
        if (proveedor != null) {
            datoProveedor = proveedor.getRazonSocial() + " (" + proveedor.getCuit() + ")";
            proveedorId = proveedor.getProveedorId();
        }
        String concepto = "Pago " + new DecimalFormat("0000").format(proveedorMovimiento.getPrefijo()) + "-" + new DecimalFormat("00000000").format(proveedorMovimiento.getNumeroComprobante()) + " - " + datoProveedor;

        for (ValorMovimiento valorMovimiento : valorMovimientos) {
            try {
                log.debug("ValorMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valorMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("ValorMovimiento=JsonException");
            }
            // Inicializa asiento
            List<CuentaMovimiento> cuentaMovimientos = new ArrayList<CuentaMovimiento>();
            // Buscar asiento
            AsientoInternal asientoInternal = contableService.nextAsiento(valorMovimiento.getFechaEmision(), null);
            try {
                log.debug("AsientoInternal={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asientoInternal));
            } catch (JsonProcessingException e) {
                log.debug("AsientoInternal=JsonException");
            }
            OffsetDateTime fechaContable = asientoInternal.getFechaContable();
            Integer ordenContable = asientoInternal.getOrdenContable();
            // Imputacion Contable
            BigDecimal numeroCuenta = new BigDecimal(20101090099L);
            if (proveedor != null) {
                numeroCuenta = proveedor.getNumeroCuenta();
            }
            CuentaMovimiento cuentaMovimiento = new CuentaMovimiento(null, fechaContable, ordenContable, 0, numeroCuenta, (byte) (comprobante.getDebita() == 1 ? 1 : 0), comprobante.getComprobanteId(), concepto, valorMovimiento.getImporte().abs(), proveedorId, 0, 0, null, null, (byte) 0, null, null, null, null, null, null, null);
            cuentaMovimientos.add(cuentaMovimiento);
            try {
                log.debug("CuentaMovimientos={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimientos));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimientos=JsonException");
            }

            Valor valor = valorService.findByValorId(valorMovimiento.getValorId());
            try {
                log.debug("Valor={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valor));
            } catch (JsonProcessingException e) {
                log.debug("Valor=JsonException");
            }
            Cuenta cuenta = null;
            if (valorMovimiento.getBancariaIdOrigen() == null) {
                cuenta = valor.getCuenta();
            } else {
                Bancaria bancaria = bancariaService.findByBancariaId(valorMovimiento.getBancariaIdOrigen());
                cuenta = bancaria.getCuenta();
            }
            try {
                log.debug("Cuenta={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valor));
            } catch (JsonProcessingException e) {
                log.debug("Cuenta=JsonException");
            }
            cuentaMovimiento = new CuentaMovimiento(null, fechaContable, ordenContable, 0, cuenta.getNumeroCuenta(), (byte) (comprobante.getDebita() == 1 ? 0 : 1), comprobante.getComprobanteId(), concepto, valorMovimiento.getImporte().abs(), proveedorId, 0, 0, null, null, (byte) 0, null, null, null, null, null, null, null);
            cuentaMovimientos.add(cuentaMovimiento);
            try {
                log.debug("CuentaMovimientos={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimientos));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimientos=JsonException");
            }

            // Guarda asiento por asiento
            contableService.saveAsiento(fechaContable, ordenContable, proveedorMovimientoId, concepto, cuentaMovimientos);

            // Graba valores
            valorMovimiento.setFechaContable(fechaContable);
            valorMovimiento.setOrdenContable(ordenContable);
            valorMovimiento = valorMovimientoService.add(valorMovimiento);
            try {
                log.debug("ValorMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valorMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("ValorMovimiento=JsonException");
            }

            // Graba proveedorValor
            proveedorValor = new ProveedorValor(null, proveedorMovimientoId, ++orden, valorMovimiento.getValorMovimientoId(), null, null);
            proveedorValor = proveedorValorService.add(proveedorValor);
            try {
                log.debug("ProveedorValor={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorValor));
            } catch (JsonProcessingException e) {
                log.debug("ProveedorValor=JsonException");
            }

            // Graba libroBanco
            if (valorMovimiento.getBancariaIdOrigen() != null) {
                Bancaria bancaria = bancariaService.findByBancariaId(valorMovimiento.getBancariaIdOrigen());
                valor = valorService.findByValorId(valorMovimiento.getValorId());

                BancoMovimiento bancoMovimiento = bancoMovimientoService.generateNextId(valorMovimiento.getBancariaIdOrigen(), valorMovimiento.getFechaEmision());
                try {
                    log.debug("BancoMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(bancoMovimiento));
                } catch (JsonProcessingException e) {
                    log.debug("BancoMovimiento=JsonException");
                }
                bancoMovimiento.setTipo(valor.getCategoria());
                if (bancoMovimiento.getTipo().equals("Transferencia")) {
                    bancoMovimiento.setTipo("TransferenciaS");
                }
                bancoMovimiento.setNumeroComprobante(String.valueOf(valorMovimiento.getNumero()));
                bancoMovimiento.setConcepto(concepto);
                bancoMovimiento.setImporte(valorMovimiento.getImporte());
                bancoMovimiento.setNumeroCuenta(bancaria.getNumeroCuenta());
                bancoMovimiento.setFechaContable(fechaContable);
                bancoMovimiento.setOrdenContable(ordenContable);
                bancoMovimiento.setValorMovimientoId(valorMovimiento.getValorMovimientoId());
                bancoMovimiento = bancoMovimientoService.add(bancoMovimiento);
                try {
                    log.debug("BancoMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(bancoMovimiento));
                } catch (JsonProcessingException e) {
                    log.debug("BancoMovimiento=JsonException");
                }
            }
        }
    }

}
