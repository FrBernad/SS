import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# ¿Cuál es el valor promedio de la frecuencia de colisiones (Nro. de colisiones totales divido
# el tiempo total de la simulación)?
# Calcular el promedio de tiempos de colisión y graficar la distribución de probabilidades de
# dichos tiempos (o alternativamente, PDF). No estudiar ni graficar evolución temporal de esta cantidad.
# Considerar al menos 3 valores de N.

def average_collision(static_file: str, results_file: str):
    # TODO: ver que hacemos con los decimales y poner la curva de los histogramas
    dfs = get_particles_data(static_file, results_file)

    event_times = np.array(list(map(lambda df: df.time, dfs)))

    # ¿Cuál es el valor promedio de la frecuencia de colisiones (Nro. de colisiones totales divido
    # # el tiempo total de la simulación)?
    avg_col_freq = event_times[-1] / len(event_times)

    # Calcular el promedio de tiempos de colisión
    collision_times = event_times[1:] - event_times[:-1]
    avg_collision_time = np.mean(collision_times)
    std_dev = np.std(collision_times)

    # Graficar la distribución de probabilidades de dichos tiempos
    fig = go.Figure(
        data=go.Histogram(
            x=collision_times,
            histnorm="probability density",
            nbinsx=40
        ),
        layout=go.Layout(
            title=dict(text=f'Collision Times - N={len(dfs[0].data) - 1}', x=0.5),
            xaxis=dict(title='Tiempo (s)', exponentformat='e', showexponent="all"),
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

    fig = go.Figure(
        data=go.Table(header=dict(values=['Average Collision Frequency', 'Average Collision Time'], height=40),
                      cells=dict(values=[[avg_col_freq], [avg_collision_time]], height=40)
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


if __name__ == "__main__":
    static_file = '../../assets/Static.txt'
    results_file = '../../results/results.txt'
    average_collision(static_file, results_file)
