# AGENTS.md — Context for AI agents

Operational entry point for working on `UM.tesoreria.core-service`. Dense and navigable: use this file to get oriented and then open the source of truth indicated (§9). Do not duplicate here what already lives in `docs/` or in the `hexagonal-arch` skill.

## 1. What this service is

**Treasury core** microservice of the UM platform (GitHub org `UM-services`, repo `UM.tesoreria.core-service`). It manages: chequeras (serie/cuota/pago/débito/tipo/clase/estado), personas, domicilios, documentos, legajos, facultades/dependencias/geográficas/ubicaciones, lectivos and lective fees, arancelaria policy, AFIP receipts, accounting accounts and movements, contracts and course-cargo, compras (articles, suppliers, pending invoices, purchase requests — pedido de compra), **Guaraní** integration (preuniversitaria/beneficios/ubicaciones), **UM Hub** (campaigns and vacancy reservations), users + authentication, and **Mercado Pago** payment context.

Distributed context — who it talks to:

| Counterpart | Channel | Detail |
|---|---|---|
| Faculty services (multi-tenant) | REST via `*FacultadConsumer` (`core/extern/consumer/`, 13 classes + variants under `view/`) | Dynamic base URL per `facultad.apiserver`+`facultad.apiport` (default 80) using `extern/resolver/FacultadUrlResolver` |
| `tesoreria-mercadopago-service` | Feign (`core/client/tesoreria/mercadopago/`) | `PreferenceClient`/`PreferenceVacanteClient` create payment preferences |
| Mercado Pago → core | Kafka | Consumes topic `payment-processed` (group `tesoreria-core-group`) → `PaymentEventListener` → `MercadoPagoContextService.processPaymentEvent`. Type mapping: `um.tesoreria.mercadopago.service.domain.event.PaymentProcessedEvent` → `um.tesoreria.core.event.PaymentProcessedEvent` |
| Core → chequera sending | Kafka | Produces topic `send-chequera` (`service/facade/MailChequeraService`) |
| `haberes-core-service` | Feign (`core/client/haberes/`) | `CargoTipoClient`, `CursoClient` |
| `tesoreria-sender-service` | Feign (`core/client/tesoreria/sender/ChequeraClient`) | chequera notification |
| `report-service` | HTTP (consumer) | Served the JSON of the `chequera/estadoChequera` slice (field by field: the contract names are NOT changed without breaking the reader) |

- **No Spring Security** (not in `pom.xml`): authentication lives in the `auth` slice + legacy LDAP (`core/service/UsuarioLdapService`). The service trusts the network/gateway; do not add security on your own. See §11 (permissions: catalog, assignment and gating).
- Registers in **Consul** (discovery; application name `tesoreria-core-service`, tags `tesoreria,core`).
- Database: MySQL `tesium` with schema managed **outside** the app (`ddl-auto: none`); almost all tables already exist — a column change requires external manual DDL.

## 2. Stack (verified in `pom.xml`, the source of truth)

- Java **25**, Spring Boot **4.1.1**, Spring Cloud **2025.1.3** (Consul discovery + OpenFeign + hc5), Kotlin **2.4.10** (legacy only, §4)
- Artifact version = service version: **7.0.0** (SemVer; bumped on release, §7)
- Data: `spring-boot-starter-data-jpa` + `mysql-connector-j` (runtime) + `h2` (test) + `spring-boot-starter-jdbc`
- Web: `starter-web` (MVC servlet) + `starter-webflux` (WebClient of the consumers) + `starter-validation` + `springdoc-openapi-starter-webmvc-ui` **3.1.0** (Swagger UI)
- Docs/exports: Apache POI + `openpdf` (reports), `modelmapper` (legacy only), Guava, Caffeine (`starter-cache`)
- `spring-kafka`, `starter-mail`, `starter-actuator` + `micrometer-registry-prometheus`, `json-path`, `jackson-datatype-jsr310`
- Quality: JaCoCo (report at `target/site/jacoco/jacoco.xml`) → **SonarCloud** (org `um-services`, projectKey `UM-services_UM.tesoreria.core-service`)
- The README §"Versiones de Dependencias" may be outdated vs `pom.xml` (e.g. connector/springdoc): when in doubt, `pom.xml`.

## 3. Commands

There is no Maven wrapper: use the system `mvn` (JDK 25 via sdkman; in CI: `setup-java` temurin 25 + Maven cache).

```bash
mvn -q -DskipTests compile                 # quick compile check (what most agents need)
mvn test                                   # unit suites + slice tests (in-memory H2, no network/DB/Kafka)
mvn -Dtest=GetEstadoChequeraUseCaseImplTest test     # a single test
mvn -B verify                              # everything + JaCoCo (what CI runs)
mvn -Pit verify                            # + 4 ITs against REAL MySQL (see .env below; needs network to the DB)
mvn spring-boot:run                        # local: starts on :8092 (APP_PORT); needs reachable MySQL; Consul/Kafka may fail → see overrides §8
```

- Integration tests (`it` profile, in-memory: `mvn -Pit verify`): create `.env` at the root (ignored by Git) with `IT_DB_HOST/IT_DB_PORT/IT_DB_NAME/IT_DB_USER/IT_DB_PASSWORD` (read-only account; `application-it.yml` disables Consul, Kafka and mail, uses `ddl-auto: none` and a read-only pool). Maven does not load `.env` by itself: `set -a; . ./.env; set +a`. If the expected data is missing (e.g. faculty assignments with chequeras), the ITs fail with an explicit message. Exception: `EscrituraHistorialDevDbIT` (#404) uses its own connection with writes **only on `TEMPORARY` tables** of the session and a `StatementInspector` that blocks any write to real tables; it also needs the `CREATE TEMPORARY TABLES` privilege (skipped without `IT_DB_HOST`).
- Swagger UI: `/swagger-ui/index.html` · spec: `/v3/api-docs` · actuator/prometheus exposed (`management.endpoints.web.exposure.include: "*"`).
- Docker (multi-stage `maven:3-eclipse-temurin-25-alpine` → `temurin:25-jre-alpine`, non-root user): `docker build -t tesoreria-core .` — the JAR is named `um.tesoreria.core-service.jar` (`finalName`).

## 4. Code map (`src/main/java/um/tesoreria/core/`)

Three layers coexist. **Working rule: new development goes to the hexagonal layer; legacy is maintained and migrated module by module, no features are added to it.**

```
TesoreriaCoreApplication.java        # single @SpringBootApplication, no extra profiles
hexagonal/        (51 slices, ~1186 files) → §5
controller/       67 legacy controllers (+ facade/, dto/, view/) — endpoints still active
service/          106 legacy services (+ facade/, dto/, transactional/, view/)
repository/       85 legacy repos · model/ 74 legacy JPA entities (base Auditable)
exception/        64 legacy exceptions — there is NO global @ControllerAdvice: each controller
                  handles errors inline (try/catch → ResponseStatusException 404/400)
extern/           REST consumers to faculty services + FacultadUrlResolver
client/           Feign: haberes/core, tesoreria/mercadopago, tesoreria/sender
listener/ event/  PaymentEventListener + PaymentProcessedEvent (consume) · SendChequeraEvent (produce)
configuration/    KafkaConsumerConfig, KafkaProducerConfig, RestClientConfig, TesoreriaConfiguration
kotlin/           60 legacy .kt files (model + repository) being migrated to slices
                  (e.g. arancelTipo/arancelPorcentaje/asiento migrated, docs/README.md)
util/             Tool, Jsonifyable, etc.
resources/        bootstrap.yml (main config), config/tesoreria.properties (path.files=/tmp/), banner.txt
```

## 5. Hexagonal layers: rules and real state

**Formal guide: global `hexagonal-arch` skill** (`/home/daniel/.agents/skills/hexagonal-arch`). Per-slice structure: `domain/{model,ports/in,ports/out}` → `application/{service,usecases,exception}` → `infrastructure/{persistence/{entity,mapper,repository,adapter},web/{controller,dto,mapper}}`. Names: `Find{X}ByIdUseCase`, `Jpa{X}RepositoryAdapter`, `{X}Request/Response`, `{X}DtoMapper`. Hard rules: `domain/` 100% pure (only `java.*`+Lombok); `application/` imports only `domain/`; controllers speak DTOs, never `{X}Entity`; `@Builder.Default` → omit from the builder when the source is null in the mappers; cross-slice need → **stop and ask the user** (do not improvise).

Real state to keep in mind:

- **Strict slices (role models):** `chequera/estadoChequera`, `usuarios/usuario`, `auth` — `domain/` with no import outside `java.*`/Lombok (or `java.*` in record models).
- **Tolerated deviations (do not copy without reason):** `personas/persona` — `domain/model/Persona` implements the legacy `core.util.Jsonifyable` and its `in` ports return types from `infrastructure.web.dto` (`DeudaPersonaDto`, `InscripcionFullDto`); `extern/facultad/tesoreriaEstado` — `TesoreriaEstadoFacultad implements Jsonifyable`. If you touch those slices, reduce that dependency instead of increasing it.
- **Migration debt (151 files under `hexagonal/` import legacy packages or other slices):** notable cases `chequera/chequeraCuota`, `chequera/chequeraSerie`, `chequera/arancelTipo` (domain imports models from other slices and `core.util.Jsonifyable`). They are refactor targets when touched, **not** a template for new code: never add legacy imports to a slice when creating/editing.
- **Existing composition ("application service composition" pattern):** `estadoChequera` orchestrates `ChequeraSerieService`, `ChequeraTotalService`, `TipoChequeraService`, `ArancelTipoService`, `FacultadService` (application services of other slices, not their ports); `personas/persona` exposes `GET /persona/deudaExamen/...` using `TesoreriaEstadoFacultadService`. If you need something analogous and new, ask first.
- **Spec tests in old packages:** `src/test/.../hexagonal/persona/` vs `personas/persona`, `hexagonal/lectivoTotalImputacion` vs `contable/...` — leftovers from package renames; the test's real `package` imports what it tests. Do not assume the test directory reflects the main package.

### Slice → REST base route table (47 expose endpoints; short + long alias coexist)

| Group | Slice (package under `hexagonal/`) | REST base |
|---|---|---|
| root | `auth` | `/api/tesoreria/core/auth` (login, `change-password`, `me/{userId}`) |
| root | `comprobante` | `/comprobante` + long alias |
| root | `lectivo` | `/lectivo` + alias |
| root | `matriculacionContext` | `/matriculacionContext` + alias (controller without a `controller/` subpackage) |
| root | `mercadoPagoContext` | `/api/tesoreria/core/mercadoPagoContext` |
| root | `setup` | `/setup` + alias |
| root | `track` | `/track` + alias |
| root | `ubicacionArticulo` | `/ubicacionArticulo` + alias |
| `chequera/` | `arancelPorcentaje` | `/arancelporcentaje` (short only) |
| `chequera/` | `arancelTipo` | `/aranceltipo` (short only) |
| `chequera/` | `baja` | `/baja` + alias |
| `chequera/` | `chequeraCuota` | `/chequeraCuota` + alias |
| `chequera/` | `chequeraPago` | `/chequeraPago` + alias |
| `chequera/` | `chequeraSerie` | `/chequeraserie` + alias |
| `chequera/` | `chequeraTotal` | `/chequeratotal` (short only) |
| `chequera/` | `claseChequera` | `/clasechequera` + alias |
| `chequera/` | `estadoChequera` | `/api/tesoreria/core/chequera` → only `/estado/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` (`report-service` contract, 6.0.0 removed `{debitoTipoId}`) |
| `chequera/` | `lectivoCuota` | — no REST; internal port (consumed by `politicaArancelaria`) |
| `chequera/` | `politicaArancelaria` | `/api/tesoreria/core/politicaArancelaria` (long only) |
| `chequera/` | `producto` | `/producto` + alias |
| `chequera/` | `tipoChequera` | `/tipoChequera` + alias |
| `compras/` | `articulo` | `/articulo` + alias |
| `compras/` | `facturaPendiente` | — no REST; exposed by the legacy `FacturacionElectronicaController` (`/api/tesoreria/core/facturacionElectronica`) |
| `compras/` | `proveedor` | `/proveedor` + alias |
| `compras/` | `proveedorMovimiento` | `/proveedorMovimiento` (short only) |
| `compras/pedidos/` | `compraPedido` | `/api/tesoreria/core/compraPedido` (long only): `GET /` (`dependenciaIds`), `POST /search`, `GET /{id}`/`/numero/{numero}`, `POST`/`PUT`, `POST /{id}/enviar?usuarioId=`, `POST /{id}/aprobar` (7.0.0: renamed from `autorizar`), `POST /{id}/rechazar` (body `autorizanteId`+`motivo`), `POST /{id}/descartar` |
| `compras/pedidos/` | `compraPedidoItem` | `/api/tesoreria/core/compraPedidoItem` (long only) |
| `compras/pedidos/` | `compraPedidoSecuencia` | — no REST; internal port (correlativo anual) |
| `compras/pedidos/` | `compraPedidoAutorizante` | `/api/tesoreria/core/compraPedidoAutorizante` (long only) |
| `compras/pedidos/` | `compraPedidoHistorial` | `/api/tesoreria/core/compraPedidoHistorial` (long only) |
| `contable/` | `asiento` | — no REST (empty web/controller) |
| `contable/` | `cuenta` | `/cuenta` + alias |
| `contable/` | `cuentaMovimiento` | `/cuentaMovimiento` + alias |
| `contable/` | `lectivoTotalImputacion` | `/lectivototalimputacion` + alias |
| `contratos/` | `contrato` | `/contrato` (short only) |
| `contratos/` | `cursoCargoContratado` | `/cursocargocontratado` + alias `/api/core/cursocargocontratado` (careful: **without** `/tesoreria`) |
| `dependencias/` | `dependencia`, `facultad`, `geografica`, `ubicacion` | each `/{entidad}` + long alias |
| `extern/facultad/` | `tesoreriaEstado` | — no REST; the adapter is a consumer to the faculty service |
| `guarani/` | `alumnoGuarani` | `/api/tesoreria/core/guarani/alumno` (long only) |
| `guarani/` | `guaraniBeneficio`, `guaraniPropuestaTipoChequera`, `guaraniUbicacion` | each `/api/tesoreria/core/{slice}` (long only) |
| `personas/` | `documento`, `domicilio`, `persona` | each `/{entidad}` + alias |
| `personas/` | `legajo` | `/api/tesoreria/core/legajo` (long only) |
| `umhub/` | `campanha`, `reservaVacante`, `consulta` | each `/api/tesoreria/core/umhub/{slice}` (long only) |
| `usuarios/` | `usuario` | `/usuario` + alias |
| `usuarios/` | `usuarioChequeraClaseChequera`, `usuarioChequeraFacultad`, `usuarioChequeraGeografica` | each `/api/tesoreria/core/{slice}` (long only) |

Rules about routes:

- The **short alias** (`/chequeraCuota`) exists for compatibility with old frontends; the **long** one (`/api/tesoreria/core/...`) is the standard for new slices. When creating a new slice use only the long one, unless told otherwise.
- `personas/persona` also exposes exam debt: `GET /persona/deudaExamen/facultad/{facultadId}/persona/{personaId}/{documentoId}/fecha/{fechaExamen}` (with a temporary `manual == 1` patch marked in the code — see CHANGELOG 5.0.1).
- The public route is a contract: removing segments or fields → major SemVer + documented consumer migration (`report-service`, frontends, other services).

## 6. Legacy layer (what there is, what not to do)

The ~67 legacy controllers still serve areas without an equivalent slice: `chequera` (creations/deletions/replacements/printing), `pago`, `debito`, `balance`, `contabilidad`, `cuentaMensual`, `compra`, `costo`, `carrera/plan/materia/curso/cargoMateria`, `notificacion`/`examen`, `payPerTic`, `postales`, `bancaria`/`bancoMovimiento`/`valorMovimiento`, `proveedor*` (articles/payments/values/track), `contrato*` (period/excluded/person), `reciboMessageCheck`/`chequeraMessageCheck` (message verification, issues 101/105), `sincronize`, `tool` (`/tool/mailvalidate`), LDAP (`InfoLdapService`/`UsuarioLdapService`). Legacy JPA models in `model/` (extend `Auditable`), mapping with ModelMapper, DTOs suffixed `Dto`.

- Legacy **may** import hexagonal application services (current pattern: `FacturacionElectronicaController`, `SheetService`). A slice **must not** import legacy (§5).
- Do not migrate "just to migrate": migrating a legacy/kotlin module to a slice is explicitly requested and documented in CHANGELOG + diagram.

## 7. Workflow and release (Git + docs)

- **Branches:** one branch per GitHub issue, `<number>-<kebab-title>` (e.g. `413-chorerelease-preparar-version-600-...`); PR → `develop`; `main` for release/CI JVM+Sonar; `develop`→deploy-develop, `staging`→deploy-staging. Labels: `feature`, `breaking-change`, `refactoring`, `documentation` (+ milestone).
- **Commits:** Conventional Commits with slice scope: `feat(chequera/estadoChequera): ...`, `fix(personas/deudaExamen): ...`, `chore(release): prepare version X.Y.Z`. For commits use the `git-commit-expert` skill; issues/PRs: `github-issue-creator`/`github-pr-creator`.
- **SemVer (release decision, not arbitrary):** `pom.xml` = service version. Breaking in the public API (URL, contract, exposed schema) → **major** (criterion used in 5.0.0/6.0.0); additive feature → minor; fixes → patch.
- **Release (`release-documentation` skill):** update together `pom.xml`, `CHANGELOG.md` (Keep a Changelog: `## [X.Y.Z] - date` + `### Added/Changed/Fixed/Removed` sections **with a `> Based on git diff …` quote block** verifying each claim against the real code), `README.md` (new `## Novedades X.Y.Z (verificado en código)` section on top of all, and "Versión actual" in the header), `docs/README.md` (version header + line for the touched slice) and the `docs/hexagonal-*.mmd` that changed structure. Date = the environment's current day.
- **Mermaid diagrams (`docs/*.mmd`):** the `generate-docs.yml` workflow validates them with `@mermaid-js/mermaid-cli` (chrome-headless-shell) and publishes to GitHub Pages (`https://um-services.github.io/UM.tesoreria.core-service/`). Mandatory rules (from `docs/README.md`): no empty `namespace`; generics with `~T~`; stereotypes with spaces `<< interface >>` (the compact form breaks the viewer); clean files (do not trust `docs/script.js` runtime sanitize). Local viewer: `docs/index.html`. Use the `mermaid-diagram-generator` skill.
- **README has old sections:** §"Estructura del Proyecto" shows flat slices (today they are grouped by subdomain: `chequera/`, `personas/`, `compras/`…) and §"API Endpoints Principales" cites routes without the current alias (e.g. `/legajo/facultad/{facultadId}` only exists as `/api/tesoreria/core/legajo/facultad/{facultadId}`). Always verify against real `@RequestMapping`; the truth of routes is the §5 table.

## 8. Runtime and config (`src/main/resources/bootstrap.yml`)

- Port `${APP_PORT:8092}`; app `tesoreria-core-service` registered in Consul `consul-service:8500` (tags `tesoreria,core`).
- Datasource: `jdbc:mysql://${app.server}/${app.database}` — hardcoded development defaults (`10.147.20.20:3306`, `tesium`, `root/root`): override with `APP_SERVER`, `APP_DATABASE`, `APP_USER`, `APP_PASSWORD`. Hikari: pool 100, leak-detection 60s.
- JPA: `ddl-auto: none`, `open-in-view: false`, MySQLDialect. Kafka: `kafka:9092` (overrides with `SPRING_KAFKA_*`), trusted packages `*`. Mail: SMTP Gmail via `app.mail.username/password` (placeholder defaults `uid`/`pwd` in `bootstrap.yml`; the test yml ships `chequeras@um.edu.ar`). Logging: level `${app.logging}` (debug by default → in prod it is set via env).
- Unit tests (`src/test/resources/application.yml`): in-memory H2 `tesium_test` + `ddl-auto: create-drop` + `allow-circular-references: true` (there are legacy cycles: **do not** add more), Kafka listener `auto-startup: false`. `it` profile: `application-it.yml` with `IT_DB_*`.
- **`MERCADO_PAGO_ACCESS_TOKEN` (mentioned by the README §Variables de Entorno) is not read in this code.** The Mercado Pago token lives in `tesoreria-mercadopago-service`; core integrates with it only via Feign (create preferences) + Kafka events. In legacy, Mercado Pago is `tipoPagoId = 18` (`TIPO_PAGO_MERCADO_PAGO` in `PagoService`/`TipoPagoFechaService`).

## 9. Sources of truth (order)

1. **The code** (`src/main/java`) and `pom.xml` — always when in conflict with docs.
2. Global **`hexagonal-arch`** skill — slice rules (structure, naming, purity, cross-slice "ask").
3. `docs/hexagonal-<slice>.mmd` + index `docs/README.md` — structure and endpoints per module (kept release by release; if a slice changes and its diagram does not, fixing it is part of the change).
4. `CHANGELOG.md` — what changed and **why** (each release cites the diff and the SemVer criterion).
5. `README.md` — human onboarding; recent "Novedades" on top; beware of old sections (§7).
6. Skills: `java-expert`, `spring-boot-expert`, `github-actions(-expert)`, `mermaid-diagrams`, `report` (opencode issues). There is no agent configuration in the repo (no `opencode.json` and no `.claude/commands`); `.claude/settings.local.json` only has local Bash/Read permissions.

## 10. Checklist when touching code (real anti-patterns of this repo)

- [ ] Modifying a slice? First `hexagonal-arch` (purity, builder-default, naming) and its diagram in `docs/`.
- [ ] Need a type from another slice? **Ask the user** (skill options: own copy / id-only / outbound port to B's public port / shared kernel / merge). Do not silence the problem.
- [ ] Changing a public URL or DTO field? Major + documented consumer migration.
- [ ] Adding a feature that requires a permission? Key `modulo.accion` in the catalog + wiring in the **new** code; **never** security on existing/legacy endpoints (§11).
- [ ] Touching legacy and a new feature is needed? Evaluate a slice; do not inflate legacy.
- [ ] Pre-existing violation inside the slice you touch? Clean it in the same change (skill rule).
- [ ] Test: unit test mirroring the slice structure; `@WebMvcTest` + `@Import(XDtoMapper.class)` + `@MockitoBean` + `MockMvcTester` for controllers; manual Mockito for use cases (see `GetEstadoChequeraUseCaseImplTest`). Sonar measures coverage via JaCoCo.
- [ ] Docs: `mvn -q -DskipTests compile` + `mvn -Dtest=... test` before considering it done; diagrams validated by CI in the PR.
- [ ] Release: full §7 checklist (pom + CHANGELOG + README + docs/README + .mmd) with `> Based on git diff …` blocks.

## 11. Permissions: catalog, assignment and feature gating

The permission system lives in the `hexagonal/usuarios/` subdomain (slices `permiso`, `rol`, `rolPermiso`, `usuarioRol`, `usuarioPermiso`, `permisoEfectivo`). Two-halves model: the **key in the catalog** `permiso` (data, administrable) and the **reference in the code** (behavior). They are linked **only by the key text**, with the `modulo.accion` convention (e.g. `pagos.reembolsos`; the `modulo` groups the UI).

Current state (verified in code):
- **Assignment: complete.** Catalog with CRUD (`POST/PUT/DELETE /api/tesoreria/core/permiso`), roles, role×permission matrix (`rolPermiso`), overrides (`usuarioPermiso`), and effective bundle (`GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}`).
- **Backend gating: implemented and opt-in.** `@RequierePermiso("<clave>")` annotation + `RequierePermisoInterceptor` under `configuration/security`, registered by `PermissionWebConfig`. **Disabled by default** (`app.permissions.enforce`, env `APP_PERMISSIONS_ENFORCE`): it only acts on annotated endpoints, so legacy stays intact. Until real identity exists (JWT, M3), identity is resolved transitionally via the `X-User-Id` header (spoofable): it is PEP plumbing, not definitive security.

### Golden rule — LEGACY (do not break)
**Never add or change security (neither `@RequierePermiso`, nor filters, nor token validation) on existing endpoints** consumed by the legacy system (VB6, old frontends, other services) **nor on the legacy layer**. The legacy system sends no token or permissions: any enforcement there **breaks it**. Gating applies **only to new features**. When in doubt, **stop and ask** (§1 rule: "do not add security on your own").

### How to add a new feature that requires a permission
1. **Choose the key** `modulo.accion` and **register it in the catalog** `permiso` (from the administrador module, or a versioned seed/SQL in the same PR).
2. **Wire the key into the new code**:
   - **Backend:** `@RequierePermiso("<clave>")` on the **new** endpoint (the interceptor only acts on annotated endpoints and with enforcement turned on).
   - **Frontend:** `permisoGuard('<clave>')` on the route, `permiso` on the menu item and `*uiPermiso` on actions.
3. The feature is born **deny-by-default**: it is enabled from the administrador (checking the key on a **role** and assigning it, or with a direct **override** for the user).
4. **Verify** with the Simulator that the user has it and where it comes from.

### Anti-pattern: "phantom permission"
Do not register keys that no code reads. Declaring the key **in the same change** that wires the feature avoids switches with no effect. The key is a **contract**: changing it forces updating code + catalog + migrating the assignments.
