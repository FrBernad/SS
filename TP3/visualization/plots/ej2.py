from typing import List, Tuple

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# Distribución de probabilidades (o alternativamente, PDF) del módulo de las
# velocidades solo de las partículas pequeñas en el último tercio de la simulación.
# Comparar con la PDF del estado inicial del sistema (a t = 0). Considerar al
# menos 3 valores de N.
def speed_distribution(run_files: List[Tuple]):
    for files in run_files:
        data = []
        dfs = get_particles_data(files[0], files[1])

        particles_speed = np.array(list(map(lambda df: (
            np.linalg.norm(df.data[df.data['mass'] == 0.9][["vx", "vy"]], axis=1)
        ), dfs)))

        initial_speed = particles_speed[0]

        counts, bin_edges = np.histogram(initial_speed, 10, density=True)
        bin_centres = (bin_edges[:-1] + bin_edges[1:]) / 2.

        data.append(go.Scatter(
            x=bin_centres,
            mode="lines+markers",
            y=counts,
            name=f'''Valores Iniciales'''
        ))

        # data.append(
        #     go.Histogram(
        #         x=initial_speed,
        #         nbinsx=10,
        #         opacity=0.75,
        #         name=f'''N={len(dfs[0].data) - 1} - Valores Iniciales''',
        #         histnorm="probability density"
        #     )
        # )
        last_third_speed = particles_speed[-int(len(particles_speed) / 3):].flatten()

        counts, bin_edges = np.histogram(last_third_speed, 40, density=True)
        bin_centres = (bin_edges[:-1] + bin_edges[1:]) / 2.

        data.append(go.Scatter(
            x=bin_centres,
            mode="lines+markers",
            y=counts,
            name=f'''Último Tercio'''
        ))
        # data.append(
        #     go.Histogram(
        #         x=last_third_speed,
        #         nbinsx=40,
        #         opacity=0.75,
        #         name=f'''N={len(dfs[0].data) - 1} - Último Tercio''',
        #         histnorm="probability density"
        #     )
        # )
        #
        # Graficar la distribución de probabilidades de dichos tiempos
        fig = go.Figure(
            data=data,
            layout=go.Layout(
                title=dict(text=f'Velocity', x=0.5),
                xaxis=dict(title=r'$\large{\text{Rapidez }(\frac{\text{m}}{\text{s}})}$', linecolor="#000000", ticks="outside",
                           tickwidth=2, tickcolor='black', ticklen=10),
                yaxis=dict(title=r'$\large{\text{PDF}}$', linecolor="#000000", ticks="outside",
                           tickwidth=2, tickcolor='black', ticklen=10),
                font=dict(
                    family="Computer Modern",
                    size=22,
                ),
                plot_bgcolor='rgba(0,0,0,0)',
                barmode='overlay',
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
    speed_distribution(
        [
            (static_file_110, results_file_110),
            (static_file_125, results_file_125),
            (static_file_140, results_file_140)
        ]
    )
