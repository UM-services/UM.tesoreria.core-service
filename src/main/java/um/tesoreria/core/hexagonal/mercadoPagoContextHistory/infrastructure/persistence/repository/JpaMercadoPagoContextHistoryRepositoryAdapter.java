package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model.MercadoPagoContextHistory;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.ports.out.MercadoPagoContextHistoryRepository;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.persistence.mapper.MercadoPagoContextHistoryMapper;
import um.tesoreria.core.model.MercadoPagoContext;

@Component
@RequiredArgsConstructor
public class JpaMercadoPagoContextHistoryRepositoryAdapter implements MercadoPagoContextHistoryRepository {

    private final JpaMercadoPagoContextHistoryRepository jpaMercadoPagoContextHistoryRepository;
    private final MercadoPagoContextHistoryMapper mercadoPagoContextHistoryMapper;

    @Override
    public MercadoPagoContextHistory create(MercadoPagoContext mercadoPagoContext) {
        var mercadoPagoContextHistory = mercadoPagoContextHistoryMapper.fromContext(mercadoPagoContext);
        var mercadoPagoContextHistoryEntity = mercadoPagoContextHistoryMapper.toEntityModel(mercadoPagoContextHistory);
        var savedEntity = jpaMercadoPagoContextHistoryRepository.save(mercadoPagoContextHistoryEntity);
        return mercadoPagoContextHistoryMapper.toDomainModel(savedEntity);
    }

}
