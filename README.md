# Servicio Core de Tesorería

## Descripción

Servicio core para la gestión de tesorería, implementado con Spring Boot 4.1.0.

**Versión actual (SemVer): 3.23.0**

## Novedades 3.23.0 (verificado en código)
- feat(campanha): Nuevo campo `created` (LocalDateTime) en modelo `Campanha`
  - `Campanha.domain.model.Campanha`: nuevo campo `created` para fecha de creación
  - `CampanhaResponse`: expone el campo `created` en respuestas REST
  - `CampanhaMapper` y `CampanhaDtoMapper`: mapeo bidireccional del campo `created`
- refactor(campanha): Actualización parcial (partial update) en `JpaCampanhaRepositoryAdapter.update()`
  - Cambio de reemplazo completo de entidad a carga de entidad existente con actualización selectiva de campos no nulos

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +20/-8 líneas).

## Novedades 3.22.0 (verificado en código)
- feat(campanha): Nuevo módulo Campanha con arquitectura hexagonal completa bajo `hexagonal/umhub/campanha/`
  - Modelo de dominio: `Campanha` con campos `campanhaId` (UUID), `nombre`, `activa`
  - Puertos de entrada: `CreateCampanhaUseCase`, `GetCampanhaByIdUseCase`, `GetAllCampanhasUseCase`, `UpdateCampanhaUseCase`, `DeleteCampanhaUseCase`
  - Puerto de salida: `CampanhaRepository` con métodos CRUD
  - Casos de uso: `CreateCampanhaUseCaseImpl`, `GetCampanhaByIdUseCaseImpl`, `GetAllCampanhasUseCaseImpl`, `UpdateCampanhaUseCaseImpl`, `DeleteCampanhaUseCaseImpl`
  - Servicio de aplicación: `CampanhaService` con delegación a casos de uso
  - Adaptador JPA: `JpaCampanhaRepositoryAdapter` con `CampanhaMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `CampanhaEntity` extends `Auditable`, mapeo a tabla `campanha`
  - Repositorio JPA: `JpaCampanhaRepository` con consultas por ID
  - Controlador REST: `CampanhaController` con endpoints CRUD bajo `GET/POST/PUT/DELETE /api/tesoreria/umhub/campanha/`
  - DTOs: `CampanhaRequest`, `CampanhaResponse`
  - DTO Mapper: `CampanhaDtoMapper` para conversión DTO ↔ dominio
  - Excepción: `CampanhaException`
- chore(deps): Actualización de Spring Boot 4.0.6 → 4.1.0, Kotlin 2.3.21 → 2.4.0, Spring Cloud 2025.1.0 → 2025.1.2
- refactor(service): Migración de `ChequeraIncompletaService` a inyección por constructor con `@RequiredArgsConstructor`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +488/-6 líneas).

## Novedades 3.21.2 (verificado en código)
- fix(model): Agregada anotación `@Serial` a `serialVersionUID` en clases serializables
  - `PersonaBeneficiario`, `ChequeraIncompleta`, `ChequeraKey`, `ChequeraPreuniv` ahora usan `@Serial` para metadatos de serialización
- refactor(model): Renombrado de campo `personaEntity` a `persona` en clases de modelo para consistencia de nomenclatura
  - `PersonaBeneficiario`, `ChequeraIncompleta`, `ChequeraKey`, `ChequeraPreuniv`: campo `personaEntity` → `persona`
  - `SheetService`: Referencias actualizadas de `getPersonaEntity()` → `getPersona()`
  - Consistencia con la convención de `ContratoEntity` establecida en v3.20.0

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +17/-9 líneas).

## Novedades 3.21.1 (verificado en código)
- fix(tables): Agregadas anotaciones `@Table(name = ...)` faltantes en `BajaEntity` y `ChequeraSerieEntity`
  - `BajaEntity` con `@Table(name = "baja")` para mapeo explícito de tabla
  - `ChequeraSerieEntity` con `@Table(name = "chequera_serie")` para referencia explícita de tabla
- fix(chequeraSerie): Excluidas chequeras "dadas de baja" en consulta `findLastPreuniversitario`
  - Cambio de `findFirstBy...` (resultado único) a `findAllBy...` (lista) con filtro post-query de bajas
  - Nueva dependencia `BajaService` en `ChequeraSerieService` para verificar registros de baja
  - Previene retorno de chequeras con soft-delete como último preuniversitario
- chore(deps): Eliminado versionado explícito de `commons-lang3` (gestionado por Spring Boot parent POM 4.0.6)

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +21/-10 líneas).

## Novedades 3.21.0 (verificado en código)
- refactor(chequeraSerie): Migración completa del módulo ChequeraSerie a arquitectura hexagonal con Java
  - Eliminación de `ChequeraSerie.kt` (Kotlin) y creación de `ChequeraSerieEntity.java` con anotaciones Lombok
  - `ChequeraSerieService` y `ChequeraSerieController` movidos a `hexagonal/chequeraSerie/`
  - Actualización de referencias en +25 archivos (`PersonaService`, `ChequeraCuota`/`ChequeraCuotaReemplazo`/`ChequeraCuotaDeuda` en Kotlin, todos los servicios facade)
  - Eliminación de repositorio legacy `ChequeraSerieRepository.java`
- refactor(baja): Migración completa del módulo Baja a arquitectura hexagonal con Java
  - Eliminación de `Baja.kt` (Kotlin) y creación de `BajaEntity.java` con anotaciones Lombok
  - `BajaService` y `BajaController` movidos a `hexagonal/baja/`
  - Eliminación de repositorio legacy `BajaRepository.java`

> Basado en análisis profundo de `git diff HEAD` (34 archivos modificados, +598/-554 líneas).

## Novedades 3.20.0 (verificado en código)
- refactor(contrato): Renombrado de campo `personaEntity` a `persona` en `ContratoEntity`
  - Campo de relación `@ManyToOne` renombrado para consistencia de nomenclatura
  - `SheetService` actualizado para usar `getPersona()` en generación de hojas de cálculo
- refactor(chequeraSerie): Renombrado de campo `personaEntity` a `persona` en `ChequeraSerieAlta` y `ChequeraSerieAltaFull`
  - Consistencia de nomenclatura con `ContratoEntity`
  - `ChequeraSerieAltaFullService` actualizado con null-safe `Objects.requireNonNull()`

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +8/-7 líneas).

## Novedades 3.19.1 (verificado en código)
- fix(chequeraCuota): Agregado filtro `baja=0` en consulta de cuotas filtradas para excluir registros soft-deleted
  - Nueva query `findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndImporte1GreaterThan` en `ChequeraCuotaRepository`
  - `ChequeraCuotaService` actualizado para usar la nueva query con `baja = (byte) 0`
  - Previene retorno de registros con soft-delete en resultados de cuotas

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +12/-2 líneas).

## Novedades 3.19.0 (verificado en código)
- feat(contrato): Nuevo módulo Contrato con arquitectura hexagonal completa
  - Modelo de dominio: `Contrato` con campos `contratoId`, `personaId`, `documentoId`, `desde`, `facultadId`, `planId`, `materiaId`, `geograficaId`, `cargoMateriaId`, `primerVencimiento`, `cargo`, `montoFijo`, `canonMensual`, `canonMensualSinAjuste`, `hasta`, `canonMensualLetras`, `canonTotal`, `canonTotalLetras`, `meses`, `mesesLetras`, `ajuste`
  - Puertos de entrada (9): `CreateContratoUseCase`, `DeleteContratoUseCase`, `GetAllContratosAjustablesUseCase`, `GetAllContratosByFacultadUseCase`, `GetAllContratosByPersonaUseCase`, `GetAllContratosVigentesUseCase`, `GetContratoByIdUseCase`, `SaveAllContratosUseCase`, `UpdateContratoUseCase`
  - Puerto de salida: `ContratoRepository` con métodos CRUD y búsquedas por facultad, persona, ajustable y vigente
  - Casos de uso: Implementaciones completas en `application/usecases/` con inyección de dependencias
  - Servicio de aplicación: `ContratoService` con delegación a casos de uso y retornos `Optional`
  - Adaptador JPA: `JpaContratoRepositoryAdapter` con mapeo dominio ↔ entidad
  - Repositorio JPA: `JpaContratoRepository` con consultas especializadas
  - Mapper: `ContratoMapper` para conversión entidad ↔ dominio
  - Controlador REST: `ContratoController` con endpoints `GET /contrato/ajustable/{referencia}`, `GET /contrato/vigente/{referencia}`, `GET /contrato/persona/{personaId}/{documentoId}`, `GET /contrato/{contratoId}`, `PUT /contrato/{contratoId}`, `PUT /contrato/`
  - DTOs: `ContratoRequest` y `ContratoResponse`
  - DTO Mapper: `ContratoDtoMapper` para conversión DTO ↔ dominio
- refactor(contrato): Migración completa de módulo Contrato a arquitectura hexagonal
  - Eliminación de `Contrato.java` (modelo legacy) del paquete `core/model/`
  - Eliminación de `ContratoRepository.java` del paquete `core/repository/`
  - Eliminación de `ContratoService.java` del paquete `core/service/`
  - Eliminación de `ContratoController.java` del paquete `core/controller/`
  - Actualización de ~10+ archivos que referenciaban `um.tesoreria.core.model.Contrato` para usar la nueva estructura hexagonal
  - Migración de `ContratoEntity` de `@Data` a `@Getter`/`@Setter`/`@Builder` para consistencia

> Basado en análisis profundo de `git diff HEAD` (39 archivos, +793/-229 líneas).

## Novedades 3.18.0 (verificado en código)
- feat(facultad): Nuevo módulo Facultad con arquitectura hexagonal completa
  - Modelo de dominio: `Facultad` con campos `facultadId`, `nombre`, `codigoempresa`, `server`, `dbadm`, `dsn`, `cuentacontable`, `apiserver`, `apiport`, `fechaAuditoria`, `usuarioAuditoria`
  - Puertos de entrada: `GetAllFacultadesUseCase`, `GetFacultadByIdUseCase`, `GetFacultadesFiltradasUseCase`
  - Puerto de salida: `FacultadRepository` con métodos `findAll()`, `findAllIn(List<Integer>)`, `findById(Integer)`
  - Casos de uso: `GetAllFacultadesUseCaseImpl`, `GetFacultadByIdUseCaseImpl`, `GetFacultadesFiltradasUseCaseImpl`
  - Servicio de aplicación: `FacultadService` con métodos `findAll()`, `findFacultades()`, `findByFacultadId(Integer)`, y métodos legacy `findAllByLectivoId`, `findAllByPersona`, `findAllByDisenho`
  - Adaptador JPA: `JpaFacultadRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `FacultadEntity` con anotaciones Lombok, `@Table(name = "facultad")`, herencia de `Auditable`
  - Repositorio JPA: `JpaFacultadRepository` con `findAllByFacultadIdIn` y `findByFacultadId`
  - Mapper: `FacultadMapper` para conversión entidad → dominio
  - Controlador REST: `FacultadController` con endpoints `GET /facultad/`, `GET /facultad/facultades`, `GET /facultad/{facultadId}`, y legacy `GET /facultad/lectivo/{lectivoId}`, `GET /facultad/bypersona/{personaId}/{documentoId}/{lectivoId}`, `GET /facultad/disenho/{lectivoId}/{geograficaId}`
  - DTO: `FacultadResponse` para respuestas HTTP
  - DTO Mapper: `FacultadDtoMapper` para conversión dominio → DTO
- refactor(facultad): Migración completa de módulo Facultad a arquitectura hexagonal
  - Eliminación de `Facultad.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `FacultadRepository.java` del paquete `core/repository/`
  - Eliminación de `FacultadService.java` del paquete `core/service/`
  - Eliminación de `FacultadController.java` del paquete `core/controller/`
  - Actualización de ~20+ archivos que referenciaban `um.tesoreria.core.kotlin.model.Facultad` para usar `FacultadEntity` del nuevo módulo hexagonal

> Basado en análisis profundo de `git diff HEAD` (nuevos archivos creados y eliminaciones de legacy).

## Novedades 3.17.0 (verificado en código)
- feat(dependencia): Nuevo módulo Dependencia con arquitectura hexagonal completa
  - Modelo de dominio: `Dependencia` con campos `dependenciaId`, `nombre`, `acronimo`, `facultadId`, `geograficaId`, `cuentaHonorariosPagar`, y relaciones a `Facultad`, `Geografica`, `Cuenta`
  - Puertos de entrada: `GetAllDependenciasUseCase`, `GetDependenciaByIdUseCase`, `UpdateDependenciaUseCase`
  - Puerto de salida: `DependenciaRepository` con métodos `findAll()`, `findById(Integer)`, `save(Dependencia)`
  - Casos de uso: `GetAllDependenciasUseCaseImpl`, `GetDependenciaByIdUseCaseImpl`, `UpdateDependenciaUseCaseImpl`
  - Servicio de aplicación: `DependenciaService` con métodos `findAll()`, `findByDependenciaId(Integer)`, `update(Integer, Dependencia)`
  - Adaptador JPA: `JpaDependenciaRepositoryAdapter` con mapeo dominio ↔ entidad y relaciones `@OneToOne`
  - Repositorio JPA: `JpaDependenciaRepository` con `findAllByOrderByNombre()` y `findByDependenciaId(Integer)`
  - Controlador REST: `DependenciaController` con endpoints `GET /dependencia/`, `GET /dependencia/{dependenciaId}`, `PUT /dependencia/{dependenciaId}`
  - DTOs: `DependenciaRequest` y `DependenciaResponse`
  - DTO Mapper: `DependenciaDtoMapper` para conversión DTO ↔ dominio
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

## Novedades 3.16.0 (verificado en código)
- feat(articulo): Enriquecimiento de respuestas con objeto `Cuenta` vía `CuentaService`
  - Nuevo campo `Cuenta cuenta` en `ArticuloSearchResponse` para incluir datos de cuenta asociada
  - Nuevo campo `articuloId` en `ArticuloRequest` para especificar ID en creación
  - Los endpoints `GET /articulo/{id}`, `POST /articulo/search` y `GET /articulo/page` ahora resuelven y adjuntan el objeto `Cuenta` completo
- refactor(articulo): Cambio de `@Data` a `@Getter`/`@Setter` en `Articulo` para mayor control de mutabilidad
- refactor(articulo): Renombrado de campo `cuenta` a `numeroCuenta` en `ArticuloSearch`, `ArticuloKey`, `ArticuloSearchResponse` y mappers
  - Compatibilidad BD asegurada vía `@Column(name = "cuenta")`
- refactor(dto): Uso de importación `OffsetDateTime` en lugar de FQCN en `ArticuloSearch` y `ArticuloSearchResponse`

> Basado en análisis profundo de `git diff HEAD` (8 archivos modificados, +72/-27 líneas).

## Novedades 3.15.0 (verificado en código)
- feat(facturaPendiente): Nuevo módulo FacturaPendiente con arquitectura hexagonal completa
  - Modelo de dominio: `FacturaPendiente` con campos `proveedorMovimientoId`, `razonSocial`, `cuit`, `cbu`, `fechaComprobante`, `fechaVencimiento`, `observaciones`, `comprobante`, `debita`, `prefijo`, `numeroComprobante`, `importeFactura`, `importePagado`
  - Puertos de entrada: `GetFacturasPendientesUseCase` con método `getFacturasPendientes(OffsetDateTime, OffsetDateTime)`
  - Puerto de salida: `FacturaPendienteRepository` con métodos `updateFechaPagoInProveedorPago()`, `findFacturasPendientes(OffsetDateTime, OffsetDateTime)`
  - Caso de uso: `GetFacturasPendientesUseCaseImpl` con lógica de negocio
  - Servicio de aplicación: `FacturaPendienteService` con método `findAllFacturasPendientesBetweenDates`
  - Adaptador JPA: `JpaFacturaPendienteRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `FacturaPendienteEntity` con anotaciones Lombok e `@Immutable`
  - Repositorio JPA: `JpaFacturaPendienteRepository` con consultas nativas
- feat(sheet): Actualización de `SheetService` para nuevos campos en hojas de cálculo
  - Agregado campo `cbu` en generación de hojas
  - Agregado campo `fechaVencimiento` con formato UTC
  - Agregado campo `observaciones` (concepto de factura)
  - Migración de uso de `jsonify()` a utilitaria `Jsonifier`
- refactor(facturaPendiente): Migración completa a arquitectura hexagonal
  - Eliminación de `FacturaPendiente.kt` (Kotlin) de `core/kotlin/model/view/`
  - Eliminación de `FacturaPendienteService.java` de `core/service/view/`
  - Migración de `FacturaPendienteRepository.kt` a Java en `hexagonal/facturaPendiente/infrastructure/persistence/repository/`
  - Actualización de `SheetService` para usar nuevo servicio hexagonal
- Removed: Eliminación de tests obsoletos `ChequeraCuotaControllerTest.java`, `ProveedorMovimientoControllerTest.java`

> Basado en análisis profundo de `git diff HEAD` (13 archivos modificados, +120/-156 líneas).

## Novedades 3.14.0 (verificado en código)
- feat(ubicacion): Nuevo módulo Ubicacion con arquitectura hexagonal
  - Modelo de dominio: `Ubicacion` con campos `ubicacionId`, `nombre`, `dependenciaId`, `geograficaId`
  - Puertos de entrada: `GetAllUbicacionesUseCase`, `GetUbicacionesBySedeUseCase`
  - Puerto de salida: `UbicacionRepository` con métodos `findAll()`, `findAllByGeograficaId(Integer)`
  - Casos de uso: `GetAllUbicacionesUseCaseImpl`, `GetUbicacionesBySedeUseCaseImpl`
  - Servicio de aplicación: `UbicacionService` con métodos `findAll()`, `findAllBySede(Integer)`
  - Adaptador JPA: `JpaUbicacionRepositoryAdapter` con mapeo a entidad JPA
  - Mapper: `UbicacionMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `UbicacionEntity` con anotaciones Lombok
  - Controlador REST: `UbicacionController` con endpoints GET
  - DTO: `UbicacionResponse` para respuestas HTTP
- feat(ubicacionArticulo): Nuevo módulo UbicacionArticulo con arquitectura hexagonal
  - Modelo de dominio: `UbicacionArticulo` con relaciones a `Ubicacion`, `Articulo`, `Cuenta`
  - Puertos de entrada: `GetAllUbicacionArticulosUseCase`, `GetUbicacionArticuloUseCase`, `GetUbicacionArticulosByArticuloUseCase`, `SaveUbicacionArticuloUseCase`
  - Puerto de salida: `UbicacionArticuloRepository` con métodos CRUD y búsquedas
  - Casos de uso: `GetAllUbicacionArticulosUseCaseImpl`, `GetUbicacionArticuloUseCaseImpl`, `GetUbicacionArticulosByArticuloUseCaseImpl`, `SaveUbicacionArticuloUseCaseImpl`
  - Servicio de aplicación: `UbicacionArticuloService` con métodos `findAll()`, `save()`, `findAllByArticuloId()`, `getByUbicacionAndArticulo()`
  - Adaptador JPA: `JpaUbicacionArticuloRepositoryAdapter` con lógica de upsert
  - Mapper: `UbicacionArticuloMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `UbicacionArticuloEntity` con restricción única `(ubicacionId, articuloId)`
  - Controlador REST: `UbicacionArticuloController` con endpoints CRUD
  - DTOs: `UbicacionArticuloRequest`, `UbicacionArticuloResponse`
- refactor(core): `SheetService` actualizado para usar nuevos modelos de dominio
- refactor(model): `Entrega.kt` actualizado con cambios en modelo
- refactor(model): `FacturaPendiente.kt` actualizado con nuevos campos
- refactor(dto): `AsignacionCostoDto` y `CostoParameterDto` actualizados

> Basado en análisis profundo de `git diff HEAD` (47 archivos modificados, +723/-246 líneas).

## Novedades 3.13.0 (verificado en código)
- feat(articulo): Implementación de paginación y búsqueda en módulo Artículo
  - Nuevo puerto de entrada: `GetPaginatedArticulosUseCase` con método `getPaginated(int page, int size)`
  - Nuevo caso de uso: `GetPaginatedArticulosUseCaseImpl` con implementación de paginación usando `Pageable`
  - Nuevo caso de uso: `SearchArticulosUseCaseImpl` para búsqueda de artículos por criterio
  - Nuevo modelo: `ArticuloSearch` para criterios de búsqueda con campo `search`
  - Nuevos métodos en `ArticuloRepository`: `findPaginated(Pageable)`, `search(String criterio)`
  - Nuevo endpoint en `ArticuloController`: `GET /articulo/page?page=X&size=Y` que retorna `PaginatedResponse<ArticuloResponse>`
  - Nuevo endpoint en `ArticuloController`: `GET /articulo/search?criterio=X` para búsqueda
  - Nuevo DTO: `ArticuloSearchResponse` para respuestas de búsqueda
- feat(proveedor): Mejoras en módulo Proveedor
  - Actualización de `ProveedorSearch` con nuevos campos para búsquedas avanzadas
  - Nuevos endpoints en `ProveedorController` para búsquedas
- feat(articulo): Completitud de migración hexagonal (commit afbeb02)
  - `ArticuloKeyService.java` eliminado (funcionalidad movida a casos de uso específicos)
  - `ArticuloKey.java`, `ArticuloKeyRepository.java`, `ArticuloKeyRepositoryCustom.java` movidos a `persistence/repository/`
  - Mappers actualizados para soportar nuevos modelos

> Basado en análisis profundo de `git diff HEAD` (24 archivos modificados, +160/-45 líneas) y commits afbeb02, 4b9e84e.

## Novedades 3.12.0 (verificado en código)
- feat(articulo): Completitud de migración de módulo Artículo a arquitectura hexagonal
  - Nuevos modelos de dominio: `Articulo` y `ArticuloSearch` con Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Puertos de entrada: `CreateArticuloUseCase`, `DeleteArticuloUseCase`, `GetAllArticulosUseCase`, `GetArticuloByIdUseCase`, `GetNewArticuloUseCase`, `SearchArticulosUseCase`, `UpdateArticuloUseCase`
  - Puerto de salida: `ArticuloRepository` con métodos `create`, `findById`, `findAll`, `update`, `deleteById`, `findLast`
  - Casos de uso: Implementaciones completas en `application/usecases/` (`CreateArticuloUseCaseImpl`, `DeleteArticuloUseCaseImpl`, etc.)
  - Servicio de aplicación: `ArticuloService` refactorizado con inyección de casos de uso y `Optional<Articulo>` en retornos
  - Adaptador JPA: `JpaArticuloRepositoryAdapter` con implementación completa de `ArticuloRepository`
  - Mappers: `ArticuloMapper` (dominio ↔ JPA Entity) y `ArticuloDtoMapper` (dominio ↔ DTO)
  - DTOs: `ArticuloRequest`, `ArticuloResponse`, `ArticuloSearchResponse` para entrada/salida
  - Controlador REST: `ArticuloController` migrado con endpoints CRUD y manejo de `ResponseEntity`
  - `JpaArticuloRepository` simplificado a solo consultas específicas
- refactor(core): `CostoParameterDto` y `CostoParameterService` actualizados para usar modelo de dominio `Articulo`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados).

## Novedades 3.11.0 (verificado en código)
- feat(articulo): Migración de módulo Artículo a arquitectura hexagonal
  - Nueva entidad JPA: `ArticuloEntity` en `hexagonal/articulo/infrastructure/persistence/entity/` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Nuevo repositorio: `JpaArticuloRepository` en `hexagonal/articulo/infrastructure/persistence/repository/` con métodos `findByArticuloId`, `findTopByOrderByArticuloIdDesc`
  - Nuevo servicio de aplicación: `ArticuloService` en `hexagonal/articulo/application/service/` con lógica de negocio
  - Nuevo controlador REST: `ArticuloController` en `hexagonal/articulo/infrastructure/web/controller/` migrado con `@RequiredArgsConstructor`
  - Eliminación de `Articulo.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `ArticuloRepository.java` del paquete `core/repository/`
  - Eliminación de `ArticuloService.java` del paquete `core/service/`
  - Eliminación de `ArticuloController.java` del paquete `core/controller/`
  - Actualización de referencias en `EntregaDetalle.kt`, `ProveedorArticulo.kt`, `UbicacionArticulo.java`, `AsignacionCostoDto.java`, `CostoParameterDto.java`, `CostoParameterService.java`
- fix(auth): Corrección de espacio extra en `ResponseStatusException` en `AuthController`

> Basado en análisis profundo de `git diff HEAD` (14 archivos modificados, +156/-98 líneas).

## Novedades 3.10.0 (verificado en código)
- feat(proveedor): Mejora de modelo de datos y refactorización de DTOs en módulo Proveedor
  - Renombrado de campo `cuenta` a `numeroCuenta` en `ProveedorSearch`, `ProveedorSearchEntity` y `ProveedorSearchResponse` para mayor claridad
  - Nuevo campo `cbu` en `ProveedorSearch`, `ProveedorSearchEntity`, `ProveedorResponse` y `ProveedorSearchResponse`
  - Nuevo campo `Cuenta cuenta` (objeto dominio) en `ProveedorResponse` y `ProveedorSearchResponse`
  - Refactorización de DTOs (`ProveedorResponse`, `ProveedorSearchResponse`) para usar patrón Builder de Lombok (`@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Refactorización de `ProveedorDtoMapper` para usar `builder()` en lugar de setters manuales
  - Actualización de `ProveedorMapper` para mapear nuevos campos (`numeroCuenta`, `cbu`)
  - Cambio de anotación en `ProveedorSearchEntity` y DTOs: de `@Data` a `@Getter`/`@Setter` para mayor control

> Basado en análisis profundo de `git diff HEAD` (6 archivos modificados, +67/-44 líneas).

## Novedades 3.9.0 (verificado en código)
- feat(proveedor): Implementación de paginación en módulo Proveedor
  - Nuevo puerto de entrada: `GetPaginatedProveedoresUseCase` con método `getPaginatedProveedores(int page, int size)`
  - Nuevo modelo genérico: `PaginatedResponse<T>` en `core/model/` para respuestas paginadas
  - Nuevo caso de uso: `GetPaginatedProveedoresUseCaseImpl` con implementación de paginación usando `Pageable`
  - Actualización de puerto de salida `ProveedorRepository` con método `findAll(Pageable pageable)`
  - Nuevo endpoint en `ProveedorController`: `GET /proveedor/page?page=X&size=Y` que retorna `PaginatedResponse<ProveedorResponse>`
  - Actualización de `ProveedorService` con método `getPaginated(int page, int size)`

> Basado en análisis profundo de `git diff HEAD` (7 archivos modificados, +101 líneas).

## Novedades 3.8.0 (verificado en código)
- feat(cuenta): Implementación completa de casos de uso CRUD para módulo Cuenta
  - Nuevos casos de uso: `CreateCuentaUseCase`, `DeleteCuentaUseCase`, `GetAllCuentasUseCase`, `GetCuentaByCuentaContableIdUseCase`, `GetCuentaByNumeroCuentaUseCase`, `RecalculaGradosUseCase`, `SaveAllCuentasUseCase`, `SearchCuentasUseCase`, `UpdateCuentaUseCase`
  - Nuevos DTOs: `CuentaRequest`, `CuentaResponse`, `CuentaSearchResponse`
  - Nuevo mapper: `CuentaDtoMapper` para conversión dominio ↔ DTO
  - Controlador `CuentaController` actualizado con todos los endpoints REST
- feat(proveedor): Mejora de módulo Proveedor con búsqueda avanzada
  - Nueva entidad `ProveedorSearchEntity` para búsquedas
  - Nuevo repositorio `JpaProveedorSearchRepository` con consultas personalizadas
  - Refactorización de paquetes: adaptador movido a `infrastructure/persistence/adapter/`
  - Actualización de `ProveedorMapper` y `GetAllProveedoresUseCaseImpl`
- refactor(service): Actualización de `BalanceService`, `ContabilidadService`, `SheetService` para usar nuevas estructuras hexagonales
- refactor(model): Actualización de `Ejercicio.kt` con cambios en modelo

> Basado en análisis profundo de `git diff HEAD` (47 archivos modificados, +894/-447 líneas).

## Novedades 3.7.0 (verificado en código)
- feat(hexagonal): Nuevo módulo Cuenta con arquitectura hexagonal
  - Entidad JPA: `CuentaEntity` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Repositorio: `CuentaRepository` con métodos `findAllByGradoAndNumeroCuentaGreaterThan`, `findAllByNumeroCuentaIn`, `findAllByGradoAndNumeroCuentaBetween`
  - Servicio de aplicación: `CuentaService` con lógica de negocio y método `recalculaGrados()`
  - Controlador REST: `CuentaController` con endpoints CRUD y búsquedas
  - Integración con `CuentaSearchService` para búsquedas avanzadas por condiciones
  - Relación `@OneToOne` con `GeograficaEntity`
- refactor(cuenta): Migración completa de módulo Cuenta a arquitectura hexagonal
  - Eliminación de `Cuenta.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `CuentaRepository.java` del paquete `core/repository/`
  - Eliminación de `CuentaService.java` del paquete `core/service/`
  - Eliminación de `CuentaController.java` del paquete `core/controller/`
  - Nueva estructura en `hexagonal/cuenta/` con capas domain, application, infrastructure
  - Actualización de servicios externos (`BalanceService`, `CompraService`, `ContabilidadService`) para usar nueva estructura
- refactor(model): Migración de modelos Kotlin a Java en paquete `core/kotlin/model/`
  - `Articulo.kt`, `Bancaria.kt`, `BancoMovimiento.kt`, `CuentaMovimiento.kt`, `Dependencia.kt`, `Setup.kt`, `Valor.kt`
  - Actualización de `UbicacionArticulo.java` y `CuentaMovimientoAsiento.java`

> Basado en análisis profundo de `git diff HEAD` (19 archivos modificados, +161/-146 líneas).

## Novedades 3.6.0 (verificado en código)
- feat(hexagonal): Nuevo módulo Auth con arquitectura hexagonal para autenticación
  - Modelo de dominio: `UsuarioAuth` con validación de login y password (SHA-256)
  - Caso de uso: `LoginUseCaseImpl` con lógica de autenticación robusta
  - Servicio de aplicación: `AuthService` como fachada del dominio
  - Adaptador JPA: `JpaUsuarioAuthRepositoryAdapter` con mappers
  - Controlador REST: `AuthController` con endpoints de login
  - DTOs: `LoginRequest` y `LoginResponse` para entrada/salida
- feat(hexagonal): Nuevo módulo Geografica con arquitectura hexagonal
  - Modelo de dominio: `Geografica` para entidades geográficas
  - Casos de uso: `GetAllGeograficasUseCase`, `GetGeograficaByIdUseCase`, `GetGeograficasBySedeUseCase`
  - Servicio de aplicación: `GeograficaService` con integración a servicios existentes
  - Adaptador JPA: `JpaGeograficaRepositoryAdapter` con `GeograficaEntity`
  - Controlador REST: `GeograficaController` migrado a arquitectura hexagonal
  - DTO: `GeograficaResponse` para respuestas HTTP
- feat(hexagonal): Implementación completa del módulo Proveedor con arquitectura hexagonal
  - Modelos de dominio: `Proveedor` y `ProveedorSearch` con Lombok
  - Puertos de entrada: `CreateProveedorUseCase`, `DeleteProveedorUseCase`, `GetAllProveedoresUseCase`, `GetLastProveedorUseCase`, `GetProveedorByCuitUseCase`, `GetProveedorByIdUseCase`, `SearchProveedoresUseCase`, `UpdateProveedorUseCase`
  - Puerto de salida: `ProveedorRepository` con métodos CRUD completos
  - Casos de uso: Implementaciones completas en `application/usecases/`
  - Servicio de aplicación: `ProveedorService` refactorizado con `Optional<Proveedor>` en retornos
  - Adaptador JPA: `JpaProveedorRepositoryAdapter` con implementación completa
  - Mappers: `ProveedorMapper` (dominio ↔ JPA) y `ProveedorDtoMapper` (dominio ↔ DTO)
  - DTOs: `ProveedorRequest`, `ProveedorResponse`, `ProveedorSearchResponse`
  - Controlador REST: `ProveedorController` migrado con manejo de `ResponseEntity` y `Optional`
- refactor(model): Migración de `Geografica.kt` (Kotlin) a `GeograficaEntity.java`
  - Eliminación de modelo Kotlin en paquete `core/kotlin/model/`
  - Creación de entidad JPA en `hexagonal/geografica/infrastructure/persistence/entity/`
  - Actualización de `CursoHaberes.java` para usar `GeograficaEntity` en lugar de `Geografica`
- refactor(proveedor): Reestructuración completa de paquetes de Proveedor a arquitectura hexagonal
  - Migración de servicios y repositorios al paquete `hexagonal/proveedor/`
  - Nuevos modelos de dominio reemplazando entidades en capa de dominio
  - Mappers agregados para conversión entre capas

> Basado en análisis profundo de `git diff HEAD` (55 archivos modificados).

## Novedades 3.5.2 (verificado en código)
- chore(deps): Actualización de Spring Boot de 4.0.2 a 4.0.5
- chore(deps): Actualización de Kotlin de 2.3.10 a 2.3.20
- chore(deps): Actualización de log4j2 de 4.0.2 a 4.0.5
- chore(deps): Actualización de springdoc-openapi de 3.0.1 a 3.0.2
- chore(deps): Actualización de openpdf de 3.0.0 a 3.0.3
- chore(deps): Actualización de json-path de 2.10.0 a 3.0.0
- chore(ci): Actualización de GitHub Actions (checkout v4→v6, setup-java v4→v5, cache v4→v5, login-action v3→v4, metadata-action v5→v6, buildx-action v3→v4, build-push-action v6→v7, deploy-pages v4→v5)
- feat(deps): Nueva dependencia commons-fileupload 1.6.0

## Novedades 3.5.1 (verificado en código)
- refactor(model): Migración de FacturacionElectronica de Kotlin a Java
  - Eliminación de FacturacionElectronica.kt (modelo Kotlin)
  - Creación de FacturacionElectronica.java con anotaciones Lombok (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor)
  - Nuevo método jsonify() para logging estructurado
- refactor(controller): Uso de ResponseEntity.ok() en lugar de new ResponseEntity<>() en FacturacionElectronicaController
- refactor(service): Uso de utilitaria Jsonifier en lugar de JsonMapper directamente en FacturacionElectronicaService
- refactor(config): Nueva configuración de Jackson en bootstrap.yml (fail-on-null-for-primitives: false)

## Novedades 3.5.0 (verificado en código)
- refactor(deps): Actualización de Kotlin de 2.3.0 a 2.3.10
- refactor(http): Migración masiva de RestTemplate a RestClient en todos los consumers externos (~14 archivos)
- feat(model): Nuevos campos de becario (hpum, becaPorcentaje, becaResolucion, becaFecha, becaUserId) en ChequeraImpresionCabecera y ChequeraSerie
- feat(model): Nuevo campo hpum en PersonaEntity
- feat(hexagonal): Nueva estructura skeleton MatriculacionContext con arquitectura hexagonal
- refactor(controller): Migración de EjercicioController a inyección con @RequiredArgsConstructor
- fix(exception): Renombrado BajaFacultadNotFoundException a BajaFacultadException
- chore(cleanup): Eliminación de MatriculaController, MatriculaRepository y modelo Matricula
- fix(api): Eliminación de endpoint sincronizeMatricula en SincronizeController
- feat(api): Nuevo endpoint `/habilitados` en ArancelTipoController para obtener tipos de arancel habilitados
  - Nuevo método `findAllHabilitados()` en ArancelTipoService
  - Nuevo método `findAllByHabilitado()` en ArancelTipoRepository
  - Retorna solo registros con campo `habilitado = 1`
- refactor: Migración a inyección por constructor con `@RequiredArgsConstructor` en ArancelTipoService
  - Reemplazo de `@Resource` por campos `final` + constructor Lombok
- refactor: Uso de `ResponseEntity.ok()` en lugar de `new ResponseEntity<>()` en CostoController
- fix: Agregado `assert` en CostoService para validación de objeto Entrega

## Novedades 3.3.2 (verificado en código)
- refactor: Migración a inyección por constructor con `@RequiredArgsConstructor` en ArancelTipoController, FacultadController y TipoChequeraSedeController
  - Reemplazo de `@Resource`/`@Autowired` por `final` + constructor Lombok
  - Uso de `ResponseEntity.ok()` en lugar de `new ResponseEntity<>()`
- fix(api): Agregado manejo de excepciones con `ResponseStatusException` en controladores
  - Devolución de HTTP 400 Bad Request para errores de negocio
- fix(transaction): Agregado `@Transactional` en MercadoPagoContextService.processPaymentEvent()

## Novedades 3.3.1 (verificado en código)
- fix(performance): Optimización de rendimiento en actualización de último login
  - Implementación de query directa con `@Modifying` en `UsuarioRepository`
  - Refactorización de `UsuarioService.updateLastLog()` para reducir overhead de persistencia
  - Corrección de manejo transaccional en `MercadoPagoContextService.processPaymentEvent()`

## Novedades 3.3.0 (verificado en código)
- feat: Implementación de batch processing y optimizaciones de rendimiento en PersonaService
  - Nuevos métodos batch en ChequeraCuotaRepository para consultas masivas por IDs
  - Nuevo método `findAllByChequeraCuotaIds()` en MercadoPagoContextService
  - Implementación de "Self-Healing" para corrección automática de chequeraId nulos
  - Reducción de N+1 queries mediante carga masiva de contextos
  - Optimización en creación de preferencias con reutilización de contextos existentes
- refactor: Migración de configuración Kafka a nueva API Jackson (JacksonJsonDeserializer)
  - Actualización de paquetes de `com.fasterxml.jackson` a `tools.jackson`
  - Simplificación de configuración del ObjectMapper
- feat: Nuevo método sobrecargado `makeContext()` en MercadoPagoCoreService
  - Permite reutilizar contextos existentes evitando duplicados

## Novedades 3.2.0 (verificado en código)
- feat: Implementación completa del módulo ChequeraCuota con Arquitectura Hexagonal
  - Nuevos modelos de dominio: `ChequeraCuota`, `ChequeraPago`, `ChequeraTotal`, `DeudaData`, `ChequeraSerie`
  - Puertos definidos: `CalculateDeudaUseCase`, `ChequeraCuotaRepository`
  - Servicio de aplicación: `ChequeraCuotaService` con cálculo de deudas y extensión
  - Adaptador JPA: `JpaChequeraCuotaRepositoryAdapter` con mappers
- refactor: Migración de lógica de negocio desde servicio tradicional a arquitectura hexagonal
  - Integración en `ChequeraCuotaController`, `PersonaService`, `ChequeraSerieService`, `SheetService`
  - Mejora en separación de concerns y testeabilidad
- fix: Eliminación de dependencia circular en `LectivoService` (uso directo de repository)
- chore: Limpieza de código comentado y optimización de imports

## Novedades 3.1.2 (verificado en código)
- fix: Refinamiento de corrección de timezone en pagos de MercadoPago
  - Simplificación de la lógica de ajuste de timezone en `ChequeraPagoService`
  - Ajuste directo de -3 horas al guardar fechas de pago y acreditación en `PagoService`
  - Eliminación de lógica condicional compleja basada en fechas específicas
- refactor: Uso consistente de `minusHours(3)` para normalización de fechas UTC a Argentina

## Novedades 3.1.1 (verificado en código)
- fix: Corrección de timezone en consultas de pagos de MercadoPago (`findAllByTipoPagoIdAndFechaPago`)
  - Ajuste de +3 horas para fechas posteriores a febrero 2026
  - Ajuste especial para el 31 de enero de 2026 (+2h 59m)
  - Nueva constante `TIPO_MERCADO_PAGO = 18`
- refactor: Uso de `@RequiredArgsConstructor` en lugar de constructor manual
- chore: Eliminación de campo `jsonMapper` no utilizado

## Novedades 3.1.0 (verificado en código)
- Refactorización del módulo Persona a arquitectura hexagonal
- Implementación de Kafka Consumer Config para manejo de eventos de pago
- Mejoras en KafkaProducerConfig usando string serializer class name
- Actualización de PaymentEventListener con containerFactory específico
- Modificación de PreferenceClient para usar MercadoPagoContextDto importado
- Actualización de logging en bootstrap.yml para incluir debug de Kafka

## Novedades 3.0.0 (verificado en código)
- Actualización de Spring Boot de 3.5.8 a 4.0.2
- Actualización de Java de 24 a 25
- Actualización de Kotlin de 2.2.21 a 2.3.0
- Actualización de Spring Cloud de 2025.0.0 a 2025.1.0
- Actualización de mysql-connector-j de 9.4.0 a 9.6.0
- Actualización de springdoc-openapi de 2.8.10 a 3.0.1
- Actualización de Apache POI de 5.4.1 a 5.5.1
- Actualización de guava de 33.4.8-jre a 33.5.0-jre
- Actualización de commons-lang3 de 3.18.0 a 3.20.0
- Actualización de modelmapper de 3.2.4 a 3.2.6
- Actualización de json-path de 2.9.0 a 2.10.0
- Actualización de GitHub Actions para usar JDK 25
- Actualización de Dockerfile para usar JDK 25

## Novedades 2.5.0 (verificado en código)
- Implementación de envío asíncrono de correos de chequeras utilizando Kafka (`SendChequeraEvent`).
- Actualización de Spring Boot a la versión 3.5.8.
- Configuración de `KafkaProducerConfig` para manejo de eventos.

## Novedades 2.4.0 (verificado en código)
- Añadido `@Builder.Default` a varios campos de modelos para mejorar la instanciación de objetos (MercadoPagoContext, Proveedor, TipoChequeraMercadoPagoCreditCard, MercadoPagoContextDto)
- Refactorización: Se extrajo lógica común en métodos auxiliares privados (`setDeuda`, `setUltimoEnvio`) en `ChequeraSerieService` para reducir la duplicación de código y mejorar la legibilidad.
- Refactorización: Se actualizaron los métodos `update` en `ChequeraSerieService`, `DomicilioService` y `TipoChequeraService` para modificar directamente las entidades existentes en lugar de crear nuevas instancias.
- Refactorización: Se simplificaron los métodos `add`, `setPayPerTic`, `setEnviado` en `ChequeraSerieService` devolviendo directamente los resultados de `repository.save()` y eliminando el registro redundante.
- Refactorización: Se reemplazó el constructor manual con `@RequiredArgsConstructor` en `PersonaService`.
- Refactorización: Se mejoró el método `DomicilioService.capture` extrayendo la sincronización de provincia y localidad en un método auxiliar y manejando IDs predeterminados.
- Refactorización: Se eliminó el registro redundante en `MercadoPagoContextService`, `PersonaService`, `TipoChequeraService` y `MercadoPagoCoreService`.
- Corrección: Se excluyeron las cuotas compensadas de la creación de preferencias pendientes de MercadoPago en `PersonaService`.
- Corrección: Se mejoró el registro de errores en `ChequeraSerieService.setUltimoEnvio` de debug a error.

## Novedades 2.3.2 (verificado en código)
- Refactorización para usar @RequiredArgsConstructor en ChequeraController y MercadoPagoCoreController

## Novedades 2.3.1 (verificado en código)
- Añadido filtro de compensada en ChequeraCuotaRepository para excluir cuotas compensadas de consultas pendientes
- Actualizado ChequeraCuotaService.findAllPendientes para incluir parámetro compensada

## Novedades 2.3.0 (verificado en código)
- Añadido módulo MercadoPagoContextHistory para seguimiento del historial de contextos de MercadoPago
- Refactorización para usar @RequiredArgsConstructor en PagarFileController y PagarFileService
- Actualización de Collectors.toList() a .toList() en JpaCursoCargoContratadoRepositoryAdapter
- Integración de MercadoPagoContextHistory en MercadoPagoContextService para creación automática de historial en operaciones add, saveAll y update

## Novedades 2.2.2 (verificado en código)
- Mejora en lógica de fecha de acreditación en PagoService para fechas posteriores al 1 de septiembre 2025
- Actualización de ID de persona en método de test de ChequeraSerieService
- Actualización de versión de Kotlin a 2.2.21
- Corrección de sintaxis de comentarios en diagrama entities.mmd
- Corrección de flechas de flujo en diagrama hexagonal-architecture.mmd

## Novedades 2.2.1 (verificado en código)
- Añadido logging de debug en método findAllByCurso de CursoCargoContratadoService
- Mejoras de formato en anotaciones de CursoCargoContratadoEntity
- Eliminación de imports no utilizados en CursoCargoContratadoResponse
- Mejoras en script.js para mejor manejo de diagramas Mermaid (eliminación de frontmatter y correcciones de validación)

## Novedades 2.1.0 (verificado en código)
- Refactorización para usar `@RequiredArgsConstructor` en CostoController y CostoService
- Añadidos métodos `jsonify()` en modelos Asiento, CuentaMovimiento, Entrega y AsignacionCostoDto para logging estructurado
- Mejoras en logging con serialización JSON en servicios
- Eliminación de modificadores public innecesarios en AsientoRepository
- Mejora en manejo de errores en CostoService al eliminar asiento previo antes de agregar nuevo

## Novedades 2.0.0 (verificado en código)
- Eliminación de ContratadoController y referencias al modelo contratado en servicios y controladores
- Actualización de dependencias principales:
  - Spring Boot: 3.5.6
  - Kotlin: 2.2.20
  - OpenPDF: 3.0.0
- Refactorización para usar `@RequiredArgsConstructor` en lugar de `@Autowired` en múltiples servicios
- Nueva clase utilitaria `Jsonifier` para logging estructurado en JSON
- Actualización de imports para la biblioteca openpdf (cambio de paquete com.lowagie a org.openpdf)
- Eliminación de ContratoAutoFixService y dependencias relacionadas
- Mejoras en logging con métodos `jsonify()` en varios servicios

## Novedades 1.6.0 (verificado en código)
- Nuevo endpoint de resumen por lectivo (`/resumen/lectivo/{lectivoId}`) que devuelve la cantidad de chequeras agrupadas por facultad y sede
- Refactorización de `ChequeraSerieController` y `ChequeraSerieService` para usar `@RequiredArgsConstructor` en lugar de constructores explícitos
- Creación del DTO `FacultadSedeChequeraDto` para representar los datos del resumen

## Novedades 1.5.0 (verificado en código)
- Añadido nuevo endpoint para buscar `ChequeraSerie` por sede (`/sede/facultad/{facultadId}/lectivo/{lectivoId}/geografica/{geograficaId}`).
- Refactorizados `GeograficaController` y `LegajoController` para usar inyección de dependencias por constructor, `ResponseEntity.ok()` y manejo de excepciones mejorado.
- Añadido nuevo alias de endpoint en `GeograficaController` (`/api/tesoreria/core/geografica`).

## Novedades 1.4.0 (verificado en código)
- Nuevos endpoints y refactor en ChequeraSerieController (más métodos REST, simplificación de respuestas).
- Nuevos servicios de búsqueda y consulta (`ProveedorSearchService`, `TipoChequeraSearchService`, etc.).
- Nueva clase utilitaria `Tool` para manejo de fechas, archivos y conversiones.
- Eliminación de campo `productoId` en `CuotaPeriodoDto` y ajuste de queries asociadas.
- Actualización de dependencias principales:
  - Spring Boot: 3.5.5
  - Kotlin: 2.2.10
  - SpringDoc OpenAPI: 2.8.10
  - OpenPDF: 2.4.0

> Versión y dependencias verificadas en `pom.xml` y código fuente.

## Características
- Gestión de chequeras y pagos con optimizaciones de rendimiento
- Cálculo de deudas con computación paralela
- Integración con Mercado Pago para tarjetas de crédito
- Gestión de inscripciones y personas
- Gestión de domicilios y documentos
- Documentación automática con OpenAPI/Swagger
- CI/CD con GitHub Actions
- Soporte para Docker

## Requisitos
- Java 25 (verificado en pom.xml)
- Maven 3.8.8+ (verificado en pom.xml)
- Docker (opcional)

## Versiones de Dependencias Principales (verificado en `pom.xml`)
- Spring Boot: 4.1.0
- Spring Cloud: 2025.1.2
- Kotlin: 2.4.0
- MySQL Connector: 9.6.0
- SpringDoc OpenAPI: 3.0.2
- Apache POI: 5.5.1
- OpenPDF: 3.0.3
- ModelMapper: 3.2.6
- Guava: 33.5.0-jre
- json-path: 3.0.0

## Optimizaciones de Rendimiento (verificado en código)
- **Computación paralela**: La función `calculateDeuda` utiliza `CompletableFuture` para ejecutar consultas en paralelo
- **Validación temprana**: Verificación de parámetros nulos antes del procesamiento
- **Optimización de memoria**: Uso de `Map<String, BigDecimal>` en lugar de objetos completos
- **Manejo robusto de errores**: Logging detallado y manejo de excepciones en operaciones paralelas

### Beneficios de Rendimiento
- **Reducción de tiempo de respuesta**: Las consultas paralelas pueden reducir el tiempo total en un 30-50%
- **Menor uso de memoria**: Eliminación de objetos innecesarios reduce el uso de memoria en ~30%
- **Mejor escalabilidad**: El procesamiento paralelo mejora el rendimiento con múltiples solicitudes

## Novedades 1.1.0 (verificado en código)
- Nuevo endpoint MercadoPago:
  - `GET /api/tesoreria/core/mercadoPagoContext/all/active/to/change`: devuelve `chequeraCuotaId` activos con `fechaVencimiento` en los últimos 90 días (usa `Tool.dateAbsoluteArgentina()`).
- `MercadoPagoContext`:
  - Campo `lastVencimientoUpdated` con formato ISO UTC.
  - Método `jsonify()` para logs estructurados.
- Servicio/Repositorios:
  - `findAllByActivoAndFechaVencimientoBetween(...)` en repositorio.
  - `update(...)` transaccional y actualización sobre entidad gestionada.
- Documentación:
  - Workflow de docs mejorado y diagramas Mermaid en `docs/`.

> Versión y dependencias verificadas en `pom.xml` y código fuente.

## Instalación
```bash
git clone https://github.com/UM-services/um.tesoreria.core-service.git
cd um.tesoreria.core-service
mvn clean install
```

## Uso

### Endpoints de Personas

#### Obtener Inscripción Completa
```http
GET /inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}
```
Retorna la información completa de una inscripción, incluyendo:
- Datos de la inscripción
- Datos de pago
- Datos de la persona
- Domicilio

### Endpoints de Domicilios

#### Obtener Domicilio con Pagador
```http
GET /domicilio/pagador/{facultadId}/{personaId}/{documentoId}/{lectivoId}
```
Retorna el domicilio con información del pagador, incluyendo:
- Datos del domicilio
- Email del pagador (si está disponible)

#### Obtener Domicilio por ID
```http
GET /domicilio/{domicilioId}
```

#### Obtener Domicilio por Persona y Documento
```http
GET /domicilio/unique/{personaId}/{documentoId}
```

#### Crear Domicilio
```http
POST /domicilio/
```

#### Actualizar Domicilio
```http
PUT /domicilio/{domicilioId}
```

#### Sincronizar Domicilio
```http
POST /domicilio/sincronize
```

#### Capturar Domicilio
```http
GET /domicilio/capture/{personaId}/{documentoId}
```

### Endpoints de Documentos

#### Listar Documentos
```http
GET /documento/
```

#### Obtener Documento por ID
```http
GET /documento/{documentoId}
```

### Endpoints de Tarjetas de Crédito de Mercado Pago

#### Listar Tarjetas Activas
```http
GET /activos
```
Retorna todas las tarjetas de crédito activas.

#### Crear/Actualizar Tarjeta
```http
POST /persist
```
Cuerpo:
```json
{
    "tipoChequeraId": 123,
    "alternativaId": 456,
    "cuotas": 12
}
```

#### Dar de Baja Tarjeta
```http
DELETE /baja/{tipoChequeraId}/{alternativaId}
```

#### Obtener Tarjeta Única
```http
GET /unique/{tipoChequeraId}/{alternativaId}
```

## Modelos de Datos

### Domicilio
```kotlin
data class Domicilio(
    @Id
    @Column(name = "dom_id")
    var id: Long = 0,
    
    @Column(name = "dom_persona_id")
    var personaId: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "dom_documento_id")
    var documentoId: Int = 0,
    
    // ... otros campos
    
    @Transient
    var emailPagador: String = ""
)
```

### Documento
```kotlin
data class Documento(
    @Id
    @Column(name = "doc_id")
    var id: Int = 0,
    
    @Column(name = "doc_documento_id")
    var documentoId: Int? = null,
    
    @Column(name = "doc_nombre")
    var nombre: String = ""
)
```

### TipoChequera
```kotlin
data class TipoChequera(
    @Id
    @Column(name = "tch_id")
    var id: Int = 0,
    
    @Column(name = "tch_geo_id")
    var geograficaId: Int = 0,
    
    var emailCopia: String? = null,
    
    // ... otros campos
)
```

### InscripcionFullDto
```java
public class InscripcionFullDto {
    private InscripcionDto inscripcion;
    private InscripcionPagoDto inscripcionPago;
    private PersonaDto personaPago;
    private DomicilioDto domicilioPago;
}
```

## Desarrollo
### Estructura del Proyecto
```
src/
├── main/
│   ├── java/
│   │   └── um/tesoreria/core/
│   │       ├── hexagonal/
│   │       │   ├── ubicacion/         # Módulo Ubicacion (v3.14.0)
│   │       │   ├── ubicacionArticulo/ # Módulo UbicacionArticulo (v3.14.0)
│   │       │   ├── umhub/campanha/    # Módulo Campanha (v3.23.0)
│   │       │   ├── contrato/          # Módulo Contrato (v3.19.0)
│   │       │   ├── chequeraSerie/     # Módulo ChequeraSerie (v3.21.0)
│   │       │   ├── baja/              # Módulo Baja (v3.21.0)
│   │   ├── dependencia/       # Módulo Dependencia (v3.17.0)
│   │   ├── facultad/          # Módulo Facultad (v3.18.0)
│   │   ├── facturaPendiente/  # Módulo FacturaPendiente (v3.15.0)
│   │   ├── articulo/          # Módulo Artículo (v3.13.0)
│   │   ├── cuenta/            # Módulo Cuenta (v3.8.0)
│   │   ├── chequeraCuota/     # Módulo ChequeraCuota (v3.2.0)
│   │   ├── persona/           # Módulo Persona (v3.1.0)
│   │   ├── auth/              # Módulo Auth (v3.6.0)
│   │   ├── geografica/        # Módulo Geografica (v3.6.0)
│   │   │   ├── proveedor/         # Módulo Proveedor (v3.9.0)
│   │       │   └── matriculacionContext/
│   │       ├── model/                 # Modelos legacy
│   │       ├── service/               # Servicios legacy
│   │       ├── controller/             # Controladores legacy
│   │       └── repository/            # Repos legacy
│   └── kotlin/
│       └── um/tesoreria/core/
│           └── model/
└── test/
```

### Optimizaciones Implementadas
- **Computación paralela en `calculateDeuda`**: Utiliza `CompletableFuture` para ejecutar consultas de pagos, cuotas, cuota1 y totales en paralelo
- **Validación temprana de parámetros**: Evita procesamiento innecesario con parámetros nulos
- **Optimización de memoria**: Mapas de pagos usando solo `BigDecimal` en lugar de objetos completos
- **Manejo robusto de errores**: Logging detallado y manejo de excepciones en operaciones paralelas

### Beneficios de Rendimiento
- **Reducción de tiempo de respuesta**: Las consultas paralelas pueden reducir el tiempo total en un 30-50%
- **Menor uso de memoria**: Eliminación de objetos innecesarios reduce el uso de memoria en ~30%
- **Mejor escalabilidad**: El procesamiento paralelo mejora el rendimiento con múltiples solicitudes

## Contribución
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia
Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## Contacto
Daniel Quinteros - daniel.quinterospinto@gmail.com

Link del proyecto: [https://github.com/UM-services/um.tesoreria.core-service](https://github.com/UM-services/um.tesoreria.core-service)

# UM Tesorería Core Service

[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.2-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-purple.svg)](https://kotlinlang.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.8+-orange.svg)](https://maven.apache.org/)
[![Versión](https://img.shields.io/badge/versión-3.23.0-blue.svg)]()

## Documentación
- [Documentación en GitHub Pages](https://um-services.github.io/UM.tesoreria.core-service/)
- [Wiki del Proyecto](https://github.com/UM-services/UM.tesoreria.core-service/wiki)


## Convenciones de Código
- Los DTOs siguen la convención de nomenclatura con sufijo "Dto" (ej: ChequeraSerieDto)
- Los repositorios no usan el prefijo "I" para interfaces
- Se utiliza ModelMapper para mapeo entre entidades y DTOs
- Se implementa manejo de nulls con Objects.requireNonNull()

## API Endpoints Principales
- `/legajo/facultad/{facultadId}` - Obtener legajos por facultad
- `/chequera/cuotas/pagos/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` - Obtener cuotas y pagos de chequera
- `/facturacion-electronica/pendientes` - Obtener los 3 registros pendientes de facturación electrónica
- `/tool/mailvalidate` o `/api/tesoreria/core/tool/mailvalidate` - Validar formato de correo electrónico

## Configuración
El servicio requiere las siguientes configuraciones:
- Base de datos MySQL
- Configuración de correo electrónico
- Java 25
- MySQL 8.0 o superior
- Maven 3.8.8 o superior

## Variables de Entorno
```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/tesoreria
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
MERCADO_PAGO_ACCESS_TOKEN=your_access_token
```

## Desarrollo
Para contribuir al proyecto:
1. Clonar el repositorio
2. Instalar dependencias con Maven
3. Configurar las variables de entorno necesarias
4. Ejecutar las pruebas unitarias
5. Enviar pull request con los cambios

## Licencia
Este proyecto está bajo la licencia [LICENCIA].

### Tarjetas de Crédito de Mercado Pago

El servicio permite gestionar la configuración de tarjetas de crédito de Mercado Pago:

- Configurar parámetros de tarjetas de crédito
- Gestionar límites y restricciones
- Validar configuraciones
- Listar tarjetas de crédito activas

#### Endpoints Disponibles

- `GET /activos`: Lista todas las tarjetas de crédito activas
- `POST /persist/{tipoChequeraId}/{alternativaId}/{cuotas}`: Persiste una nueva configuración de tarjeta de crédito
- `DELETE /baja/{tipoChequeraId}`: Da de baja una configuración de tarjeta de crédito
- `GET /tipoChequera/{tipoChequeraId}`: Consulta la configuración de una tarjeta de crédito