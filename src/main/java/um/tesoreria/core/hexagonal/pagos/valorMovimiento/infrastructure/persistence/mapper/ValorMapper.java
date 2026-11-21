package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity.ValorEntity;

@Component
public class ValorMapper {

    public Valor toDomain(ValorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Valor(
                entity.getValorId(),
                entity.getConcepto(),
                entity.getCategoria(),
                entity.getRetardo(),
                entity.getVencimiento(),
                entity.getProveedorId(),
                entity.getNumeroCuenta());
    }
}
