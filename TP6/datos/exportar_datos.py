# Extrae del archivo registros_EIS.mat (Dell'Osa et al., Zenodo 10.5281/zenodo.15795175)
# los 4 registros elegidos y los guarda como CSV (frecuencia en Hz, parte real e imaginaria en ohm).
# Uso:  python exportar_datos.py registros_EIS.mat      (requiere scipy)
import sys
import numpy as np
import scipy.io as sio

# id del registro -> nombre del archivo
ELEGIDOS = {1: "humano_A", 401: "humano_B", 1311: "agar_A", 1401: "agar_B"}

registros = sio.loadmat(sys.argv[1], squeeze_me=True)["registros"]
for reg in registros:
    n = int(str(reg["id"]))
    if n in ELEGIDOS:
        e = reg["eis"]
        f = e[:, 0]
        if str(reg["origin"]) == "4":      # origen 4 (Ayllon): la columna de frecuencia esta en rad/s
            f = f / (2 * np.pi)
        np.savetxt(ELEGIDOS[n] + ".csv", np.column_stack([f, e[:, 1], e[:, 2]]),
                   delimiter=",", header="f_Hz,Re_ohm,Im_ohm", comments="", fmt="%.6g")
