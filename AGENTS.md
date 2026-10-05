# AGENTS.md — Contexto para agentes de IA

Punto de entrada operativo para trabajar en `UM.tesoreria.core-service`. Denso y navegable: usa este archivo para ubicarte y luego abre la fuente de verdad indicada (§9). No dupliques aquí lo que ya vive en `docs/` o en la skill `hexagonal-arch`.

## 1. Qué es este servicio

Microservicio **core de Tesorería** de la plataforma UM (GitHub org `UM-services`, repo `UM.tesoreria.core-service`). Gestiona: chequeras (serie/cuota/pago/débito/tipo/clase/estado), personas, domicilios, documentos, legajos, facultades/dependencias/geográficas/ubicaciones, lectivos y cuotas lectivas, política arancelaria, comprobantes AFIP, cuentas y movimientos contables, contratos y cursos-cargo, compras (artículos, proveedores, facturas pendientes), integración **Guaraní** (preuniversitaria/beneficios/ubicaciones), **UM Hub** (campañas y reservas de vacante), usuarios + autenticación, y contexto de pagos **Mercado Pago**.

Contexto distribuido — con quién se habla:

| Interlocutor | Canal | Detalle |
|---|---|---|
| Servicios de facultad (multi-tenant) | REST vía `*FacultadConsumer` (`core/extern/consumer/`, 13 clases + variantes en `view/`) | URL base dinámica por `facultad.apiserver`+`facultad.apiport` (default 80) usando `extern/resolver/FacultadUrlResolver` |
| `tesoreria-mercadopago-service` | Feign (`core/client/tesoreria/mercadopago/`) | `PreferenceClient`/`PreferenceVacanteClient` crean preferencias de pago |
| Mercado Pago → core | Kafka | Consume topic `payment-processed` (grupo `tesoreria-core-group`) → `PaymentEventListener` → `MercadoPagoContextService.processPaymentEvent`. Mapeo de tipo: `um.tesoreria.mercadopago.service.domain.event.PaymentProcessedEvent` → `um.tesoreria.core.event.PaymentProcessedEvent` |
| Core → envío de chequeras | Kafka | Produce topic `send-chequera` (`service/facade/MailChequeraService`) |
| `haberes-core-service` | Feign (`core/client/haberes/`) | `CargoTipoClient`, `CursoClient` |
| `tesoreria-sender-service` | Feign (`core/client/tesoreria/sender/ChequeraClient`) | notificación de chequeras |
| `report-service` | HTTP (consumidor) | Le sirve el JSON del slice `chequera/estadoChequera` (campo a campo: los nombres del contrato NO se cambian sin romper al lector) |

- **Sin Spring Security** (no está en `pom.xml`): la autenticación vive en el slice `auth` + LDAP legacy (`core/service/UsuarioLdapService`). El servicio confía en la red/gateway; no agregues seguridad por tu cuenta.
- Registro en **Consul** (descubrimiento; nombre de aplicación `tesoreria-core-service`, tags `tesoreria,core`).
- Base de datos: MySQL `tesium` con esquema gestionado **fuera** de la app (`ddl-auto: none`); casi todas las tablas ya existen — un cambio de columna exige DDL manual externo.

## 2. Stack (verificado en `pom.xml`, la fuente de verdad)

- Java **25**, Spring Boot **4.1.1**, Spring Cloud **2025.1.3** (Consul discovery + OpenFeign + hc5), Kotlin **2.4.10** (solo legacy, §4)
- Version artefacto = versión del servicio: **6.0.0** (SemVer; se bump-ea en release, §7)
- Datos: `spring-boot-starter-data-jpa` + `mysql-connector-j` (runtime) + `h2` (test) + `spring-boot-starter-jdbc`
- Web: `starter-web` (MVC servlet) + `starter-webflux` (WebClient de los consumers) + `starter-validation` + `springdoc-openapi-starter-webmvc-ui` **3.1.0** (Swagger UI)
- Docs/exports: Apache POI + `openpdf` (reportes), `modelmapper` (solo legacy), Guava, Caffeine (`starter-cache`)
- `spring-kafka`, `starter-mail`, `starter-actuator` + `micrometer-registry-prometheus`, `json-path`, `jackson-datatype-jsr310`
- Calidad: JaCoCo (reporte en `target/site/jacoco/jacoco.xml`) → **SonarCloud** (org `um-services`, projectKey `UM-services_UM.tesoreria.core-service`)
- El README §"Versiones de Dependencias" puede estar desactualizado vs `pom.xml` (p. ej. connector/springdoc): ante duda, `pom.xml`.

## 3. Comandos

No hay wrapper Maven: usa el `mvn` del sistema (JDK 25 vía sdkman; en CI: `setup-java` temurin 25 + Maven cache).

```bash
mvn -q -DskipTests compile                 # chequeo rápido de compilación (lo que la mayoría de agentes necesita)
mvn test                                   # suites unitarias + slice tests (H2 en memoria, sin red/BD/Kafka)
mvn -Dtest=GetEstadoChequeraUseCaseImplTest test     # un test puntual
mvn -B verify                              # todo + JaCoCo (es lo que corre CI)
mvn -Pit verify                            # + 3 IT contra MySQL REAL (ver .env abajo; requiere red a la BD)
mvn spring-boot:run                        # local: levanta en :8092 (APP_PORT); necesita MySQL alcanzable; Consul/Kafka pueden fallar → ver overrides §8
```

- Pruebas de integración (perfil `it`, en memoria: `mvn -Pit verify`): crear `.env` en la raíz (ignorada por Git) con `IT_DB_HOST/IT_DB_PORT/IT_DB_NAME/IT_DB_USER/IT_DB_PASSWORD` (cuenta de solo lectura; `application-it.yml` deshabilita Consul, Kafka y mail, usa `ddl-auto: none` y pool read-only). Maven no carga `.env` solo: `set -a; . ./.env; set +a`. Si no hay datos esperados (p. ej. asignaciones de facultad con chequeras), los IT fallan con mensaje explícito.
- Swagger UI: `/swagger-ui/index.html` · spec: `/v3/api-docs` · actuator/prometheus expuestos (`management.endpoints.web.exposure.include: "*"`).
- Docker (multi-stage `maven:3-eclipse-temurin-25-alpine` → `temurin:25-jre-alpine`, usuario no-root): `docker build -t tesoreria-core .` — el JAR se nombra `um.tesoreria.core-service.jar` (`finalName`).

## 4. Mapa del código (`src/main/java/um/tesoreria/core/`)

Tres capas coexisten. **Regla de trabajo: el desarrollo nuevo va al layer hexagonal; el legacy se mantiene y se migra por módulos, no se le agregan features.**

```
TesoreriaCoreApplication.java        # único @SpringBootApplication, sin profiles extra
hexagonal/        (51 slices, ~1186 archivos) → §5
controller/       67 controladores legacy (+ facade/, dto/, view/) — endpoints aún activos
service/          106 servicios legacy (+ facade/, dto/, transactional/, view/)
repository/       85 repos legacy · model/ 74 entidades JPA legacy (base Auditable)
exception/        64 excepciones legacy — NO hay @ControllerAdvice global: cada controlador
                  maneja errores inline (try/catch → ResponseStatusException 404/400)
extern/           consumers REST hacia servicios de facultad + FacultadUrlResolver
client/           Feign: haberes/core, tesoreria/mercadopago, tesoreria/sender
listener/ event/  PaymentEventListener + PaymentProcessedEvent (consumo) · SendChequeraEvent (producción)
configuration/    KafkaConsumerConfig, KafkaProducerConfig, RestClientConfig, TesoreriaConfiguration
kotlin/           60 archivos .kt legacy (model + repository) en proceso de migración a slices
                  (p. ej. arancelTipo/arancelPorcentaje/asiento migrated, docs/README.md)
util/             Tool, Jsonifyable, etc.
resources/        bootstrap.yml (config principal), config/tesoreria.properties (path.files=/tmp/), banner.txt
```

## 5. Layers hexagonales: reglas y estado real

**Guía formal: skill global `hexagonal-arch`** (`/home/daniel/.agents/skills/hexagonal-arch`). Estructura por slice: `domain/{model,ports/in,ports/out}` → `application/{service,usecases,exception}` → `infrastructure/{persistence/{entity,mapper,repository,adapter},web/{controller,dto,mapper}}`. Nombres: `Find{X}ByIdUseCase`, `Jpa{X}RepositoryAdapter`, `{X}Request/Response`, `{X}DtoMapper`. Reglas duras: `domain/` 100% puro (solo `java.*`+Lombok); `application/` importa solo `domain/`; controladores hablan DTOs, nunca `{X}Entity`; `@Builder.Default` → omitir del builder cuando el origen es null en los mappers; necesidad cross-slice → **detenerse y preguntar al usuario** (no improvisar).

Estado real a tener en cuenta:

- **Slices estrictos (modelo a seguir):** `chequera/estadoChequera`, `usuarios/usuario`, `auth` — `domain/` sin ningún import fuera de `java.*`/Lombok (o `java.*` en modelos record).
- **Desviaciones toleradas (no copiarlas sin motivo):** `personas/persona` — `domain/model/Persona` implementa el `core.util.Jsonifyable` legacy y sus puertos `in` devuelven tipos de `infrastructure.web.dto` (`DeudaPersonaDto`, `InscripcionFullDto`); `extern/facultad/tesoreriaEstado` — `TesoreriaEstadoFacultad implements Jsonifyable`. Si tocás esos slices, reducí esa dependencia en vez de aumentarla.
- **Deuda de migración (151 archivos de `hexagonal/` importan packages legacy o de otros slices):** casos notorios `chequera/chequeraCuota`, `chequera/chequeraSerie`, `chequera/arancelTipo` (domain importa modelos de otros slices y `core.util.Jsonifyable`). Son targets de refactor al tocarlos, **no** plantilla para código nuevo: nunca agregues imports legacy a un slice al crear/editar.
- **Composición existente (patrón "composición de servicios de aplicación"):** `estadoChequera` orquesta `ChequeraSerieService`, `ChequeraTotalService`, `TipoChequeraService`, `ArancelTipoService`, `FacultadService` (servicios de aplicación de otros slices, no sus puertos); `personas/persona` expone `GET /persona/deudaExamen/...` usando `TesoreriaEstadoFacultadService`. Si necesitas algo análogo nuevo, preguntá primero.
- **Tests spec en paquetes viejos:** `src/test/.../hexagonal/persona/` vs `personas/persona`, `hexagonal/lectivoTotalImputacion` vs `contable/...` — residuos de renames de paquetes; el `package` real del test importa lo que teste. No asumas que el directorio del test refleja el paquete main.

### Tabla de slices → rutas base REST (47 exponen endpoints; alias corto + largo conviven)

| Grupo | Slice (paquete bajo `hexagonal/`) | Base REST |
|---|---|---|
| raíz | `auth` | `/api/tesoreria/core/auth` (login, `change-password`, `me/{userId}`) |
| raíz | `comprobante` | `/comprobante` + alias largo |
| raíz | `lectivo` | `/lectivo` + alias |
| raíz | `matriculacionContext` | `/matriculacionContext` + alias (controller sin subpaquete `controller/`) |
| raíz | `mercadoPagoContext` | `/api/tesoreria/core/mercadoPagoContext` |
| raíz | `setup` | `/setup` + alias |
| raíz | `track` | `/track` + alias |
| raíz | `ubicacionArticulo` | `/ubicacionArticulo` + alias |
| `chequera/` | `arancelPorcentaje` | `/arancelporcentaje` (solo corto) |
| `chequera/` | `arancelTipo` | `/aranceltipo` (solo corto) |
| `chequera/` | `baja` | `/baja` + alias |
| `chequera/` | `chequeraCuota` | `/chequeraCuota` + alias |
| `chequera/` | `chequeraPago` | `/chequeraPago` + alias |
| `chequera/` | `chequeraSerie` | `/chequeraserie` + alias |
| `chequera/` | `chequeraTotal` | `/chequeratotal` (solo corto) |
| `chequera/` | `claseChequera` | `/clasechequera` + alias |
| `chequera/` | `estadoChequera` | `/api/tesoreria/core/chequera` → solo `/estado/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` (contrato de `report-service`, 6.0.0 quitó `{debitoTipoId}`) |
| `chequera/` | `lectivoCuota` | — sin REST; puerto interno (lo consume `politicaArancelaria`) |
| `chequera/` | `politicaArancelaria` | `/api/tesoreria/core/politicaArancelaria` (solo largo) |
| `chequera/` | `producto` | `/producto` + alias |
| `chequera/` | `tipoChequera` | `/tipoChequera` + alias |
| `compras/` | `articulo` | `/articulo` + alias |
| `compras/` | `facturaPendiente` | — sin REST; lo expone el legacy `FacturacionElectronicaController` (`/api/tesoreria/core/facturacionElectronica`) |
| `compras/` | `proveedor` | `/proveedor` + alias |
| `compras/` | `proveedorMovimiento` | `/proveedorMovimiento` (solo corto) |
| `contable/` | `asiento` | — sin REST (web/controller vacío) |
| `contable/` | `cuenta` | `/cuenta` + alias |
| `contable/` | `cuentaMovimiento` | `/cuentaMovimiento` + alias |
| `contable/` | `lectivoTotalImputacion` | `/lectivototalimputacion` + alias |
| `contratos/` | `contrato` | `/contrato` (solo corto) |
| `contratos/` | `cursoCargoContratado` | `/cursocargocontratado` + alias `/api/core/cursocargocontratado` (ojo: **sin** `/tesoreria`) |
| `dependencias/` | `dependencia`, `facultad`, `geografica`, `ubicacion` | cada uno `/{entidad}` + alias largo |
| `extern/facultad/` | `tesoreriaEstado` | — sin REST; adapter es consumer hacia el servicio de facultad |
| `guarani/` | `alumnoGuarani` | `/api/tesoreria/core/guarani/alumno` (solo largo) |
| `guarani/` | `guaraniBeneficio`, `guaraniPropuestaTipoChequera`, `guaraniUbicacion` | cada uno `/api/tesoreria/core/{slice}` (solo largo) |
| `personas/` | `documento`, `domicilio`, `persona` | cada uno `/{entidad}` + alias |
| `personas/` | `legajo` | `/api/tesoreria/core/legajo` (solo largo) |
| `umhub/` | `campanha`, `reservaVacante`, `consulta` | cada uno `/api/tesoreria/core/umhub/{slice}` (solo largo) |
| `usuarios/` | `usuario` | `/usuario` + alias |
| `usuarios/` | `usuarioChequeraClaseChequera`, `usuarioChequeraFacultad`, `usuarioChequeraGeografica` | cada uno `/api/tesoreria/core/{slice}` (solo largo) |

Reglas sobre las rutas:

- El **alias corto** (`/chequeraCuota`) existe por compatibilidad con frontends viejos; el **largo** (`/api/tesoreria/core/...`) es el estándar para slices nuevos. Al crear un slice nuevo usa solo el largo, salvo indicación contraria.
- `personas/persona` además expone deuda de examen: `GET /persona/deudaExamen/facultad/{facultadId}/persona/{personaId}/{documentoId}/fecha/{fechaExamen}` (con parche temporal `manual == 1` marcado en el código — ver CHANGELOG 5.0.1).
- La ruta pública es contrato: remover segmentos o campos → major SemVer + migración documentada del consumidor (`report-service`, frontends, otros servicios).

## 6. Capa legacy (qué hay, qué no hacer)

Los ~67 controllers legacy sirven todavía áreas sin slice equivalente: `chequera` (altas/bajas/reemplazos/impresión), `pago`, `debito`, `balance`, `contabilidad`, `cuentaMensual`, `compra`, `costo`, `carrera/plan/materia/curso/cargoMateria`, `notificacion`/`examen`, `payPerTic`, `postales`, `bancaria`/`bancoMovimiento`/`valorMovimiento`, `proveedor*` (artículos/pagos/valores/track), `contrato*` (periodo/excluido/persona), `reciboMessageCheck`/`chequeraMessageCheck` (verificación de mensajes, issues 101/105), `sincronize`, `tool` (`/tool/mailvalidate`), LDAP (`InfoLdapService`/`UsuarioLdapService`). Modelos legacy JPA en `model/` (heredan `Auditable`), mapeo con ModelMapper, DTOs sufijo `Dto`.

- Legacy **sí puede** importar servicios de aplicación hexagonales (patrón actual: `FacturacionElectronicaController`, `SheetService`). Un slice **no debe** importar legacy (§5).
- No migrés "por migrar": la migración de un módulo legacy/kotlin a slice se pide explícitamente y se documenta en CHANGELOG + diagrama.

## 7. Flujo de trabajo y release (Git + docs)

- **Ramas:** una rama por issue GitHub, `<número>-<título-kebab>` (p. ej. `413-chorerelease-preparar-version-600-...`); PR → `develop`; `main` para release/CI JVM+Sonar; `develop`→deploy-develop, `staging`→deploy-staging. Labels: `feature`, `breaking-change`, `refactoring`, `documentation` (+ milestone).
- **Commits:** Conventional Commits con scope de slice: `feat(chequera/estadoChequera): ...`, `fix(personas/deudaExamen): ...`, `chore(release): prepare version X.Y.Z`. Para commits usá la skill `git-commit-expert`; issues/PRs: `github-issue-creator`/`github-pr-creator`.
- **SemVer (decisión de release, no arbitraria):** `pom.xml` = versión del servicio. Breaking en API pública (URL, contrato, esquema expuesto) → **major** (criterio usado en 5.0.0/6.0.0); feature aditivo → minor; fixes → patch.
- **Release (skill `release-documentation`):** actualiza a la par `pom.xml`, `CHANGELOG.md` (Keep a Changelog: `## [X.Y.Z] - fecha` + secciones `### Added/Changed/Fixed/Removed` **con bloque cita `> Basado en git diff …`** verificando cada afirmación contra el código real), `README.md` (sección nueva `## Novedades X.Y.Z (verificado en código)` arriba de todas, y "Versión actual" en el header), `docs/README.md` (header de versión + línea del slice tocado) y los `docs/hexagonal-*.mmd` que cambiaron de estructura. Fecha = día del entorno.
- **Diagramas Mermaid (`docs/*.mmd`):** el workflow `generate-docs.yml` los valida con `@mermaid-js/mermaid-cli` (chrome-headless-shell) y publica a GitHub Pages (`https://um-services.github.io/UM.tesoreria.core-service/`). Reglas obligatorias (de `docs/README.md`): sin `namespace` vacíos; genéricos con `~T~`; estereotipos con espacios `<< interface >>` (la forma compacta rompe el visor); archivos limpios (no confíes en el sanitize de runtime de `docs/script.js`). Visor local: `docs/index.html`. Usá la skill `mermaid-diagram-generator`.
- **README tiene secciones viejas:** §"Estructura del Proyecto" muestra slices planos (hoy están agrupados por subdominio: `chequera/`, `personas/`, `compras/`…) y §"API Endpoints Principales" cita rutas sin alias actual (p. ej. `/legajo/facultad/{facultadId}` solo existe como `/api/tesoreria/core/legajo/facultad/{facultadId}`). Verificar siempre contra `@RequestMapping` reales; la verdad de rutas es la tabla §5.

## 8. Runtime y config (`src/main/resources/bootstrap.yml`)

- Puerto `${APP_PORT:8092}`; app `tesoreria-core-service` registrado en Consul `consul-service:8500` (tags `tesoreria,core`).
- Datasource: `jdbc:mysql://${app.server}/${app.database}` — defaults hardcodeados de desarrollo (`10.147.20.20:3306`, `tesium`, `root/root`): sobrepone con `APP_SERVER`, `APP_DATABASE`, `APP_USER`, `APP_PASSWORD`. Hikari: pool 100, leak-detection 60s.
- JPA: `ddl-auto: none`, `open-in-view: false`, MySQLDialect. Kafka: `kafka:9092` (overrides con `SPRING_KAFKA_*`), trusted packages `*`. Mail: SMTP Gmail vía `app.mail.username/password` (defaults placeholder `uid`/`pwd` en `bootstrap.yml`; el yml de tests trae `chequeras@um.edu.ar`). Logging: nivel `${app.logging}` (debug por default → en prod se setea vía env).
- Tests unitarios (`src/test/resources/application.yml`): H2 en memoria `tesium_test` + `ddl-auto: create-drop` + `allow-circular-references: true` (hay ciclos legacy: **no** agregar más), Kafka listener `auto-startup: false`. Perfil `it`: `application-it.yml` con `IT_DB_*`.
- **`MERCADO_PAGO_ACCESS_TOKEN` (la menciona el README §Variables de Entorno) no se lee en este código.** El token de Mercado Pago vive en `tesoreria-mercadopago-service`; core se integra con él solo por Feign (crear preferencias) + eventos Kafka. En legacy, Mercado Pago es el `tipoPagoId = 18` (`TIPO_PAGO_MERCADO_PAGO` en `PagoService`/`TipoPagoFechaService`).

## 9. Fuentes de verdad (orden)

1. **El código** (`src/main/java`) y `pom.xml` — siempre ante conflicto con docs.
2. Skill global **`hexagonal-arch`** — reglas de slices (estructura, naming, purity, cross-slice "ask").
3. `docs/hexagonal-<slice>.mmd` + índice `docs/README.md` — estructura y endpoints por módulo (mantenidos release a release; si un slice cambia y su diagrama no, corregirlo es parte del cambio).
4. `CHANGELOG.md` — qué cambió y **por qué** (cada release cita el diff y el criterio SemVer).
5. `README.md` — onboarding humano; "Novedades" recientes arriba; cuidado con secciones viejas (§7).
6. Skills: `java-expert`, `spring-boot-expert`, `github-actions(-expert)`, `mermaid-diagrams`, `report` (issues de opencode). No hay configuración de agente en el repo (ni `opencode.json` ni `.claude/commands`); `.claude/settings.local.json` solo tiene permisos Bash/Read locales.

## 10. Checklist al tocar código (anti-patterns reales de este repo)

- [ ] ¿Modifico un slice? Primero `hexagonal-arch` (puridad, builder-default, naming) y su diagrama en `docs/`.
- [ ] ¿Necesito un tipo de otro slice? **Preguntar al usuario** (opciones del skill: copia propia / solo-id / puerto out hacia el puerto público de B / shared kernel / merge). No silenciar el problema.
- [ ] ¿Cambio una URL pública o campo de DTO? Major + migración del consumidor documentada.
- [ ] ¿Toco legacy y hace falta feature nueva? Evaluar slice; no inflar legacy.
- [ ] ¿Violación pre-existente dentro del slice que toco? La limpio en el mismo cambio (es regla del skill).
- [ ] Test: unit test espejo de la estructura del slice; `@WebMvcTest` + `@Import(XDtoMapper.class)` + `@MockitoBean` + `MockMvcTester` para controllers; Mockito a mano para use cases (ver `GetEstadoChequeraUseCaseImplTest`). Sonar mide cobertura vía JaCoCo.
- [ ] Docs: `mvn -q -DskipTests compile` + `mvn -Dtest=... test` antes de dar por hecho; diagramas validados por CI en el PR.
- [ ] Release: checklist §7 completo (pom + CHANGELOG + README + docs/README + .mmd) con bloques `> Basado en git diff …`.
