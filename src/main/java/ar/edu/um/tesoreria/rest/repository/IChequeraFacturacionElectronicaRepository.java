package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraFacturacionElectronica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IChequeraFacturacionElectronicaRepository extends JpaRepository<ChequeraFacturacionElectronica, Long> {

    public Optional<ChequeraFacturacionElectronica> findByChequeraId(Long chequeraId);

    public Optional<ChequeraFacturacionElectronica> findByChequeraFacturacionElectronicaId(Long chequeraFacturacionElectronicaId);
}
