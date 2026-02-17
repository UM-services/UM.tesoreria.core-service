package um.tesoreria.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.model.dto.DeudaChequeraDto;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraSerieService;
import um.tesoreria.core.service.view.ChequeraCuotaDeudaService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChequeraCuotaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChequeraCuotaService service;

    @Mock
    private ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    @Mock
    private ChequeraSerieService chequeraSerieService;

    @Mock
    private CalculateDeudaUseCase calculateDeudaUseCase;

    private ChequeraCuotaController controller;

    @BeforeEach
    void setUp() {
        controller = new ChequeraCuotaController(service, chequeraCuotaDeudaService, chequeraSerieService, calculateDeudaUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void calculateDeuda_shouldCallExtendedUseCase() throws Exception {
        Integer facultadId = 1;
        Integer tipoChequeraId = 2;
        Long chequeraSerieId = 13961L;

        ChequeraSerie chequeraSerie = new ChequeraSerie();
        chequeraSerie.setFacultadId(facultadId);
        chequeraSerie.setTipoChequeraId(tipoChequeraId);
        chequeraSerie.setChequeraSerieId(chequeraSerieId);
        // Set minimal required fields to avoid NPEs if mapper accesses them
        chequeraSerie.setChequeraId(1L);
        chequeraSerie.setPersonaId(BigDecimal.ONE);
        chequeraSerie.setDocumentoId(1);
        chequeraSerie.setAlternativaId(1);
        chequeraSerie.setLectivoId(1);

        when(chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId)).thenReturn(chequeraSerie);
        
        // Mock the return value of calculateDeudaExtended
        DeudaChequeraDto dto = new DeudaChequeraDto(
                BigDecimal.ZERO, 0, facultadId, "Facultad Test", tipoChequeraId, "Tipo Test", chequeraSerieId, 1, "Lectivo Test", 1, BigDecimal.ZERO, BigDecimal.ZERO, 0, 1L, OffsetDateTime.now(), BigDecimal.ZERO
        );
        when(calculateDeudaUseCase.calculateDeudaExtended(any())).thenReturn(dto);

        mockMvc.perform(get("/api/tesoreria/core/chequeraCuota/deuda/{facultadId}/{tipoChequeraId}/{chequeraSerieId}", facultadId, tipoChequeraId, chequeraSerieId))
                .andExpect(status().isOk());

        verify(calculateDeudaUseCase).calculateDeudaExtended(any());
    }
}
