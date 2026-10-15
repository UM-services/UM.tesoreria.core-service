package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.CreateCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.DeleteCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.GetAllCotizacionTramosUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.GetCotizacionTramoByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.UpdateCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service.CompraReferenciaService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraCotizacionTramoServiceTest {

    @Mock private GetAllCotizacionTramosUseCase getAllCotizacionTramosUseCase;
    @Mock private GetCotizacionTramoByIdUseCase getCotizacionTramoByIdUseCase;
    @Mock private CreateCotizacionTramoUseCase createCotizacionTramoUseCase;
    @Mock private UpdateCotizacionTramoUseCase updateCotizacionTramoUseCase;
    @Mock private DeleteCotizacionTramoUseCase deleteCotizacionTramoUseCase;
    @Mock private CompraReferenciaService compraReferenciaService;

    @InjectMocks
    private CompraCotizacionTramoService service;

    @Test
    void findAllDelega() {
        when(getAllCotizacionTramosUseCase.getAll()).thenReturn(List.of());
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void findByIdDelega() {
        when(getCotizacionTramoByIdUseCase.getById(1L)).thenReturn(Optional.empty());
        assertThat(service.findById(1L)).isEmpty();
    }

    @Test
    void addDelega() {
        CompraCotizacionTramo tramo = CompraCotizacionTramo.builder().cantidadMinima(1).build();
        when(createCotizacionTramoUseCase.create(tramo)).thenReturn(tramo);

        assertThat(service.add(tramo)).isEqualTo(tramo);
    }

    @Test
    void updateFallaSiNoExiste() {
        when(updateCotizacionTramoUseCase.update(any(), eq(1L))).thenReturn(null);

        assertThatThrownBy(() -> service.update(CompraCotizacionTramo.builder().build(), 1L))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void updateDelega() {
        CompraCotizacionTramo tramo = CompraCotizacionTramo.builder().cantidadMinima(1).build();
        when(updateCotizacionTramoUseCase.update(tramo, 1L)).thenReturn(tramo);

        assertThat(service.update(tramo, 1L)).isEqualTo(tramo);
    }

    @Test
    void deleteDelega() {
        service.delete(1L);
        verify(deleteCotizacionTramoUseCase).delete(1L);
    }

    @Test
    void resolverMinimoUsaLaReferenciaYLosTramos() {
        when(compraReferenciaService.getByEjercicioId(15))
                .thenReturn(Optional.of(CompraReferencia.builder().ejercicioId(15).importe(new BigDecimal("100000")).build()));
        when(getAllCotizacionTramosUseCase.getAll()).thenReturn(List.of(
                CompraCotizacionTramo.builder().topeMultiplico(2).cantidadMinima(1).activo((byte) 1).build(),
                CompraCotizacionTramo.builder().topeMultiplico(6).cantidadMinima(2).activo((byte) 1).build(),
                CompraCotizacionTramo.builder().topeMultiplico(null).cantidadMinima(3).activo((byte) 1).build()));

        assertThat(service.resolverMinimo(15, new BigDecimal("150000"))).contains(1);
        assertThat(service.resolverMinimo(15, new BigDecimal("500000"))).contains(2);
        assertThat(service.resolverMinimo(15, new BigDecimal("900000"))).contains(3);
    }

    @Test
    void resolverMinimoSinReferenciaDevuelveVacio() {
        when(compraReferenciaService.getByEjercicioId(15)).thenReturn(Optional.empty());

        assertThat(service.resolverMinimo(15, new BigDecimal("150000"))).isEmpty();
    }

}
