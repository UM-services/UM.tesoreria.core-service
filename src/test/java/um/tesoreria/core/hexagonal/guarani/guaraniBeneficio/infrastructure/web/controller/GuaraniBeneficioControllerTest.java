package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception.GuaraniBeneficioException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.service.GuaraniBeneficioService;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto.GuaraniBeneficioResponse;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.mapper.GuaraniBeneficioDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(GuaraniBeneficioController.class)
class GuaraniBeneficioControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private GuaraniBeneficioService service;
    @MockitoBean
    private GuaraniBeneficioDtoMapper mapper;

    @Test
    void add_acceptsInclusiveBenefitLimits() throws Exception {
        var domain = GuaraniBeneficio.builder().requisito(10).porcentajeBeneficio(BigDecimal.ZERO).build();
        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(GuaraniBeneficioResponse.builder()
                .requisito(10).porcentajeBeneficio(BigDecimal.ZERO).build());

        mockMvc.post().uri("/api/tesoreria/core/guaraniBeneficio/")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "0"))
                .assertThat().hasStatusOk();
        mockMvc.post().uri("/api/tesoreria/core/guaraniBeneficio/")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "1"))
                .assertThat().hasStatusOk();
    }

    @Test
    void add_rejectsInvalidBenefitConfiguration() throws Exception {
        assertBadRequest(json(10, "-0.01"));
        assertBadRequest(json(10, "1.01"));
        assertBadRequest(json(10, "50"));
        assertBadRequest(json(10, "0.001"));
        assertBadRequest("{\"requisito\":10}");
        assertBadRequest("{\"porcentajeBeneficio\":10}");
    }

    @Test
    void add_translatesDuplicateToConflict() throws Exception {
        when(mapper.toDomain(any())).thenReturn(GuaraniBeneficio.builder().requisito(10)
                .porcentajeBeneficio(new BigDecimal("0.50")).build());
        when(service.create(any())).thenThrow(new GuaraniBeneficioException("duplicado"));

        mockMvc.post().uri("/api/tesoreria/core/guaraniBeneficio/")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "0.50"))
                .assertThat().hasStatus(409);
    }

    @Test
    void update_appliesSameInputValidation() throws Exception {
        mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "1.01"))
                .assertThat().hasStatus(400);
        mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "50"))
                .assertThat().hasStatus(400);
        mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "-0.01"))
                .assertThat().hasStatus(400);
        mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .contentType(MediaType.APPLICATION_JSON).content("{\"requisito\":10}")
                .assertThat().hasStatus(400);
        mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .contentType(MediaType.APPLICATION_JSON).content("{\"porcentajeBeneficio\":10}")
                .assertThat().hasStatus(400);
    }

    @Test
    void update_acceptsInclusiveBenefitLimits() throws Exception {
        var domain = GuaraniBeneficio.builder().requisito(10).porcentajeBeneficio(BigDecimal.ZERO).build();
        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.updateByRequisito(org.mockito.ArgumentMatchers.eq(10), any())).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(GuaraniBeneficioResponse.builder()
                .requisito(10).porcentajeBeneficio(BigDecimal.ZERO).build());

        for (String porcentaje : List.of("0", "0.50", "1")) {
            mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                    .contentType(MediaType.APPLICATION_JSON).content(json(10, porcentaje))
                    .assertThat().hasStatusOk();
        }
    }

    @Test
    void findEndpoints_returnMappedBenefitsAndNotFoundForMissingRequirement() throws Exception {
        var domain = GuaraniBeneficio.builder().requisito(10).porcentajeBeneficio(new BigDecimal("0.50")).build();
        var response = GuaraniBeneficioResponse.builder().requisito(10).porcentajeBeneficio(new BigDecimal("0.50")).build();
        when(service.findAll()).thenReturn(List.of(domain));
        when(service.findByRequisito(10)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/guaraniBeneficio/")
                .assertThat().hasStatusOk();
        mockMvc.get().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .assertThat().hasStatusOk();

        when(service.findByRequisito(99)).thenThrow(new GuaraniBeneficioException("no existe"));
        mockMvc.get().uri("/api/tesoreria/core/guaraniBeneficio/requisito/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void findByRequisitos_returnsOnlyConfiguredBenefits() throws Exception {
        var domain = GuaraniBeneficio.builder().requisito(10).porcentajeBeneficio(new BigDecimal("0.50")).build();
        when(service.findByRequisitos(List.of(10, 99))).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(GuaraniBeneficioResponse.builder()
                .requisito(10).porcentajeBeneficio(new BigDecimal("0.50")).build());

        mockMvc.post().uri("/api/tesoreria/core/guaraniBeneficio/requisitos")
                .contentType(MediaType.APPLICATION_JSON).content("[10,99]")
                .assertThat().hasStatusOk();
    }

    @Test
    void update_mapsAndReturnsUpdatedBenefit() throws Exception {
        var domain = GuaraniBeneficio.builder().requisito(10).porcentajeBeneficio(new BigDecimal("0.50")).build();
        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.updateByRequisito(10, domain)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(GuaraniBeneficioResponse.builder()
                .requisito(10).porcentajeBeneficio(new BigDecimal("0.50")).build());

        mockMvc.put().uri("/api/tesoreria/core/guaraniBeneficio/requisito/10")
                .contentType(MediaType.APPLICATION_JSON).content(json(10, "0.50"))
                .assertThat().hasStatusOk();
    }

    private void assertBadRequest(String payload) {
        mockMvc.post().uri("/api/tesoreria/core/guaraniBeneficio/")
                .contentType(MediaType.APPLICATION_JSON).content(payload)
                .assertThat().hasStatus(400);
    }

    private String json(Integer requisito, String porcentaje) {
        return "{\"requisito\":" + requisito + ",\"porcentajeBeneficio\":" + porcentaje + "}";
    }
}
