# 🤖 agent.md — Reglas del Agente de IA para este Workspace
> **Proyectos cubiertos:** `data-generator/` · `latam-automation/`  
> **Aplicar en:** generación, modificación, eliminación y actualización de código.  
> Toda IA que opere en este workspace **DEBE cumplir estas reglas antes de escribir una sola línea.**

---

## 1. 🧱 Principios SOLID (No negociables)

### S — Single Responsibility
- **Una clase = una razón para cambiar.** Si una clase hace más de una cosa, divídela.
- No mezclar lógica de negocio con acceso a datos, ni presentación con cómputo.
- Clases de servicio **no** deben leer archivos ni parsear CSV directamente.

### O — Open/Closed
- Preferir extensión vía herencia/interfaces sobre modificar código existente.
- Evitar cadenas `if/else if` o `switch` que crezcan con nuevos tipos. Usar polimorfismo o Strategy Pattern.
- Cada nuevo `UserType`, regla de validación o canal de exportación debe poder añadirse **sin tocar** clases existentes.

### L — Liskov Substitution
- Toda subclase debe poder sustituir a su clase padre sin romper el programa.
- Las implementaciones de `AbstractUser` (`NaturalPersonUser`, `CompanyUser`) deben honor todos los contratos de `Validatable` y `Documentable`.
- **Prohibido** que una subclase lance excepciones no declaradas o retorne `null` donde la superclase garantiza un valor.

### I — Interface Segregation
- Interfaces pequeñas y focalizadas. Máximo 3-4 métodos por interfaz.
- **Prohibido** crear interfaces "dios" con 10+ métodos.
- Si una clase implementa una interfaz pero deja métodos vacíos → la interfaz está mal diseñada.

### D — Dependency Inversion
- Los servicios de alto nivel dependen de **abstracciones** (interfaces), no de implementaciones concretas.
- Toda dependencia no trivial debe **inyectarse por constructor**, no instanciarse con `new` dentro de la clase.
- `DocumentGeneratorService` debe inyectarse en `DataGeneratorService`, no crearse internamente.

---

## 2. 🧠 KISS — Keep It Simple, Stupid

- **La solución más simple que funcione es la correcta.** No anticipar complejidad innecesaria.
- Métodos de máximo **20-25 líneas** de cuerpo. Si excede → extraer método privado.
- Evitar condicionales anidados de más de 2 niveles. Usar early-return o guard clauses.
- **No usar FQN (Fully Qualified Names) inline** — siempre usar imports limpios.
- Preferir ternario sobre `if/else` de 2 ramas simples:
  ```java
  // ✅ KISS
  String ciudad = "Extranjero".equalsIgnoreCase(tipo) ? "Lima" : "Bogota";
  // ❌ innecesariamente verboso
  if ("Extranjero".equalsIgnoreCase(tipo)) { ciudad = "Lima"; } else { ciudad = "Bogota"; }
  ```

---

## 3. 🚫 YAGNI — You Aren't Gonna Need It

- **No escribir código que no se use hoy.** No implementar funcionalidad "por si acaso".
- Antes de agregar un método nuevo, verificar si ya existe algo similar (`findByField`, `normalizarCiudad`, etc.).
- Si un método existe pero **no se invoca desde ningún lugar**, marcarlo con `@Deprecated` o eliminarlo con justificación documentada.
- Los métodos YAGNI identificados que **requieren decisión del equipo** antes de eliminarse:
  - `UserRepository.existsDocument()` — validación duplicada de lo que hace el Set en memoria.
  - `UserRepository.existsFullName()` — ídem.
  - `UserRepository.deleteByCreatedBefore()` — no expuesto en ningún menú ni servicio.

---

## 4. ♻️ DRY — Don't Repeat Yourself

- **Si el mismo bloque de código aparece 2+ veces → extraer método/clase.**
- Strings literales repetidos → constante `static final`.
- Lógica SQL repetida → método privado genérico (ver `findByField()`).
- Aserciones de Serenity repetidas → método privado en el StepDefinition (ver `verificarCargaResultados()`).
- Queries de BD repetidas → repositorio centralizado, no duplicar en helpers de test.

---

## 5. 📦 Organización de Paquetes

```
com.latam.datagenerator
├── model/          ← Entidades del dominio (AbstractUser, UserType, interfaces)
├── builder/        ← Constructores fluidos (UserBuilder)
├── factory/        ← Fábricas de objetos (UserFactory, UserTypeFactory)
├── repository/     ← Acceso a datos (UserRepository, DatabaseManager)
├── service/        ← Lógica de negocio (DataGeneratorService, DocumentGeneratorService)
└── util/           ← Utilidades transversales (CsvExporter, MailSender)

com.latam.automation
├── tasks/          ← Screenplay Tasks (BuscarVuelo, SeleccionarVuelo, IngresarPasajero)
├── ui/             ← Page Objects / UI Elements (LatamSearchPage, LatamCheckoutPage)
├── stepdefinitions/ ← Glue code Cucumber (BuscarVueloStepDefinitions)
└── util/           ← Helpers de prueba (DataHelper)
```

**Regla:** No crear clases fuera de estos paquetes sin justificación documentada.

---

## 6. ☕ Estándares de Código Java

### Imports
- **Prohibidos los imports con `*` (wildcard).**
- Eliminar imports no usados antes de cada commit.
- Orden: `java.*` → `javax.*` → `org.*` → `com.*` → `net.*` → estáticos al final.

### Naming
| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Clase | PascalCase | `DataGeneratorService` |
| Método | camelCase, verbo | `generateUsers()`, `findByField()` |
| Constante | UPPER_SNAKE | `static final String ACTOR_NAME` |
| Variable local | camelCase, descriptiva | `quantityToGenerate`, `generatedUsers` |
| Paquete | lowercase | `com.latam.datagenerator.service` |

### Logger
- **Siempre usar SLF4J** (`org.slf4j.Logger`). Nunca `java.util.logging`, `System.out.println` ni `e.printStackTrace()`.
- Declaración estándar:
  ```java
  private static final Logger log = LoggerFactory.getLogger(MiClase.class);
  ```
- Usar interpolación paramétrica: `log.info("Cargados {} registros", count)` — **nunca** concatenación de strings.

### Versión Java
- **Target: Java 17** en ambos proyectos. Aprovechar: `var` para tipos obvios, records para DTOs simples, switch expressions.
- `pom.xml` siempre debe decir `<maven.compiler.source>17</maven.compiler.source>`.

---

## 7. 🧪 Código de Test (latam-automation)

- **Un Step Definition = una acción de negocio.** No mezclar lógica en los steps.
- Toda acción de usuario → Task de Serenity Screenplay.
- Toda verificación → método `seeThat(...)` en el actor, nunca `assert` crudo.
- **No hardcodear datos de prueba** en los steps — obtenerlos de `DataHelper.getUsuarioPorTipo(tipo)`.
- Los Steps que no hacen nada (`// La tarea ya maneja las opciones`) son aceptables como placeholders, pero deben tener un comentario `// TODO:` si hay intención futura.
- **Prohibido** duplicar la misma aserción en 2+ métodos `@Entonces` — extraer método privado.

---

## 8. 🗄️ Base de Datos y Persistencia

- Toda operación JDBC debe usar **try-with-resources** para Connection, Statement y ResultSet.
- Toda query parametrizada debe usar `PreparedStatement` — **nunca concatenación de SQL**.
- `DatabaseManager` es Singleton — no crear nuevas instancias de conexión fuera de él.
- `UserRepository` es la **única** clase autorizada a escribir SQL de producción. `DataHelper` solo puede leer, y preferiblemente via el repositorio compartido.

---

## 9. 🔄 Proceso de Modificación de Código

Cuando la IA modifique cualquier archivo, **DEBE**:

1. **Verificar imports** → eliminar los no usados, agregar los nuevos necesarios.
2. **Verificar FQN inline** → convertirlos a imports limpios en la cabecera.
3. **Verificar duplicación** → si el bloque existe en otro lugar, extraer o reutilizar.
4. **Verificar YAGNI** → no añadir parámetros, métodos o clases que no se usen inmediatamente.
5. **Verificar SOLID** → el cambio no debe romper SRP, OCP ni DIP.
6. **Mantener comentarios** → no eliminar Javadoc existente ni comentarios de reglas de negocio.
7. **Preservar tests** → si hay un test existente, el cambio no debe rompéerlo.

---

## 10. ⚠️ Señales de Alerta (Red Flags)

La IA debe **detenerse y notificar al usuario** si detecta:

| Señal | Acción |
|-------|--------|
| Método de más de 30 líneas | Preguntar si extraer submétodo |
| Clase de más de 300 líneas | Proponer división de responsabilidades |
| `new ConcreteClass()` dentro de un servicio de negocio | Proponer inyección de dependencias |
| `System.out.println` en código de producción | Reemplazar con `log.info/debug` |
| `e.printStackTrace()` | Reemplazar con `log.error("mensaje", e)` |
| Import `*` (wildcard) | Expandir a imports explícitos |
| Variable declarada y nunca leída | Eliminar con comentario explicativo |
| Campo de instancia nunca accedido | Eliminar o convertir a constante |
| Código comentado (no TODO) | Eliminar — el historial está en git |

---

## 11. 📝 Convención de Commits

```
<tipo>(<scope>): <descripción en español, imperativo>

feat(data-generator): agregar filtro de usuarios por edad
fix(latam-automation): eliminar imports no usados en BuscarVuelo
refactor(repository): extraer findByField para eliminar duplicación DRY
chore(pom): actualizar compiler target a Java 17
```

Tipos válidos: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `style`.

---

> **Última actualización:** 2026-06-11  
> **Mantenido por:** Equipo QA Automation LATAM  
> **Este archivo es vinculante para todos los agentes IA y desarrolladores del proyecto.**
