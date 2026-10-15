package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRolUseCaseImplTest {

    @Mock
    private RolRepository repository;

    @InjectMocks
    private CreateRolUseCaseImpl useCase;

    @Test
    void createRol_savesWhenValid() {
        var rol = Rol.builder().nombre("OPERADOR_CHEQUERAS").build();
        when(repository.save(rol)).thenReturn(rol);

        assertThat(useCase.createRol(rol)).isEqualTo(rol);
        verify(repository).save(rol);
    }

    @Test
    void createRol_whenBlankName_throws() {
        assertThatThrownBy(() -> useCase.createRol(Rol.builder().nombre("  ").build()))
                .isInstanceOf(RolException.class);
        verifyNoInteractions(repository);
    }
}
