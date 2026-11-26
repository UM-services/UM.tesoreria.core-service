package um.tesoreria.core.controller.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import um.tesoreria.core.service.facade.BalanceService;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;
import um.tesoreria.core.service.facade.mayor.MayorConsultaService;
import um.tesoreria.core.service.facade.sumasysaldos.SumasSaldosService;

@WebMvcTest(BalanceController.class)
class BalanceControllerExcelTest {

    private static final String SUMAS = "/api/tesoreria/core/balance/sumasysaldos/2025-12-01T00:00:00Z/2026-02-01T00:00:00Z";
    private static final String MAYOR = "/api/tesoreria/core/balance/mayor/10101010001/2025-12-01T00:00:00Z/2026-02-01T00:00:00Z";
    private static final byte[] LIBRO = {80, 75, 3, 4, 1, 2, 3};

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private BalanceService balanceService;

    @MockitoBean
    private MayorConsultaService mayorConsultaService;

    @MockitoBean
    private SumasSaldosService sumasSaldosService;

    // protects=el Excel de sumas y saldos responde los bytes del libro con el mismo nombre, cabeceras y largo; fails_when=cambia el nombre del adjunto, falta el largo o el cuerpo no es el libro generado; no_claim=no prueba el contenido del libro (lo cubre BalanceServiceExcelTest)
    @Test
    void sumasSaldosRespondeElLibroComoAdjunto() {
        when(balanceService.makeSumasSaldos(any(), any())).thenReturn(LIBRO);

        mockMvc.get().uri(SUMAS).assertThat().hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sumas_y_saldos.xlsx")
                .hasHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .hasHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(LIBRO.length))
                .hasContentType(MediaType.APPLICATION_OCTET_STREAM)
                .body().isEqualTo(LIBRO);
    }

    // protects=el Excel del mayor responde los bytes del libro con nombre mayor.xlsx; fails_when=cambia el nombre del adjunto o el cuerpo no es el libro generado; no_claim=no prueba el contenido del libro
    @Test
    void mayorRespondeElLibroComoAdjunto() {
        when(balanceService.makeMayor(any(BigDecimal.class), any(), any())).thenReturn(LIBRO);

        mockMvc.get().uri(MAYOR).assertThat().hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mayor.xlsx")
                .hasHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(LIBRO.length))
                .body().isEqualTo(LIBRO);
    }

    // protects=un rango que cruza ejercicios en el Excel de sumas y saldos responde 409 con el cuerpo del JSON, por la ruta corta y la larga; fails_when=sale un 500 o un 200 vacío; no_claim=no prueba cuándo un rango cruza ejercicios
    @Test
    void sumasSaldosFueraDeEjercicioResponde409() {
        when(balanceService.makeSumasSaldos(any(), any())).thenThrow(
                new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO, "Desde y hasta deben pertenecer al mismo ejercicio"));

        for (String ruta : new String[] {SUMAS, SUMAS.replace("/api/tesoreria/core", "")}) {
            mockMvc.get().uri(ruta).assertThat().hasStatus(409)
                    .bodyJson().extractingPath("$.codigo").isEqualTo("FUERA_DE_EJERCICIO");
        }
    }

    // protects=un rango que cruza ejercicios en el Excel del mayor responde 409 con codigo y mensaje; fails_when=sale un 500 o un 200 vacío; no_claim=no prueba cuándo un rango cruza ejercicios
    @Test
    void mayorFueraDeEjercicioResponde409() {
        when(balanceService.makeMayor(any(BigDecimal.class), any(), any())).thenThrow(
                new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO, "Desde y hasta deben pertenecer al mismo ejercicio"));

        mockMvc.get().uri(MAYOR).assertThat().hasStatus(409)
                .bodyJson().extractingPath("$.message").isEqualTo("Desde y hasta deben pertenecer al mismo ejercicio");
    }
}
