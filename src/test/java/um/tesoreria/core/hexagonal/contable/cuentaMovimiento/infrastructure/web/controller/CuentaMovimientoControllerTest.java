package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.mapper.CuentaMovimientoDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(CuentaMovimientoController.class)
@Import(CuentaMovimientoDtoMapper.class)
class CuentaMovimientoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CuentaMovimientoService service;

    @Test
    void findAllByNumeroCuentaConRutaCanonica() {
        when(service.findAllByNumeroCuenta(any(), eq(true))).thenReturn(List.of());

        mockMvc.get().uri("/api/tesoreria/core/cuentaMovimiento/cuenta/123/true")
                .assertThat().hasStatusOk();

        verify(service).findAllByNumeroCuenta(any(), eq(true));
    }

    @Test
    void findAllByNumeroCuentaConAliasLegacyDeGestion() {
        when(service.findAllByNumeroCuenta(any(), eq(true))).thenReturn(List.of());

        mockMvc.get().uri("/api/tesoreria/core/cuentaMovimiento/123/true")
                .assertThat().hasStatusOk();

        verify(service).findAllByNumeroCuenta(
                org.mockito.ArgumentMatchers.argThat(numero -> numero.compareTo(new BigDecimal("123")) == 0),
                eq(true));
    }
}
