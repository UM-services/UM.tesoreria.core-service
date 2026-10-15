package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraAutoridadPerfilUseCasesTest {

    @Mock
    private CompraAutoridadPerfilRepository repository;

    private CompraAutoridadPerfil perfil(Long id, String nombre, Integer multiplico) {
        return CompraAutoridadPerfil.builder().autoridadPerfilId(id).nombre(nombre).multiplico(multiplico).build();
    }

    @Test
    void getAllDelega() {
        when(repository.findAll()).thenReturn(List.of(perfil(1L, "N1", 3)));

        assertThat(new GetAllPerfilesUseCaseImpl(repository).getAll()).hasSize(1);
    }

    @Test
    void getByIdDelega() {
        when(repository.findByAutoridadPerfilId(1L)).thenReturn(Optional.of(perfil(1L, "N1", 3)));

        assertThat(new GetPerfilByIdUseCaseImpl(repository).getById(1L)).isPresent();
    }

    @Test
    void createValidaNombre() {
        CreatePerfilUseCaseImpl useCase = new CreatePerfilUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.create(perfil(null, " ", 3)))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void createValidaMultiplico() {
        CreatePerfilUseCaseImpl useCase = new CreatePerfilUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.create(perfil(null, "N1", 0)))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void createRechazaNombreDuplicado() {
        when(repository.findByNombre("N1")).thenReturn(Optional.of(perfil(1L, "N1", 3)));

        assertThatThrownBy(() -> new CreatePerfilUseCaseImpl(repository).create(perfil(null, "N1", 3)))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void createGuarda() {
        CompraAutoridadPerfil nuevo = perfil(null, "N1", 3);
        when(repository.save(nuevo)).thenReturn(perfil(1L, "N1", 3));

        assertThat(new CreatePerfilUseCaseImpl(repository).create(nuevo).getAutoridadPerfilId()).isEqualTo(1L);
    }

    @Test
    void updateInexistenteDevuelveNull() {
        when(repository.findByAutoridadPerfilId(99L)).thenReturn(Optional.empty());

        assertThat(new UpdatePerfilUseCaseImpl(repository).update(perfil(null, "N1", 3), 99L)).isNull();
    }

    @Test
    void updateAplicaNombreMultiplicoYActivo() {
        CompraAutoridadPerfil actual = perfil(1L, "Viejo", 3);
        when(repository.findByAutoridadPerfilId(1L)).thenReturn(Optional.of(actual));
        when(repository.findByNombre("Nuevo")).thenReturn(Optional.empty());
        when(repository.save(actual)).thenReturn(actual);

        CompraAutoridadPerfil resultado = new UpdatePerfilUseCaseImpl(repository)
                .update(CompraAutoridadPerfil.builder().nombre("Nuevo").multiplico(null).activo((byte) 0).build(), 1L);

        assertThat(resultado.getNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getMultiplico()).isNull();
        assertThat(resultado.getActivo()).isEqualTo((byte) 0);
    }

    @Test
    void updateRechazaNombreDuplicado() {
        when(repository.findByAutoridadPerfilId(1L)).thenReturn(Optional.of(perfil(1L, "N1", 3)));
        when(repository.findByNombre("N2")).thenReturn(Optional.of(perfil(2L, "N2", 6)));

        assertThatThrownBy(() -> new UpdatePerfilUseCaseImpl(repository)
                .update(perfil(null, "N2", 6), 1L))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void updateExigePerfil() {
        assertThatThrownBy(() -> new UpdatePerfilUseCaseImpl(repository).update(null, 1L))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void updateValidaMultiplico() {
        assertThatThrownBy(() -> new UpdatePerfilUseCaseImpl(repository)
                .update(perfil(null, "N1", -1), 1L))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void deleteExigePerfilExistente() {
        assertThatThrownBy(() -> new DeletePerfilUseCaseImpl(repository).delete(99L))
                .isInstanceOf(CompraAutoridadPerfilException.class);
    }

    @Test
    void deleteDelega() {
        when(repository.existsByAutoridadPerfilId(1L)).thenReturn(true);

        new DeletePerfilUseCaseImpl(repository).delete(1L);

        org.mockito.Mockito.verify(repository).deleteByAutoridadPerfilId(1L);
    }

}
