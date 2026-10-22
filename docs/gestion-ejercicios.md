# Ejercicios: alta, baja y bloqueo — issue #408

El servicio decide las reglas de datos de `clsCtlEjercicios` (VB6). Las rutas y los campos existentes no cambian; no se agrega gating de permisos ni DDL. Las escrituras requieren la tabla `gestion_escritura_historial` de #404. Las lecturas (`GET /ejercicio/`, `/{id}`, `/last`, `/fecha/{fecha}`) siguen en `EjercicioService` y `Ejercicio.kt`; `EjercicioController` queda como fachada de POST, PUT y DELETE (se retiraron `add`, `update` y `delete` de `EjercicioService`, que quedaron sin usuarios), que delegan en el slice `contable/ejercicio`.

## Reglas

Las fechas guardadas son `datetime` a las 00:00 y la conexión usa `serverTimezone=UTC`: se leen como día en **UTC**. Solo «hoy» sale del `Clock` (Buenos Aires). Pasar las fechas guardadas a Buenos Aires correría todo un día.

- `POST /ejercicio/`: crea solo el siguiente al último. Id `último + 1`, del 1 de marzo al último día de febrero (`2027-03-01` → `2028-02-29`), nombre `Ejercicio Económico <año de inicio>`. Lo omitido se deriva; lo enviado tiene que coincidir. Órdenes contables y `bloqueado` por defecto `0`. Límite: si `Year(último.fechaInicio) > Year(hoy) + 1` rechaza (`LIMITE_ALTA`). El alta exige que el último ejercicio termine el día anterior al inicio derivado (si no, `409 SALTO`). Con la tabla vacía no hay nada que derivar: exige `ejercicioId`, `fechaInicio` y `fechaFinal`, con inicio anterior al fin.
- `PUT /ejercicio/{id}`: solo cambian `bloqueado` (0 o 1; otro valor → `400`) y las dos órdenes contables. `null` conserva lo guardado. `nombre`, `fechaInicio`, `fechaFinal` o `ejercicioId` distintos de lo guardado → `409 CAMPO_INMUTABLE`; iguales se aceptan (VB6 reenvía todo con `makeParams`). Sin cambio real no deja historial.
- `DELETE /ejercicio/{id}`: solo el último (`NO_ES_ULTIMO`) y solo si su año de inicio es posterior al año de hoy (`NO_FUTURO`). Rechaza con `EN_USO` si hay movimientos contables (`movcon`) **o** asientos (`asiento`) con fecha dentro del período, o si una FK `RESTRICT` rechaza el borrado (nueve tablas de Compras). Las dos consultas hacen falta: en dev hay asientos sin movimientos y movimientos sin asiento. Un id inexistente responde `404`.

## Errores

Todo rechazo de regla responde con JSON (VB6 toma un cuerpo vacío como éxito):

| Estado | `codigo` | Cuándo |
|---|---|---|
| 409 | `SALTO` | el id o las fechas enviados saltan un período |
| 409 | `DUPLICADO` | el ejercicio ya existe o el período se superpone (incluye la carrera de dos altas) |
| 409 | `LIMITE_ALTA` | alta por encima del límite relativo al año actual |
| 409 | `NO_ES_ULTIMO` / `NO_FUTURO` | baja de un ejercicio que no es el último o que no es futuro |
| 409 | `EN_USO` | baja con movimientos, asientos o filas que lo referencian |
| 409 | `CAMPO_INMUTABLE` | PUT o alta con nombre/fechas distintos |
| 409 | `CONCURRENCIA` | la base rechazó el bloqueo (deadlock persistente o lock wait timeout); reintentar |
| 404 | `NO_ENCONTRADO` | PUT o DELETE de un id inexistente |
| 400 | `SOLICITUD_INVALIDA` | `bloqueado` fuera de {0,1}, órdenes contables fuera de 0..32767 (`smallint`), `nombre` de más de 128 caracteres, primer período irregular con la tabla vacía o cuerpo mal formado |

Cuerpo: `{status, error, codigo, message}`. El `@ExceptionHandler` vive en `EjercicioController`, que es quien recibe el HTTP.

## Concurrencia

Alta, baja y el cambio de la fila editada leen con `@Lock(PESSIMISTIC_WRITE)` (JPA estándar) como primera lectura de la transacción; el alta usa `persist` + `flush`, nunca `save()`/`merge`, para no pisar una fila existente. Está aislado en `JpaEjercicioRepositoryAdapter` con el comentario «específico de MySQL: revisar al migrar a Postgres» (el dialecto `MySQL57Dialect` genera `FOR UPDATE` simple). El alta y la baja leen el último con `ORDER BY ... LIMIT 1 FOR UPDATE`, que en InnoDB toma también el bloqueo de hueco del final del índice: dos altas simultáneas pueden quedar en deadlock y MySQL revierte una. Como la transacción es repetible, el caso de uso reintenta hasta 3 veces solo ante un deadlock (error de MySQL 1213, identificado por código y no por SQLState: Connector/J también usa 40001 para el lock wait timeout 1205; con una espera aleatoria de 20 a 80 ms). La edición reintenta igual y la repetición ve el estado ya confirmado (`DUPLICADO` en lugar de un error de base). Un lock wait timeout (1205) no se reintenta: la web lo responde `409 CONCURRENCIA`.

## Límites conocidos

- **Baja contra escritores del libro.** La baja revisa `movcon` y `asiento` bajo el bloqueo de la fila del ejercicio, pero `saveAsiento`, `CompraService` y el resto de los escritores leen el ejercicio sin bloqueo y no hay FK desde esas tablas: un asiento confirmado entre la revisión y el `DELETE` quedaría sin ejercicio. La ventana es mínima (solo se borran ejercicios de año futuro). Igual con el bloqueo: un `saveAsiento` que ya leyó `bloqueado = 0` puede confirmarse después del PUT.
- **PUT con el objeto completo.** VB6 reenvía todos los campos y el servidor aplica `bloqueado` y las órdenes tal como llegan: un cliente con un estado viejo puede reabrir un ejercicio recién bloqueado o pisar una orden recién asignada. No hay control de versión.
- **Órdenes contables.** Se aceptan tal cual; `AsientoInflacionService` borra los movimientos de esa fecha y orden antes de regenerar, de modo que una orden apuntada a un asiento ajeno lo borraría (el PUT anterior ya lo permitía).
- **Nombre y fechas del alta y del PUT.** Se comparan con lo derivado o guardado. Los nombres de los ejercicios 4 a 15 de dev, creados por el alta de VB6, coinciden byte a byte con `Ejercicio Económico <año>` (la columna es `latin1`, `ó` = `F3`), y VB6 envía las fechas como `...T00:00:00+0000`; no se probó con el ejecutable de VB6.
- **Fin del índice.** El alta y la baja leen el último ejercicio con `ORDER BY ... LIMIT 1 FOR UPDATE`; una transacción que espera el bloqueo del último y reanuda cuando otra insertó un posterior podría decidir con el anterior. Los E2E de alta contra baja y de dos altas no lo reprodujeron; sin una prueba específica queda como riesgo residual.
- **Primer ejercicio.** Con la tabla vacía el alta exige un período regular (marzo a febrero); un último ejercicio heredado con otro período (por ejemplo, los tres primeros de dev) no se puede continuar por API.
- **Historial.** Las escrituras y `AsientoInflacionService` dependen de `gestion_escritura_historial` (#404, aplicada hoy solo en dev): sin la tabla fallan y revierten.

## Consumidores del flag `bloqueado`

Leen `Ejercicio.bloqueado` y lo respetan: `ContabilidadService.saveAsiento` (lanza `EjercicioBloqueadoException`), `ContabilidadService.deleteAsientoDesde` (devuelve `false` sin borrar) y `CompraService.anulateValor` (con el ejercicio bloqueado genera un contra-asiento en lugar de borrar el original). `AsientoInflacionService` ya no reescribe el ejercicio completo: usa `UpdateEjercicioUseCase` y manda solo la orden que cambia, así que no puede reabrir un ejercicio bloqueado ni deja de registrar historial.

Se corrigió un defecto de `EjercicioBloqueadoException`: su mensaje usaba `{}` en `MessageFormat`, y el constructor lanzaba `IllegalArgumentException` en vez de la excepción esperada (los `catch (EjercicioBloqueadoException)` de `CostoService` no podían alcanzarse). Ahora usa `{0}`.

## Diferencias frente a VB6

`frmEjercicios` exige `userService.autorizado` y, para bloquear/desbloquear, `usuarioApp.habilitaOPeliminacion`; además ignora la respuesta de `DELETE` salvo el mensaje de error. Core no replica esos controles de usuario (el servicio no tiene autenticación, riesgo aceptado del proyecto): quien llame a la API puede bloquear y desbloquear. VB6 consulta el flag en `clsCtlContable.saveAsiento` y `deleteAsientoDesde` y en `SMGestion.puedeGenerarOperacion`; esta última rama pide una clave que no está implementada. Core rechaza además el PUT con nombre o fechas distintos de lo guardado (antes los reemplazaba); si algún cliente depende de editar esos campos, el cambio es de contrato.

## Verificación

- `mvn test`: 1121 pruebas. El dominio, los casos de uso, los adaptadores, el controlador (409/404/400 con JSON) y la persistencia sobre H2 se prueban directamente; los tres puntos del flag se prueban con colaboradores simulados (Mockito).
- `EjercicioDevDbIT` (9 pruebas, MySQL dev por el relay, copias `TEMPORARY` y un inspector que prohíbe escribir en tablas reales): columnas reales, `FOR UPDATE` simple en MySQL 5.7 y `EXPLAIN` con índice en `movcon` y `asiento` para el rango por fecha.
- `EjercicioDevE2E` (14 pruebas, opt-in, siempre en local): `E2E_DEV_ESCRITURA=si`, `IT_DB_*` y `-Dit.test=EjercicioDevE2E`. Escribe en dev los ejercicios 16 y 17, filas marcadas `E2E-408-*` en `movcon` y `asiento`, una fila en `compra_referencia` (`importe` 408.08) y eventos de historial; aborta sin tocar nada si el 16 ya existe; al terminar borra lo propio, restaura los `AUTO_INCREMENT` e informa la comparación de `CHECKSUM`. Reales: MySQL, FK, locks, historial y el controlador (MockMvc standalone, sin Tomcat). Simulados: el reloj (fijo en 2026-10-21) y los colaboradores de `ContabilidadService` que no leen el flag. Cada concurrencia fuerza el cruce con una pausa del SQL y comprueba el `LOCK WAIT` en `INNODB_TRX`, y se repite con un mutante (lectura sin bloqueo) que tiene que perder. El flujo de `CompraService.anulateValor` y de `AsientoInflacionService` no tiene E2E (solo unitarias): necesitan movimientos de valores y vistas mensuales.
- No se probó con el ejecutable VB6.
