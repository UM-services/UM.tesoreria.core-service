# Spec — Beneficio por requisitos de ingreso en cuotas preuniversitarias

> Especificación ejecutable de **una** feature. El orden de trabajo del repositorio
> completo vive en `../../roadmap.md`.
>
> Versión del servicio al redactar: **4.1.1** · Fecha: **2026-08-19** · Estado: **implementación local en validación; no lista para rollout**

## Estado de implementación — Issue #338 (2026-08-20)

Esta sección registra evidencia del estado real del workspace. No sustituye los
criterios de aceptación ni habilita el rollout: una fase sólo se marca completa
cuando se cumple íntegramente su apartado **Listo cuando**.

| Fase | Estado | Evidencia local | Pendiente para cerrar |
|---|---|---|---|
| F0 — validación y conflicto | Parcial | Validaciones `@Valid`/rango y 409 implementadas; los tests de controller cubren `0`/`0.50`/`1.00`, fuera de rango, escala mayor a dos decimales, negativo y nulos **sobre POST y PUT**, más el duplicado a 409 en el alta. | Verificar los mensajes HTTP legibles del 400 contra el formato que espera el cliente. |
| F1 — caracterización | Parcial | `PreuniversitarioChequeraDetailsCreatorTest` cubre precio de lista/originales/barcode, alternativas, total, vencimientos vencidos, offset y lista vacía. | No puede demostrarse retrospectivamente la condición «antes de tocar producción»; faltan T1–T5 y los siete casos equivalentes de Spoter como contrato histórico. El test existente fija el orden **nuevo** (alternativas → cuotas → totales), no el que T4 debía congelar. |
| F2 — factory | Implementada, pendiente de contrato F1 | `ChequeraCuotaFactory` es usada por preuniversitario y Spoter; pruebas de `0`/`0.20`/`0.33`/`1.00`, HALF_UP, originales, vencimientos y datos incompletos pasan. El predicado de vencimiento (`vencio`) vive en la factory, así que los dos consumidores comparten la misma regla. | Validarla contra la suite histórica completa e inmutable de F1. |
| F3 — cálculo y persistencia | Implementada localmente, bloqueada para rollout | Política MAX filtrada por ingreso/activo, `becaPorcentaje`, factor en tres tramos, total de cuotas activas y fallbacks con `warn`. Los productos de `chequera_total` salen de `lectivo_total` unidos a los que generaron cuotas, así una alternativa sin cuotas para un producto conserva su fila en cero. | Confirmar en staging que `requisitoRel` llega poblado y ejecutar la matriz 0 %/parcial/100 % con payload real. |
| F4 — inconsistencias | Completada localmente, bloqueada para rollout | Detector null-safe; pruebas para parcial, un tramo inválido, 0 %, 100 %, máximo y originales nulos, más endpoint local sin falsos positivos. | Ejecutar el endpoint sobre chequeras bonificadas en staging y confirmar que no aparezcan inconsistencias nuevas. **Los campos nulos ahora se reportan como inconsistentes** (antes lanzaban `NullPointerException`): medir el volumen sobre datos históricos antes de exponer el endpoint. |
| F6-Core — calidad | Parcial | Tests de política, factory, servicio, cinco endpoints de beneficios, `ChequeraCuotaController` (JSON bonificado/original e ISO), inconsistencias, alta 100 % con `send-chequera`, Spoter sin beneficio, productos sin cuotas por alternativa e importes nulos. JaCoCo con `check` efectivo. | Falta la integración `AlumnoGuaraniController` y la regresión end-to-end de alta → cuotas → inconsistencias → evento, que requiere el payload/infraestructura de staging. |
| Documentación | Completada | `docs/hexagonal-beneficioCuota.mmd`, registrado en `docs/script.js`, `docs/index.html` y `docs/README.md`. | — |

**Resultado de la validación local:** `mvn -B verify` pasó el 2026-08-20 con
111 tests, 0 fallos y el check JaCoCo activo.

**Corrección del gate de cobertura (2026-08-20).** La primera versión de la
`<execution>` usaba `<element>BUNDLE</element>` con `<includes>` en formato de
ruta (`um/tesoreria/...`). En JaCoCo los `<includes>` de una `<rule>` matchean el
**nombre del elemento**, y para `BUNDLE` ese nombre es el del bundle
(`um.tesoreria.core-service`): los cinco patrones no matcheaban nada, la regla se
evaluaba sobre cero elementos y el check pasaba siempre. Verificado subiendo el
mínimo a 0,99 sin que el build fallara, con la cobertura del bundle completo en
7,71 %. Corregido a `<element>CLASS</element>` con nombres separados por
**puntos**; comprobado que a 0,99 la regla dispara sobre
`PreuniversitarioChequeraService` (0,82) y que a 0,80 pasa.

**Gates externos pendientes:** payload de staging con `requisitoRel`, endpoint de
inconsistencias en staging y conciliación de 0 %/parcial/100 %. No se modificaron
sender-service, PDF ni correo, que continúan fuera del alcance de #338.
> Decisiones de negocio: `decisiones.md` (D1–D8)

---

## Objetivo

**Aplicar el beneficio por requisitos de ingreso cargados en Guaraní al cálculo de importes de cuota para alumnos preuniversitarios, en `core-service`.**

Todo lo que no sirva a ese objetivo queda fuera. La deuda técnica general del repositorio está documentada en `../../tech-stack.md` (§6, huecos H1–H8) pero **no priorizada**: son huecos reconocidos, no fases activas.

## Contexto

Guaraní registra los requisitos de ingreso que presenta cada aspirante. Tesorería definió que ciertos requisitos otorgan un porcentaje de bonificación sobre el arancel. Ese porcentaje **hoy no llega a ningún importe**: se puede cargar por REST en `guarani_beneficio`, pero ningún componente del servicio lo lee.

El resultado es que un ingresante con derecho a beneficio recibe una chequera al precio de lista. La corrección es manual, o no ocurre.

El módulo `GuaraniBeneficio` se construyó en la 3.45.0 y se le agregó la consulta por múltiples requisitos en la 4.1.0. La infraestructura está; falta el cableado.

## Estado actual (verificado sobre 4.1.1)

### El módulo existe y está aislado

`GuaraniBeneficio` tiene dominio, 5 casos de uso, adaptador JPA sobre `guarani_beneficio` y controller con 5 endpoints. **Ninguna clase fuera de su propio paquete lo referencia.**

### Landscape de los mecanismos de descuento

| Mecanismo | Modelado | Persistido | Aplicado a importes |
|---|---|---|---|
| `GuaraniBeneficio` (requisito → %) | ✅ | ✅ | ❌ |
| `ArancelPorcentaje` (arancelTipo+producto → %) | ✅ | ✅ | ❌ |
| `ChequeraSerie.becaPorcentaje` | ✅ | ✅ | ❌ hardcodeado en `ZERO` |

Tres mecanismos, cero aplicados. Esta feature cablea el primero usando el tercero como almacenamiento.

### El hook ya está reservado

`PreuniversitarioChequeraService.java:49-50` tiene el lugar exacto, con comentario y todo. Fluye a `policy.createSerie(data, control, becaPorcentaje)` (línea 58) → `ChequeraSerie.becaPorcentaje` → `ChequeraService.track():190` → `ChequeraImpresionCabecera`. La cadena hasta impresión está completa.

### Los importes se copian sin descuento, en dos lugares casi idénticos

| Ubicación | Qué hace |
|---|---|
| `PreuniversitarioChequeraDetailsCreator.createCuotas()` | Copia `importe1/2/3` de `LectivoCuota`; fija `importeN` e `importeNOriginal` **iguales** |
| `SpoterService.java:180-206` | El mismo bloque duplicado |

### El total ya es inconsistente, antes de esta feature

`createTotals()` copia `LectivoTotal.getTotal()`. Pero `PagoService.calcularPagado():214` lo reasigna a `Σ importe1` de las cuotas activas al primer pago. Si difieren, el total cambia en silencio.

### Cobertura del camino

19 archivos de test en todo el repo. Sobre este camino: **cero**.

## Cambio propuesto

```
AlumnoGuaraniRequest.personaRel.requisitosPresentados
        │
        ▼
BeneficioPolicy.porcentajeEfectivo(requisitos, beneficios)   ← nuevo, dominio puro
        │   filtra requisitoIngreso='S' AND activo='S', toma MAX
        ▼
PreuniversitarioChequeraService:49-50   ← hook existente
        │
        ▼
ChequeraSerie.becaPorcentaje   ← congelado al emitir
        │
        ▼
ChequeraCuotaFactory   ← nuevo, compartido con SpoterService
        │   importeN = importeNLista × (1 - becaPorcentaje), scale 0 HALF_UP
        │   importeNOriginal = importeNLista
        ▼
createTotals(chequeraSerie, cuotas)   ← total = Σ importe1, orden invertido
```

### Fórmula

```
beneficioAplicado = MAX(porcentajeBeneficio de requisitos con requisitoIngreso='S' y activo='S')
importeN          = importeNLista × (1 - beneficioAplicado)  .setScale(0, HALF_UP)
importeNOriginal  = importeNLista
total             = Σ importe1 de cuotas activas, por productoId
```

Los tres tramos reciben el mismo factor. Sin requisitos elegibles → 0 % → comportamiento idéntico al actual. Justificación de cada decisión en `decisiones.md`.

## Criterios de aceptación

1. `POST` y `PUT /guaraniBeneficio` aceptan `porcentajeBeneficio` en `[0, 1]` inclusive, con hasta dos decimales, y devuelven **400** fuera de rango, con `requisito` nulo, o con porcentaje nulo.
2. `POST /guaraniBeneficio/` sobre un requisito ya cargado devuelve **409**, no 500.
3. Alumno sin requisitos elegibles → `importe1/2/3`, `importe1/2/3Original`, `vencimiento1/2/3`, `codigoBarras` y `chequera_total.total` **idénticos** a los valores que fijan los tests de caracterización de F1 (`PreuniversitarioChequeraDetailsCreatorTest`, casos 1-8 y T1-T5), sobre las mismas fixtures. Ningún test de F1 puede modificarse para que este criterio pase.
4. Alumno con requisitos de `0.10`, `0.30` y `0.20` elegibles → `becaPorcentaje = 0.30` en `ChequeraSerie`.
5. Requisito con `requisitoIngreso = 'N'` o `activo = 'N'` → excluido, aunque tenga el porcentaje más alto.
6. `requisitoRel` nulo → excluido, con `log.warn` que incluya `persona` y `requisito`.
7. Los tres tramos de cada cuota reflejan el mismo factor, redondeados a entero HALF_UP.
8. `importeNOriginal` conserva el precio de lista en las tres posiciones.
9. `chequera_total.total` es **exactamente** `Σ importe1` de las cuotas activas de ese `productoId`.
10. Tras `PagoService.calcularPagado()`, `chequera_total.total` **no cambia**.
11. Beneficio 100 % → cuotas y total en cero; `FindAllInconsistenciasUseCaseImpl` no reporta la chequera.
12. `GET /chequeraCuota/inconsistencias/{desde}/{hasta}` no reporta falsos positivos sobre chequeras bonificadas.
13. Una chequera creada por `SpoterService` no recibe beneficio.
14. Ante cualquiera de estas fallas, la chequera se emite con `becaPorcentaje = 0` y se registra `log.warn` con `persona`, `documentoId` y la causa; **nunca** se aborta la emisión ni se propaga la excepción:
    - `personaRel` nulo en el `PersonalesResponse` recibido
    - `requisitosPresentados` nulo o lista vacía
    - `requisitoRel` nulo en uno o más elementos (se excluye ese requisito, no la chequera)
    - `GuaraniBeneficioRepository.findByRequisitos(...)` lanza excepción de persistencia
    - `porcentajeBeneficio` nulo en un registro de `guarani_beneficio` (se trata como cero)
15. Tests escritos y pasando; sin degradación de funcionalidad existente.

## Plan de tests

| Capa | Qué | Count |
|---|---|---|
| Unit | `BeneficioPolicy` (12 casos, 100 % de ramas) | +12 |
| Unit | `ChequeraCuotaFactory`: vencimientos, offset, factor, redondeo, originales | +11 |
| Unit | `createTotals`: suma, multi-producto, baja, 100 %, resto de redondeo | +7 |
| Unit | `GuaraniBeneficio` use cases + mappers | +5 |
| Unit | `FindAllInconsistenciasUseCaseImpl` con cuotas bonificadas | +6 |
| Integration | `PreuniversitarioChequeraService` end-to-end con y sin beneficio | +4 |
| Integration | `SpoterService` sin beneficio (no regresión) | +2 |
| Controller | `GuaraniBeneficioController` validación + `ChequeraCuotaController` | +12 |

Total: **+59 tests**.

**Umbral de cobertura:** `jacoco-maven-plugin` 0.8.13 ya está en `pom.xml` con `prepare-agent` y `report`, pero **sin** goal `check` ni `<rules>`, así que hoy nada rompe el build por cobertura. Se agrega una `<execution>` con goal `check` y una `<rule>` de `BUNDLE` con `LINE` al 80 %, limitada vía `<includes>` a `um/tesoreria/core/hexagonal/guarani/guaraniBeneficio/**`, `um/tesoreria/core/hexagonal/chequera/chequeraCuota/**` y `um/tesoreria/core/hexagonal/chequera/chequeraSerie/**`. El resto del proyecto queda sin umbral para no frenar PRs ajenos. `mvn -B verify` en `maven.yml` debe fallar si esos tres paquetes bajan del 80 %.

## Plan de rollback

El beneficio se aplica solo en **creación**; las chequeras existentes no se tocan. Revertir el PR restituye el comportamiento anterior para chequeras nuevas. Las emitidas con beneficio quedan con `becaPorcentaje` grabado y sus importes persistidos: revertirlas requiere baja y reemisión, que es el flujo manual que tesorería ya usa.

El cambio de `createTotals()` es el único que altera semántica de datos; es aditivo sobre chequeras nuevas y no reescribe históricos.

## Hitos

| Hito | Resultado | Dependencia |
|---|---|---|
| 1. Configuración y contrato actual | Validación, conflicto 409 y tests de caracterización | — |
| 2. Cálculo persistido | Factory, política MAX, cuotas, totales e inconsistencias | Hito 1 y payload validado en staging |
| 3. Cierre de cálculo | Controllers, JaCoCo, diagrama y regresión de `send-chequera` | Hito 2 |
| 4. Documento con beneficios | Contrato Core → sender, PDF 0/30/100 y barcode acordado | Hito 3 desplegado |
| 5. Aviso complementario | Mail específico de beca completa, si el equipo lo aprueba | Hito 4 y contenido definido |

## Archivos afectados

| Archivo | Cambio |
|---|---|
| `src/main/java/um/tesoreria/core/hexagonal/guarani/guaraniBeneficio/infrastructure/web/dto/GuaraniBeneficioRequest.java` | `@NotNull`, `@DecimalMin("0")`, `@DecimalMax("1")`, `@Digits(integer=1, fraction=2)` |
| `src/main/java/um/tesoreria/core/hexagonal/guarani/guaraniBeneficio/infrastructure/web/controller/GuaraniBeneficioController.java:53,60` | `@Valid` en POST y PUT |
| `src/main/java/um/tesoreria/core/hexagonal/guarani/guaraniBeneficio/application/usecases/CreateGuaraniBeneficioUseCaseImpl.java` | Duplicado → 409 |
| `src/main/java/um/tesoreria/core/hexagonal/guarani/guaraniBeneficio/domain/policy/BeneficioPolicy.java` | **Nuevo** |
| `src/main/java/um/tesoreria/core/hexagonal/chequera/chequeraCuota/application/factory/ChequeraCuotaFactory.java` | **Nuevo** |
| `src/main/java/um/tesoreria/core/hexagonal/chequera/chequeraSerie/application/service/PreuniversitarioChequeraService.java:49-50` | Llamada a `BeneficioPolicy` en el hook |
| `src/main/java/um/tesoreria/core/hexagonal/chequera/chequeraSerie/application/service/PreuniversitarioChequeraDetailsCreator.java` | Usa la factory; invierte orden; `createTotals` suma cuotas |
| `src/main/java/um/tesoreria/core/service/transactional/spoter/SpoterService.java:180-206` | Usa la factory |
| `docs/hexagonal-beneficioCuota.mmd` | **Nuevo** diagrama, registrado en `docs/script.js` |

## Cómo leer el plan de implementación

- Ocho fases, en orden estricto. Cada una es **un PR**.
- Cada fase deja el repositorio en verde y desplegable.
- `[ ]` pendiente · `[~]` en curso · `[x]` hecho.

## Fuera de alcance — deuda conocida, no fases activas

| Tema | Por qué queda afuera |
|---|---|
| `SpoterService` más allá de extraer la factory | Se toca en F2 solo para que consuma la factory; su migración a hexagonal no entra |
| `RecalculateCuotaByUniqueIndexUseCaseImpl` | El recálculo por política arancelaria es otro camino de cálculo; no se cablea beneficio acá |
| Todo lo de VB6 | Sistema externo, fuera de este servicio |
| `ArancelPorcentaje` sin consumidores | Segunda isla de descuentos; se anota, no se resuelve |
| Cobertura general, Flyway, cierre del legacy | H1, H2, H3 en `../../tech-stack.md` |
| Corrupción silenciosa del código de barras | H7 en `../../tech-stack.md`. La feature lo evita por construcción, pero no lo corrige |

---

## Corrección de referencias

Las líneas que se citaron al definir esta tarea apuntan a un estado anterior del código. Mapeo verificado al 4.1.1:

| Referencia original | Ubicación real hoy |
|---|---|
| `PreuniversitarioChequeraService` líneas 218-242 | `PreuniversitarioChequeraDetailsCreator.createCuotas()` — el archivo original quedó en 76 líneas tras el refactor de la 4.1.0 que separó el flujo preuniversitario en componentes |
| `SpoterService` líneas 179-203 | `SpoterService` líneas **180-206**, bloque `// Generar Cuotas` |

**Hallazgo que justifica la fase 2:** los dos bloques son casi idénticos — mismo cálculo de vencimientos con offset de 30 días cuando la cuota ya venció, misma construcción de `ChequeraCuota` con constructor posicional de más de 30 argumentos, misma llamada a `calculateCodigoBarras`. La duplicación es literal, no conceptual.

---

## F0 — Validación de entrada en el alta de `guarani_beneficio` `[ ]`

**Integridad de datos, no seguridad.** Un typo no puede poder cargar 500%.

Sin dependencias: se puede hacer y desplegar hoy, en paralelo con F1.

### Contexto

Hoy no hay ninguna validación en el camino de alta, verificado en tres capas:

```java
// GuaraniBeneficioRequest — dos campos, cero anotaciones
private Integer requisito;
private BigDecimal porcentajeBeneficio;

// GuaraniBeneficioController:53 — sin @Valid
public ResponseEntity<GuaraniBeneficioResponse> add(@RequestBody GuaraniBeneficioRequest request)

// CreateGuaraniBeneficioUseCaseImpl — sin validación
public GuaraniBeneficio create(GuaraniBeneficio g) { return repository.save(g); }
```

`AuthController` **sí** usa `@Valid` en su `LoginRequest`, así que la omisión no es convención del proyecto.

Esta validación es la barrera efectiva contra un error de carga. Eso la vuelve más importante, no menos.

### Alcance

**a) DTO** — `GuaraniBeneficioRequest`:
```java
@NotNull
private Integer requisito;

@NotNull
@DecimalMin(value = "0",   inclusive = true)
@DecimalMax(value = "1", inclusive = true)
@Digits(integer = 1, fraction = 2)
private BigDecimal porcentajeBeneficio;
```

**b) Controller** — `@Valid` en `POST /` (línea 53) **y** en `PUT /requisito/{requisito}` (línea 60). El `PUT` importa igual que el alta: hoy permite llevar a 500% un beneficio ya cargado.

**c) Manejo de duplicado** — `create()` hace `save()` directo; si el requisito ya tiene beneficio, la unique constraint de `guarani_beneficio` tira excepción de base sin manejar → **500 crudo**. Traducir a `409 Conflict` con `GuaraniBeneficioException`.

**d) Tests** — `GuaraniBeneficioControllerTest` (`@WebMvcTest`): `porcentajeBeneficio` en `0` → aceptado; en `0.50` → aceptado; en **`1.00` → aceptado**; en `1.01`, `50` o con más de dos decimales → **400**; negativo → **400**; nulo → **400**; `requisito` nulo → **400**; requisito duplicado → **409**. Mismos casos sobre el `PUT`.

### Nota sobre la escala (confirmada contra la base real el 2026-08-20)

El rango persistido es `[0, 1]` **inclusive**, con dos decimales: `0.50` representa 50 % y `1.00`, 100 %. Tanto `guarani_beneficio.porcentaje_beneficio` como `chequera_serie.beca_porcentaje` son `DECIMAL(5,2)` y los datos reales usan esa escala fraccional.

Con `MAX` el resultado nunca supera el mayor de sus entradas, así que no hace falta ningún tope adicional. Y permitir `1.00` es necesario, porque la chequera debe confirmar que no queda deuda.

### Listo cuando
Los casos pasan, `1.00` se acepta, y `POST` / `PUT` con porcentaje fuera de `[0, 1]` o con más de dos decimales devuelven 400 con mensaje legible.

### Gate de escala antes de desplegar

La escala es parte del contrato de datos: `0.50` significa 50 %, no 0,50 %. Antes de promover el cambio, verificar en la base objetivo:

```sql
SELECT MIN(porcentaje_beneficio), MAX(porcentaje_beneficio),
       SUM(porcentaje_beneficio < 0 OR porcentaje_beneficio > 1) AS fuera_de_rango
FROM guarani_beneficio;

SELECT MIN(beca_porcentaje), MAX(beca_porcentaje),
       SUM(beca_porcentaje < 0 OR beca_porcentaje > 1) AS fuera_de_rango
FROM chequera_serie;
```

Ambas consultas deben informar `fuera_de_rango = 0`. Confirmar además que las dos columnas siguen como `DECIMAL(5,2)`, que Sender muestra el porcentaje multiplicando el valor por 100 y que una emisión de prueba con `0.50` cobra exactamente la mitad de cada tramo.

---

## F1 — Tests de caracterización `[ ]`

Documentar el comportamiento actual **antes de tocar nada**. Estos tests son el contrato contra el que se verifica F2.

### Alcance

**`PreuniversitarioChequeraDetailsCreatorTest`**

| # | Escenario | Esperado |
|---|---|---|
| 1 | `LectivoCuota` con `vencimiento1` futuro | Vencimientos copiados tal cual desde `LectivoCuota` |
| 2 | `vencimiento1` ya vencido | Vencimientos reemplazados por `dateAbsoluteArgentina() + 7/20/40` días |
| 3 | Varias cuotas vencidas | El `offset` incrementa 30 días por cuota — **solo** para las vencidas |
| 4 | Mezcla de vencidas y no vencidas | El `offset` no se incrementa en las no vencidas |
| 5 | Cualquier caso | `importe1/2/3` **y** `importe1/2/3Original` reciben el mismo valor de `LectivoCuota` |
| 6 | Cualquier caso | `arancelTipoId` viene de `chequeraSerie`, no de `LectivoCuota` |
| 7 | Cualquier caso | `codigoBarras` se calcula después de fijar importes; flags `pagado`/`baja`/`manual`/`compensada` en 0 |
| 8 | Sin `LectivoCuota` | `saveAll` con lista vacía, sin excepción |

**`createTotals()` necesita cobertura real, no un caso de cortesía.** F3(f) le cambia tanto la fuente del dato como su posición en `create()`, así que hay que congelar antes:

| # | Escenario | Esperado hoy |
|---|---|---|
| T1 | Un producto | `total` = `LectivoTotal.getTotal()` copiado tal cual |
| T2 | Varios productos | Un `ChequeraTotal` por `productoId` |
| T3 | Cualquier caso | `pagado` inicializa en `BigDecimal.ZERO` |
| T4 | Cualquier caso | Se ejecuta **antes** que `createCuotas()` |
| T5 | Sin `LectivoTotal` | `saveAll` con lista vacía, sin excepción |

T4 documenta el orden actual justamente porque F3(f) lo invierte.

Añadir además un caso de caracterización sobre `createAlternatives()`, que no cambia pero comparte el mismo `create()`.

**`SpoterServiceTest`** — acotado al bloque `// Generar Cuotas` (líneas 180-206): repetir los casos 1 a 7. Es deliberadamente redundante: si F2 hace divergir los dos caminos, estos tests lo detectan.

**Punto crítico del caso 5:** hoy `importeN` e `importeNOriginal` nacen **idénticos**. Ese es exactamente el invariante que F3 va a romper a propósito, y el que F4 verifica. Dejarlo escrito y explícito acá.

### Listo cuando
Los tests pasan sin modificar una línea de producción. Si hubo que tocar producción para poder testear, es refactor y pertenece a F2.

---

## F2 — Extraer `ChequeraCuotaFactory` `[ ]`

Refactor puro. **Cero cambio de comportamiento**, verificado contra F1.

### Alcance

Nueva clase en `.../chequera/chequeraCuota/application/factory/ChequeraCuotaFactory.java`:

```java
ChequeraCuota crear(ChequeraSerie chequeraSerie, LectivoCuota lectivoCuota, int offset, OffsetDateTime ahora);
```

Absorbe, tal cual está hoy:
- el cálculo de `vencimiento1/2/3` con la regla de los 7/20/40 días más `30 * offset`
- la construcción de `ChequeraCuota` — **reemplazando el constructor posicional de 30+ argumentos por `builder()`**, que es donde vive el riesgo real de este refactor
- la llamada a `calculateCodigoBarras`

Dos consumidores pasan a usarla: `PreuniversitarioChequeraDetailsCreator.createCuotas()` y `SpoterService` (bloque 180-206).

**`ahora` entra por parámetro**, no se resuelve adentro con `OffsetDateTime.now()`. Es lo que hace la factory testeable de forma determinística, y sigue el criterio que ya se aplicó en la 3.42.0 al pasar la fecha de referencia por parámetro en `FindAllDebidasUseCase`.

La gestión del `offset` queda en cada llamador (es estado del bucle, no de la factory).

### Riesgo y mitigación
Traducir 30 argumentos posicionales a builder es donde se cuela un campo cambiado de lugar. Mitigación: los tests de F1 ya fijan valor por valor; **ningún test de F1 puede modificarse en esta fase**.

### Listo cuando
Todos los tests de F1 pasan **sin una sola modificación**, y `git diff` muestra que el bloque de cuotas desapareció de los dos consumidores.

> **Invariante del roadmap:** si un test de F1 tuvo que cambiar, el refactor cambió comportamiento. Frenar y revisar.

---

## F3 — Conectar `GuaraniBeneficioService` dentro de la factory `[ ]`

El corazón de la feature. Todo el cableado ocurre en **un solo lugar** gracias a F2.

### Decisiones de negocio — **CERRADAS** (2026-08-19)

F3 ya **no está bloqueada**.

| # | Decisión | Resuelto |
|---|---|---|
| 1 | **Acumulación entre requisitos** | Gana el **más alto**. No se acumulan, no hay cascada |
| 2 | **Vigencia** | **No hay**. La chequera del preuniversitario es un trámite puntual, no se reevalúa en el tiempo |
| 3 | **Tope** | Ninguno adicional. Cada porcentaje fraccional acotado a `[0, 1]` inclusive por F0; `MAX` no puede superar el mayor de sus entradas |
| 4 | **Tramos** | Los **tres** (`importe1/2/3`) reciben el mismo factor |
| 5 | **`importeNOriginal`** | Precio de **lista**, sin ningún descuento |
| 6 | **Redondeo** | Pesos enteros, `setScale(0, RoundingMode.HALF_UP)` |

### Fórmula acordada

```
beneficioAplicado = MAX(porcentajeBeneficio de los requisitos del alumno
                          con requisitoIngreso = 'S' y activo = 'S')

importeN         = importeNLista × (1 - beneficioAplicado)
                       .setScale(0, RoundingMode.HALF_UP)
importeNOriginal = importeNLista          // sin ningún descuento
```

Sin requisitos elegibles → `beneficioAplicado = 0` → importes idénticos a hoy.

### El caso 100 % — **resuelto** (2026-08-19)

El rango fraccional es `[0, 1]` inclusive y `1.00` (100 %) es un caso **de primera clase**, no un borde a evitar: la chequera debe informar que no queda deuda.

La cota de 99 que figuraba antes venía de la propuesta de cascada multiplicativa y quedó obsoleta al adoptar `MAX`. Con máximo no hay riesgo de combinación: el resultado nunca supera el mayor de los porcentajes de entrada, así que la validación por campo de F0 es el único control necesario.

**Consecuencias verificadas del beneficio 100 %** (importes en cero):

- `FindAllInconsistenciasUseCaseImpl` **no** se dispara. `importesInvalidos` compara `0 > 0` → falso en los tres tramos. `multiplicadoresInvalidos` evalúa `importeNOriginal × 49 < 0` → falso, porque `importeNOriginal` guarda el precio de lista (positivo). Ver F4 caso 4.
- `calculateCodigoBarras` no se corrompe: `DecimalFormat("0000000").format(0)` da `"0000000"`, y las diferencias `0 - 0 = 0` dan `"00000"`. Largo correcto, sin signo negativo. Queda por confirmar con el banco si un código Gire con importe cero es **operativamente** válido, aunque sea sintácticamente correcto.
- `importeNOriginal` conserva el precio de lista, así que la chequera puede mostrar cuánto se bonificó.

### Alcance

**a) Origen de los requisitos.** Hoy `PersonaGuarani.requisitosPresentados` solo entra por `AlumnoGuaraniRequest`; no existe caso de uso que consulte los requisitos vigentes de una persona. Definir al empezar la fase si vienen en el request del alta preuniversitaria o si hay que consultarlos, y construir el eslabón que falte.

**a.bis) El hook ya existe — usarlo.** `PreuniversitarioChequeraService.java:49-50` tiene el lugar reservado:

```java
// Determina beneficio
var becaPorcentaje = BigDecimal.ZERO;
```

Ahí va la llamada a `BeneficioPolicy`. El resultado se persiste en `ChequeraSerie.becaPorcentaje` a través de `policy.createSerie(data, control, becaPorcentaje)` (línea 58), que ya recibe el porcentaje como parámetro.

Tres cosas que esto resuelve sin código extra:

- **Congelamiento.** El porcentaje queda grabado en la serie al emitir. No se reevalúa nunca.
- **Trazabilidad.** `ChequeraSerie` ya tiene `becaResolucion`, `becaFecha` y `becaUserId` para registrar de dónde salió el beneficio.
- **Impresión.** `ChequeraService.track():190` ya copia `becaPorcentaje` a `ChequeraImpresionCabecera`, así que el dato llega solo a la capa de impresión.

`detailsCreator.create(chequeraSerie)` se invoca en la línea 62, después de que la serie tiene el porcentaje. **La factory lo lee de `chequeraSerie.getBecaPorcentaje()`**, sin parámetro adicional. Eso también resuelve F3(e): `SpoterService` construye su propia `ChequeraSerie`, cuyo `becaPorcentaje` es null o cero, así que no recibe beneficio sin necesidad de una rama especial.

**b) `BeneficioPolicy`** — dominio puro, sin Spring ni JPA:

```java
BigDecimal porcentajeEfectivo(List<RequisitoPresentadoGuarani> requisitos,
                              List<GuaraniBeneficio> beneficios);
```

Sin parámetro de fecha: la decisión 2 eliminó la vigencia temporal, así que la política es **puramente funcional** sobre sus dos entradas. Eso la vuelve trivial de testear y elimina toda dependencia del reloj.

La política resuelve en dos pasos, en este orden:

**1. Elegibilidad del requisito** — solo cuentan los requisitos de ingreso activos:

```java
requisito.getRequisitoRel() != null
  && "S".equalsIgnoreCase(requisito.getRequisitoRel().getRequisitoIngreso())
  && "S".equalsIgnoreCase(requisito.getRequisitoRel().getActivo())
```

`requisitoIngreso` y `activo` son `String` en `RequisitoGuarani` (líneas 19-20), así que la comparación va **null-safe y case-insensitive**, con la constante a la izquierda.

Este filtro importa porque `guarani_beneficio` no tiene FK hacia el catálogo de Guaraní: es un `Integer` con unique constraint. Nada impide que alguien cargue un beneficio para un requisito de **egreso** o para uno dado de baja. Este filtro y la validación de F0 son las dos barreras del dominio.

**2. Máximo** — de los beneficios cuyos requisitos pasaron el filtro, se toma el `porcentajeBeneficio` más alto. Sin requisitos elegibles, `ZERO`.

> **La deduplicación dejó de hacer falta.** Con suma o cascada, un requisito repetido en la lista contaba dos veces y había que deduplicar. Con `MAX`, repetir el mismo requisito no cambia el resultado: la operación es idempotente. Un test lo deja fijado igual, porque es una propiedad de la que ahora dependemos.
>
> Por la misma razón desaparecieron los casos de "requisito vencido" y "`fechaPresentacion` futura": la decisión 2 eliminó la vigencia, así que `fechaPresentacion`, `fechaVencimiento` y `fechaAlta` **no se leen**.

**Decisión pendiente — `requisitoRel` nulo.** El filtro depende de que Guaraní popule `requisitoRel`; si llega nulo, no se puede saber si el requisito es de ingreso. Se **falla cerrado**: no se otorga el beneficio para ese requisito y se loguea en `warn` con `requisito` y `persona`. Un descuento otorgado de más produce cuotas más baratas ya enviadas por mail, difíciles de revertir; un descuento faltante lo corrige tesorería a mano, que es el camino que el principio 5 de `../../mission.md` ya privilegia.

> ### 🔴 Verificación bloqueante antes de arrancar F3
>
> La plomería del core está verificada e intacta: `AlumnoGuaraniDtoMapper:40` y `:55` pasan `personaRel` **por referencia**, sin reconstruir. Lo que no se pudo verificar desde el repositorio es si Guaraní **envía** el objeto anidado — no hay fixtures JSON ni cliente hacia Guaraní, porque el core es receptor.
>
> **Cómo verificar (10 minutos, sin código nuevo):** `PersonaGuarani implements Jsonifyable`. Agregar un `log.debug("personaRel -> {}", personaRel.jsonify())` temporal en `CreatePreuniversitarioUseCaseImpl` y disparar un alta real en staging.
>
> | Resultado | Qué se hace |
> |---|---|
> | `requisitoRel` poblado | Se implementa F3 tal como está especificada. **Camino esperado** |
> | `requisitoRel` nulo | El filtro `requisitoIngreso`/`activo` **no se puede aplicar sobre el payload**. Plan B: resolver la elegibilidad contra un catálogo local de requisitos de Guaraní, que hoy **no existe** en el core. Agrega un módulo de persistencia y sincronización: **hay que revisar el alcance del hito antes de arrancar** |
> | Poblado de forma intermitente | Se trata como nulo por requisito (criterio 6): se excluye ese requisito, no la chequera |
>
> **Riesgo adicional, fuera de este repositorio:** el flujo son dos llamadas (`/create/personales` → `/create/preuniversitario`). El cliente recibe `PersonalesResponse` y debe reenviarlo. Si lo reconstruye en vez de reenviarlo, `personaRel` se pierde ahí y ningún test de core lo detecta.

**Tests de `BeneficioPolicy`:**

| # | Entrada | Esperado |
|---|---|---|
| 1 | Sin requisitos | `ZERO` |
| 2 | Requisito sin beneficio configurado | `ZERO`, sin excepción |
| 3 | Un requisito con 20 % | `20` |
| 4 | Requisitos con 10 %, 30 % y 20 % | **`30`** — gana el máximo, no suma 60 |
| 5 | El mismo requisito repetido | Igual que una sola vez (idempotencia de `MAX`) |
| 6 | `requisitoIngreso = 'N'` | Excluido |
| 7 | `activo = 'N'` | Excluido |
| 8 | `requisitoRel` nulo | Excluido, con `log.warn` |
| 9 | `'s'` minúscula en ambos flags | **Incluido** (case-insensitive) |
| 10 | El de mayor porcentaje es de egreso; el menor es de ingreso | Gana el **menor** — el filtro corre **antes** que el máximo |
| 11 | Listas nulas | `ZERO`, nunca `NullPointerException` |
| 12 | Beneficio con `porcentajeBeneficio` nulo | `ZERO` para ese requisito (el `@Builder.Default` es `ZERO`, pero un registro viejo puede tener null) |

El caso 10 es el que verifica que el orden de los dos pasos es el correcto. Invertirlo produce el resultado equivocado sin fallar ningún otro test.

**100 % de ramas** — es una clase chica y pura.

**c) Aplicación en la factory.** La factory recibe el porcentaje efectivo y lo aplica a **los tres tramos** con el mismo factor, redondeando a entero con `HALF_UP`:

```java
importeN = importeNLista.multiply(BigDecimal.ONE.subtract(
               beneficio))
           .setScale(0, RoundingMode.HALF_UP);
```

`importeNOriginal` recibe `importeNLista` **sin tocar**.

Tests: 0 % → resultado **idéntico** a F1 (garantía de no regresión); 20 % sobre importes reales → valor exacto contra ejemplo firmado; porcentaje con decimales (ej. 33,33 %) → redondeo verificado; los tres tramos mantienen `importe1 ≤ importe2 ≤ importe3`; `importeNOriginal ≠ importeN` cuando hay beneficio; `importeNOriginal == importeN` cuando el beneficio es 0.

**d) Degradación segura.** Si falla la resolución de beneficios, se crea la cuota **sin bonificación** y se loguea. Nunca se rompe la emisión de una chequera por un beneficio no resuelto. Test explícito.

**e) Alcance del cableado.** Resuelto por (a.bis): la factory lee `chequeraSerie.getBecaPorcentaje()`. `SpoterService` construye series sin beca, así que queda excluido sin rama especial. Test que lo fija.

**f) `ChequeraTotal` — el total debe coincidir con la suma de las cuotas.**

Hoy `createTotals()` copia `LectivoTotal.getTotal()` sin descuento, mientras las cuotas sí lo tendrían. `ChequeraService.track():203-207` mete ambos en la misma fila de `ChequeraImpresionDetalle`, así que la chequera impresa mostraría total sin bonificar y cuotas bonificadas.

**No aplicar el factor al total.** Bonificar `LectivoTotal.total` por separado no garantiza que coincida con la suma de las cuotas, porque cada cuota se redondea a entero de forma independiente:

```
Total lista $47.350, tres cuotas de $15.783,33, beneficio 30 %

Factor al total:   47.350 × 0,70            = $33.145
Suma de cuotas:    (15.783,33 × 0,70 → 11.048) × 3 = $33.144   ← no coinciden
```

**Calcular el total como la suma de las cuotas ya redondeadas.** Es lo que el sistema **ya hace** en otro momento del ciclo: `PagoService.calcularPagado():214` reasigna

```java
chequeraTotal.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(...));
```

y `CalculateTotalCuotasActivasUseCaseImpl` suma `importe1` de las cuotas activas (`baja = 0`). O sea: el invariante `total = Σ importe1` ya existe en el sistema, pero se aplica recién con el primer pago, no al crear.

**Esto además corrige un defecto latente previo a esta feature:** si `LectivoTotal.total` difiere de `Σ importe1` de las cuotas, el total cambia solo al registrarse el primer pago, sin que nadie lo note. Con beneficio aplicado a las cuotas y no al total, esa diferencia pasaría de sutil a enorme.

**Cambio de orden en `PreuniversitarioChequeraDetailsCreator.create()`:**

```java
// antes
createTotals(chequeraSerie);
createAlternatives(chequeraSerie);
createCuotas(chequeraSerie);

// después
createAlternatives(chequeraSerie);
var cuotas = createCuotas(chequeraSerie);      // devuelve las cuotas guardadas
createTotals(chequeraSerie, cuotas);           // agrupa por productoId y suma importe1
```

Agrupar en memoria por `productoId` evita una segunda consulta a la base. `createCuotas` pasa a devolver `List<ChequeraCuota>` en vez de `void`.

**Semántica de "cuotas activas"** — idéntica a la que ya usa `CalculateTotalCuotasActivasUseCaseImpl`, para que el invariante sea el mismo en creación y en pago:

- Se consulta `findAllByCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, (byte) 0)`, o sea **`baja = 0`**.
- Se suma **`importe1`**, no `importe2` ni `importe3`.
- Se crea **un `ChequeraTotal` por `productoId`**, sumando solo las cuotas de ese producto.
- `pagado` inicializa en `BigDecimal.ZERO`, igual que hoy.

**Tests:**
- Beneficio 0 % → `total` igual a `Σ importe1`, y verificar si eso coincide o no con `LectivoTotal.total` (deja documentado el estado real del dato)
- Beneficio 30 % con importes que produzcan resto de redondeo → `total == Σ importe1` **exacto**
- Beneficio 100 % → `total = 0` y todas las cuotas en cero
- Varios productos → un `ChequeraTotal` por `productoId`, cada uno sumando solo sus cuotas
- Cuota con `baja = 1` → excluida del total, igual que en `calculateTotalCuotasActivas`
- `pagado` sigue inicializando en `BigDecimal.ZERO`
- Tras `PagoService.calcularPagado()`, el total **no cambia** (invariante ya consistente desde la creación)

### Listo cuando
Los tests de F1 siguen verdes con beneficio 0 %, los nuevos pasan, y `BeneficioPolicy` está al 100 % de ramas.

---

## F4 — Verificar el invariante de `FindAllInconsistenciasUseCaseImpl` `[ ]`

La fase que evita que la feature dispare falsas alarmas en el detector de inconsistencias.

### Por qué importa — verificado en código

`FindAllInconsistenciasUseCaseImpl` marca una cuota como inconsistente si se cumple **cualquiera** de tres condiciones:

| Chequeo | Condición que dispara la alarma |
|---|---|
| `vencimientosInvalidos` | `vencimiento1 > vencimiento2` o `vencimiento2 > vencimiento3` |
| `importesInvalidos` | `importe1 > importe2` o `importe2 > importe3` |
| `multiplicadoresInvalidos` | `importeNOriginal * 49 < importeN` para algún tramo |

Consecuencias directas sobre F3:

- **Si el beneficio se aplica a un solo tramo** (decisión 4), puede romperse la relación creciente `importe1 ≤ importe2 ≤ importe3` y disparar `importesInvalidos`. Bonificar solo `importe1` es seguro; bonificar solo `importe3` **no lo es**.
- **`multiplicadoresInvalidos` es asimétrico**: solo detecta importes demasiado *grandes* respecto del original. Un beneficio que reduce `importeN` dejando `importeNOriginal` intacto **no lo dispara**. Esa asimetría es la razón por la que la decisión 3.5 es segura en ambas direcciones — pero hay que verificarlo, no asumirlo.
- El chequeo desreferencia `importeNOriginal` sin control de nulo. Si F3 llegara a dejar algún original en nulo, esto es un `NullPointerException`.

### Alcance

`FindAllInconsistenciasUseCaseImplTest` con cuotas generadas por la factory de F3:

1. Cuota con beneficio aplicado a todos los tramos → **no** es inconsistente
2. Cuota con beneficio en un solo tramo → verificar contra la decisión 4 si corresponde alarma o no
3. Cuota con beneficio 0 % → idéntica al comportamiento pre-feature
4. Beneficio del 100 % (importes en cero) → verificar que `0 * 49 < 0` es falso y no dispara
5. `importeNOriginal` nulo → hoy revienta; decidir si se blinda acá o se garantiza en F3 que nunca sea nulo
6. Beneficio máximo posible → sigue dentro del margen del multiplicador 49

Además, correr el endpoint `GET /chequeraCuota/inconsistencias/{desde}/{hasta}` sobre datos de staging con beneficios aplicados y confirmar que no aparecen falsos positivos.

### Listo cuando
Los seis casos pasan y la corrida sobre staging no reporta inconsistencias nuevas atribuibles al beneficio.

---

## F5 — Issue 2: proyección documental para `sender-service` `[ ]`

**Depende del Issue 1 de cálculo desplegado.** Esta fase consume cuotas y porcentajes ya
persistidos; no modifica elegibilidad, factor ni totales. Su objetivo es que la chequera
impresa pueda mostrar precio de lista y precio con beneficio, incluso cuando el importe a
cobrar sea cero.

### Precondición — identificar el DTO exacto

Verificado hoy: **`ChequeraCuotaResponse` y `ChequeraCuotaPagosDto` ya exponen `importe1/2/3Original`.** También los tienen las vistas `ChequeraCuotaDeuda.kt`, `CuotaDeuda.java`, `CuotaDeudaPayPerTic.java` y `ChequeraCuotaPersona.java`.

Es decir: **el campo puede estar ya expuesto en el DTO correcto**, y la fase se reduciría a verificarlo y documentarlo. La relación con `sender-service` es por Feign (`ChequeraClient` → `tesoreria-sender-service` para `generatePdf`/`sendChequera`), así que sender consulta de vuelta a core por algún endpoint que hay que identificar.

**Primer paso de la fase:** determinar qué endpoint de core consume `sender-service` para armar el PDF, y qué DTO devuelve. Recién ahí se sabe si hay que agregar el campo o solo verificarlo.

### Alcance
Según lo que arroje la verificación:
- **Si el campo ya está**: test de contrato que lo fija, y nota en el roadmap. Fase de medio día.
- **Si falta**: agregarlo al DTO y su mapper, con test de serialización JSON. Cambio **aditivo** → *minor* de SemVer, sin romper clientes.

En ambos casos: confirmar con sender-service que el nombre del campo en el JSON coincide con lo que espera (`importe1Original` vs `importe_1_original` — hay precedente de `@JsonProperty` con snake_case en `DeudaExamenResponse`).

### Listo cuando
El JSON que recibe sender-service incluye precio de lista y precio bonificado, verificado con un test de contrato.

---

## F6 — Calidad dividida entre Issue 1 e Issue 2 `[ ]`

El Issue 1 cubre controllers, cálculo, inconsistencias y JaCoCo. El Issue 2 agrega los
tests de contrato Core → sender y las fixtures documentales 0 %/30 %/100 %.

### Alcance

**`GuaraniBeneficioControllerTest`** (`@WebMvcTest`) — **extiende** el archivo que ya creó F0 con sus casos de validación; no lo reemplaza. Cubrir los 5 endpoints bajo `/api/tesoreria/core/guaraniBeneficio`:
- `GET /` lista completa
- `GET /requisito/{requisito}` → 200, y **404** vía `ResponseStatusException` cuando no existe
- `POST /requisitos` con lista de enteros → respuesta parcial cuando algunos requisitos no tienen beneficio
- `POST /` alta
- `PUT /requisito/{requisito}` actualización

**`AlumnoGuaraniControllerTest`** — alta preuniversitaria con requisitos presentados; verificar que la respuesta refleja los importes bonificados.

**`ChequeraCuotaControllerTest`** — acotado a lo que toca la feature:
- `GET /chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` → importes bonificados y originales en el JSON
- `GET /inconsistencias/{desde}/{hasta}` → sin falsos positivos (cierra el círculo con F4)
- `GET /unique/...` → formato ISO 8601 de vencimientos

**Regresión Issue 1**: alta de preuniversitario con beneficio → chequera creada → cuotas con
importes correctos → sin inconsistencias → evento `send-chequera` publicado, también para
100 %.

**Regresión Issue 2**: el DTO/proyección que consulta sender-service contiene los precios de
lista y bonificados, incluidas las filas en cero necesarias para el PDF.

### Listo cuando
Los cuatro grupos pasan y el JSON de cada endpoint queda documentado como ejemplo dentro del propio test.

---

## Dependencias entre fases

```
F0 ─────────────────────────────────> (independiente, desplegable solo)

ISSUE 1: F1 ──> F2 ──> F3 ──> F4 ──> F6-Core

ISSUE 2: (Issue 1 desplegado) ──> F5 ──> F6-integración

F0 y F1 sin bloqueos — se pueden empezar hoy, en paralelo
F3 requiere además validar el payload en staging
F5 comienza sólo después del despliegue del Issue 1
```

Las seis decisiones de negocio y el rango fraccional `[0, 1]` están cerrados. Ver `decisiones.md`.

## Riesgos

| Riesgo | Mitigación |
|---|---|
| Las decisiones de negocio de F3 se demoran | F1 y F2 no dependen de ellas; se puede avanzar con esos hitos sin bloquear el cálculo |
| El refactor de F2 cambia comportamiento en silencio | F1 es el contrato; ningún test de F1 puede tocarse en F2. El constructor posicional de 30+ argumentos es el punto exacto de riesgo |
| El beneficio dispara falsas inconsistencias | F4 existe para eso, y ya está identificada la condición precisa (`importe1 ≤ importe2 ≤ importe3`) que puede romperse |
| Aplicar beneficios altera chequeras ya emitidas | La factory solo interviene en la **creación**. Verificar en staging antes de main |
| `SpoterService` hereda el beneficio sin quererlo | F3(e) lo define explícitamente: el porcentaje entra por parámetro y Spoter pasa `ZERO` si corresponde |
| F5 resulta ser trabajo nulo | Es un desenlace posible y bueno: se convierte en verificación documentada |
| **`requisitoRel` viene nulo desde Guaraní** y el filtro de ingreso anula todos los beneficios | Verificar contra datos reales **antes** de cerrar F3. Es el riesgo más concreto de la feature: falla cerrado y en silencio, sin errores, solo cuotas sin descuento |
| Un beneficio cargado sobre un requisito de egreso o dado de baja | F0 valida el rango del porcentaje; F3(b) filtra `requisitoIngreso`/`activo`. Son las dos barreras del dominio |

---

## Documentos relacionados

- `../../mission.md` — propósito, audiencia y principios
- `../../tech-stack.md` — stack y huecos declarados (H1–H8, documentados y **no priorizados**)
- `decisiones.md` — decisiones de negocio D1–D8 y verificaciones
