# um.tesoreria.core-service

[![Estado del Build](https://github.com/UM-services/UM.tesoreria.core-service/actions/workflows/maven.yml/badge.svg)](https://github.com/UM-services/UM.tesoreria.core-service/actions/workflows/maven.yml)

Servicio CORE para la arquitectura de microservicios de UM Tesoreria. Este servicio maneja la lógica central del sistema de tesorería, incluyendo la gestión de pagos, chequeras y asientos contables.

## Tecnologías

- Java 21
- Spring Boot 3.4.2
- Spring Cloud 2024.0.0
- Kotlin 2.1.10
- MySQL
- Maven

## Características Principales

- Gestión de pagos y chequeras
- Procesamiento de pagos de MercadoPago
- Manejo de asientos contables
- Gestión de cuotas y facturación
- Caché distribuido con Caffeine
- Documentación automática con OpenAPI/Swagger

## Requisitos

- JDK 21
- Maven 3.8.8+
- MySQL 8+
- Docker (opcional)

## Configuración

1. Clonar el repositorio:
```bash
git clone https://github.com/UM-services/UM.tesoreria.core-service.git
```

2. Configurar la base de datos en `application.properties`

3. Compilar el proyecto:
```bash
mvn clean install
```

## Ejecución

### Local
```bash
mvn spring-boot:run
```

### Docker
```bash
docker build -t um-tesoreria-core .
docker run -p 8080:8080 um-tesoreria-core
```

## Documentación

- [Documentación Detallada](https://um-services.github.io/UM.tesoreria.core-service/project-documentation)
- [Wiki del Proyecto](https://github.com/UM-services/UM.tesoreria.core-service/wiki)
- [OpenAPI/Swagger](http://localhost:8080/swagger-ui.html)

## Monitoreo

El servicio incluye endpoints de Actuator y métricas Prometheus en:
- Health check: `/actuator/health`
- Métricas: `/actuator/prometheus`

## CI/CD

El proyecto utiliza GitHub Actions para:
- Compilación y pruebas automáticas
- Generación de documentación
- Construcción y publicación de imágenes Docker
- Actualización automática de la Wiki

## Contribuir

1. Crear un fork del repositorio
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit de tus cambios (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Licencia

Este proyecto es propiedad de la Universidad de Mendoza.

## Contacto

- **Equipo de Desarrollo** - [UM Services Team](https://github.com/dqmdz)
- **Proyecto** - [UM.tesoreria.core-service](https://github.com/UM-services/UM.tesoreria.core-service)
