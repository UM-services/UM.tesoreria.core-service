package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorArticuloTrack;

@Repository
public interface IProveedorArticuloTrackRepository extends JpaRepository<ProveedorArticuloTrack, Integer> {

    @Modifying
    public void deleteAllByProveedorArticuloIdAndTrackId(Long proveedorArticuloId, Long trackId);

}
