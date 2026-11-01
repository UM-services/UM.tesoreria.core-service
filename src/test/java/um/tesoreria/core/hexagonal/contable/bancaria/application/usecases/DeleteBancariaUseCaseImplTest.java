package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaUsoPort;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteBancariaUseCaseImplTest {
    @Mock BancariaRepository repository;
    @Mock BancariaUsoPort uso;
    @Mock BancariaHistorialPort historial;
    DeleteBancariaUseCaseImpl useCase;
    Bancaria guardada = Bancaria.builder().bancariaId(15L).banco("Caja").numeroCuenta(BigDecimal.TEN).build();

    @BeforeEach
    void preparar() {
        useCase = new DeleteBancariaUseCaseImpl(repository, uso, historial);
    }

    @Test
    void cuentaLibreSeBorraConFlushYElHistorialLlevaLaFotoLeidaBajoElLock() {
        when(repository.findWithLockByBancariaId(15L)).thenReturn(Optional.of(guardada));
        useCase.deleteBancaria(15L);
        InOrder orden = inOrder(repository, uso, historial);
        orden.verify(repository).findWithLockByBancariaId(15L);
        orden.verify(uso).enUso(15L);
        orden.verify(repository).deleteByBancariaId(15L);
        orden.verify(historial).registrarBaja(guardada);
        verify(repository, never()).findByBancariaId(any());
    }

    @Test
    void cuentaInexistenteEs404() {
        when(repository.findWithLockByBancariaId(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.deleteBancaria(99L)).isInstanceOf(BancariaNoEncontradaException.class);
        verifyNoInteractions(uso, historial);
    }

    @Test
    void cuentaEnUsoEsEnUsoConLosConteosYNoBorraNiRegistra() {
        when(repository.findWithLockByBancariaId(8L)).thenReturn(Optional.of(guardada));
        when(uso.enUso(8L)).thenReturn(true);
        when(uso.contar(8L)).thenReturn(new BancariaUso(3, 69000));
        assertThatThrownBy(() -> useCase.deleteBancaria(8L))
                .isInstanceOfSatisfying(BancariaConflictoException.class, e -> {
                    assertThat(e.getCodigo()).isEqualTo(BancariaConflictoException.Codigo.EN_USO);
                    assertThat(e.getMessage()).contains("3 valores").contains("69000 movimientos");
                });
        verify(repository, never()).deleteByBancariaId(any());
        verifyNoInteractions(historial);
    }
}
