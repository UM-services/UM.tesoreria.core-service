package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.GetNewArancelTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;

@Component
@RequiredArgsConstructor
public class GetNewArancelTipoUseCaseImpl implements GetNewArancelTipoUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public ArancelTipo getNewArancelTipo() {
        ArancelTipo last = repository.findLast()
                .orElse(ArancelTipo.builder().arancelTipoId(0).build());
        return ArancelTipo.builder()
                .arancelTipoId(last.getArancelTipoId() + 1)
                .descripcion("")
                .medioArancel((byte) 0)
                .habilitado((byte) 0)
                .build();
    }
}
