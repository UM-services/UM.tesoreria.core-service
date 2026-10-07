package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.exception.CompraPedidoSecuenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.out.CompraPedidoSecuenciaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservarNumeroCompraPedidoUseCaseImplTest {

    @Mock
    private CompraPedidoSecuenciaRepository repository;

    @Test
    void reservaElSiguienteNumeroDelEjercicio() {
        when(repository.reservarSiguienteNumero(7, 2026)).thenReturn(3);
        ReservarNumeroCompraPedidoUseCaseImpl useCase = new ReservarNumeroCompraPedidoUseCaseImpl(repository);

        assertThat(useCase.reservarNumero(7, 2026)).isEqualTo(3);
    }

    @Test
    void exigeElEjercicio() {
        ReservarNumeroCompraPedidoUseCaseImpl useCase = new ReservarNumeroCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.reservarNumero(null, 2026))
                .isInstanceOf(CompraPedidoSecuenciaException.class);
    }

}
