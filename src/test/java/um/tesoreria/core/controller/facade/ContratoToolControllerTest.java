package um.tesoreria.core.controller.facade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.service.facade.ContratoToolService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(ContratoToolController.class)
class ContratoToolControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private ContratoToolService service;

    @Test
    void resetMarcaTemporalOk() {
        when(service.resetMarcaTemporal(5L)).thenReturn(true);

        assertThat(mockMvc.get().uri("/contratotool/reset/marca/temporal/5"))
                .hasStatusOk()
                .bodyText().isEqualTo("true");
    }
}
