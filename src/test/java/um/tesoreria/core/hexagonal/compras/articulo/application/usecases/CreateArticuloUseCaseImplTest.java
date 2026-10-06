package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateArticuloUseCaseImplTest {

    private ArticuloRepository repository;
    private CreateArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ArticuloRepository.class);
        useCase = new CreateArticuloUseCaseImpl(repository);
        when(repository.create(any())).thenAnswer(inv -> inv.getArgument(0));
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
