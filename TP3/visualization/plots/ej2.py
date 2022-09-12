import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# Distribución de probabilidades (o alternativamente, PDF) del módulo de las
# velocidades solo de las partículas pequeñas en el último tercio de la simulación.
# Comparar con la PDF del estado inicial del sistema (a t = 0). Considerar al
# menos 3 valores de N.
def speed_distribution(static_file: str, results_file: str):
    dfs = get_particles_data(static_file, results_file)

    particles_speed = np.array(list(map(lambda df: (
        np.linalg.norm(df.data[df.data['mass'] == 0.9][["vx", "vy"]], axis=1)
    ), dfs)))

    # Graficar la distribución de probabilidades de dichos tiempos
    fig = go.Figure(
        data=go.Histogram(
            x=particles_speed[0],
            histnorm="probability density",
        ),
        layout=go.Layout(
            title=dict(text=f'Collision Times - N={len(dfs[0].data) - 1}', x=0.5),
            xaxis=dict(title='Rapidez (m/s)'),
            yaxis=dict(title='PDF'),
            font=dict(
                family="Arial",
                size=22,
            )
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()

    last_third_speed = particles_speed[-int(len(particles_speed) / 3):].flatten()

    # Graficar la distribución de probabilidades de dichos tiempos
    fig = go.Figure(
        data=go.Histogram(
            x=last_third_speed,
            histnorm="probability density",
            nbinsx=40
        ),
        layout=go.Layout(
            title=dict(text=f'Collision Times - N={len(dfs[0].data) - 1}', x=0.5),
            xaxis=dict(title='Rapidez (m/s)'),
            yaxis=dict(title='PDF'),
            font=dict(
                family="Arial",
                size=22,
            )
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/Static.txt'
    results_file = '../../results/results.txt'
    speed_distribution(static_file, results_file)
