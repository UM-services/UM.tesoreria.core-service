# Decisiones — Beneficio por requisitos de ingreso de Guaraní

> Registro de las decisiones de negocio que gobiernan el cálculo del beneficio.
> Cada una con su fecha, quién la tomó y un ejemplo numérico.
>
> Alcance: chequera del alumno **preuniversitario** en `UM.tesoreria.core-service`.
> Fecha de cierre: **2026-08-19** · Versión del servicio: **4.1.1**

---

## Fórmula acordada

```
beneficioAplicado = MAX(porcentajeBeneficio de los requisitos del alumno
                          con requisitoIngreso = 'S' y activo = 'S')

importeN         = importeNLista × (1 - beneficioAplicado)
                       .setScale(0, RoundingMode.HALF_UP)

importeNOriginal = importeNLista          // sin ningún descuento
```

Los tres tramos (`importe1`, `importe2`, `importe3`) reciben **el mismo factor**.

Sin requisitos elegibles → `beneficioAplicado = 0` → importes idénticos al comportamiento actual.

---

## D1 — Acumulación entre requisitos: gana el más alto

**Decidido el 2026-08-19 por el equipo.**

Si un alumno presenta varios requisitos de ingreso con beneficio asociado, **se aplica el más alto**. No se suman, no se aplican en cascada.

**Ejemplo:** alumno con tres requisitos elegibles de `0.10`, `0.30` y `0.20` → `beneficioAplicado = 0.30` (30 %). Sobre una cuota de lista de $47.350, paga $33.145.

**Consecuencias técnicas:**

- No hace falta deduplicar la lista de requisitos presentados. `MAX` es idempotente: el mismo requisito repetido no cambia el resultado. `RequisitoPresentadoGuarani` tiene PK propia y no tiene unicidad por `(persona, requisito)`, así que los duplicados son posibles y ahora son inofensivos. Igual se fija con un test, porque es una propiedad de la que dependemos.
- El resultado nunca supera el mayor de sus entradas, lo que hace innecesario cualquier tope adicional (ver D3).

**Descartado:** acumulación aditiva (10+30+20 = 60 %), porque exige un tope artificial y produce el caso "descuento mayor al precio". Cascada multiplicativa (0,90 × 0,70 × 0,80 = 49,6 % de descuento), porque es más difícil de explicar en un mostrador y acá no aporta nada que `MAX` no resuelva.

---

## D2 — Vigencia: no hay

**Decidido el 2026-08-19 por el equipo.**

El beneficio **no tiene vencimiento**. La chequera del preuniversitario es un trámite puntual, no algo que se reevalúe en el tiempo.

**Consecuencias técnicas:**

- `RequisitoPresentadoGuarani.fechaPresentacion`, `.fechaVencimiento` y `.fechaAlta` **no se leen**.
- `BeneficioPolicy` no recibe parámetro de fecha. Es puramente funcional sobre `(requisitos, beneficios)`, sin dependencia del reloj — trivial de testear y sin tests que fallen según el día en que corran.
- El beneficio queda congelado de hecho: se calcula una vez, al crear la chequera.

---

## D3 — Tope: ninguno adicional; rango fraccional `[0, 1]` inclusive

**Confirmado contra la base real el 2026-08-20.**

Cada `porcentajeBeneficio` individual se valida en `[0, 1]` **inclusive**, con dos decimales, al cargarse en `guarani_beneficio`. `0.50` representa 50 % y `1.00`, 100 %. No hay ningún tope adicional en el cálculo.

**Por qué esta escala:** `guarani_beneficio.porcentaje_beneficio` y `chequera_serie.beca_porcentaje` son `DECIMAL(5,2)` y los datos reales se guardan como fracción. Con `MAX`, permitir `1.00` es necesario: es el caso de beneficio completo.

**Ejemplo:** `porcentajeBeneficio = 1.00` → cuota en cero → el alumno no debe nada.

**Consecuencias verificadas del caso 100 %:**

| Componente | Comportamiento con importes en cero |
|---|---|
| `FindAllInconsistenciasUseCaseImpl` | No dispara. `importesInvalidos` evalúa `0 > 0` → falso. `multiplicadoresInvalidos` evalúa `importeNOriginal × 49 < 0` → falso, porque el original guarda el precio de lista |
| `calculateCodigoBarras` | Sintácticamente correcto. `DecimalFormat("0000000").format(0)` → `"0000000"`; diferencias `0 - 0 = 0` → `"00000"`. Sin signo negativo, largo correcto |
| `importeNOriginal` | Conserva el precio de lista, así que se puede mostrar cuánto se bonificó |

**Pendiente de confirmación externa:** si un código de barras Gire con importe cero es **operativamente** válido para el banco, más allá de ser sintácticamente correcto.

---

## D4 — Tramos: los tres, con el mismo factor

**Decidido el 2026-08-19 por el equipo.**

`importe1`, `importe2` e `importe3` reciben el mismo porcentaje de beneficio.

**Aclaración de vocabulario:** no son tres cuotas. Son los tres tramos de vencimiento de **la misma** cuota, con precio creciente según cuándo se pague (temprano / a término / tardío).

**Por qué el mismo factor:** multiplicar los tres por el mismo `k` preserva automáticamente el orden creciente (`a ≤ b ≤ c` → `ka ≤ kb ≤ kc`), así que `FindAllInconsistenciasUseCaseImpl` no se dispara. Y las diferencias que codifica el código de barras (`importe2 - importe1`, `importe3 - importe2`) también se escalan por `k`: como `k ≤ 1`, **se achican**, nunca desbordan los 5 dígitos ni se vuelven negativas.

**Descartado:** bonificar solo `importe1` agranda la diferencia `importe2 - importe1`, que puede desbordar el campo de 5 dígitos del barcode. Bonificar solo `importe3` rompe el orden creciente, dispara `importesInvalidos`, y produce una diferencia negativa que `calculateCodigoBarras` acepta sin frenar (solo escribe `log.debug`), generando un código corrupto que llega impreso al alumno. Ver H7 en `../../tech-stack.md`.

---

## D5 — `importeNOriginal`: precio de lista, sin ningún descuento

**Decidido el 2026-08-19 por el equipo.**

`importe1Original`, `importe2Original` e `importe3Original` guardan el precio de lista, sin beneficio ni ningún otro descuento.

**Por qué:** es la semántica que el código ya asume en dos lugares independientes. `RecalculateCuotaByUniqueIndexUseCaseImpl` modifica `importe3` y deja `importe3Original` intacto, tratándolo como referencia inmutable. Y `FindAllInconsistenciasUseCaseImpl` chequea `importeNOriginal × 49 < importeN`, que solo tiene sentido si el original es la base contra la que se mide el valor efectivo.

**Beneficio para el mail:** con precio de lista y precio final en la misma fila, el ahorro sale de una resta, sin recalcular nada.

**Nota:** hoy, en creación, `importeN` e `importeNOriginal` nacen idénticos. Esta feature es el primer momento en que divergen de verdad.

---

## D6 — Redondeo: pesos enteros, HALF_UP

**Decidido el 2026-08-19 por el equipo.**

`setScale(0, RoundingMode.HALF_UP)` sobre el importe bonificado.

**Por qué no dos decimales:** el código de barras Gire no tiene decimales. `importe1` se codifica con `DecimalFormat("0000000")` (7 dígitos enteros) y las diferencias con `setScale(0, HALF_UP)`. Guardar centavos genera divergencia entre lo que registra la base y lo que cobra el banco — la moneda efectiva del sistema es el peso entero.

**Por qué HALF_UP y no HALF_EVEN:** alinea con el `setScale(0, HALF_UP)` que ya usan las diferencias en `calculateCodigoBarras`. Existe una inconsistencia latente ahí (`importe1` usa el default HALF_EVEN de `DecimalFormat`); adoptar HALF_UP no la resuelve pero tampoco la propaga. Unificar `calculateCodigoBarras` es trabajo aparte (H8).

**Ejemplo:** cuota de lista $47.350 con 33 % → $47.350 × 0,67 = $31.724,50 → **$31.725**.

---

## D7 — El beneficio se persiste en `ChequeraSerie.becaPorcentaje`

**Decidido el 2026-08-19 por el equipo.**

El porcentaje calculado se guarda en `ChequeraSerie.becaPorcentaje`, usando el hook que ya estaba reservado en `PreuniversitarioChequeraService.java:49-50`:

```java
// Determina beneficio
var becaPorcentaje = BigDecimal.ZERO;   // ← acá va la llamada a BeneficioPolicy
```

**Qué resuelve sin código adicional:**

- **Congelamiento.** Queda grabado al emitir; no se reevalúa. `ChequeraSerie` es el lugar natural: un beneficio por chequera, no por cuota.
- **Trazabilidad.** `becaResolucion`, `becaFecha` y `becaUserId` ya existen en el modelo para registrar el origen del beneficio.
- **Impresión.** `ChequeraService.track():190` ya copia `becaPorcentaje` a `ChequeraImpresionCabecera`, así que el dato llega solo a la chequera impresa.
- **Alcance.** La factory lo lee de `chequeraSerie.getBecaPorcentaje()`. `SpoterService` construye series sin beca, así que queda excluido del beneficio sin necesidad de una rama especial.

---

## D8 — `ChequeraTotal.total` = suma de las cuotas, no factor sobre el total

**Decidido el 2026-08-19 por el equipo:** el total impreso debe coincidir con la suma de las cuotas.

**Aplicar el factor al total no logra eso.** Cada cuota se redondea a entero por separado, así que la suma de cuotas redondeadas no es igual al total redondeado:

```
Total lista $47.350, tres cuotas de $15.783,33, beneficio 30 %

Factor al total:  47.350 × 0,70                   = $33.145
Suma de cuotas:   (15.783,33 × 0,70 → 11.048) × 3 = $33.144   ← difieren en $1
```

**El total se calcula como la suma de `importe1` de las cuotas ya redondeadas.** Coincide por construcción, sin deriva posible.

**No es una invención:** el sistema ya sostiene ese invariante en otro punto del ciclo. `PagoService.calcularPagado():214` reasigna `chequeraTotal.total` con `calculateTotalCuotasActivas(...)`, que suma `importe1` de las cuotas con `baja = 0`. Lo que falta es aplicarlo también **al crear**, no solo al registrar el primer pago.

**Corrige un defecto latente previo a esta feature:** hoy, si `LectivoTotal.total` difiere de `Σ importe1`, el total de la chequera cambia solo cuando entra el primer pago, en silencio. Con beneficio aplicado a las cuotas y no al total, esa diferencia dejaría de ser sutil.

**Requiere invertir el orden** en `PreuniversitarioChequeraDetailsCreator.create()`: hoy `createTotals()` corre antes que `createCuotas()`; pasa a correr después, recibiendo las cuotas guardadas y agrupándolas por `productoId`.

---

## Fuera de alcance

| Tema | Estado |
|---|---|
| `RecalculateCuotaByUniqueIndexUseCaseImpl` | No se cablea el beneficio. Segundo camino de determinación de importes, queda inconsistente con el de creación |
| `ArancelPorcentaje` | Segunda isla de descuentos sin consumidores. No se combina ni se resuelve acá |
| Recálculo de chequeras ya emitidas | No bloqueante; se decide aparte |
| VB6 | Sistema externo. Ver H6 |
| Corrupción del barcode con diferencias negativas | Ver H7. La feature lo evita por construcción, no lo corrige |

---

## Verificación — la cadena de `requisitoRel` (2026-08-19)

Se verificó eslabón por eslabón si `RequisitoPresentadoGuarani.requisitoRel` puede llegar poblado al cálculo. **La plomería está intacta: el core no lo pierde en ningún punto.**

```
POST /guarani/alumno/create/personales
  AlumnoGuaraniRequest.personaRel : PersonaGuarani        ← el DTO usa el modelo de dominio directo
        │
        │  AlumnoGuaraniDtoMapper.toDomain():40
        │    .personaRel(request.getPersonaRel())          ← pass-through por referencia
        ▼
  AlumnoGuarani.personaRel
        │
        │  AlumnoGuaraniDtoMapper.toPersonalesResponse():55
        │    .alumnoGuarani(alumnoGuarani)                 ← el mismo objeto, sin reconstruir
        ▼
  PersonalesResponse.alumnoGuarani.personaRel.requisitosPresentados[].requisitoRel
        │
        ▼
POST /guarani/alumno/create/preuniversitario
  CreatePreuniversitarioUseCaseImpl(PersonalesResponse)    ← llega completo
```

Ningún mapper reconstruye `PersonaGuarani` desde la base ni copia campo por campo: se pasa la referencia. Los cuatro niveles de anidamiento (`personaRel` → `requisitosPresentados` → `requisitoRel` → `requisitoIngreso`/`activo`) existen en las clases destino, así que Jackson los deserializa si vienen en el JSON.

**Lo que NO se pudo verificar desde el repositorio:** si Guaraní efectivamente envía `requisitoRel` anidado dentro de cada `requisitosPresentados`. No hay fixtures JSON, ni payloads de ejemplo, ni un cliente Feign hacia Guaraní — el core es **receptor**, no llamador. El dato depende enteramente de lo que arme el emisor.

**Riesgo adicional identificado:** el flujo son **dos** llamadas. El cliente recibe `PersonalesResponse` de la primera y debe reenviarlo a la segunda. Si ese cliente reconstruye el objeto en vez de reenviar lo que recibió, `personaRel` se pierde ahí — fuera de este repositorio y fuera del alcance de cualquier test de core.

**Cómo cerrarlo antes de F3** (10 minutos, no requiere código nuevo): `PersonaGuarani implements Jsonifyable`. Agregar un `log.debug("personaRel -> {}", personaRel.jsonify())` temporal en `CreatePreuniversitarioUseCaseImpl` y disparar un alta real en staging. El log muestra si `requisitosPresentados` llega y si cada elemento trae `requisitoRel`.

**Si viniera nulo:** el filtro de elegibilidad anularía todos los beneficios en silencio, sin errores. El plan B es filtrar contra el catálogo local en vez de contra `requisitoRel`, lo que exige que el core tenga acceso a los requisitos de Guaraní — trabajo que hoy no existe y que cambiaría el tamaño de F3.

---

## Abierto — pendiente de definición

1. **Contenido del aviso complementario de beca completa (100 %)** — es la **fase F7** del roadmap, bloqueada por definición, **no está fuera de alcance**. La chequera bonificada se envía siempre con `send-chequera`; falta decidir qué mensaje adicional la acompaña, qué dice y si el disparo es `becaPorcentaje == 1.00` o deuda total cero. Requiere además un endpoint nuevo en `sender-service`: `ChequeraClient` no expone ninguno que sirva para un mail sin chequera.
2. **Chequeras ya emitidas** sin beneficio — si se recalculan, se reemplazan, o se ajustan a mano. Declarado como no bloqueante el 2026-08-19.
3. **Código de barras con importe cero** — confirmar con el banco si un código Gire con importe cero es operativamente válido, más allá de ser sintácticamente correcto.
4. **`requisitoRel` poblado** — verificar contra un alta real en staging antes de empezar F3 (ver arriba).

---

## Documentos relacionados

- `spec.md` — contexto, criterios de aceptación y fases de implementación
- `../../tech-stack.md` — huecos declarados H1–H8
- `../../mission.md` — propósito y principios
