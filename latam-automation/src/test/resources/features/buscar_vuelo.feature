# language: es
Característica: Búsqueda de Vuelos en Latam Airlines

  Como un ingeniero de control de calidad
  Quiero realizar la búsqueda de vuelos en el portal de Latam
  Para verificar el correcto funcionamiento del flujo de reserva utilizando datos generados de prueba

  Escenario: CP1 - Búsqueda de vuelo nacional para usuario adulto residente en Colombia (Solo Ida)
    Dado que el actor carga un usuario "Adulto" de la base de datos
    Cuando el actor ingresa al portal de Latam en Colombia
    Y selecciona el tipo de viaje solo ida
    Y busca un vuelo desde su ciudad de residencia "Bogota" hacia "Medellin"
    Entonces el sistema debe mostrar los vuelos disponibles para la seleccion

#   Escenario: CP2 - Búsqueda de vuelo internacional multilenguaje (Usuario Extranjero - Ida y Vuelta)
#     Dado que el actor carga un usuario "Extranjero" de la base de datos
#     Cuando el actor ingresa al portal regional según su país de origen e idioma
#     Y selecciona el tipo de viaje ida y vuelta
#     Y busca un vuelo desde su ciudad de residencia hacia "Orlando"
#     Entonces la página de resultados debe cargarse correctamente respetando la moneda y el idioma del usuario
# 
#   Escenario: CP3 - Validacion de datos de pasajero menor de edad en Checkout
#     Dado que el actor carga un usuario "Menor" de la base de datos
#     Cuando busca un vuelo de ida en Latam
#     Y selecciona la tarifa más económica del vuelo
#     Y completa los datos del pasajero menor en el Checkout
#     Entonces el formulario de pasajeros debe aceptar el documento especial del menor
