package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ReferenciasArticuloRepository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.DeleteUbicacionArticulosByArticuloUseCase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DeleteArticuloUseCaseImplTest {

    private ArticuloRepository repository;
    private ReferenciasArticuloRepository referencias;
    private DeleteUbicacionArticulosByArticuloUseCase vinculos;
    private DeleteArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ArticuloRepository.class);
        referencias = mock(ReferenciasArticuloRepository.class);
        vinculos = mock(DeleteUbicacionArticulosByArticuloUseCase.class);
        useCase = new DeleteArticuloUseCaseImpl(repository, referencias, vinculos);
        when(referencias.findReferencias(any())).thenReturn(List.of());
        when(vinculos.deleteByArticuloId(any())).thenReturn(List.of());
    }

    @Test
    void libre_seBloquea_seConsultanReferencias_yBorraVinculosAntesQueElArticulo() {
        when(repository.findByIdForUpdate(5L)).thenReturn(Optional.of(Articulo.builder().articuloId(5L).build()));

        useCase.deleteArticulo(5L);

        InOrder orden = inOrder(repository, referencias, vinculos);
        orden.verify(repository).findByIdForUpdate(5L);
        orden.verify(referencias).findReferencias(5L);
        orden.verify(vinculos).deleteByArticuloId(5L);
        orden.verify(repository).deleteById(5L);
    }

    @Test
    void referenciado_409ConTablasYCantidades_sinBorrarNada() {
        when(repository.findByIdForUpdate(5L)).thenReturn(Optional.of(Articulo.builder().articuloId(5L).build()));
        var encontradas = List.of(new ReferenciaArticulo("entrega_detalle", 3L), new ReferenciaArticulo("movprov_detallefactura", 10L));
        when(referencias.findReferencias(5L)).thenReturn(encontradas);

        assertThatThrownBy(() -> useCase.deleteArticulo(5L))
                .isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
                    assertThat(ex.getMotivo()).isEqualTo(ArticuloConflictException.Motivo.REFERENCIADO);
                    assertThat(ex.getReferencias()).isEqualTo(encontradas);
                });
        verifyNoInteractions(vinculos);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void inexistente_404SinConsultarNiBorrar() {
        when(repository.findByIdForUpdate(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.deleteArticulo(6L)).isInstanceOf(ArticuloException.class);
        verifyNoInteractions(referencias, vinculos);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void bloqueoAlBorrarVinculos_saleComoConflictoDelArticulo_conservandoSiSeReintenta() {
        when(repository.findByIdForUpdate(5L)).thenReturn(Optional.of(Articulo.builder().articuloId(5L).build()));
        when(vinculos.deleteByArticuloId(5L))
                .thenThrow(new UbicacionArticuloConflictException(true, "interbloqueo"))
                .thenThrow(UbicacionArticuloConflictException.bloqueado("espera vencida"));

        assertThatThrownBy(() -> useCase.deleteArticulo(5L))
                .isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
                    assertThat(ex.getMotivo()).isEqualTo(ArticuloConflictException.Motivo.BLOQUEADO);
                    assertThat(ex.isReintentable()).isTrue();
                });
        assertThatThrownBy(() -> useCase.deleteArticulo(5L))
                .isInstanceOfSatisfying(ArticuloConflictException.class, ex -> assertThat(ex.isReintentable()).isFalse());
        verify(repository, never()).deleteById(any());
    }
}
