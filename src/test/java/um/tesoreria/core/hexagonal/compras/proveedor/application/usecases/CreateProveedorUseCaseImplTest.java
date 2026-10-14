package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorInvalidoException;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProveedorUseCaseImplTest {

    @Mock
    private ProveedorRepository repository;
    @Mock
    private ProveedorHistorialPort historial;

    private CreateProveedorUseCaseImpl useCase() {
        return new CreateProveedorUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
    }

    private static Proveedor.ProveedorBuilder proveedor() {
        return Proveedor.builder().cuit("30-12345678-1").razonSocial("ACME").numeroCuenta(new BigDecimal("2110101"));
    }

    @Test
    void rechazaLaCuentaFaltanteSinTocarNada() {
        assertThatThrownBy(() -> useCase().createProveedor(proveedor().numeroCuenta(null).build()))
                .isInstanceOf(ProveedorInvalidoException.class);

        verifyNoInteractions(repository, historial);
    }

    @Test
    void rechazaElCuitQueYaTieneOtroProveedor() {
        when(repository.existsOtroConCuit("30-12345678-1", null)).thenReturn(true);

        assertThatThrownBy(() -> useCase().createProveedor(proveedor().build()))
                .isInstanceOf(ProveedorConflictoException.class)
                .hasMessageContaining("30-12345678-1");

        verify(repository, never()).create(any());
        verifyNoInteractions(historial);
    }

    @Test
    void creaYRegistraElAltaEnElHistorial() {
        when(repository.existsOtroConCuit("30-12345678-1", null)).thenReturn(false);
        when(repository.create(any())).thenAnswer(inv -> {
            Proveedor p = inv.getArgument(0);
            p.setProveedorId(77);
            return p;
        });

        Proveedor creado = useCase().createProveedor(proveedor().build());

        assertThat(creado.getProveedorId()).isEqualTo(77);
        verify(historial).registrarAlta(creado);
    }

    @Test
    void sinDatosDeEmailInternoNiHabilitadoGuardaVacioYCero() {
        when(repository.create(any())).thenAnswer(inv -> inv.getArgument(0));

        Proveedor creado = useCase().createProveedor(proveedor().build());

        assertThat(creado.getEmailInterno()).isEmpty();
        assertThat(creado.getHabilitado()).isEqualTo((byte) 0);
    }

    @Test
    void elCuitSinDatoSePermiteRepetidoYNoSeControla() {
        when(repository.create(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase().createProveedor(proveedor().cuit("00-00000000-0").build());
        useCase().createProveedor(proveedor().cuit("").build());

        verify(repository, never()).existsOtroConCuit(any(), isNull());
    }

    @Test
    void elFormatoInvalidoDelCuitNoSeRechaza() {
        when(repository.existsOtroConCuit("abc", null)).thenReturn(false);
        when(repository.create(any())).thenAnswer(inv -> inv.getArgument(0));

        Proveedor creado = useCase().createProveedor(proveedor().cuit("abc").build());

        assertThat(creado.getCuit()).isEqualTo("abc");
    }
}