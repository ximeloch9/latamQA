# Prueba de Concepto - Cliente Latam

Haces parte del equipo para el cliente Latam, en donde quieren realizar una prueba de concepto para una implementación **Shift Right** del servicio de aseguramiento de calidad y te realizan unas preguntas y unas solicitudes para los siguientes módulos.

## PARTE 1

Se están realizando todo tipo de pruebas continuamente a Latam y se requiere generar datos de prueba ficticios. Por lo tanto, se requiere realizar un desarrollo para que genere los datos automáticamente.

### Los datos que se deben generar son los siguientes:

* Nombre
* Apellido: si es una empresa este campo debe ir en blanco.
* La combinación de nombre y apellido no se puede repetir.
* Edad: todos los usuarios deben ser mayores a 10 años y menores de 80.
* Documento de identificación:

  * Si es una empresa, deberá generar un número que inicie por `9`.
  * Si es un menor de edad, el documento deberá generarse a partir del `11000000`.
  * Si es un mayor de edad, el número de dígitos deberá ser mayor a 8 y menor que 12.
  * Los números de documento no se pueden repetir.
* Ciudad de residencia.
* País de residencia.
* Idioma: si se trata de un país diferente a Colombia, el idioma no podrá ser Español.

### El desarrollo debe incluir:

1. Un ejemplo por cada uno de los pilares de la programación:

   * Encapsulamiento
   * Abstracción
   * Herencia
   * Polimorfismo

2. Al menos dos patrones de diseño distintos; un ejemplo por cada uno.

3. Los datos deben quedar almacenados en una base de datos (de su preferencia).

   **Bonus:** Incluir métodos para gestionar los datos almacenados en las ejecuciones pasadas.

4. El desarrollo deberá incluir al menos 2 principios SOLID.

   **Bonus adicional:** Incluir los 5 principios SOLID.

5. El desarrollo deberá permitir indicar cuántos registros se requieren generar.

6. El desarrollo deberá generar un archivo de texto separado por comas (CSV) con los datos generados.

7. **Bonus adicional:** Enviar el archivo de texto generado por correo electrónico.

8. **Bonus adicional:** Permitir la ejecución en paralelo.

---

## PARTE 2

### Módulo Diseño

Te pedimos que identifiques, con la ayuda que consideres necesaria, **3 casos de prueba** en la página de Latam que te permitan realizar la búsqueda de vuelos.

### Módulo Automatización

Acabas de iniciar tu primer sprint en donde tienes las siguientes actividades a desarrollar:

* Crear el framework de automatización de pruebas de acuerdo con los lineamientos de arquitectura que planteaste anteriormente.
* Implementar los 3 casos de prueba de UI que planteaste anteriormente.
* Utilizar los datos generados en el punto 1 como datos de entrada para esta automatización.

> **Nota:** Realiza la implementación en **Serenity** y **Cucumber**.

---

Esto ha sido todo para esta prueba de concepto del cliente Latam.

## Recomendación

Te recomiendo leer las siguientes notas.

### Notas

* Cuando termines, tanto el desarrollo de generación de datos como la automatización deberán ser publicados en un repositorio público.
* Debes incluir las instrucciones de ejecución correspondientes.
