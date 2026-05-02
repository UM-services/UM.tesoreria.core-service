# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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