# Auditoría transversal de tesoreria-core (v11.5.0)

El servicio registra **quién hizo qué** sobre los datos de Tesorería y lo entrega al servicio de auditoría. La captura vive en `src/main/java/um/tesoreria/core/audit/` (paquete de plataforma, no es un slice hexagonal) y se apoya en el ciclo de vida de JPA: **toda escritura de negocio pasa por una entidad** para que Hibernate dispare sus listeners. Diagrama: [`auditoria.mmd`](auditoria.mmd).

No agrega ni cambia rutas REST, DTO ni permisos: el enforcement de permisos sigue siendo opt-in y la identidad del actor se toma del mismo header transitorio (`X-User-Id`) que ya usa esa capa.

## Qué se audita

| Nivel | Origen | Contenido |
|---|---|---|
| `REQUEST` | `AuditRequestFilter` | método, endpoint, estado HTTP, resultado (`OK`/`ERROR`), duración, actor, IP, correlación y cantidad de cambios |
| `CHANGE` | `AuditEntityListener` (`POST_INSERT`/`POST_UPDATE`/`POST_DELETE` + `AuditFlushListener`) | entidad, id, operación, `changedFields`, `beforeData`/`afterData` del diff escalar |
| `SUMMARY` | `AuditBulkService` + `AuditBulkScope` | conteos por entidad y operación de una generación masiva, con `bulkId`/`label`/`anho`/`mes` |
| `CHANGE` manual | `AuditManualChange.change(...)` / `.sequence(...)` | escrituras que no pasan por JPA (correlativos con `LAST_INSERT_ID`) |

- El `REQUEST` se emite para toda operación mutante (`POST`/`PUT`/`PATCH`/`DELETE`) y también para un `GET` que haya cambiado filas, incluso si la operación falló o hizo rollback. Se excluyen `/actuator`, `/error` y cualquier ruta que contenga `/search`.
- El diff sólo mira propiedades escalares: ignora asociaciones, colecciones y las columnas `created`/`updated`. Un update que no cambia campos no emite evento.
- En una operación masiva, las entidades de `summary-entities` se cuentan y **no** emiten un `CHANGE` por fila. Las bajas nunca se resumen: necesitan `beforeData` por fila.
- Hoy el resumen está cableado en `JpaChequeraCuotaRepositoryAdapter.saveAll` (etiqueta `alta-cuotas`), sólo cuando la cantidad de cuotas alcanza `bulk-min-rows` y hay transacción activa.

## Modos

`tesoreria.audit.mode` (`TESORERIA_AUDIT_MODE`):

| Modo | Captura | Escribe outbox | Envía |
|---|---|---|---|
| `OFF` | no | no | no |
| `LOG` | sí (sólo mide y loguea) | no | no |
| `SEND` | sí | sí | sí |

El default es `SEND`. **Si la tabla `audit_outbox` no existe, usar `LOG` u `OFF`**: en `SEND` el flush intenta insertar y el error rompe la transacción de negocio. Los tests corren en `OFF` (`src/test/resources/application.yml`).

## Configuración

| Propiedad | Variable de entorno | Default |
|---|---|---|
| `tesoreria.audit.mode` | `TESORERIA_AUDIT_MODE` | `SEND` |
| `tesoreria.audit.request-events` | `TESORERIA_AUDIT_REQUEST_EVENTS` | `true` |
| `tesoreria.audit.change-events` | `TESORERIA_AUDIT_CHANGE_EVENTS` | `true` |
| `tesoreria.audit.url` | `TESORERIA_AUDIT_URL` | `http://localhost:8211` |
| `tesoreria.audit.api-key` | `TESORERIA_AUDIT_API_KEY` | vacío |
| `tesoreria.audit.batch-size` | `TESORERIA_AUDIT_BATCH_SIZE` | `200` |
| `tesoreria.audit.sender-delay-ms` | `TESORERIA_AUDIT_SENDER_DELAY_MS` | `2000` |
| `tesoreria.audit.sender-initial-delay-ms` | `TESORERIA_AUDIT_SENDER_INITIAL_DELAY_MS` | `5000` |
| `tesoreria.audit.max-attempts` | `TESORERIA_AUDIT_MAX_ATTEMPTS` | `10` |
| `tesoreria.audit.max-payload-chars` | `TESORERIA_AUDIT_MAX_PAYLOAD_CHARS` | `32768` |
| `tesoreria.audit.mask-fields` | `TESORERIA_AUDIT_MASK_FIELDS` | `password,token,cbu,documento,tarjeta` |
| `tesoreria.audit.bulk-enabled` | `TESORERIA_AUDIT_BULK_ENABLED` | `true` |
| `tesoreria.audit.bulk-min-rows` | `TESORERIA_AUDIT_BULK_MIN_ROWS` | `20` |
| `tesoreria.audit.summary-entities` | `TESORERIA_AUDIT_SUMMARY_ENTITIES` | `ChequeraCuotaEntity,ChequeraSerieEntity,ChequeraTotalEntity` |
| `tesoreria.audit.excluded-entities` | — (sólo por archivo de configuración) | vacío |

`bulk-enabled: false` es el kill switch del resumen: los scopes quedan inertes y todo se audita fila por fila, sin deploy.

## Outbox y envío

- **DDL externo.** `sql/001_audit_outbox.sql` crea `audit_outbox` en la base de negocio (`tesium`). La aplicación no crea ni migra esquema (`ddl-auto: none`); el script se ejecuta a mano antes de pasar a `SEND`.
- **Misma transacción.** `AuditOutboxWriter` inserta con la conexión de la transacción de negocio: o se confirman el cambio y su evento, o ninguno. El `REQUEST` se escribe en una transacción nueva (`REQUIRES_NEW`) porque se emite cuando la del negocio ya terminó.
- **Envío.** `AuditOutboxSender` corre cada `sender-delay-ms`, reclama hasta `batch-size` filas con un `claim_token` y un lease de 60 s (no usa `SKIP LOCKED`: no requiere MySQL 8), y hace `POST {url}/api/tesoreria/auditoria/evento/lote` con `X-API-Key` y cuerpo `{"events":[...]}`.
- **Reintentos.** Los `5xx` y las excepciones vuelven la fila a `PENDING` con backoff exponencial (máximo 60 s) hasta `max-attempts`; `400`/`401`/`403`/`422` y los rechazos parciales (`rejected[].index`) pasan a `FAILED` con `last_error` (500 caracteres). La idempotencia real la garantiza el `event_id` del lado del servicio de auditoría.
- **Retención.** A las 03:30 se borran las filas `SENT` con más de 7 días. Las `FAILED` se conservan para diagnóstico.
- **Sin URL o sin API key** el sender loguea un warning y deja el outbox pendiente; no pierde eventos.

## Enmascarado

`mask-fields` acepta dos formas:

- `campo` — se enmascara en cualquier entidad (`password`).
- `Entidad.campo` — sólo en esa entidad; el nombre se compara ignorando mayúsculas y el sufijo `Entity` (`Usuario.clave` alcanza a `UsuarioEntity`).

Los valores enmascarados conservan los últimos cuatro caracteres (`****1234`) o quedan `****` si el texto tiene cuatro o menos. `clave` y `cuenta` **no** están en el default global a propósito: en `Permiso` la primera es la clave funcional del permiso y en `CuentaMensual`/`FacultadPagoCuenta`/`ChequeraPagoAsiento` la segunda es un número de cuenta contable. Si una entidad las usa como dato sensible, se agregan acotadas.

## Cobertura: el guard de escrituras

`AuditCoverageGuard` (usado por `AuditCoverageGuardTest`) recorre `src/main/java` y falla si encuentra una escritura que saltee el ciclo de vida de JPA: `createNativeQuery`, `createMutationQuery`, `StatelessSession`, `deleteAllInBatch`/`deleteAllByIdInBatch`, `@SQLDelete`/`@SQLInsert`/`@SQLUpdate`, `executeUpdate`, `@Procedure`, `SimpleJdbcInsert`, `doWork`, `getConnection()`, `@Query` de escritura y `JdbcTemplate`/`JdbcClient` usado para escribir.

- No marca falsos positivos: un delete derivado con `@Modifying` (Spring Data lo ignora y usa `em.remove`), un `@Query` de lectura o un `JdbcTemplate` que sólo consulta no son violaciones.
- La allowlist es `src/test/resources/audit-coverage-exceptions.txt` (`archivo|patrón|motivo`). Una entrada que ya no corresponde también rompe el test.
- Sólo hay cuatro excepciones: los correlativos anuales de compras (`compra_pedido_secuencia`, `compra_cotizacion_secuencia`, `compra_formalizacion_secuencia`, `compra_recepcion_secuencia`), que reservan con `LAST_INSERT_ID` en una sola sentencia por atomicidad y emiten su evento con `AuditManualChange.sequence(...)`.
- Para regenerar la lista después de una migración deliberada: `mvn test -Dtest=AuditCoverageGuardTest -Daudit.exceptions.write=true`.

## Límites conocidos

- **El actor no está autenticado.** Sale de `X-User-Id`/`X-Usuario-Id` y del login de `X-Usuario-Login`, ambos falsificables, por eso el evento se marca `actorTrust: CLIENT`. Es la misma identidad transitoria de la capa de permisos; cambiará cuando exista JWT.
- **Sólo se auditan cambios de columnas.** Cambiar una asociación sin tocar columnas propias no genera diff.
- **Los `GET` de búsqueda quedan fuera** (`/search`) para no llenar el outbox con lecturas.
- **El outbox es un buffer, no el registro final.** Pasados 7 días desde el envío, la fila se borra: la fuente de verdad es el servicio de auditoría.
- **La IP** se toma de `X-Forwarded-For` (primer valor) y, si falta, de la conexión. Es lo que informe el proxy.
