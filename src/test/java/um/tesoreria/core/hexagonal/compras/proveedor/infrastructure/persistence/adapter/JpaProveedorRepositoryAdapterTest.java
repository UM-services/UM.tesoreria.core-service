package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.mapper.ProveedorMapper;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository.JpaProveedorRepository;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository.JpaProveedorSearchRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaProveedorRepositoryAdapterTest {

    @Mock
    private JpaProveedorRepository jpaRepository;
    @Mock
    private JpaProveedorSearchRepository searchRepository;
    @Mock
    private ProveedorMapper mapper;

    private JpaProveedorRepositoryAdapter adapter() {
        return new JpaProveedorRepositoryAdapter(jpaRepository, searchRepository, mapper);
    }

    @Test
    void borraYForzaElDeleteParaVerLaRestriccionDeLaBase() {
        when(jpaRepository.existsById(5)).thenReturn(true);

        assertThat(adapter().deleteById(5)).isTrue();

        InOrder orden = inOrder(jpaRepository);
        orden.verify(jpaRepository).deleteById(5);
        orden.verify(jpaRepository).flush();
    }

    @Test
    void siUnaFkFrenaElBorradoLoTraduceAConflicto() {
        when(jpaRepository.existsById(5)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK")).when(jpaRepository).flush();

        assertThatThrownBy(() -> adapter().deleteById(5))
                .isInstanceOf(ProveedorConflictoException.class)
                .hasMessageContaining("registros asociados");
    }

    @Test
    void siNoExisteDevuelveFalsoSinBorrar() {
        when(jpaRepository.existsById(5)).thenReturn(false);

        assertThat(adapter().deleteById(5)).isFalse();

        verify(jpaRepository, never()).deleteById(5);
    }

    @Test
    void sinIdAExcluirPreguntaPorElCuitSoloYConIdExcluyeAEseProveedor() {
        when(jpaRepository.existsByCuit("30-1")).thenReturn(true);
        when(jpaRepository.existsByCuitAndProveedorIdNot("30-2", 5)).thenReturn(false);

        assertThat(adapter().existsOtroConCuit("30-1", null)).isTrue();
        assertThat(adapter().existsOtroConCuit("30-2", 5)).isFalse();
    }
}