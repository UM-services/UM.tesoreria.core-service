package um.tesoreria.core.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.service.LocalidadService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(LocalidadController.class)
class LocalidadControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private LocalidadService service;

    @Test
    void altaConElBodyQueMandaGestion() {
        when(service.add(any())).thenAnswer(inv -> {
            Localidad localidad = inv.getArgument(0);
            localidad.setUniqueId(1L);
            return localidad;
        });

        mockMvc.post().uri("/api/tesoreria/core/localidad/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"facultadId":10,"provinciaId":5,"localidadId":3,"nombre":"La Plata"}
                        """)
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.localidadId").isEqualTo(3);

        ArgumentCaptor<Localidad> captor = ArgumentCaptor.forClass(Localidad.class);
        verify(service).add(captor.capture());
        assertThat(captor.getValue().getFacultadId()).isEqualTo(10);
        assertThat(captor.getValue().getProvinciaId()).isEqualTo(5);
    }
}
