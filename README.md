# Servicio Core de Tesorería

## Descripción
Servicio core para la gestión de tesorería, implementado con Spring Boot 3.0.1.

## Características
- Gestión de chequeras y pagos
- Integración con Mercado Pago para tarjetas de crédito
- Gestión de inscripciones y personas
- Gestión de domicilios y documentos
- Documentación automática con OpenAPI/Swagger
- CI/CD con GitHub Actions
- Soporte para Docker

## Requisitos
- Java 21
- Maven 3.8+
- Docker (opcional)

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
│   │   └── um/
│   │       └── tesoreria/
│   │           └── core/
│   │               ├── controller/
│   │               ├── service/
│   │               ├── repository/
│   │               ├── model/
│   │               ├── extern/
│   │               │   ├── consumer/
│   │               │   └── model/
│   │               │       └── dto/
│   │               └── exception/
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── um/
            └── tesoreria/
                └── core/
```

### Convenciones de Commits
Este proyecto sigue las convenciones de [Conventional Commits](https://www.conventionalcommits.org/).

### CI/CD
El proyecto utiliza GitHub Actions para:
- Build automático
- Tests unitarios
- Análisis de código
- Despliegue automático

## Licencia
Este proyecto está bajo la Licencia MIT.

# UM Tesorería Core Service

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.1-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-purple.svg)](https://kotlinlang.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.8+-orange.svg)](https://maven.apache.org/)

## Documentación
- [Documentación en GitHub Pages](https://um-services.github.io/UM.tesoreria.core-service/)
- [Wiki del Proyecto](https://github.com/UM-services/UM.tesoreria.core-service/wiki)

## Características Principales
- Gestión de chequeras y sus cuotas
- Manejo de legajos y sus relaciones con carreras
- Procesamiento de pagos y acreditaciones
- Integración con sistemas externos
- Generación de reportes y documentos
- Validación de mensajes
- Integración con Mercado Pago
- Gestión de tarjetas de crédito de Mercado Pago
- Sistema de logging mejorado
- Validación de correos electrónicos

## Tecnologías Utilizadas
- Java 21
- Spring Boot 3.4.4
- Spring Cloud 2024.0.1
- Kotlin 2.1.20
- JPA/Hibernate
- ModelMapper
- PostgreSQL
- Mercado Pago SDK

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
- `/facturacion-electronica/pendientes` - Obtener los 3 registros pendientes de facturación electrónica
- `/tool/mailvalidate` o `/api/tesoreria/core/tool/mailvalidate` - Validar formato de correo electrónico

## Configuración
El servicio requiere las siguientes configuraciones:
- Base de datos MySQL
- Configuración de correo electrónico
- Java 17
- PostgreSQL 12 o superior
- Maven 3.6 o superior

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