# UM Tesorería Core Service

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.1-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-purple.svg)](https://kotlinlang.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.8+-orange.svg)](https://maven.apache.org/)

## Documentación
- [Documentación en GitHub Pages](https://um-services.github.io/UM.tesoreria.core-service/)
- [Wiki del Proyecto](https://github.com/UM-services/UM.tesoreria.core-service/wiki)

## Descripción
Servicio core de Tesorería para la Universidad de Mendoza. Este servicio maneja la gestión de chequeras, legajos, pagos y cuotas.

## Características Principales
- Gestión de chequeras y sus cuotas
- Manejo de legajos y sus relaciones con carreras
- Procesamiento de pagos y acreditaciones
- Integración con sistemas externos
- Generación de reportes y documentos

## Tecnologías Utilizadas
- Java 21
- Spring Boot 3.4.4
- Spring Cloud 2024.0.1
- Kotlin 2.1.20
- JPA/Hibernate
- ModelMapper

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/
│   │   └── um/
│   │       └── tesoreria/
│   │           └── core/
│   │               ├── controller/       # Controladores REST
│   │               ├── service/          # Lógica de negocio
│   │               ├── repository/       # Acceso a datos
│   │               ├── model/            # Modelos de datos
│   │               └── configuration/    # Configuraciones
│   └── resources/
└── test/                                # Pruebas unitarias
```

## Convenciones de Código
- Los DTOs siguen la convención de nomenclatura con sufijo "Dto" (ej: ChequeraSerieDto)
- Los repositorios no usan el prefijo "I" para interfaces
- Se utiliza ModelMapper para mapeo entre entidades y DTOs
- Se implementa manejo de nulls con Objects.requireNonNull()

## API Endpoints Principales
- `/legajo/facultad/{facultadId}` - Obtener legajos por facultad
- `/chequera/cuotas/pagos/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` - Obtener cuotas y pagos de chequera

## Configuración
El servicio requiere las siguientes configuraciones:
- Base de datos MySQL
- Configuración de correo electrónico

## Desarrollo
Para contribuir al proyecto:
1. Clonar el repositorio
2. Instalar dependencias con Maven
3. Configurar las variables de entorno necesarias
4. Ejecutar las pruebas unitarias
5. Enviar pull request con los cambios

## Licencia
Este proyecto está bajo la licencia [LICENCIA].