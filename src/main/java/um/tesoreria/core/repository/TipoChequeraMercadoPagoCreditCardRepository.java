package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.model.TipoChequeraMercadoPagoCreditCard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipoChequeraMercadoPagoCreditCardRepository extends JpaRepository<TipoChequeraMercadoPagoCreditCard, UUID> {

    List<TipoChequeraMercadoPagoCreditCard> findAllByActive(Byte active);

    Optional<TipoChequeraMercadoPagoCreditCard> findByTipoChequeraIdAndAlternativaId(Integer tipoChequeraId, Integer alternativaId);

}
