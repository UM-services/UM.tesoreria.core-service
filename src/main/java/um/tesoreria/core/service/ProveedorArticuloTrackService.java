package um.tesoreria.core.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import um.tesoreria.core.exception.ProveedorArticuloTrackException;
import um.tesoreria.core.kotlin.model.ProveedorArticuloTrack;
import um.tesoreria.core.repository.IProveedorArticuloTrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProveedorArticuloTrackService {

    private final IProveedorArticuloTrackRepository repository;

    public ProveedorArticuloTrackService(IProveedorArticuloTrackRepository repository) {
        this.repository = repository;
    }

    public List<ProveedorArticuloTrack> findAllByProveedorArticuloId(Long proveedorArticuloId) {
        log.debug("Processing findAllByProveedorArticuloId");
        return repository.findAllByProveedorArticuloId(proveedorArticuloId);
    }

    public ProveedorArticuloTrack add(ProveedorArticuloTrack proveedorArticuloTrack) {
        return repository.save(proveedorArticuloTrack);
    }

    public ProveedorArticuloTrack update(ProveedorArticuloTrack newProveedorArticuloTrack, Long proveedorArticuloTrackId) {
        log.debug("Processing update");
        return repository.findByProveedorArticuloTrackId(proveedorArticuloTrackId)
                .map(proveedorArticuloTrack -> {
                    proveedorArticuloTrack = new ProveedorArticuloTrack.Builder()
                            .proveedorArticuloTrackId(proveedorArticuloTrackId)
                            .proveedorMovimientoId(newProveedorArticuloTrack.getProveedorMovimientoId())
                            .proveedorArticuloId(newProveedorArticuloTrack.getProveedorArticuloId())
                            .trackId(newProveedorArticuloTrack.getTrackId())
                            .importe(newProveedorArticuloTrack.getImporte())
                            .track(newProveedorArticuloTrack.getTrack())
                            .proveedorMovimiento(newProveedorArticuloTrack.getProveedorMovimiento())
                            .proveedorArticulo(newProveedorArticuloTrack.getProveedorArticulo())
                            .build();
                    return proveedorArticuloTrack;
                })
                .orElseThrow(() -> new ProveedorArticuloTrackException(proveedorArticuloTrackId));
    }

    @Transactional
    public void deleteAllByProveedorArticuloIdAndTrackId(Long proveedorArticuloId, Long trackId) {
        repository.deleteAllByProveedorArticuloIdAndTrackId(proveedorArticuloId, trackId);
    }

    @Transactional
    public void deleteAllByProveedorArticuloTrackIdIn(List<Long> proveedorArticuloTrackIds) {
        log.debug("Processing deleteAllByProveedorArticuloTrackIdIn");
        repository.deleteAllByProveedorArticuloTrackIdIn(proveedorArticuloTrackIds);
    }

    public void deleteAllByProveedorArticuloIdIn(List<Long> proveedorArticuloIds) {
        log.debug("Processing deleteAllByProveedorArticuloIdIn");
        repository.deleteAllByProveedorArticuloIdIn(proveedorArticuloIds);
    }

    @Transactional
    public void deleteAll(List<ProveedorArticuloTrack> tracks) {
        log.debug("Processing deleteAll");
        repository.deleteAll(tracks);
    }

}
