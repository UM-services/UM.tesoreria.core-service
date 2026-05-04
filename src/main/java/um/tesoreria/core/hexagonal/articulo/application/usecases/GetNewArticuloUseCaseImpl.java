package um.tesoreria.core.hexagonal.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.GetNewArticuloUseCase;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetNewArticuloUseCaseImpl implements GetNewArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    public Articulo getNewArticulo() {
        Articulo last = repository.findLast()
                .orElse(Articulo.builder().articuloId(0L).build());
        return Articulo.builder()
                .articuloId(last.getArticuloId() + 1)
                .nombre("")
                .descripcion("")
                .unidad("")
                .precio(BigDecimal.ZERO)
                .inventariable((byte) 0)
                .stockMinimo(0L)
                .tipo("")
                .directo((byte) 0)
                .habilitado((byte) 0)
                .build();
    }
}
