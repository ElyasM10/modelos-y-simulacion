# TP5 — Modelos y Simulación

Ecuaciones diferenciales lineales de primer orden (enfriamiento de Newton, Malthus,
decaimiento radiactivo), diagramas en bloques de 6 sistemas lineales, y el modelo
Depredador-Presa (Lotka-Volterra), analizado y simulado numéricamente.

## Qué hace

- **Ejercicios 1 a 3**: son ecuaciones separables de la forma `dz/dt = k·z` (o
  `dz/dt = -k·(z - z_amb)`); la solución cerrada es una exponencial. Se calcula esa
  fórmula para un juego de parámetros de ejemplo y se grafica la curva resultante.
- **Ejercicio 4**: cada sistema `ẋ = a·x + b·y, ẏ = c·x + d·y` se dibuja como diagrama
  en bloques (un integrador `1/s` por variable de estado, un sumador por integrador, y
  un bloque de ganancia por cada coeficiente no nulo — si un coeficiente es 0 no se
  dibuja ese lazo), con `Graphics2D` puro (sin librerías de diagramado).
- **Ejercicio 5 (Lotka-Volterra)**: sistema no lineal sin solución cerrada. Se calculan
  los puntos de equilibrio `(0,0)` y `(γ/δ, α/β)` y se los clasifica analíticamente
  (punto silla y centro, respectivamente — ver el informe para la linealización). Como
  no hay fórmula cerrada, además se integra numéricamente con **Runge-Kutta de orden 4**
  y se grafican las poblaciones vs. tiempo y el retrato de fase (la órbita cerrada
  alrededor del equilibrio de coexistencia).

Los diagramas y gráficos son paneles Swing con `Graphics2D` puro (sin librerías de
diagramado ni de graficado), exportados a PNG en `graficos/` sin abrir ventanas. El integrador RK4 del
Ejercicio 5 usa [Apache Commons Math](https://commons.apache.org/proper/commons-math/)
(`ClassicalRungeKuttaIntegrator`) — la versión equivalente escrita a mano, sin
librerías, está en `RungeKutta4SinLibreria.java`, comentada a modo de alternativa (con
instrucciones de cómo probarla). Da exactamente los mismos resultados numéricos.

## Estructura

```
src/main/java/tp5/
├── Main.java                     # punto de entrada: llama a cada ejercicio en orden
├── ejercicios/
│   ├── Ejercicio1.java           # Enfriamiento de Newton (+ variación de Tamb y k)
│   ├── Ejercicio2.java           # Malthus (+ variación de r y P0)
│   ├── Ejercicio3.java           # Decaimiento radiactivo (+ variación de λ y N0)
│   ├── Ejercicio4.java           # diagramas en bloques de los 6 sistemas
│   └── Ejercicio5.java           # Lotka-Volterra: equilibrios, RK4 y gráficos
├── modelos/
│   └── ModelosExponenciales.java # soluciones cerradas de los Ejercicios 1-3
├── edo/
│   ├── SistemaEDO.java           # interfaz: dado (t, estado) devuelve la derivada
│   ├── RungeKutta4.java          # ACTIVA: integrador RK4 con Apache Commons Math
│   ├── RungeKutta4SinLibreria.java # alternativa sin librerías (RK4 a mano), comentada
│   └── LotkaVolterra.java        # sistema Depredador-Presa (Ejercicio 5)
├── diagrama/
│   └── DiagramaBloques.java      # dibuja el diagrama en bloques de un sistema 2x2 (Ejercicio 4)
└── grafico/
    ├── Curva.java                # función + etiqueta + color, para graficar
    ├── GraficoFuncion.java       # grafica una o más curvas y=f(t)
    ├── GraficoFase.java          # grafica una trayectoria paramétrica (x(t),y(t)) cerrada
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

## Ejercicios

| Ejercicio | Modelo | Qué hace el programa |
|-----------|--------|------------------------|
| 1 | Enfriamiento de Newton | `T(t) = Tamb + (T0-Tamb)·e^(-kt)`, gráfico de la curva |
| 2 | Crecimiento de Malthus | `P(t) = P0·e^(rt)`, gráfico de la curva |
| 3 | Decaimiento radiactivo | `N(t) = N0·e^(-λt)`, gráfico de la curva y vida media |
| 4 | 6 sistemas lineales (a-f) | diagrama en bloques de cada uno |
| 5 | Lotka-Volterra | equilibrios + clasificación (analítico), simulación RK4 y gráficos |

El desarrollo matemático completo (separación de variables, matriz jacobiana,
cantidad conservada) está en `../../TP5-Informe-Completo.docx` / `.pdf`.
