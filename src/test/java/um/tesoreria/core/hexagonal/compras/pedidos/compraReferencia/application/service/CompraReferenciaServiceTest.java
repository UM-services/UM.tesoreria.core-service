package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in.GetReferenciaByEjercicioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in.UpsertReferenciaUseCase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraReferenciaServiceTest {

    @Mock private GetReferenciaByEjercicioUseCase getReferenciaByEjercicioUseCase;
    @Mock private UpsertReferenciaUseCase upsertReferenciaUseCase;

    @InjectMocks
    private CompraReferenciaService service;

    @Test
    void getDelega() {
        CompraReferencia referencia = CompraReferencia.builder().ejercicioId(7).build();
        when(getReferenciaByEjercicioUseCase.getByEjercicioId(7)).thenReturn(Optional.of(referencia));

        assertThat(service.getByEjercicioId(7)).contains(referencia);
    }

    @Test
    void upsertDelega() {
        CompraReferencia referencia = CompraReferencia.builder().ejercicioId(7).build();
        when(upsertReferenciaUseCase.upsert(referencia)).thenReturn(referencia);

        assertThat(service.upsert(referencia)).isEqualTo(referencia);
        verify(upsertReferenciaUseCase).upsert(referencia);
    }

}
