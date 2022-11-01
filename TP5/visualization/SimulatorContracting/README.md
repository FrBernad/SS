# Autores

- [Francisco Bernad](https://github.com/FrBernad)
- [Nicolás Rampoldi](https://github.com/NicolasRampoldi)
- [Agustín Manfredi](https://github.com/imanfredi)

# MEDIO GRANULAR: PARTÍCULAS CON RADIO VARIANTE

# Requerimientos

- [Python 3+](https://www.python.org/downloads/)
- [Pipenv](https://pipenv.pypa.io/en/latest/)
- [Java 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

# Setup

En la carpeta raiz del proyecto ejecutar el comando `pipenv install`. Esto instalará todas las dependencias
necesarias para ejecutar el simulador.

# Uso

Para utilizar el simulador se **DEBEN** seguir los siguientes pasos:

1. Generar o modificar un archivo de configuración con formato yaml con los valores necesarios. Puede encontrar un
   archivo de ejemplo
   llamado ***config.yaml*** explicando los valores posibles para cada parámetro.
2. Ejecutar el simulador con el siguiente comando:

```bash
  pipenv shell 
  python simulator.py [-h] [-c config_file]
```

Donde:

- config_file: especifica la ubicación de un archivo de configuración válido. Valor por defecto: **"config.yaml"**

## Archivo de Configuración

El archivo de configuración debe ser un archivo con formato yaml y cuenta con dos secciones.

### Generator

Sección dedidcada a la generación de archivos estáticos y dinamicos de partículas.

Variables:

* ***generate:*** Bool --> False/True para indicar si es necesario generar partículas.
  En caso de ser True, generará nuevos archivos estáticos y dinámicoslas. Si su valor es False, solo ejecutará el
  simulador.
* ***seed:*** long --> Un long que indique la seed para la generación de números random. En caso omitirla se generará
  aleatoriamente.
* ***dynamic_file:*** str --> "absolute/path/to/dynamic/file". Path al archivo donde se quiere dejar la configuración
  inicial del sistema (posiciones y velocidades de las partículas).
* ***static_file:*** str "absolute/path/to/static/file". Path al archivo donde se quiere dejar la configuración inicial
  del sistema (masa y radios)
* ***N:*** int --> número de partículas
* ***L:*** int --> alto del silo
* ***W:*** int --> ancho del silo
* ***mass:*** float --> masa de las partículas en gramos
* ***r0:*** float --> r0 de la función del radio
* ***minTheta:*** float --> mínimo valor para la fase de la función del radio
* ***maxTheta:*** float --> máximo valor para la fase de la función del radio
* ***w:*** float --> frecuencia de la funcion del radio
* ***A:*** float --> amplitud de la funcion del radio
* ***vx:*** float --> velocidad inicial en x de las partículas
* ***vy:*** float --> velocidad inicial en y de las partícula

### Simulator

Sección dedidcada a la simulación del sistema.

Variables:

* ***results_file:*** "absolute/path/to/results/file". Path al archivo donde se va a dejar los resultados de la
  simulación.
* ***exit_time_file:*** "absolute/path/to/exit/time/file". Path al archivo donde en que tiempo sale cada partícula
* ***dynamic_file:*** str --> "absolute/path/to/dynamic/file". Path al archivo donde se encuentra la configuración
  inicial del sistema (posiciones y velocidades de las partículas).
* ***static_file:*** str "absolute/path/to/static/file". Path al archivo donde se encuentra la configuración inicial del
  sistema (masa y radios)
* ***seed:*** long --> Un long que indique la seed para la generación de números random. En caso omitirla se generará
  aleatoriamente.
* ***L:*** int --> alto del silo
* ***W:*** int --> ancho del silo
* ***vx:*** float --> velocidad inicial al ser reinsertadas en x de las partículas
* ***vy:*** float --> velocidad inicial al ser reinsertadas en y de las partícula
* ***vd:*** float --> constante para fuerza de propulsión
* ***tau:*** float --> constante para fuerza de propulsión
* ***dt:*** float --> Paso temporal de la simulación
* ***dt2:*** float --> Cada cuanto se imprime el resultado de la simulación
* ***tf:*** float --> tiempo de simulación
* ***D:*** int --> ancho de la apertura
* ***w:*** float --> frecuencia de la funcion del radio
* ***A:*** float --> amplitud de la funcion del radio
* ***r0:*** float --> r0 de la función del radio
* ***kn:*** float --> valor de la constante kn
* ***kt:*** float --> valor de la constante kt
* ***reenter_min_height:*** int --> altura mínima para la cual se quiere que aparezcan las partículas al ser
  reinsertadas
* ***reenter_max_height:*** int --> altura máxima para la cual se quiere que aparezcan las partículas al ser
  reinsertadas
* ***exit_distance:***  int --> valor positivo que indica la altura a la cual las partículas deben ser reinyectadas al
  salir del silo

### Ejemplo

```
---
config:

  generator:
    generate: True
#    seed: 1
    dynamic_file: "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/dynamic3.txt"
    static_file: "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/static3.txt"
    N: 30
    L: 70
    W: 20
    mass: 1
    w: 3
    A: 0.15
    minTheta: 0
    maxTheta: 6.28
    r0: 1
    vx: 0
    vy: 0

  simulator:
    results_file: "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/results3.txt"
    exit_time_file: "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/exitTime3.txt"
    dynamic_file: "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/dynamic3.txt"
    static_file: "/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/static3.txt"
#    seed: 1
    L: 70
    W: 20
    vx: 0
    vy: 0
    vd: 5
    tau: 1
    dt: 0.001
    dt2: 0.1
    tf: 1000
    D: 3
    A: 0.15
    w: 5
    r0: 1
    kn: 250
    kt: 500
    reenter_min_height: 40
    reenter_max_height: 70
    exit_distance: 7



...
```



