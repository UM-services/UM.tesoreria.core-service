# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-03-19

### Añadido
- Nuevo endpoint `/legajo/facultad/{facultadId}` para obtener legajos por facultad
- Nuevo endpoint `/chequera/cuotas/pagos/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` para obtener cuotas y pagos de chequera
- Nuevo DTO `ChequeraCuotaPagosDto` para manejar cuotas con sus pagos asociados
- Nuevo DTO `ChequeraPagoDto` para representar pagos individuales
- Nuevo DTO `CuotaPeriodoDto` para manejar periodos de cuotas
- Relación entre Legajo y Carrera
- Método `findAllByFacultadId` en LegajoService
- Método `findAllCuotaPagosByChequera` en ChequeraService
- Método `findCuotaPeriodosByLectivoId` en ChequeraCuotaRepository

### Cambiado
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

### Removido
- Eliminación de RabbitMQ y colas de mensajes
- Eliminación de configuración JPA redundante
- Eliminación de dependencias no utilizadas:
  - disruptor
  - spring-boot-starter-amqp
  - spring-tx

### Corregido
- Problemas de nomenclatura inconsistente en DTOs
- Manejo de valores nulos en fechas y otros campos
- Formateo inconsistente en el código

### Seguridad
- Mejorado el manejo de datos sensibles en DTOs
- Implementado mejor manejo de nulls para prevenir NPEs

### Documentación
- Actualizado README con nueva estructura y convenciones
- Documentados nuevos endpoints y funcionalidades
- Actualizada la documentación de la API

## [0.1.0] - 2024-01-01
### Añadido
- Versión inicial del servicio
- Configuración básica de Spring Boot
- Integración con base de datos
- Endpoints básicos para gestión de chequeras

## [Unreleased]

### Added
- Nuevo controlador `TipoChequeraMercadoPagoCreditCardController` para manejar operaciones relacionadas con tarjetas de crédito de Mercado Pago
- Nueva entidad `TipoChequeraMercadoPagoCreditCard` para almacenar información de configuración de tarjetas de crédito
- Nuevo repositorio `TipoChequeraMercadoPagoCreditCardRepository` para operaciones de base de datos
- Nuevo servicio `TipoChequeraMercadoPagoCreditCardService` para la lógica de negocio
- Sistema de verificación de mensajes para chequeras
- Mejoras en el sistema de logging
- Nuevo endpoint para verificación de registros
- Nuevo endpoint POST `/persist/{tipoChequeraId}/{alternativaId}/{cuotas}` para persistir configuraciones de tarjetas de crédito
- Nuevo endpoint DELETE `/baja/{tipoChequeraId}` para dar de baja configuraciones de tarjetas de crédito

### Changed
- Actualización de dependencias
- Optimización en el código de servicios
- Modificación del endpoint `/facturacion-electronica/pendientes` para retornar 3 registros en lugar de 100
- Agregada ruta alternativa `/api/tesoreria/core/tool` para el endpoint de validación de correo
- Mejorada la inyección de dependencias en ToolController usando constructor
- Renombrado el método `mailvalidate` a `mailValidate` para seguir convenciones de nomenclatura
- Renombrado `ITipoChequeraRepository` a `TipoChequeraRepository` siguiendo las convenciones de nomenclatura
- Mejorado el código en `TipoChequeraService` usando referencias de método y constructor injection
- Actualizada la versión de Spring Boot a 3.4.5
- Actualizada la versión de MySQL Connector a 9.3.0
- Actualizada la versión de ModelMapper a 3.2.3
- Eliminado el campo `defaultPaymentMethodId` del modelo TipoChequeraMercadoPagoCreditCard
- Mejorado el manejo de UUID en el servicio de tarjetas de crédito

### Removed
- Eliminación de RabbitMQ y colas de mensajes
- Eliminación de configuración JPA redundante
- Eliminación de dependencias no utilizadas:
  - disruptor
  - spring-boot-starter-amqp
  - spring-tx

### Fixed
- Manejo de casos nulos en consultas de repositorio
- Mejora en el manejo de excepciones en operaciones de chequeras
- Corrección en el manejo de correos electrónicos
- Mejora en el logging de errores
- Corregido el manejo de excepciones en `TipoChequeraService` para usar referencias de método
- Mejorada la legibilidad del código en la actualización de `TipoChequera`
- Mejorado el manejo de excepciones en el servicio de tarjetas de crédito
- Optimizada la lógica de persistencia y baja de configuraciones

### Security
- Mejorado el manejo de datos sensibles en DTOs
- Implementado mejor manejo de nulls para prevenir NPEs

### Documentación
- Actualizado README con nueva estructura y convenciones
- Documentados nuevos endpoints y funcionalidades
- Actualizada la documentación de la API

## [0.0.1-SNAPSHOT] - 2024-02-22

### Added
- Implementación inicial del servicio core de tesorería
- Gestión de pagos y chequeras
- Procesamiento de pagos de MercadoPago
- Manejo de asientos contables
- Gestión de cuotas y facturación
- Caché distribuido con Caffeine
- Documentación automática con OpenAPI/Swagger
- Soporte para RabbitMQ
- Transacciones distribuidas
- Métricas con Prometheus
- Configuración de CI/CD con GitHub Actions
- Documentación en GitHub Pages
- Soporte para Docker

### Changed
- N/A (versión inicial)

### Deprecated
- N/A (versión inicial)

### Removed
- N/A (versión inicial)

### Fixed
- N/A (versión inicial)

### Security
- N/A (versión inicial) 