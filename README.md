# Creando un Conecta-4 en Kotlin y KorGE usando TDD

Este es el repositorio que usaremos para el evento online:

<https://www.meetup.com/es-ES/Kotlin-es/events/272028949/>

Vamos a dividir el juego en dos partes: lógica y presentación.
Para que nos de tiempo a desarrollar todo durante el evento,
veremos cómo testear la lógica, pero no testearemos la presentación
aunque daremos alguna indicación de cómo hacerlo.

## Lógica con modelos inmutables

* Tablero y fichas
* Acciones
* Transiciones

Y cubriremos:

* Creación y representación del tablero en los tests
* Testeo y aplicación de acciones sobre un tablero (colocar ficha)
* Representación de movimientos válidos e inválidos
* Testeo y comprobación de finalización: ganador o empate
* Resultado de la aplicación de una acción: nuevo tablero y listados de transiciones

## Presentación

* Generación de gráficos programáticamente
* Eventos de ratón
* Animaciones a partir de las transiciones
