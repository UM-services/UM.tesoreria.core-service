package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetMateriasInscriptasUseCaseImplTest {

    // protects=el use case devuelve una lista vacía y no nula mientras no haya fuentes conectadas (#482);
    // fails_when=devuelve null o elementos antes de que #487 conecte las fuentes;
    // no_claim=la regla 1 de #481 (clase de chequera) ni la consulta a la facultad: eso lo prueba #487.
    @Test
    void returnsAnEmptyListUntilTheSourcesAreWired() {
        var useCase = new GetMateriasInscriptasUseCaseImpl();

        assertThat(useCase.getMateriasInscriptas(1, 2, 12345L)).isNotNull().isEmpty();
    }
}
