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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CompraService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProveedorPagoService proveedorPagoService;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final ProveedorValorService proveedorValorService;
    private final ProveedorArticuloService proveedorArticuloService;
    private final ProveedorArticuloTrackService proveedorArticuloTrackService;
    private final ValorMovimientoService valorMovimientoService;
    private final BancoMovimientoService bancoMovimientoService;
    private final ContabilidadService contabilidadService;
    private final EjercicioService ejercicioService;
    private final ValorService valorService;
    private final BancariaService bancariaService;

    public CompraService(ProveedorPagoService proveedorPagoService,
                         ProveedorMovimientoService proveedorMovimientoService,
                         ProveedorValorService proveedorValorService,
                         ProveedorArticuloService proveedorArticuloService,
                         ProveedorArticuloTrackService proveedorArticuloTrackService,
                         ValorMovimientoService valorMovimientoService,
                         BancoMovimientoService bancoMovimientoService,
                         ContabilidadService contabilidadService,
                         EjercicioService ejercicioService,
                         ValorService valorService,
                         BancariaService bancariaService) {
        this.proveedorPagoService = proveedorPagoService;
        this.proveedorMovimientoService = proveedorMovimientoService;
        this.proveedorValorService = proveedorValorService;
        this.proveedorArticuloService = proveedorArticuloService;
        this.proveedorArticuloTrackService = proveedorArticuloTrackService;
        this.valorMovimientoService = valorMovimientoService;
        this.bancoMovimientoService = bancoMovimientoService;
        this.contabilidadService = contabilidadService;
        this.ejercicioService = ejercicioService;
        this.valorService = valorService;
        this.bancariaService = bancariaService;
    }

    @Transactional
    public void deleteComprobante(Long proveedorMovimientoId) {
        // Collect all provider payments and their IDs in one pass
        List<ProveedorPago> proveedorPagos = proveedorPagoService.findAllByPago(proveedorMovimientoId);
        List<Long> proveedorPagoIds = proveedorPagos.stream()
            .map(ProveedorPago::getProveedorPagoId)
            .toList();

        // Update cancellation amounts for all related movements
        proveedorPagos.forEach(proveedorPago -> {
            logProveedorPago(proveedorPago);
            ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(
                proveedorPago.getProveedorMovimientoIdFactura()
            );
            
            proveedorMovimiento.setCancelado(
                proveedorMovimiento.getCancelado()
                    .subtract(proveedorPago.getImporteAplicado())
                    .setScale(2, RoundingMode.HALF_UP)
            );
            
            proveedorMovimientoService.update(proveedorMovimiento, proveedorMovimiento.getProveedorMovimientoId());
            logProveedorMovimiento(proveedorMovimiento);
        });

        // Bulk delete all provider payments
        log.debug("Deleting all provider payments");
        proveedorPagoService.deleteAllByProveedorPagoIdIn(proveedorPagoIds);

        // Process valor-related deletions
        var proveedorValores = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId);
        var valorMovimientos = proveedorValores.stream()
            .peek(this::logProveedorValor)
            .map(pv -> valorMovimientoService.findByValorMovimientoId(pv.getValorMovimientoId()))
            .peek(vm -> {
                if (vm.getFechaContable() != null) {
                    log.debug("Eliminando Asiento Contable valorMovimiento -> {}/{}", 
                        vm.getFechaContable(), vm.getOrdenContable());
                    if (!contabilidadService.deleteAsiento(vm.getFechaContable(), vm.getOrdenContable())) {
                        throw new ContableException(vm.getFechaContable(), vm.getOrdenContable());
                    }
                }
            })
            .toList();

        // Eliminar Asiento del ProveedorMovimiento antes de los artículos
        ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        if (proveedorMovimiento.getFechaContable() != null) {
            log.debug("Eliminando Asiento Contable proveedorMovimiento -> {}/{}", 
                proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable());
            if (!contabilidadService.deleteAsiento(proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable())) {
                throw new ContableException(proveedorMovimiento.getFechaContable(), proveedorMovimiento.getOrdenContable());
            }
        }

        // Collect IDs for bulk deletion
        var bancoMovimientoIds = valorMovimientos.stream()
            .map(vm -> {
                try {
                    return bancoMovimientoService.findByValorMovimientoId(vm.getValorMovimientoId())
                        .getBancoMovimientoId();
                } catch (BancoMovimientoException e) {
                    log.debug("Sin BancoMovimiento -> {}", e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();

        var proveedorValorIds = proveedorValores.stream()
            .map(ProveedorValor::getProveedorValorId)
            .toList();

        var valorMovimientoIds = valorMovimientos.stream()
            .map(ValorMovimiento::getValorMovimientoId)
            .toList();

        // Bulk delete operations
        bancoMovimientoService.deleteAllByBancoMovimientoIdIn(bancoMovimientoIds);
        proveedorValorService.deleteAllByProveedorValorIdIn(proveedorValorIds);
        valorMovimientoService.deleteAllByValorMovimientoIdIn(valorMovimientoIds);

        // Ahora procesamos los artículos
        var proveedorArticulos = proveedorArticuloService
            .findAllByProveedorMovimientoId(proveedorMovimientoId, false)
            .stream()
            .peek(this::logProveedorArticulo)
            .toList();

        var proveedorArticuloIds = proveedorArticulos.stream()
            .map(ProveedorArticulo::getProveedorArticuloId)
            .toList();

        if (!proveedorArticuloIds.isEmpty()) {
            // Primero desvinculamos las relaciones
            log.debug("Desvinculando relaciones para {} artículos", proveedorArticuloIds.size());
            proveedorArticulos.forEach(proveedorArticulo -> {
                var tracks = proveedorArticuloTrackService.findAllByProveedorArticuloId(proveedorArticulo.getProveedorArticuloId());
                tracks.forEach(track -> {
                    track.setProveedorArticulo(null);
                    track.setProveedorMovimiento(null);
                    proveedorArticuloTrackService.update(track, track.getProveedorArticuloTrackId());
                });
                proveedorArticulo.setArticulo(null);
                proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
            });
            entityManager.flush();

            // Ahora eliminamos los tracks
            log.debug("Eliminando tracks");
            proveedorArticuloTrackService.deleteAllByProveedorArticuloIdIn(proveedorArticuloIds);
            entityManager.flush();

            // Finalmente eliminamos los artículos
            log.debug("Eliminando artículos");
            proveedorArticuloService.deleteAllByProveedorArticuloIdIn(proveedorArticuloIds);
            entityManager.flush();
        }

        // Eliminamos el comprobante
        log.debug("Eliminando comprobante {}", proveedorMovimientoId);
        proveedorMovimientoService.deleteByProveedorMovimientoId(proveedorMovimientoId);
    }

    private void logProveedorArticuloTrack(ProveedorArticuloTrack paTrack) {
        log.debug("Processing logProveedorArticuloTrack");
        try {
            log.debug("ProveedorArticuloTrack -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(paTrack));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorArticuloTrack JSON error -> {}", e.getMessage());
        }
    }

    private void logProveedorArticulo(ProveedorArticulo proveedorArticulo) {
        log.debug("Processing logProveedorArticulo");
        try {
            log.debug("ProveedorArticulo -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorArticulo JSON error -> {}", e.getMessage());
        }

    }

    private void logProveedorValor(ProveedorValor proveedorValor) {
        log.debug("Processing logProveedorValor");
        try {
            log.debug("ProveedorValor -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorValor));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorValor JSON error -> {}", e.getMessage());
        }
    }

    private void logProveedorMovimiento(ProveedorMovimiento proveedorMovimiento) {
        log.debug("Processing logProveedorMovimiento");
        try {
            log.debug("ProveedorMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimiento));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorMovimiento JSON error -> {}", e.getMessage());
        }
    }

    private void logProveedorPago(ProveedorPago proveedorPago) {
        log.debug("Processing logProveedorPago");
        try {
            log.debug("ProveedorPago -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorPago));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorPago JSON error -> {}", e.getMessage());
        }
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
            log.debug("Ejercicio no bloqueado");
            valorMovimiento.setFechaContable(null);
            valorMovimiento.setOrdenContable(null);
            valorMovimiento = valorMovimientoService.update(valorMovimiento, valorMovimiento.getValorMovimientoId());
            try {
                log.debug("... ValorMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valorMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("... sin ValorMovimiento -> {}", e.getMessage());
            }

            if (fechaContable != null) {
                contabilidadService.deleteAsiento(fechaContable, ordenContable);
                log.debug("... asiento eliminado");
            }

            try {
                BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());
                bancoMovimiento.setAnulado((byte) 1);
                bancoMovimiento.setFechaContable(null);
                bancoMovimiento.setOrdenContable(0);
                bancoMovimiento = bancoMovimientoService.update(bancoMovimiento, bancoMovimiento.getBancoMovimientoId());
                try {
                    log.debug("... BancoMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(bancoMovimiento));
                } catch (JsonProcessingException e) {
                    log.debug("... sin BancoMovimiento -> {}", e.getMessage());
                }
            } catch (BancoMovimientoException e) {
                log.debug("... sin BancoMovimiento -> {}", e.getMessage());
            }
        }

        // Para el caso que el ejercicio ya esté bloqueado
        if (ejercicio.getBloqueado() == 1) {
            AsientoInternal asientoInternal = new AsientoInternal(fechaContableAnulacion, 0, BigDecimal.ZERO, BigDecimal.ZERO);
            contabilidadService.contraAsiento(valorMovimiento.getFechaContable(), valorMovimiento.getOrdenContable(), asientoInternal);

            valorMovimiento.setFechaContableAnulacion(asientoInternal.getFechaContable());
            valorMovimiento.setOrdenContableAnulacion(asientoInternal.getOrdenContable());
            valorMovimiento = valorMovimientoService.update(valorMovimiento, valorMovimiento.getValorMovimientoId());

            try {
                BancoMovimiento bancoMovimiento = bancoMovimientoService.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());
                bancoMovimiento.setAnulado((byte) 1);
                bancoMovimiento = bancoMovimientoService.update(bancoMovimiento, bancoMovimiento.getBancoMovimientoId());
                try {
                    log.debug("... BancoMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(bancoMovimiento));
                } catch (JsonProcessingException e) {
                    log.debug("... sin BancoMovimiento -> {}", e.getMessage());
                }
            } catch (BancoMovimientoException e) {
                log.debug("... sin BancoMovimiento -> {}", e.getMessage());
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
            contabilidadService.deleteAsiento(fechaContable, ordenContable);
            log.debug("Asiento contable eliminado");
        }

    }

    @Transactional
    public void anulateOPago(Long proveedorMovimientoId) {
        ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);

        proveedorMovimiento.setFechaAnulacion(Tool.dateAbsoluteArgentina());
        proveedorMovimiento = proveedorMovimientoService.update(proveedorMovimiento, proveedorMovimientoId);
        logProveedorMovimiento(proveedorMovimiento);

        for (ValorMovimiento valorMovimiento : valorMovimientoService.findAllByOrdenPago(proveedorMovimientoId)) {
            try {
                log.debug("anulating ValorMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(valorMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("Sin ValorMovimiento");
            }
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
            AsientoInternal asientoInternal = contabilidadService.nextAsiento(valorMovimiento.getFechaEmision(), null);
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
            contabilidadService.saveAsiento(fechaContable, ordenContable, proveedorMovimientoId, concepto, cuentaMovimientos);

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
