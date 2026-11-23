package um.tesoreria.core.hexagonal.compras.correlativo;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Base de los repositorios de correlativo anual: la lectura bloquea la fila del ejercicio
 * ({@code SELECT ... FOR UPDATE}) para serializar dos reservas concurrentes.
 *
 * <p>{@code @NoRepositoryBean}: es un contrato base, no un repositorio concreto (sin esto
 * Spring Data intenta crearlo como bean y falla porque {@link SecuenciaAnual} no es una
 * entidad).</p>
 */
@NoRepositoryBean
public interface SecuenciaAnualRepository<T extends SecuenciaAnual> extends JpaRepository<T, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<T> findWithLockByEjercicioId(Integer ejercicioId);

}
