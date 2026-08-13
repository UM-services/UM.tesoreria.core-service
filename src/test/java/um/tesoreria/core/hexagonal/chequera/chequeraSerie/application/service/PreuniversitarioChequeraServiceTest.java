package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;
import um.tesoreria.core.service.ChequeraSerieControlService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreuniversitarioChequeraServiceTest {

    @Mock
    private PreuniversitarioDataResolver dataResolver;
    @Mock
    private PreuniversitarioLegajoManager legajoManager;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraSerieControlService chequeraSerieControlService;
    @Mock
    private PreuniversitarioChequeraDetailsCreator detailsCreator;
    @Mock
    private PreuniversitarioChequeraPolicy policy;

    @InjectMocks
    private PreuniversitarioChequeraService service;

    @Test
    void create_returnsNullWhenRequiredReferencesCannotBeResolved() {
        var data = new PreuniversitarioChequeraData(10, 20, 30,
                BigDecimal.ONE, 50);
        when(dataResolver.resolve(data)).thenReturn(Optional.empty());

        assertThat(service.create(data)).isNull();
        verifyNoInteractions(legajoManager, chequeraSerieService,
                chequeraSerieControlService, detailsCreator);
    }
}
