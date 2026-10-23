# Cuentas bancarias: alta, edición y baja — issue #409

El servicio decide las reglas de datos de `frmCuentasBancarias` (VB6), que hasta ahora escribía en `bancaria` por SQL directo y borraba sin chequear referencias. `BancariaController` queda como fachada de las rutas nuevas, que delegan en el slice `contable/bancaria`; no se agrega gating de permisos ni DDL. Las escrituras requieren la tabla `gestion_escritura_historial` de #404. `GET /bancaria/`, `BancariaService` y `Bancaria.kt` no cambian (siguen sirviendo al listado, a `CompraService` y al JSON anidado de `ValorMovimiento` que lee VB6).

## Rutas

| Ruta | Qué hace |
|---|---|
| `POST /bancaria/` | alta; responde la cuenta creada |
| `PUT /bancaria/{id}` | edición por presencia de campos; responde la cuenta |
| `DELETE /bancaria/{id}` | baja; éxito con cuerpo vacío |
| `GET /bancaria/{id}` | una cuenta, con las claves planas del listado (sin el objeto `cuenta` anidado) |
| `GET /bancaria/{id}/uso` | `{valores, libroBanco, eliminable}` para que el cliente confirme antes de borrar |

Las respuestas de éxito no llevan la clave `status`; todo error lleva cuerpo JSON `{status, error, codigo, message}` (VB6 toma un cuerpo con `status` como rechazo y uno vacío como éxito).

## Reglas

- **Alta.** `numeroCuenta` (cuenta contable) es obligatoria: `CompraService` la usa al pagar con un valor de esa cuenta bancaria y falla con `NullPointerException` si falta. Banco, número, CBU, titular y CUIT ausentes o en `null` valen `''` (la tabla los guarda `NOT NULL`). `bancariaId` `null`, `0` o negativo se toma como omitido (VB6 manda `0`); un id positivo es `400 ID_NO_PERMITIDO`. `created` y `uuid` (32 hexadecimales, como `REPLACE(UUID(), '-', '')`) los pone el alta; `updated` lo maneja MySQL.
- **Edición.** El id de la URL manda: un `bancariaId` distinto en el cuerpo es `400 ID_NO_COINCIDE`; ausente, `null`, `0`, negativo o igual se acepta. Campo ausente **conserva** lo guardado; `null` **vacía** (texto `''`, `proveedorId` en `NULL`); `numeroCuenta` en `null` es `400 CUENTA_REQUERIDA`. Sin cambios efectivos responde `200` sin evento de historial. Es distinto de #407 y #408, donde `null` conserva.
- **Precondición opcional `updated`.** Si el PUT trae `updated` (el de la última lectura) y no coincide con el guardado, responde `409 CAMBIO_CONCURRENTE` y no escribe. Sin `updated` se comporta como antes. La base guarda segundos: dos ediciones dentro del mismo segundo no se distinguen.
- **Textos.** Banco, número y titular se guardan tal cual (sin recortar espacios) y no pueden pasar de 64 caracteres ni contener lo que la columna `latin1` no guarda (en MySQL es cp1252: acepta `ñ`, tildes, `€` y comillas tipográficas, no un emoji) → `400 TEXTO_INVALIDO`.
- **CUIT y CBU.** CUIT con dígito verificador (módulo 11), con o sin guiones, y se guarda `XX-XXXXXXXX-X` → `400 CUIT_INVALIDO`. CBU de 22 dígitos con sus dos verificadores → `400 CBU_INVALIDO`. Vacío y el CUIT «sin dato» `00-00000000-0` son válidos. En la edición solo se validan si cambian: un valor histórico inválido que no se toca pasa.
- **Referencias.** Cuenta contable y proveedor se consultan por los puertos públicos `GetCuentaByNumeroCuentaUseCase` y `GetProveedorByIdUseCase` → `400 CUENTA_INEXISTENTE` / `400 PROVEEDOR_INEXISTENTE`. En la edición solo se consultan si cambian.
- **Números.** `numeroCuenta`, `proveedorId` y `bancariaId` se leen como decimales y se exigen enteros exactos (Jackson truncaría `12.7` a `12`): `proveedorId` de 1 a 32767 (`smallint`), `numeroCuenta` positivo de hasta 11 dígitos → `400 NUMERO_INVALIDO`.
- **Baja.** Rechaza con `409 EN_USO` y el conteo si hay valores (`valores.val_cuentaorigen`) o movimientos de libro banco (`libro_banco.lib_cub_id`). Un `DELETE` o `GET` de un id inexistente es `404` con cuerpo.

## Errores

| Estado | `codigo` | Cuándo |
|---|---|---|
| 400 | `ID_NO_PERMITIDO` / `ID_NO_COINCIDE` | id positivo en el alta; id del cuerpo distinto al de la URL |
| 400 | `TEXTO_INVALIDO` / `NUMERO_INVALIDO` | texto largo o fuera de latin1; id o cuenta no entero o fuera de rango |
| 400 | `CUIT_INVALIDO` / `CBU_INVALIDO` | dígitos verificadores o formato |
| 400 | `CUENTA_REQUERIDA` | alta sin cuenta contable o `null` en la edición |
| 400 | `CUENTA_INEXISTENTE` / `PROVEEDOR_INEXISTENTE` | la referencia no existe |
| 400 | `SOLICITUD_INVALIDA` | cuerpo mal formado, `updated` ilegible o id de la ruta no numérico |
| 404 | `NO_ENCONTRADA` | GET, PUT o DELETE de un id inexistente |
| 409 | `EN_USO` | baja con valores o movimientos; también si la base rechaza el borrado por una FK |
| 409 | `CAMBIO_CONCURRENTE` | la precondición `updated` no coincide |
| 409 | `CONFLICTO` | un dato referenciado cambió durante el alta o la edición (FK); o la fila está tomada por otra operación (lock wait timeout) |

Solo una violación de **clave foránea** se traduce a `409`: Hibernate la clasifica como `ConstraintViolationException` con `getKind() == FOREIGN_KEY` (también en Postgres, sin leer códigos de MySQL). Un `NOT NULL`, un duplicado u otro error de integridad es un defecto nuestro y sigue como `500` con log.

## Concurrencia

PUT y DELETE leen la fila con `@Lock(PESSIMISTIC_WRITE)` como primera lectura de la transacción (JPA estándar; el dialecto `MySQL57Dialect` genera `FOR UPDATE` simple). Aislado en `JpaBancariaRepository` con el comentario «específico de MySQL: revisar al migrar a Postgres». Dos PUT sobre la misma cuenta se serializan y los dos cambios quedan; una baja que cruza una edición registra en el historial el último estado confirmado. El lock no cubre un cliente que reenvíe todos los campos con valores viejos: para eso está la precondición `updated`.

## Historial

Alta, edición y baja registran en `gestion_escritura_historial` (entidad `bancaria`, clave `cub_id`) en la misma transacción: los casos de uso son `@Transactional` porque el historial es `MANDATORY`. El estado se guarda como copia (nunca la entidad): `cub_id`, `banco`, `numero`, `cbu`, `titular`, `cuit`, `numeroCuenta`, `proveedorId`.

## Diseño de persistencia

`bancaria`, `valores` y `libro_banco` ya tienen entidades Kotlin (`Bancaria.kt`, `ValorMovimiento.kt`, `BancoMovimiento.kt`). El slice agrega `BancariaEntity` y dos entidades mínimas de solo lectura (`ValorReferenciaEntity`, `LibroBancoReferenciaEntity`) con los **mismos nombres lógicos de columna, en minúscula** (`cub_*`, `val_cuentaorigen`, `lib_cub_id`…): Hibernate no arranca si dos entidades sobre una tabla nombran distinto una columna. `BancariaEntity` no extiende `Auditable`: `created` es `NULL` en las filas históricas y allí es `NOT NULL`. Los chequeos de uso son métodos derivados (`existsBy…`, `countBy…`), sin `@Query` ni SQL nativo. La clave real de `libro_banco` es `(lib_cub_id, lib_fecha, lib_orden)`; `lib_id` es un auto-incremental indexado, que alcanza para una entidad que solo se consulta con `exists` y `count`.

## Límites conocidos

- **Lecturas legacy.** El listado, `CompraService` y `ValorMovimiento.bancaria` siguen en `Bancaria.kt`: hay dos clases sobre `bancaria` hasta que se migren (ver `TODOS.md`). `numeroCuenta` obligatoria protege a `CompraService`; las cuentas existentes de dev ya la tienen.
- **VB6 directo a SQL.** `frmCuentasBancarias` sigue escribiendo por SQL directo mientras no use esta API: no pasa por estas reglas ni deja historial.
- **Resolución de `updated`.** Un segundo; dos ediciones dentro del mismo segundo no se distinguen.
- **Valores con cuenta 0.** En dev hay filas de `valores` con `val_cuentaorigen = 0`, un valor que ninguna cuenta tiene; el chequeo de uso solo mira cuentas existentes.
- **Historial.** Las escrituras dependen de `gestion_escritura_historial` (#404, aplicada hoy solo en dev): sin la tabla fallan y revierten.
- **Tabla `cuentasBancarias`.** No se toca.
