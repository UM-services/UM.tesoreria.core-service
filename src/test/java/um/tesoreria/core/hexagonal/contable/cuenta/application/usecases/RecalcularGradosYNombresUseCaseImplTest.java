package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.RecalculoGrados;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecalcularGradosYNombresUseCaseImplTest {

    // protects=recálculo y limpieza corren en una sola transacción, en ese orden, y el resumen trae ambos conteos; fails_when=alguno corre fuera de la transacción o se invierten los conteos; no_claim=no prueba el rollback real (es de Spring)
    @Test
    @SuppressWarnings("unchecked")
    void recalculaYLimpiaEnUnaTransaccion() {
        CuentaRepository repository = mock(CuentaRepository.class);
        TransactionOperations transaction = mock(TransactionOperations.class);
        when(transaction.execute(any())).thenAnswer(i -> ((TransactionCallback<Object>) i.getArgument(0)).doInTransaction(null));
        when(repository.recalcularGrados()).thenReturn(3);
        when(repository.limpiarNombres()).thenReturn(2);

        RecalculoGrados resumen = new RecalcularGradosYNombresUseCaseImpl(repository, transaction).recalcularGradosYNombres();

        assertThat(resumen).isEqualTo(new RecalculoGrados(3, 2));
        var orden = inOrder(transaction, repository);
        orden.verify(transaction).execute(any());
        orden.verify(repository).recalcularGrados();
        orden.verify(repository).limpiarNombres();
        verifyNoMoreInteractions(repository);
    }
}
