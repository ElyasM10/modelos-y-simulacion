# Datos del Ejercicio 3

Fuente: Dell'Osa, A. H., Bravo, N. Y., Villanueva Jousset, T. (2025). *Single dispersion EIS measurements:
a compilation* [Dataset]. Zenodo. https://doi.org/10.5281/zenodo.15795175 (archivo `registros_EIS.mat`, 1457 registros).

Se eligieron 4 registros que siguen bien una única dispersión de Cole, y se guardaron como CSV
(`f_Hz, Re_ohm, Im_ohm`, en `src/main/resources/datos/`) con `exportar_datos.py`:

| Archivo | ID | Muestra | Equipo / origen | Rango |
|---------|----|---------|-----------------|-------|
| `humano_A.csv` | 1 | humano, tetrapolar | Impedimed SFB7, Ayllón et al. 2016 | 3 kHz – 1 MHz |
| `humano_B.csv` | 401 | humano, tetrapolar | Impedimed SFB7, Ayllón et al. 2016 | 3 kHz – 1 MHz |
| `agar_A.csv` | 1311 | fantoma de agar-agar, bipolar | Solartron, Dell'Osa et al. 2019 | 1 Hz – 65 kHz |
| `agar_B.csv` | 1401 | fantoma de agar-agar, bipolar | Solartron, Dell'Osa et al. 2019 | 1 Hz – 65 kHz |

Detalle: en los registros de origen 4 (Ayllón) la columna de frecuencia del `.mat` está en rad/s;
el script la convierte a Hz (f = ω / 2π).

Se descartaron, entre otros, los de origen 2 (Gonzalez Correa, parte imaginaria con signo opuesto) y los
vegetales/animales (curvas con parte imaginaria positiva y negativa que no responden a una sola dispersión).
