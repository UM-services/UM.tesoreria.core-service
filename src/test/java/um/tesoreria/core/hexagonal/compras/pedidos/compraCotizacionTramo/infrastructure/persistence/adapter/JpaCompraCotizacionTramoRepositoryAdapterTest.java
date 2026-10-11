package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.entity.CompraCotizacionTramoEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.mapper.CompraCotizacionTramoMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.repository.JpaCompraCotizacionTramoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraCotizacionTramoRepositoryAdapterTest {

    @Mock private JpaCompraCotizacionTramoRepository repository;
    @Mock private CompraCotizacionTramoMapper mapper;

    @InjectMocks
    private JpaCompraCotizacionTramoRepositoryAdapter adapter;

    @Test
    void findByIdMapea() {
        CompraCotizacionTramoEntity entity = CompraCotizacionTramoEntity.builder().cotizacionTramoId(1L).build();
        CompraCotizacionTramo domain = CompraCotizacionTramo.builder().cotizacionTramoId(1L).build();
        when(repository.findByCotizacionTramoId(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.findById(1L)).contains(domain);
    }

    @Test
    void findAllOrdenaTopeAscendenteYAbiertoAlFinal() {
        CompraCotizacionTramo abierto = CompraCotizacionTramo.builder().cotizacionTramoId(3L).topeMultiplico(null).cantidadMinima(3).build();
        CompraCotizacionTramo alto = CompraCotizacionTramo.builder().cotizacionTramoId(2L).topeMultiplico(6).cantidadMinima(2).build();
        CompraCotizacionTramo bajo = CompraCotizacionTramo.builder().cotizacionTramoId(1L).topeMultiplico(2).cantidadMinima(1).build();
        when(repository.findAll()).thenReturn(List.of(
                CompraCotizacionTramoEntity.builder().cotizacionTramoId(3L).build(),
                CompraCotizacionTramoEntity.builder().cotizacionTramoId(2L).build(),
                CompraCotizacionTramoEntity.builder().cotizacionTramoId(1L).build()));
        when(mapper.toDomain(any())).thenReturn(abierto, alto, bajo);

        List<CompraCotizacionTramo> resultado = adapter.findAll();

        assertThat(resultado).extracting(CompraCotizacionTramo::getCotizacionTramoId).containsExactly(1L, 2L, 3L);
    }

    @Test
    void saveExistenteActualizaManaged() {
        CompraCotizacionTramo domain = CompraCotizacionTramo.builder().cotizacionTramoId(1L).build();
        CompraCotizacionTramoEntity managed = CompraCotizacionTramoEntity.builder().cotizacionTramoId(1L).build();
        when(repository.findByCotizacionTramoId(1L)).thenReturn(Optional.of(managed));
        when(repository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(domain);

        assertThat(adapter.save(domain)).isEqualTo(domain);
        verify(mapper).updateEntity(domain, managed);
    }

    @Test
    void saveNuevoInserta() {
        CompraCotizacionTramo domain = CompraCotizacionTramo.builder().nombre("N1").build();
        CompraCotizacionTramoEntity entity = CompraCotizacionTramoEntity.builder().build();
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.save(domain)).isEqualTo(domain);
    }

    @Test
    void saveExistenteInexistenteLanza() {
        CompraCotizacionTramo domain = CompraCotizacionTramo.builder().cotizacionTramoId(99L).build();
        when(repository.findByCotizacionTramoId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(domain))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void existsDelega() {
        when(repository.existsByCotizacionTramoId(1L)).thenReturn(true);
        assertThat(adapter.existsById(1L)).isTrue();
    }

    @Test
    void deleteDelega() {
        adapter.deleteById(1L);
        verify(repository).deleteByCotizacionTramoId(1L);
    }

}
