package um.tesoreria.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.TipoChequeraMercadoPagoCreditCardException;
import um.tesoreria.core.model.TipoChequeraMercadoPagoCreditCard;
import um.tesoreria.core.repository.TipoChequeraMercadoPagoCreditCardRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TipoChequeraMercadoPagoCreditCardService {

    private final TipoChequeraMercadoPagoCreditCardRepository repository;

    public TipoChequeraMercadoPagoCreditCardService(TipoChequeraMercadoPagoCreditCardRepository repository) {
        this.repository = repository;
    }

    public List<TipoChequeraMercadoPagoCreditCard> findAllActivos() {
        return repository.findAllByActive((byte) 1);
    }

    public TipoChequeraMercadoPagoCreditCard findByUnique(Integer tipoChequeraId, Integer alternativaId) {
        return repository.findByTipoChequeraIdAndAlternativaId(tipoChequeraId, alternativaId).orElseThrow(() -> new TipoChequeraMercadoPagoCreditCardException(tipoChequeraId, alternativaId));
    }

    public String persist(Integer tipoChequeraId, Integer alternativaId, Integer cuotas) {
        UUID id;
        try {
            var tipoChequeraMercadoPagoCreditCard = findByUnique(tipoChequeraId, alternativaId);
            id = tipoChequeraMercadoPagoCreditCard.getId();
        } catch (TipoChequeraMercadoPagoCreditCardException e) {
            id = UUID.randomUUID();
        }
        var tipoChequeraMercadoPagoCreditCard = TipoChequeraMercadoPagoCreditCard.builder()
                .id(id)
                .tipoChequeraId(tipoChequeraId)
                .alternativaId(alternativaId)
                .installments(cuotas)
                .defaultInstallments(cuotas)
                .active((byte) 1)
                .build();
        tipoChequeraMercadoPagoCreditCard = repository.save(tipoChequeraMercadoPagoCreditCard);
        return "Ok";
    }

    public String baja(Integer tipoChequeraId, Integer alternativaId) {
        try {
            var tipoChequeraMercadoPagoCreditCard = findByUnique(tipoChequeraId, alternativaId);
            tipoChequeraMercadoPagoCreditCard.setActive((byte) 0);
            tipoChequeraMercadoPagoCreditCard = repository.save(tipoChequeraMercadoPagoCreditCard);
            return "Ok";
        } catch (TipoChequeraMercadoPagoCreditCardException e) {
            return "Sin Tipo Chequera";
        }
    }

    public String bajaId(UUID id) {
        try {
            var tipoChequeraMercadoPagoCreditCardOptional = repository.findById(id);
            if (tipoChequeraMercadoPagoCreditCardOptional.isPresent()) {
                var tipoChequeraMercadoPagoCreditCard = tipoChequeraMercadoPagoCreditCardOptional.get();
                tipoChequeraMercadoPagoCreditCard.setActive((byte) 0);
                tipoChequeraMercadoPagoCreditCard = repository.save(tipoChequeraMercadoPagoCreditCard);
                return "Ok";
            }
        } catch (TipoChequeraMercadoPagoCreditCardException e) {
            log.debug("Sin Registro -> {}", e.getMessage());
        }
        return "Sin Tipo Chequera";
    }

}
