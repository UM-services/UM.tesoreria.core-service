package um.tesoreria.rest.service;

import um.tesoreria.rest.kotlin.model.ProveedorArticuloTrack;
import um.tesoreria.rest.repository.IProveedorArticuloTrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.rest.repository.IProveedorArticuloTrackRepository;

@Service
public class ProveedorArticuloTrackService {

    @Autowired
    private IProveedorArticuloTrackRepository repository;

    public ProveedorArticuloTrack add(ProveedorArticuloTrack proveedorArticuloTrack) {
        return repository.save(proveedorArticuloTrack);
    }

    @Transactional
    public void deleteAllByProveedorArticuloIdAndTrackId(Long proveedorArticuloId, Long trackId) {
        repository.deleteAllByProveedorArticuloIdAndTrackId(proveedorArticuloId, trackId);
    }
}
