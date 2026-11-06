package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception.GuaraniBeneficioException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateGuaraniBeneficioUseCaseImplTest {

    @Test
    void create_rejectsExistingAndConcurrentDuplicatesAsDomainConflict() {
        var repository = mock(GuaraniBeneficioRepository.class);
        var useCase = new CreateGuaraniBeneficioUseCaseImpl(repository);
        var beneficio = GuaraniBeneficio.builder().requisito(10).porcentajeBeneficio(BigDecimal.TEN).build();
        when(repository.findByRequisito(10)).thenReturn(Optional.of(beneficio));

        assertThatThrownBy(() -> useCase.create(beneficio)).isInstanceOf(GuaraniBeneficioException.class);

        when(repository.findByRequisito(10)).thenReturn(Optional.empty());
        when(repository.save(beneficio)).thenThrow(new DataIntegrityViolationException("duplicate"));
        assertThatThrownBy(() -> useCase.create(beneficio)).isInstanceOf(GuaraniBeneficioException.class);
    }
}
