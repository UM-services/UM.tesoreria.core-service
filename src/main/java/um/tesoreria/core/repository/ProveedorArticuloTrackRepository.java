package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorArticuloTrack;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorArticuloTrackRepository extends JpaRepository<ProveedorArticuloTrack, Integer> {

    List<ProveedorArticuloTrack> findAllByProveedorArticuloId(Long proveedorArticuloId);

    Optional<ProveedorArticuloTrack> findByProveedorArticuloTrackId(Long proveedorArticuloTrackId);

    void deleteAllByProveedorArticuloIdAndTrackId(Long proveedorArticuloId, Long trackId);

    void deleteAllByProveedorArticuloTrackIdIn(List<Long> proveedorArticuloTrackIds);

    void deleteAllByProveedorArticuloIdIn(List<Long> ids);

}
