package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.MateriaCursada;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse.Detalle;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse.Materia;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse.MateriaCurso;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class PendienteInfoFacultadMapper {

    private record Clave(Integer facultadId, Integer planId, String materiaId) {
    }

    private static final Comparator<Clave> ORDEN_DE_BUSQUEDA = Comparator
            .comparing(Clave::planId, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(Clave::materiaId, Comparator.nullsFirst(Comparator.naturalOrder()));

    /**
     * Cruza detalles, materias, {@code materiascarrera} y {@code materiascurso}.
     * <ul>
     *   <li>Nombre: {@code materias} por (facultad, plan del detalle, materia); si falta, {@code detalles[].materia};
     *   si falta, el {@code resolvedor}. El resolvedor recibe cada clave faltante una sola vez,
     *   en orden de plan y materia.</li>
     *   <li>Curso y período: solo si la materia figura en {@code materiascarrera} de la carrera de la inscripción.</li>
     * </ul>
     * Vacío si no hay inscripción o no hay detalles.
     */
    public Optional<InscripcionMaterias> toDomain(PendienteInfoFacultadResponse response,
                                                  NombreMateriaResolver resolvedor) {
        if (response == null || response.inscripcion() == null || orEmpty(response.detalles()).isEmpty()) {
            return Optional.empty();
        }
        var inscripcion = response.inscripcion();
        List<Detalle> detalles = orEmpty(response.detalles());

        Map<Clave, String> nombres = new HashMap<>();
        for (Materia materia : orEmpty(response.materias())) {
            if (hasText(materia.nombre())) {
                nombres.putIfAbsent(clave(materia.facultadId(), materia.planId(), materia.materiaId()), materia.nombre());
            }
        }
        for (Detalle detalle : detalles) {
            var clave = clave(detalle.facultadId(), detalle.planId(), detalle.materiaId());
            if (!nombres.containsKey(clave) && detalle.materia() != null && hasText(detalle.materia().nombre())) {
                nombres.put(clave, detalle.materia().nombre());
            }
        }
        Set<Clave> faltantes = new HashSet<>();
        for (Detalle detalle : detalles) {
            var clave = clave(detalle.facultadId(), detalle.planId(), detalle.materiaId());
            // Una clave incompleta no se puede buscar: la llamada iría con un tramo vacío y cortaría las demás.
            boolean completa = clave.facultadId() != null && clave.planId() != null && hasText(clave.materiaId());
            if (completa && !nombres.containsKey(clave)) {
                faltantes.add(clave);
            }
        }
        faltantes.stream().sorted(ORDEN_DE_BUSQUEDA).forEach(clave -> {
            String nombre = resolvedor.resolver(clave.facultadId(), clave.planId(), clave.materiaId());
            if (hasText(nombre)) {
                nombres.put(clave, nombre);
            }
        });

        // Sin carrera en la inscripción no hay con qué cruzar: curso y período quedan null.
        Integer carreraId = inscripcion.carreraId();
        Set<Clave> enCarrera = new HashSet<>();
        Map<Clave, MateriaCurso> cursadas = new HashMap<>();
        if (carreraId != null) {
            orEmpty(response.materiascarrera()).stream()
                    .filter(m -> carreraId.equals(m.carreraId()))
                    .forEach(m -> enCarrera.add(clave(m.facultadId(), m.planId(), m.materiaId())));
            orEmpty(response.materiascurso()).stream()
                    .filter(m -> carreraId.equals(m.carreraId()))
                    .forEach(m -> cursadas.putIfAbsent(clave(m.facultadId(), m.planId(), m.materiaId()), m));
        }

        List<MateriaCursada> materias = detalles.stream().map(detalle -> {
            var clave = clave(detalle.facultadId(), detalle.planId(), detalle.materiaId());
            MateriaCurso cursada = enCarrera.contains(clave) ? cursadas.get(clave) : null;
            return new MateriaCursada(detalle.facultadId(), detalle.planId(), detalle.materiaId(),
                    nombres.get(clave),
                    cursada == null ? null : cursada.curso(),
                    cursada == null ? null : cursada.periodo());
        }).toList();

        return Optional.of(new InscripcionMaterias(
                inscripcion.curso(),
                response.plan() == null ? null : response.plan().nombre(),
                response.carrera() == null ? null : response.carrera().nombre(),
                hasText(inscripcion.observaciones()) ? inscripcion.observaciones() : null,
                materias));
    }

    private static Clave clave(Integer facultadId, Integer planId, String materiaId) {
        return new Clave(facultadId, planId, materiaId);
    }

    private static boolean hasText(String texto) {
        return texto != null && !texto.isBlank();
    }

    private static <T> List<T> orEmpty(List<T> lista) {
        return lista == null ? List.of() : lista.stream().filter(Objects::nonNull).toList();
    }
}
