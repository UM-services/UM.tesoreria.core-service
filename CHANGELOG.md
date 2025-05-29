# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Nuevo endpoint `GET /domicilio/pagador/{facultadId}/{personaId}/{documentoId}/{lectivoId}` para obtener domicilio con información del pagador
- Nuevo campo `emailPagador` en el modelo `Domicilio`
- Constructor con inyección de dependencias en `DocumentoController` y `DomicilioController`
- Renombrado `IDocumentoRepository` a `DocumentoRepository` siguiendo convenciones de Spring
- Renombrado `IDomicilioRepository` a `DomicilioRepository` siguiendo convenciones de Spring

### Changed
- Mejorada la inyección de dependencias en `DomicilioService` usando constructor
- Optimizado el manejo de strings vacíos usando `isEmpty()` en lugar de `equals("")`
- Actualizado el manejo de nulos en `DomicilioService`
- Mejorada la estructura de los modelos eliminando espacios innecesarios
- Actualizada la documentación en README.md

### Removed
- Eliminada la anotación `@Autowired` en favor de inyección por constructor
- Eliminados archivos de configuración obsoletos:
  - `TransactionConfig.java`
  - `RabbitMQConfig.java`
  - `JpaConfig.java`
- Eliminados modelos obsoletos:
  - `ChequeraMessageDto.kt`
  - `CuotaPeriodo.java`
  - `ChequeraCuotaDto.java`
- Eliminado repositorio obsoleto:
  - `IChequeraPagoRepository.java`

## [1.0.0] - 2024-03-19

### Added
- Nuevo endpoint `GET /inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}` para obtener información completa de inscripción
- Nuevos DTOs: `InscripcionDto`, `InscripcionFullDto`, `InscripcionPagoDto`
- Nuevo campo `emailCopia` en el modelo `TipoChequera`
- Actualización de openpdf de 2.0.3 a 2.0.4
- Mejora en el manejo de nulos usando `Objects.requireNonNull`
- Actualización de `TipoChequeraService` para incluir `emailCopia` en la creación
- Documentación actualizada en README y CHANGELOG
- Corrección de versión de Java en README (17 a 21)

### Changed
- Mejorado el manejo de nulos en `InscripcionFacultadConsumer`
- Actualizada la documentación con nuevos endpoints y modelos
- Actualizada la estructura del proyecto

## [0.2.0] - 2024-03-11

### Added
- Soporte para tarjetas de crédito alternativas en Mercado Pago
- Nuevo endpoint para listar tarjetas de crédito activas
- Nuevo endpoint para crear/actualizar tarjetas
- Nuevo endpoint para dar de baja tarjetas
- Nuevo endpoint para obtener tarjeta única

### Changed
- Mejorada la gestión de tarjetas de crédito
- Actualizada la documentación de endpoints
- Actualizada la estructura del proyecto

## [0.1.0] - 2024-01-01
### Added
- Versión inicial del servicio
- Configuración básica de Spring Boot
- Integración con base de datos
- Endpoints básicos para gestión de chequeras

## [0.0.1-SNAPSHOT] - 2022-12-01

### Added
- Implementación inicial del servicio core de tesorería
- Configuración básica de Spring Boot 3.0.1
- Integración con base de datos
- Endpoints básicos para gestión de chequeras
- Documentación con Swagger
- Configuración de CI/CD con GitHub Actions
- Soporte para Docker

### Changed
- Actualización de Spring Boot a 3.0.1
- Mejoras en el manejo de asignación de costos
- Optimización de asientos contables

### Fixed
- Correcciones en asignación de costos
- Mejoras en el manejo de asientos contables
- Optimización de consultas y rendimiento

### Deprecated
- N/A (versión inicial)

### Removed
- N/A (versión inicial)

### Security
- N/A (versión inicial) 