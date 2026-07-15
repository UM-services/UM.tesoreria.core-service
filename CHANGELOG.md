# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

## [3.34.0] - 2026-07-10
### Added
- feat(chequeraCuota): Enriquecimiento del modelo de dominio `ChequeraCuota` con nuevas asociaciones a `Facultad` y `TipoChequera`
  - Nuevos campos `Facultad facultad` y `TipoChequera tipoChequera` en `ChequeraCuota` domain model
  - `ChequeraCuotaMapper.toDomain()`: mapeo de nuevas asociaciones desde `FacultadMapper` y `TipoChequeraMapper` inyectados
  - `ChequeraCuotaResponse`: expone objetos `Facultad` y `TipoChequera` en respuestas REST
  - `ChequeraCuotaDtoMapper.toResponse()`: mapeo de nuevas asociaciones del dominio al DTO
  - Actualización de constructores en `PreuniversitarioChequeraService` y `SpoterService` con parámetros null para las nuevas asociaciones
- feat(facultad): Nuevo campo `guaraniResponsableAcademica` (Integer) en el módulo Facultad
  - `Facultad` domain model: nuevo campo `guaraniResponsableAcademica`
  - `FacultadEntity`: persistencia del campo con mapeo JPA automático
  - `FacultadMapper.toDomain()`: mapeo del nuevo campo
  - `FacultadResponse`: expone el campo en respuestas REST
  - `FacultadDtoMapper.toResponse()`: mapeo del nuevo campo al DTO
- feat(util): Nueva interfaz `Jsonifyable` con método `jsonify()` por defecto
  - Interfaz `Jsonifyable` en `core/util/` con implementación por defecto que delega a `Jsonifier.builder(this).build()`
  - `ChequeraCuota` ahora implementa `Jsonifyable`, eliminando la implementación manual de `jsonify()`

### Changed
- refactor(chequeraCuota): `ChequeraCuota` implementa `Jsonifyable` en lugar de definir `jsonify()` manualmente
  - Eliminación del método `jsonify()` en `ChequeraCuota.java` (ahora se hereda de la interfaz)
  - `GetChequeraCuotaByUniqueUseCaseImpl`: añadido `@Slf4j` y logging debug con `jsonify()`

### Fixed
- fix(mappers): Null-safety en 16 mappers de persistencia hexagonal
  - `toEntity()`: Cambio de `return Entity.builder()...build()` a builder intermedio con validación `!= null` antes de setear cada campo
  - `updateEntity()`: Añadidas validaciones `!= null` antes de cada `setField()` en `ChequeraCuotaMapper`
  - Afecta: `ArticuloMapper`, `ArancelPorcentajeMapper`, `ArancelTipoMapper`, `ChequeraCuotaMapper`, `ChequeraSerieMapper`, `ClaseChequeraMapper`, `ProductoMapper`, `TipoChequeraMapper`, `CuentaMapper`, `ContratoMapper`, `DependenciaMapper`, `DocumentoMapper`, `DomicilioMapper`, `LectivoMapper`, `MercadoPagoContextMapper`, `ProveedorMapper`
  - Previene `NullPointerException` en persistencia cuando campos opcionales del dominio son nulos

> Basado en análisis profundo de `git diff HEAD` (16 archivos staged, +200/-161 líneas) y `pom.xml` (versión 3.33.0 → 3.34.0).

## [3.33.1] - 2026-07-06
### Fixed
- fix(docs): Corregida sintaxis de genéricos en 20 diagramas Mermaid de arquitectura hexagonal y secuencia
  - Cambio de `~` (tilde) a `<>` (angle brackets) para tipos genéricos: `List~Documento~` → `List<Documento>`, `Optional~Facultad~` → `Optional<Facultad>`, `ResponseEntity~List~Foo~~` → `ResponseEntity<List<Foo>>`
  - Afecta a todos los diagramas `hexagonal-*.mmd` y `sequence-consulta-articulos.mmd`
  - Sincroniza la sintaxis con la especificación estándar Mermaid (v10+), mejorando la compatibilidad con parsers y herramientas de renderizado

> Basado en `git diff HEAD` (20 archivos modificados, +431/-431 líneas, corrección únicamente sintáctica en diagramas de documentación). Sin cambios en código de aplicación.

## [3.33.0] - 2026-07-06
### Added
- feat(documento): Nuevo módulo Documento con arquitectura hexagonal completa
  - Modelo de dominio: `Documento` con campos `documentoId`, `nombre`
  - Puertos de entrada (2): `GetAllDocumentosUseCase`, `GetDocumentoByIdUseCase`
  - Puerto de salida: `DocumentoRepository` con métodos `findAll()`, `findById(Integer)`
  - Casos de uso: `GetAllDocumentosUseCaseImpl`, `GetDocumentoByIdUseCaseImpl`
  - Servicio de aplicación: `DocumentoService` con métodos `findAll()`, `findByDocumentoId(Integer)`
  - Adaptador JPA: `JpaDocumentoRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `DocumentoEntity` reemplazando `Documento.kt` (Kotlin legacy)
  - Repositorio JPA: `JpaDocumentoRepository` con consulta `findByDocumentoId`
  - Mapper: `DocumentoMapper` para conversión entidad ↔ dominio
  - Controlador REST: `DocumentoController` con endpoints `GET /documento/` y `GET /documento/{documentoId}`
  - DTOs: `DocumentoRequest`, `DocumentoResponse`
  - DTO Mapper: `DocumentoDtoMapper` para conversión DTO ↔ dominio
  - Excepción: `DocumentoException` movida a `hexagonal/documento/application/exception/`
- feat(preuniversitario): Integración de `DocumentoService` en `PreuniversitarioChequeraService.create()`
  - Validación de `tipoDocumento` > 1 contra BD antes de asignar documentoId
  - Fallback a documentoId=1 si el tipo de documento no existe en la base de datos
  - Previene errores de integridad referencial al crear chequeras desde Guarani con tipos de documento no registrados
- feat(docs): Nuevo diagrama Mermaid `hexagonal-documento.mmd` para el módulo Documento

### Removed
- Eliminación de `Documento.kt` (Kotlin legacy) del paquete `core/kotlin/model/`
- Eliminación de `DocumentoRepository.java` (legacy) del paquete `core/repository/`
- Eliminación de `DocumentoService.java` (legacy) del paquete `core/service/`
- Eliminación de `DocumentoController.java` (legacy) del paquete `core/controller/`

> Basado en análisis profundo de `git diff HEAD` (21 archivos, +337/-120 líneas, incluyendo migración completa del módulo Documento a hexagonal + integración en PreuniversitarioChequeraService) y `pom.xml` (versión 3.32.2 → 3.33.0).

## [3.32.2] - 2026-07-05
### Fixed
- fix(domicilio): Null-safety en campos email de `JpaDomicilioRepositoryAdapter` y `DomicilioMapper`
  - `JpaDomicilioRepositoryAdapter.update()`: Asigna string vacío si `emailPersonal` o `emailInstitucional` es null al actualizar domicilio
  - `DomicilioMapper.toEntity()`: Asigna string vacío si `emailPersonal` o `emailInstitucional` es null en el mapeo a entidad
  - Previene `NullPointerException` en persistencia de domicilios cuando los campos email no están informados

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +8/-8 líneas) y `pom.xml` (versión 3.32.1 → 3.32.2).

## [3.32.1] - 2026-07-05
### Fixed
- fix(preuniversitario): Null-safety en creación de persona/domicilio y envío de chequera preuniversitaria
  - `PreuniversitarioChequeraService.create()`: Envuelve `personaService.create(persona)` en try-catch con `PersonaException`, retornando null si falla
  - `PreuniversitarioChequeraService.create()`: Envuelve `domicilioService.create(domicilio)` en try-catch con `DomicilioException`, retornando null si falla
  - `CreatePreuniversitarioUseCaseImpl.createPreuniversitario()`: Agrega null-guard para `chequeraSerie`, retornando `alumnoGuarani` sin enviar mail si la chequera no se pudo crear
  - Previene `NullPointerException` y envío de chequeras incompletas cuando la creación de persona o domicilio falla desde Guarani

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +17/-0 líneas) y `pom.xml` (versión 3.32.0 → 3.32.1).

## [3.32.0] - 2026-07-05
### Added
- feat(mercadopagoContext): Integración de pago MercadoPago con ReservaVacante en `ProcessPaymentEventUseCaseImpl`
  - Nuevo flujo condicional: si `MercadoPagoContext.reservaVacanteId != null`, procesa el pago como reserva de vacante
  - Al recibir evento `approved`, busca la `ReservaVacante` vía `ReservaVacanteService.findReservaVacante(UUID)` y actualiza su estado a `"pagado"`
  - Nuevo campo `reservaVacanteId` (UUID) en `PaymentProcessedEvent` para identificar la reserva en el evento de pago
  - Nueva dependencia `ReservaVacanteService` inyectada en `ProcessPaymentEventUseCaseImpl`
- feat(reservaVacante): Nuevo `UpdateReservaVacanteUseCase` con implementación completa
  - Nuevo puerto de entrada: `UpdateReservaVacanteUseCase` con método `update(ReservaVacante, UUID)`
  - Nueva implementación: `UpdateReservaVacanteUseCaseImpl` con actualización de campos `estado`, `importe`, `vencimiento`
  - Nuevo método `updateReservaVacante(ReservaVacante, UUID)` en `ReservaVacanteService`
  - Nuevo método `update(ReservaVacante, UUID)` en `ReservaVacanteRepository` (interfaz y adaptador JPA)

> Basado en análisis profundo de `git diff HEAD` (8 archivos modificados, +68/-15 líneas, incluyendo cambios en `ProcessPaymentEventUseCaseImpl`, `ReservaVacanteService`, `UpdateReservaVacanteUseCase` y adaptador JPA) y `pom.xml` (versión 3.31.0 → 3.32.0).

## [3.31.0] - 2026-07-03
### Added
- feat(lectivoTotalImputacion): Nuevo caso de uso `FindAllByLectivoUseCase` y endpoint `GET /lectivo/{lectivoId}`
  - Nuevo puerto de entrada: `FindAllByLectivoUseCase` con método `findAllByLectivo(Integer lectivoId)`
  - Nueva implementación: `FindAllByLectivoUseCaseImpl`
  - Nuevo método `findAllByLectivo(Integer)` en `LectivoTotalImputacionRepository`
  - Nuevo endpoint: `GET /api/tesoreria/core/lectivototalimputacion/lectivo/{lectivoId}`
  - Nueva ruta alternativa: `/api/tesoreria/core/lectivototalimputacion` como base path
- feat(lectivoTotalImputacion): Enriquecimiento del modelo de dominio con asociaciones a `Facultad`, `Lectivo`, `TipoChequera`, `Producto` y `Cuenta`
  - `LectivoTotalImputacion` domain model: nuevos campos `facultad`, `lectivo`, `tipoChequera`, `producto`, `cuenta`
  - `LectivoTotalImputacionEntity`: corregida anotación `@JoinColumn` de `columnDefinition` a `name` (fix)
  - `LectivoTotalImputacionResponse`: expone objetos completos de asociaciones en respuestas REST
  - `LectivoTotalImputacionDtoMapper.toResponse()`: mapea nuevas asociaciones del dominio al DTO
  - `LectivoTotalImputacionMapper`: ahora con `@RequiredArgsConstructor` e inyección de 5 mappers de dominio
  - Refactorizado `toDomain()` para mapear asociaciones desde entidades JPA a modelos de dominio
- feat(lectivoTotalImputacion): Refactorización de `ResponseEntity` API en controller
  - Migración de `new ResponseEntity<>(..., HttpStatus.OK)` a `ResponseEntity.ok(...)`
  - Migración de `new ResponseEntity<>(HttpStatus.NOT_FOUND)` a `ResponseEntity.notFound().build()`
- feat(tests): Nuevos tests unitarios y de integración para el módulo LectivoTotalImputacion (8 tests)
  - `LectivoTotalImputacionControllerTest`: controller REST con MockMvc
  - `LectivoTotalImputacionServiceTest`: service con mocking de casos de uso
  - `FindAllByLectivoUseCaseImplTest`, `AddLectivoTotalImputacionUseCaseImplTest`, `FindAllByTipoUseCaseImplTest`, `FindByProductoUseCaseImplTest`, `UpdateLectivoTotalImputacionUseCaseImplTest`: casos de uso individuales
  - `JpaLectivoTotalImputacionRepositoryAdapterTest`, `JpaLectivoTotalImputacionRepositoryTest`: integración con BBDD H2
  - `LectivoTotalImputacionMapperTest`, `LectivoTotalImputacionDtoMapperTest`: mappers
- feat(deps): Nuevas dependencias de test `spring-boot-starter-data-jpa-test` y `spring-boot-starter-webmvc-test`
- fix(test): Agregado `INIT=SET REFERENTIAL_INTEGRITY FALSE` en H2 test URL para evitar violaciones de FK

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +1071/-14 líneas) y `pom.xml` (versión 3.30.0 → 3.31.0).

## [3.30.0] - 2026-07-01
### Added
- feat(producto): Nuevo módulo Producto con arquitectura hexagonal completa
  - Modelo de dominio: `Producto` con campos `productoId`, `nombre` y método `jsonify()`
  - Puertos de entrada (2): `GetAllProductosUseCase`, `GetProductoByIdUseCase`
  - Puerto de salida: `ProductoRepository` con métodos CRUD
  - Casos de uso: `GetAllProductosUseCaseImpl`, `GetProductoByIdUseCaseImpl`
  - Servicio de aplicación: `ProductoService` con integración a `ProductoTipoChequeraRepository`
  - Adaptador JPA: `JpaProductoRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `ProductoEntity` reemplazando `Producto.kt` (Kotlin legacy)
  - Controlador REST: `ProductoController` con endpoints CRUD
  - DTOs: `ProductoRequest`, `ProductoResponse`
  - DTO Mapper: `ProductoDtoMapper`
  - Excepción: `ProductoException`
- feat(tipoChequera): Nuevo módulo TipoChequera con arquitectura hexagonal completa
  - Modelo de dominio: `TipoChequera` con 11 campos, relaciones a `Geografica` y `ClaseChequera`, método `jsonify()`
  - Puertos de entrada (11): CRUD completo + `GetAllTipoChequeraAsignable`, `GetAllTipoChequeraByClaseChequera`, `GetAllTipoChequeraByFacultadAndGeografica`, `MarkTipoChequera`, `UnmarkTipoChequera`
  - Puerto de salida: `TipoChequeraRepository` con 11 métodos
  - Casos de uso: 11 implementaciones individuales
  - Servicio de aplicación: `TipoChequeraService` con integración a `TipoChequeraSearchService`
  - Entidad JPA: `TipoChequeraEntity` reemplazando `TipoChequera.kt` (Kotlin legacy)
  - Controlador REST: `TipoChequeraController` con 9 endpoints
  - DTOs: `TipoChequeraRequest`, `TipoChequeraResponse`
  - DTO Mapper: `TipoChequeraDtoMapper`
  - Excepción: `TipoChequeraException`
- feat(lectivo): Nuevo módulo Lectivo con arquitectura hexagonal completa
  - Modelo de dominio: `Lectivo` con campos `lectivoId`, `nombre`, `fechaInicio`, `fechaFinal` y método `jsonify()`
  - Puertos de entrada (8): CRUD + `GetAllLectivosReverse`, `GetAllLectivosByPersona`, `GetLectivoByFecha`, `GetLastLectivo`
  - Puerto de salida: `LectivoRepository` con 8 métodos
  - Casos de uso: 8 implementaciones individuales
  - Servicio de aplicación: `LectivoService`
  - Entidad JPA: `LectivoEntity` reemplazando `Lectivo.kt` (Kotlin legacy)
  - Controlador REST: `LectivoController` con 7 endpoints
  - DTOs: `LectivoRequest`, `LectivoResponse`
  - DTO Mapper: `LectivoDtoMapper`
  - Excepción: `LectivoException`
- feat(lectivoTotalImputacion): Nuevo módulo LectivoTotalImputacion con arquitectura hexagonal completa
  - Modelo de dominio: `LectivoTotalImputacion` con campos `lectivoTotalImputacionId`, `facultadId`, `lectivoId`, `tipoChequeraId`, `productoId`, `cuenta`
  - Puertos de entrada (4): `AddLectivoTotalImputacionUseCase`, `FindAllByTipoUseCase`, `FindByProductoUseCase`, `UpdateLectivoTotalImputacionUseCase`
  - Puerto de salida: `LectivoTotalImputacionRepository` con 5 métodos
  - Casos de uso: 4 implementaciones individuales
  - Entidad JPA: `LectivoTotalImputacionEntity` reemplazando `LectivoTotalImputacion.java` (legacy)
  - Controlador REST: `LectivoTotalImputacionController` con endpoints GET/POST/PUT
  - DTOs: `LectivoTotalImputacionRequest`, `LectivoTotalImputacionResponse`
  - DTO Mapper: `LectivoTotalImputacionDtoMapper`
  - Excepción: `LectivoTotalImputacionException`
- feat(arancelTipo): Migración completa a casos de uso individuales con arquitectura hexagonal
  - 8 nuevos casos de uso: `CreateArancelTipoUseCaseImpl`, `DeleteArancelTipoUseCaseImpl`, `GetAllArancelTiposUseCaseImpl`, `GetAllHabilitadosUseCaseImpl`, `GetArancelTipoByIdCompletoUseCaseImpl`, `GetArancelTipoByIdUseCaseImpl`, `GetLastArancelTipoUseCaseImpl`, `GetNewArancelTipoUseCaseImpl`, `UpdateArancelTipoUseCaseImpl`
  - Nuevos puertos de entrada (9) correspondientes
  - Nuevo puerto de salida: `ArancelTipoRepository` con métodos CRUD y búsquedas
  - Nuevo repositorio JPA: `JpaArancelTipoRepositoryAdapter`
  - Nuevos DTOs: `ArancelTipoRequest`, `ArancelTipoResponse`
  - Nuevo mapper: `ArancelTipoDtoMapper`, `ArancelTipoMapper` (persistencia)
- feat(arancelPorcentaje): Migración completa a casos de uso individuales con arquitectura hexagonal
  - 4 nuevos casos de uso: `AddArancelPorcentajeUseCaseImpl`, `FindAllByArancelTipoIdUseCaseImpl`, `FindByUniqueUseCaseImpl`, `UpdateArancelPorcentajeUseCaseImpl`
  - Nuevos puertos de entrada (4) correspondientes
  - Nuevo puerto de salida: `ArancelPorcentajeRepository`
  - Nuevo repositorio JPA: `JpaArancelPorcentajeRepositoryAdapter`
  - Nuevos DTOs: `ArancelPorcentajeRequest`, `ArancelPorcentajeResponse`
  - Nuevo mapper: `ArancelPorcentajeMapper`, `ArancelPorcentajeDtoMapper`
- feat(claseChequera): Nuevo módulo ClaseChequera con arquitectura hexagonal completa
  - Modelo de dominio: `ClaseChequera` con campos `claseChequeraId`, `nombre`, `preuniversitario`, `grado`, `posgrado`, `curso`, `secundario`, `titulo`, y método `jsonify()`
  - Puertos de entrada (4): `GetAllClaseChequeraUseCase`, `GetAllClaseChequeraByCursoUseCase`, `GetAllClaseChequeraByPosgradoUseCase`, `GetAllClaseChequeraByTituloUseCase`
  - Puerto de salida: `ClaseChequeraRepository` con métodos CRUD y búsquedas por tipo
  - Casos de uso: Implementaciones completas en `application/usecases/`
  - Servicio de aplicación: `ClaseChequeraService` con delegación a casos de uso
  - Adaptador JPA: `JpaClaseChequeraRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `ClaseChequeraEntity` (Java) reemplazando `ClaseChequeraEntity.kt` (Kotlin)
  - Repositorio JPA: `JpaClaseChequeraRepository`
  - Controlador REST: `ClaseChequeraController` con endpoints GET
  - DTOs: `ClaseChequeraRequest`, `ClaseChequeraResponse`
  - DTO Mapper: `ClaseChequeraDtoMapper` para conversión DTO ↔ dominio
  - Excepción: `ClaseChequeraException`
- feat(chequeraCuota): Migración completa a casos de uso individuales con arquitectura hexagonal
  - 20 nuevos casos de uso en `application/usecases/`: `CalculateTotalCuotasActivasUseCaseImpl`, `CalculateTotalCuotasPagadasUseCaseImpl`, `DeleteAllByChequeraUseCaseImpl`, `FindAllByChequeraAlternativaConImporteUseCaseImpl`, `FindAllByChequeraAlternativaUseCaseImpl`, `FindAllByChequeraIdsUseCaseImpl`, `FindAllByChequeraUseCaseImpl`, `FindAllByFacultadTipoChequeraSerieIdsUseCaseImpl`, `FindAllDebidasUseCaseImpl`, `FindAllInconsistenciasUseCaseImpl`, `FindAllPendientesBajaUseCaseImpl`, `FindAllPendientesUseCaseImpl`, `FindAllPeriodosLectivoUseCaseImpl`, `GetChequeraCuotaByIdUseCaseImpl`, `GetChequeraCuotaByUniqueUseCaseImpl`, `GetOneActivaImpagaPreviaUseCaseImpl`, `SaveAllChequeraCuotasUseCaseImpl`, `UpdateBarrasUseCaseImpl`, `UpdateChequeraCuotaUseCaseImpl`
  - Nuevos puertos de entrada (20) correspondientes en `domain/ports/in/`
  - Nuevos DTOs: `ChequeraCuotaRequest`, `ChequeraCuotaResponse`
  - Nueva capa web: `ChequeraCuotaDtoMapper` para conversión DTO ↔ dominio
  - Nuevo `ChequeraCuotaCalculoDeudaService` con lógica de cálculo de deuda separada
  - Modelo de dominio enriquecido: nuevos campos `importe1Original`, `importe2`, `importe2Original`, `importe3`, `importe3Original`, `vencimiento2`, `vencimiento3`, `codigoBarras`, `i2Of5`, `manual`, `tramoId`, `arancelTipoId`, relaciones a `Producto` y `ChequeraSerie`, método `jsonify()`
  - `ChequeraCuotaDeudaService` refactorizado y simplificado con `@RequiredArgsConstructor` y `.peek()`
  - `ChequeraCuotaService` simplificado: lógica delegada a casos de uso individuales
  - `ChequeraCuotaException` movido a `hexagonal/chequera/chequeraCuota/application/exception/`
- feat(chequeraSerie): Migración completa a casos de uso individuales con arquitectura hexagonal
  - 15 nuevos casos de uso en `application/usecases/`: `CreateChequeraSerieUseCaseImpl`, `DeleteChequeraSerieUseCaseImpl`, `GetChequeraSerieAltasUseCaseImpl`, `GetChequeraSerieByCbuOrVisaUseCaseImpl`, `GetChequeraSerieByFacultadUseCaseImpl`, `GetChequeraSerieByIdUseCaseImpl`, `GetChequeraSerieByNumberUseCaseImpl`, `GetChequeraSerieByPersonaUseCaseImpl`, `GetChequeraSerieByUniqueUseCaseImpl`, `GetChequeraSerieIncompletasUseCaseImpl`, `GetChequeraSerieSpecialQueriesUseCaseImpl`, `GetResumenLectivoUseCaseImpl`, `MarkSentUseCaseImpl`, `SetPayPerTicUseCaseImpl`, `UpdateChequeraSerieUseCaseImpl`
  - Nuevos puertos de entrada (15) correspondientes en `domain/ports/in/`
  - Nuevo puerto de salida: `ChequeraSerieRepository` con métodos completos CRUD y búsquedas
  - NUEvos DTOs: `ChequeraSerieRequest`, `ChequeraSerieResponse`
  - Nueva capa web: `ChequeraSerieDtoMapper` para conversión DTO ↔ dominio
  - Nuevo adaptador JPA: `JpaChequeraSerieRepositoryAdapter` con mapeo dominio ↔ entidad
  - Modelo de dominio enriquecido: nuevos campos (`arancelTipoId`, `cursoId`, `asentado`, `geograficaId`, `fecha`, `cuotasPagadas`, `observaciones`, `algoPagado`, `tipoImpresionId`, `flagPayperTic`, `usuarioId`, `enviado`, `retenida`, `version`, `hpum`, `becaPorcentaje`, `becaResolucion`, `becaFecha`, `becaUserId`), relaciones a (`ArancelTipo`, `Domicilio`, `Facultad`, `Geografica`, `Lectivo`, `Persona`, `TipoChequera`), campos transient (`cuotasDeuda`, `importeDeuda`, `ultimoEnvio`), métodos `getPersonaKey()`, `getFacultadKey()`, `jsonify()`
  - `ChequeraSerieException` movido a `hexagonal/chequera/chequeraSerie/application/exception/`
  - `PreuniversitarioChequeraService` actualizado para usar nuevos casos de uso

### Changed
- refactor(package): Reorganización masiva de módulos bajo paquete `hexagonal/chequera/`
  - `hexagonal/arancelTipo/` → `hexagonal/chequera/arancelTipo/`
  - `hexagonal/arancelPorcentaje/` → `hexagonal/chequera/arancelPorcentaje/`
  - `hexagonal/chequeraCuota/` → `hexagonal/chequera/chequeraCuota/`
  - `hexagonal/chequeraSerie/` → `hexagonal/chequera/chequeraSerie/`
  - `hexagonal/producto/` → `hexagonal/chequera/producto/`
  - `hexagonal/tipoChequera/` → `hexagonal/chequera/tipoChequera/`
  - Actualizadas todas las referencias de imports en todos los archivos del proyecto
- refactor(chequeraCuota): `ChequeraCuotaService` simplificado, lógica delegada a 20+ casos de uso individuales
- refactor(chequeraSerie): `ChequeraSerieService` simplificado, lógica delegada a 15+ casos de uso individuales
- refactor(chequeraSerie): `ChequeraSerieMapper` movido a `hexagonal/chequera/chequeraSerie/infrastructure/persistence/mapper/`
- refactor(lectivo): Migración completa del módulo Lectivo a arquitectura hexagonal con Java
  - Eliminación de `Lectivo.kt` (Kotlin) del paquete `core/kotlin/model/`
  - `LectivoService` y `LectivoController` movidos de `core/service|controller/` a `hexagonal/lectivo/`
- refactor(lectivoTotalImputacion): Migración completa del módulo LectivoTotalImputacion a arquitectura hexagonal con Java
  - Eliminación de `LectivoTotalImputacion.java` (legacy) del paquete `core/model/`
  - Nuevo controlador REST, DTOs, mapper y excepción en `hexagonal/lectivoTotalImputacion/`
- refactor(producto): Migración completa del módulo Producto a arquitectura hexagonal con Java
  - Eliminación de `Producto.kt` (Kotlin) del paquete `core/kotlin/model/`
  - `ProductoService` y `ProductoController` movidos a `hexagonal/chequera/producto/`
- refactor(tipoChequera): Migración completa del módulo TipoChequera a arquitectura hexagonal con Java
  - Eliminación de `TipoChequera.kt` (Kotlin) del paquete `core/kotlin/model/`
  - `TipoChequeraService` y `TipoChequeraController` movidos a `hexagonal/chequera/tipoChequera/`
- refactor(arancelTipo): Migración completa a casos de uso individuales con repositorio y mapper propios
  - Nuevo `JpaArancelTipoRepositoryAdapter`, `ArancelTipoMapper`, `ArancelTipoDtoMapper`, DTOs Request/Response
- refactor(arancelPorcentaje): Migración completa a casos de uso individuales con repositorio y mapper propios
  - Nuevo `JpaArancelPorcentajeRepositoryAdapter`, `ArancelPorcentajeMapper`, `ArancelPorcentajeDtoMapper`, DTOs Request/Response
- refactor(cuenta): Reorganización de paquete a `hexagonal/contable/cuenta/`
  - Actualización de referencias cruzadas con módulo Contable
- refactor(controller): `ChequeraController.java` actualizado con nuevas referencias de paquetes
- refactor(facade): `BalanceService`, `ChequeraService`, `CompraService`, `ContabilidadService`, `CostoService`, `FormulariosToPdfService`, `MailChequeraService`, `MercadoPagoCoreService`, `PagarFileService`, `PayPerTicFileService`, `ProcessBajaService`, `SheetService`, `SincronizeService`, `SpoterService` actualizados para usar nuevas referencias hexagonales
- refactor(exception): `ChequeraCuotaException`, `ChequeraSerieException`, `ArancelTipoException`, `ArancelPorcentajeException`, `AsientoException`, `ProductoException`, `TipoChequeraException`, `LectivoException`, `LectivoTotalImputacionException` movidos de `core/exception/` a sus módulos hexagonales
- refactor(mapper): `ChequeraSerieMapper.java` actualizado con nuevos campos y mapeos
- refactor(jsonify): Métodos `jsonify()` añadidos a modelos de dominio `ChequeraCuota`, `ChequeraSerie`, `ClaseChequera`, `Producto`, `TipoChequera`, `Lectivo`, y entidades JPA correspondientes para logging estructurado

### Removed
- Eliminación de `ChequeraCuotaDeudaService.java` legacy (`core/service/view/`)
- Eliminación de módulos Kotlin legacy: `ClaseChequeraEntity.kt`, `ClaseChequera.kt`, `Lectivo.kt`, `Producto.kt`, `TipoChequera.kt`
- Eliminación de repositorios legacy: `ClaseChequeraRepository.java`, `LectivoRepository.java`, `LectivoTotalImputacionRepository.java`, `ProductoRepository.java`, `TipoChequeraRepository.java`
- Eliminación de servicios legacy: `ClaseChequeraService.java`, `LectivoService.java`, `LectivoTotalImputacionService.java`, `ProductoService.java`, `TipoChequeraService.java`, `ChequeraCuotaService.java`
- Eliminación de controladores legacy: `ClaseChequeraController.java`, `LectivoController.java`, `LectivoTotalImputacionController.java`, `ProductoController.java`, `TipoChequeraController.java`, `ArancelPorcentajeController.java`
- Eliminación de `LectivoTotalImputacion.java` (modelo legacy) del paquete `core/model/`

### Added
- feat(docs): Nuevos diagramas Mermaid para módulos Producto, TipoChequera, Lectivo, LectivoTotalImputación, ClaseChequera; actualizados ChequeraCuota (a classDiagram con 20+ casos de uso) y ChequeraSerie (de flowchart a classDiagram con 15+ casos de uso)

> Basado en análisis profundo de `git diff HEAD` (146 archivos, +4387/-1264 líneas, incluyendo migración masiva de 6 módulos a hexagonal + paquete contable) y `pom.xml` (versión 3.29.0 → 3.30.0).

## [3.29.0] - 2026-06-26
### Added
- feat(Tool): Nuevos mappings de propuesta a facultadId en `Tool.convert2Tesium()`
  - Nuevas propuestas: 70, 111, 125-134 → facultad 1; 72, 121-123 → facultad 2; 74, 120 → facultad 3; 75, 112-118 → facultad 4; 68, 124 → facultad 5
  - Eliminados mappings legacy: propuestas 109, 73, 108 reemplazados por valores actualizados
  - Amplía compatibilidad con nuevas sedes/carreras del sistema Guarani
- feat(chequeraSerie): Nuevo `PreuniversitarioChequeraService` en `hexagonal/chequeraSerie/application/service/`
  - Lógica de creación de chequera preuniversitaria extraída de `CreatePreuniversitarioUseCaseImpl`

### Changed
- refactor(arancelTipo): Migración completa del módulo ArancelTipo a arquitectura hexagonal (Java)
  - Eliminación de `ArancelTipo.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Creación de `ArancelTipoEntity.java` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`) en `hexagonal/arancelTipo/infrastructure/persistence/entity/`
  - Creación de `JpaArancelTipoRepository.java` en `hexagonal/arancelTipo/infrastructure/persistence/repository/`
  - `ArancelTipoService` movido de `core/service/` a `hexagonal/arancelTipo/application/service/`
  - `ArancelTipoException` movido de `core/exception/` a `hexagonal/arancelTipo/application/exception/`
  - `ArancelTipoController` movido de `core/controller/` a `hexagonal/arancelTipo/infrastructure/web/controller/`
  - Referencias actualizadas en `ChequeraSerieReemplazo.kt`, `ChequeraSerieAlta.kt`, `ChequeraSerieAltaFull.kt`, `ChequeraIncompleta.java`, `ChequeraKey.java`, `ChequeraPreuniv.java`, `SheetService.java`, `ChequeraService.java`
- refactor(arancelPorcentaje): Migración completa del módulo ArancelPorcentaje a arquitectura hexagonal (Java)
  - Eliminación de `ArancelPorcentaje.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Creación de `ArancelPorcentajeEntity.java` en `hexagonal/arancelPorcentaje/infrastructure/persistence/entity/`
  - Creación de `JpaArancelPorcentajeRepository.java` en `hexagonal/arancelPorcentaje/infrastructure/persistence/repository/`
  - Nuevo `ArancelPorcentajeService` en `hexagonal/arancelPorcentaje/application/service/`
  - Nuevo `ArancelPorcentajeController` en `hexagonal/arancelPorcentaje/infrastructure/web/controller/`
  - Referencias actualizadas en `PlantillaArancelService.java`
- refactor(asiento): Migración completa del módulo Asiento a arquitectura hexagonal (Java)
  - Eliminación de `Asiento.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Creación de `AsientoEntity.java` con Lombok Builder en `hexagonal/asiento/infrastructure/persistence/entity/`
  - Creación de `JpaAsientoRepository.java` en `hexagonal/asiento/infrastructure/persistence/repository/`
  - `AsientoService` movido de `core/service/` a `hexagonal/asiento/application/service/`
  - `AsientoException` movido de `core/exception/` a `hexagonal/asiento/application/exception/`
  - Referencias actualizadas en `ContabilidadService.java`, `CostoService.java`
  - Migración de `Asiento.Builder()` interno a `AsientoEntity.builder()` de Lombok
- refactor(alumnoGuarani): Simplificación del mapper y extracción de lógica de creación de chequera
  - Eliminación de 7 DTOs de infraestructura: `ContactoGuarani.java`, `DocumentoPrincipalGuarani.java`, `PersonaGuarani.java`, `PropuestaGuarani.java`, `PropuestaTipoGuarani.java`, `TipoDocumentoGuarani.java`, `UbicacionGuarani.java`
  - `AlumnoGuaraniDtoMapper` simplificado: eliminados 7 métodos privados de mapeo recursivo DTO → dominio
  - `CreatePreuniversitarioUseCaseImpl` delegado a `PreuniversitarioChequeraService`
  - Modelo de dominio `AlumnoGuarani`: campo `alumnoId` renombrado a `alumno`; nuevo campo `cantidadReadmisiones`
- refactor(baja): `BajaException` movido a `hexagonal/baja/application/exception/`
- refactor(chequeraSerie): `ChequeraSerieMapper` movido de `core/util/` a `hexagonal/chequeraSerie/infrastructure/web/mapper/`
- refactor(articulo): `ArticuloException` movido a `hexagonal/articulo/application/exception/`
- chore(logging): Añadido `@Slf4j` y logging con `\n\n` en `AlumnoGuaraniDtoMapper`, `MailChequeraService`, `AsientoService`

### Removed
- Eliminación de repositorios legacy: `ArancelPorcentajeRepository.java`, `ArancelTipoRepository.java`, `AsientoRepository.java`
- Eliminación de servicio legacy: `ArancelPorcentajeService.java`
- Eliminación de controladores legacy: `ArancelPorcentajeController.java`

> Basado en análisis profundo de `git diff HEAD` (64 archivos modificados, +693/-1075 líneas, incluyendo cambios locales en Tool.java) y `pom.xml` (versión 3.28.0 → 3.29.0).

## [3.28.0] - 2026-06-23
### Added
- feat(guarani): Nuevo módulo `alumnoGuarani` con arquitectura hexagonal para integración con sistema Guarani
  - Nuevo modelo de dominio `AlumnoGuarani` (aggregate root) con relaciones a `PersonaGuarani`, `PropuestaGuarani`, `UbicacionGuarani`
  - Nuevos modelos de dominio: `PersonaGuarani`, `DocumentoPrincipalGuarani`, `TipoDocumentoGuarani`, `ContactoGuarani`, `UbicacionGuarani`, `PropuestaGuarani`, `PropuestaTipoGuarani`
  - Nuevo puerto de entrada: `CreatePreuniversitarioUseCase` con método `createPreuniversitario(AlumnoGuarani)`
  - Nuevo puerto de entrada: `CheckAllPreuniversitarioWithoutChequeraUseCase` con método `desmarcarEnviados(List<AlumnoDeteccionRequest>)`
  - Nuevo caso de uso: `CreatePreuniversitarioUseCaseImpl` con orquestación completa de creación de chequera preuniversitaria (Persona, Domicilio, ChequeraSerie, ChequeraTotal, ChequeraAlternativa, ChequeraCuota)
  - Nuevo caso de uso: `CheckAllPreuniversitarioWithoutChequeraUseCaseImpl` para detección de preuniversitarios sin chequera
  - Nuevo servicio de aplicación: `AlumnoGuaraniService` como fachada de casos de uso
  - Nuevo controlador REST: `AlumnoGuaraniController` con endpoints:
    - `POST /api/tesoreria/core/guarani/alumno/create/preuniversitario`
    - `POST /api/tesoreria/core/guarani/alumno/desmarcar/enviadas`
  - Nuevos DTOs: `AlumnoGuaraniRequest`, `AlumnoDeteccionRequest`, `ChequeraContextFromGuaraniInternal`, y DTOs para cada sub-modelo Guarani
  - Nuevo mapper: `AlumnoGuaraniDtoMapper` para conversión DTO ↔ dominio (202 líneas, mapeo recursivo de árbol anidado)
- feat(guarani): Nueva utilidad `Tool.convert2Tesium()` para conversión de ubicación/propuesta Guarani a contexto Tesoreria (geograficaId, facultadId, tipoChequeraId)

### Changed
- chore(deps): Eliminada dependencia `spring-boot-starter-log4j2` (versionada explícitamente 4.1.0) — gestionada por Spring Boot parent POM 4.1.0
- refactor(geografica): Actualización en `JpaGeograficaRepositoryAdapter` para compatibilidad con nuevo módulo
- refactor(mail): Ajuste menor en `MailChequeraService` (3 líneas modificadas)

> Basado en análisis profundo de `git diff HEAD` (29 archivos modificados, +1205/-12 líneas) y `pom.xml` (versión 3.27.0 → 3.28.0).

## [3.27.0] - 2026-06-19
### Added
- feat(reservaVacante): Extended `vencimiento` from 2 to 60 days in `CreateReservaVacanteUseCaseImpl.createReservaVacante()`
  - `plusDays(2)` → `plusDays(60)` for more flexible reservation window
- feat(reservaVacante): Custom `importe` override from `ReservaVacanteRequest`
  - If `importe` is non-null and non-zero in the request, it overrides `Campanha.getValorReserva()`
  - New `BigDecimal importe` field in `ReservaVacanteRequest` DTO
  - Enables manual importe specification per reservation
- feat(reservaVacante): Added `jsonify()` structured logging method to `ReservaVacanteRequest` DTO
- chore(logging): Added `jsonify()` debug log of `ReservaVacanteRequest` in `CreateReservaVacanteUseCaseImpl`

### Fixed
- fix(domicilio): Added missing `@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXX")` annotation to `fecha` field in `Domicilio`, `DomicilioRequest`, and `DomicilioResponse`
  - Completes ISO 8601 compliance for Domicilio module (was missed in v3.26.1)

> Basado en análisis profundo de `git diff HEAD --cached` (5 archivos modificados, +15/-2 líneas) y `pom.xml` (versión 3.26.1 → 3.27.0).

## [3.26.1] - 2026-06-18
### Fixed
- fix(core): Extendida corrección ISO 8601 a todas las entidades del proyecto (61 archivos)
  - Migración del patrón `@JsonFormat` de `yyyy-MM-dd'T'HH:mm:ssZ` a `yyyy-MM-dd'T'HH:mm:ssXX` en todos los modelos Java y Kotlin del proyecto
  - Afecta modelos de dominio, entidades JPA, DTOs y vistas en los paquetes `model/`, `kotlin/model/` y `hexagonal/*/entity/`
  - Garantiza cumplimiento completo con el estándar ISO 8601 (incluye el separador `:` en el offset de zona horaria)

### Changed
- refactor(core): Unificado formato de fecha ISO 8601 en modelos restantes no cubiertos en v3.26.0
  - `model/`: ChequeraClase, ChequeraCuotaPersona, ChequeraIncompleta, ChequeraKey, ChequeraPagoReemplazo, ContratoFactura, CuentaMovimientoAsiento, CuentaSearch, CuotaDeuda, CuotaDeudaPayPerTic, Debito, DomicilioHistorico, DomicilioKey, FacturacionElectronica, IngresoAsiento, LegajoKey, LectivoCuota, PayPerTic, TipoPagoFechaAcreditacion, TipoPagoFechaPago
  - `kotlin/model/`: Asiento, BancoMovimiento, ChequeraCuotaReemplazo, ChequeraEliminada, ChequeraImpresionCabecera, ChequeraImpresionDetalle, ChequeraPago, ChequeraPagoAsiento, ChequeraSerieReemplazo, CuentaMovimiento, Ejercicio, Entrega, Lectivo, Legajo, Plan, ProveedorMovimiento, ProveedorPago, Usuario, ValorMovimiento, ChequeraCuotaDto, ChequeraSerieDto, LectivoDto, AsientoInternal, ClickPagosEntity, ChequeraCuotaDeuda, ChequeraSerieAlta, ChequeraSerieAltaFull
  - `kotlin/model/dto/`: ChequeraCuotaDto, ChequeraSerieDto, LectivoDto
  - `extern/model/dto/`: InscripcionDto
  - Entidades hexagonales: BajaEntity, ChequeraSerieEntity, ContratoEntity, CuentaEntity, DomicilioEntity, MercadoPagoContextEntity

> Basado en análisis profundo de `git diff HEAD` (61 archivos modificados, +115/-115 líneas) y `pom.xml` (versión 3.26.0 → 3.26.1).

## [3.26.0] - 2026-06-15
### Added
- feat(mercadopagoContext): New `FindActiveByReservaVacanteIdUseCase` for retrieving active MP context by `reservaVacanteId` (UUID)
  - New input port: `FindActiveByReservaVacanteIdUseCase` with method `findActiveByReservaVacanteId(UUID)`
  - New use case: `FindActiveByReservaVacanteIdUseCaseImpl` with delegation to repository
  - New repository method: `findByReservaVacanteIdAndActivo(UUID, Byte)` in `MercadoPagoContextRepository`
  - New JPA method: `findByReservaVacanteId(UUID)` in `JpaMercadoPagoContextRepository`
  - Adapter method: `findByReservaVacanteIdAndActivo()` in `JpaMercadoPagoContextRepositoryAdapter`
- feat(mercadopagoContext): New `PreferenceVacanteClient` Feign client for creating vacante preferences via `tesoreria-mercadopago-service`
- feat(mercadopagoContext): Added `jsonify()` method to `MercadoPagoContext` domain model for structured logging
- feat(reservaVacante): MercadoPago integration on reservation creation
  - `CreateReservaVacanteUseCaseImpl` now creates MP context via `MercadoPagoCoreService.createContextVacante()` and preference via `PreferenceVacanteClient.createPreference()`
  - New `createContextVacante()` and `buildResponseVacante()` methods in `MercadoPagoCoreService`
- feat(reservaVacante): Added `Campanha campanha` domain field to `ReservaVacante` model for enriched responses
- feat(reservaVacante): Enhanced `ReservaVacanteResponse` with `initPoint` from MercadoPagoContext and reorganized `estado` field
  - `ReservaVacanteDtoMapper` injects `MercadoPagoContextService` to resolve `initPoint` for each response
  - Field order: `estado` moved before `importe`, new `initPoint` field added
- feat(reservaVacante): `FindReservaVacanteUseCaseImpl` enriches domain model with `Campanha` association and structured `jsonify()` logging

### Changed
- refactor(mercadopagoContext): Renamed `makeContext()` → `makeContextCuota()` in both `MercadoPagoCoreService` and `MercadoPagoCoreController` for semantic clarity
  - Updated `MercadoPagoCoreController.makeContext()` caller
  - Updated `GetDeudaPersonaUseCaseImpl` reference
- fix(chequeraCuota): Consolidated ISO 8601 date format pattern from `yyyy-MM-dd'T'HH:mm:ssZ` to `yyyy-MM-dd'T'HH:mm:ssXX` across all entities
  - `ChequeraCuotaEntity`: 3 `@JsonFormat` patterns updated
  - `MercadoPagoContext`: 4 `@JsonFormat` patterns updated (fechaVencimiento, lastVencimientoUpdated, fechaPago, fechaAcreditacion)
  - `MercadoPagoContextDto`: 1 `@JsonFormat` pattern updated
  - `ReservaVacante`, `ReservaVacanteEntity`, `ReservaVacanteResponse`: new `@JsonFormat` with correct pattern
- fix(mercadopagoContext): Corrected `isCuotaAvailable()` logic from `!=` to `==` comparisons for `pagado`, `baja`, `compensada` fields
- refactor(reservaVacante): Removed `@Transactional` from `CreateReservaVacanteUseCaseImpl`
- refactor(dto): `UMPreferenceMPDto` extended with `ReservaVacante` field alongside `ChequeraCuotaEntity`
- refactor(reservaVacante): `PreferenceClient.createPreference()` method signature simplified to single line
- chore(logging): Added `@Slf4j` and structured `jsonify()` logging in `ReservaVacanteDtoMapper`

> Basado en análisis profundo de `git diff HEAD` (21 archivos modificados, +166/-19 líneas) y `pom.xml` (versión actual 3.25.0).

## [3.25.0] - 2026-06-15
### Added
- feat(mercadoPagoContext): New `reservaVacanteId` (UUID) field and domain associations `ChequeraCuota`/`ReservaVacante`
  - `MercadoPagoContext.domain.model.MercadoPagoContext`: new fields `reservaVacanteId`, `chequeraCuota` (domain), `reservaVacante` (domain)
  - `MercadoPagoContextEntity`: new field `reservaVacanteId`, `@OneToOne` associations to `ChequeraCuotaEntity` and `ReservaVacanteEntity`
  - `MercadoPagoContextMapper`: bidirectional mapping of new fields, injection of `ChequeraCuotaMapper` and `ReservaVacanteMapper`
  - `MercadoPagoContextRequest`/`MercadoPagoContextResponse`: expose `reservaVacanteId` in API
  - `MercadoPagoContextDtoMapper`: mapping of `reservaVacanteId` and domain associations between DTOs and domain
- feat(chequeraCuota): New Java `ChequeraCuotaEntity` replacing Kotlin `ChequeraCuotaEntity.kt`
  - `ChequeraCuotaEntity.java` in `hexagonal/chequeraCuota/infrastructure/persistence/entity/` with Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Extends `Auditable`, maps to `chequera_cuota` table with `@Table` and `@UniqueConstraint`
  - `ChequeraCuotaMapper.toDomain()` updated to use the new Java entity instead of Kotlin entity
  - All references updated across 20+ files (services, controllers, facade services)

### Changed
- refactor(chequeraSerie): Moved `ChequeraSerie` domain model from `hexagonal/chequeraCuota/domain/model/` to correct module `hexagonal/chequeraSerie/domain/model/`
  - Package renamed from `um.tesoreria.core.hexagonal.chequeraCuota.domain.model` to `um.tesoreria.core.hexagonal.chequeraSerie.domain.model`
  - Updated imports in `ChequeraCuotaService`, `CalculateDeudaUseCase`, `GetDeudaPersonaUseCaseImpl`, `ChequeraSerieMapper`
  - Resolves cross-module domain model misplacement
- refactor(chequeraCuota): Replace all legacy Kotlin imports (`ChequeraCuotaEntity.kt`) with new Java entity imports across ~30 files
  - `ChequeraCuotaService`, `ChequeraPagoService`, `ChequeraSerieAltaFullService`, `ChequeraCuotaDeudaService`
  - `ChequeraService`, `FormulariosToPdfService`, `MailChequeraService`, `MercadoPagoCoreService`, `ProcessBajaService`, `SpoterService`
  - `ChequeraCuotaRepository`, `UMPreferenceMPDto`, `ClickPagosEntity`, `ChequeraCuotaDeuda.kt`, `ChequeraPago.kt`
- refactor(chequeraCuota): `ChequeraCuotaService.update()` migrated from `.Builder()` pattern to Lombok `.builder()` pattern

> Basado en análisis profundo de `git diff HEAD` (30+ archivos modificados/renombrados, +410/-230 líneas), `pom.xml` (versión 3.24.0 → 3.25.0) y cambios locales staged no commiteados.

## [3.24.0] - 2026-06-15
### Added
- feat(reservaVacante): Nuevo `FindReservaVacanteUseCase` con endpoint `GET /{reservaVacanteId}`
  - Nuevo puerto de entrada: `FindReservaVacanteUseCase` con método `findByReservaVacanteId(UUID)`
  - Nuevo caso de uso: `FindReservaVacanteUseCaseImpl` que busca reserva + persona + domicilio
  - Nuevo endpoint en `ReservaVacanteController`: `GET /api/tesoreria/core/umhub/reservaVacante/{reservaVacanteId}`
  - Integración con `PersonaService` y `DomicilioService` para enriquecer respuesta
- feat(reservaVacante): Nuevos campos `importe` (BigDecimal) y `vencimiento` (OffsetDateTime) en modelo de dominio `ReservaVacante`
  - `ReservaVacante.domain.model.ReservaVacante`: nuevos campos `importe` y `vencimiento`
  - `ReservaVacanteEntity`: persistencia de `importe` y `vencimiento`
  - `ReservaVacanteMapper`: mapeo bidireccional de `importe` y `vencimiento`
  - `ReservaVacanteResponse`: expone `importe` y `vencimiento` en respuestas REST
  - `CreateReservaVacanteUseCaseImpl`: importe obtenido de `Campanha.getValorReserva()`, vencimiento calculado como 2 días desde ahora (fin del día, UTC-3)
- feat(campanha): Nuevo campo `valorReserva` (BigDecimal) en modelo de dominio `Campanha`
  - `Campanha.domain.model.Campanha`: nuevo campo `valorReserva` para importe de reserva
  - `CampanhaEntity`: persistencia del campo `valorReserva`
  - `CampanhaMapper`: mapeo bidireccional de `valorReserva` entre entidad y dominio
  - `CampanhaRequest`/`CampanhaResponse`: exponen `valorReserva` en API REST
  - `CampanhaDtoMapper`: mapeo de `valorReserva` entre DTOs y dominio
  - `JpaCampanhaRepositoryAdapter.update()`: actualización parcial del campo `valorReserva`

### Changed
- refactor(domicilio): Migración completa del módulo Domicilio a arquitectura hexagonal
  - `Domicilio` (modelo Kotlin) reemplazado por `Domicilio` (modelo de dominio Java) en `hexagonal/domicilio/domain/model/`
  - `DomicilioService` movido de `core/service/` a `hexagonal/domicilio/application/service/`
  - Método `add(Domicilio, boolean)` renombrado a `create(Domicilio)` con firma simplificada
  - Método `update(Domicilio, Long, boolean)` cambiado a `update(Long, Domicilio)` (cambio de orden de parámetros)
  - `DomicilioException` movido de `core/exception/` a `hexagonal/domicilio/application/exception/`
  - Actualización de referencias en `MailChequeraService`, `NotificacionService`, `PayPerTicFileService`, `SincronizeService`, `SpoterService`
- refactor(persona): Migración del modelo `PersonaEntity` a modelo de dominio `Persona` en arquitectura hexagonal
  - `PersonaEntity` → `Persona` domain model en `hexagonal/persona/domain/model/`
  - Método `add(PersonaEntity)` renombrado a `create(Persona)` con retorno `Persona`
  - Actualización de referencias en `PayPerTicFileService`, `SpoterService`
- refactor(mercadoPagoContext): Migración completa del módulo MercadoPagoContext a arquitectura hexagonal
  - `MercadoPagoContext` movido de `core/model/` a `hexagonal/mercadoPagoContext/domain/model/`
  - `MercadoPagoContextService` movido de `core/service/` a `hexagonal/mercadoPagoContext/application/service/`
  - Actualización de referencias en `MercadoPagoCoreService`, `PagoService`
- refactor(chequeraCuota): Renombrado de `ChequeraCuota` (Kotlin) a `ChequeraCuotaEntity` (Java) en múltiples servicios
  - `ProcessBajaService`, `ChequeraCuotaDeudaService`, `ChequeraSerieAltaFullService`, `SpoterService` actualizados
- refactor(logging): Eliminación de métodos privados de logging con serialización Jackson manual
  - `MailChequeraService`: 7 métodos `log*()` eliminados, reemplazados por `jsonify()` inline
  - `SpoterService`: 8 métodos `log*()` eliminados, reemplazados por `Jsonifier` utilitaria
- refactor(payPerTic): Migración de `PayPerTicFileService` de inyección `@Autowired` a `@RequiredArgsConstructor`
  - Mejora de tipos: `Integer` → `int` en variables de bucle y contadores
  - Reemplazo de `e.printStackTrace()` por `log.error(e.getMessage())`
  - Simplificación de catch: unificación de excepciones `FileNotFoundException` + `IOException` → solo `IOException`
- refactor(sincronize): `SincronizeService` actualizado para usar nueva API de `DomicilioService` hexagonal
  - Cambio de `findFirstByPersonaId()` (retorno directo) a `orElse(new Domicilio())`
  - Cambio de `add(domicilio, true)` a `create(domicilio)` y `update(domicilio, domicilioId, true)` a `update(domicilioId, domicilio)`
- chore(banner): Simplificación del banner de texto de consola en `banner.txt`
- refactor(notificacion): `NotificacionService` actualizado para usar modelos de dominio hexagonales
  - `DomicilioException` importado desde nuevo paquete hexagonal
  - `Domicilio` importado desde modelo de dominio hexagonal
  - `DomicilioEntity` importado desde entidad JPA hexagonal
- chore(deps): Actualización de dependencias principales
  - `spring-boot-starter-log4j2`: 4.0.5 → 4.1.0
  - `mysql-connector-j`: 9.6.0 → 9.7.0
  - `guava`: 33.5.0-jre → 33.6.0-jre
  - `springdoc-openapi`: 3.0.2 → 3.0.3
  - `openpdf`: 3.0.3 → 3.0.5
  - Nueva propiedad `lombok.version`: 1.18.38
  - Eliminado `commons-lang3` de `dependencyManagement` (gestionado por Spring Boot parent POM)

> Basado en análisis profundo de `git diff HEAD` (archivos modificados en working tree), `git log` y `pom.xml` (versión 3.23.0 → 3.24.0).

## [3.23.0] - 2026-06-13
### Added
- feat(campanha): Nuevo campo `created` (LocalDateTime) en modelo de dominio `Campanha`
  - `Campanha.domain.model.Campanha`: nuevo campo `created` para fecha de creación
  - `CampanhaResponse`: expone el campo `created` en respuestas REST
  - `CampanhaMapper`: mapeo bidireccional del campo `created` entre entidad y dominio
  - `CampanhaDtoMapper`: mapeo del campo `created` de dominio a DTO de respuesta

### Changed
- refactor(campanha): Actualización parcial (partial update) en `JpaCampanhaRepositoryAdapter.update()`
  - Cambio de reemplazo completo de entidad a carga de entidad existente con actualización selectiva de campos no nulos
  - `CampanhaEntity.setCreated()` invocado explícitamente en `CampanhaMapper.toEntity()` para preservar el campo `created`

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +20/-8 líneas).

## [3.22.0] - 2026-06-13
### Added
- feat(campanha): Nuevo módulo Campanha con arquitectura hexagonal completa
  - Modelo de dominio: `Campanha` con campos `campanhaId` (UUID), `nombre`, `activa`
  - Puertos de entrada (5): `CreateCampanhaUseCase`, `GetCampanhaByIdUseCase`, `GetAllCampanhasUseCase`, `UpdateCampanhaUseCase`, `DeleteCampanhaUseCase`
  - Puerto de salida: `CampanhaRepository` con métodos CRUD completos
  - Casos de uso: Implementaciones completas en `application/usecases/` con inyección de dependencias
  - Servicio de aplicación: `CampanhaService` con delegación a casos de uso y retornos `Optional`
  - Adaptador JPA: `JpaCampanhaRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `CampanhaEntity` con herencia de `Auditable` y mapeo a tabla `campanha`
  - Repositorio JPA: `JpaCampanhaRepository` con consultas por identificación
  - Mapper: `CampanhaMapper` para conversión entidad ↔ dominio
  - Controlador REST: `CampanhaController` con endpoints CRUD bajo `GET/POST/PUT/DELETE /api/tesoreria/umhub/campanha/`
  - DTOs: `CampanhaRequest` y `CampanhaResponse`
  - DTO Mapper: `CampanhaDtoMapper` para conversión DTO ↔ dominio
  - Excepción: `CampanhaException` para manejo de errores del dominio

### Changed
- chore(deps): Actualización de Spring Boot de 4.0.6 a 4.1.0
- chore(deps): Actualización de Kotlin de 2.3.21 a 2.4.0
- chore(deps): Actualización de Spring Cloud de 2025.1.0 a 2025.1.2
- refactor(service): Migración de `ChequeraIncompletaService` de inyección `@Autowired` a `@RequiredArgsConstructor` con campo `final`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +488/-6 líneas) y cambios en `pom.xml`, `ChequeraIncompletaService`, y nuevo módulo `hexagonal/umhub/campanha/`.

## [3.21.2] - 2026-05-28
### Fixed
- fix(model): Add `@Serial` annotation to `serialVersionUID` in serializable model classes
  - `PersonaBeneficiario`, `ChequeraIncompleta`, `ChequeraKey`, `ChequeraPreuniv` now use `@Serial` for proper serialization metadata

### Changed
- refactor(model): Rename `personaEntity` field to `persona` in model classes for naming consistency
  - `PersonaBeneficiario`, `ChequeraIncompleta`, `ChequeraKey`, `ChequeraPreuniv`: field `personaEntity` → `persona`
  - `SheetService`: Updated references from `getPersonaEntity()` → `getPersona()` to match renamed field
  - Consistency with `ContratoEntity` naming convention established in v3.20.0

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +17/-9 líneas).

## [3.21.1] - 2026-05-21
### Fixed
- fix(tables): Add missing `@Table(name = ...)` annotations to `BajaEntity` and `ChequeraSerieEntity` for explicit table mapping
  - `BajaEntity` now has `@Table(name = "baja")` for consistent ORM mapping
  - `ChequeraSerieEntity` now has `@Table(name = "chequera_serie")` for explicit table reference
- fix(chequeraSerie): Exclude soft-deleted chequeras in `findLastPreuniversitario` query
  - Method changed from `findFirstBy...` (single result) to `findAllBy...` (list)
  - Filters out chequeras with associated baja records via `BajaService.findAllByChequeraIdIn()`
  - Prevents returning "dada de baja" chequeras as the last preuniversitario record

### Changed
- chore(deps): Remove explicit version pin for `commons-lang3` (managed by Spring Boot parent POM 4.0.6)

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +21/-10 líneas) y cambios en `pom.xml`, `BajaEntity`, `ChequeraSerieEntity`, `ChequeraSerieService`, `JpaChequeraSerieRepository`.

## [3.21.0] - 2026-05-21
### Changed
- refactor(chequeraSerie): Migración completa del módulo ChequeraSerie a arquitectura hexagonal con Java
  - Eliminación de `ChequeraSerie.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Creación de `ChequeraSerieEntity.java` en `hexagonal/chequeraSerie/infrastructure/persistence/entity/` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Creación de `JpaChequeraSerieRepository.java` en `hexagonal/chequeraSerie/infrastructure/persistence/repository/`
  - `ChequeraSerieService` movido de `core/service/` a `hexagonal/chequeraSerie/application/service/`
  - `ChequeraSerieController` movido de `core/controller/` a `hexagonal/chequeraSerie/infrastructure/web/controller/`
  - Eliminación de `ChequeraSerieRepository.java` legacy del paquete `core/repository/`
  - Actualización de referencias de `ChequeraSerie` a `ChequeraSerieEntity` en `PersonaService`, `ChequeraCuota.kt`, `ChequeraCuotaReemplazo.kt`, `ChequeraCuotaDeuda.kt`, `ChequeraCuotaService`, `ChequeraPagoService`, `LectivoService`, y servicios facade (`SheetService`, `ChequeraService`, `FormulariosToPdfService`, `MailChequeraService`, `PayPerTicFileService`, `ProcessBajaService`, `SincronizeService`, `SpoterService`, `TipoChequeraSedeService`)
  - Actualización de `ChequeraSerieMapper` para mapear desde `ChequeraSerieEntity`
  - Actualización de `Debito.java` para usar `ChequeraSerieEntity`
- refactor(baja): Migración completa del módulo Baja a arquitectura hexagonal con Java
  - Eliminación de `Baja.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Creación de `BajaEntity.java` en `hexagonal/baja/infrastructure/persistence/entity/` con anotaciones Lombok
  - Creación de `JpaBajaRepository.java` en `hexagonal/baja/infrastructure/persistence/repository/`
  - `BajaService` movido de `core/service/` a `hexagonal/baja/application/service/`
  - `BajaController` movido de `core/controller/` a `hexagonal/baja/infrastructure/web/controller/`
  - Eliminación de `BajaRepository.java` legacy del paquete `core/repository/`
  - Actualización de referencias en `ChequeraCuotaController`, `ProcessBajaController`, `ProcessBajaService`, `ChequeraService`

> Basado en análisis profundo de `git diff HEAD` (34 archivos modificados, +598/-554 líneas).

## [3.20.0] - 2026-05-20
### Changed
- refactor(contrato): Renombrado de campo `personaEntity` a `persona` en `ContratoEntity`
  - Campo de relación `@ManyToOne` renombrado de `personaEntity` a `persona` para consistencia de nomenclatura
  - Actualización de `SheetService` para usar `getPersona()` en lugar de `getPersonaEntity()` al generar hojas de cálculo de contratos
- refactor(chequeraSerie): Renombrado de campo `personaEntity` a `persona` en vistas `ChequeraSerieAlta` y `ChequeraSerieAltaFull`
  - Campo de relación renombrado en ambos modelos Kotlin para consistencia con `ContratoEntity`
  - `ChequeraSerieAltaFullService` actualizado con null-safe `Objects.requireNonNull(getPersona())` en logging

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +8/-7 líneas).

## [3.19.1] - 2026-05-18
### Fixed
- fix(chequeraCuota): Agregado filtro `baja=0` en consulta de cuotas filtradas para excluir registros soft-deleted
  - Nueva query `findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndImporte1GreaterThan` en `ChequeraCuotaRepository`
  - `ChequeraCuotaService.findAllCuotasFiltradas()` actualizado para usar la nueva query con `baja = (byte) 0`
  - La query anterior sin filtro `baja` podía retornar registros con soft-delete, causando resultados inconsistentes
  - Método anterior comentado y reemplazado por nueva implementación con filtro de baja

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +12/-2 líneas).

## [3.19.0] - 2026-05-13
### Added
- feat(contrato): Nuevo módulo Contrato con arquitectura hexagonal completa
  - Modelo de dominio: `Contrato` con campos `contratoId`, `personaId`, `documentoId`, `desde`, `facultadId`, `planId`, `materiaId`, `geograficaId`, `cargoMateriaId`, `primerVencimiento`, `cargo`, `montoFijo`, `canonMensual`, `canonMensualSinAjuste`, `hasta`, `canonMensualLetras`, `canonTotal`, `canonTotalLetras`, `meses`, `mesesLetras`, `ajuste`, y método `jsonify()`
  - Puertos de entrada (9): `CreateContratoUseCase`, `DeleteContratoUseCase`, `GetAllContratosAjustablesUseCase`, `GetAllContratosByFacultadUseCase`, `GetAllContratosByPersonaUseCase`, `GetAllContratosVigentesUseCase`, `GetContratoByIdUseCase`, `SaveAllContratosUseCase`, `UpdateContratoUseCase`
  - Puerto de salida: `ContratoRepository` con métodos `create`, `findById`, `findAllByFacultad`, `findAllAjustable`, `findAllVigente`, `findAllByPersona`, `update`, `saveAll`, `deleteById`
  - Casos de uso: Implementaciones completas en `application/usecases/` con inyección de dependencias
  - Servicio de aplicación: `ContratoService` con delegación a casos de uso y retornos `Optional`
  - Adaptador JPA: `JpaContratoRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `ContratoEntity` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`), herencia de `Auditable`, y `@Builder.Default` para campos con valores por defecto
  - Repositorio JPA: `JpaContratoRepository` con consultas por facultad, persona, ajustable, vigente
  - Mapper: `ContratoMapper` para conversión entidad ↔ dominio
  - Controlador REST: `ContratoController` con endpoints `GET /contrato/ajustable/{referencia}`, `GET /contrato/vigente/{referencia}`, `GET /contrato/persona/{personaId}/{documentoId}`, `GET /contrato/{contratoId}`, `PUT /contrato/{contratoId}`, `PUT /contrato/`
  - DTOs: `ContratoRequest` para entrada y `ContratoResponse` para respuestas HTTP
  - DTO Mapper: `ContratoDtoMapper` para conversión DTO ↔ dominio

### Changed
- refactor(contrato): Migración completa de módulo Contrato a arquitectura hexagonal
  - Eliminación de `Contrato.java` (modelo legacy) del paquete `core/model/` → renombrado a `ContratoEntity.java` en `hexagonal/contrato/infrastructure/persistence/entity/`
  - Eliminación de `ContratoRepository.java` del paquete `core/repository/`
  - Eliminación de `ContratoService.java` del paquete `core/service/`
  - Eliminación de `ContratoController.java` del paquete `core/controller/`
  - Actualización de referencias en `ContratoPeriodo.java`, `ContratoFacturaService.java`, `ContratoToolService.java`, `SheetService.java`, `CursoCargoContratadoResponse.java`, `CursoCargoContratadoDtoMapper.java` para usar la nueva estructura hexagonal
  - `ContratoToolService` y `ContratoFacturaService` actualizados para usar `ContratoService` hexagonal con `Optional` y manejo de `ContratoException`
  - `SheetService`: líneas comentadas de datos de persona reactivadas usando `contratoPeriodo.getContrato().getPersonaEntity()`
- refactor(contrato): Migración de `ContratoEntity` de `@Data` a `@Getter`/`@Setter`/`@Builder` para consistencia con otros módulos hexagonales

> Basado en análisis profundo de `git diff HEAD` (39 archivos, +793/-229 líneas).

## [3.18.0] - 2026-05-09
### Added
- feat(facultad): Nuevo módulo Facultad con arquitectura hexagonal completa
  - Modelo de dominio: `Facultad` con campos `facultadId`, `nombre`, `codigoempresa`, `server`, `dbadm`, `dsn`, `cuentacontable`, `apiserver`, `apiport`, `fechaAuditoria`, `usuarioAuditoria`
  - Puertos de entrada: `GetAllFacultadesUseCase`, `GetFacultadByIdUseCase`, `GetFacultadesFiltradasUseCase`
  - Puerto de salida: `FacultadRepository` con métodos `findAll()`, `findAllIn(List<Integer>)`, `findById(Integer)`
  - Casos de uso: `GetAllFacultadesUseCaseImpl`, `GetFacultadByIdUseCaseImpl`, `GetFacultadesFiltradasUseCaseImpl`
  - Servicio de aplicación: `FacultadService` con métodos `findAll()`, `findFacultades()`, `findByFacultadId(Integer)`, y métodos legacy view `findAllByLectivoId`, `findAllByPersona`, `findAllByDisenho`
  - Adaptador JPA: `JpaFacultadRepositoryAdapter` con mapeo dominio ↔ entidad y orden por `facultadId`
  - Entidad JPA: `FacultadEntity` con anotaciones Lombok, herencia de `Auditable`, y mapeo a tabla `facultad`
  - Repositorio JPA: `JpaFacultadRepository` con métodos `findAllByFacultadIdIn(List<Integer>)` y `findByFacultadId(Integer)`
  - Mapper: `FacultadMapper` para conversión entidad → dominio
  - Controlador REST: `FacultadController` con endpoints `GET /facultad/`, `GET /facultad/facultades`, `GET /facultad/{facultadId}`, y legacy view endpoints `GET /facultad/lectivo/{lectivoId}`, `GET /facultad/bypersona/{personaId}/{documentoId}/{lectivoId}`, `GET /facultad/disenho/{lectivoId}/{geograficaId}`
  - DTO: `FacultadResponse` para respuestas HTTP con datos completos de facultad
  - DTO Mapper: `FacultadDtoMapper` para conversión dominio → DTO

### Changed
- refactor(facultad): Migración completa de módulo Facultad a arquitectura hexagonal
  - Eliminación de `Facultad.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `FacultadRepository.java` del paquete `core/repository/`
  - Eliminación de `FacultadService.java` del paquete `core/service/`
  - Eliminación de `FacultadController.java` del paquete `core/controller/`
  - Actualización de referencias de `um.tesoreria.core.kotlin.model.Facultad` a `um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity` en todos los modelos, servicios y controladores que dependían de la entidad legacy (~20+ archivos afectados)

> Basado en análisis profundo de `git diff HEAD` (archivos creados, eliminados y modificados, incluyendo cambios locales no commiteados).

## [3.17.0] - 2026-05-08
### Added
- feat(dependencia): Nuevo módulo Dependencia con arquitectura hexagonal completa
  - Modelo de dominio: `Dependencia` con campos `dependenciaId`, `nombre`, `acronimo`, `facultadId`, `geograficaId`, `cuentaHonorariosPagar`, `fechaAuditoria`, `usuarioAuditoria`, relaciones a `Facultad`, `Geografica`, `Cuenta`, y método `getSedeKey()`
  - Puertos de entrada: `GetAllDependenciasUseCase`, `GetDependenciaByIdUseCase`, `UpdateDependenciaUseCase`
  - Puerto de salida: `DependenciaRepository` con métodos `findAll()`, `findById(Integer)`, `save(Dependencia)`
  - Casos de uso: `GetAllDependenciasUseCaseImpl`, `GetDependenciaByIdUseCaseImpl`, `UpdateDependenciaUseCaseImpl`
  - Servicio de aplicación: `DependenciaService` con métodos `findAll()`, `findByDependenciaId(Integer)`, `update(Integer, Dependencia)`
  - Adaptador JPA: `JpaDependenciaRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `DependenciaEntity` con anotaciones Lombok, relaciones `@OneToOne` a `Facultad`, `GeograficaEntity`, `CuentaEntity`, y herencia de `Auditable`
  - Repositorio JPA: `JpaDependenciaRepository` con métodos `findAllByOrderByNombre()` y `findByDependenciaId(Integer)`
  - Mapper: `DependenciaMapper` para conversión dominio ↔ entidad con inyección de `GeograficaMapper` y `CuentaMapper`
  - Controlador REST: `DependenciaController` con endpoints `GET /dependencia/`, `GET /dependencia/{dependenciaId}`, `PUT /dependencia/{dependenciaId}`
  - DTOs: `DependenciaRequest` para entrada y `DependenciaResponse` para respuestas HTTP con datos completos de relaciones
  - DTO Mapper: `DependenciaDtoMapper` para conversión entre DTOs y modelo de dominio
  - Refactorización de `UbicacionEntity` para usar nueva estructura

### Changed
- refactor(dependencia): Migración completa de módulo Dependencia a arquitectura hexagonal
  - Eliminación de `Dependencia.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `DependenciaRepository.java` del paquete `core/repository/`
  - Eliminación de `DependenciaService.java` del paquete `core/service/`
  - Eliminación de `DependenciaController.java` del paquete `core/controller/`

### Fixed
- fix(proveedor): Manejo de campos nulos en creación y actualización de proveedores
  - Validación de `emailInterno` en `CreateProveedorUseCaseImpl.createProveedor()` con asignación a string vacío si es nulo
  - Validación de `emailInterno` en `UpdateProveedorUseCaseImpl.updateProveedor()` con asignación a string vacío si es nulo
  - Validación de `habilitado` en `CreateProveedorUseCaseImpl.createProveedor()` con asignación a `(byte) 0` si es nulo
  - Validación de `habilitado` en `UpdateProveedorUseCaseImpl.updateProveedor()` con asignación a `(byte) 0` si es nulo

### Changed
- refactor(proveedor): Migración de `ProveedorRequest` de `@Data` a `@Getter`/`@Setter`/`@Builder` para consistencia
  - Nuevas anotaciones: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` reemplazando `@Data`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +421/-197 líneas, incluyendo cambios locales no commiteados).

## [3.16.0] - 2026-05-07
### Added
- feat(articulo): Enriquecimiento de respuestas con objeto `Cuenta` vía `CuentaService`
  - Nuevo campo `Cuenta cuenta` en `ArticuloSearchResponse` para incluir datos de cuenta asociada
  - Nuevo campo `articuloId` en `ArticuloRequest` para especificar ID en creación
  - Los endpoints `GET /articulo/{id}`, `POST /articulo/search` y `GET /articulo/page` ahora resuelven y adjuntan el objeto `Cuenta` completo cuando `numeroCuenta` está presente

### Changed
- refactor(articulo): Cambio de `@Data` a `@Getter`/`@Setter` en `Articulo` para mayor control de mutabilidad
- refactor(articulo): Renombrado de campo `cuenta` a `numeroCuenta` en `ArticuloSearch`, `ArticuloKey`, `ArticuloSearchResponse` y mappers para claridad semántica
  - Compatibilidad BD asegurada vía `@Column(name = "cuenta")` en `ArticuloKey`
- refactor(articulo): Migración de `@Data` a `@Getter`/`@Setter` + `@Serial` en `ArticuloKey`
- refactor(dto): Uso de importación `OffsetDateTime` en lugar de FQCN en `ArticuloSearch` y `ArticuloSearchResponse`

> Basado en análisis profundo de `git diff HEAD` (8 archivos modificados, +72/-27 líneas).

## [3.15.0] - 2026-05-07
### Added
- feat(facturaPendiente): Nuevo módulo FacturaPendiente con arquitectura hexagonal completa
  - Modelo de dominio: `FacturaPendiente` con campos `proveedorMovimientoId`, `razonSocial`, `cuit`, `cbu`, `fechaComprobante`, `fechaVencimiento`, `observaciones`, `comprobante`, `debita`, `prefijo`, `numeroComprobante`, `importeFactura`, `importePagado`
  - Puerto de entrada: `GetFacturasPendientesUseCase` con método `getFacturasPendientes(OffsetDateTime, OffsetDateTime)`
  - Puerto de salida: `FacturaPendienteRepository` con métodos `updateFechaPagoInProveedorPago()`, `findFacturasPendientes(OffsetDateTime, OffsetDateTime)`
  - Caso de uso: `GetFacturasPendientesUseCaseImpl` con lógica de negocio
  - Servicio de aplicación: `FacturaPendienteService` con método `findAllFacturasPendientesBetweenDates`
  - Adaptador JPA: `JpaFacturaPendienteRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `FacturaPendienteEntity` con anotaciones Lombok e `@Immutable`
  - Repositorio JPA: `JpaFacturaPendienteRepository` con consultas nativas SQL
- feat(sheet): Actualización de `SheetService` para nuevos campos en hojas de cálculo
  - Agregado campo `cbu` en generación de hojas de facturas pendientes
  - Agregado campo `fechaVencimiento` con formato UTC
  - Agregado campo `observaciones` (concepto de factura)

### Changed
- refactor(facturaPendiente): Migración completa a arquitectura hexagonal
  - Eliminación de `FacturaPendiente.kt` (Kotlin) de `core/kotlin/model/view/`
  - Eliminación de `FacturaPendienteService.java` de `core/service/view/`
  - Migración de `FacturaPendienteRepository.kt` a Java en `hexagonal/facturaPendiente/infrastructure/persistence/repository/`
  - Actualización de `SheetService` para usar nuevo servicio hexagonal `FacturaPendienteService`
  - Migración de uso de `jsonify()` a utilitaria `Jsonifier` en `SheetService`

### Removed
- Eliminación de tests obsoletos `ChequeraCuotaControllerTest.java`, `ProveedorMovimientoControllerTest.java`

> Basado en análisis profundo de `git diff HEAD` (13 archivos modificados, +120/-156 líneas).

## [3.14.0] - 2026-05-06

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.14.0] - 2026-05-06
### Added
- feat(ubicacion): Nuevo módulo Ubicacion con arquitectura hexagonal
  - Modelo de dominio: `Ubicacion` con campos `ubicacionId`, `nombre`, `dependenciaId`, `geograficaId`
  - Puertos de entrada: `GetAllUbicacionesUseCase`, `GetUbicacionesBySedeUseCase`
  - Puerto de salida: `UbicacionRepository` con métodos `findAll()`, `findAllByGeograficaId(Integer)`
  - Casos de uso: `GetAllUbicacionesUseCaseImpl`, `GetUbicacionesBySedeUseCaseImpl`
  - Servicio de aplicación: `UbicacionService` con métodos `findAll()`, `findAllBySede(Integer)`
  - Adaptador JPA: `JpaUbicacionRepositoryAdapter` con mapeo a entidad JPA
  - Mapper: `UbicacionMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `UbicacionEntity` con anotaciones Lombok y relación a `DependenciaEntity`
  - Controlador REST: `UbicacionController` con endpoints GET
  - DTO: `UbicacionResponse` para respuestas HTTP
- feat(ubicacionArticulo): Nuevo módulo UbicacionArticulo con arquitectura hexagonal
  - Modelo de dominio: `UbicacionArticulo` con relaciones a `Ubicacion`, `Articulo`, `Cuenta`
  - Puertos de entrada: `GetAllUbicacionArticulosUseCase`, `GetUbicacionArticuloUseCase`, `GetUbicacionArticulosByArticuloUseCase`, `SaveUbicacionArticuloUseCase`
  - Puerto de salida: `UbicacionArticuloRepository` con métodos CRUD y búsquedas
  - Casos de uso: `GetAllUbicacionArticulosUseCaseImpl`, `GetUbicacionArticuloUseCaseImpl`, `GetUbicacionArticulosByArticuloUseCaseImpl`, `SaveUbicacionArticuloUseCaseImpl`
  - Servicio de aplicación: `UbicacionArticuloService` con métodos `findAll()`, `save()`, `findAllByArticuloId()`, `getByUbicacionAndArticulo()`
  - Adaptador JPA: `JpaUbicacionArticuloRepositoryAdapter` con lógica de upsert usando `findByUbicacionIdAndArticuloId`
  - Mapper: `UbicacionArticuloMapper` con mapeo de relaciones anidadas
  - Entidad JPA: `UbicacionArticuloEntity` con restricción única `(ubicacionId, articuloId)`
  - Controlador REST: `UbicacionArticuloController` con endpoints CRUD
  - DTOs: `UbicacionArticuloRequest`, `UbicacionArticuloResponse`

### Changed
- refactor(core): `SheetService` actualizado para usar nuevos modelos de dominio
- refactor(model): `Entrega.kt` actualizado con cambios en modelo
- refactor(model): `FacturaPendiente.kt` actualizado con nuevos campos
- refactor(dto): `AsignacionCostoDto` y `CostoParameterDto` actualizados para usar modelos de dominio
- refactor(legacy): Eliminación de controladores legacy `UbicacionController.java` y `UbicacionArticuloController.java` del paquete `core/controller/`
- refactor(legacy): Eliminación de servicios legacy `UbicacionService.java` y `UbicacionArticuloService.java` del paquete `core/service/`
- refactor(legacy): Eliminación de repositorios legacy `UbicacionRepository.java` y `UbicacionArticuloRepository.java` del paquete `core/repository/`

> Basado en análisis profundo de `git diff HEAD` (47 archivos modificados, +723/-246 líneas).

## [3.13.0] - 2026-05-05
### Added
- feat(articulo): Completitud de migración hexagonal y nuevas funcionalidades (commit afbeb02)
  - Nuevo puerto de entrada: `GetPaginatedArticulosUseCase` con método `getPaginated(int page, int size)`
  - Nuevo caso de uso: `GetPaginatedArticulosUseCaseImpl` con implementación de paginación usando `Pageable`
  - Nuevo caso de uso: `SearchArticulosUseCaseImpl` para búsqueda de artículos por criterio
  - Nuevo modelo: `ArticuloSearch` para criterios de búsqueda con campo `search`
  - Nuevos métodos en `ArticuloRepository`: `findPaginated(Pageable)`, `search(String criterio)`
  - Nuevo endpoint en `ArticuloController`: `GET /articulo/page?page=X&size=Y` que retorna `PaginatedResponse<ArticuloResponse>`
  - Nuevo endpoint en `ArticuloController`: `GET /articulo/search?criterio=X`
  - Nuevo DTO: `ArticuloSearchResponse` para respuestas de búsqueda
- feat(proveedor): Mejoras en módulo Proveedor
  - Actualización de `ProveedorSearch` con nuevos campos para búsquedas avanzadas
  - Nuevos endpoints en `ProveedorController` para búsquedas

### Changed
- refactor(articulo): Reorganización de paquetes
  - `ArticuloKey.java`, `ArticuloKeyRepository.java`, `ArticuloKeyRepositoryCustom.java` movidos de `core/` a `hexagonal/articulo/infrastructure/persistence/repository/`
  - Mappers actualizados para soportar nuevos modelos

### Removed
- `ArticuloKeyService.java` eliminado (funcionalidad movida a casos de uso específicos)

> Basado en análisis profundo de `git diff HEAD` (24 archivos modificados, +160/-45 líneas) y commits afbeb02, 4b9e84e.

## [3.12.0] - 2026-05-04
### Added
- feat(articulo): Completitud de migración de módulo Artículo a arquitectura hexagonal
  - Nuevos modelos de dominio: `Articulo` y `ArticuloSearch` con Lombok
  - Puertos de entrada: `CreateArticuloUseCase`, `DeleteArticuloUseCase`, `GetAllArticulosUseCase`, `GetArticuloByIdUseCase`, `GetNewArticuloUseCase`, `SearchArticulosUseCase`, `UpdateArticuloUseCase`
  - Puerto de salida: `ArticuloRepository` con métodos CRUD completos
  - Casos de uso: Implementaciones completas en `application/usecases/`
  - Servicio de aplicación: `ArticuloService` refactorizado con inyección de casos de uso
  - Adaptador JPA: `JpaArticuloRepositoryAdapter` con implementación completa
  - Mappers: `ArticuloMapper` (dominio ↔ JPA) y `ArticuloDtoMapper` (dominio ↔ DTO)
  - DTOs: `ArticuloRequest`, `ArticuloResponse`, `ArticuloSearchResponse`
  - Controlador REST: `ArticuloController` migrado con endpoints CRUD y manejo de `ResponseEntity`
- refactor(core): `CostoParameterDto` y `CostoParameterService` actualizados para usar modelo de dominio `Articulo`

### Changed
- refactor(articulo): Reestructuración de paquetes a arquitectura hexagonal
- `JpaArticuloRepository` simplificado a solo consultas específicas
- `ArticuloController` migrado a DTOs y `ArticuloDtoMapper`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados).

## [3.11.0] - 2026-05-03
### Added
- feat(articulo): Migración de módulo Artículo a arquitectura hexagonal
  - Nueva entidad JPA: `ArticuloEntity` en `hexagonal/articulo/infrastructure/persistence/entity/` con anotaciones Lombok
  - Nuevo repositorio: `JpaArticuloRepository` con métodos `findByArticuloId`, `findTopByOrderByArticuloIdDesc`
  - Nuevo servicio de aplicación: `ArticuloService` en `hexagonal/articulo/application/service/`
  - Nuevo controlador REST: `ArticuloController` en `hexagonal/articulo/infrastructure/web/controller/` con `@RequiredArgsConstructor`
  - Relación `@OneToOne` con `CuentaEntity` en `ArticuloEntity`

### Changed
- refactor(articulo): Migración completa de módulo Artículo a arquitectura hexagonal
  - Eliminación de `Articulo.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `ArticuloRepository.java` del paquete `core/repository/`
  - Eliminación de `ArticuloService.java` del paquete `core/service/`
  - Eliminación de `ArticuloController.java` del paquete `core/controller/`
  - Actualización de referencias en `EntregaDetalle.kt`, `ProveedorArticulo.kt`, `UbicacionArticulo.java`, `AsignacionCostoDto.java`, `CostoParameterDto.java`, `CostoParameterService.java`
- fix(auth): Corrección de espacio extra en `ResponseStatusException` en `AuthController`

> Basado en análisis profundo de `git diff HEAD` (14 archivos modificados, +156/-98 líneas).

## [3.10.0] - 2026-05-03
### Added
- feat(proveedor): Mejora de modelo de datos y refactorización de DTOs en módulo Proveedor
  - Nuevo campo `cbu` en `ProveedorSearch`, `ProveedorSearchEntity`, `ProveedorResponse` y `ProveedorSearchResponse`
  - Nuevo campo `Cuenta cuenta` (objeto dominio) en `ProveedorResponse` y `ProveedorSearchResponse` para incluir datos de cuenta asociada
  - Renombrado de campo `cuenta` a `numeroCuenta` en `ProveedorSearch`, `ProveedorSearchEntity` y `ProveedorSearchResponse`

### Changed
- refactor(proveedor): Refactorización de DTOs para usar patrón Builder de Lombok
  - `ProveedorResponse` y `ProveedorSearchResponse` ahora usan `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
  - `ProveedorDtoMapper.toResponse()` y `toSearchResponse()` refactorizados para usar `builder()` en lugar de setters manuales
  - Cambio de `@Data` a `@Getter`/`@Setter` en `ProveedorSearchEntity`, `ProveedorResponse` y `ProveedorSearchResponse`
  - `ProveedorMapper` actualizado para mapear nuevos campos (`numeroCuenta`, `cbu`)

> Basado en análisis profundo de `git diff HEAD` (6 archivos modificados, +67/-44 líneas).

## [3.9.0] - 2026-05-02
### Added
- feat(proveedor): Implementación de paginación en módulo Proveedor
  - Nuevo puerto de entrada: `GetPaginatedProveedoresUseCase` con método `getPaginatedProveedores(int page, int size)`
  - Nuevo modelo genérico: `PaginatedResponse<T>` para respuestas paginadas (data, totalElements, totalPages, currentPage, pageSize)
  - Nuevo caso de uso: `GetPaginatedProveedoresUseCaseImpl` con implementación de paginación
  - Nuevo método en puerto de salida `ProveedorRepository`: `findAll(Pageable pageable)`
  - Actualización de `JpaProveedorRepositoryAdapter` con soporte para paginación vía `Page<ProveedorEntity>`
  - Nuevo endpoint en `ProveedorController`: `GET /proveedor/page?page=X&size=Y` que retorna `PaginatedResponse<ProveedorResponse>`
  - Actualización de `ProveedorService` con método `getPaginated(int page, int size)`

> Basado en análisis profundo de `git diff HEAD` (7 archivos modificados, +101 líneas).

## [3.8.0] - 2026-05-02
### Added
- feat(cuenta): Implementación completa de casos de uso CRUD para módulo Cuenta
  - Nuevos casos de uso: `CreateCuentaUseCase`, `DeleteCuentaUseCase`, `GetAllCuentasUseCase`, `GetCuentaByCuentaContableIdUseCase`, `GetCuentaByNumeroCuentaUseCase`, `RecalculaGradosUseCase`, `SaveAllCuentasUseCase`, `SearchCuentasUseCase`, `UpdateCuentaUseCase`
  - Nuevos DTOs: `CuentaRequest`, `CuentaResponse`, `CuentaSearchResponse`
  - Nuevo mapper: `CuentaDtoMapper` para conversión dominio ↔ DTO
  - Controlador `CuentaController` actualizado con todos los endpoints REST
- feat(proveedor): Mejora de módulo Proveedor con búsqueda avanzada
  - Nueva entidad `ProveedorSearchEntity` para búsquedas
  - Nuevo repositorio `JpaProveedorSearchRepository` con consultas personalizadas
  - Refactorización de paquetes: adaptador movido a `infrastructure/persistence/adapter/`

### Changed
- refactor(service): Actualización de `BalanceService`, `ContabilidadService`, `SheetService` para usar nuevas estructuras hexagonales
- refactor(proveedor): Reestructuración de paquetes de Proveedor (adaptador movido a `infrastructure/persistence/adapter/`)
- refactor(model): Actualización de `Ejercicio.kt` con cambios en modelo

> Basado en análisis profundo de `git diff HEAD` (47 archivos modificados, +894/-447 líneas).

## [3.7.0] - 2026-05-02
### Added
- feat(hexagonal): Nuevo módulo Cuenta con arquitectura hexagonal
  - Entidad JPA: `CuentaEntity` en `hexagonal/cuenta/infrastructure/persistence/entity/` con anotaciones Lombok
  - Repositorio: `CuentaRepository` en `hexagonal/cuenta/infrastructure/persistence/repository/` con métodos de consulta personalizados
  - Servicio de aplicación: `CuentaService` en `hexagonal/cuenta/application/service/` con lógica de negocio
  - Controlador REST: `CuentaController` en `hexagonal/cuenta/infrastructure/web/controller/` con endpoints REST
  - Métodos de consulta: `findAllByGradoAndNumeroCuentaGreaterThan`, `findAllByNumeroCuentaIn`, `findAllByGradoAndNumeroCuentaBetween`
  - Métodos de búsqueda: `findByNumeroCuenta`, `findByCuentaContableId`
  - Operaciones CRUD: `add`, `update`, `delete`, `saveAll`
  - Utilidad: `recalculaGrados()` para recalcular grados de cuentas
  - Integración con `CuentaSearchService` para búsquedas avanzadas

### Changed
- refactor(cuenta): Migración de módulo Cuenta a arquitectura hexagonal
  - Eliminación de `Cuenta.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `CuentaRepository.java` del paquete `core/repository/`
  - Eliminación de `CuentaService.java` del paquete `core/service/`
  - Eliminación de `CuentaController.java` del paquete `core/controller/`
  - Creación de estructura hexagonal en `hexagonal/cuenta/` con capas domain, application, infrastructure
  - Actualización de `BalanceService`, `CompraService`, `ContabilidadService` para usar nueva estructura
  - Migración de modelos Kotlin a Java: `Articulo.kt`, `Bancaria.kt`, `BancoMovimiento.kt`, `CuentaMovimiento.kt`, `Dependencia.kt`, `Setup.kt`, `Valor.kt`
  - Actualización de `UbicacionArticulo.java` y `CuentaMovimientoAsiento.java` para usar nuevas entidades
  - `CuentaEntity` con relación `@OneToOne` a `GeograficaEntity`
  - Uso de `Auditable` como clase base para auditoría

> Basado en análisis profundo de `git diff HEAD` (19 archivos modificados, +161/-146 líneas).

## [3.6.0] - 2026-05-02
### Added
- feat(hexagonal): Nuevo módulo Auth con arquitectura hexagonal para autenticación
  - Modelo de dominio: `UsuarioAuth` con validación de login/password (SHA-256)
  - Caso de uso: `LoginUseCaseImpl` con lógica de autenticación robusta y manejo de mayúsculas/minúsculas
  - Servicio de aplicación: `AuthService` como fachada del dominio
  - Adaptador JPA: `JpaUsuarioAuthRepositoryAdapter` con `UsuarioAuthMapper`
  - Controlador REST: `AuthController` con endpoint de login
  - DTOs: `LoginRequest` y `LoginResponse` para entrada/salida
- feat(hexagonal): Nuevo módulo Geografica con arquitectura hexagonal
  - Modelo de dominio: `Geografica` para entidades geográficas
  - Casos de uso: `GetAllGeograficasUseCase`, `GetGeograficaByIdUseCase`, `GetGeograficasBySedeUseCase`
  - Servicio de aplicación: `GeograficaService` con integración a `GeograficaLectivoService`
  - Adaptador JPA: `JpaGeograficaRepositoryAdapter` con `GeograficaEntity`
  - Controlador REST: `GeograficaController` migrado a arquitectura hexagonal
  - DTO: `GeograficaResponse` para respuestas HTTP
- feat(hexagonal): Implementación completa del módulo Proveedor con arquitectura hexagonal
  - Modelos de dominio: `Proveedor` y `ProveedorSearch` con Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Puertos de entrada (Input Ports): `CreateProveedorUseCase`, `DeleteProveedorUseCase`, `GetAllProveedoresUseCase`, `GetLastProveedorUseCase`, `GetProveedorByCuitUseCase`, `GetProveedorByIdUseCase`, `SearchProveedoresUseCase`, `UpdateProveedorUseCase`
  - Puerto de salida (Output Port): `ProveedorRepository` con métodos `create`, `findByProveedorId`, `findByCuit`, `findLast`, `findAll`, `findAllByStrings`, `update`, `deleteById`
  - Casos de uso (Use Cases): Implementaciones completas en `application/usecases/` con inyección de dependencias
  - Servicio de aplicación: `ProveedorService` refactorizado para usar casos de uso con `Optional<Proveedor>` en retornos
  - Adaptador JPA: `JpaProveedorRepositoryAdapter` con implementación completa de `ProveedorRepository`
  - Mapper de persistencia: `ProveedorMapper` para conversión entre `Proveedor`/`ProveedorSearch` y entidades JPA
  - Mapper de DTO: `ProveedorDtoMapper` para conversión entre dominio y DTOs
  - DTOs: `ProveedorRequest`, `ProveedorResponse`, `ProveedorSearchResponse` para entrada/salida
  - Controlador REST: `ProveedorController` migrado completamente con manejo de `ResponseEntity<ProveedorResponse>` y `Optional`

### Changed
- refactor(model): Migración de `Geografica.kt` (Kotlin) a `GeograficaEntity.java`
  - Eliminación de modelo Kotlin en paquete `core/kotlin/model/`
  - Creación de entidad JPA en `hexagonal/geografica/infrastructure/persistence/entity/`
  - Actualización de `CursoHaberes.java` para usar `GeograficaEntity` en lugar de `Geografica`
- refactor(proveedor): Reestructuración completa de paquetes de Proveedor a arquitectura hexagonal
  - Migración de servicios y repositorios al paquete `hexagonal/proveedor/`
  - Nuevos modelos de dominio (`Proveedor`, `ProveedorSearch`) reemplazando `ProveedorEntity` en capa de dominio
  - `ProveedorService` refactorizado: retornos cambiados a `Optional<Proveedor>`, uso de casos de uso
  - `ProveedorController` refactorizado: uso de DTOs (`ProveedorRequest`, `ProveedorResponse`, `ProveedorSearchResponse`) y `ProveedorDtoMapper`
  - `JpaProveedorRepository` simplificado: solo interfaces de consulta, lógica movida a `JpaProveedorRepositoryAdapter`
  - Mappers agregados: `ProveedorMapper` (dominio ↔ JPA), `ProveedorDtoMapper` (dominio ↔ DTO)
  - Servicios externos (`NotificacionService`, `SheetService`) actualizados para usar nueva estructura

> Basado en análisis profundo de `git diff HEAD` (30 archivos modificados, +699/-165 líneas).