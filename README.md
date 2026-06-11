# Data Generator — Latam QA POC

## Descripción
Data Generator es una herramienta diseñada para la generación robusta de datos de prueba de personas naturales y empresas para Latam Airlines. Su propósito principal es alimentar los entornos de pruebas automatizadas QA mediante la creación dinámica de registros únicos, validados bajo estrictas reglas de negocio comerciales de aerolíneas, y permitir su persistencia local en SQLite y su exportación hacia archivos CSV listos para ser consumidos.

## Tecnologías utilizadas
* **Java 11 / 17** (Compilado usando OpenJDK)
* **Maven 3.6+** (Gestor de dependencias y construcción)
* **SQLite JDBC (v3.42.0.0)** (Motor de base de datos local embebida)
* **JavaFaker (v1.0.2)** (Generador aleatorio de datos ficticios)
* **OpenCSV (v5.7.1)** (Procesamiento y volcado de archivos planos CSV)
* **SLF4J + Logback (v1.7.36 / v1.2.11)** (Registro, control y trazabilidad de logs)
* **JavaMail API (v1.6.7)** (Envío automatizado de reportes por correo electrónico)

## Estructura del proyecto
```text
data-generator/
├── src/
│   ├── main/
│   │   ├── java/com/latam/datagenerator/
│   │   │   ├── model/         ← Modelos, interfaces de validación y de documentos (PPO/SOLID)
│   │   │   ├── factory/       ← Factorías de creación de usuarios y determinación de tipos
│   │   │   ├── builder/       ← Constructor fluido de usuarios paso a paso
│   │   │   ├── service/       ← Lógica de generación secuencial, paralela y lógica de negocio
│   │   │   ├── repository/    ← Acceso a datos SQLite (Singleton de base de datos y CRUD)
│   │   │   ├── util/          ← Utilidades de exportación CSV y cliente de correo SMTP
│   │   │   └── Main.java      ← Interfaz de menú de consola e inicio de la aplicación
│   │   └── resources/
│   │       ├── logback.xml    ← Configuración de logs en consola
│   │       └── config.properties  ← Parámetros de base de datos, salida CSV y correo SMTP
│   └── test/
│       └── java/com/latam/datagenerator/  ← Estructura para pruebas automatizadas futuras
├── pom.xml                    ← Configuración de dependencias Maven
└── README.md                  ← Documentación general del proyecto
```

## Principios y patrones implementados

### Pilares OOP
| Pilar | Clase | Descripción breve |
| :--- | :--- | :--- |
| **Abstracción** | [AbstractUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/AbstractUser.java) | Expone una plantilla base abstrayendo los atributos y declarando métodos abstractos (`validate()`, `generateDocument()`). |
| **Encapsulamiento** | [AbstractUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/AbstractUser.java) | Oculta el estado interno mediante atributos privados y expone accesores controlados (Getters y Setters). |
| **Herencia** | [NaturalPersonUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/NaturalPersonUser.java) / [CompanyUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/CompanyUser.java) | Reutilizan los atributos comunes y amplían los comportamientos definidos en la clase abstracta padre. |
| **Polimorfismo** | [CompanyUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/CompanyUser.java) | Sobrescribe de forma personalizada métodos como `getLastName()` para asegurar el apellido vacío y define su propia generación de documentos. |

### Patrones de diseño
| Patrón | Clase | Descripción breve |
| :--- | :--- | :--- |
| **Factory** | [UserFactory](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/factory/UserFactory.java) | Centraliza e independiza la instanciación de objetos `NaturalPersonUser` y `CompanyUser` basándose en el tipo de usuario. |
| **Builder** | [UserBuilder](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/builder/UserBuilder.java) | Permite construir instancias de `AbstractUser` de forma fluida paso a paso con asignaciones altamente legibles. |
| **Singleton** | [DatabaseManager](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/repository/DatabaseManager.java) | Garantiza una única instancia del administrador de conexiones hacia la base de datos SQLite. |

### Principios SOLID
| Principio | Dónde se aplica | Cómo se aplica |
| :--- | :--- | :--- |
| **S** (Single Responsibility) | [UserRecord](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/UserRecord.java) / [DocumentGeneratorService](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/service/DocumentGeneratorService.java) | Cada clase tiene un único motivo de cambio (UserRecord es un simple DTO y DocumentGeneratorService solo genera caracteres de documentos). |
| **O** (Open/Closed) | [AbstractUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/AbstractUser.java) | Está abierta para que se creen nuevas clases de usuario (como corporaciones internacionales o viajeros frecuentes) pero cerrada a la modificación de su diseño core. |
| **L** (Liskov Substitution) | [NaturalPersonUser](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/NaturalPersonUser.java) | Se pueden emplear instancias de las subclases indiferentemente en lugar del tipo base `AbstractUser` sin romper el código cliente. |
| **I** (Interface Segregation) | [Validatable](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/Validatable.java) y [Documentable](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/model/Documentable.java) | Interfaces pequeñas y enfocadas a una sola acción, evitando interfaces gordas con métodos que las subclases no implementan. |
| **D** (Dependency Inversion) | [DataGeneratorService](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/java/com/latam/datagenerator/service/DataGeneratorService.java) | Depende de la abstracción `UserRepository` e inyecta sus utilidades en lugar de instanciar componentes concretos acoplados. |

## Reglas de negocio implementadas
1. **Rango de Edad**: Las personas naturales deben tener edades comprendidas estrictamente entre 11 y 79 años (mayores o menores).
2. **Apellido en blanco para Empresas**: Las empresas (COMPANY) no poseen apellido, el cual se inicializa y se fuerza a cadena vacía `""`.
3. **Documentación de Empresas**: El identificador de documento para empresas debe comenzar de forma obligatoria con el dígito "9" y posee longitud libre.
4. **Documentación de Menor de Edad (11 a 17 años)**: Su número de documento de identificación debe corresponder a un valor mayor o igual a `11000000`.
5. **Documentación de Mayor de Edad (18 a 79 años)**: Su documento de identificación debe poseer una longitud obligatoria de entre 9 y 11 dígitos.
6. **Idioma Extranjero**: Si el país de origen de la persona o empresa no es Colombia, el idioma de comunicación y contacto no puede ser Español.
7. **Unicidad de Datos**: Se valida en base de datos y memoria que no existan duplicados en los números de identificación ni en la composición del nombre y apellido de los usuarios.

## Cómo ejecutar

### Requisitos previos
* Java 11+
* Maven 3.6+

### Pasos
1. Clonar el repositorio localmente en tu máquina.
2. Construir e instalar las dependencias con Maven ejecutando en consola:
   ```bash
   mvn clean install
   ```
3. Ejecutar la aplicación parametrizando la cantidad de usuarios ficticios por línea de comando (ejemplo para 20 registros):
   ```bash
   mvn exec:java -Dexec.mainClass="com.latam.datagenerator.Main"
   ```

### Opciones del menú
Al iniciar el aplicativo, se despliega una interfaz interactiva de consola con las siguientes opciones:
* **[1] Generar nuevos registros**: Limpia la base de datos y memoria caché para asegurar sincronización total con el CSV de la prueba. Solicita al usuario la cantidad de registros a crear y decide automáticamente si usar ejecución paralela o secuencial según el volumen (paralela para >= 100 registros). Guarda en SQLite, exporta a CSV y da la opción de enviar el reporte por e-mail.
* **[2] Ver registros existentes en BD**: Recupera de la base de datos local todos los datos y los imprime en consola organizados en formato tabla.
* **[3] Eliminar todos los registros**: Elimina el historial y todos los datos almacenados en la tabla `users` tras confirmar la operación.
* **[4] Exportar registros existentes a CSV**: Vuelca la base de datos completa hacia un archivo plano CSV local sin generar nuevos datos ficticios.
* **[5] Salir**: Cierra las conexiones activas con la base de datos SQLite y finaliza el programa de manera segura.
 
## Bonus implementados
- [x] **Ejecución en paralelo**: Generación multihilo concurrente utilizando `ExecutorService` con hilos basados en los núcleos activos del CPU y conjuntos concurrentes (`ConcurrentHashMap.newKeySet()`) para preservar la integridad de unicidad en memoria. Se activa automáticamente cuando la cantidad a generar es igual o superior a 100 registros.
- [x] **Gestión de datos históricos**: Métodos de administración para consulta y eliminación por antigüedad (`deleteByCreatedBefore`), además de visualización interactiva y vaciado completo.
- [x] **Envío por correo**: Soporte de cliente SMTP integrado con **JavaMail API** para enviar el reporte de datos a cualquier casilla de correo electrónico. Ya se encuentra configurado y habilitado por defecto usando Gmail SMTP en [config.properties](file:///Users/Marlopch/.gemini/antigravity-ide/scratch/data-generator/src/main/resources/config.properties).

## Resultado esperado
Al ejecutarse el proceso de generación:
1. Se crea un archivo de base de datos SQLite local `data-generator.db` (a menos que se modifique su ruta) con la tabla `users` cargada y actualizada.
2. Se exporta un reporte estructurado y delimitado por comas (CSV) en la ruta `output/datos_latam.csv` conteniendo las columnas de usuario correspondientes.

## Autor
Senior QA Automation Engineer — Latam QA
