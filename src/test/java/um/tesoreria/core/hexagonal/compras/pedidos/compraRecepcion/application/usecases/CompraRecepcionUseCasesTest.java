package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraRecepcionUseCasesTest {

    @Mock private CompraRecepcionRepository repository;

    @Test
    void crearDelegaEnElRepositorio() {
        CompraRecepcion recepcion = CompraRecepcion.builder().build();
        when(repository.save(recepcion)).thenReturn(recepcion);

        assertThat(new CreateCompraRecepcionUseCaseImpl(repository).crear(recepcion)).isSameAs(recepcion);
    }

    @Test
    void getByIdDelegaEnElRepositorio() {
        CompraRecepcion recepcion = CompraRecepcion.builder().compraRecepcionId(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(recepcion));

        assertThat(new GetCompraRecepcionByIdUseCaseImpl(repository).getById(1L)).contains(recepcion);
    }

    @Test
    void listarDelegaEnElRepositorio() {
        CompraRecepcionCriteria criteria = new CompraRecepcionCriteria(15, null, null, null, null);
        when(repository.findByCriteria(criteria)).thenReturn(List.of(CompraRecepcion.builder().build()));

        assertThat(new ListCompraRecepcionesUseCaseImpl(repository).listar(criteria)).hasSize(1);
        verify(repository).findByCriteria(criteria);
    }

}
