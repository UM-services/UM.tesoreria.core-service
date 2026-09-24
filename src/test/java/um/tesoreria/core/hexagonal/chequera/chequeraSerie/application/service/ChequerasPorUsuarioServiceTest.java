package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaChequeraDto;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.service.UsuarioChequeraFacultadService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChequerasPorUsuarioServiceTest {

    @Mock
    private UsuarioChequeraFacultadService facultadService;
    @Mock
    private ChequeraSerieRepository chequeraRepository;
    @Mock
    private CalculateDeudaUseCase calculateDeudaUseCase;
    @InjectMocks
    private ChequerasPorUsuarioService service;

    @Test
    void withoutAssignedFacultiesReturnsEmptyWithoutReadingChequeras() {
        when(facultadService.findAllByUserId(7L)).thenReturn(List.of());

        var result = service.findAll(7L, 2026, null, null, 0, 20);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(chequeraRepository, never()).findAllByLectivoIdAndFacultadIdIn(any(), any(), any());
    }

    @Test
    void readsOnlyAssignedFacultiesAndCalculatesDebtForReturnedPage() {
        var chequera = ChequeraSerie.builder().chequeraId(10L).facultadId(2).build();
        var debt = new DeudaChequeraDto();
        debt.setDeuda(new BigDecimal("120.50"));
        debt.setCuotas(2);
        when(facultadService.findAllByUserId(7L)).thenReturn(List.of(
                UsuarioChequeraFacultad.builder().facultadId(2).build(),
                UsuarioChequeraFacultad.builder().facultadId(2).build(),
                UsuarioChequeraFacultad.builder().facultadId(3).build()));
        when(chequeraRepository.findAllByLectivoIdAndFacultadIdIn(eq(2026), eq(List.of(2, 3)), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(List.of(chequera), invocation.getArgument(2), 1));
        when(calculateDeudaUseCase.calculateDeuda(chequera)).thenReturn(debt);

        var result = service.findAll(7L, 2026, null, null, 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getImporteDeuda()).isEqualByComparingTo("120.50");
        assertThat(result.getContent().getFirst().getCuotasDeuda()).isEqualTo(2);
        assertThat(result.getPageable().getSort().getOrderFor("chequeraId").getDirection().name()).isEqualTo("DESC");
    }

    @Test
    void studentFilterUsesBothPersonaAndDocumentoWithinAssignedFaculties() {
        var personaId = new BigDecimal("12345678");
        when(facultadService.findAllByUserId(7L)).thenReturn(List.of(
                UsuarioChequeraFacultad.builder().facultadId(2).build()));
        when(chequeraRepository.findAllByLectivoIdAndFacultadIdInAndPersonaIdAndDocumentoId(
                eq(2026), eq(List.of(2)), eq(personaId), eq(4), any(Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = service.findAll(7L, 2026, personaId, 4, 0, 20);

        assertThat(result.getContent()).isEmpty();
        verify(chequeraRepository).findAllByLectivoIdAndFacultadIdInAndPersonaIdAndDocumentoId(
                eq(2026), eq(List.of(2)), eq(personaId), eq(4), any(Pageable.class));
    }

    @Test
    void rejectsPartialStudentIdentityAndOversizedPage() {
        assertThatThrownBy(() -> service.findAll(7L, 2026, BigDecimal.ONE, null, 0, 20))
                .isInstanceOf(ChequerasPorUsuarioService.InvalidQueryException.class);
        assertThatThrownBy(() -> service.findAll(7L, 2026, null, null, 0, 101))
                .isInstanceOf(ChequerasPorUsuarioService.InvalidQueryException.class);
        verify(facultadService, never()).findAllByUserId(any());
    }
}
