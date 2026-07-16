/**
 * 
 */
package um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.repository;

import java.util.Optional;

import um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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

    	Optional<UsuarioEntity> findByGoogleMailAndActivo(String googleMail, Byte activo);
    
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query("UPDATE UsuarioEntity u SET u.lastLog = :lastLog WHERE u.userId = :userId")
        void updateLastLog(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("lastLog") java.time.OffsetDateTime lastLog);
    }
