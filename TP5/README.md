# TP5 — Modelos y Simulación

Ecuaciones diferenciales lineales de primer orden (enfriamiento de Newton, Malthus,
decaimiento radiactivo), diagramas en bloques de 6 sistemas lineales (en Scilab/Xcos), y el
modelo Depredador-Presa (Lotka-Volterra), analizado y simulado numéricamente.

## Qué hace

- **Ejercicios 1 a 3**: son ecuaciones separables de la forma `dz/dt = k·z` (o
  `dz/dt = -k·(z - z_amb)`); la solución cerrada es una exponencial. Se calcula esa
  fórmula para un juego de parámetros de ejemplo y se grafica la curva resultante.
- **Ejercicio 4**: los 6 diagramas en bloques se resuelven en **Scilab/Xcos**, no en Java
  (ver la carpeta `scilab/` y su `README.md`).
- **Ejercicio 5 (Lotka-Volterra)**: sistema no lineal sin solución con fórmula, así que se
  resuelve **paso a paso** con el método de **Runge-Kutta de orden 4**, usando el integrador
  `ClassicalRungeKuttaIntegrator` de [Apache Commons Math](https://commons.apache.org/proper/commons-math/).
  Todo está en un solo archivo, `Ejercicio5.java`, pensado para leerse de arriba hacia abajo:
  datos → ecuaciones → punto de equilibrio → simulación → gráficos. Los gráficos (poblaciones
  vs. tiempo y retrato de fase) se hacen con [JFreeChart](https://www.jfree.org/jfreechart/).
  El análisis teórico (equilibrios, linealización, cantidad conservada) está en el informe.

Los gráficos de los ejercicios 1 a 3 son paneles Swing con `Graphics2D` propio; los del ejercicio 5
usan JFreeChart. Todos se exportan a PNG en `graficos/` sin abrir ventanas.

## Estructura

```
src/main/java/tp5/
├── Main.java                     # punto de entrada: llama a cada ejercicio en orden
├── ejercicios/
│   ├── Ejercicio1.java           # Enfriamiento de Newton (+ variación de Tamb y k)
│   ├── Ejercicio2.java           # Malthus (+ variación de r y P0)
│   ├── Ejercicio3.java           # Decaimiento radiactivo (+ variación de λ y N0)
│   └── Ejercicio5.java           # Lotka-Volterra: ecuaciones, simulación RK4 y gráficos (todo en un archivo)
├── modelos/
│   └── ModelosExponenciales.java # soluciones cerradas de los Ejercicios 1-3
└── grafico/
    ├── Curva.java                # función + etiqueta + color, para graficar
    ├── GraficoFuncion.java       # grafica una o más curvas y=f(t)
    ├── GraficosComparativos.java # familia de curvas variando un parámetro (sensibilidad)
    └── VentanaGrafico.java       # guarda cualquier JPanel como PNG y muestra el cartel final
```

## Cómo correrlo

Requiere Java y Maven, y entorno gráfico (usa Swing).

```bash
mvn compile
mvn exec:java -Dexec.mainClass=tp5.Main
```

Los resultados numéricos se imprimen por consola y los gráficos se guardan como PNG en
`graficos/` (no se abre una ventana por gráfico). Al terminar aparece un único cartel
que indica revisar la carpeta `graficos`.

## Ejercicio 4 en Scilab/Xcos

Los 6 sistemas están armados como diagramas de **Xcos** (software libre, alternativa a Simulink)
en la carpeta `scilab/`: archivos `.zcos` listos para abrir y simular, y un script que los genera
y los verifica contra la solución exacta. Ver `scilab/README.md`.

## Ejercicios

| Ejercicio | Modelo | Qué hace el programa |
|-----------|--------|------------------------|
| 1 | Enfriamiento de Newton | `T(t) = Tamb + (T0-Tamb)·e^(-kt)`, gráfico de la curva |
| 2 | Crecimiento de Malthus | `P(t) = P0·e^(rt)`, gráfico de la curva |
| 3 | Decaimiento radiactivo | `N(t) = N0·e^(-λt)`, gráfico de la curva y vida media |
| 4 | 6 sistemas lineales (a-f) | resuelto en Scilab/Xcos (carpeta `scilab/`), no en Java |
| 5 | Lotka-Volterra | equilibrio, simulación RK4 (Commons Math) y gráficos (JFreeChart) |

El desarrollo matemático completo (separación de variables, matriz jacobiana,
cantidad conservada) está en `../../TP5-Informe-Completo.docx` / `.pdf`.
