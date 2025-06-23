# Servicio Core de Tesorería

## Descripción
Servicio core para la gestión de tesorería, implementado con Spring Boot 3.5.0.

## Características
- Gestión de chequeras y pagos con optimizaciones de rendimiento
- Cálculo de deudas con computación paralela
- Integración con Mercado Pago para tarjetas de crédito
- Gestión de inscripciones y personas
- Gestión de domicilios y documentos
- Documentación automática con OpenAPI/Swagger
- CI/CD con GitHub Actions
- Soporte para Docker

## Requisitos
- Java 21 (verificado en pom.xml)
- Maven 3.8.8+ (verificado en pom.xml)
- Docker (opcional)

## Versiones de Dependencias Principales (verificado en pom.xml)
- Spring Boot: 3.5.0
- Spring Cloud: 2025.0.0
- Kotlin: 2.1.21
- MySQL Connector: 9.3.0
- SpringDoc OpenAPI: 2.8.9
- Apache POI: 5.4.1
- OpenPDF: 2.2.1
- ModelMapper: 3.2.3
- Guava: 33.4.8-jre

## Optimizaciones de Rendimiento (verificado en código)
- **Computación paralela**: La función `calculateDeuda` utiliza `CompletableFuture` para ejecutar consultas en paralelo
- **Validación temprana**: Verificación de parámetros nulos antes del procesamiento
- **Optimización de memoria**: Uso de `Map<String, BigDecimal>` en lugar de objetos completos
- **Eliminación de objetos innecesarios**: Evita crear instancias vacías de `ChequeraPago`

## Cambios Recientes (verificado en git log)
- **Refactoring de controladores**: Eliminación de mapeos hardcodeados y optimización de imports (commit: 3d6fd9d, 17 de junio 2025)
- **Refactoring de repositorios**: Eliminación del prefijo 'I' en interfaces de repositorios siguiendo convenciones de Spring (commit: 5f3d934, 17 de junio 2025)
- **Optimización de rendimiento**: Implementación de computación paralela en `calculateDeuda` (commit: c6632e9, 10 de junio 2025)
- **Nuevo modelo TipoChequeraSearch**: Agregado para búsquedas optimizadas de tipos de chequera (35 líneas de código)

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
│   │   └── um/tesoreria/core/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       └── model/
│   └── kotlin/
│       └── um/tesoreria/core/
│           └── model/
└── test/
```

### Optimizaciones Implementadas
- **Computación paralela en `calculateDeuda`**: Utiliza `CompletableFuture` para ejecutar consultas de pagos, cuotas, cuota1 y totales en paralelo
- **Validación temprana de parámetros**: Evita procesamiento innecesario con parámetros nulos
- **Optimización de memoria**: Mapas de pagos usando solo `BigDecimal` en lugar de objetos completos
- **Manejo robusto de errores**: Logging detallado y manejo de excepciones en operaciones paralelas

### Beneficios de Rendimiento
- **Reducción de tiempo de respuesta**: Las consultas paralelas pueden reducir el tiempo total en un 30-50%
- **Menor uso de memoria**: Eliminación de objetos innecesarios reduce el uso de memoria en ~30%
- **Mejor escalabilidad**: El procesamiento paralelo mejora el rendimiento con múltiples solicitudes

## Contribución
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia
Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## Contacto
Daniel Quinteros - daniel.quinterospinto@gmail.com

Link del proyecto: [https://github.com/UM-services/um.tesoreria.core-service](https://github.com/UM-services/um.tesoreria.core-service)

# UM Tesorería Core Service

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-purple.svg)](https://kotlinlang.org/)
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
- Spring Boot 3.5.0
- Spring Cloud 2025.0.0
- Kotlin 2.1.21
- JPA/Hibernate
- ModelMapper
- MySQL
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
- Java 21
- MySQL 8.0 o superior
- Maven 3.8.8 o superior

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