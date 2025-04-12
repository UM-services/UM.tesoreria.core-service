# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Sistema de verificación de mensajes para chequeras
- Logging mejorado con Log4j2
- Nuevo endpoint para consulta de registros de verificación de chequeras
- Modo de prueba para envío de correos

### Changed
- Actualización de dependencias:
  - Spring Boot 3.4.3 → 3.4.4
  - Kotlin 2.1.10 → 2.1.20
  - Spring Cloud 2024.0.0 → 2024.0.1
  - Springdoc OpenAPI 2.8.5 → 2.8.6
  - Guava 33.4.6-jre → 33.4.7-jre
  - Apache POI 5.4.0 → 5.4.1
- Mejora en el ordenamiento de resultados de MercadoPago (orden descendente por ID)
- Optimización de código en servicios de sincronización
- Mejora en el manejo de excepciones en controladores

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