package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.service.CompraAutoridadPerfilService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.exception.CompraAutoridadUsuarioException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.LimiteAutorizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.AsignarAutoridadUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.GetPerfilIdsByUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.QuitarAutoridadUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service.CompraReferenciaService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraAutoridadUsuarioServiceTest {

    @Mock private AsignarAutoridadUsuarioUseCase asignarAutoridadUsuarioUseCase;
    @Mock private QuitarAutoridadUsuarioUseCase quitarAutoridadUsuarioUseCase;
    @Mock private GetPerfilIdsByUsuarioUseCase getPerfilIdsByUsuarioUseCase;
    @Mock private CompraAutoridadPerfilService compraAutoridadPerfilService;
    @Mock private CompraReferenciaService compraReferenciaService;

    @InjectMocks
    private CompraAutoridadUsuarioService service;

    private CompraAutoridadPerfil perfil(Long id, Integer multiplico, byte activo) {
        return CompraAutoridadPerfil.builder().autoridadPerfilId(id).nombre("P" + id)
                .multiplico(multiplico).activo(activo).build();
    }

    private CompraReferencia referencia(String importe) {
        return CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal(importe)).build();
    }

    @Test
    void asignarExigeUsuarioYPerfil() {
        assertThatThrownBy(() -> service.asignar(null, 1L)).isInstanceOf(CompraAutoridadUsuarioException.class);
        assertThatThrownBy(() -> service.asignar(9, null)).isInstanceOf(CompraAutoridadUsuarioException.class);
    }

    @Test
    void asignarExigePerfilExistente() {
        when(compraAutoridadPerfilService.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.asignar(9, 5L)).isInstanceOf(CompraAutoridadUsuarioException.class);
    }

    @Test
    void asignarDelega() {
        when(compraAutoridadPerfilService.findById(5L)).thenReturn(Optional.of(perfil(5L, 3, (byte) 1)));

        service.asignar(9, 5L);

        verify(asignarAutoridadUsuarioUseCase).asignar(9, 5L);
    }

    @Test
    void quitarExigeArgumentos() {
        assertThatThrownBy(() -> service.quitar(null, 5L)).isInstanceOf(CompraAutoridadUsuarioException.class);
    }

    @Test
    void quitarDelega() {
        service.quitar(9, 5L);
        verify(quitarAutoridadUsuarioUseCase).quitar(9, 5L);
    }

    @Test
    void getPerfilIdsDelega() {
        when(getPerfilIdsByUsuarioUseCase.getPerfilIds(9)).thenReturn(List.of(5L));
        assertThat(service.getPerfilIds(9)).containsExactly(5L);
    }

    @Test
    void getLimiteExigeUsuarioYEjercicio() {
        assertThatThrownBy(() -> service.getLimite(null, 7)).isInstanceOf(CompraAutoridadUsuarioException.class);
        assertThatThrownBy(() -> service.getLimite(9, null)).isInstanceOf(CompraAutoridadUsuarioException.class);
    }

    @Test
    void getLimiteSinPerfilesDaTieneAutoridadFalso() {
        when(getPerfilIdsByUsuarioUseCase.getPerfilIds(9)).thenReturn(List.of());

        LimiteAutorizacion limite = service.getLimite(9, 7);

        assertThat(limite.tieneAutoridad()).isFalse();
        assertThat(limite.ilimitado()).isFalse();
        assertThat(limite.limite()).isEqualByComparingTo("0");
    }

    @Test
    void getLimiteMultiplicaElMayorMultiploPorLaReferencia() {
        when(getPerfilIdsByUsuarioUseCase.getPerfilIds(9)).thenReturn(List.of(5L, 6L));
        when(compraAutoridadPerfilService.findById(5L)).thenReturn(Optional.of(perfil(5L, 3, (byte) 1)));
        when(compraAutoridadPerfilService.findById(6L)).thenReturn(Optional.of(perfil(6L, 6, (byte) 1)));
        when(compraReferenciaService.getByEjercicioId(7)).thenReturn(Optional.of(referencia("1000.00")));

        LimiteAutorizacion limite = service.getLimite(9, 7);

        assertThat(limite.tieneAutoridad()).isTrue();
        assertThat(limite.multiplico()).isEqualTo(6);
        assertThat(limite.limite()).isEqualByComparingTo("6000.00");
        assertThat(limite.ilimitado()).isFalse();
    }

    @Test
    void getLimiteConPerfilIlimitadoNoCalculaLimite() {
        when(getPerfilIdsByUsuarioUseCase.getPerfilIds(9)).thenReturn(List.of(5L));
        when(compraAutoridadPerfilService.findById(5L)).thenReturn(Optional.of(perfil(5L, null, (byte) 1)));
        when(compraReferenciaService.getByEjercicioId(7)).thenReturn(Optional.of(referencia("1000.00")));

        LimiteAutorizacion limite = service.getLimite(9, 7);

        assertThat(limite.ilimitado()).isTrue();
        assertThat(limite.multiplico()).isNull();
        assertThat(limite.limite()).isNull();
    }

    @Test
    void getLimiteSinReferenciaNoPuedeCalcular() {
        when(getPerfilIdsByUsuarioUseCase.getPerfilIds(9)).thenReturn(List.of(5L));
        when(compraAutoridadPerfilService.findById(5L)).thenReturn(Optional.of(perfil(5L, 3, (byte) 1)));
        when(compraReferenciaService.getByEjercicioId(7)).thenReturn(Optional.empty());

        LimiteAutorizacion limite = service.getLimite(9, 7);

        assertThat(limite.tieneAutoridad()).isTrue();
        assertThat(limite.limite()).isNull();
    }

    @Test
    void getLimiteIgnoraPerfilesInactivos() {
        when(getPerfilIdsByUsuarioUseCase.getPerfilIds(9)).thenReturn(List.of(5L));
        when(compraAutoridadPerfilService.findById(5L)).thenReturn(Optional.of(perfil(5L, 3, (byte) 0)));

        LimiteAutorizacion limite = service.getLimite(9, 7);

        assertThat(limite.tieneAutoridad()).isFalse();
        verifyNoInteractions(compraReferenciaService);
    }

}
