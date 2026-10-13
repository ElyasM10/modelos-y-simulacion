# Ejercicio: Onda Cuadrada y Serie de Fourier

Implementación de la serie de Fourier para aproximar una onda cuadrada periódica.

## Qué hace

La clase `EjercicioOndaCuadradaFourier` calcula la suma de los primeros armónicos impares de

```text
f(t) = (4A/π) · Σ[sin(2π(2k+1)t/T)/(2k+1)]
```

Por defecto se usan amplitud `A = 1`, período `T = 2π` y 15 términos. El gráfico compara
la onda ideal con la aproximación y se guarda en `graficos/OndaCuadradaFourier.png`.
JFreeChart se usa para generar el gráfico. En los puntos de discontinuidad la serie
converge al promedio de los límites laterales, y alrededor de ellos se observa el fenómeno
de Gibbs.

## Estructura

```text
src/main/java/ejercicioondacuadradafourier/
├── Main.java
└── ejercicios/
    └── EjercicioOndaCuadradaFourier.java
graficos/
```

## Cómo correrlo

Requiere Java y Maven.

```bash
mvn compile
mvn exec:java -Dexec.mainClass=ejercicioondacuadradafourier.Main
```

Para cambiar la cantidad de términos, se puede pasar el entero al programa, por ejemplo:

```bash
mvn exec:java -Dexec.mainClass=ejercicioondacuadradafourier.Main -Dexec.args="30"
```
