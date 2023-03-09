package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.model.kotlin.ProveedorArticuloTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface IProveedorArticuloTrackRepository extends JpaRepository<ProveedorArticuloTrack, Integer> {

    @Modifying
    public void deleteAllByProveedorArticuloIdAndTrackId(Long proveedorArticuloId, Long trackId);

}
