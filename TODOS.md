# TODOS

## Contabilidad

### Validar la jerarquía al imputar movimientos

**What:** En `CreateCuentaMovimientoUseCaseImpl` y en la edición de movimientos, rechazar la imputación a una cuenta que tiene cuentas hijas (o es integradora).

**Why:** El alta de cuentas (#407) controla que el padre no tenga movimientos solo en ese momento; después nada impide imputar un asiento al padre. La FK `movcon → plancta` no cubre esta regla.

**Pros:** Cierra en Java el invariante "las cuentas con hijas no reciben movimientos".

**Cons:** Cambia el comportamiento de los asientos que hoy se aceptan; VB6 escribe `movcon` directo y sigue sin control.

**Context:** Surgió en la revisión técnica de #407 (2026-10-16), hallazgo O-3: `CreateCuentaMovimientoUseCaseImpl.java:17` guarda sin validar jerarquía. Empezar por `CuentaJerarquia` (rango de descendientes) del slice `contable/cuenta`, que agrega #407. Es cross-slice (`cuentaMovimiento` → `cuenta`).

**Effort:** M
**Priority:** P2
**Depends on:** #407 mergeado; acuerdo en la revisión sobre asientos existentes y VB6.

### Migrar las lecturas de Bancaria al slice y retirar `Bancaria.kt`

**What:** Pasar `GET /bancaria/` y las lecturas de `BancariaService` (`CompraService`) y de `ValorMovimiento.bancaria` al slice `contable/bancaria`, y borrar `Bancaria.kt`, `BancariaRepository` y `BancariaService`.

**Why:** #409 mueve las escrituras al slice con `BancariaEntity` propia; el listado, el pago de compras y el JSON de valores siguen leyendo con la entidad Kotlin, y quedan dos clases (más dos de lectura mínima para `valores` y `libro_banco`) sobre las mismas tablas.

**Pros:** Un solo camino de acceso y una sola entidad por tabla.

**Cons:** Toca `CompraService`, que tiene poca cobertura de tests, y el JSON anidado que lee VB6.

**Context:** Decidido en la revisión de #409 (2026-10-23). Empezar por las pruebas de regresión de #409 (`BancariaControllerTest`, `CompraServiceAddValoresBancariaTest`, `BancariaPersistenceTest`).

**Effort:** L
**Priority:** P2
**Depends on:** #409 mergeado; desglose de Compras y Libro Banco de #403.

### Migrar las lecturas de Ejercicio al slice y retirar `Ejercicio.kt`

**What:** Pasar `GET /ejercicio`, `/{id}`, `/last` y `/fecha/{fecha}` al slice `contable/ejercicio`, migrar los consumidores de `EjercicioService` (`ContabilidadService`, `CompraService`, `SheetService`, `CostoService`) y borrar `EjercicioService`, `EjercicioRepository` y `kotlin/model/Ejercicio.kt`.

**Why:** #408 mueve las escrituras al slice con `EjercicioEntity` propia; las lecturas siguen en legacy y dos clases mapean `ejercicios`.

**Pros:** Un solo camino de acceso y una sola entidad.

**Cons:** Toca servicios grandes de contabilidad y compras con poca cobertura de tests.

**Context:** Decidido en la revisión de #408 (2026-10-21, D13). Empezar por tests de los consumidores del flag `bloqueado` que agrega #408.

**Effort:** L
**Priority:** P2
**Depends on:** #408 mergeado; desglose de Contabilidad de #403.

### Revisar el SQL directo de `EjercicioActualJdbcAdapter`

**What:** `compras/pedidos/compraPedido/.../EjercicioActualJdbcAdapter` resuelve el ejercicio vigente con `JdbcTemplate` y SQL a mano.

**Why:** Se resolvió así para no acoplar el slice de Compras al modelo legacy. Cuando exista el slice `contable/ejercicio`, definir con el equipo si Compras debe consultarlo y así evitar SQL nativo ante la migración a Postgres.

**Pros:** Menos SQL nativo.

**Cons:** Cruce entre slices Compras → Ejercicio.

**Context:** Surgió en la revisión de #408 (D12). No se toca en #408.

**Effort:** S
**Priority:** P3
**Depends on:** #408 mergeado; acuerdo del equipo.

### Agregar la FK de `compra_adjudicacion.ejercicio_id`

**What:** `compra_adjudicacion.ejercicio_id` no tiene FK hacia `ejercicios` (las otras 9 tablas con `ejercicio_id` sí, con `RESTRICT`).

**Why:** La baja de un ejercicio no la ve como referencia; la baja de #408 solo traduce el rechazo de integridad de las FKs existentes.

**Pros:** Cierra el único hueco de referencias con FK conocido.

**Cons:** Es un cambio de esquema desde el repo de Compras; verificar datos huérfanos antes.

**Context:** Medido en dev el 2026-10-21 (1 fila, ejercicio 15). Propuesto en la revisión de #408 (D6).

**Effort:** S
**Priority:** P3
**Depends on:** #408 mergeado.

## Privacidad

### Quitar datos personales de los logs existentes

**What:** Revisar los `log.*` que escriben `personaId` o `documentoId` y dejar solo identificadores que no revelen a la persona.

**Why:** Hay unos 10 logs con esos datos (slices `personas/domicilio`, `umhub/consulta` y `chequera/chequeraSerie`). El repositorio es público y los logs pueden llegar a sistemas compartidos. Los logs nuevos de materias inscriptas (#487) no los escriben.

**Pros:** Menos exposición de datos personales en logs y en capturas de soporte.

**Cons:** Algunos logs sirven hoy para diagnóstico; hay que decidir qué identificador los reemplaza.

**Context:** Surgió en la revisión de #487 (D14). #487 no limpia estos logs. Empezar con `grep -rn "log\." src/main | grep -i persona`.

**Effort:** S
**Priority:** P3
**Depends on:** acuerdo con Daniel sobre el identificador de reemplazo.

## Completed
