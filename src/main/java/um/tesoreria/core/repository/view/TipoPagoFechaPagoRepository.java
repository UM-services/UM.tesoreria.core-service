package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.model.view.TipoPagoFechaPago;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TipoPagoFechaPagoRepository extends JpaRepository<TipoPagoFechaPago, String> {

    List<TipoPagoFechaPago> findAllByFechaPago(OffsetDateTime fechaPago);

}
