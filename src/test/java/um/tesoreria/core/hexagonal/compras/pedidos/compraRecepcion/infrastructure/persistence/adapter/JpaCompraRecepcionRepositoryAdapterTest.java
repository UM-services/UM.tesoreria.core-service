package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.mapper.CompraRecepcionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository.JpaCompraRecepcionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository.JpaCompraRecepcionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraRecepcionRepositoryAdapterTest {

    @Mock private JpaCompraRecepcionRepository repository;
    @Mock private JpaCompraRecepcionItemRepository itemRepository;

    private JpaCompraRecepcionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaCompraRecepcionRepositoryAdapter(repository, itemRepository, new CompraRecepcionMapper());
    }

    private CompraRecepcion domain(Long id) {
        return CompraRecepcion.builder()
                .compraRecepcionId(id)
                .compraFormalizacionId(50L)
                .numero("RC-2026-000001")
                .ejercicioId(15)
                .items(List.of(CompraRecepcionItem.builder().compraFormalizacionItemId(100L).orden(1)
                        .cantidadRecibida(new BigDecimal("2.00"))
                        .resultado(CompraRecepcionResultado.CONFORME).build()))
                .build();
    }

    @Test
    void saveNuevoPersisteCabeceraEItems() {
        when(repository.save(any())).thenAnswer(inv -> {
            CompraRecepcionEntity e = inv.getArgument(0);
            e.setCompraRecepcionId(1L);
            return e;
        });
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.findById(1L)).thenReturn(Optional.of(CompraRecepcionEntity.builder()
                .compraRecepcionId(1L).numero("RC-2026-000001").ejercicioId(15).build()));
        when(itemRepository.findByCompraRecepcionIdOrderByOrden(1L)).thenReturn(List.of(
                CompraRecepcionItemEntity.builder().compraRecepcionItemId(2L).compraRecepcionId(1L)
                        .compraFormalizacionItemId(100L).orden(1).cantidadRecibida(new BigDecimal("2.00"))
                        .build()));

        CompraRecepcion resultado = adapter.save(domain(null));

        assertThat(resultado.getCompraRecepcionId()).isEqualTo(1L);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getCompraRecepcionId()).isEqualTo(1L);
        verify(itemRepository).deleteByCompraRecepcionId(1L);
        verify(itemRepository).save(any());
    }

    @Test
    void saveExistenteActualizaLaEntidadGestionada() {
        CompraRecepcionEntity managed = CompraRecepcionEntity.builder().compraRecepcionId(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(managed));
        when(repository.save(managed)).thenReturn(managed);
        when(itemRepository.findByCompraRecepcionIdOrderByOrden(1L)).thenReturn(List.of());
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CompraRecepcion resultado = adapter.save(domain(1L));

        assertThat(managed.getNumero()).isEqualTo("RC-2026-000001");
        assertThat(resultado.getCompraRecepcionId()).isEqualTo(1L);
    }

    @Test
    void findByIdMapeaConItems() {
        when(repository.findById(1L)).thenReturn(Optional.of(
                CompraRecepcionEntity.builder().compraRecepcionId(1L).build()));
        when(itemRepository.findByCompraRecepcionIdOrderByOrden(1L)).thenReturn(List.of(
                CompraRecepcionItemEntity.builder().compraRecepcionItemId(2L).orden(1).build()));

        assertThat(adapter.findById(1L).orElseThrow().getItems()).hasSize(1);
        assertThat(adapter.findById(99L)).isEmpty();
    }

    @Test
    void findByCompraFormalizacionIdMapeaConItems() {
        when(repository.findByCompraFormalizacionIdOrderByFecha(50L)).thenReturn(List.of(
                CompraRecepcionEntity.builder().compraRecepcionId(1L).build()));
        when(itemRepository.findByCompraRecepcionIdOrderByOrden(1L)).thenReturn(List.of());

        assertThat(adapter.findByCompraFormalizacionId(50L)).hasSize(1);
    }

    @Test
    void findByCriteriaAplanaConItems() {
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(
                CompraRecepcionEntity.builder().compraRecepcionId(1L).build()));
        when(itemRepository.findByCompraRecepcionIdOrderByOrden(1L)).thenReturn(List.of());

        List<CompraRecepcion> recepciones = adapter.findByCriteria(new CompraRecepcionCriteria(15, 50L,
                CompraRecepcionResultado.CONFORME, null, null));

        assertThat(recepciones).hasSize(1);
    }

}
