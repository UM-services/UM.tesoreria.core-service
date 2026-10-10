package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.entity.CompraAutoridadPerfilEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.mapper.CompraAutoridadPerfilMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.repository.JpaCompraAutoridadPerfilRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraAutoridadPerfilRepositoryAdapterTest {

    @Mock private JpaCompraAutoridadPerfilRepository repository;
    @Mock private CompraAutoridadPerfilMapper mapper;

    @InjectMocks
    private JpaCompraAutoridadPerfilRepositoryAdapter adapter;

    @Test
    void findByAutoridadPerfilIdMapea() {
        CompraAutoridadPerfilEntity entity = CompraAutoridadPerfilEntity.builder().autoridadPerfilId(1L).build();
        CompraAutoridadPerfil domain = CompraAutoridadPerfil.builder().autoridadPerfilId(1L).build();
        when(repository.findByAutoridadPerfilId(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.findByAutoridadPerfilId(1L)).contains(domain);
    }

    @Test
    void findByNombreMapea() {
        CompraAutoridadPerfilEntity entity = CompraAutoridadPerfilEntity.builder().autoridadPerfilId(1L).build();
        when(repository.findByNombre("N1")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(CompraAutoridadPerfil.builder().autoridadPerfilId(1L).build());

        assertThat(adapter.findByNombre("N1")).isPresent();
    }

    @Test
    void findAllMapea() {
        when(repository.findAll()).thenReturn(List.of(CompraAutoridadPerfilEntity.builder().autoridadPerfilId(1L).build()));
        when(mapper.toDomain(org.mockito.ArgumentMatchers.any())).thenReturn(CompraAutoridadPerfil.builder().build());

        assertThat(adapter.findAll()).hasSize(1);
    }

    @Test
    void saveExistenteActualizaManaged() {
        CompraAutoridadPerfil domain = CompraAutoridadPerfil.builder().autoridadPerfilId(1L).build();
        CompraAutoridadPerfilEntity managed = CompraAutoridadPerfilEntity.builder().autoridadPerfilId(1L).build();
        when(repository.findByAutoridadPerfilId(1L)).thenReturn(Optional.of(managed));
        when(repository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(domain);

        assertThat(adapter.save(domain)).isEqualTo(domain);
        verify(mapper).updateEntity(domain, managed);
    }

    @Test
    void saveNuevoInserta() {
        CompraAutoridadPerfil domain = CompraAutoridadPerfil.builder().nombre("N1").build();
        CompraAutoridadPerfilEntity entity = CompraAutoridadPerfilEntity.builder().build();
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.save(domain)).isEqualTo(domain);
    }

    @Test
    void deleteConAsignacionesTraduceElFk() {
        doThrow(new DataIntegrityViolationException("fk")).when(repository).deleteByAutoridadPerfilId(1L);

        assertThatThrownBy(() -> adapter.deleteByAutoridadPerfilId(1L))
                .isInstanceOf(CompraAutoridadPerfilException.class)
                .hasMessageContaining("usuarios asignados");
    }

    @Test
    void deleteLimpioBorraYFlushea() {
        adapter.deleteByAutoridadPerfilId(1L);

        verify(repository).deleteByAutoridadPerfilId(1L);
        verify(repository).flush();
    }

    @Test
    void existsDelega() {
        when(repository.existsByAutoridadPerfilId(1L)).thenReturn(true);
        assertThat(adapter.existsByAutoridadPerfilId(1L)).isTrue();
    }

}
