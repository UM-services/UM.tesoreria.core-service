# Plan de Cuentas — issue #407

El servicio decide la jerarquía y las restricciones de escritura. El cliente conserva las rutas y los campos existentes. No se agrega gating de permisos ni cambios de esquema. Las escrituras requieren la tabla `gestion_escritura_historial` de #404.

## Jerarquía y escrituras

Los números son enteros de once dígitos, agrupados como `1-2-2-2-4`. Si un grupo es cero, todos los inferiores deben ser cero. El padre se obtiene anulando el último grupo no nulo. Los ancestros se bloquean de raíz a padre antes de consultar movimientos o escribir.

El rango incluye la propia cuenta y termina según su grado: `n + 10^k - 1`, con `k = 10/8/6/4/0`. Por ejemplo, `11010100000` termina en `11010109999`, sin incluir la hermana `11010110000`. No se recortan ceros finales. El ejemplo `11010010001` del plan de pruebas previo es **válido** (`1-10-10-01-0001`); un caso inválido equivalente es `11000010001` (`1-10-00-01-0001`).

- `POST /cuenta/`: rechaza número inválido, duplicado, ancestro ausente o con movimientos. Calcula grados y marca integradoras en los ancestros que lo necesiten. Alta, integradoras e historial se confirman o revierten juntos. La persistencia usa `persist` para impedir sobrescrituras en carreras.
- `PUT /cuenta/{numeroCuenta}`: bloquea y fusiona. Un valor nulo conserva lo guardado; un nombre vacío rechaza. `geograficaId=0` quita la sede y guarda `NULL`. El número de la ruta y el id contable guardado no cambian. Grados enviados se ignoran y se recalculan. Con hijas, `integradora` queda en 1. Editar una cuenta con movimientos está permitido. `fechaBloqueo` nula conserva: esta operación no permite vaciarla.
- `DELETE /cuenta/{numeroCuenta}`: bloquea antes de comprobar hijas y movimientos en todo el rango (sin depender de que exista una fila de cuenta por cada movimiento). Si tiene hijas, movimientos o una referencia protegida por FK, rechaza y revierte también el historial. Una inexistente conserva `204` vacío.
- `GET /cuenta/sede/{geograficaId}/grado/{grado}`: coincidencia exacta de ambos campos, ordenada por número. No incluye filtro por rama.
- `GET /cuenta/recalculagrados`: conserva `Ok`; modifica entidades gestionadas en una transacción, sin `saveAll` ni merge. El balance delega este recálculo y limpia los nombres con `\r\n` mediante otra operación gestionada: lee los candidatos con bloqueo de escritura, en orden numérico, y conserva el bloqueo hasta confirmar para no perder ediciones concurrentes. La detección de cambios y `@DynamicUpdate` evitan actualizaciones innecesarias y reinserciones.

Todas las rutas anteriores tienen también el alias `/api/tesoreria/core/cuenta`. Los rechazos de regla responden `409` con `{status:409,error:"Conflict",message:"..."}`. POST/PUT exitosos conservan `200`; PUT inexistente conserva `404`.

## Compatibilidad y observaciones de VB6

El control de baja de VB6 construye un tope de **10** dígitos para cuentas de **11**: queda un rango vacío y no bloquea movimientos. Core aplica la restricción intencionada con un rango calculado por grado.

VB6 toma un cuerpo vacío como éxito, por lo que el `409` siempre lleva JSON. Sin embargo, `cmdEliminar` ignora la respuesta del DELETE: una baja rechazada deja la cuenta visible. Además, VB6 sigue llamando a `saveIntegradoras` aunque falle el alta. Esos dos comportamientos del cliente quedan fuera de #407.

El POST conserva el comportamiento actual de no recuperar de la base el `cuentaContableId` generado por `AUTO_INCREMENT`; los clientes deben consultar la cuenta para obtenerlo. No se expone autocompletar ni se renombran cuentas.

## Datos de desarrollo relevados en T0 (2026-10-16)

Estas observaciones corresponden a dev; no se asumen para producción:

- `plancta`: 2611 filas, sin grados cero/inconsistentes, padres ausentes ni movimientos huérfanos. Sede real `geografica_id`: 1980 valores `NULL`, ninguno en cero; sedes 1–7.
- «Falta Cuenta»: `10402980000`, `20901000000`, `20901010000`, `40106980000`. Las que tienen hijas e integradora cero, junto con `40101810000`, pasan a integradora 1 la próxima vez que se editen. No se corrigen masivamente.
- `30104030000` tiene movimientos propios e hijas: se puede editar, no borrar ni agregar nuevas hijas bajo esa rama.
- `movcon_ibfk_2` (`MCo_Pla_Cuenta → plancta`) es RESTRICT y cierra la carrera entre baja e inserción externa de movimientos. Hay otras 15 referencias RESTRICT hacia `plancta`. `movcon` tiene índices que empiezan por `mco_pla_cuenta`.
- La raíz 9 estaba libre para el E2E. El test vuelve a comprobarlo antes de escribir y reserva la corrida con `GET_LOCK`.

## Verificación

Pruebas locales: `mvn -B verify`. `CuentaJerarquiaTest` cubre los cinco grados, límites, números inválidos, escalas equivalentes, padre y ancestros. Los casos de uso prueban orden de bloqueo, fusión y rechazo sin escrituras. `CuentaControllerTest` verifica ambos alias, códigos y cuerpos para VB6. `CuentaPersistenceTest` verifica JPA/transacciones/historial reales en H2, incluyendo rollback de la segunda integradora y recálculo sin reinserciones.

`CuentaDevDbIT` ejecuta los mismos casos más movimiento huérfano sobre copias `TEMPORARY` de `plancta`, `movcon` y `gestion_escritura_historial`, con pool de una conexión, comprobación de tablas temporales e inspector de escrituras. Las copias no conservan las FK. Se ejecuta a mano con el perfil `it` y `IT_DB_*`; no se incluye una contraseña en el repositorio.

`CuentasConcurrenciaDevE2E` está desactivado por defecto. Requiere `E2E_DEV_ESCRITURA=si`, `IT_DB_*` y `-Dit.test=CuentasConcurrenciaDevE2E`. Escribe en tablas reales, solo en la raíz 9 y en un comprobante sintético reservado (`1901-01-01`, n.º 9407) que ninguna otra tabla referencia, con filas marcadas `E2E-407-*`. Cada caso fuerza el cruce que prueba: una operación queda pausada (inspector de SQL) o bloqueada por otra conexión y se comprueba en `information_schema` (`INNODB_TRX`, `INNODB_LOCK_WAITS`) que la otra queda en `LOCK WAIT` antes de soltarla. Ocho casos: baja de padre frente a alta de hija en ambos órdenes (con el motivo exacto del rechazo); asiento de otro escritor en la ventana entre los controles de la baja y su `DELETE` (la FK lo rechaza); referencia con FK real en otra tabla (`cuenta_movimiento_saldo`) → `409` sin baja ni evento de historial; baja durante el recálculo sin reinserción; edición confirmada durante el recálculo sin pérdida de campos; dos altas hermanas serializadas; dos altas de la misma raíz (una gana, la otra `409`, sin pisar la ganadora). La limpieza borra solo filas de prueba y recupera residuos de corridas interrumpidas; si la raíz 9 tiene cuentas que no son de prueba, no toca nada. La foto global (CHECKSUM, filas, AUTO_INCREMENT) se informa pero no hace fallar la prueba, porque la base de dev es compartida.

Verificado el 2026-10-20: `mvn test` pasó con **1007 pruebas**, sin fallos ni errores; el diagrama Mermaid validó y se renderizó. `CuentaDevDbIT` pasó por el relay local con **10 pruebas ejecutadas y 2 omitidas** (2026-10-17; los dos casos de dos conexiones son propios de H2, incompatibles con tablas TEMPORARY de una sola sesión). Los **8 E2E de concurrencia pasaron en dev por el relay** (2026-10-17) y la base quedó idéntica (CHECKSUM, filas y AUTO_INCREMENT de `plancta`, `movcon`, `cuenta_movimiento_saldo` y `gestion_escritura_historial`). Se comprobó que detectan lo que protegen introduciendo cada regresión: baja sin bloqueo (fallan la ventana y la baja frente al alta de hija), alta sin bloqueo de ancestros (fallan la baja de padre frente al alta y las altas hermanas, que se interbloquean), recálculo sin `@DynamicUpdate` (se pierde la edición concurrente) y recálculo con lectura y guardado en transacciones distintas (la cuenta borrada se reinserta). No quedaron datos de prueba.

## Release

Se prepara `9.7.0` según T8 del plan revisado: consulta aditiva y nuevas restricciones de negocio con `409`, sin retirar URL ni campos. La clasificación SemVer se confirma en la revisión del PR.

La regresión `CuentaPersistenceTest.sumasSaldosConservaSalidaTrasRecalcularGrados` abre el XLSX real y compara fechas, columnas, orden, nombres y saldos deudores/acreedores/cero antes y después de reparar grados. Usa persistencia y recálculo reales; los importes y el ejercicio son un fixture controlado. La prueba de limpieza con dos conexiones H2 comprueba el bloqueo y una edición auditada posterior; no se ejecuta contra tablas reales de dev.
