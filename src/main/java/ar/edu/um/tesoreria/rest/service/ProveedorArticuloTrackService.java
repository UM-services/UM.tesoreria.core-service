package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.model.kotlin.ProveedorArticuloTrack;
import ar.edu.um.tesoreria.rest.repository.IProveedorArticuloTrackRepository;
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
