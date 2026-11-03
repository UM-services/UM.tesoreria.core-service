package um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model;

import java.util.List;

/** Una inscripción por propuesta y versión de plan de la responsable solicitada. */
public record InscripcionMateriasGuarani(Integer propuesta, Integer planVersion, String carrera,
                                        Integer cursoInscripcion, List<MateriaCursadaGuarani> materias) {
}
