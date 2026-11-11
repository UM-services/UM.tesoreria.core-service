package um.tesoreria.core.architecture;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guardrail de rutas: todos los controllers deben exponer la ruta larga
 * {@code /api/tesoreria/core/...}, además de la corta cuando la tienen.
 *
 * <p>El gateway sólo enruta {@code /api/tesoreria/core/**} hacia este servicio, así que un
 * endpoint sin alias largo existe pero es inalcanzable para los frontends (el request cae en
 * el handler de recursos estáticos y devuelve 404). El VB6, en cambio, llama directo al puerto
 * 8092 con la ruta corta: por eso las dos deben convivir.
 */
class ControllerRouteAliasTest {

    private static final String PREFIJO_LARGO = "/api/tesoreria/core";

    @Test
    void todosLosControllersDeclaranLaRutaLarga() {
        List<String> sinAlias = new ArrayList<>();
        List<String> sinMapping = new ArrayList<>();

        List<Class<?>> controllers = controllers();
        assertThat(controllers)
                .as("El escaneo no encontró controllers: el guardrail quedaría vacío")
                .hasSizeGreaterThan(100);

        for (Class<?> controller : controllers) {
            RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class);
            if (mapping == null) {
                sinMapping.add(controller.getName());
                continue;
            }
            String[] paths = mapping.value().length > 0 ? mapping.value() : mapping.path();
            boolean tieneAlias = Arrays.stream(paths).anyMatch(p -> p.startsWith(PREFIJO_LARGO));
            if (!tieneAlias) {
                sinAlias.add(controller.getSimpleName() + " -> " + Arrays.toString(paths));
            }
        }

        assertThat(sinMapping)
                .as("Controllers sin @RequestMapping de clase: no se puede validar el alias largo")
                .isEmpty();
        assertThat(sinAlias)
                .as("Controllers sin la ruta larga %s (el gateway no los puede alcanzar)", PREFIJO_LARGO)
                .isEmpty();
    }

    private List<Class<?>> controllers() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class, true, true));

        List<Class<?>> controllers = new ArrayList<>();
        for (var definition : scanner.findCandidateComponents("um.tesoreria.core")) {
            try {
                Class<?> clazz = Class.forName(definition.getBeanClassName());
                if (AnnotatedElementUtils.hasAnnotation(clazz, RestController.class)) {
                    controllers.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("No se pudo cargar " + definition.getBeanClassName(), e);
            }
        }
        return controllers;
    }
}
