import glob
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# En todos los casos graficar las soluciones analítica y numérica y calcular el error cuadrático
# medio (sumando las diferencias al cuadrado para todos los pasos temporales y normalizando por el
# número total de pasos).
def plot_oscillator_per_dt(run_folders: List[Tuple[str, str]]):
    data = []

    dts = np.array([10 ** (-i) for i in range(2, 7)])
    steps = np.array([1, 1, 1, 10, 100])
    A = 1
    gamma = 100
    k = 10000
    m = 70

    names = ['Verlet Original', 'Beeman', 'Gear Predictor-Corrector Orden 5']
    for i, static_and_folder in enumerate(run_folders):
        files = glob.glob(static_and_folder[1] + '/*')
        files.sort(key=lambda f: float(f.split('_')[1]), reverse=True)
        print(f'''Parsing {names[i]}''')
        ECMs = []
        for j, file in enumerate(files):
            print(f'''dt = {dts[j]}, step = {steps[j]}''')
            analytic_x = np.arange(0, 5 + dts[j], dts[j])[::steps[j]]
            exp_term = -(gamma / (2 * m))
            cos_term = ((k / m) - ((gamma ** 2) / (4 * m ** 2))) ** 0.5
            analytic_y = A * np.exp(exp_term * analytic_x) * np.cos(cos_term * analytic_x)

            dfs = get_particles_data(static_and_folder[0], file)

            particle_df = np.array(list(map(lambda df: df.data.x, dfs))).flatten()

            ECMs.append(np.sum((analytic_y - particle_df) ** 2) / len(analytic_y))

        data.append(
            go.Scatter(
                x=dts,
                y=ECMs,
                name=names[i],
                marker=dict(size=10)
            )
        )

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Oscillator ECM per dt', x=0.5),
            xaxis=dict(title=r'$\Large{\text{dt (s)}}$', exponentformat="power", type='log',
                       dtick=1,
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{ECM } (\text{m}^{\text{2}})}$', exponentformat="power", type='log',
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=22,
            ),
            plot_bgcolor='rgba(0,0,0,0)',
        )

    )

    # Set figure size
    fig.update_layout(width=1500, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/ej1/Static.txt'
    results_gear_predictor_folder = '../../results/ej1/multipleRuns/GearPredictor'
    results_beeman_folder = '../../results/ej1/multipleRuns/Beeman'
    results_verlet_original_folder = '../../results/ej1/multipleRuns/VerletOriginal'
    plot_oscillator_per_dt(
        [
            (static_file, results_verlet_original_folder),
            (static_file, results_beeman_folder),
            (static_file, results_gear_predictor_folder),
        ]
    )
