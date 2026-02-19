# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.3.1] - 2026-02-19
### Fixed
- fix(performance): Optimización de rendimiento en actualización de último login de usuario
  - Nuevo método `updateLastLog()` en `UsuarioRepository` con `@Modifying` y `@Query` para actualización directa
  - Refactorización de `UsuarioService.updateLastLog()` para usar query directa en lugar de cargar/guardar entidad completa
  - Reducción de overhead de persistencia al actualizar solo el campo `lastLog`
- fix(transaction): Corrección de manejo transaccional en procesamiento de eventos de pago
  - Eliminación de `@Transactional` en `MercadoPagoContextService.processPaymentEvent()`
  - El procesamiento de eventos hereda la transacción del contexto de ejecución de Kafka

> Basado en análisis profundo de `git diff HEAD` (cambios no commiteados).

## [3.3.0] - 2026-02-18
### Added
- feat(performance): Implementación de batch processing y optimizaciones de rendimiento en PersonaService
  - Nuevo método `findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds()` en ChequeraCuotaRepository para consultas batch
  - Nuevo método `findAllByChequeraIdIn()` en ChequeraCuotaRepository para carga masiva por IDs de chequera
  - Nuevo método `findAllByChequeraCuotaIds()` en MercadoPagoContextService para recuperación batch de contextos
  - Implementación de "Self-Healing" para corregir chequeraId nulos en cuotas
  - Procesamiento batch de cuotas con agrupación por facultad y tipo de chequera
  - Reducción de N+1 queries mediante carga masiva de contextos de MercadoPago
  - Optimización en creación de preferencias con reutilización de contextos existentes
- feat(api): Nuevo método sobrecargado `makeContext(ChequeraCuota, MercadoPagoContext)` en MercadoPagoCoreService
  - Permite reutilizar contextos existentes evitando duplicados
  - Validación inteligente para evitar actualizaciones innecesarias

### Changed
- refactor(kafka): Migración de configuración de deserialización Jackson en KafkaConsumerConfig
  - Cambio de `JsonDeserializer` a `JacksonJsonDeserializer` (nueva API de Spring Kafka)
  - Actualización de `DefaultJackson2JavaTypeMapper` a `DefaultJacksonJavaTypeMapper`
  - Migración de paquetes `com.fasterxml.jackson` a `tools.jackson`
  - Simplificación de configuración del ObjectMapper usando `JsonMapper.builder()`
- refactor(service): Mejoras en arquitectura hexagonal de PersonaService
  - Inyección de contextos pre-cargados en lugar de consultas individuales
  - Mejor separación de concerns entre lógica de negocio y acceso a datos
  - Optimización de ordenamiento y filtrado de cuotas en memoria

### Fixed
- fix(data-integrity): Corrección automática de cuotas con chequeraId nulo
  - Detección y reparación batch de registros inconsistentes
  - Relación automática entre cuotas y series mediante business keys

> Basado en análisis profundo de `git diff HEAD` (cambios no commiteados), estructura de repositorios y servicios.

## [3.2.0] - 2026-02-17
### Added
- feat(hexagonal): Implementación completa del módulo ChequeraCuota con Arquitectura Hexagonal
  - Creación de modelo de dominio: `ChequeraCuota`, `ChequeraPago`, `ChequeraTotal`, `DeudaData`, `ChequeraSerie`
  - Definición de puertos (interfaces): `CalculateDeudaUseCase` (entrada), `ChequeraCuotaRepository` (salida)
  - Implementación de servicio de aplicación: `ChequeraCuotaService` con métodos `calculateDeuda()` y `calculateDeudaExtended()`
  - Adaptador de persistencia: `JpaChequeraCuotaRepositoryAdapter` con mapeo entre entidades JPA y modelos de dominio
  - Mappers: `ChequeraCuotaMapper` para conversión entre capas, `ChequeraSerieMapper` para integración
- feat(api): Integración del nuevo caso de uso en controladores y servicios existentes
  - `ChequeraCuotaController`: Uso de `CalculateDeudaUseCase` en endpoint `/deuda/{facultadId}/{tipoChequeraId}/{chequeraSerieId}`
  - `PersonaService`: Migración de cálculo de deudas al nuevo servicio hexagonal
  - `ChequeraSerieService`: Integración para seteo automático de deuda en chequeras
  - `SheetService`: Actualización para usar el nuevo caso de uso

### Changed
- refactor(hexagonal): Migración de lógica de negocio desde `ChequeraCuotaService` tradicional a arquitectura hexagonal
  - Eliminación de métodos `calculateDeuda()` y `calculateDeudaExtended()` del servicio tradicional (~210 líneas)
  - Separación de concerns: lógica de negocio aislada en capa de dominio
  - Mejora en testeabilidad mediante inyección de dependencias por interfaces

### Fixed
- fix(circular-deps): Eliminación de dependencia circular en `LectivoService`
  - Reemplazo de `ChequeraSerieService` por `ChequeraSerieRepository` directamente
  - Resolución de acoplamiento entre servicios de dominio

### Removed
- chore(cleanup): Limpieza de código obsoleto
  - Eliminación de comentario de código muerto en `ChequeraCuotaController`
  - Remoción de imports no utilizados en `ChequeraCuotaService`

> Basado en análisis profundo de `git diff HEAD`, estructura de archivos y dependencias en `pom.xml`.

## [3.1.2] - 2026-02-17
- fix: Refinamiento de corrección de timezone en pagos de MercadoPago
  - Simplificación de la lógica de ajuste de timezone en `ChequeraPagoService`
  - Ajuste directo de -3 horas al guardar fechas de pago y acreditación en `PagoService`
  - Eliminación de lógica condicional compleja basada en fechas específicas
- refactor: Uso consistente de `minusHours(3)` para normalización de fechas UTC a Argentina

> Based on deep analysis of git diff HEAD (uncommitted changes) en `ChequeraPagoService.java` y `PagoService.java`.

## [3.1.1] - 2026-02-16
- fix: Corrección de timezone en consultas de pagos de MercadoPago (`ChequeraPagoService.findAllByTipoPagoIdAndFechaPago`)
  - Ajuste de +3 horas para fechas posteriores a febrero 2026
  - Ajuste especial para el 31 de enero de 2026 (+2h 59m)
  - Nueva constante `TIPO_MERCADO_PAGO = 18` para identificar pagos de MercadoPago
- refactor: Uso de `@RequiredArgsConstructor` en `ChequeraPagoService` en lugar de constructor manual
- chore: Eliminación de campo `jsonMapper` no utilizado en `ChequeraPagoService`

> Based on deep analysis of git diff HEAD (uncommitted changes).

## [3.1.0] - 2026-02-05
- refactor: Migración del módulo Persona a arquitectura hexagonal
  - Renombramiento de `Persona.kt` a `PersonaEntity.java`
  - Reubicación de servicios y repositorios a paquete `um.tesoreria.core.hexagonal.persona`
  - Actualización de dependencias en todos los servicios que usan Persona
- feat: Implementación de Kafka Consumer Config para manejo de eventos de pago
- refactor: Mejoras en KafkaProducerConfig usando string serializer class name
- feat: Actualización de PaymentEventListener con containerFactory específico
- refactor: Modificación de PreferenceClient para usar MercadoPagoContextDto importado
- chore: Actualización de logging en bootstrap.yml para incluir debug de Kafka

> Based on deep analysis of git diff HEAD (staged changes).

## [3.0.0] - 2026-02-03
- chore(deps): Actualización de Spring Boot de 3.5.8 a 4.0.2
- chore(deps): Actualización de Java de 24 a 25
- chore(deps): Actualización de Kotlin de 2.2.21 a 2.3.0
- chore(deps): Actualización de Spring Cloud de 2025.0.0 a 2025.1.0
- chore(deps): Actualización de mysql-connector-j de 9.4.0 a 9.6.0
- chore(deps): Actualización de springdoc-openapi de 2.8.10 a 3.0.1
- chore(deps): Actualización de Apache POI de 5.4.1 a 5.5.1
- chore(deps): Actualización de guava de 33.4.8-jre a 33.5.0-jre
- chore(deps): Actualización de commons-lang3 de 3.18.0 a 3.20.0
- chore(deps): Actualización de modelmapper de 3.2.4 a 3.2.6
- chore(deps): Actualización de json-path de 2.9.0 a 2.10.0
- chore: Actualización de GitHub Actions para usar JDK 25
- chore: Actualización de Dockerfile para usar JDK 25

> Based on deep analysis of git diff (staged changes) and pom.xml.

## [2.5.0] - 2025-12-14
- feat: Implementación de envío asíncrono de chequeras mediante Kafka y `SendChequeraEvent`.
- feat: Configuración de productor de Kafka en `KafkaProducerConfig`.
- refactor: Modificación de `MailChequeraService` para publicar eventos en lugar de procesamiento síncrono.
- chore(deps): Actualización de Spring Boot de 3.5.6 a 3.5.8.

> Based on deep analysis of git diff HEAD (Kafka integration) and pom.xml (Spring Boot update).

## [2.4.0] - 2025-11-16
- feat: Added @Builder.Default to various model fields for improved object instantiation (MercadoPagoContext, Proveedor, TipoChequeraMercadoPagoCreditCard, MercadoPagoContextDto)
- refactor: Extracted common logic into private helper methods (setDeuda, setUltimoEnvio) in ChequeraSerieService to reduce code duplication and improve readability
- refactor: Updated update methods in ChequeraSerieService, DomicilioService, and TipoChequeraService to modify existing entities directly instead of creating new instances
- refactor: Simplified add, setPayPerTic, setEnviado methods in ChequeraSerieService by directly returning repository.save() results and removing redundant logging
- refactor: Replaced manual constructor with @RequiredArgsConstructor in PersonaService
- refactor: Improved DomicilioService.capture method by extracting province and locality synchronization into a helper method and handling default IDs
- refactor: Removed redundant logging in MercadoPagoContextService, PersonaService, TipoChequeraService, and MercadoPagoCoreService
- fix: Excluded compensated quotas from pending MercadoPago preference creation in PersonaService
- fix: Improved error logging in ChequeraSerieService.setUltimoEnvio from debug to error

> Based on deep analysis of git diff HEAD.

## [2.3.2] - 2025-11-15
- refactor: Replaced manual constructors with @RequiredArgsConstructor in ChequeraController and MercadoPagoCoreController

> Based on deep analysis of git diff HEAD.

## [2.3.1] - 2025-11-11
- fix: Added compensada filter to ChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBajaAndCompensadaAndImporte1GreaterThan to exclude compensated cuotas from pending queries
- Updated ChequeraCuotaService.findAllPendientes to include compensada parameter

> Based on deep analysis of git diff HEAD.

## [2.3.0] - 2025-11-11
- feat: Added MercadoPagoContextHistory module for tracking history of MercadoPago contexts
- refactor: Replaced @Autowired with @RequiredArgsConstructor in PagarFileController and PagarFileService
- refactor: Updated Collectors.toList() to .toList() in JpaCursoCargoContratadoRepositoryAdapter
- refactor: Integrated MercadoPagoContextHistory into MercadoPagoContextService for automatic history creation on add, saveAll, and update operations

> Based on deep analysis of code changes in git diff HEAD and pom.xml.

## [2.2.2] - 2025-10-28
- fix: Improved acreditacion date logic in PagoService for dates after September 1, 2025
- refactor: Updated test persona ID in ChequeraSerieService.findAllByLectivoIdAndFacultadIdTest
- chore(deps): Updated Kotlin version to 2.2.21
- docs: Fixed Mermaid comment syntax in entities.mmd diagram
- docs: Corrected flow arrows in hexagonal-architecture.mmd diagram

> Based on deep analysis of code changes in git diff HEAD and pom.xml.

## [2.2.1] - 2025-10-05
- refactor: Added debug logging in CursoCargoContratadoService.findAllByCurso method
- refactor: Improved formatting in CursoCargoContratadoEntity annotations
- refactor: Removed unused imports in CursoCargoContratadoResponse
- docs: Enhanced script.js for better Mermaid diagram handling (frontmatter stripping and validation fixes)

> Based on deep analysis of code changes in git diff HEAD.

## [2.2.0] - 2025-10-01
- feat: Agregar relación contratadoPersona en modelo CursoCargoContratado
- feat: Agregar método jsonify() en modelo Debito para logging estructurado
- refactor: Refactorización de DebitoController para usar @RequiredArgsConstructor y simplificar respuestas HTTP
- refactor: Mejoras en DebitoService con logging JSON y inyección por constructor
- refactor: Actualizaciones en ContratoToolService con mejoras en manejo de datos
- docs: Actualización de diagramas Mermaid (dependencies.mmd y sequence-chequera-serie-sede.mmd)

> Based on deep analysis of code changes in git diff HEAD and pom.xml.

## [2.1.0] - 2025-09-29
- refactor: Improved dependency injection with @RequiredArgsConstructor in CostoController and CostoService
- feat: Added jsonify() methods to Asiento, CuentaMovimiento, Entrega, and AsignacionCostoDto models for structured JSON logging
- refactor: Enhanced logging throughout services with JSON serialization using Jsonifier utility
- refactor: Removed unnecessary public modifiers in AsientoRepository methods
- fix: Improved error handling in CostoService by deleting previous asiento before adding new one
- chore: Minor code cleanups and logging improvements

> Based on deep analysis of code changes in git diff HEAD and pom.xml.

## [2.0.0] - 2025-09-28
- refactor: remove ContratadoController and references to contratado model across services and controllers
- chore(deps): update Spring Boot to 3.5.6, Kotlin to 2.2.20, openpdf to 3.0.0
- refactor: replace @Autowired with @RequiredArgsConstructor in multiple services and controllers
- feat: add Jsonifier utility class for structured JSON logging
- refactor: update imports for openpdf library change from com.lowagie to org.openpdf
- refactor: remove ContratoAutoFixService and related dependencies
- fix: improve logging with jsonify() methods in various services

> Based on deep analysis of code changes in git diff HEAD, commit history, and pom.xml.

## [1.6.0] - 2025-09-11
- feat(controller,repository,service): agregar endpoint de resumen por lectivo (`/resumen/lectivo/{lectivoId}`) que devuelve la cantidad de chequeras agrupadas por facultad y sede
- refactor(controller,service): reemplazar constructores con inyección de dependencias por `@RequiredArgsConstructor` en `ChequeraSerieController` y `ChequeraSerieService`
- feat(model): crear nuevo DTO `FacultadSedeChequeraDto` para representar los datos del resumen

> Basado en análisis de código (`git diff HEAD`), historial de commits y `pom.xml`.

## [1.5.0] - 2025-09-07
- feat: Añadido nuevo endpoint para buscar `ChequeraSerie` por sede (`/sede/facultad/{facultadId}/lectivo/{lectivoId}/geografica/{geograficaId}`).
- refactor: Refactorizados `GeograficaController` y `LegajoController` para usar inyección de dependencias por constructor, `ResponseEntity.ok()` y manejo de excepciones mejorado.
- refactor: Añadido nuevo alias de endpoint en `GeograficaController` (`/api/tesoreria/core/geografica`).

## [1.4.0] - 2025-09-05
- feat(controller): nuevos endpoints y refactor en ChequeraSerieController (más métodos REST, simplificación de respuestas).
- feat(service): nuevos servicios de búsqueda y consulta (`ProveedorSearchService`, `TipoChequeraSearchService`, etc.).
- feat(util): nueva clase utilitaria `Tool` para manejo de fechas, archivos y conversiones.
- refactor(model/repository): eliminación de campo `productoId` en `CuotaPeriodoDto` y ajuste de queries asociadas.
- refactor: migración y renombrado de servicios y repositorios a nuevos paquetes (`um.tesoreria.core`).
- chore(deps): actualización de dependencias principales:
  - Spring Boot: 3.5.4 a 3.5.5
  - Kotlin: 2.2.0 a 2.2.10
  - SpringDoc OpenAPI: 2.8.9 a 2.8.10
  - OpenPDF: 2.2.4 a 2.4.0
- test: nuevos tests unitarios para controladores y servicios.
- docs: actualización de diagramas Mermaid y documentación automática (`docs/`), eliminación de archivos obsoletos (`application.yml`, `logback-spring.xml`).
> Basado en análisis de código (`git diff HEAD`), historial de commits y `pom.xml`.
# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.3.0] - 2025-08-19
- refactor(controller): simplificación de respuestas HTTP en ChequeraCuotaController (uso de ResponseEntity.ok).
- feat(model/repository): `productoId` agregado a `CuotaPeriodoDto` y ajustada la query en ChequeraCuotaRepository.
- docs: nueva generación de documentación automática y pipeline local (`generate-docs-local.sh`), nuevos archivos en `docs/`.
- docs: actualización de `index.html`, `script.js` y `style.css` para documentación dinámica y visual.
> Basado en análisis de código (`git diff HEAD`), historial de commits y `pom.xml`.

## [1.2.0] - 2025-08-18
- feat(controller): nuevos endpoints REST en ChequeraPagoController para consulta por facultad/tipoChequera/lectivo.
- feat(repository/service): métodos para búsqueda avanzada de pagos por facultad, tipo de chequera y lectivo.
- refactor(controller): simplificación de respuestas HTTP en FacultadController y TipoChequeraController (uso de ResponseEntity.ok).
- docs(ci): mejoras en el workflow de documentación automática y despliegue en GitHub Pages.
- docs: diagramas Mermaid y documentación sincronizados con la funcionalidad actual.
> Basado en análisis de código (`git diff HEAD`), historial de commits y `pom.xml`.

## [1.1.0] - 2025-08-14
- feat(core/mercadopago): nuevo endpoint `GET /api/tesoreria/core/mercadoPagoContext/all/active/to/change` para listar `chequeraCuotaId` activos con vencimientos en los últimos 90 días.
- feat(model): campo `lastVencimientoUpdated` en `MercadoPagoContext` con `@JsonFormat` y método `jsonify()` para logging estructurado.
- feat(repository): `findAllByActivoAndFechaVencimientoBetween(...)` para consultas por rango de fechas.
- refactor(service): `update(...)` ahora actualiza la entidad gestionada y es `@Transactional`, con logs JSON de entrada/salida.
- docs(ci): workflow de documentación mejorado; agregado render dinámico de diagramas Mermaid y árbol de dependencias; nuevos archivos en `docs/`.
- docs: agregados diagramas `architecture.mmd`, `entities.mmd`, `deployment.mmd` y múltiples `sequence-*.mmd`.

> Basado en `git diff --cached` y `pom.xml` (versión previa 1.0.0).

## [1.0.0] - 2025-08-11
### Added
- Nuevos endpoints: búsqueda por id de MercadoPago, reseteo de marca temporal de contratos, búsqueda optimizada de tipos de chequera, proveedores y pagos.
- Nuevos servicios: `ContratoAutoFixService`, `ProveedorSearchService`, `TipoChequeraSearchService`, `TipoPagoFechaAcreditacionService`, `TipoPagoFechaPagoService`, entre otros.
- Métodos `jsonify()` en modelos y DTOs para trazabilidad y logging estructurado.
- Test unitario para `ProveedorMovimientoController`.

### Changed
- Refactor completo de inyección de dependencias: eliminación de `@Autowired` en favor de constructores explícitos.
- Refactor de repositorios: eliminación del prefijo 'I' en interfaces, siguiendo convenciones de Spring.
- Mejoras de logging: uso de `Slf4j` y serialización JSON en logs.
- Optimizaciones de rendimiento: uso de `CompletableFuture` para consultas paralelas.
- Actualización de dependencias principales: Spring Boot 3.5.4, Kotlin 2.2.0, MySQL Connector 9.4.0.
- Actualización de diagramas de dependencias y documentación automática.

### Removed
- Archivos y configuraciones obsoletas: `TransactionConfig.java`, `RabbitMQConfig.java`, `JpaConfig.java`, `ChequeraMessageDto.kt`, `CuotaPeriodo.java`, `ChequeraCuotaDto.java`, `IChequeraPagoRepository.java`, `application.yml`, `logback-spring.xml`, y tests antiguos.

> Versión y cambios verificados en `pom.xml`, `git diff`, y código fuente.

## [0.0.1-SNAPSHOT] - Versión actual según pom.xml
## [0.1.0] - 2025-08-06
### Changed
- Actualización de dependencias:
  - Spring Boot: 3.5.3 → 3.5.4
  - Spring Boot Log4j2: 3.5.3 → 3.5.4
  - MySQL Connector: 9.3.0 → 9.4.0
- Dockerfile: Se otorgan permisos explícitos al usuario de la aplicación sobre el directorio `/app`.
- `bootstrap.yml`: Se agrega sección de health check para mail y ajustes en métricas.
- `FacultadService`: Se amplía el array de facultades soportadas (ahora incluye la facultad 6).

### Fixed
- Correcciones menores en configuración y permisos para mejorar despliegue y monitoreo.

> Versión actualizada a 0.1.0, basado en `pom.xml` y los cambios en `git diff HEAD`.

## [Unreleased] - Cambios verificables del git log

### Added
...existing code...
## [0.0.1-SNAPSHOT] - Versión actual según pom.xml
### Nota
La versión actual del proyecto es 0.0.1-SNAPSHOT según el pom.xml. Esta es la única versión que puede ser verificada directamente del código fuente.

### Dependencias Principales (verificado en pom.xml)
- Spring Boot: 3.5.0
- Spring Cloud: 2025.0.0
- Kotlin: 2.1.21
- Java: 21
- MySQL Connector: 9.3.0
- SpringDoc OpenAPI: 2.8.9
- Apache POI: 5.4.1
- OpenPDF: 2.2.1
- ModelMapper: 3.2.3
- Guava: 33.4.8-jre

### Características Implementadas (verificado en código)
- Gestión de chequeras y pagos con optimizaciones de rendimiento
- Cálculo de deudas con computación paralela
- Integración con Mercado Pago para tarjetas de crédito
- Gestión de inscripciones y personas
- Gestión de domicilios y documentos
- Documentación automática con OpenAPI/Swagger
- CI/CD con GitHub Actions
- Soporte para Docker

### Optimizaciones de Rendimiento (verificado en código)
- **Computación paralela**: La función `calculateDeuda` utiliza `CompletableFuture` para ejecutar consultas en paralelo
- **Validación temprana**: Verificación de parámetros nulos antes del procesamiento
- **Optimización de memoria**: Uso de `Map<String, BigDecimal>` en lugar de objetos completos
- **Eliminación de objetos innecesarios**: Evita crear instancias vacías de `ChequeraPago`

### Commits de Optimización Verificables (git log)
- `447b4b2` - Merge PR #141: Refactoring de repositorios y optimización de rendimiento (17 de junio 2025)
- `3d6fd9d` - refactor(controllers): remove hardcoded request mappings and optimize imports (17 de junio 2025)
- `9cab730` - Merge PR #139: Refactoring eliminación de prefijo 'I' en repositorios y actualización de documentación (17 de junio 2025)
- `5f3d934` - refactor(repositories): remove 'I' prefix from repository interfaces (17 de junio 2025)
- `8a93581` - Merge PR #137: Optimización de rendimiento en calculatedeuda con computación paralela (10 de junio 2025)
- `c6632e9` - perf(calculateDeuda): implement parallel computation with CompletableFuture (10 de junio 2025)
- `a7e078b` - Merge PR #135: Actualización de documentación y limpieza de código (3 de junio 2025)
- `cc5be90` - docs(changelog): actualiza documentación con cambios verificables (3 de junio 2025)
- `ab3bc99` - Merge PR #133: Cambio emailpagador inicial
- `62acf40` - fix emailPagador
- `1e8a299` - Merge PR #131: Limpieza de código y mejoras en gestión de domicilios
- `42b667c` - chore(cleanup): remove obsolete files and improve SpoterService
- `1cc4146` - Merge PR #129: Refactorización y mejoras en gestión de domicilios y documentos
- `a8b9de1` - refactor(domicilio): improve dependency injection and add pagador endpoint
- `7ddfee9` - Merge PR #127: Feature implementación de endpoint para obtener información completa de inscripciones
- `833a92b` - feat(inscripcion): add full endpoint for complete inscription information

### Información No Verificable
Las siguientes versiones mencionadas en el CHANGELOG anterior no pueden ser verificadas directamente del código fuente y han sido removidas:
- Versión 1.0.0 (2024-03-19)
- Versión 0.2.0 (2024-03-11) 
- Versión 0.1.0 (2024-01-01)

Estas versiones no aparecen en el historial de git ni en el pom.xml actual, por lo que no pueden ser confirmadas como verificables.