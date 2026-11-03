package um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.ports.out;

import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model.InscripcionMateriasGuarani;
import java.util.List;

public interface FindInscripcionesGuaraniPort {
    List<InscripcionMateriasGuarani> findByDocumentoAndAnioAcademico(
            String nroDocumento, Integer anioAcademico, Integer responsableAcademica);
}
