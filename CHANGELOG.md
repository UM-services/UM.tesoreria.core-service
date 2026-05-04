# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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