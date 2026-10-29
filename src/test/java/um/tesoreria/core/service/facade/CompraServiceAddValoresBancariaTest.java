package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.exception.ProveedorValorException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.kotlin.model.Bancaria;
import um.tesoreria.core.kotlin.model.BancoMovimiento;
import um.tesoreria.core.kotlin.model.Valor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.kotlin.model.internal.AsientoInternal;
import um.tesoreria.core.service.BancariaService;
import um.tesoreria.core.service.BancoMovimientoService;
import um.tesoreria.core.service.ProveedorValorService;
import um.tesoreria.core.service.ValorMovimientoService;
import um.tesoreria.core.service.ValorService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Rama de CompraService.addValores que paga con una cuenta bancaria (bancariaIdOrigen): el asiento se imputa a la
 * cuenta contable de la bancaria y el libro banco la guarda. Es el consumidor vivo que #409 protege exigiendo la
 * cuenta contable. Simulación con Mockito, sin base.
 */
@ExtendWith(MockitoExtension.class)
class CompraServiceAddValoresBancariaTest {
    static final BigDecimal CUENTA_BANCARIA = new BigDecimal("11101010001");
    static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-10-23T00:00:00Z");

    @Mock ProveedorMovimientoService proveedorMovimientoService;
    @Mock ProveedorValorService proveedorValorService;
    @Mock ValorMovimientoService valorMovimientoService;
    @Mock BancoMovimientoService bancoMovimientoService;
    @Mock ContabilidadService contabilidadService;
    @Mock ValorService valorService;
    @Mock BancariaService bancariaService;
    @InjectMocks CompraService compra;

    ValorMovimiento valorMovimiento() {
        ValorMovimiento valor = new ValorMovimiento();
        valor.setValorId(3);
        valor.setFechaEmision(FECHA);
        valor.setNumero(77L);
        valor.setImporte(new BigDecimal("1500.00"));
        valor.setBancariaIdOrigen(8L);
        return valor;
    }

    void preparar(Bancaria bancaria) {
        when(proveedorValorService.findLastByProveedorMovimientoId(1L)).thenThrow(new ProveedorValorException(1L));
        when(proveedorMovimientoService.findByProveedorMovimientoId(1L)).thenReturn(ProveedorMovimiento.builder()
                .prefijo(1).numeroComprobante(2L).comprobante(Comprobante.builder().comprobanteId(9).debita((byte) 0).build())
                .build());
        when(contabilidadService.nextAsiento(any(), any())).thenReturn(new AsientoInternal(FECHA, 4, BigDecimal.ZERO, BigDecimal.ZERO));
        Valor valor = new Valor();
        valor.setCategoria("Cheque");
        when(valorService.findByValorId(3)).thenReturn(valor);
        when(bancariaService.findByBancariaId(8L)).thenReturn(bancaria);
    }

    Bancaria bancaria(CuentaEntity cuenta) {
        Bancaria bancaria = new Bancaria();
        bancaria.setBancariaId(8L);
        bancaria.setNumeroCuenta(cuenta == null ? null : cuenta.getNumeroCuenta());
        bancaria.setCuenta(cuenta);
        return bancaria;
    }

    @Test
    void pagarConUnaCuentaBancariaImputaElAsientoASuCuentaContableYRegistraElLibroBanco() {
        preparar(bancaria(CuentaEntity.builder().numeroCuenta(CUENTA_BANCARIA).build()));
        when(valorMovimientoService.add(any())).thenAnswer(i -> {
            ValorMovimiento v = i.getArgument(0);
            v.setValorMovimientoId(50L);
            return v;
        });
        when(bancoMovimientoService.generateNextId(8L, FECHA)).thenReturn(new BancoMovimiento());
        when(bancoMovimientoService.add(any())).thenAnswer(i -> i.getArgument(0));

        compra.addValores(1L, List.of(valorMovimiento()));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CuentaMovimiento>> asiento = ArgumentCaptor.forClass(List.class);
        verify(contabilidadService).saveAsiento(any(), any(), any(), anyString(), asiento.capture());
        assertThat(asiento.getValue()).extracting(CuentaMovimiento::getNumeroCuenta).contains(CUENTA_BANCARIA);
        ArgumentCaptor<BancoMovimiento> libro = ArgumentCaptor.forClass(BancoMovimiento.class);
        verify(bancoMovimientoService).add(libro.capture());
        assertThat(libro.getValue().getNumeroCuenta()).isEqualTo(CUENTA_BANCARIA);
        assertThat(libro.getValue().getTipo()).isEqualTo("Cheque");
    }

    @Test
    void unaCuentaBancariaSinCuentaContableRompeElPagoConNullPointerPorEsoLaApiLaExige() {
        preparar(bancaria(null));
        List<ValorMovimiento> valores = List.of(valorMovimiento());
        assertThatThrownBy(() -> compra.addValores(1L, valores)).isInstanceOfSatisfying(NullPointerException.class,
                e -> assertThat(e.getStackTrace()[0].getClassName()).isEqualTo(CompraService.class.getName()));
    }
}
