package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SanitizeActiveContextsUseCaseImplTest {

    @Mock
    private MercadoPagoContextRepository repository;

    @InjectMocks
    private SanitizeActiveContextsUseCaseImpl useCase;

    @Test
    void conservaElUltimoYDesactivaElResto() {
        var uno = contexto(1L);
        var dos = contexto(2L);
        var tres = contexto(3L);
        when(repository.findChequeraCuotaIdsWithMultipleActiveContexts()).thenReturn(List.of(9L));
        when(repository.findAllByChequeraCuotaIdAndActivo(9L, (byte) 1)).thenReturn(List.of(uno, dos, tres));

        var resultado = useCase.sanitize(false);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().chequeraCuotaId()).isEqualTo(9L);
        assertThat(resultado.getFirst().conservadoId()).isEqualTo(3L);
        assertThat(resultado.getFirst().desactivados()).containsExactly(1L, 2L);
        assertThat(uno.getActivo()).isZero();
        assertThat(dos.getActivo()).isZero();
        assertThat(tres.getActivo()).isEqualTo((byte) 1);
        verify(repository).saveAll(any());
    }

    @Test
    void enDryRunNoEscribe() {
        when(repository.findChequeraCuotaIdsWithMultipleActiveContexts()).thenReturn(List.of(9L));
        when(repository.findAllByChequeraCuotaIdAndActivo(9L, (byte) 1))
                .thenReturn(List.of(contexto(1L), contexto(2L)));

        var resultado = useCase.sanitize(true);

        assertThat(resultado).hasSize(1);
        verify(repository, never()).saveAll(any());
    }

    @Test
    void ignoraGruposQueDejaronDeSerDuplicados() {
        when(repository.findChequeraCuotaIdsWithMultipleActiveContexts()).thenReturn(List.of(9L));
        when(repository.findAllByChequeraCuotaIdAndActivo(eq(9L), eq((byte) 1))).thenReturn(List.of(contexto(1L)));

        assertThat(useCase.sanitize(false)).isEmpty();
        verify(repository, never()).saveAll(any());
    }

    private MercadoPagoContext contexto(Long id) {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(id)
                .chequeraCuotaId(9L)
                .activo((byte) 1)
                .build();
    }

}
