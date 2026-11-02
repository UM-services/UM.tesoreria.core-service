package um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model;

/** Período del contrato B1, sin traducciones de SIUM. */
public record MateriaCursadaGuarani(String actividadCodigo, String nombre, Integer curso,
                                   String periodo, Integer periodoLectivo) {
}
