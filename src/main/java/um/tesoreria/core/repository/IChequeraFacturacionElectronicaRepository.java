package um.tesoreria.core.repository;

import um.tesoreria.core.kotlin.model.ChequeraFacturacionElectronica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IChequeraFacturacionElectronicaRepository extends JpaRepository<ChequeraFacturacionElectronica, Long> {

    public Optional<ChequeraFacturacionElectronica> findByChequeraId(Long chequeraId);

    public Optional<ChequeraFacturacionElectronica> findByChequeraFacturacionElectronicaId(Long chequeraFacturacionElectronicaId);

    @Modifying
    public void deleteByChequeraFacturacionElectronicaId(Long chequeraFacturacionElectronicaId);

}
