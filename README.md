# um.tesoreria.core-service

[![Estado del Build](https://github.com/UM-services/UM.tesoreria.core-service/actions/workflows/maven.yml/badge.svg)](https://github.com/UM-services/UM.tesoreria.core-service/actions/workflows/maven.yml)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.1-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-purple.svg)](https://kotlinlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8+-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.8.8+-orange.svg)](https://maven.apache.org/)

Servicio CORE para la arquitectura de microservicios de UM Tesoreria. Este servicio maneja la lógica central del sistema de tesorería, incluyendo la gestión de pagos, chequeras y asientos contables.

## Tecnologías

- Java 21
- Spring Boot 3.4.4
- Spring Cloud 2024.0.1
- Kotlin 2.1.20
- MySQL 8+
- Maven 3.8.8+

## Características Principales

- Gestión de pagos y chequeras
- Procesamiento de pagos de MercadoPago
- Manejo de asientos contables
- Gestión de cuotas y facturación
- Sistema de verificación de mensajes para chequeras
- Caché distribuido con Caffeine
- Documentación automática con OpenAPI/Swagger
- Soporte para RabbitMQ
- Transacciones distribuidas
- Métricas con Prometheus
- Logging mejorado con Log4j2
- Modo de prueba para envío de correos

## Requisitos

- JDK 21
- Maven 3.8.8+
- MySQL 8+
- RabbitMQ 3.12+
- Docker (opcional)

## Configuración

1. Clonar el repositorio:
```bash
git clone https://github.com/UM-services/UM.tesoreria.core-service.git
```

2. Configurar la base de datos en `bootstrap.yml`

3. Configurar RabbitMQ en `bootstrap.yml`

4. Configurar el modo de prueba en `bootstrap.yml`:
```yaml
app:
  testing: false  # Cambiar a true para pruebas de correo
```

5. Compilar el proyecto:
```bash
mvn clean install
```