package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.model.TipoChequeraMercadoPagoCreditCard;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipoChequeraMercadoPagoCreditCardRepository extends JpaRepository<TipoChequeraMercadoPagoCreditCard, UUID> {

    Optional<TipoChequeraMercadoPagoCreditCard> findByTipoChequeraId(Integer tipoChequeraId);

}
