from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


def plot_oscillator(run_files: List[Tuple]):
    data = []
    names = ['Gear Predictor-Corrector Orden 5', 'Beeman', 'Verlet Original']
    for i, files in enumerate(run_files):
        dfs = get_particles_data(files[0], files[1])

        event_times = np.array(list(map(lambda df: df.time, dfs)))
        particle_df = np.array(list(map(lambda df: df.data.x, dfs))).flatten()

        data.append(
            go.Scatter(
                x=event_times,
                y=particle_df,
                name=names[i]
            )
        )

    dt = 0.001
    A = 1
    gamma = 100
    k = 10000
    m = 70
    time = np.arange(0, 5 + dt, dt)
    exp_term = -(gamma / (2 * m))
    cos_term = ((k / m) - ((gamma ** 2) / (4 * m ** 2))) ** 0.5
    data.append(
        go.Scatter(
            x=time,
            y=A * np.exp(exp_term * time) * np.cos(cos_term * time),
            name='Analítica'
        )
    )

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Oscillator', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$', exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Posición (m)}}$', exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=22,
            ),
            plot_bgcolor='rgba(0,0,0,0)',
            legend=dict(
                yanchor="top",
                y=0.99,
                xanchor="right",
                x=0.99
            )
        )

    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/Static.txt'
    results_gear_predictor = '../../results/ejer1/resultsGearPredictor.txt'
    results_beeman = '../../results/ejer1/resultsBeeman.txt'
    results_verlet_original = '../../results/ejer1/resultsVerletOriginal.txt'
    plot_oscillator(
        [
            (static_file, results_gear_predictor),
            (static_file, results_beeman),
            (static_file, results_verlet_original),
        ]
    )
