package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.CreatePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.DeletePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.GetAllPerfilesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.GetPerfilByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.UpdatePerfilUseCase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraAutoridadPerfilServiceTest {

    @Mock private GetAllPerfilesUseCase getAllPerfilesUseCase;
    @Mock private GetPerfilByIdUseCase getPerfilByIdUseCase;
    @Mock private CreatePerfilUseCase createPerfilUseCase;
    @Mock private UpdatePerfilUseCase updatePerfilUseCase;
    @Mock private DeletePerfilUseCase deletePerfilUseCase;

    @InjectMocks
    private CompraAutoridadPerfilService service;

    @Test
    void findAllDelega() {
        when(getAllPerfilesUseCase.getAll()).thenReturn(List.of());
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void findByIdDelega() {
        when(getPerfilByIdUseCase.getById(1L)).thenReturn(Optional.empty());
        assertThat(service.findById(1L)).isEmpty();
    }

    @Test
    void addDelega() {
        CompraAutoridadPerfil perfil = CompraAutoridadPerfil.builder().nombre("N1").build();
        when(createPerfilUseCase.create(perfil)).thenReturn(perfil);

        assertThat(service.add(perfil)).isEqualTo(perfil);
    }

    @Test
    void updateFallaSiNoExiste() {
        when(updatePerfilUseCase.update(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(null);

        assertThatThrownBy(() -> service.update(CompraAutoridadPerfil.builder().build(), 1L))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void updateDelega() {
        CompraAutoridadPerfil perfil = CompraAutoridadPerfil.builder().nombre("N1").build();
        when(updatePerfilUseCase.update(perfil, 1L)).thenReturn(perfil);

        assertThat(service.update(perfil, 1L)).isEqualTo(perfil);
    }

    @Test
    void deleteDelega() {
        service.delete(1L);
        verify(deletePerfilUseCase).delete(1L);
    }

}
