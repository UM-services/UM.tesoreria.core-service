package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraFormalizacionUseCasesTest {

    @Mock private CompraFormalizacionRepository repository;

    @Test
    void createDelegaEnElRepositorio() {
        CompraFormalizacion oc = CompraFormalizacion.builder().numero("OC-2026-000001").build();
        when(repository.save(oc)).thenReturn(oc);

        assertThat(new CreateCompraFormalizacionUseCaseImpl(repository).crear(oc)).isEqualTo(oc);
    }

    @Test
    void getByIdDelega() {
        CompraFormalizacion oc = CompraFormalizacion.builder().compraFormalizacionId(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(oc));

        assertThat(new GetCompraFormalizacionByIdUseCaseImpl(repository).getById(1L)).contains(oc);
    }

    @Test
    void listarDelega() {
        CompraFormalizacionCriteria criteria = new CompraFormalizacionCriteria(15, null, null, null);
        when(repository.findByCriteria(criteria)).thenReturn(List.of(CompraFormalizacion.builder().build()));

        assertThat(new ListCompraFormalizacionesUseCaseImpl(repository).listar(criteria)).hasSize(1);
    }

    @Test
    void crearPasaPorElRepositorioConCualquierInstancia() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new CreateCompraFormalizacionUseCaseImpl(repository)
                .crear(CompraFormalizacion.builder().ejercicioId(15).build()).getEjercicioId()).isEqualTo(15);
    }
}
