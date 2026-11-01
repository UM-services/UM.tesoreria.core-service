package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaUsoPort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBancariaUseCasesTest {
    @Mock BancariaRepository repository;
    @Mock BancariaUsoPort uso;

    @Test
    void getDevuelveLaCuentaOLanza404() {
        Bancaria cuenta = Bancaria.builder().bancariaId(8L).build();
        when(repository.findByBancariaId(8L)).thenReturn(Optional.of(cuenta));
        when(repository.findByBancariaId(9L)).thenReturn(Optional.empty());
        var useCase = new GetBancariaByIdUseCaseImpl(repository);
        assertThat(useCase.getBancariaById(8L)).isSameAs(cuenta);
        assertThatThrownBy(() -> useCase.getBancariaById(9L)).isInstanceOf(BancariaNoEncontradaException.class);
    }

    @Test
    void usoDevuelveLosConteosYEliminableSoloSiSonCero() {
        when(repository.findByBancariaId(8L)).thenReturn(Optional.of(Bancaria.builder().bancariaId(8L).build()));
        when(uso.contar(8L)).thenReturn(new BancariaUso(0, 0));
        var useCase = new GetBancariaUsoUseCaseImpl(repository, uso);
        assertThat(useCase.getBancariaUso(8L).eliminable()).isTrue();
        assertThat(new BancariaUso(0, 5).eliminable()).isFalse();
        assertThat(new BancariaUso(2, 0).eliminable()).isFalse();
    }

    @Test
    void usoDeCuentaInexistenteEs404SinContar() {
        when(repository.findByBancariaId(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new GetBancariaUsoUseCaseImpl(repository, uso).getBancariaUso(9L))
                .isInstanceOf(BancariaNoEncontradaException.class);
        verifyNoInteractions(uso);
    }
}
