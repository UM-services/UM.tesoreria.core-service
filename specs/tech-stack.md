# Stack Técnico — UM.tesoreria.core-service

> Documento de constitución. Define **con qué construimos**, **qué reglas rigen** ese stack y **qué huecos reconocemos**.
>
> Versión del servicio al redactar: **4.1.1** · Fecha: **2026-08-19**
> Todo lo listado está verificado en `pom.xml`, `Dockerfile`, `.github/workflows/` y el árbol de fuentes.

---

## 1. Plataforma

| Pieza | Versión | Nota |
|---|---|---|
| Java | 25 | `<java.version>` en `pom.xml` |
| Kotlin | 2.4.10 | Solo legacy en extinción — **no se escribe Kotlin nuevo** |
| Spring Boot | 4.1.0 | Parent POM |
| Spring Cloud | 2025.1.2 | BOM |
| Maven | 3.8.8+ | |
| MySQL Connector/J | 26.7.0 | Runtime |
| Docker | — | Imagen JVM publicada en Docker Hub |

## 2. Dependencias principales

**Web y API**
`spring-boot-starter-web` · `spring-boot-starter-webflux` · `spring-boot-starter-validation` · `springdoc-openapi-starter-webmvc-ui` 3.1.0

**Persistencia**
`spring-boot-starter-data-jpa` · `spring-boot-starter-jdbc` · `mysql-connector-j` 26.7.0

**Ecosistema distribuido**
`spring-cloud-starter-consul-discovery` (service discovery) · `spring-cloud-starter-openfeign` + `feign-hc5` · `spring-cloud-starter-bootstrap` · `spring-kafka` · `RestClient` compartido vía `RestClientConfig` (read timeout 15 s)

**Observabilidad**
`spring-boot-starter-actuator` · `micrometer-registry-prometheus` · logging con `@Slf4j`

**Cache**
`spring-boot-starter-cache` + `caffeine`

**Utilidades**
`lombok` 1.18.38 · `modelmapper` 3.2.6 · `guava` 33.6.0-jre · `jackson-datatype-jsr310` · `json-path` 3.0.0 · `commons-fileupload` 1.6.0

**Reportes y archivos**
`poi` / `poi-ooxml` 5.5.1 (Excel) · `openpdf` 3.0.5 (PDF)

**Mail**
`spring-boot-starter-mail`

**Testing**
`spring-boot-starter-test` (JUnit 5 + Mockito + AssertJ) · `spring-boot-starter-data-jpa-test` · `spring-boot-starter-webmvc-test` · `h2` (con `INIT=SET REFERENTIAL_INTEGRITY FALSE` en `src/test/resources/application.yml`)

**Calidad**
`jacoco-maven-plugin` 0.8.13 · SonarCloud (`UM-services_UM.tesoreria.core-service`)

## 3. CI/CD

Cuatro workflows en `.github/workflows/`:

| Workflow | Disparador | Qué hace |
|---|---|---|
| `maven.yml` | push/PR a `main` | `mvn -B verify` + análisis SonarCloud; en push a `main` construye y publica la imagen JVM |
| `deploy-develop.yml` | push a `develop` | Verifica, construye, publica y despliega en develop |
| `deploy-staging.yml` | staging | Verifica, construye, publica y despliega en staging |
| `generate-docs.yml` | — | Valida los diagramas Mermaid y publica en GitHub Pages |

Ramas: `develop` → `staging` → `main`. Versionado SemVer en `pom.xml`, con nota de versión verificada en `../README.md`.

---

## 4. Arquitectura

**Hexagonal (puertos y adaptadores)** es el estándar. Cada módulo bajo `src/main/java/um/tesoreria/core/hexagonal/<contexto>/<modulo>/` sigue esta estructura, sin excepciones:

```
domain/
  model/                    # POJOs puros: sin JPA, sin Jackson, sin Spring
  ports/in/                 # Un caso de uso por interfaz
  ports/out/                # Contratos de repositorio
application/
  usecases/                 # *UseCaseImpl — la lógica de negocio vive acá
  service/                  # Fachada que delega a los casos de uso
  exception/                # <Modulo>Exception
infrastructure/
  persistence/
    entity/                 # *Entity JPA
    repository/             # Jpa*Repository (Spring Data)
    adapter/                # Jpa*RepositoryAdapter implementa el puerto out
    mapper/                 # entidad ↔ dominio
  web/
    controller/             # REST
    dto/                    # *Request / *Response
    mapper/                 # *DtoMapper: DTO ↔ dominio
```

Contextos existentes: `auth`, `chequera`, `compras`, `comprobante`, `contable`, `contratos`, `dependencias`, `extern`, `guarani`, `lectivo`, `matriculacionContext`, `mercadoPagoContext`, `personas`, `setup`, `track`, `ubicacionArticulo`, `umhub`, `usuario`.

### Reglas de arquitectura — no negociables

1. `domain/model` no importa `jakarta.persistence`, `org.springframework` ni tipos HTTP. La única anotación tolerada es `@JsonFormat` en campos de fecha (deuda conocida, ver §6).
2. Los casos de uso dependen de puertos, nunca de adaptadores ni de `Jpa*Repository`.
3. Inyección **por constructor** con `@RequiredArgsConstructor`. `@Autowired` en campos está prohibido en código nuevo (migración ya hecha en ~80 controllers y ~70 services en la 3.46.0).
4. Un caso de uso, una interfaz, un método. Nada de interfaces "de conveniencia" con cinco operaciones.
5. Los controllers devuelven `ResponseEntity` y traducen las excepciones de dominio a `ResponseStatusException`.
6. Todo módulo nuevo trae su diagrama `docs/hexagonal-<modulo>.mmd`, registrado en `docs/script.js` y validado por `generate-docs.yml`.
7. Dinero siempre en `BigDecimal`, con escala y redondeo explícitos.

---

## 5. Convenciones

- Rutas REST bajo `/api/tesoreria/core/**`. Las rutas cortas heredadas (ej. `/documento`) se conservan solo por compatibilidad de clientes.
- Nomenclatura: `*UseCase` / `*UseCaseImpl` / `*Service` / `*Repository` / `*Entity` / `*Mapper` / `*DtoMapper` / `*Request` / `*Response` / `*Exception`. Las interfaces **no** llevan prefijo `I`.
- Modelos de dominio: `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`; DTOs con `@Getter`/`@Setter` (no `@Data`).
- Serialización de logs vía la interfaz `Jsonifyable` (`jsonify()`), no concatenación manual.
- Fechas en la API: ISO 8601 con offset (`yyyy-MM-dd'T'HH:mm:ssXX`). Fechas de negocio con `OffsetDateTime`; utilidades horarias de Argentina en `Tool` (`dateAbsoluteArgentina()`, `firstTime()`).

---

## 6. Huecos declarados

Deuda reconocida y aceptada, no descubrimientos.

> **Estos huecos están declarados acá; el orden en que se atacan vive en `roadmap.md`.** La feature activa (`features/beneficio-requisitos-ingreso/spec.md`) solo cierra parcialmente H4. El resto figura en el roadmap como cola priorizada por riesgo (R2–R7), **no como trabajo comprometido**.
>
> Una excepción al criterio de "deuda pendiente":
> - **H4** es el hueco que la feature en curso cierra parcialmente.

### H1 — Cobertura de tests crítica

**Evidencia:** 19 archivos de test contra ~1476 archivos de producción en `src/main/java`. La cobertura existente está concentrada casi por completo en un solo módulo (`lectivoTotalImputacion`, 10 de los 19), más `auth`, `tesoreriaEstado`, `persona` y `preuniversitario`.

**Agravante:** `jacoco-maven-plugin` está configurado con `prepare-agent` y `report`, pero **sin goal `check` ni `<rules>`**. Nada rompe el build por cobertura insuficiente; SonarCloud reporta pero no bloquea.

**Dónde duele más:** ningún test cubre hoy `RecalculateCuotaByUniqueIndexUseCaseImpl`, `ChequeraCuotaService`, `LectivoCuotaService`, `GetDeudaExamenUseCaseImpl` ni `calculateDeuda`. Es decir: **la lógica que decide cuánta plata debe un alumno no tiene red de seguridad**. Contradice de frente el principio 2 de `mission.md`.

### H2 — Coexistencia legacy / doble modelo

**Evidencia:** conviviendo con 1085 archivos hexagonales quedan

| Paquete legacy | Archivos |
|---|---|
| `core/service/` | 105 |
| `core/repository/` | 84 |
| `core/model/` | 74 |
| `core/controller/` | 68 |
| `core/exception/` | 62 |
| `core/kotlin/` (model + repository, Kotlin) | 60 |
| `core/extern/` | 36 |

**El costo concreto:** la misma entidad puede existir dos veces con reglas distintas, y hay que saber cuál manda. Ya se filtra hacia adentro del código nuevo — `PreuniversitarioChequeraDetailsCreator` (hexagonal) importa `um.tesoreria.core.kotlin.model.ChequeraAlternativa`, `LectivoAlternativa`, `um.tesoreria.core.model.LectivoTotal` y tres services legacy.

### H3 — Sin gestión de esquema de base de datos

**Evidencia:** no hay Flyway ni Liquibase en `pom.xml`. El esquema MySQL vive fuera del repositorio.

**El costo concreto:** cada módulo hexagonal nuevo implicó un `CREATE TABLE` manual y no versionado (`guarani_beneficio`, `guarani_ubicacion`, `guarani_propuesta_tipo_chequera`). No hay forma de reconstruir un ambiente desde cero ni de auditar cuándo cambió una columna. Los tests con H2 dependen de `ddl-auto` y de desactivar la integridad referencial, lo que los aleja del comportamiento real de MySQL.

### H4 — Beneficios de Guaraní sin cablear  ▲ *en curso — es la feature activa*

**Evidencia verificada:** `GuaraniBeneficio` (`requisito` → `porcentajeBeneficio`) tiene módulo hexagonal completo —dominio, 5 casos de uso, adaptador JPA sobre `guarani_beneficio`, controller con 5 endpoints— pero **ninguna clase fuera de su propio paquete lo referencia**.

La cadena está cortada en tres puntos:

1. `PersonaGuarani.requisitosPresentados` (`List<RequisitoPresentadoGuarani>`) solo entra por `AlumnoGuaraniRequest`; no existe caso de uso que consulte los requisitos vigentes de una persona.
2. `PreuniversitarioChequeraDetailsCreator.createCuotas()` copia `importe1/2/3` desde `LectivoCuota` tal cual, sin aplicar bonificación. Además fija `importeN` e `importeNOriginal` con el **mismo** valor.
3. `SpoterService` (líneas 180-206) duplica ese bloque casi literalmente, con el mismo problema.

**En alcance de `features/beneficio-requisitos-ingreso/spec.md`:** los puntos 1 y 2, más la extracción de una `ChequeraCuotaFactory` compartida que elimina la duplicación del punto 3.

**Fuera de alcance, se mantiene como deuda:**

- **`RecalculateCuotaByUniqueIndexUseCaseImpl`** recalcula `importe3` y `vencimiento3` **sin conocer beneficios**: compara la cuota en revisión contra una de referencia y toma el mayor. Es un segundo camino de determinación de importes que quedará inconsistente con el de creación hasta que se cablee.
- **`ArancelPorcentaje`** (`aranceltipoId` + `productoId` → `porcentaje`) sufre exactamente lo mismo: `ChequeraCuota.arancelTipoId` se persiste, pero `ArancelPorcentajeService` no tiene consumidores fuera de su paquete. Quedan **dos mecanismos de descuento**, y su relación entre sí sigue sin definir.
- **`SpoterService` más allá de consumir la factory**: su migración a hexagonal no entra.

**Interacción crítica con `FindAllInconsistenciasUseCaseImpl`:** ese caso de uso marca una cuota como inconsistente si `importe1 > importe2` o `importe2 > importe3`, y si `importeNOriginal * 49 < importeN`. Aplicar el beneficio a un solo tramo puede romper la relación creciente y disparar falsas alarmas. El segundo chequeo es asimétrico —solo detecta importes demasiado grandes—, así que reducir `importeN` dejando el original intacto no lo dispara. Está cubierto por la fase F4 del roadmap.

### H5 — Observabilidad instrumentada pero no explotada

`micrometer-registry-prometheus` y Actuator están presentes, pero no hay métricas de negocio propias ni tracing distribuido. El síntoma es visible en el historial: se agregan `log.debug` ad hoc para diagnosticar (3.42.1, 3.49.0), lo que sugiere que las métricas existentes no alcanzan para entender qué pasó en producción.

### H6 — Sistemas externos heredados (VB6)

Existe un sistema heredado en VB6 que convive con este servicio. Queda **fuera del alcance** de cualquier trabajo en este repositorio: no se migra, no se integra y no se toca desde acá. Se anota únicamente para que cualquier decisión de contrato o de modelo de datos tenga presente que hay otro consumidor de la misma base fuera del ecosistema de microservicios.

### H7 — `calculateCodigoBarras` genera códigos corruptos en silencio

**Evidencia:** `UpdateBarrasUseCaseImpl.calculateCodigoBarras()` (línea 44 en adelante) arma el código Gire codificando `importe1` y las **diferencias** entre tramos:

```java
// 1er Importe     → new DecimalFormat("0000000").format(importe1)     7 dígitos, sin decimales
// Dif 2do Importe → (importe2 - importe1).setScale(0, HALF_UP)        5 dígitos
// Dif 3er Importe → (importe3 - importe2).setScale(0, HALF_UP)        5 dígitos
```

Tres defectos, todos verificados en código:

1. **Diferencias negativas no se frenan.** Si `importe3 < importe2`, `diferenciaImporte2` es negativa; el código escribe `log.debug(...)` y **continúa**. `DecimalFormat("00000")` sobre un negativo produce una cadena con signo, y el código de barras resultante tiene largo y contenido inválidos. Llega impreso al alumno y el banco lo rechaza.
2. **Sin control de desborde.** Las diferencias tienen 5 dígitos (máximo 99.999). `DecimalFormat("00000")` sobre un valor mayor imprime 6 dígitos en vez de truncar o fallar, corrompiendo el largo total. Lo mismo con `importe1` sobre 9.999.999.
3. **Dos modos de redondeo en la misma función.** `importe1` usa el default de `DecimalFormat` (**HALF_EVEN**); las diferencias usan `setScale(0, HALF_UP)`. Inconsistencia latente.

**Por qué importa:** el barcode no tiene decimales, así que **la moneda efectiva del sistema es el peso entero**. Cualquier importe con centavos genera divergencia entre lo que dice la base y lo que cobra el banco.

**Estado:** fuera del alcance del roadmap actual. La feature en curso lo evita por construcción —aplicar el mismo factor a los tres tramos escala las diferencias sin volverlas negativas ni desbordarlas— pero el defecto sigue latente para cualquier otro camino que modifique importes de a un tramo, en particular `RecalculateCuotaByUniqueIndexUseCaseImpl`, que escribe solo `importe3`.

---

## 7. Restricciones aceptadas

Cosas que **no** vamos a cambiar aunque incomoden:

- **Rutas REST duplicadas** se mantienen hasta verificar que ningún cliente las usa.
- **`@JsonFormat` en modelos de dominio**: filtración de infraestructura conocida y tolerada; corregirla rompería contratos de fecha.
- **ModelMapper** convive con mappers manuales. Los mappers nuevos se escriben a mano (más explícitos y testeables); ModelMapper no se erradica por ahora.
- **Kotlin** permanece hasta que el último archivo legacy migre. No se agrega Kotlin nuevo.

---

## 8. Documentos relacionados

- `mission.md` — propósito, audiencia y principios
- `roadmap.md` — en qué orden avanza el servicio completo
- `features/beneficio-requisitos-ingreso/spec.md` — feature activa
- `../docs/hexagonal-architecture.mmd` — diagrama general
