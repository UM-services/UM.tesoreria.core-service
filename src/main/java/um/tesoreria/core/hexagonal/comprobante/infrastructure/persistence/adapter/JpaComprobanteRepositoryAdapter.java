package um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.out.ComprobanteRepository;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.mapper.ComprobanteMapper;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.repository.JpaComprobanteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaComprobanteRepositoryAdapter implements ComprobanteRepository {

    private final JpaComprobanteRepository jpaComprobanteRepository;
    private final ComprobanteMapper comprobanteMapper;

    @Override
    public List<Comprobante> findAll() {
        return jpaComprobanteRepository.findAll().stream()
                .map(comprobanteMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comprobante> findAllByOrdenPago() {
        return jpaComprobanteRepository.findAllByOrdenPago((byte) 1).stream()
                .map(comprobanteMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId) {
        return jpaComprobanteRepository.findAllByTipoTransaccionId(tipoTransaccionId).stream()
                .map(comprobanteMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comprobante> findAllByOrdenPagoAndTipoTransaccionId(Integer tipoTransaccionId) {
        return jpaComprobanteRepository.findAllByOrdenPagoAndTipoTransaccionId((byte) 1, tipoTransaccionId).stream()
                .map(comprobanteMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comprobante> findFirstByTipoTransaccionId(Integer tipoTransaccionId) {
        return jpaComprobanteRepository.findFirstByTipoTransaccionId(tipoTransaccionId)
                .map(comprobanteMapper::toDomainModel);
    }

    @Override
    public Optional<Comprobante> findByComprobanteId(Integer comprobanteId) {
        return jpaComprobanteRepository.findByComprobanteId(comprobanteId)
                .map(comprobanteMapper::toDomainModel);
    }
}
