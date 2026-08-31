# TP2 — Modelos y Simulación

Implementación digital y visualización de cinco señales continuas: Escalón,
Seno, Exponencial, Señal Amortiguada y Pulso.

## Qué hace

Cada señal se digitaliza muestreándola en instantes equiespaciados
`x[n] = x(n·Ts)`, con un período de muestreo `Ts` propio para cada una, y se
grafica como *stem plot* (muestras discretas, no una curva continua) usando
un módulo gráfico propio hecho con Java Swing. Al ejecutar el programa se
abre una ventana por señal y además se guarda una imagen `.png` de cada
gráfico en la carpeta `graficos/`.

## Estructura

```
src/main/java/tp2/
├── Main.java                    # arma cada señal y dispara su gráfico
├── funciones/
│   ├── Funcion.java             # interfaz común (calcular(t), getNombre())
│   ├── Escalon.java
│   ├── Seno.java
│   ├── Exponencial.java
│   ├── SenalAmortiguada.java
│   └── Pulso.java
└── grafico/
    ├── Grafico.java              # muestreo x[n]=x(n·Ts) + dibujo del stem plot
    └── VentanaGrafico.java       # ventana Swing + export a PNG
```

## Cómo correrlo

Requiere Java y Maven.

```bash
mvn compile
mvn exec:java -Dexec.mainClass=tp2.Main
```

(o compilar y correr directo: `mvn compile && java -cp target/classes tp2.Main`)

Los gráficos generados quedan en `graficos/`.

## Parámetros usados (definidos en Main.java)

| Señal              | Parámetros                          | Ts   |
|--------------------|--------------------------------------|------|
| Escalón            | A=5, t0=2                            | 0.2  |
| Seno               | A=5, f0=1 Hz, θ=0                    | 0.05 |
| Exponencial        | A=1, α=-0.5, t0=0                    | 0.2  |
| Señal Amortiguada  | A=5, f0=1 Hz, θ=0, α=-0.3             | 0.05 |
| Pulso              | A=5, t0=2, D=3                       | 0.1  |

El desarrollo de cómo se llega a cada resultado (cálculo de la cantidad de
muestras, valores puntuales, etc.) está en `Desarrollo-TP2.txt/pdf` y
`TP2-Informe-Completo.pdf`, en la carpeta `UNI-5TOANIO/` (un nivel arriba de
este repo, no versionado con el código).
