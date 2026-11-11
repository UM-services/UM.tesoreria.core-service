package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity.AutorizanteEnvioDependenciaEntity;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.mapper.AutorizanteEnvioMapper;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.repository.JpaAutorizanteEnvioDependenciaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaAutorizanteEnvioRepositoryAdapterTest {

    @Mock
    private JpaAutorizanteEnvioDependenciaRepository repository;

    private JpaAutorizanteEnvioRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaAutorizanteEnvioRepositoryAdapter(repository, new AutorizanteEnvioMapper());
    }

    @Test
    void findByUsuarioIdSinFilasDevuelveVacio() {
        when(repository.findAllByUsuarioIdOrderByDependenciaIdAsc(9)).thenReturn(List.of());

        assertThat(adapter.findByUsuarioId(9)).isEmpty();
    }

    @Test
    void findByUsuarioIdArmaElAgregado() {
        when(repository.findAllByUsuarioIdOrderByDependenciaIdAsc(9)).thenReturn(List.of(
                new AutorizanteEnvioDependenciaEntity(9, 1),
                new AutorizanteEnvioDependenciaEntity(9, 2)));

        Optional<AutorizanteEnvio> autorizante = adapter.findByUsuarioId(9);

        assertThat(autorizante).isPresent();
        assertThat(autorizante.get().dependenciaIds()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void findAllAgrupaPorUsuario() {
        when(repository.findAllByOrderByUsuarioIdAscDependenciaIdAsc()).thenReturn(List.of(
                new AutorizanteEnvioDependenciaEntity(9, 1),
                new AutorizanteEnvioDependenciaEntity(9, 2),
                new AutorizanteEnvioDependenciaEntity(10, 3)));

        List<AutorizanteEnvio> autorizantes = adapter.findAll();

        assertThat(autorizantes).hasSize(2);
        assertThat(autorizantes.get(0).usuarioId()).isEqualTo(9);
        assertThat(autorizantes.get(0).dependenciaIds()).containsExactlyInAnyOrder(1, 2);
        assertThat(autorizantes.get(1).usuarioId()).isEqualTo(10);
    }

    @Test
    void saveAplicaElDiffDeAltasYBajas() {
        when(repository.findAllByUsuarioIdOrderByDependenciaIdAsc(9)).thenReturn(List.of(
                new AutorizanteEnvioDependenciaEntity(9, 1),
                new AutorizanteEnvioDependenciaEntity(9, 2)));
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(new AutorizanteEnvio(9, Set.of(2, 3)));

        verify(repository).deleteByUsuarioIdAndDependenciaId(9, 1);
        verify(repository, never()).deleteByUsuarioIdAndDependenciaId(9, 2);
        ArgumentCaptor<List<AutorizanteEnvioDependenciaEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(AutorizanteEnvioDependenciaEntity::getDependenciaId)
                .containsExactly(3);
    }

    @Test
    void saveSinCambiosNoEscribe() {
        when(repository.findAllByUsuarioIdOrderByDependenciaIdAsc(9)).thenReturn(List.of(
                new AutorizanteEnvioDependenciaEntity(9, 1)));

        adapter.save(new AutorizanteEnvio(9, Set.of(1)));

        verify(repository, never()).deleteByUsuarioIdAndDependenciaId(9, 1);
        verify(repository, never()).saveAll(any());
    }

    @Test
    void existeDelegaEnElRepositorio() {
        when(repository.existsByUsuarioIdAndDependenciaId(9, 2)).thenReturn(true);

        assertThat(adapter.existe(9, 2)).isTrue();
    }
}
