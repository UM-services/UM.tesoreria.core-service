package um.tesoreria.core.hexagonal.usuarios.usuario;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper.UsuarioAuthMapper;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginResponse;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.mapper.AuthDtoMapper;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.mapper.UsuarioMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioResponse;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.mapper.UsuarioDtoMapper;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Guarda de CI contra el bug estructural: agregar una columna a UsuarioEntity y
 * "olvidar" de propagarla por las cadenas de mapeo. Con mapeo manual, ese olvido
 * pasa de verde en el build y se convierte en datos NULL/0 pisados en produccion
 * por el merge de JPA. Este test exige que TODO campo de UsuarioEntity exista
 * (mismo nombre y tipo) y conserve su valor en cada destino de las dos cadenas:
 * entity -> Usuario -> UsuarioResponse  y  entity -> UsuarioAuth -> LoginResponse.
 * Las exclusiones legitimas se declaran explicitamente en los skip-sets.
 */
class UsuarioFieldCompletenessTest {

    private static final OffsetDateTime SAMPLE_TIME = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    // Auditable: gestionadas por JPA auditing, no viajan en DTOs
    private static final Set<String> ENTITY_ALLOW = Set.of("created", "updated");

    // Campos que el dominio/modelo puede no exponer a proposito
    private static final Map<Class<?>, Set<String>> SKIPS = Map.of(
            Usuario.class, Set.of(),
            UsuarioResponse.class, Set.of("password"),
            um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth.class, Set.of(),
            LoginResponse.class, Set.of("password", "lastLog", "googleMail", "activo"));

    private static Object sampleValue(Field field) {
        Class<?> type = field.getType();
        if (type == Byte.class || type == byte.class) return (byte) 7;
        if (type == Long.class || type == long.class) return 42L;
        if (type == Integer.class || type == int.class) return 7;
        if (type == String.class) return "test-" + field.getName();
        if (type == OffsetDateTime.class) return SAMPLE_TIME;
        throw new IllegalStateException("Sample value no definido para " + type + " (" + field.getName() + ")");
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // seguir subiendo
            }
        }
        return null;
    }

    private static Object read(Object obj, String name) {
        try {
            Field f = findField(obj.getClass(), name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void assertChainCarriesAllEntityFields(UsuarioEntity source, Object target) {
        Set<String> skip = SKIPS.getOrDefault(target.getClass(), Set.of());
        for (Field ef : UsuarioEntity.class.getDeclaredFields()) {
            if (ef.isSynthetic() || ENTITY_ALLOW.contains(ef.getName())) continue;

            if (skip.contains(ef.getName())) continue;

            Field tf = findField(target.getClass(), ef.getName());
            assertThat(tf)
                    .as("El campo '%s' de UsuarioEntity no esta propagado a %s (agregalo o excluyendolo "
                            + "explicitamente en SKIPS)", ef.getName(), target.getClass().getSimpleName())
                    .isNotNull();
            assertThat(tf.getType())
                    .as("Tipo de '%s' en %s debe coincidir con el del entity", ef.getName(),
                            target.getClass().getSimpleName())
                    .isEqualTo(ef.getType());

            Object expected = read(source, ef.getName());
            assertThat(read(target, ef.getName()))
                    .as("'%s' perdio su valor en %s", ef.getName(), target.getClass().getSimpleName())
                    .isEqualTo(expected);
        }
    }

    private static UsuarioEntity fullyPopulatedEntity() {
        UsuarioEntity entity = new UsuarioEntity();
        for (Field f : UsuarioEntity.class.getDeclaredFields()) {
            if (f.isSynthetic()) continue;
            f.setAccessible(true);
            try {
                f.set(entity, sampleValue(f));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return entity;
    }

    @Test
    void usuarioChain_propagatesEveryEntityField() {
        UsuarioEntity entity = fullyPopulatedEntity();

        Usuario domain = new UsuarioMapper().toDomainModel(entity);
        UsuarioResponse response = new UsuarioDtoMapper().toResponse(domain);

        assertChainCarriesAllEntityFields(entity, domain);
        assertChainCarriesAllEntityFields(entity, response);
    }

    @Test
    void authChain_propagatesEveryEntityField() {
        UsuarioEntity entity = fullyPopulatedEntity();

        um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth authDomain =
                new UsuarioAuthMapper().toDomainModel(entity);

        GeograficaService geograficaService = mock(GeograficaService.class);
        when(geograficaService.findByGeograficaId(any())).thenReturn(Optional.empty());
        LoginResponse loginResponse = new AuthDtoMapper(geograficaService).toResponse(authDomain);

        assertChainCarriesAllEntityFields(entity, authDomain);
        assertChainCarriesAllEntityFields(entity, loginResponse);
    }
}
