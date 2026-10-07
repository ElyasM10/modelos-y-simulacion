# Ejercicio 4 en Scilab / Xcos

Los 6 sistemas del ejercicio 4 (`ẋ = a11·x + a12·y`, `ẏ = a21·x + a22·y`) armados como diagrama
en bloques en **Xcos**, la herramienta de diagramas en bloques de Scilab (software libre,
alternativa a Simulink — el punto opcional del TP).

## Qué hay

```
scilab/
├── crear_diagramas.sce            # arma, guarda, simula y verifica los 6 diagramas
├── diagramas/sistema_a..f.zcos    # diagramas listos para abrir en Xcos
├── capturas_xcos/sistema_a..f.png # captura de cada diagrama abierto en Xcos
└── respuestas/sistema_a..f.png    # respuesta simulada x(t), y(t) de cada sistema
```

## Cómo se arma cada diagrama

Mismo esquema que el diagrama de Java (`tp5.diagrama.DiagramaBloques`):

| Bloque Xcos | Para qué |
|---|---|
| `BIGSOM_f` (Σ) | sumador de las entradas realimentadas de cada variable |
| `INTEGRAL_m` (∫) | integrador `1/s`: de ẋ obtiene x (condición inicial x₀ = 1, y₀ = 2) |
| `GAINBLK_f` (triángulo) | ganancia = coeficiente `aij` (solo si es distinto de 0) |
| `SPLIT_f` | punto de bifurcación de x o de y |
| `TOWS_c` | guarda x(t), y(t) en el workspace de Scilab (`xs`, `ys`) |
| `CMSCOPE` | osciloscopio con las dos señales |
| `SampleCLK` + `CLKSPLIT_f` | reloj que dispara `TOWS_c` y el osciloscopio (son bloques por eventos) |

Los lazos de realimentación (ganancias) van espejados (`mirror`) porque la señal vuelve de derecha a izquierda.

## Cómo usarlo

**Ver un diagrama y simularlo en Xcos** (tiempo final 5):

```
xcos("diagramas/sistema_a.zcos")      // y luego Simulación > Iniciar (▶)
```

**Regenerar todo** desde Scilab (consola o `scilab -f crear_diagramas.sce`):

```
exec("crear_diagramas.sce", -1)
```

o sin interfaz gráfica (necesita `scilab-adv-cli`, que incluye la JVM que usa Xcos):

```bash
xvfb-run -a scilab-adv-cli -nb -f crear_diagramas.sce
```

El script, para cada sistema, guarda el `.zcos`, lo vuelve a cargar, lo simula (sin ventanas) y
compara `[x(t) y(t)]` contra la solución exacta `expm(A·t)·x₀` en el último instante muestreado:

```
Sistema  [x y] simulado         [x y] exacto           error relativo
(a)      [-1423.2087 -3817.0912]   [-1423.4799 -3819.0262]   4.79e-04
(b)      [   0.0001    0.0000]   [   0.0000    0.0000]   3.08e-06
(c)      [   0.0206    0.0206]   [   0.0205    0.0206]   6.92e-05
(d)      [-1164.2839 -582.1419]   [-1163.7946 -581.8972]   4.20e-04
(e)      [  -0.0274   -0.0205]   [  -0.0274   -0.0205]   8.21e-05
(f)      [   2.9862    0.0138]   [   2.9863    0.0137]   2.56e-05
```

**Condición inicial:** `x₀ = 1, y₀ = 2` (se cambia en la variable `X0` del script). No se usó `(1, 1)`
porque en los sistemas (c) y (e) ese vector es un autovector de `A`: ahí `x(t) = y(t)` y las dos curvas
quedan superpuestas. En (c) y (e) igual terminan juntándose para `t` grande, porque el modo lento
`e^(−t)` tiene dirección `(1, 1)` y domina: es el comportamiento real del sistema.

Probado con Scilab 2024.1.0.
