package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProveedorUseCaseImplTest {

    @Mock
    private ProveedorRepository repository;
    @Mock
    private ProveedorHistorialPort historial;

    private DeleteProveedorUseCaseImpl useCase() {
        return new DeleteProveedorUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
    }

    private static Proveedor proveedor() {
        return Proveedor.builder().proveedorId(5).cuit("30-12345678-1").razonSocial("ACME").build();
    }

    @Test
    void libreRegistraLaBajaYLaBorra() {
        when(repository.findByProveedorId(5)).thenReturn(Optional.of(proveedor()));
        when(repository.deleteById(5)).thenReturn(true);

        assertThat(useCase().deleteProveedor(5)).isTrue();

        ArgumentCaptor<Proveedor> anterior = ArgumentCaptor.forClass(Proveedor.class);
        InOrder orden = inOrder(historial, repository);
        orden.verify(historial).registrarBaja(anterior.capture());
        orden.verify(repository).deleteById(5);
        assertThat(anterior.getValue().getRazonSocial()).isEqualTo("ACME");
    }

    @Test
    void siLaBaseFrenaElBorradoPorRegistrosAsociadosPropagaElConflicto() {
        when(repository.findByProveedorId(5)).thenReturn(Optional.of(proveedor()));
        when(repository.deleteById(5)).thenThrow(new ProveedorConflictoException("tiene registros asociados"));

        assertThatThrownBy(() -> useCase().deleteProveedor(5))
                .isInstanceOf(ProveedorConflictoException.class)
                .hasMessageContaining("registros asociados");
    }

    @Test
    void siNoExisteDevuelveFalsoSinTocarNada() {
        when(repository.findByProveedorId(5)).thenReturn(Optional.empty());

        assertThat(useCase().deleteProveedor(5)).isFalse();

        verify(repository, never()).deleteById(5);
        verifyNoInteractions(historial);
    }
}