package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaUsoPort;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository.JpaLibroBancoReferenciaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository.JpaValorReferenciaRepository;

/** Las únicas referencias a bancaria.CuB_ID son las FK de valores y de libro_banco. */
@Component
@RequiredArgsConstructor
public class BancariaUsoAdapter implements BancariaUsoPort {

    private final JpaValorReferenciaRepository valores;
    private final JpaLibroBancoReferenciaRepository libroBanco;

    @Override
    public boolean enUso(Long bancariaId) {
        return valores.existsByBancariaIdOrigen(bancariaId) || libroBanco.existsByBancariaId(bancariaId);
    }

    @Override
    public BancariaUso contar(Long bancariaId) {
        return new BancariaUso(valores.countByBancariaIdOrigen(bancariaId), libroBanco.countByBancariaId(bancariaId));
    }
}
