package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.BorradorConsultaService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ListarBorradoresSedeUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ResumenBorradoresLectivoUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorListadoItemResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorListadoRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorListadoResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper.EmisionDtoMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(BorradorConsultaController.class)
@Import(EmisionDtoMapper.class)
class BorradorConsultaControllerTest {

    @Autowired
    MockMvcTester mvc;
    @MockitoBean
    ResumenBorradoresLectivoUseCase resumen;
    @MockitoBean
    ListarBorradoresSedeUseCase listar;
    @MockitoBean
    BorradorConsultaService consulta;

    @Test
    void resumenDevuelveFacultadSedeYCantidad() {
        when(resumen.resumenLectivo(38)).thenReturn(List.of(
                new FacultadSedeBorradorResponse(14, "Facultad de Ciencias Médicas", 1, "Mendoza", 2L)));

        var respuesta = mvc.get().uri("/api/tesoreria/core/emision/chequera/borradores/resumen/lectivo/38")
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();

        respuesta.extractingPath("$[0].facultadId").isEqualTo(14);
        respuesta.extractingPath("$[0].facultad").isEqualTo("Facultad de Ciencias Médicas");
        respuesta.extractingPath("$[0].geograficaId").isEqualTo(1);
        respuesta.extractingPath("$[0].geografica").isEqualTo("Mendoza");
        respuesta.extractingPath("$[0].cantidad").isEqualTo(2);
    }

    @Test
    void listarPorSedeDevuelveLosBorradoresEnriquecidos() {
        BorradorChequera borrador = BorradorChequera.builder()
                .borradorId(7L)
                .estado(EstadoBorrador.ABIERTO)
                .personaId(new BigDecimal("1234567"))
                .documentoId(1)
                .facultadId(14)
                .facultadNombre("Facultad de Ciencias Médicas")
                .personaApellido("Pérez")
                .personaNombre("Ana")
                .lectivoId(38)
                .tipoChequeraId(1)
                .tipoChequeraNombre("Chequera")
                .geograficaId(1)
                .geograficaNombre("Mendoza")
                .build();
        when(listar.listar(14, 38, 1)).thenReturn(List.of(borrador));

        var respuesta = mvc.get().uri(
                        "/api/tesoreria/core/emision/chequera/borradores/sede/facultad/14/lectivo/38/geografica/1")
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();

        respuesta.extractingPath("$[0].borradorId").isEqualTo(7);
        respuesta.extractingPath("$[0].estado").isEqualTo("ABIERTO");
        respuesta.extractingPath("$[0].personaApellido").isEqualTo("Pérez");
        respuesta.extractingPath("$[0].geograficaNombre").isEqualTo("Mendoza");
        respuesta.extractingPath("$[0].cuotas").asArray().isEmpty();
    }

    @Test
    void listadoDevuelveLaPaginaLivianaDeBorradores() {
        when(consulta.listar(any(BorradorListadoRequest.class))).thenReturn(BorradorListadoResponse.builder()
                .items(List.of(BorradorListadoItemResponse.builder()
                        .borradorId(9L)
                        .estado("ABIERTO")
                        .personaApellido("Gómez")
                        .personaNombre("Luis")
                        .facultadId(14)
                        .facultadNombre("Facultad de Ciencias Médicas")
                        .geograficaId(1)
                        .geograficaNombre("Mendoza")
                        .totalBorrador(new BigDecimal("2100.00"))
                        .usuarioId("42")
                        .usuarioNombre("Pérez, Ana")
                        .updatedAt(OffsetDateTime.parse("2026-11-18T10:00:00Z"))
                        .build()))
                .total(1)
                .page(0)
                .size(20)
                .build());

        var respuesta = mvc.post().uri("/api/tesoreria/core/emision/chequera/borradores/listado")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""
                        {"lectivoId":38,"facultadId":14,"geograficaId":1,"page":0,"size":20}
                        """)
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();

        respuesta.extractingPath("$.total").isEqualTo(1);
        respuesta.extractingPath("$.items[0].borradorId").isEqualTo(9);
        respuesta.extractingPath("$.items[0].usuarioNombre").isEqualTo("Pérez, Ana");
    }

    @Test
    void countDevuelveLaCantidadDeBorradoresAbiertosDeLaSede() {
        when(consulta.contar(14, 38, 1)).thenReturn(3L);

        var respuesta = mvc.get().uri(
                        "/api/tesoreria/core/emision/chequera/borradores/count/facultad/14/lectivo/38/geografica/1")
                .assertThat().hasStatus(HttpStatus.OK).bodyText();

        respuesta.isEqualTo("3");
    }
}
