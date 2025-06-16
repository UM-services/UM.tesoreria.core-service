package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorArticuloTrack;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorArticuloTrackRepository extends JpaRepository<ProveedorArticuloTrack, Integer> {

    List<ProveedorArticuloTrack> findAllByProveedorArticuloId(Long proveedorArticuloId);

    Optional<ProveedorArticuloTrack> findByProveedorArticuloTrackId(Long proveedorArticuloTrackId);

    @Modifying
    void deleteAllByProveedorArticuloIdAndTrackId(Long proveedorArticuloId, Long trackId);

    @Modifying
    void deleteAllByProveedorArticuloTrackIdIn(List<Long> proveedorArticuloTrackIds);

    @Modifying
    @Query("DELETE FROM ProveedorArticuloTrack t WHERE t.proveedorArticulo.proveedorArticuloId IN :ids")
    void deleteAllByProveedorArticuloIdIn(List<Long> ids);

}
