package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;

import java.util.Optional;

public interface BancariaRepository {
    Optional<Bancaria> findByBancariaId(Long bancariaId);

    /** Lee la cuenta con bloqueo de escritura; tiene que ser la primera lectura de esa fila en la transacción. */
    Optional<Bancaria> findWithLockByBancariaId(Long bancariaId);

    /** INSERT con flush; devuelve la cuenta con el id y el updated asignados. Una FK rota se informa como conflicto. */
    Bancaria create(Bancaria bancaria);

    /** UPDATE con flush; devuelve la cuenta con el updated nuevo. Una FK rota se informa como conflicto. */
    Bancaria update(Bancaria bancaria);

    /** DELETE con flush. Una FK rota (la cuenta pasó a estar en uso) se informa como EN_USO. */
    void deleteByBancariaId(Long bancariaId);
}
