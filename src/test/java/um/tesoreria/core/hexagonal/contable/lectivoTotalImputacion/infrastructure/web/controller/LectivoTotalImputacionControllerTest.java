package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.service.LectivoTotalImputacionService;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionRequest;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionResponse;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.mapper.LectivoTotalImputacionDtoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(LectivoTotalImputacionController.class)
class LectivoTotalImputacionControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private LectivoTotalImputacionService service;

    @MockitoBean
    private LectivoTotalImputacionDtoMapper mapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAllByLectivo_returnsList() {
        var domain = LectivoTotalImputacion.builder().lectivoTotalImputacionId(1L).build();
        var response = LectivoTotalImputacionResponse.builder().lectivoTotalImputacionId(1L).build();

        when(service.findAllByLectivo(1)).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/lectivototalimputacion/lectivo/1")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$").asArray().hasSize(1);
    }

    @Test
    void findAllByTipo_returnsList() {
        var domain = LectivoTotalImputacion.builder().lectivoTotalImputacionId(1L).build();
        var response = LectivoTotalImputacionResponse.builder().lectivoTotalImputacionId(1L).build();

        when(service.findAllByTipo(1, 2, 3)).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/lectivototalimputacion/tipo/1/2/3")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$").asArray().hasSize(1);
    }

    @Test
    void findByProducto_whenFound_returnsOk() {
        var domain = LectivoTotalImputacion.builder().lectivoTotalImputacionId(1L).build();
        var response = LectivoTotalImputacionResponse.builder().lectivoTotalImputacionId(1L).build();

        when(service.findByProducto(1, 2, 3, 4)).thenReturn(Optional.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/lectivototalimputacion/producto/1/2/3/4")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$.lectivoTotalImputacionId").isEqualTo(1);
    }

    @Test
    void findByProducto_whenNotFound_returnsNotFound() {
        when(service.findByProducto(1, 2, 3, 4)).thenReturn(Optional.empty());

        mockMvc.get().uri("/lectivototalimputacion/producto/1/2/3/4")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatus(404);
    }

    @Test
    void add_returnsOk() throws Exception {
        var request = new LectivoTotalImputacionRequest();
        request.setFacultadId(1);
        request.setLectivoId(2);
        request.setTipoChequeraId(3);
        request.setProductoId(4);
        request.setNumeroCuenta(new BigDecimal("100.50"));

        var domain = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build();

        var response = LectivoTotalImputacionResponse.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build();

        when(mapper.toDomain(request)).thenReturn(domain);
        when(service.add(domain)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.post().uri("/lectivototalimputacion/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$.lectivoTotalImputacionId").isEqualTo(1);
    }

    @Test
    void update_whenFound_returnsOk() throws Exception {
        var request = new LectivoTotalImputacionRequest();
        request.setFacultadId(1);
        request.setLectivoId(2);

        var domain = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(1).lectivoId(2).build();

        var response = LectivoTotalImputacionResponse.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(1).lectivoId(2).build();

        when(mapper.toDomain(request)).thenReturn(domain);
        when(service.update(1L, domain)).thenReturn(Optional.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.put().uri("/lectivototalimputacion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$.lectivoTotalImputacionId").isEqualTo(1);
    }

    @Test
    void update_whenNotFound_returnsNotFound() throws Exception {
        var domain = LectivoTotalImputacion.builder().build();
        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.update(1L, domain)).thenReturn(Optional.empty());

        mockMvc.put().uri("/lectivototalimputacion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LectivoTotalImputacionRequest()))
                .assertThat()
                .hasStatus(404);
    }

}
