# Plan — Saneamiento de `chequera_cuota` (arancel parcial) antes del recálculo

> **Para el agente que toma este plan:** este documento asume que NO tenés el contexto de la conversación
> previa. Todo lo marcado como **VERIFICADO** salió de leer el código y de consultar la base de desarrollo
> real (`tesium`, MySQL) y de ejecutar el endpoint contra ella. Todo lo marcado como **DECISIÓN** requiere
> confirmación humana antes de codificar. No asumas nada fuera de lo acá escrito.

---

## 0. Resumen ejecutivo

El endpoint de recálculo de cuota

```
GET /api/tesoreria/core/politicaArancelaria/recalculate/cuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}/plazo/{dias}
```

**no aplica el porcentaje de arancel parcial en ningún punto del cálculo**: se limita a copiar el
`importe1` de la *cuota de referencia* (la "cuota actual") a la *cuota en revisión*. Por eso, cuando los
datos de `chequera_cuota` vienen con el importe completo (`importeN == importeN_original`), el endpoint
devuelve el valor completo en lugar del 50 %.

La propuesta a ejecutar es **sanitizar los importes antes de recalcular**, de modo que el cálculo parta de
un contexto correcto (importe = base × porcentaje / 100). Pero hay dos salvedades que deben respetarse:

1. **La sanitización debe cubrir la cuota de referencia, no sólo la cuota analizada.** El resultado sale de
   `cuotaReferencia.getImporte1()`; sanitizar sólo la cuota en revisión **no cambia la salida** (demostrado
   en §3.1).
2. La corrección se hará **por demanda, de a una cuota por vez** (decisión de negocio): cada invocación
   sanea **la cuota en revisión y su cuota de referencia**. **No** se hace saneamiento masivo ni se modifica
   la generación en este trabajo (ver §5 y §6: quedan documentados como causa raíz / fuera de alcance).

### Decisiones ya tomadas

| # | Decisión |
|---|---|
| Alcance | Sanitizar **cuota en revisión + cuota de referencia**, por demanda, de a una cuota por vez (§3.4). |
| Persistencia | **Sí**: los importes saneados de ambas cuotas **se guardan en la base** (`chequera_cuota`), ver §4 Paso 4. |
| Masivo | **No** se hace saneamiento masivo de datos ya propagados (§5 fuera de alcance). |
| Generación | **No** se modifica la generación en este trabajo; queda como causa raíz documentada (§6 fuera de alcance). |

---

## 1. Problema

El negocio espera que, para un alumno con **arancel parcial**, la cuota recalculada tome el valor parcial
(por ejemplo, 50 % del valor de referencia) y no el completo. Caso de referencia:

- Chequera `2 / 517 / 2788`, producto 3 (Arancel), alternativa 1, cuota 1, plazo 10 días.
- `arancel_porcentaje` de `arancelTipoId = 46`, `productoId = 3` → **50 %** (`productoId = 2` → 100 %).
- Resultado correcto esperado: `importe3 = 190000`, `vencimiento3 = hoy + 10 días`.

## 2. Evidencia verificada

### 2.1 Flujo real del endpoint

| Paso | Clase / método | Archivo |
|---|---|---|
| Controller | `PoliticaArancelariaController.recalculateCuotaByUnique` (líneas 18‑29) | `src/main/java/um/tesoreria/core/hexagonal/chequera/politicaArancelaria/infrastructure/web/controller/PoliticaArancelariaController.java` |
| Service | `PoliticaArancelariaService.recalculateCuotaByUniqueIndex` | `.../politicaArancelaria/application/service/PoliticaArancelariaService.java` |
| Use case | `RecalculateCuotaByUniqueIndexUseCaseImpl.recalculateCuota` (líneas 28‑46) | `.../politicaArancelaria/application/usecases/RecalculateCuotaByUniqueIndexUseCaseImpl.java` |
| Mapper | `PoliticaArancelariaDtoMapper.toResponse` | `.../politicaArancelaria/infrastructure/web/mapper/PoliticaArancelariaDtoMapper.java` |

Secuencia interna de `recalculateCuota` (líneas 28‑46):

1. `findCuotaEnRevision` (líneas 48‑58) → `chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)`.
2. **Corte temprano** (líneas 33‑35): si `cuotaEnRevision.getVencimiento3().isAfter(ahora)` → devuelve la cuota sin cambios.
3. `resolveCuotaReferencia` (líneas 68‑90) → `chequeraCuotaService.getCuotaActual(...)`. Si no encuentra, **fallback** a `lectivoCuotaService.findCuotaByFecha(...)` (importes base **sin** parcial) — `buildChequeraCuotaFromLectivo` (líneas 101‑121).
4. `resolveImporteReferencia` (líneas 92‑99):
   ```java
   var importeBase = cuotaReferencia.getImporte1();
   if (cuotaEnRevision.getImporte3().compareTo(importeBase) > 0) return cuotaEnRevision.getImporte3();
   return importeBase;
   ```
5. `cuotaEnRevision.setImporte3(importeBase)` y `setVencimiento3(Tool.firstTime(ahora).plusDays(plazo))` (líneas 42‑43).
6. Devuelve el dominio modificado (el controller lo mapea). **No persiste nada** (verificado: la fila en BD queda igual después de la llamada).

**No hay ninguna lectura de `arancel_porcentaje` ni de `beca_porcentaje` en este camino.**

### 2.2 Comparación de datos (misma chequera, mismos IDs)

**Antes** (base con el parcial aplicado) vs **Ahora** (base recargada con datos de producción):

| Dato | Antes | Ahora |
|---|---|---|
| `chequera_cuota` cuota 1 (pro 3) `ChC_1er_Importe` / `importe1_original` | 162500 / 325000 | **325000 / 325000** |
| `chequera_cuota` cuota 1 `ChC_2do_Importe` / `importe2_original` | 170625 / 341250 | **341250 / 341250** |
| `chequera_cuota` cuota 1 `ChC_3er_Importe` / `importe3_original` | 178750 / 357500 | **357500 / 357500** |
| `chequera_cuota` cuota 8 (referencia) `ChC_1er_Importe` / `importe1_original` | 190000 / 380000 | **380000 / 380000** |
| `arancel_porcentaje` (46 / 3) | 50.00 | 50.00 (sin cambios) |
| `lectivo_cuota` lectivo 37, pro 3: cuota 1 / cuota 8 | 325000 / 380000 | 325000 / 380000 (sin cambios) |
| `chequera_serie.beca_porcentaje` | 0.00 | 0.00 |
| **Respuesta del endpoint (`importe3`)** | **190000** ✅ | **380000** ❌ (valor completo) |
| `vencimiento3` | hoy + 10 días | hoy + 10 días |

En ambos casos la referencia elegida fue la **cuota 8** (`v1 = 2026‑10‑10`, `v2 = 2026‑10‑31`).

Interpretación: `importeN_original` **es la base completa** en los dos escenarios (coincide con
`lectivo_cuota.importeN`). Lo que cambió es sólo `importeN` de `chequera_cuota`: pasó de "50 % de la base"
a "la base completa".

### 2.3 Causa raíz (VERIFICADO en código)

Los generadores de cuotas de este servicio **no aplican `arancel_porcentaje`**: escriben
`importeN == importeN_original == lectivo_cuota.importeN` (valor completo).

- `PreuniversitarioChequeraDetailsCreator.createCuotas` — líneas 90‑96
  (`... source.getImporte1(), source.getImporte1(), ...`).
  `src/main/java/um/tesoreria/core/hexagonal/chequera/chequeraSerie/application/service/PreuniversitarioChequeraDetailsCreator.java`
- `SpoterService` — líneas 194‑200 (mismo patrón).
  `src/main/java/um/tesoreria/core/service/transactional/spoter/SpoterService.java`
- El porcentaje **sí** se aplica únicamente en la "plantilla" legacy:
  `PlantillaArancelService.findAllByPlantilla` (líneas 39‑41):
  `importe1 = modelo.getImporte1().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100))`,
  expuesta por `PlantillaArancelController` (`/.../plantillaArancel/...`). Es decir, el dato correcto
  depende de un generador **externo** que use esa plantilla.

Conclusión: si producción regeneró chequeras con los generadores de este servicio (o cargó datos
equivalentes), quedaron importes completos. Ese es "el problema en otro lado".

### 2.4 Hallazgos secundarios (VERIFICADOS)

- **`GetCuotaActualUseCaseImpl` ignora `productoId`**: recibe `productoId` pero consulta
  `repository.findAllByChequeraAlternativa(facultadId, tipoChequeraId, chequeraSerieId, alternativaId)`
  (sin filtrar por producto) y toma la primera cuota por `ORDER BY vencimiento1, productoId, cuotaId`.
  Archivo: `.../chequera/chequeraCuota/application/usecases/GetCuotaActualUseCaseImpl.java` (líneas 21‑43).
  Riesgo: la referencia puede pertenecer a **otro producto** (en el caso analizado coincidió con el
  producto 3 por casualidad del orden).
- **El orden del sort** se define en `JpaChequeraCuotaRepositoryAdapter.findAllByChequeraAlternativa`
  (líneas 81‑89): `vencimiento1 ASC, productoId ASC, cuotaId ASC`.
- **`importeN_original`** hoy coincide con `lectivo_cuota.importeN` (base completa), pero no hay una regla
  escrita que garantice eso: usar `lectivo_cuota` como fuente canónica y `importeN_original` como
  respaldo/cross-check.
- **Escalas distintas**: `arancel_porcentaje.porcentaje` es un **porcentaje** (50.00 = 50 %), mientras que
  `chequera_serie.beca_porcentaje` es una **fracción** (0.5 = 50 %, ver `@Schema` en
  `ChequeraEstadoUsuarioResponse` y `movePointRight(2)` en el PDF). No mezclar sin normalizar.
- **`divide(new BigDecimal(100))`** de `PlantillaArancelService` puede lanzar `ArithmeticException` con
  decimales no terminantes; en la nueva implementación usar escala + `RoundingMode` explícitos.

---

## 3. Enfoque propuesto: sanitizar antes de recalcular

### 3.1 Salvedad crítica — hay que sanitizar la REFERENCIA

`resolveImporteReferencia` devuelve `cuotaReferencia.getImporte1()`. Con los datos actuales:

- Sanitizar **sólo** la cuota en revisión (cuota 1): `max(178750, 380000) = 380000` → **sigue mal**.
- Sanitizar **la referencia** (cuota 8): `max(178750, 190000) = 190000` → **correcto**.

Por lo tanto la sanitización debe alcanzar, como mínimo, a **la cuota de referencia**; y para "que el
contexto quede correcto" lo natural es sanear **todas las cuotas de la chequera/alternativa** (o al menos
la en revisión + la de referencia).

### 3.2 Fórmula canónica

Para cada cuota y cada vencimiento `n ∈ {1,2,3}`:

```
base_n        = lectivo_cuota.importeN            (fuente canónica, por facultad+lectivo+tipoChequera+producto+alternativa+cuota)
porcentaje    = arancel_porcentaje(arancelTipoId, productoId).porcentaje   // 100 si no existe la fila
importeN      = base_n × porcentaje / 100          // escala y RoundingMode explícitos
importeN_original = base_n                          // se preserva/mantiene
```

- Es **idempotente**: si el dato ya está bien (`importeN = 50 % de la base`), el recálculo da el mismo valor.
- No tocar `vencimiento*` ni flags (`pagado`, `baja`, `compensada`, `manual`).
- Cuotas sin precio (base 0) quedan en 0.
- **DECISIÓN**: si además debe aplicarse `beca_porcentaje` de la serie (hoy 0.00 y no lo usa la plantilla).
  Si se aplica, definir orden/composición con `arancel_porcentaje` para evitar doble descuento.

### 3.3 Dónde sanitizar (opciones)

| Opción | Descripción | Pros | Contras |
|---|---|---|---|
| **A. En memoria, en el recálculo** | Antes de calcular, reemplazar los importes usados (`cuotaEnRevision` y `cuotaReferencia`) por los efectivos | Simple, no cambia el contrato de escritura, idempotente | No corrige los datos; sólo la cuota que se recalcula |
| **B. En memoria + persistir lo saneado** | Igual que A, pero guardando los importes corregidos de la(s) cuota(s) saneada(s) | Corrige datos de a una | Convierte un GET en escritor; hay que decidir alcance y transacción |
| **C. Saneamiento masivo + generación corregida** | Job/script de reparación sobre las filas afectadas + fix en `PreuniversitarioChequeraDetailsCreator` y `SpoterService` (+ `GetCuotaActualUseCaseImpl`) | Ataca la causa raíz y la propagación | Requiere migración de datos y validación con negocio |

**Decidido:** se implementa **B** — sanitización **por demanda y de a una cuota por vez** (la cuota en
revisión + su cuota de referencia) **persistiendo en `chequera_cuota` los importes corregidos**. Se descarta
**C** por ahora (§5/§6 fuera de alcance). Ver §4 Paso 4 (persistencia y transacción) y §10 (riesgos).

### 3.4 Alcance de la sanitización — DECIDIDO

- **(i) la cuota en revisión + la cuota de referencia, por demanda y de a una cuota por vez.** Es el mínimo
  para que el resultado sea correcto (ver §3.1) y respeta la decisión de negocio de no hacer saneamiento
  masivo. Los importes saneados de **ambas** cuotas se persisten (§4 Paso 4).
- ~~(ii) toda la chequera/alternativa~~ — **descartado**.
- ~~(iii) todas las chequeras afectadas~~ — **descartado** (§5 fuera de alcance).

---

## 4. Pasos de implementación (opción B)

> Reglas del repo a respetar (ver §8): nuevo desarrollo en `hexagonal/`; `domain/` puro; los controllers
> hablan DTO; composición cross-slice en la capa de aplicación; **cualquier tipo de otro slice → preguntar
> al humano** (acá se asume autorizado, pero dejarlo anotado en el PR).

### Paso 1 — Extraer la aritmética a dominio (testeable)

Agregar a `ChequeraCuota` (`.../chequera/chequeraCuota/domain/model/ChequeraCuota.java`) un método puro:

```java
public void aplicarPorcentaje(BigDecimal porcentaje) {
    // importeN = importeN_original * porcentaje / 100, escala 4, HALF_UP
}
```

o un pequeño `domain/service` `ArancelParcialCalculator`. **No** meter acceso a datos en `domain/`.

### Paso 2 — Obtener base y porcentaje (capa de aplicación)

En `RecalculateCuotaByUniqueIndexUseCaseImpl` (o en un nuevo use case de sanitización) resolver:

- **Base**: `LectivoCuotaService.findByUniqueKey(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, cuotaId)`
  (ya existe; `LectivoCuota` tiene `importe1/2/3`).
- **Porcentaje**: `ArancelPorcentajeService.findByUnique(arancelTipoId, productoId)`.
  `arancelTipoId` está en `cuotaEnRevision.getArancelTipoId()` (y en la serie). Si lanza
  `ArancelPorcentajeException` → usar `100`.
- **Lectivo de la serie** (si hiciera falta): `ChequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId)`.

`politicaArancelaria` ya compone servicios de otros slices (`LectivoCuotaService`, `LectivoService`), así
que sumar `ArancelPorcentajeService`/`ChequeraSerieService` sigue el patrón existente (ver `estadoChequera`).

### Paso 3 — Aplicar la sanitización en el flujo

En `recalculateCuota` (líneas 28‑46), **después** de resolver `cuotaEnRevision` y `cuotaReferencia` y
**antes** de `resolveImporteReferencia`:

1. sanear `cuotaReferencia` (imprescindible, §3.1);
2. sanear `cuotaEnRevision` (para que su `importe3` sea consistente);
3. persistir (ver Paso 4) los importes saneados de la **referencia** y de la **cuota en revisión**.

Mantener `resolveImporteReferencia` como está (el `max`), ahora operando sobre valores saneados.

### Paso 4 — Persistir lo saneado (DECIDIDO: sí)

- Guardar en `chequera_cuota` los importes corregidos de **la cuota en revisión y de la referencia**, con
  `ChequeraCuotaService.update(cuota, chequeraCuotaId)` o `ChequeraCuotaService.saveAll(...)`.
- Envolver toda la operación (sanear + recalcular + guardar) en **una transacción** (`@Transactional` en el
  servicio de aplicación o en el use case); si falla el guardado, no dejar estado parcial.
- Orden sugerido:
  1. sanear la **referencia** y persistirla;
  2. sanear la **cuota en revisión**;
  3. aplicar el recálculo (`importe3` = `referencia.importe1` saneado; `vencimiento3` = hoy + plazo);
  4. persistir la **cuota en revisión**.
- **Guardas**: no reescribir cuotas con `pagado = 1` ni `baja = 1` (confirmar con negocio); no tocar
  `vencimiento1`/`vencimiento2` ni los flags. La idempotencia la garantiza la fórmula (§3.2).
- **Contrato**: el endpoint **pasa a escribir** (hoy es un GET de sólo lectura). Documentarlo en
  `CHANGELOG.md` y `README.md` y definir el criterio de versión (no cambia la URL ni el esquema de respuesta,
  pero sí el comportamiento).
- **Verificación**: el perfil `it` usa un pool **read-only**; los tests que persisten deben correr sobre H2 /
  base con permisos de escritura (ver §7 y §11).

### Paso 5 — Corregir `GetCuotaActualUseCaseImpl` (hallazgo secundario, §2.4)

Evaluar filtrar por `productoId`. **Ojo**: cambia el comportamiento actual; requiere test y confirmación.

---

## 5. FUERA DE ALCANCE (por ahora) — Saneamiento masivo de datos ya propagados (punto C)

> **Decisión de negocio:** no se ejecuta en este trabajo. La corrección será **por demanda, de a una cuota
> por vez** (§3.4). Este apartado queda documentado como referencia para una eventual tarea futura.

Script versionado (el repo usa `docs/sql/V###__descripcion.sql`, ver `docs/sql/V413__compras_recepcion.sql`)
que corrija `chequera_cuota` para las chequeras afectadas:

```sql
-- Bosquejo (VERIFICAR nombres de columnas y reglas con negocio antes de correr)
UPDATE chequera_cuota cc
JOIN chequera_serie  cs ON cs.clave = cc.chequera_id
JOIN lectivo_cuota   lc ON  lc.LeC_Fac_ID = cc.ChC_Fac_ID
                       AND lc.LeC_Lec_ID = cs.ChS_Lec_ID
                       AND lc.LeC_TCh_ID = cc.ChC_TCh_ID
                       AND lc.LeC_Pro_ID = cc.ChC_Pro_ID
                       AND lc.LeC_Alt_ID = cc.ChC_Alt_ID
                       AND lc.LeC_Cuo_ID = cc.ChC_Cuo_ID
JOIN arancel_porcentaje ap ON ap.aranceltipo_id = cs.ChS_ArT_ID
                          AND ap.producto_id    = cc.ChC_Pro_ID
SET cc.ChC_1er_Importe   = ROUND(lc.LeC_1er_Importe * ap.porcentaje / 100, 4),
    cc.ChC_2do_Importe   = ROUND(lc.LeC_2do_Importe * ap.porcentaje / 100, 4),
    cc.ChC_3er_Importe   = ROUND(lc.LeC_3er_Importe * ap.porcentaje / 100, 4)
WHERE cc.ChC_Pagado = 0 AND cc.ChC_Baja = 0;
```

Requisitos antes de correr:

1. **Backup** de `chequera_cuota` (o tabla temporal `chequera_cuota_bkp_YYYYMMDD`).
2. Consulta de **conteo previo** (¿cuántas filas tienen `importeN <> importeN_original`? ¿cuántas no?).
3. Definir si se limita por lectivo/serie/arancel tipo y si se excluyen cuotas pagadas/baja.
4. Política de **reversión** y validación post-update (ver §11).

> Recordatorio: `ddl-auto: none`, el esquema se gestiona fuera de la app; todo cambio de datos/estructura va
> por script versionado.

---

## 6. FUERA DE ALCANCE (por ahora) — Corregir la generación (causa raíz §2.3)

> **Decisión de negocio:** no se toca la generación en este trabajo. Queda como causa raíz documentada;
> mientras tanto, la sanitización por demanda (§3) compensa el dato malo que siga apareciendo.

1. `PreuniversitarioChequeraDetailsCreator.createCuotas` (líneas 90‑96): aplicar el porcentaje al construir
   `importeN` (dejar `importeN_original = base`).
2. `SpoterService` (líneas 194‑200): ídem.
3. Reutilizar la misma lógica que la sanitización (Paso 1/2) para no duplicar la fórmula.
4. Evaluar unificar con `PlantillaArancelService` (que ya aplica el porcentaje) para que haya **una** regla.

**Riesgo:** tocar generación legacy puede afectar otros consumidores; encapsular y testear.

---

## 7. Tests

- **Dominio**: `aplicarPorcentaje` (50 %, 100 %, 0 %, nulos, escalas/redondeo, idempotencia).
- **Use case** (`RecalculateCuotaByUniqueIndexUseCaseImplTest`, Mockito manual, patrón de
  `GetEstadoChequeraUseCaseImplTest`):
  - referencia con importe completo + porcentaje 50 % ⇒ `importe3 = base × 50 %`;
  - referencia ya parcial ⇒ mismo resultado (idempotencia);
  - sin fila en `arancel_porcentaje` ⇒ 100 %;
  - fallback a `lectivo_cuota` (referencia inexistente) ⇒ también aplica parcial;
  - corte temprano por `vencimiento3` intacto;
  - **persistencia**: se invoca el `update`/`save` con los importes saneados de la **referencia** y de la
    **cuota en revisión** (verificar con mock del repositorio / puerto de salida).
- **Controller** (si se cambia contrato): `@WebMvcTest` + `@Import(...DtoMapper.class)` + `MockMvcTester`.
- Verificar con `mvn -q -DskipTests compile` y `mvn -Dtest=<Test> test` antes de dar por cerrado.
- ⚠️ Los tests que **persisten** no pueden validarse con el perfil `it` (pool read-only): usar H2 o una base
  con permisos de escritura.

---

## 8. Reglas del repo (AGENTS.md) aplicables

- Nuevo desarrollo va a `hexagonal/`; no agregar features al legacy.
- `domain/` 100 % puro (`java.*` + Lombok); `application/` sólo importa `domain/`; los controllers hablan DTOs.
- Necesidad de un tipo de **otro slice** → **preguntar al humano** (opciones de la skill `hexagonal-arch`).
  Acá ya está propuesto componer servicios de `arancelPorcentaje`/`lectivoCuota`/`chequeraSerie`; dejarlo
  explícito en el PR.
- Cambio de URL/contrato público ⇒ **major** de SemVer + migración documentada. **No** agregar seguridad a
  endpoints/legacy existentes.
- Release (skill `release-documentation`): actualizar juntos `pom.xml`, `CHANGELOG.md`, `README.md`,
  `docs/README.md` y los `docs/hexagonal-*.mmd` afectados.
- Commits: Conventional Commits con scope de slice (`fix(chequera/politicaArancelaria): ...`).
- Rama por issue `<number>-<kebab-title>`; PR a `develop`.

---

## 9. Decisiones abiertas (confirmar ANTES de codificar)

1. **Alcance** — ✅ **RESUELTO**: cuota en revisión + cuota de referencia, por demanda, de a una cuota por
   vez (§3.4).
2. **Persistencia** — ✅ **RESUELTO**: **se persisten** los importes saneados (cuota en revisión + referencia)
   en `chequera_cuota`, dentro de una transacción (opción B; ver §4 Paso 4).
3. **`beca_porcentaje`**: ¿se aplica junto con `arancel_porcentaje`? ¿cómo se combinan? (fracción vs porcentaje)
4. **Fuente de base**: ¿`lectivo_cuota` (recomendado) o `importeN_original`? ¿qué hacer si discrepan?
5. **Fila faltante** de `arancel_porcentaje`: ¿100 % o dejar el importe como está?
6. **`GetCuotaActualUseCaseImpl`**: ¿se corrige el filtro por `productoId` en el mismo cambio o va aparte?
7. ~~¿El saneamiento masivo (§5) entra en este trabajo?~~ — ✅ **RESUELTO**: no; se hace por demanda
   (§3.4, §5 fuera de alcance).

---

## 10. Riesgos

- **Doble descuento** si se combinan mal `arancel_porcentaje` (50) y `beca_porcentaje` (0.5).
- **Redondeo**: la fórmula original divide por 100 sin `RoundingMode`; definir escala (los importes son
  `decimal(20,4)`).
- **Escrituras**: si no se hace transaccional o se toca una cuota pagada, se corrompe el histórico.
- **Cambiar el orden/la referencia** (`getCuotaActual`) puede variar resultados en otras chequeras.
- **Corte temprano** (`vencimiento3 > ahora`): el endpoint puede no recalcular y devolver el valor crudo;
  tenerlo en cuenta al validar.
- **El endpoint pasa a escribir** (GET con efectos): sin transacción o con una guarda mal puesta se puede
  corromper el histórico (cuotas pagadas/baja). Riesgo de **concurrencia** si dos recálculos tocan la misma
  cuota; el repo ya tiene referencias de lectura bajo bloqueo + reintento en `compras/articulo` y
  `compras/ubicacionArticulo` — evaluar reutilizar ese patrón si aparece contención.

---

## 11. Verificación

Comandos:

```bash
mvn -q -DskipTests compile
mvn -Dtest=RecalculateCuotaByUniqueIndexUseCaseImplTest test
# el perfil it usa un pool READ-ONLY: sirve para verificar LECTURA, no la persistencia
# (crear .env desde .env.example; set -a; . ./.env; set +a)
mvn -Pit verify
```

> Para validar la **escritura** usar H2 (perfil de test por defecto) o una base con permisos de escritura.

Caso de aceptación (base de desarrollo, chequera `2/517/2788`, pro 3, alt 1, cuota 1, plazo 10):

- **Respuesta**: `importe3 = base(cuota actual) × 50 %` = **190000** con `vencimiento3 = hoy + 10 días`.
- **Idempotencia**: con datos ya parciales, mismo resultado.
- **Persistencia**: en BD la cuota en revisión (cuota 1) queda con `importe1/2/3` parciales
  (162500 / 170625 / **190000**) y `vencimiento3 = hoy + 10 días`; la referencia (cuota 8) queda con sus
  `importeN` parciales (190000 / 195700 / 201400). Confirmar con la consulta de control de abajo.

Consultas de control (ajustar credenciales fuera del repo):

```sql
SELECT ChC_ID, ChC_Pro_ID, ChC_Cuo_ID, ChC_1er_Importe, importe1_original,
       ChC_2do_Importe, importe2_original, ChC_3er_Importe, importe3_original
FROM chequera_cuota
WHERE ChC_Fac_ID = 2 AND ChC_TCh_ID = 517 AND ChC_ChS_ID = 2788
ORDER BY ChC_Pro_ID, ChC_Alt_ID, ChC_Cuo_ID;

SELECT * FROM arancel_porcentaje WHERE aranceltipo_id = 46;   -- 46/2 = 100, 46/3 = 50
SELECT * FROM lectivo_cuota
WHERE LeC_Fac_ID = 2 AND LeC_TCh_ID = 517 AND LeC_Pro_ID = 3 AND LeC_Alt_ID = 1 AND LeC_Lec_ID = 37
ORDER BY LeC_Cuo_ID;
```

---

## 12. Anexo — datos de la corrida de diagnóstico

Ejecutado contra la base real el 2026‑10‑15 (mismo código, mismos IDs):

```
NOW = 2026-10-15T12:32
REFERENCIA (getCuotaActual) -> cuotaId=8 productoId=3 importe1=380000 (original 380000) v1=2026-10-10 v2=2026-10-31
Cuota en revisión           -> cuotaId=1 productoId=3 importe3=357500 (original 357500) v3=2026-04-30
resolveImporteReferencia    -> Importe Referencia = 380000
RESPONSE -> importe1=325000 importe2=341250 importe3=380000 importe3Original=357500 vencimiento3=2026-10-25
BD posterior                -> sin cambios (el endpoint no persiste)
```

Antes de la recarga (mismo flujo), la referencia traía `importe1 = 190000` y la respuesta era
`importe3 = 190000` — es decir, el cálculo es el mismo; lo que cambió fueron los datos de `chequera_cuota`.

Clases Kotlin relevantes que pueden faltar al compilar incremental (hacer `mvn -q -DskipTests clean compile`
si aparece "class file for X not found"): `um.tesoreria.core.kotlin.model.*`.
