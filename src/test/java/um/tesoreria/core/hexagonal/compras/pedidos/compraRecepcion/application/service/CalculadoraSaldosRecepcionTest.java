package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.service;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraSaldosRecepcionTest {

    private CompraRecepcion recepcion(Long itemId, String cantidad, CompraRecepcionResultado resultado) {
        return CompraRecepcion.builder()
                .items(List.of(CompraRecepcionItem.builder()
                        .compraFormalizacionItemId(itemId)
                        .cantidadRecibida(new BigDecimal(cantidad))
                        .resultado(resultado)
                        .build()))
                .build();
    }

    @Test
    void sumaSoloLasCantidadesQueCubrenElSaldo() {
        var cubierto = CalculadoraSaldosRecepcion.cubiertoPorItem(List.of(
                recepcion(100L, "2.00", CompraRecepcionResultado.CONFORME),
                recepcion(100L, "1.00", CompraRecepcionResultado.CON_OBSERVACIONES),
                recepcion(100L, "9.00", CompraRecepcionResultado.NO_CONFORME),
                recepcion(101L, "4.00", CompraRecepcionResultado.CONFORME)));

        assertThat(cubierto).containsEntry(100L, new BigDecimal("3.00"));
        assertThat(cubierto).containsEntry(101L, new BigDecimal("4.00"));
    }

    @Test
    void ignoraItemsIncompletosYRecepcionesVacias() {
        CompraRecepcion sinItem = CompraRecepcion.builder().build();
        CompraRecepcion itemSinDatos = CompraRecepcion.builder()
                .items(List.of(CompraRecepcionItem.builder().cantidadRecibida(new BigDecimal("1.00")).build(),
                        CompraRecepcionItem.builder().compraFormalizacionItemId(100L)
                                .resultado(CompraRecepcionResultado.CONFORME).build()))
                .build();

        assertThat(CalculadoraSaldosRecepcion.cubiertoPorItem(List.of(sinItem, itemSinDatos))).isEmpty();
        assertThat(CalculadoraSaldosRecepcion.cubiertoPorItem(null)).isEmpty();
    }

    @Test
    void saldoDescuentaElCubiertoYNuncaEsNegativo() {
        assertThat(CalculadoraSaldosRecepcion.saldo(new BigDecimal("3.00"), new BigDecimal("1.00")))
                .isEqualByComparingTo("2.00");
        assertThat(CalculadoraSaldosRecepcion.saldo(new BigDecimal("3.00"), new BigDecimal("5.00")))
                .isEqualByComparingTo("0");
        assertThat(CalculadoraSaldosRecepcion.saldo(new BigDecimal("3.00"), null))
                .isEqualByComparingTo("3.00");
        assertThat(CalculadoraSaldosRecepcion.saldo(null, new BigDecimal("1.00")))
                .isEqualByComparingTo("0");
    }

}
