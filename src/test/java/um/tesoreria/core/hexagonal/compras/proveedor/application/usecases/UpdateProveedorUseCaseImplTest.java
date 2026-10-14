package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorInvalidoException;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProveedorUseCaseImplTest {

    @Mock
    private ProveedorRepository repository;
    @Mock
    private ProveedorHistorialPort historial;

    private UpdateProveedorUseCaseImpl useCase() {
        return new UpdateProveedorUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
    }

    private static Proveedor guardado() {
        return Proveedor.builder()
                .proveedorId(5).cuit("30-12345678-1").nombreFantasia("Acme").razonSocial("ACME SA")
                .ordenCheque("Acme").domicilio("Calle 1").telefono("111").fax("222").celular("333")
                .email("a@acme.com").emailInterno("compras@um.edu.ar").numeroCuenta(new BigDecimal("2110101"))
                .habilitado((byte) 1).cbu("0110599520000001234567")
                .build();
    }

    private void proveedorGuardado() {
        when(repository.findByProveedorId(5)).thenAnswer(inv -> Optional.of(guardado()));
        lenient().when(repository.update(eq(5), any())).thenAnswer(inv -> Optional.of(inv.getArgument(1)));
    }

    @Test
    void unPedidoSinCamposConservaTodoLoGuardado() {
        proveedorGuardado();

        useCase().updateProveedor(5, Proveedor.builder().razonSocial("ACME SRL").build());

        ArgumentCaptor<Proveedor> guardadoNuevo = ArgumentCaptor.forClass(Proveedor.class);
        verify(repository).update(eq(5), guardadoNuevo.capture());
        Proveedor p = guardadoNuevo.getValue();
        assertThat(p.getRazonSocial()).isEqualTo("ACME SRL");
        assertThat(p.getHabilitado()).isEqualTo((byte) 1);
        assertThat(p.getEmailInterno()).isEqualTo("compras@um.edu.ar");
        assertThat(p.getNumeroCuenta()).isEqualByComparingTo("2110101");
        assertThat(p.getCuit()).isEqualTo("30-12345678-1");
        assertThat(p.getDomicilio()).isEqualTo("Calle 1");
        assertThat(p.getCbu()).isEqualTo("0110599520000001234567");
    }

    @Test
    void unaCadenaVaciaBorraElCampo() {
        proveedorGuardado();

        useCase().updateProveedor(5, Proveedor.builder().telefono("").emailInterno("").build());

        ArgumentCaptor<Proveedor> captor = ArgumentCaptor.forClass(Proveedor.class);
        verify(repository).update(eq(5), captor.capture());
        assertThat(captor.getValue().getTelefono()).isEmpty();
        assertThat(captor.getValue().getEmailInterno()).isEmpty();
        assertThat(captor.getValue().getFax()).isEqualTo("222");
    }

    @Test
    void habilitadoCeroExplicitoSeGuardaComoCero() {
        proveedorGuardado();

        useCase().updateProveedor(5, Proveedor.builder().habilitado((byte) 0).build());

        ArgumentCaptor<Proveedor> captor = ArgumentCaptor.forClass(Proveedor.class);
        verify(repository).update(eq(5), captor.capture());
        assertThat(captor.getValue().getHabilitado()).isEqualTo((byte) 0);
    }

    @Test
    void sinCambiarElCuitNoControlaDuplicado() {
        proveedorGuardado();

        useCase().updateProveedor(5, Proveedor.builder().cuit("30-12345678-1").razonSocial("Otra").build());

        verify(repository, never()).existsOtroConCuit(any(), any());
    }

    @Test
    void siCambiaElCuitYYaLoTieneOtroProveedorRechazaYNoGuarda() {
        proveedorGuardado();
        when(repository.existsOtroConCuit("30-99999999-9", 5)).thenReturn(true);

        assertThatThrownBy(() -> useCase().updateProveedor(5, Proveedor.builder().cuit("30-99999999-9").build()))
                .isInstanceOf(ProveedorConflictoException.class);

        verify(repository, never()).update(any(), any());
        verifyNoInteractions(historial);
    }

    @Test
    void siCambiaElCuitAUnoLibreGuardaYRegistraLaEdicion() {
        proveedorGuardado();
        when(repository.existsOtroConCuit("30-99999999-9", 5)).thenReturn(false);

        Optional<Proveedor> resultado = useCase().updateProveedor(5, Proveedor.builder().cuit("30-99999999-9").build());

        assertThat(resultado).isPresent();
        ArgumentCaptor<Proveedor> antes = ArgumentCaptor.forClass(Proveedor.class);
        ArgumentCaptor<Proveedor> despues = ArgumentCaptor.forClass(Proveedor.class);
        verify(historial).registrarEdicion(antes.capture(), despues.capture());
        assertThat(antes.getValue().getCuit()).isEqualTo("30-12345678-1");
        assertThat(despues.getValue().getCuit()).isEqualTo("30-99999999-9");
    }

    @Test
    void cambiarElCuitAUnoSinDatoNoControlaDuplicado() {
        proveedorGuardado();

        useCase().updateProveedor(5, Proveedor.builder().cuit("00-00000000-0").build());

        verify(repository, never()).existsOtroConCuit(any(), any());
    }

    @Test
    void rechazaSiElResultadoNoTieneCuenta() {
        when(repository.findByProveedorId(5)).thenAnswer(inv -> Optional.of(
                Proveedor.builder().proveedorId(5).cuit("30-12345678-1").numeroCuenta(null).build()));

        assertThatThrownBy(() -> useCase().updateProveedor(5, Proveedor.builder().razonSocial("X").build()))
                .isInstanceOf(ProveedorInvalidoException.class);

        verify(repository, never()).update(any(), any());
    }

    @Test
    void siElProveedorNoExisteDevuelveVacio() {
        when(repository.findByProveedorId(5)).thenReturn(Optional.empty());

        assertThat(useCase().updateProveedor(5, Proveedor.builder().build())).isEmpty();

        verify(repository, never()).update(any(), any());
        verifyNoInteractions(historial);
    }
}