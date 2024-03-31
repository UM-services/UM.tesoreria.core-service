package um.tesoreria.core.service;

import um.tesoreria.core.kotlin.model.ProveedorArticuloTrack;
import um.tesoreria.core.repository.IProveedorArticuloTrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
