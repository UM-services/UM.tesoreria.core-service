/**
 * 
 */
package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

	Optional<UsuarioEntity> findByPassword(String password);

	Optional<UsuarioEntity> findByLogin(String login);

	Optional<UsuarioEntity> findByUserId(Long userId);

	@Query("SELECT u FROM UsuarioEntity u WHERE u.activo = 1 AND (:texto IS NULL "
			+ "OR LOWER(u.login) LIKE LOWER(CONCAT('%', :texto, '%')) "
			+ "OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))")
	List<UsuarioEntity> findUsuariosBySearch(@Param("texto") String texto);

	@Query("SELECT u FROM UsuarioEntity u WHERE (:texto IS NULL "
			+ "OR LOWER(u.login) LIKE LOWER(CONCAT('%', :texto, '%')) "
			+ "OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))")
	List<UsuarioEntity> findAllBySearch(@Param("texto") String texto);

    	Optional<UsuarioEntity> findByGoogleMailAndActivo(String googleMail, Byte activo);
    
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query("UPDATE UsuarioEntity u SET u.lastLog = :lastLog WHERE u.userId = :userId")
        void updateLastLog(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("lastLog") java.time.OffsetDateTime lastLog);
    }
