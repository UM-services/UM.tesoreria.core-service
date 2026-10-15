package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreateArticuloUseCaseImplTest {

    private ArticuloRepository repository;
    private GetCuentaByNumeroCuentaUseCase cuentas;
    private RegistrarEscrituraHistorialUseCase historial;
    private CreateArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ArticuloRepository.class);
        cuentas = mock(GetCuentaByNumeroCuentaUseCase.class);
        historial = mock(RegistrarEscrituraHistorialUseCase.class);
        useCase = new CreateArticuloUseCaseImpl(repository, cuentas, historial);
        when(repository.create(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cuentas.getCuentaByNumeroCuenta(any())).thenAnswer(inv -> Optional.of(Cuenta.builder().numeroCuenta(inv.getArgument(0)).build()));
    }

    @Test
    void gastoValido_seGuarda() {
        var articulo = valido().build();

        assertThat(useCase.createArticulo(articulo)).isSameAs(articulo);
        verify(repository).create(articulo);
    }

    @ParameterizedTest
    @ValueSource(strings = {"bien", "gasto"})
    void tiposDelEnum_seAceptan(String tipo) {
        useCase.createArticulo(valido().tipo(tipo).build());
    }

    @Test
    void nombreVacio_yCuentaNula_seAceptan() {
        var articulo = valido().nombre("").numeroCuenta(null).build();

        assertThat(useCase.createArticulo(articulo).getNombre()).isEmpty();
    }

    @Test
    void largosEnElLimite_seAceptan() {
        useCase.createArticulo(valido().nombre("n".repeat(150)).descripcion("d".repeat(64)).unidad("u".repeat(16)).build());
    }

    @Test
    void idEnLosLimites_seAcepta() {
        useCase.createArticulo(valido().articuloId(1L).build());
        useCase.createArticulo(valido().articuloId(2147483647L).build());
    }

    @Test
    void idNuloCeroOFueraDeInt_400() {
        rechaza(valido().articuloId(null).build(), "articuloId");
        rechaza(valido().articuloId(0L).build(), "articuloId");
        rechaza(valido().articuloId(-5L).build(), "articuloId");
        rechaza(valido().articuloId(2147483648L).build(), "articuloId");
    }

    @Test
    void tipoAusenteVacioOFueraDelEnum_400() {
        rechaza(valido().tipo(null).build(), "tipo");
        rechaza(valido().tipo("").build(), "tipo");
        rechaza(valido().tipo("servicio").build(), "tipo");
        rechaza(valido().tipo("GASTO").build(), "tipo");
    }

    @Test
    void banderasFueraDeCeroOUno_400() {
        rechaza(valido().directo((byte) 2).build(), "directo");
        rechaza(valido().habilitado((byte) -1).build(), "habilitado");
    }

    @Test
    void largosExcedidos_400() {
        rechaza(valido().nombre("n".repeat(151)).build(), "nombre");
        rechaza(valido().descripcion("d".repeat(65)).build(), "descripcion");
        rechaza(valido().unidad("u".repeat(17)).build(), "unidad");
    }

    @Test
    void numerosQueEntranEnSusColumnas_seAceptan() {
        useCase.createArticulo(valido().precio(new BigDecimal("99999999999999.99")).numeroCuenta(new BigDecimal("99999999999"))
                .stockMinimo(2147483647L).inventariable((byte) 1).build());
        useCase.createArticulo(valido().precio(new BigDecimal("-0.50")).numeroCuenta(new BigDecimal("5.101E+7"))
                .stockMinimo(-2147483648L).build());
    }

    @Test
    void precioConMasDeCatorceEnteros_400() {
        rechaza(valido().precio(new BigDecimal("1e999999999")).build(), "precio");
        rechaza(valido().precio(new BigDecimal("100000000000000")).build(), "precio");
        // Al redondear pasa a tener 15 dígitos enteros
        rechaza(valido().precio(new BigDecimal("99999999999999.995")).build(), "precio");
    }

    @Test
    void precioConDecimalesDeMas_seRedondeaComoMysql() {
        assertThat(useCase.createArticulo(valido().precio(new BigDecimal("10.333")).build()).getPrecio()).isEqualTo(new BigDecimal("10.33"));
        assertThat(useCase.createArticulo(valido().precio(new BigDecimal("1.235")).build()).getPrecio()).isEqualTo(new BigDecimal("1.24"));
        assertThat(useCase.createArticulo(valido().precio(new BigDecimal("-1.235")).build()).getPrecio()).isEqualTo(new BigDecimal("-1.24"));
        assertThat(useCase.createArticulo(valido().precio(new BigDecimal("1e-999999999")).build()).getPrecio()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void cuentaFueraDeDecimal11_0_400() {
        rechaza(valido().numeroCuenta(new BigDecimal("1e999999999")).build(), "numeroCuenta");
        rechaza(valido().numeroCuenta(new BigDecimal("100000000000")).build(), "numeroCuenta");
        rechaza(valido().numeroCuenta(new BigDecimal("51010101.5")).build(), "numeroCuenta");
    }

    @Test
    void exponentesExtremos_400SinDesbordar() {
        rechaza(valido().precio(new BigDecimal("100e2147483647")).build(), "precio");
        rechaza(valido().numeroCuenta(new BigDecimal("100e2147483647")).build(), "numeroCuenta");
    }

    @Test
    void ceroConEscalaEnorme_seGuardaConLaEscalaDeLaColumna() {
        var guardado = useCase.createArticulo(valido().precio(new BigDecimal("0e-1000000000")).numeroCuenta(new BigDecimal("0e-1000000000")).build());

        assertThat(guardado.getPrecio().scale()).isEqualTo(2);
        assertThat(guardado.getNumeroCuenta().scale()).isZero();
    }

    @Test
    void numerosValidos_seGuardanConLaEscalaDeLaColumna() {
        var guardado = useCase.createArticulo(valido().precio(new BigDecimal("1.5")).numeroCuenta(new BigDecimal("5.101E+7")).build());

        assertThat(guardado.getPrecio()).isEqualTo(new BigDecimal("1.50"));
        assertThat(guardado.getNumeroCuenta()).isEqualTo(new BigDecimal("51010000"));
    }

    @Test
    void inventariableYStockFueraDeColumna_400() {
        rechaza(valido().inventariable((byte) 2).build(), "inventariable");
        rechaza(valido().stockMinimo(2147483648L).build(), "stockMinimo");
        rechaza(valido().stockMinimo(-2147483649L).build(), "stockMinimo");
    }

    @Test
    void cuentaInexistente_400SinEscribir() {
        when(cuentas.getCuentaByNumeroCuenta(new BigDecimal("99999999999"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.createArticulo(valido().numeroCuenta(new BigDecimal("99999999999")).build()))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> {
                    assertThat(ex.getCampo()).isEqualTo("numeroCuenta");
                    assertThat(ex.getMessage()).isEqualTo("La cuenta indicada no existe en el plan de cuentas.");
                });
        verify(repository, never()).create(any());
    }

    @Test
    void cuentaNula_noSeConsulta() {
        useCase.createArticulo(valido().numeroCuenta(null).build());

        verifyNoInteractions(cuentas);
    }

    @Test
    void cuenta_seBuscaConLaEscalaDeLaColumna() {
        useCase.createArticulo(valido().numeroCuenta(new BigDecimal("5.101E+7")).build());

        // Con otra escala, el BigDecimal no sería igual al id de la cuenta
        verify(cuentas).getCuentaByNumeroCuenta(new BigDecimal("51010000"));
    }

    @Test
    void datoMalFormado_400SinConsultarLaCuenta() {
        rechaza(valido().tipo("servicio").numeroCuenta(new BigDecimal("51010000")).build(), "tipo");

        verifyNoInteractions(cuentas);
    }

    @Test
    void alta_registraElEstadoCreadoSinLaCuenta() {
        var articulo = valido().numeroCuenta(new BigDecimal("5.101E+7")).precio(new BigDecimal("1.5")).build();

        useCase.createArticulo(articulo);

        verify(historial).registrarAlta("articulo", "10", new ArticuloEstado(10L, "Gasto", "", "", new BigDecimal("1.50"),
                null, null, new BigDecimal("51010000"), "gasto", (byte) 0, (byte) 1));
    }

    @Test
    void altaRechazada_noRegistra() {
        rechaza(valido().tipo("servicio").build(), "tipo");

        verifyNoInteractions(historial);
    }

    private void rechaza(Articulo articulo, String campo) {
        assertThatThrownBy(() -> useCase.createArticulo(articulo))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo(campo));
        verify(repository, never()).create(articulo);
    }

    private static Articulo.ArticuloBuilder valido() {
        return Articulo.builder().articuloId(10L).nombre("Gasto").descripcion("").unidad("").tipo("gasto")
                .directo((byte) 0).habilitado((byte) 1);
    }
}
