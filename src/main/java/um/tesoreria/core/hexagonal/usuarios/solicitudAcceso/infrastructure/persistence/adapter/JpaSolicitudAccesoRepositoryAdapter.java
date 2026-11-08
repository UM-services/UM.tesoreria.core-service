package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.EstadoSolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.out.SolicitudAccesoRepository;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.entity.SolicitudAccesoEntity;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.mapper.SolicitudAccesoMapper;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.repository.JpaSolicitudAccesoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaSolicitudAccesoRepositoryAdapter implements SolicitudAccesoRepository {

    private final JpaSolicitudAccesoRepository repository;
    private final SolicitudAccesoMapper mapper;

    @Override
    public Optional<SolicitudAcceso> findById(Long solicitudAccesoId) {
        return repository.findBySolicitudAccesoId(solicitudAccesoId).map(mapper::toDomain);
    }

    @Override
    public List<SolicitudAcceso> findPendientes() {
        return repository
                .findAllByEstadoOrderByCreatedDesc(EstadoSolicitudAcceso.PENDIENTE.name())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudAcceso> findByUserId(Long userId) {
        return repository.findAllByUserIdOrderByCreatedDesc(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsPendienteByUserIdAndClave(Long userId, String clave) {
        return repository.existsByUserIdAndClaveAndEstado(
                userId, clave, EstadoSolicitudAcceso.PENDIENTE.name());
    }

    @Override
    @Transactional
    public SolicitudAcceso save(SolicitudAcceso solicitud) {
        SolicitudAccesoEntity saved;
        if (solicitud.getSolicitudAccesoId() != null) {
            SolicitudAccesoEntity managed = repository
                    .findBySolicitudAccesoId(solicitud.getSolicitudAccesoId())
                    .orElseGet(() -> mapper.toEntity(solicitud));
            managed.setUserId(solicitud.getUserId());
            managed.setAplicacion(solicitud.getAplicacion());
            managed.setClave(solicitud.getClave());
            managed.setModulo(solicitud.getModulo());
            managed.setComentario(solicitud.getComentario());
            managed.setEstado(solicitud.getEstado());
            managed.setObservacion(solicitud.getObservacion());
            managed.setResueltoPor(solicitud.getResueltoPor());
            managed.setResuelto(solicitud.getResuelto());
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(solicitud));
        }
        return mapper.toDomain(saved);
    }
}
