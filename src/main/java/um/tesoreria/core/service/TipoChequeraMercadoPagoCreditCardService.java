package um.tesoreria.core.service;

import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.TipoChequeraMercadoPagoCreditCardException;
import um.tesoreria.core.model.TipoChequeraMercadoPagoCreditCard;
import um.tesoreria.core.repository.TipoChequeraMercadoPagoCreditCardRepository;

@Service
public class TipoChequeraMercadoPagoCreditCardService {

    private final TipoChequeraMercadoPagoCreditCardRepository repository;

    public TipoChequeraMercadoPagoCreditCardService(TipoChequeraMercadoPagoCreditCardRepository repository) {
        this.repository = repository;
    }

    public TipoChequeraMercadoPagoCreditCard findByTipoChequeraId(Integer tipoChequeraId) {
        return repository.findByTipoChequeraId(tipoChequeraId).orElseThrow(() -> new TipoChequeraMercadoPagoCreditCardException(tipoChequeraId));
    }

}
