from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# Estudiar como disminuye el error al disminuir el paso de integración (dt). Usar ejes semilogarítmicos
# o logarítmicos para poder apreciar las diferencias de error a escalas pequeñas. ¿Cuál de
# los esquemas de integración resulta mejor para este sistema ?
def plot_oscillator_ecm(run_files: List[Tuple]):
    data = []
    ECMs = []

    dt = 0.001
    A = 1
    gamma = 100
    k = 10000
    m = 70
    time = np.arange(0, 5 + dt, dt)
    exp_term = -(gamma / (2 * m))
    cos_term = ((k / m) - ((gamma ** 2) / (4 * m ** 2))) ** 0.5
    analytic_y = A * np.exp(exp_term * time) * np.cos(cos_term * time)
    data.append(
        go.Scatter(
            x=time,
            y=analytic_y,
            name='Analítica'
        )
    )

    names = ['Verlet Original', 'Beeman', 'Gear Predictor-Corrector Orden 5']
    for i, files in enumerate(run_files):
        dfs = get_particles_data(files[0], files[1])

        event_times = np.array(list(map(lambda df: df.time, dfs)))
        particle_df = np.array(list(map(lambda df: df.data.x, dfs))).flatten()

        ECMs.append(np.sum((analytic_y - particle_df) ** 2) / len(analytic_y))

        data.append(
            go.Scatter(
                x=event_times,
                y=particle_df,
                name=names[i]
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

    fig = go.Figure(
        data=go.Table(
            header=dict(values=names,
                        height=40),
            cells=dict(values=[[ECMs[0]], [ECMs[1]], [ECMs[2]]], height=40)
        ),
        layout=go.Layout(
            title=dict(text=f'ECM', x=0.5),
            font=dict(
                family="Computer Modern",
                size=22)
        )
    )
    fig.update_layout(width=1000, height=1000)
    fig.show()

    fig = go.Figure(
        data=go.Scatter(
            x=names,
            y=ECMs,
            marker=dict(size=5)
        ),
        layout=go.Layout(
            title=dict(text=f'ECMs', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Algoritmo}}$', exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{ECM } (\text{m}^{\text{2}})}$', exponentformat="power", type='log',
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
    fig.update_layout(width=1000, height=1000)
    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/ej1/Static.txt'
    results_gear_predictor = '../../results/ej1/resultsGearPredictor.txt'
    results_beeman = '../../results/ej1/resultsBeeman.txt'
    results_verlet_original = '../../results/ej1/resultsVerletOriginal.txt'

    plot_oscillator_ecm(
        [
            (static_file, results_verlet_original),
            (static_file, results_beeman),
            (static_file, results_gear_predictor),
        ]
    )
