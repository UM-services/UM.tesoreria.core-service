# Historial transaccional de escrituras (Gestión) — issue #404

## Objetivo

Registrar, **en la misma transacción** de la operación de negocio, cada alta, edición o baja
con fecha, entidad, clave y valores anteriores/nuevos. Bloquea el cierre de escrituras Java
nuevas de Parámetros/Gestión que dependan de este mecanismo (#405–#409).

## Contrato reutilizable

Puerto de entrada: `RegistrarEscrituraHistorialUseCase`

| Método | valor_anterior | valor_nuevo |
|--------|----------------|-------------|
| `registrarAlta(entidad, clave, nuevo)` | NULL | estado creado |
| `registrarEdicion(entidad, clave, antes, despues)` | estado previo | estado nuevo |
| `registrarBaja(entidad, clave, antes)` | estado previo | NULL |

Implementación: `EscrituraHistorialService` (propagación `MANDATORY` → exige y participa de la TX del caller).

Errores (`IllegalArgumentException`): entidad o clave en blanco, entidad de más de 128 caracteres o clave
de más de 255 (después de recortar espacios), estado obligatorio nulo (el nuevo en el alta, ambos en la
edición, el previo en la baja), una entidad JPA en lugar de una copia, o un valor que no se puede serializar.
Sin transacción activa: `IllegalTransactionStateException`. Cualquier error marca la TX del caller como
rollback-only: capturarlo **no** salva la operación de negocio.

### Convenciones

- **entidad**: nombre estable del agregado/tabla de negocio (`proveedor`, `ejercicio`, `bancaria`, `cuenta`, `articulo`, …).
- **entidadClave**: clave de negocio como texto (`"42"`, o compuesta `"1:2"`, en el orden de la PK). Se rechaza `"null"` (id sin asignar). Debe permanecer igual a lo largo del ciclo de vida para ligar el historial. Se recortan espacios; mayúsculas y minúsculas se comparan igual (`utf8mb4_unicode_ci`), como en el resto de `tesium`.
- **valores**: copia del estado relevante (DTO, record o `Map` con valores simples), **nunca la entidad JPA**: el `save` la pisa y sus relaciones lazy se cargarían. Se rechazan entidades, proxies lazy y colecciones de Hibernate (listas, sets y mapas) en cualquier nivel del valor. La copia tiene que ser profunda: un record que guarda la lista de la entidad también se rechaza. Se guarda como JSON compacto con claves ordenadas, fechas ISO-8601 (`"2026-10-02"`, `"2026-10-02T10:15:30-03:00"`) y decimales sin notación científica; un `String` se guarda como cadena JSON. No incluir secretos innecesarios; no loguear ni devolver en APIs públicas.
- **fecha**: la asigna MySQL al insertar (`DEFAULT CURRENT_TIMESTAMP(6)`), igual que los `Now()` de VB6: hora del servidor (-03), porque el driver no cambia la zona de la sesión. Lo guardado no depende de la JVM. El evento devuelto no trae `fecha` (no se relee tras el insert); al leerla desde Java con `serverTimezone=UTC`, una JVM fuera de UTC la ve corrida. Ojo: los `created`/`updated` de `Auditable` que escribe Java quedan en UTC, no en -03.
- **Inmutable**: un evento registrado no se actualiza ni se borra (`@Immutable`; el repositorio no expone update ni delete y el adaptador rechaza guardar un evento que ya trae id).
- **Tamaño**: los valores son `LONGTEXT`; el límite práctico es `max_allowed_packet` del servidor (256 MB en desarrollo). Guardar solo los campos relevantes.
- **Sin estado**: el valor anterior de un alta y el nuevo de una baja se guardan como `NULL`.
- **Sin actor verificado**: no se inventa ni persiste un usuario como identidad autenticada por el servidor.
- **Sin consulta pública**: no hay controller REST de historial ni lectura en el puerto de dominio.

### Ejemplo de integración

```java
@Service
@RequiredArgsConstructor
public class EjemploEscrituraService {

    private final EjemploRepository repository;
    private final RegistrarEscrituraHistorialUseCase historial;

    @Transactional
    public Ejemplo alta(Ejemplo nuevo) {
        var guardado = repository.save(nuevo);
        historial.registrarAlta("ejemplo", String.valueOf(guardado.getId()), snapshot(guardado));
        return guardado;
    }

    @Transactional
    public Ejemplo edicion(Long id, Ejemplo cambios) {
        var antes = repository.findById(id).orElseThrow();
        var estadoAnterior = snapshot(antes);
        var despues = repository.save(merge(antes, cambios));
        historial.registrarEdicion("ejemplo", String.valueOf(id), estadoAnterior, snapshot(despues));
        return despues;
    }

    @Transactional
    public void baja(Long id) {
        var antes = repository.findById(id).orElseThrow();
        var estadoAnterior = snapshot(antes);
        repository.deleteById(id);
        historial.registrarBaja("ejemplo", String.valueOf(id), estadoAnterior);
    }
}
```

`snapshot` devuelve una copia de los campos de negocio relevantes antes de modificar la entidad administrada.
Los campos que se completan recién en el flush (`updated` de `Auditable`, `@Version`) no se ven en `snapshot(despues)`:
dejarlos fuera de la copia o usar `saveAndFlush`.

Ediciones concurrentes: si dos usuarios editan el mismo registro a la vez, ambos leen el mismo `antes`
y el segundo pisa al primero sin rastro. Leer `antes` con lock (`PESSIMISTIC_WRITE`) o usar `@Version`
en la entidad de negocio.

Si la operación de negocio lanza y la TX se revierte, **no queda evento** de historial.

## Esquema

Script reproducible: [`docs/sql/V404__gestion_escritura_historial.sql`](sql/V404__gestion_escritura_historial.sql)

Tabla `gestion_escritura_historial` en `tesium`. Aplicar manualmente (o con el proceso de esquema del equipo)
porque el servicio usa `spring.jpa.hibernate.ddl-auto: none`. Aplicado en desarrollo el 2026-10-02.

El script es `CREATE TABLE IF NOT EXISTS`: re-ejecutarlo no actualiza una tabla existente. Cualquier cambio
de esquema posterior va en un script nuevo con `ALTER TABLE`, no editando este.

## Pruebas

`EscrituraHistorialDevDbIT` corre contra la base de desarrollo con el perfil Maven `it` (`mvn -Pit verify`, ver
README), pero con su propio datasource: no usa `application-it.yml` ni su conexión de solo lectura. Crea la tabla
del script y una copia de `proveedores` como tablas `TEMPORARY` de su única conexión, y un `StatementInspector`
hace fallar cualquier escritura de Hibernate sobre otra tabla. No deja datos en la base. Si la tabla real ya está
aplicada, verifica que `fecha` coincida con el script. Sin `IT_DB_HOST` se saltea (`@EnabledIfEnvironmentVariable`).

## Relación con Auditable

`created` / `updated` de `Auditable` en las entidades de negocio **no sustituyen** este historial:
solo marcan timestamps de fila; el historial captura operación, clave y antes/después.

## Diagrama

Ver [`hexagonal-escrituraHistorial.mmd`](hexagonal-escrituraHistorial.mmd).
