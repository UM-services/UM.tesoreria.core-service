package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.exception.AutorizanteEnvioException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out.AutorizanteEnvioRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizanteEnvioUseCasesTest {

    @Mock
    private AutorizanteEnvioRepository repository;

    @Test
    void getDevuelveElConjuntoDelRepositorio() {
        when(repository.findByUsuarioId(9))
                .thenReturn(Optional.of(new AutorizanteEnvio(9, Set.of(1, 2))));
        GetAutorizanteEnvioUseCaseImpl useCase = new GetAutorizanteEnvioUseCaseImpl(repository);

        assertThat(useCase.get(9).dependenciaIds()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void getSinAsignacionesDevuelveConjuntoVacio() {
        when(repository.findByUsuarioId(9)).thenReturn(Optional.empty());
        GetAutorizanteEnvioUseCaseImpl useCase = new GetAutorizanteEnvioUseCaseImpl(repository);

        assertThat(useCase.get(9).dependenciaIds()).isEmpty();
    }

    @Test
    void getExigeUsuario() {
        GetAutorizanteEnvioUseCaseImpl useCase = new GetAutorizanteEnvioUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.get(null))
                .isInstanceOf(AutorizanteEnvioException.class);
    }

    @Test
    void reemplazarGuardaElConjuntoCompleto() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ReemplazarDependenciasAutorizanteUseCaseImpl useCase =
                new ReemplazarDependenciasAutorizanteUseCaseImpl(repository);

        AutorizanteEnvio resultado = useCase.reemplazar(9, List.of(1, 2, 1));

        ArgumentCaptor<AutorizanteEnvio> captor = ArgumentCaptor.forClass(AutorizanteEnvio.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().usuarioId()).isEqualTo(9);
        assertThat(captor.getValue().dependenciaIds()).containsExactlyInAnyOrder(1, 2);
        assertThat(resultado.dependenciaIds()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void reemplazarConListaNulaLimpiaElConjunto() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ReemplazarDependenciasAutorizanteUseCaseImpl useCase =
                new ReemplazarDependenciasAutorizanteUseCaseImpl(repository);

        useCase.reemplazar(9, null);

        ArgumentCaptor<AutorizanteEnvio> captor = ArgumentCaptor.forClass(AutorizanteEnvio.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().dependenciaIds()).isEmpty();
    }

    @Test
    void asignarSumaLaDependenciaAlConjuntoActual() {
        when(repository.findByUsuarioId(9))
                .thenReturn(Optional.of(new AutorizanteEnvio(9, Set.of(1))));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AsignarDependenciaAutorizanteUseCaseImpl useCase =
                new AsignarDependenciaAutorizanteUseCaseImpl(repository);

        AutorizanteEnvio resultado = useCase.asignar(9, 2);

        assertThat(resultado.dependenciaIds()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void asignarSinConjuntoPrevioCreaElPrimero() {
        when(repository.findByUsuarioId(9)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AsignarDependenciaAutorizanteUseCaseImpl useCase =
                new AsignarDependenciaAutorizanteUseCaseImpl(repository);

        assertThat(useCase.asignar(9, 2).dependenciaIds()).containsExactly(2);
    }

    @Test
    void asignarExigeUsuarioYDependencia() {
        AsignarDependenciaAutorizanteUseCaseImpl useCase =
                new AsignarDependenciaAutorizanteUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.asignar(9, null))
                .isInstanceOf(AutorizanteEnvioException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void quitarEliminaLaDependenciaDelConjuntoActual() {
        when(repository.findByUsuarioId(9))
                .thenReturn(Optional.of(new AutorizanteEnvio(9, Set.of(1, 2))));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        QuitarDependenciaAutorizanteUseCaseImpl useCase =
                new QuitarDependenciaAutorizanteUseCaseImpl(repository);

        assertThat(useCase.quitar(9, 1).dependenciaIds()).containsExactly(2);
    }

    @Test
    void listarDelegaEnElRepositorio() {
        when(repository.findAll()).thenReturn(List.of(new AutorizanteEnvio(9, Set.of(1))));
        ListAutorizantesEnvioUseCaseImpl useCase = new ListAutorizantesEnvioUseCaseImpl(repository);

        assertThat(useCase.listar()).hasSize(1);
    }

}
