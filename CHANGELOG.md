# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

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