from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# ¿Cuál es el valor promedio de la frecuencia de colisiones (Nro. de colisiones totales divido
# el tiempo total de la simulación)?
# Calcular el promedio de tiempos de colisión y graficar la distribución de probabilidades de
# dichos tiempos (o alternativamente, PDF). No estudiar ni graficar evolución temporal de esta cantidad.
# Considerar al menos 3 valores de N.
def average_collision(run_files: List[Tuple]):
    # TODO: ver que hacemos con los decimales y poner la curva de los histogramas
    data = []
    for files in run_files:
        dfs = get_particles_data(files[0], files[1])

        event_times = np.array(list(map(lambda df: df.time, dfs)))

        avg_col_freq = len(event_times) / event_times[-1]

        # Calcular el promedio de tiempos de colisión
        collision_times = event_times[1:] - event_times[:-1]
        avg_collision_time = np.mean(collision_times)
        std_dev = np.std(collision_times)

        counts, bin_edges = np.histogram(collision_times, bins=40, density=True)
        bin_centres = (bin_edges[:-1] + bin_edges[1:]) / 2.

        data.append(go.Scatter(
            x=bin_centres,
            y=counts,
            mode='lines',
            name=f'''N={len(dfs[0].data) - 1}'''
        ))

        # data.append(go.Histogram(
        #     x=collision_times,
        #     nbinsx=40,
        #     histnorm="probability density"
        # ))

        fig = go.Figure(
            data=go.Table(
                header=dict(values=['Average Collision Frequency', 'Average Collision Time', 'STD Collision Time'],
                            height=40),
                cells=dict(values=[[avg_col_freq], [avg_collision_time], [std_dev]], height=40)
            ),
            layout=go.Layout(
                title=dict(text=f'N={len(dfs[0].data) - 1}', x=0.5),
                font=dict(
                    family="Arial",
                    size=22)
            )
        )
        fig.update_layout(width=1000, height=1000)
        fig.show()

    # Graficar la distribución de probabilidades de dichos tiempos
    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Collision Times', x=0.5),
            xaxis=dict(title='Tiempo (s)', exponentformat="power"),
            yaxis=dict(title='PDF', exponentformat="power"),
            font=dict(
                family="Arial",
                size=22,
            ),
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
    static_file_110 = '../../assets/Static110.txt'
    static_file_125 = '../../assets/Static125.txt'
    static_file_140 = '../../assets/Static140.txt'
    results_file_110 = '../../results/results110.txt'
    results_file_125 = '../../results/results125.txt'
    results_file_140 = '../../results/results140.txt'
    average_collision(
        [
            (static_file_110, results_file_110),
            (static_file_125, results_file_125),
            (static_file_140, results_file_140)
        ]
    )
