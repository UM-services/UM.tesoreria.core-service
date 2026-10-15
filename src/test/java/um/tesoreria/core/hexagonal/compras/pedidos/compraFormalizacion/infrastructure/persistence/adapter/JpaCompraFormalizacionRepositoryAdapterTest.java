package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.mapper.CompraFormalizacionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository.JpaCompraFormalizacionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository.JpaCompraFormalizacionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraFormalizacionRepositoryAdapterTest {

    @Mock private JpaCompraFormalizacionRepository repository;
    @Mock private JpaCompraFormalizacionItemRepository itemRepository;

    private JpaCompraFormalizacionRepositoryAdapter adapter;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        adapter = new JpaCompraFormalizacionRepositoryAdapter(repository, itemRepository,
                new CompraFormalizacionMapper());
    }

    private CompraFormalizacion domain(Long id) {
        return CompraFormalizacion.builder()
                .compraFormalizacionId(id)
                .compraAdjudicacionId(9L)
                .numero("OC-2026-000001")
                .ejercicioId(15)
                .proveedorId(7)
                .monto(new BigDecimal("28000.00"))
                .items(List.of(CompraFormalizacionItem.builder().orden(1)
                        .cantidad(new BigDecimal("3.00")).descripcion("Notebook").build()))
                .build();
    }

    @Test
    void saveNuevoPersisteCabeceraEItems() {
        when(repository.save(any())).thenAnswer(inv -> {
            CompraFormalizacionEntity e = inv.getArgument(0);
            e.setCompraFormalizacionId(1L);
            return e;
        });
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CompraFormalizacionEntity persistida = CompraFormalizacionEntity.builder()
                .compraFormalizacionId(1L).numero("OC-2026-000001").ejercicioId(15).build();
        when(repository.findById(1L)).thenReturn(Optional.of(persistida));
        when(itemRepository.findByCompraFormalizacionIdOrderByOrden(1L)).thenReturn(List.of(
                CompraFormalizacionItemEntity.builder().compraFormalizacionItemId(2L).compraFormalizacionId(1L)
                        .orden(1).cantidad(new BigDecimal("3.00")).descripcion("Notebook").build()));

        CompraFormalizacion resultado = adapter.save(domain(null));

        assertThat(resultado.getCompraFormalizacionId()).isEqualTo(1L);
        assertThat(resultado.getItems()).hasSize(1);
        verify(itemRepository).deleteByCompraFormalizacionId(1L);
        verify(itemRepository).save(any());
    }

    @Test
    void saveExistenteActualizaLaEntidadGestionada() {
        CompraFormalizacionEntity managed = CompraFormalizacionEntity.builder().compraFormalizacionId(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(managed));
        when(repository.save(managed)).thenReturn(managed);
        when(itemRepository.findByCompraFormalizacionIdOrderByOrden(1L)).thenReturn(List.of());

        CompraFormalizacion resultado = adapter.save(domain(1L));

        assertThat(managed.getNumero()).isEqualTo("OC-2026-000001");
        assertThat(resultado.getCompraFormalizacionId()).isEqualTo(1L);
    }

    @Test
    void findByIdMapeaConItems() {
        when(repository.findById(1L)).thenReturn(Optional.of(
                CompraFormalizacionEntity.builder().compraFormalizacionId(1L).build()));
        when(itemRepository.findByCompraFormalizacionIdOrderByOrden(1L)).thenReturn(List.of(
                CompraFormalizacionItemEntity.builder().compraFormalizacionItemId(2L).orden(1).build()));

        CompraFormalizacion resultado = adapter.findById(1L).orElseThrow();

        assertThat(resultado.getItems()).hasSize(1);
    }

    @Test
    void findByCompraAdjudicacionIdDelega() {
        when(repository.findByCompraAdjudicacionId(9L)).thenReturn(Optional.of(
                CompraFormalizacionEntity.builder().compraFormalizacionId(1L).build()));
        when(itemRepository.findByCompraFormalizacionIdOrderByOrden(1L)).thenReturn(List.of());

        assertThat(adapter.findByCompraAdjudicacionId(9L)).isPresent();
    }

    @Test
    void existsDelega() {
        when(repository.existsByCompraAdjudicacionId(9L)).thenReturn(true);
        assertThat(adapter.existsByCompraAdjudicacionId(9L)).isTrue();
    }

    @Test
    void findByCriteriaAplanaConItems() {
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(
                CompraFormalizacionEntity.builder().compraFormalizacionId(1L).build()));
        when(itemRepository.findByCompraFormalizacionIdOrderByOrden(1L)).thenReturn(List.of());

        assertThat(adapter.findByCriteria(new CompraFormalizacionCriteria(15, null, null, null))).hasSize(1);
    }
}
