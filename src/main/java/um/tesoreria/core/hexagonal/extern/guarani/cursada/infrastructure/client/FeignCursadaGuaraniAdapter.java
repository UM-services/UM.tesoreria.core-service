package um.tesoreria.core.hexagonal.extern.guarani.cursada.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.client.tesoreria.guarani.GuaraniCursadaClient;
import um.tesoreria.core.client.tesoreria.guarani.CursadaGuaraniResponse;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model.InscripcionMateriasGuarani;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model.MateriaCursadaGuarani;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.ports.out.FindInscripcionesGuaraniPort;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class FeignCursadaGuaraniAdapter implements FindInscripcionesGuaraniPort {
    private final GuaraniCursadaClient client;

    @Override
    public List<InscripcionMateriasGuarani> findByDocumentoAndAnioAcademico(
            String nroDocumento, Integer anioAcademico, Integer responsableAcademica) {
        if (responsableAcademica == null) return List.of();
        Map<Clave, List<CursadaGuaraniResponse>> grupos = new TreeMap<>(Comparator
                .comparing(Clave::propuesta).thenComparing(Clave::planVersion));
        for (var fila : client.findByDocumentoAndAnioAcademico(nroDocumento, anioAcademico)) {
            if (Objects.equals(responsableAcademica, fila.responsableAcademica())) {
                grupos.computeIfAbsent(new Clave(fila.propuesta(), fila.planVersion()),
                        ignored -> new ArrayList<>()).add(fila);
            }
        }
        return grupos.entrySet().stream().map(entry -> {
            var primera = entry.getValue().getFirst();
            return new InscripcionMateriasGuarani(entry.getKey().propuesta(), entry.getKey().planVersion(),
                    primera.propuestaNombre(), primera.anioCursada() == null ? null : primera.anioCursada().intValue(),
                    entry.getValue().stream().map(fila -> new MateriaCursadaGuarani(fila.actividadCodigo(),
                            fila.actividadNombre(), fila.anioPlan(), fila.periodo(), fila.periodoLectivo())).toList());
        }).toList();
    }

    private record Clave(Integer propuesta, Integer planVersion) {}
}
