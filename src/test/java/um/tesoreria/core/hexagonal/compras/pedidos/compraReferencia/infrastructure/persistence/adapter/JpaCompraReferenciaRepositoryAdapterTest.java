package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.entity.CompraReferenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.mapper.CompraReferenciaMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.repository.JpaCompraReferenciaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraReferenciaRepositoryAdapterTest {

    @Mock private JpaCompraReferenciaRepository repository;
    @Mock private CompraReferenciaMapper mapper;

    @InjectMocks
    private JpaCompraReferenciaRepositoryAdapter adapter;

    @Test
    void findByEjercicioIdMapea() {
        CompraReferenciaEntity entity = CompraReferenciaEntity.builder().ejercicioId(7).build();
        CompraReferencia domain = CompraReferencia.builder().ejercicioId(7).build();
        when(repository.findByEjercicioId(7)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.findByEjercicioId(7)).contains(domain);
    }

    @Test
    void saveExistenteActualizaLaEntidadManaged() {
        CompraReferencia domain = CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("2")).build();
        CompraReferenciaEntity managed = CompraReferenciaEntity.builder().ejercicioId(7).build();
        when(repository.existsById(7)).thenReturn(true);
        when(repository.findById(7)).thenReturn(Optional.of(managed));
        when(repository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(domain);

        assertThat(adapter.save(domain)).isEqualTo(domain);
        verify(mapper).updateEntity(domain, managed);
    }

    @Test
    void saveNuevaInserta() {
        CompraReferencia domain = CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("2")).build();
        CompraReferenciaEntity entity = CompraReferenciaEntity.builder().ejercicioId(7).build();
        when(repository.existsById(7)).thenReturn(false);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.save(domain)).isEqualTo(domain);
    }

}
