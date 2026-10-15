# Plan v2 — Sanitización de importes por arancel parcial en el recálculo de cuota

> **Contexto para el agente:** este documento reemplaza/complementa a `plan-sanitizacion-arancel-parcial.md` (v1).
> Nace de re-analizar el proceso sobre el estado actual: la base de desarrollo fue **recargada desde producción**
> y hoy los importes de `chequera_cuota` vienen **completos** (`importeN == importeN_original`), lo que hace que el
> endpoint de recálculo devuelva el valor completo en vez del parcial.
>
> Lo marcado como **VERIFICADO** salió de leer el código y de consultar la base real (`tesium`, MySQL) el 2026-10-15.
> Lo marcado como **DECISIÓN** requiere confirmación humana antes de codificar.
>
> **En esta etapa NO se busca la causa raíz** (el/los generadores que escriben importes completos). Sólo se
> implementa una **sanitización del proceso** de recálculo, que garantice que se parta de importes correctos.

---

## 0. Qué cambió respecto del plan v1

| Tema | v1 | v2 (este plan) |
|---|---|---|
| Estado de datos | "Ahora" ya venía completo y se asumía | Re-verificado con la base recargada |
| Respuesta del endpoint | Conceptualmente 380000 | Confirmado 380000 (chequera 2/517/2788, cuota 1) |
| Base del cálculo | `lectivo_cuota.importeN` (canónica) | **`importeN_original`** como base primaria (auto-contenida y estable); `lectivo_cuota` como cross-check |
| Impacto | No medido | Medido (§2.4) |
| Alcance | cuota en revisión + referencia | Confirmado revisión + referencia (§5) |
| Contrato | Se decide que el GET escribe | Confirmado: GET sanea y persiste (§6) |

### Decisiones tomadas (2026-10-15)

| # | Decisión |
|---|---|
| Base | **`importeN_original`** (auto-contenida). `lectivo_cuota` se usa como **cross-check**, no como base. |
| Alcance | **Cuota en revisión + cuota de referencia** por invocación. |
| Contrato | **El GET sanea y persiste** (para no tocar el cliente VB6). |
| Beca | **No** se compone en esta iteración: sólo `arancel_porcentaje`. |
| Comparación | **`importeN_original` vs `lectivo_cuota.importeN`** para detectar inconsistencias de base. |
| Guardas | Excluir de escritura cuotas `pagado=1`, `baja=1`, `compensada=1`, `manual=1`. |
| Porcentaje faltante | Si no hay fila en `arancel_porcentaje(arancelTipoId, productoId)` ⇒ factor = **1 (100%)**. |
| `getCuotaActual` por producto | **Fuera** de este cambio: issue aparte. |

---

## 1. Objetivo y no-objetivo

**Objetivo:** que el recálculo de política arancelaria (`recalculate/cuota/...`) opere **siempre** sobre importes
que respeten el porcentaje de arancel (y, eventualmente, la beca), sin depender de que el dato en `chequera_cuota`
esté bien; y (según decisión) dejar el dato corregido.

**No-objetivo (fuera de alcance en esta etapa):**
- Encontrar y corregir la causa raíz en los generadores.
- Saneamiento masivo de datos ya propagados (se documenta como script, no se ejecuta).

---

## 2. Estado verificado (2026-10-15)

### 2.1 Endpoint

```
GET /api/tesoreria/core/politicaArancelaria/recalculate/cuota/2/517/2788/3/1/1/plazo/10
```
Respuesta actual (extracto):
```json
{ "cuotaId":1, "arancelTipoId":46,
  "importe1":325000.0000, "importe1Original":325000.0000,
  "importe2":341250.0000, "importe2Original":341250.0000,
  "importe3":380000.0000, "importe3Original":357500.0000,
  "vencimiento3":"2026-10-25T00:00:00Z" }
```
Esperado: `importe3 = 190000` (parcial) y `vencimiento3 = hoy + 10`. **Hoy devuelve 380000 (completo).**

### 2.2 Datos

`chequera_cuota` (fac 2 / tCh 517 / serie 2788 / prod 3 / alt 1):

| cuota | i1 / o1 | i2 / o2 | i3 / o3 |
|---|---|---|---|
| 1 | 325000 / 325000 | 341250 / 341250 | 357500 / 357500 |
| 8 (referencia) | 380000 / 380000 | 391400 / 391400 | 402800 / 402800 |

`arancel_porcentaje`: `arancelTipoId=46` → producto 2 = **100**, producto 3 = **50**.
`lectivo_cuota` (lectivo 37, prod 3, alt 1): coincide con `importeN_original`.
`chequera_serie.beca_porcentaje` = 0.00 para esta chequera.

Interpretación: **`importeN_original` es la base completa** y quedó intacta; lo que se pisó fue `importeN`.

### 2.3 Flujo del endpoint (sin cambios respecto de v1)

| Paso | Clase/método |
|---|---|
| Controller | `PoliticaArancelariaController.recalculateCuotaByUnique` |
| Service | `PoliticaArancelariaService.recalculateCuotaByUniqueIndex` |
| Use case | `RecalculateCuotaByUniqueIndexUseCaseImpl.recalculateCuota` (líneas 28‑46) |
| Mapper | `PoliticaArancelariaDtoMapper.toResponse` |

`recalculateCuota`:
1. `findCuotaEnRevision` → `ChequeraCuotaService.findByUnique(...)` (o fallback a `LectivoCuota`).
2. Corte temprano si `vencimiento3 > ahora`.
3. `resolveCuotaReferencia` → `ChequeraCuotaService.getCuotaActual(...)` (o fallback a `LectivoCuota`).
4. `resolveImporteReferencia` = `max(cuotaEnRevision.importe3, cuotaReferencia.importe1)`.
5. `setImporte3(...)` y `setVencimiento3(hoy + plazo)`. **No persiste**.

**No hay lectura de `arancel_porcentaje` ni de `beca_porcentaje` en este camino.**

### 2.4 Impacto medido (base de desarrollo)

```sql
SELECT COUNT(*) FROM chequera_cuota cc
JOIN chequera_serie cs ON cs.clave = cc.chequera_id
JOIN arancel_porcentaje ap ON ap.aranceltipo_id = cs.ChS_ArT_ID AND ap.producto_id = cc.ChC_Pro_ID
WHERE ap.porcentaje < 100 AND cc.ChC_1er_Importe = cc.importe1_original AND cc.ChC_1er_Importe > 0;
```

| Grupo | Filas |
|---|---|
| `arancel<100` e `importe=original` (>0) | 21.614 |
| ... de esas, pagadas o baja | 20.275 |
| ... activas impagas (candidatas reales) | ~1.339 |
| `arancel<100` con `importe≠original` (ya parciales, **no tocar**) | 704.976 |

Conclusión: el volumen a reparar es acotado y mayormente histórico (pagado/baja).

---

## 3. Regla de sanitización (canónica)

Para cada cuota y cada vencimiento `n ∈ {1,2,3}`:

```
base_n     = importe{n}_original            // base completa, estable (DECIDIDO)
factor     = porcentaje / 100               // arancel_porcentaje(arancelTipoId, productoId); default 100 si no hay fila
importe_n  = base_n * factor                // escala 4, RoundingMode HALF_UP
importe{n}_original = base_n                // se preserva (no se toca)
```

Propiedades:
- **Idempotente**: si el dato ya está parcial, el recálculo da el mismo valor.
- No toca `vencimiento*` ni flags (`pagado`, `baja`, `manual`, `compensada`).
- Cuotas sin precio (base 0) quedan en 0.

**Base — DECIDIDO:** `importeN_original`.
- Es auto-contenida, refleja la base al momento de la generación y no depende de que `lectivo_cuota` no haya
  cambiado después.
- `lectivo_cuota.importeN` **no** se usa como base; se usa para el **diagnóstico** de consistencia (abajo).

**Diagnóstico (reproducir el cálculo original y comparar) — DECIDIDO:** `importeN_original` vs `lectivo_cuota.importeN`.
- Se reproduce el cálculo original: `reproducido = lectivo_cuota.importeN × factor`.
- Se obtiene el diseño con
  `lectivoCuotaService.findByUniqueKey(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, cuotaId)`
  (el `lectivoId` sale de `chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId).getLectivoId()`).
- Comparaciones y registro:
  - si `importeN_original ≠ lectivo_cuota.importeN` ⇒ **inconsistencia de base** (log de auditoría con ids, ambos
    valores y la diferencia);
  - adicionalmente, comparar `importeN_original × factor` contra el `importeN` **almacenado**: si difieren, es una fila
    que el saneamiento corrige (útil para dimensionar el impacto).
  - si `lectivo_cuota` no existe para esa cuota ⇒ se registra como inconsistencia y se continúa.
- El resultado del diagnóstico se **loguea** (y opcionalmente se expone de forma aditiva en la respuesta, a definir).
- Una inconsistencia detectada **no** cambia el valor a persistir: siempre se persiste `importeN_original × factor`.

**Porcentaje — fuente del `arancelTipoId`:**
- Primario: `cuota.getArancelTipoId()` (columna de la cuota; así lo usa el legacy `constructChequera`).
- Fallback: `chequeraSerie.getArancelTipoId()` si la cuota lo trae nulo.
- Si no existe fila en `arancel_porcentaje` → `factor = 1` (100%). **DECIDIDO**.

**Beca — DECIDIDO:** no se compone en esta iteración.
- El legacy `constructChequera` componía `original × (1 − becaPorcentaje) × (porcentaje/100)`, con `beca_porcentaje`
  como **fracción** (`decimal(5,2)`: 0.50 = 50%) y `arancel_porcentaje` como **porcentaje** (50 = 50%). Esa
  composición **queda para una tarea aparte** para no alterar importes de las ~1.4k chequeras con beca.

---

## 4. Dónde implementar (arquitectura)

Respetando `AGENTS.md` y la skill `hexagonal-arch`:

1. **Dominio (puro, testeable):** en `chequeraCuota`, un cálculo puro:
   - método `aplicarFactor(BigDecimal factor)` en `ChequeraCuota` o un `domain/service` `ChequeraCuotaArancelCalculator`.
   - recibe la cuota y el factor; devuelve los importes saneados. Sin acceso a datos.

2. **Capa de aplicación (nuevo caso de uso):** `SanitizeCuotaArancelUseCase` (en `politicaArancelaria`):
   - resuelve el `arancelTipoId` (cuota → serie) y el **porcentaje** por `productoId` (default 100%);
   - resuelve el `lectivoId` (vía `ChequeraSerieService`) y `lectivo_cuota` para el **diagnóstico** (§3);
   - aplica la regla §3, emite el diagnóstico y devuelve las cuotas saneadas (con su `chequeraCuotaId` para persistir).

3. **Integración en el flujo:** en `RecalculateCuotaByUniqueIndexUseCaseImpl.recalculateCuota`, **después** de resolver
   `cuotaEnRevision` y `cuotaReferencia` y **antes** de `resolveImporteReferencia`:
   1. sanear la **cuota en revisión** y la **referencia** en memoria;
   2. **persistir cada una sólo si el saneamiento corrigió algo** (importe almacenado ≠ `importeN_original × factor`);
   3. recalcular (`importe3 = referencia.importe1` saneado; `vencimiento3 = hoy + plazo`) — **sólo en la respuesta,
      NO se persiste** (lo guarda el cliente u otro proceso).

4. **Composición cross-slice:** `politicaArancelaria` ya compone `LectivoCuotaService` y `LectivoService`.
   Sumar `ArancelPorcentajeService` (y `ChequeraSerieService` si hace falta el `arancelTipoId`/`lectivoId`) sigue el
   patrón existente (`estadoChequera`). Dejarlo anotado en el PR.

`ArancelPorcentajeService` hoy **no tiene consumidores** fuera de su slice (verificado), así que este sería su primer uso.

---

## 5. Alcance de la sanitización — DECIDIDO

| Opción | Qué sanea en cada invocación | Veredicto |
|---|---|---|
| **A. Revisión + referencia** | 2 cuotas | **ELEGIDA** |
| B. Toda la chequera/alternativa | Todas las cuotas de `(facultad,tipoChequera,serie,alternativa)` | Descartada |

Se sanean **la cuota en revisión y su cuota de referencia** por invocación. Consecuencia aceptada: otras cuotas de
la misma chequera quedan como estén hasta que se recalculen o se corra el script masivo (§8.2). El hallazgo del
`getCuotaActual` sin filtro de producto (§7) puede hacer que la referencia sea de otro producto; se aborda aparte.

---

## 6. Persistencia y contrato — DECIDIDO

**Contrato:** **el GET sanea y persiste** (opción A), para no tocar el cliente VB6 que ya invoca este endpoint.
Implica documentar el cambio de comportamiento (hoy es de sólo lectura) y fijar versión (SemVer) en el release.

**Persistencia:**

> **Regla (decidida 2026-10-15, corrige la interpretación inicial):** se persiste **la sanitización**, no el recálculo.
> - Cuota en revisión: se persiste **sólo si el cálculo no corresponde** (importe almacenado ≠ `importeN_original × factor`).
> - Cuota de referencia: se persiste **sólo si se detecta mal el cálculo**.
> - El recálculo (`importe3`/`vencimiento3`) **no se persiste** en este proceso; se devuelve en la respuesta y lo guarda
>   el cliente u otro proceso.

- Persistir con `ChequeraCuotaService.saveAll(...)` (el adapter hace update si el `chequeraCuotaId` existe) **sólo** para
  las cuotas efectivamente corregidas.
- **`@Transactional`** en el servicio de aplicación / use case: si falla el guardado, no dejar estado parcial.
- **Guardas — DECIDIDO:** no reescribir cuotas con `pagado = 1`, `baja = 1`, `compensada = 1` ni `manual = 1`.
- Saltar cuotas **sintéticas** sin `chequeraCuotaId` (fallback a `LectivoCuota`): no son filas persistibles.
- La **idempotencia** la garantiza la fórmula (§3): con datos ya parciales no hay corrección y no se escribe.

---

## 7. Hallazgo secundario — `getCuotaActual` ignora `productoId` (DECISIÓN)

`GetCuotaActualUseCaseImpl.getCuotaActual` recibe `productoId` pero consulta
`findAllByChequeraAlternativa(...)` **sin filtrar por producto** y toma la primera por
`ORDER BY vencimiento1, productoId, cuotaId` (`JpaChequeraCuotaRepositoryAdapter.findAllByChequeraAlternativa`).
Riesgo: la referencia puede ser de **otro producto** (matrícula vs arancel). En el caso analizado coincidió por orden.

Es un endpoint público (`GET /chequeraCuota/cuotaActual/...`) además de ser usado por el recálculo: cambiarlo altera
comportamiento. **DECIDIDO:** se deja **fuera** de este cambio y se abre un **issue aparte** (el recálculo sigue con
el comportamiento actual de selección de referencia).

---

## 8. Fuera de alcance

### 8.1 Causa raíz (generadores)
No se toca en esta etapa. Documentado (verificado): `PreuniversitarioChequeraDetailsCreator.createCuotas` y
`SpoterService` escriben `importeN == importeN_original == lectivo_cuota.importeN` (completo). El porcentaje sólo
se aplica en `PlantillaArancelService.findAllByPlantilla` (legacy, expuesto por `/plantillaArancel/...`).

### 8.2 Saneamiento masivo (script versionado)
Se documenta como `docs/sql/V###__sanitizacion_arancel_parcial.sql` para una tarea futura. Bosquejo (VERIFICAR
columnas/reglas con negocio antes de correr):

```sql
-- NO ejecutar sin backup y conteo previo
UPDATE chequera_cuota cc
JOIN chequera_serie  cs ON cs.clave = cc.chequera_id
JOIN arancel_porcentaje ap ON ap.aranceltipo_id = cs.ChS_ArT_ID
                          AND ap.producto_id    = cc.ChC_Pro_ID
SET cc.ChC_1er_Importe = ROUND(cc.importe1_original * ap.porcentaje / 100, 4),
    cc.ChC_2do_Importe = ROUND(cc.importe2_original * ap.porcentaje / 100, 4),
    cc.ChC_3er_Importe = ROUND(cc.importe3_original * ap.porcentaje / 100, 4)
WHERE ap.porcentaje < 100
  AND cc.ChC_Pagado = 0 AND cc.ChC_Baja = 0;
```
Requisitos: backup/tabla temporal, conteo previo, alcance por lectivo/serie/arancel tipo, política de reversión.

---

## 9. Tests

- **Dominio**: `aplicarFactor` (50%, 100%, 0%, nulos, escala/redondeo, idempotencia).
- **Use case de saneamiento**: referencia/importes completos + 50% ⇒ parcial; ya parcial ⇒ idempotente; sin fila de
  `arancel_porcentaje` ⇒ 100%; cross-check con `lectivo_cuota`.
- **`RecalculateCuotaByUniqueIndexUseCaseImpl`**: salida correcta (`importe3 = referencia.importe1 × factor`);
  corte temprano intacto; fallback a `LectivoCuota` también saneado; persistencia invocada con los ids correctos
  (mock del puerto de salida).
- **Persistencia/transacción**: verificar en **H2** (el perfil `it` usa pool **read-only**; no sirve para escritura).

---

## 10. Verificación / criterio de aceptación

Caso (base dev, 2/517/2788, prod 3, alt 1, cuota 1, plazo 10):

- **Respuesta**: `importe3 = 190000` con `vencimiento3 = hoy + 10`.
- **Idempotencia**: con datos ya parciales, no hay corrección y no se escribe.
- **Persistencia de la sanitización** (no del recálculo):
  - cuota 1 (revisión) queda saneada `importe1/2/3 = 162500 / 170625 / 178750`; `importeN_original` sin cambios y
    **`vencimiento3` sin cambios** (el `190000`/`hoy+10` sólo va en la respuesta).
  - cuota 8 (referencia) queda `190000 / 195700 / 201400`.

```bash
mvn -q -DskipTests compile
mvn -Dtest=RecalculateCuotaByUniqueIndexUseCaseImplTest test
mvn -Dtest=SanitizeCuotaArancelUseCaseImplTest test
```

Consultas de control (credenciales fuera del repo):
```sql
SELECT ChC_ID, ChC_Pro_ID, ChC_Cuo_ID, ChC_1er_Importe, importe1_original,
       ChC_2do_Importe, importe2_original, ChC_3er_Importe, importe3_original
FROM chequera_cuota
WHERE ChC_Fac_ID=2 AND ChC_TCh_ID=517 AND ChC_ChS_ID=2788
ORDER BY ChC_Pro_ID, ChC_Alt_ID, ChC_Cuo_ID;

SELECT * FROM arancel_porcentaje WHERE aranceltipo_id=46;  -- 46/2=100, 46/3=50
```

---

## 11. Riesgos

- **Doble descuento** si más adelante se combinan `arancel_porcentaje` (porcentaje) y `beca_porcentaje` (fracción) mal.
- **Redondeo**: los importes son `decimal(20,4)`; definir escala + `RoundingMode` (la plantilla legacy divide por 100 sin control).
- **Escrituras**: sin transacción o tocando cuotas pagadas/baja se corrompe el histórico.
- **Referencia de otro producto** (§7).
- **Corte temprano** (`vencimiento3 > ahora`) devuelve el valor crudo sin recalcular.
- **GET que escribe**: concurrencia si dos recálculos tocan la misma cuota (evaluar locking/optimistic como en `compras`).
- **Base de cálculo**: si `importeN_original` estuviera también parcial en algún dato viejo, la base elegida (a) propagaría el error (mitigar con cross-check `lectivo_cuota`).

---

## 12. Reglas del repo y release

- Nuevo desarrollo en `hexagonal/`; `domain/` puro; controllers hablan DTO.
- Cross-slice (tipos de otro slice) → dejar explícito en el PR.
- Cambio de comportamiento del endpoint público ⇒ fijar SemVer (hoy v9.3.0) y documentar en `CHANGELOG.md`, `README.md`, `docs/README.md` y diagrama `docs/hexagonal-politicaArancelaria.mmd`.
- Commits Conventional Commits con scope (`fix(chequera/politicaArancelaria): ...` o `feat(chequera/politicaArancelaria): ...`).
- Rama por issue `<number>-<kebab-title>`; PR a `develop`.
- **No** agregar seguridad a endpoints/legacy existentes.

---

## 13. Decisiones — RESUELTAS (2026-10-15)

| # | Decisión | Resolución |
|---|---|---|
| 1 | Base del saneamiento | **`importeN_original`** |
| 2 | Alcance | **cuota en revisión + referencia** |
| 3 | Beca | **no** se compone (sólo `arancel_porcentaje`) |
| 4 | Persistencia/contrato | **GET sanea y persiste** |
| 5 | Comparación de inconsistencias | **`importeN_original` vs `lectivo_cuota.importeN`** |
| 6 | Guardas | excluir `pagado`/`baja`/`compensada`/`manual` |
| 7 | Fila faltante de `arancel_porcentaje` | factor = **1 (100%)** |
| 8 | `getCuotaActual` por producto | **issue aparte** |

No quedan decisiones abiertas para iniciar la implementación (Paso 1–5, §4).

---

## 14. Anexo — datos de la corrida (2026-10-15)

```
GET .../recalculate/cuota/2/517/2788/3/1/1/plazo/10
REFERENCIA (getCuotaActual) -> cuotaId=8 productoId=3 importe1=380000 (original 380000) v1=2026-10-10 v2=2026-10-31
Cuota en revisión           -> cuotaId=1 productoId=3 importe3=357500 original=357500 v3=2026-04-30
resolveImporteReferencia    -> max(357500, 380000) = 380000
RESPONSE -> importe3=380000 vencimiento3=2026-10-25   (esperado 190000)
BD posterior -> sin cambios (el endpoint no persiste)
```

Con sanitización por `arancel_porcentaje(46,3)=50%`: la referencia queda `importe1=190000`, la revisión
`importe3=178750`, y `resolveImporteReferencia` = `max(178750, 190000) = 190000` ✅.

---

## 15. Estado de implementación (2026-10-15, rama `develop`)

**Implementado (working tree):**

| Archivo | Cambio |
|---|---|
| `hexagonal/chequera/chequeraCuota/domain/model/ChequeraCuota.java` | método puro `aplicarPorcentajeArancel(BigDecimal)` (escala 4, HALF_UP; no toca original ni vencimientos). |
| `hexagonal/chequera/politicaArancelaria/domain/ports/in/SanitizeChequeraCuotaArancelUseCase.java` | nuevo puerto de entrada (`sanitize` devuelve si corrigió). |
| `hexagonal/chequera/politicaArancelaria/application/usecases/SanitizeChequeraCuotaArancelUseCaseImpl.java` | resuelve porcentaje (default 100), diagnostica `original` vs `lectivo_cuota`, aplica el % y reporta si corrigió. |
| `hexagonal/chequera/politicaArancelaria/application/usecases/RecalculateCuotaByUniqueIndexUseCaseImpl.java` | sanea **referencia + revisión**; **persiste cada una sólo si hubo corrección**; **no persiste el recálculo**. |
| `hexagonal/chequera/politicaArancelaria/application/service/PoliticaArancelariaService.java` | `@Transactional` (saneamiento + persistencia en una transacción). |

**Tests:** `ChequeraCuotaTest` (7), `SanitizeChequeraCuotaArancelUseCaseImplTest` (6),
`RecalculateCuotaByUniqueIndexUseCaseImplTest` (6). Suite completa: **924 tests, 0 fallos**.

**Verificación end-to-end (hecha, contenedor `tesoreria-core-service` redeployado):**
- Respuesta: `importe3 = 190000`, `vencimiento3 = 2026-10-25`.
- Base: cuota 1 → `162500 / 170625 / 178750` (vencimiento3 sin cambios); cuota 8 → `190000 / 195700 / 201400`;
  `importeN_original` intactos; el recálculo **no** persistido.
- Idempotente: 2ª llamada sin escrituras.

**Pendiente:**
- **Release docs** (`pom.xml`, `CHANGELOG.md`, `README.md`, `docs/README.md`, `docs/hexagonal-politicaArancelaria.mmd`):
  pendiente de decidir si entra como 9.4.0 o se incluye en la release 9.3.0 en curso (compras/recepción, aún sin commitear en `develop`).
