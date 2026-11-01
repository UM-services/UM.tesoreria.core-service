package um.tesoreria.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.application.service.CursoCargoContratadoService;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.domain.ports.in.GetAllCargosByPersonaUseCase;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.infrastructure.persistence.entity.CursoCargoContratadoEntity;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.infrastructure.web.controller.CursoCargoContratadoController;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.infrastructure.web.mapper.CursoCargoContratadoDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CursoCargoContratadoControllerTest {

    @Mock
    private CursoCargoContratadoService service;

    @Mock
    private GetAllCargosByPersonaUseCase getAllCargosByPersonaUseCase;

    @Mock
    private CursoCargoContratadoDtoMapper cursoCargoContratadoDtoMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CursoCargoContratadoController controller = new CursoCargoContratadoController(service,
                getAllCargosByPersonaUseCase, cursoCargoContratadoDtoMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private CursoCargoContratadoEntity sample() {
        CursoCargoContratadoEntity entity = new CursoCargoContratadoEntity();
        entity.setCursoCargoContratadoId(1L);
        entity.setCursoId(301L);
        entity.setAnho(2026);
        entity.setMes(9);
        entity.setContratoId(7L);
        entity.setPersonaId(new BigDecimal("1001"));
        entity.setDocumentoId(1);
        entity.setCargoTipoId(2);
        entity.setHorasSemanales(new BigDecimal("6.50"));
        return entity;
    }

    @Test
    void findAllByPeriodo_devuelveLosCursosDelPeriodo() throws Exception {
        when(service.findAllByPeriodo(2026, 9)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/core/cursocargocontratado/periodo/2026/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cursoCargoContratadoId").value(1))
                .andExpect(jsonPath("$[0].cursoId").value(301))
                .andExpect(jsonPath("$[0].cargoTipoId").value(2))
                .andExpect(jsonPath("$[0].horasSemanales").value(6.50));
    }
}
