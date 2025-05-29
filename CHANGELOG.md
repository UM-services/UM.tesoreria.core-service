# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Nuevo endpoint GET `/activos` en TipoChequeraMercadoPagoCreditCardController
- Nuevo método `findAllByActive` en TipoChequeraMercadoPagoCreditCardRepository
- Nuevo método `findAllActivos` en TipoChequeraMercadoPagoCreditCardService
- Nuevo endpoint GET `/inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}` en PersonaController
- Nuevos DTOs para inscripciones:
  - `InscripcionDto`
  - `InscripcionFullDto`
  - `InscripcionPagoDto`
- Nuevo campo `emailCopia` en modelo `TipoChequera`

### Changed
- Actualizado springdoc-openapi-starter-webmvc-ui a versión 2.8.8
- Modificado endpoint DELETE `/baja/{tipoChequeraId}` a `/baja/{tipoChequeraId}/{alternativaId}`
- Modificado endpoint GET `/tipoChequera/{tipoChequeraId}` a `/unique/{tipoChequeraId}/{alternativaId}`
- Actualizada restricción única en TipoChequeraMercadoPagoCreditCard para incluir alternativaId
- Actualizado manejo de excepciones para incluir alternativaId
- Actualizado openpdf de 2.0.3 a 2.0.4
- Mejorado manejo de nulos en `InscripcionFacultadConsumer` usando `Objects.requireNonNull`
- Actualizado `TipoChequeraService` para incluir `emailCopia` en la creación

## [1.0.0] - 2024-03-19

### Added
- Nuevo endpoint `/legajo/facultad/{facultadId}` para obtener legajos por facultad
- Nuevo endpoint `/chequera/cuotas/pagos/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` para obtener cuotas y pagos de chequera
- Nuevo DTO `ChequeraCuotaPagosDto` para manejar cuotas con sus pagos asociados
- Nuevo DTO `ChequeraPagoDto` para representar pagos individuales
- Nuevo DTO `CuotaPeriodoDto` para manejar periodos de cuotas
- Relación entre Legajo y Carrera
- Método `findAllByFacultadId` en LegajoService
- Método `findAllCuotaPagosByChequera` en ChequeraService
- Método `findCuotaPeriodosByLectivoId` en ChequeraCuotaRepository

### Changed
- Actualización de Java 17 a Java 21
- Refactorización de nombres de DTOs para seguir la convención de nomenclatura:
  - `ChequeraSerieDTO` -> `ChequeraSerieDto`
  - `ChequeraCuotaDTO` -> `ChequeraCuotaDto`
  - `ArancelTipoDTO` -> `ArancelTipoDto`
  - `DomicilioDTO` -> `DomicilioDto`
  - `FacultadDTO` -> `FacultadDto`
  - `GeograficaDTO` -> `GeograficaDto`
  - `LectivoDTO` -> `LectivoDto`
  - `PersonaDTO` -> `PersonaDto`
  - `ProductoDTO` -> `ProductoDto`
  - `TipoChequeraDTO` -> `TipoChequeraDto`
- Renombrado de interfaces de repositorios:
  - `IChequeraCuotaRepository` -> `ChequeraCuotaRepository`
  - `ILegajoRepository` -> `LegajoRepository`
- Mejorado el manejo de nulls usando `Objects.requireNonNull()`
- Actualizado el formateo del código para mayor legibilidad
- Eliminados imports no utilizados

### Removed
- Eliminación de RabbitMQ y colas de mensajes
- Eliminación de configuración JPA redundante
- Eliminación de dependencias no utilizadas:
  - disruptor
  - spring-boot-starter-amqp
  - spring-tx

### Fixed
- Problemas de nomenclatura inconsistente en DTOs
- Manejo de valores nulos en fechas y otros campos
- Formateo inconsistente en el código

### Security
- Mejorado el manejo de datos sensibles en DTOs
- Implementado mejor manejo de nulls para prevenir NPEs

### Documentation
- Actualizado README con nueva estructura y convenciones
- Documentados nuevos endpoints y funcionalidades
- Actualizada la documentación de la API

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