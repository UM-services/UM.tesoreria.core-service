package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorNombre;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNombresByProveedorIdsUseCaseImplTest {

    @Mock ProveedorRepository repository;

    @Test
    void listaVaciaONulaNoConsultaLaBase() {
        var useCase = new GetNombresByProveedorIdsUseCaseImpl(repository);

        assertThat(useCase.getNombresByProveedorIds(Set.of())).isEmpty();
        assertThat(useCase.getNombresByProveedorIds(null)).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void conIdsDelegaEnElRepositorio() {
        when(repository.findNombresByProveedorIdIn(Set.of(7))).thenReturn(List.of(new ProveedorNombre(7, "Siete", "20-12345678-9")));

        assertThat(new GetNombresByProveedorIdsUseCaseImpl(repository).getNombresByProveedorIds(Set.of(7)))
                .containsExactly(new ProveedorNombre(7, "Siete", "20-12345678-9"));
    }
}
