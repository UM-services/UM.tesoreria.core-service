# Plan de implementación — Beneficio por requisitos de ingreso

> Fuente funcional: `spec.md`. Decisiones de negocio: `decisiones.md` (D1–D8).
> Estado: dividido en dos issues secuenciales: cálculo primero, correo/PDF después.

## División solicitada de issues

| Issue | Fases | Dependencia | Estado |
|---|---|---|---|
| 1. Cálculo y persistencia del beneficio | F0–F4 + parte Core de F6 | Validación de payload en staging antes de F3 | [#338](https://github.com/UM-services/UM.tesoreria.core-service/issues/338) |
| 2. Correo/PDF con información de beneficios | F5 + parte de integración de F6 | Issue 1 desplegado | Pendiente de crear en GitHub |

La Issue 1 mantiene la publicación existente de `send-chequera` como regresión, pero no
modifica sender-service. La Issue 2 consume las cuotas ya bonificadas y resuelve cómo se
documentan, incluido el caso 100%; no recalcula beneficios.

## Objetivo y límite

Aplicar a chequeras preuniversitarias el mayor beneficio configurado para los requisitos
de ingreso activos del alumno. El valor queda congelado al emitir la serie; no se vuelve a
consultar ni recalcular para chequeras existentes.

No entra en este trabajo:

- Migrar `SpoterService` a arquitectura hexagonal; solo reutiliza la factory.
- Aplicar `ArancelPorcentaje` ni modificar `RecalculateCuotaByUniqueIndexUseCaseImpl`.
- Corregir los contratos heredados `generatePdf`/`sendCuota` entre core y sender, fuera del
  flujo preuniversitario. La Issue 2 sí coordina la proyección documental de una chequera
  100 %, para que conserve sus filas con importe cero.

## Diseño acordado

```text
POST /guarani/alumno/create/preuniversitario
  PersonalesResponse.alumnoGuarani.personaRel.requisitosPresentados
          |
          v
PreuniversitarioChequeraData (propaga requisitos)
          |
          v
GuaraniBeneficioService.findByRequisitos(ids)   [una consulta IN]
          |
          v
BeneficioPolicy.porcentajeEfectivo()            [puro, MAX elegible]
          |
          v
ChequeraSerie.becaPorcentaje                    [congelado]
          |
          +--> ChequeraCuotaFactory              [3 tramos, HALF_UP]
                    |
                    +--> importeNOriginal = precio de lista
                    +--> importeN = precio bonificado
          |
          +--> ChequeraTotal = SUM(importe1 de cuotas activas)
```

Reglas no negociables:

- Solo son elegibles `requisitoIngreso='S'` y `activo='S'`, con comparación
  null-safe y case-insensitive.
- Gana el porcentaje mayor; 0% y 100% son válidos.
- Si faltan requisitos, `personaRel`, un porcentaje heredado, o falla la consulta de
  beneficios, se emite sin bonificación y se registra `warn`; nunca se aborta la emisión.
- `SpoterService` crea series con beneficio cero y conserva el comportamiento actual.
- Si el beneficio es 100%, la serie y sus cuotas se emiten en cero y se publica
  `send-chequera` normalmente, para que el alumno reciba su chequera con los importes
  bonificados, el porcentaje aplicado y total a pagar cero. No se genera un correo alternativo.

## Fases y PRs

### F0 — Validar configuración de beneficios

Cambiar `GuaraniBeneficioRequest` a `@Getter`/`@Setter` y agregar:

- `@NotNull requisito`.
- `@NotNull`, `@DecimalMin("0")` y `@DecimalMax("100")` para el porcentaje.
- `@Valid` en POST y PUT del controller.
- Detección previa de requisito existente y traducción de la violación única concurrente
  a una excepción de dominio mapeada a `409 Conflict`.

Pruebas: 0, 50 y 100 válidos; negativos, nulo y mayor a 100 inválidos; POST y PUT;
duplicado secuencial y traducción de conflicto de persistencia.

### F1 — Caracterizar el comportamiento actual

Antes de producción nueva, cubrir `PreuniversitarioChequeraDetailsCreator` y el bloque
equivalente de `SpoterService`:

- vencimientos originales y vencidos, offsets y mezcla de ambos;
- importes y originales idénticos sin beneficio;
- arancel, código de barras y flags iniciales;
- cuotas/totales vacíos, varios productos y alternativas.

Estos tests no se cambian en F2: son el contrato de no regresión.

### F2 — Extraer `ChequeraCuotaFactory`

Crear `chequeraCuota/application/factory/ChequeraCuotaFactory` con fecha de referencia
inyectada por parámetro. Extrae construcción de cuota, vencimientos y código de barras de:

- `PreuniversitarioChequeraDetailsCreator`.
- `SpoterService`.

La factory es el único punto que aplicará el factor en F3. F2 no cambia importes ni
orden de creación; toda F1 debe seguir verde sin modificaciones.

### F3 — Resolver y aplicar el beneficio

1. Agregar `requisitosPresentados` a `PreuniversitarioChequeraData` y propagarlo desde
   `CreatePreuniversitarioUseCaseImpl` por la ruta real
   `alumnoGuaraniFull.getAlumnoGuarani().getPersonaRel().getRequisitosPresentados()`.
   La ruta debe ser null-safe y contar con un test de pass-through.
2. Antes de habilitar la fase en producción, registrar temporalmente el payload en staging
   y verificar que cada requisito trae `requisitoRel`; si no llega, detener F3 y revisar el alcance.
3. Crear `BeneficioPolicy` puro. Recibe requisitos y beneficios, filtra elegibles y toma
   `MAX`; las entradas nulas devuelven cero.
4. Resolver los beneficios en lote en `PreuniversitarioChequeraService`, persistir el
   resultado en `ChequeraSerie.becaPorcentaje` y mantener el fallback seguro en cero.
5. Hacer que la factory lea `chequeraSerie.becaPorcentaje` y aplique a cada tramo:

```text
importe = importeLista * (1 - beca), scale 0, HALF_UP
original = importeLista
```

6. Preparar las cuotas en memoria y persistir en orden de foreign keys: totales → alternativas →
   cuotas. Agrupar las cuotas activas construidas por `productoId` y sumar `importe1`; no bonificar
   `LectivoTotal.total` por separado.
7. Mantener en `CreatePreuniversitarioUseCaseImpl` la publicación normal de
   `send-chequera` también cuando `becaPorcentaje` es `1.00` (100 %). Probar que el evento no se
   suprime. La proyección del PDF, sus filas en cero y el contrato de datos son alcance de
   la Issue 2.

### F4 — Proteger el detector de inconsistencias

Agregar pruebas a `FindAllInconsistenciasUseCaseImpl` para 0%, todos los tramos
bonificados, un solo tramo, 100%, originales nulos y límite del multiplicador. Una cuota
generada correctamente con beneficio no debe ser inconsistente; un original nulo histórico
debe devolverse como inconsistencia y nunca abortar el endpoint.

Ejecutar el endpoint en staging sobre chequeras nuevas y comprobar que no aparezcan falsos
positivos atribuibles al beneficio.

### F5 — Issue 2: correo/PDF con beneficios (posterior a F0–F4)

MercadoPago obtiene de Core las cuotas pendientes —que reenvía a Sender en
`UMPreferenceMPDto.chequeraCuota`— y crea el contexto de pago mediante
`mercadopago/makeContext`. Sender genera el PDF localmente a partir de esos datos.

- Añadir pruebas JSON para `/chequeraCuota/chequera/pendientes/...` que fijen los
  importes bonificados y originales: esta es la cuota que llega a Sender y alimenta el PDF.
- Probar `/mercadopago/makeContext/{chequeraCuotaId}` por separado: el contexto debe usar
  el importe bonificado que se cobra, sin asumir que sea el objeto de cuota reenviado por
  MercadoPago a Sender en el flujo normal.
- Separar proyección documental de cobro: el handler de `send-chequera` debe consumir una
  proyección que incluya cuotas con `importe1 = 0`; el endpoint de pendientes y MercadoPago
  conserva su filtro positivo. Coordinar en sender-service el DTO/consulta exactos y una
  prueba de contrato entre repos. `UMPreferenceMPDto.chequeraCuota` preserva los originales
  para cobro, sin agregar datos al evento Kafka.
- En staging, validar el recorrido alta → `send-chequera` → sender → MercadoPago → core →
  PDF generado.
- Para beneficio 100 %, verificar por separado que `send-chequera` se publica y el PDF
  informativo incluye sus filas con importes cero, mientras `MercadoPagoCoreService.makeContext*`
  no crea contextos de cobro para cuotas de importe cero. Definir con sender/Gire si esas filas
  muestran barcode cero o ningún barcode antes de producción.

Sender y su plantilla PDF no se modifican en este repositorio. El drift heredado de
`generatePdf` y `sendCuota` es un trabajo de integración separado: no es el camino usado
por el alta preuniversitaria.

### F6 — Calidad dividida entre ambos issues

- **Issue 1:** extender `GuaraniBeneficioControllerTest`, probar alta preuniversitaria,
  endpoints de cuotas e inconsistencias, y configurar JaCoCo.
- **Issue 2:** pruebas de contrato Core → sender, fixtures 0 %/30 %/100 % y validación del PDF.
- Configurar JaCoCo `check` con 80% de líneas, limitado a los paquetes de beneficio,
  cuota y serie afectados; `mvn -B verify` debe fallar si bajan del umbral.
- Añadir y registrar `docs/hexagonal-beneficioCuota.mmd` si el diagrama de la feature se
  incorpora al catálogo de documentación.

## Plan de pruebas

```text
POLÍTICA                         EMISIÓN Y DATOS                         API
12 unitarias BeneficioPolicy     11 unitarias factory                    12 controller
  - MAX / elegibilidad             - fechas, offset, rounding              - validación y 409
  - nulos / 100%                   - originales / barcode                  - cuotas JSON
                                  7 unitarias totals                      4 integración alta
DETECCIÓN                           - por producto / baja / 100%           2 integración Spoter
6 unitarias inconsistencias
```

Rutas críticas:

- Requisito elegible → MAX → serie → cuotas → total → Kafka → sender → MercadoPago → PDF.
- Sin requisito o con datos incompletos → cero, `warn`, emisión exitosa.
- Beneficio 100% → importes/totales cero, sin inconsistencia y con evento `send-chequera`.
- Primer pago → el total persiste igual porque ya era la suma de cuotas.

## Fallos y tratamiento

| Falla | Tratamiento | Cobertura |
|---|---|---|
| `personaRel` o lista de requisitos nula | beneficio 0 + `warn` | unit/integración |
| `requisitoRel` nulo | excluir solo ese requisito + `warn` | unit |
| error al consultar beneficios | beneficio 0 + `warn`, sin abortar | integración |
| porcentaje legado nulo | tratar como cero | unit |
| duplicado concurrente | 409, no 500 | controller/integración |
| redondeo por cuota | sumar cuotas ya redondeadas | unit totals |
| sender filtra cuotas 100% | proyección documental que incluye importes cero; pendientes/MercadoPago siguen filtrando cobros | contrato entre repos |
| beneficio 100% no llega al alumno | publicar `send-chequera` normal con cuotas en cero, porcentaje aplicado y total a pagar cero | integración |

## Dependencias y paralelización

| Lane | Trabajo | Depende de |
|---|---|---|
| A | F0 — validación y conflicto | — |
| B | F1 — caracterización | — |
| A+B | F2 — factory | F1 |
| C | F3 — beneficio, cuotas y total | F0, F2, validación staging del payload |
| D | F4 — inconsistencias | F3 |
| E | Issue 2 / F5 — contrato de correo/PDF | Issue 1 desplegado |
| F | Calidad de Issue 1 | F0–F4 |

Lanzar F0 y F1 en paralelo. F2–F4 y la calidad de Issue 1 son secuenciales por los mismos
módulos. F5 comienza sólo después del despliegue de Issue 1.

## Gates de rollout

1. Staging confirma que `personaRel.requisitosPresentados` llega poblado al core.
2. `mvn -B verify` y los tests de integración pasan.
3. Altas con beneficio parcial y con 100% publican `send-chequera`; el PDF de 100% conserva
   sus filas en cero y los canales de pago omiten contextos de cobro inexistentes.
4. La corrida de inconsistencias no agrega falsos positivos ni falla ante originales nulos.
5. La conciliación de staging deja registrados fallbacks a cero, beneficios 100 % y cualquier
   diferencia entre `LectivoTotal` y `SUM(importe1)` por producto.

## Autoplan — Fase 1: revisión CEO

### Premisas y dirección

La meta es correcta: aplicar un beneficio verificable al emitir una chequera nueva, sin
reescribir las ya emitidas. Se confirmó con negocio que incluso una beca del 100 % publica
`send-chequera`: el documento informa al alumno que sus cuotas fueron bonificadas.

### Qué ya existe y se reutiliza

| Subproblema | Base existente | Decisión |
|---|---|---|
| Resolver beneficios por varios requisitos | `GuaraniBeneficioService.findByRequisitos` y repositorio `findByRequisitoIn` | Reutilizar una consulta IN |
| Persistir el porcentaje congelado | `ChequeraSerie.becaPorcentaje` y cabecera de impresión | Reutilizar; no crear una segunda fuente de verdad |
| Publicar la chequera | `MailChequeraService.sendChequera` publica `SendChequeraEvent` | Conservar para todos los porcentajes |
| Pago online | `MercadoPagoCoreService` rechaza importes cero | Tratar 100 % como chequera informativa sin contexto de pago |
| Cálculo de total posterior | `PagoService` ya suma cuotas activas | Llevar el mismo invariante a la creación |

### Alternativas evaluadas

| Enfoque | Cobertura | Riesgo | Decisión |
|---|---:|---|---|
| Aplicar el descuento sólo al total | 3/10 | El total puede diferir de la suma de cuotas redondeadas | Rechazado |
| Aplicar el descuento a cada cuota y conservar los originales | 10/10 | Requiere extraer el código duplicado | Elegido |
| Crear una segunda emisión específica para 100 % | 6/10 | Duplicaría envío y dejaría al alumno sin el PDF estándar | Rechazado |

### Estado objetivo

```text
HOY: beneficio configurado + series siempre en 0 % + totales potencialmente divergentes
  -> ESTE PLAN: beneficio inmutable por serie + cuotas/total coherentes + envío normal
  -> IDEAL: decisión auditable, conciliación de emisiones y documento que explica la bonificación
```

### Revisión por secciones

1. **Arquitectura.** La factory compartida elimina la duplicación entre preuniversitario y
   Spoter sin extender el beneficio a Spoter. El porcentaje viaja por la serie, una frontera
   explícita y reversible para chequeras nuevas.
2. **Errores y rescate.** Mantener emisión a 0 % para datos de negocio ausentes confirmados,
   pero registrar `personaId`, `documentoId`, requisitos recibidos y causa. Una falla de la
   consulta debe aparecer en la conciliación de rollout, no quedar como un `warn` aislado.
3. **Seguridad e integridad.** La validación `[0,100]`, unicidad y 409 cubren entradas inválidas;
   se conserva la fuente de configuración actual, sin inventar permisos nuevos en esta feature.
4. **Datos.** `importeNOriginal` es obligatorio al crear. Si el detector encuentra un original
   nulo histórico, debe marcar la cuota como inconsistente en vez de lanzar `NullPointerException`.
5. **Calidad.** La factory debe recibir la fecha de referencia y encapsular el constructor
   posicional de cuota. No se introducirá una abstracción de descuento genérica para los dos
   mecanismos que siguen fuera de alcance.
6. **Pruebas.** F1 protege ambos flujos actuales; F3 agrega política, factor, total y evento;
   F4 cubre los caminos de datos nulos; F5 cubre el contrato y el caso de pago cero.
7. **Rendimiento.** Una consulta `IN` reemplaza cualquier potencial consulta por requisito.
   Las sumas se hacen en memoria sobre las cuotas recién creadas, con tamaño acotado por una serie.
8. **Observabilidad.** En staging, conciliar diariamente series creadas con beneficio, fallbacks
   a cero, porcentajes 100 y diferencias entre `LectivoTotal` y la suma de cuotas.
9. **Rollout.** Primero validar el payload real; después desplegar con F1/F3 verdes y una alta
   parcial y otra al 100 %. Rollback afecta sólo emisiones futuras; las emitidas se corrigen por
   baja y reemisión, como hoy.
10. **Trayectoria.** La decisión queda congelada y trazable por porcentaje. La chequera
    bonificada es la única comunicación necesaria para confirmar el beneficio.

### Registro de errores y rescate

| Código | Falla | Rescate | Efecto visible | Cobertura |
|---|---|---|---|---|
| R1 | requisitos/persona nulos | 0 % + `warn` estructurado | Chequera normal sin bonificación | unit + integración |
| R2 | consulta de beneficio falla | 0 % + `warn` + conciliación | Chequera normal, caso visible a Tesorería | integración |
| R3 | original histórico nulo | marcar inconsistencia, no excepción | caso recuperable en detector | unit |
| R4 | 100 % | publicar chequera, omitir contexto MercadoPago de importe 0 | PDF informativo sin cobro | integración |
| R5 | duplicado concurrente | excepción de dominio a 409 | error de carga legible | controller + integración |

### Modificaciones CEO incorporadas

- F4 debe hacer null-safe el detector: original nulo se informa como inconsistencia.
- F5 debe probar explícitamente que 100 % publica `send-chequera`, genera el PDF informativo
  y no crea un contexto MercadoPago para una cuota de importe cero.
- El gate de rollout incluye conciliación de fallback a cero y de totales por producto.

### Fuera de alcance, con motivo

- Explicar visualmente ahorro y precio original en la plantilla del PDF: requiere cambio coordinado
  en sender-service; el contrato Core conserva ambos valores para habilitarlo.
- Auditoría detallada de cada requisito ganador: `becaPorcentaje` congela el resultado de esta fase;
  un registro histórico de reglas es una mejora de plataforma separada.
- Normalizar totales de series ya emitidas: esta emisión sólo crea datos nuevos.

### Modificar F4, F5 y rollout

En F4, tratar `importeNOriginal == null` como una inconsistencia reportable y probar que el
endpoint no falla. En F5, sumar el caso 100 %: `send-chequera` se publica y
`MercadoPagoCoreService.makeContext*` devuelve `null`/no agrega contexto para una cuota en cero.
En el rollout, registrar la cantidad de fallbacks a cero, becas 100 % y diferencias detectadas
entre el total de configuración y `SUM(importe1)` por producto.

## Autoplan — Fase 3: revisión de ingeniería

### Arquitectura y ejecución

```text
CreatePreuniversitarioUseCaseImpl
  -> PreuniversitarioChequeraData(requisitos de alumnoGuarani.personaRel)
  -> PreuniversitarioChequeraService
       -> GuaraniBeneficioService.findByRequisitos(ids) [error -> 0 % + warn]
       -> BeneficioPolicy [MAX, puro]
       -> ChequeraSerie(becaPorcentaje congelado)
       -> ChequeraCuotaFactory(referencia, serie, lectivoCuota)
            -> cuota bonificada + originales + barcode
       -> save totals -> save alternativas -> save cuotas
            totals = SUM(cuotas activas construidas por producto)
  -> MailChequeraService.sendChequera [siempre, incluido 100 %]
```

La ruta real de requisitos es `alumnoGuarani.personaRel`, no `PersonalesResponse.persona`.
La extracción debe preservar exactamente el orden, offsets y barcode de F1 cuando el porcentaje
es cero. `SpoterService` pasa una serie con beneficio cero/null normalizado a `BigDecimal.ZERO`.

### Revisión técnica

1. **Datos de entrada.** Añadir el pass-through null-safe al record y probar persona, lista,
   elemento y `requisitoRel` nulos. No se debe consultar beneficios con una lista vacía.
2. **Factory.** Recibe una única fecha de referencia por emisión, normaliza beneficio nulo a cero
   y crea cada tramo con `HALF_UP`; calcula barcode después de fijar los tres importes finales.
   El formato del barcode para importe cero queda como gate explícito con sender/Gire.
3. **Transacción.** Construir las cuotas en memoria permite calcular los totales y respetar el orden
   de foreign keys al persistir `chequera_total` → `chequera_alternativa` → `chequera_cuota`.
   La suma filtra `baja == 0` y la transacción revierte todos los padres si falla una cuota.
4. **Inconsistencias.** Reemplazar accesos a originales, importes y vencimientos por una
   validación null-safe; cualquier campo requerido nulo es una inconsistencia, no una excepción
   de lectura masiva.
5. **Evento y pagos.** No se agrega una rama para suprimir `send-chequera`. El contexto MercadoPago
   ya omite importe cero y se prueba como ausencia esperada, no como error.
6. **Configuración.** La prevalidación de duplicado no sustituye capturar `DataIntegrityViolationException`;
   ambas son necesarias para entregar 409 bajo concurrencia.
7. **Política.** Intersectar requisitos elegibles recibidos con los beneficios devueltos, deduplicar
   IDs y no consultar con una lista vacía; un beneficio alto de un requisito no presentado no
   puede ganar por error.

### Diagrama de cobertura

```text
POLÍTICA                         ORQUESTACIÓN                         INTEGRACIONES
MAX elegible [unit]              requisitos completos [int]          send-chequera 0 %/30 %/100 % [int]
sin elegible [unit]              persona/lista/requisito nulos [unit] MP importe cero -> sin contexto [unit]
porcentaje nulo [unit]           consulta falla -> 0% + warn [int]    JSON conserva originales [contract]
0 %/100 %/HALF_UP [unit]         serie existente -> sin reenvío [int]  PDF bonificado en staging [manual]

FACTORY Y TOTALES                DETECTOR
fecha vigente/vencida [unit]     0 %/beneficio/100 % [unit]
0% idéntico a F1 [regresión]     original nulo -> inconsistencia [unit]
multi-producto/baja/redondeo [unit] límite multiplicador [unit]
```

### Modos de falla

| Código | Modo | Prevención/prueba | Severidad |
|---|---|---|---|
| E1 | ruta de requisitos equivocada | test pass-through de `personaRel` | P1 |
| E2 | porcentaje nulo o fuera de rango | validación API y política defensiva | P1 |
| E3 | total no coincide por redondeo | suma de cuotas ya redondeadas por producto | P1 |
| E4 | detector cae con dato legado nulo | inconsistencia reportable | P1 |
| E5 | pago para cuota cero | no crear contexto MercadoPago | P1 |
| E6 | doble alta de configuración | consulta previa + traducción de unique constraint | P1 |
| E7 | PDF 100% sin filas | proyección documental sin filtro `importe1 > 0` | P1 |

### No entra en esta fase

- Corregir el generador de series concurrente existente (`nextChequeraSerieId`): no lo introduce
  el beneficio y requiere una estrategia de secuencias separada.
- Cambiar el sender/PDF: Core verifica el contrato y el recorrido de staging.

### Plan de pruebas de ingeniería

Rutas: `POST /guaraniBeneficio/`, `PUT /guaraniBeneficio/requisito/{id}`,
`POST /guarani/alumno/create/preuniversitario`, cuotas pendientes, `mercadopago/makeContext`
e inconsistencias. El gate mínimo es `mvn -B verify`, más pruebas de contrato JSON y dos altas
de staging: 30 % y 100 %.

## Autoplan — Fase 3.5: revisión DX

**Producto:** API/servicio de Tesorería para integradores Guaraní, sender y operación de
Tesorería. **Persona primaria:** backend integrator que necesita saber qué enviar, qué recibe y
cómo diagnosticar una emisión sin beneficio.

| Dimensión | Puntaje | Corrección incorporada |
|---|---:|---|
| Inicio | 6/10 | Documentar payload mínimo y recorrido de staging |
| API/contrato | 7/10 | Separar explícitamente documento 100 % de contexto de pago |
| Errores | 6/10 | `warn` con causa e identificadores; 409 para duplicados |
| Documentación | 6/10 | Diagrama de flujo y contrato entre Core y sender |
| Upgrade | 8/10 | Cambio aditivo para series nuevas; rollback definido |
| Entorno/CI | 7/10 | `mvn -B verify` y contratos como gates |
| Ecosistema | 5/10 | Coordinación explícita con sender/Gire pendiente |
| Medición | 6/10 | Conciliación de fallbacks, 100 % y totales en staging |

### Recorrido del integrador

| Etapa | Resultado esperado | Evidencia |
|---|---|---|
| Configurar | `POST/PUT guaraniBeneficio` acepta 0..100 o responde 400/409 | controller tests |
| Enviar personales | `alumnoGuarani.personaRel.requisitosPresentados` llega intacto | fixture/payload staging |
| Emitir | serie guarda el porcentaje y genera cuotas | tests de integración |
| Entregar | `send-chequera` publica el evento para 0..100 | prueba Kafka/contrato |
| Cobrar | sólo cuotas positivas generan MercadoPago | test `makeContext` |
| Diagnosticar | fallback deja persona/documento/causa trazables | logs y conciliación |

### Checklist DX

- [ ] OpenAPI/README explica rango, 409 y payload de requisitos.
- [ ] Ejemplo JSON muestra una chequera parcial y otra 100 %.
- [ ] El contrato documental incluye filas de importe cero; el de pagos no.
- [ ] Todo fallback incluye problema, causa, identificadores y siguiente acción operativa.
- [ ] El rollout publica una guía corta de validación y rollback para Tesorería/sender.

**TTHW operacional:** un integrador existente debe validar una alta bonificada en menos de
cinco minutos con dos fixtures reproducibles y los comandos de `mvn -B verify`.

## Cross-phase themes

- El 100 % es una chequera informativa, no un cobro. CEO, ingeniería y DX coinciden: debe
  publicarse, incluir sus filas en el documento y quedar fuera de MercadoPago.
- La visibilidad del fallback a cero es requisito de operación, no un detalle de logging.

## Decision Audit Trail

| # | Phase | Decision | Classification | Principle | Rationale | Rejected |
|---|---|---|---|---|---|---|
| 1 | CEO | Mantener `send-chequera` con 100 % | User-confirmed | P1 | El alumno debe recibir la chequera bonificada | Suprimir el evento |
| 2 | CEO | Tratar 100 % como documento sin cobro | Mechanical | P5 | MercadoPago ya omite importes cero | Crear un pago de $0 |
| 3 | Eng | Usar `alumnoGuarani.personaRel` | Mechanical | P5 | Es la única ruta real con requisitos | `PersonalesResponse.persona` |
| 4 | Eng | Hacer null-safe el detector | Mechanical | P1 | Datos legados no pueden tumbar el endpoint | Dejar NPE |
| 5 | Eng | Separar consulta documental y de cobro | Mechanical | P1 | El PDF 100 % necesita filas cero; pagos no | Reutilizar pendientes positivos |
| 6 | Eng | Una referencia temporal por emisión | Mechanical | P5 | Evita flakiness y offsets inconsistentes | Múltiples llamadas a reloj |
| 7 | DX | Conciliar fallbacks y totales en staging | Mechanical | P1 | Detecta sobrecobros y drift antes de producción | Sólo logs dispersos |

## GSTACK REVIEW REPORT

| Review | Trigger | Why | Runs | Status | Findings |
|--------|---------|-----|------|--------|----------|
| CEO Review | `/plan-ceo-review` | Scope & strategy | 0 | — | — |
| Codex Review | `/codex review` | Independent 2nd opinion | 0 | — | — |
| Eng Review | `/plan-eng-review` | Architecture & tests | 1 | CLEAR | 5 findings folded into the plan |
| Design Review | `/plan-design-review` | UI/UX gaps | 0 | — | Backend scope |
| DX Review | `/plan-devex-review` | Developer experience gaps | 0 | — | — |

**VERDICT:** ENG CLEARED — ready to implement once rollout gates are satisfied.
NO UNRESOLVED DECISIONS
