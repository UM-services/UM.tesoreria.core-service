package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception.CompraReferenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.out.CompraReferenciaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraReferenciaUseCasesTest {

    @Mock
    private CompraReferenciaRepository repository;

    @Test
    void getDelegaEnElRepositorio() {
        CompraReferencia referencia = CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("100")).build();
        when(repository.findByEjercicioId(7)).thenReturn(Optional.of(referencia));
        GetReferenciaByEjercicioUseCaseImpl useCase = new GetReferenciaByEjercicioUseCaseImpl(repository);

        assertThat(useCase.getByEjercicioId(7)).contains(referencia);
    }

    @Test
    void getExigeEjercicio() {
        GetReferenciaByEjercicioUseCaseImpl useCase = new GetReferenciaByEjercicioUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.getByEjercicioId(null)).isInstanceOf(CompraReferenciaException.class);
    }

    @Test
    void upsertExigeEjercicio() {
        UpsertReferenciaUseCaseImpl useCase = new UpsertReferenciaUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.upsert(CompraReferencia.builder().importe(new BigDecimal("1")).build()))
                .isInstanceOf(CompraReferenciaException.class);
    }

    @Test
    void upsertValidaImportePositivo() {
        UpsertReferenciaUseCaseImpl useCase = new UpsertReferenciaUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.upsert(CompraReferencia.builder().ejercicioId(7).build()))
                .isInstanceOf(CompraReferenciaException.class);
        assertThatThrownBy(() -> useCase.upsert(CompraReferencia.builder()
                .ejercicioId(7).importe(BigDecimal.ZERO).build()))
                .isInstanceOf(CompraReferenciaException.class);
    }

    @Test
    void upsertGuardaLaReferencia() {
        CompraReferencia referencia = CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("100")).build();
        when(repository.save(referencia)).thenReturn(referencia);
        UpsertReferenciaUseCaseImpl useCase = new UpsertReferenciaUseCaseImpl(repository);

        assertThat(useCase.upsert(referencia)).isEqualTo(referencia);
        verify(repository).save(referencia);
    }

}
