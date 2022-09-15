import glob

import numpy as np
import plotly.graph_objects as go
from sklearn.linear_model import LinearRegression

from utils.parser_utils import get_particles_data


# Para el máximo valor de N usado en los puntos anteriores, estimar el coeficiente de difusión de
# la partícula grande, primero, y de las pequeñas después, calculando el desplazamiento cuadrático
# medio en función del tiempo. Luego realizar el ajuste del coeficiente de difusión (D), con el método
# genérico visto en la Teórica 0. Ademas, se debe describir como se eligen los tiempos en los cuales
# se calcula el DCM, dado que el estado del sistema fue guardado con dt no uniformes debido a los
# eventos.
# Para las partículas que se estudien, solo se deben considerar la segunda mitad de sus trayectorias,
# teniendo en cuenta que la misma solo es válida hasta que choque con alguna de las paredes.
def big_particle_DCM(static_file: str, results_dir: str):
    runs = []
    for file in glob.glob(f'''{results_dir}*.txt'''):
        runs.append(get_particles_data(static_file, file))

    time_step = 1
    clock_dfs = []
    for dfs in runs:
        current_time = 0
        clock_df = []
        # 0 2.53 2.78 3.01 3.30 3.64
        for df in dfs:
            if current_time <= df.time:
                clock_df.append(df)
                current_time += time_step
        clock_dfs.append(clock_df)

    min_clock_dfs = min(clock_dfs, key=lambda clock_df: clock_df[-1].time)
    min_len = len(min_clock_dfs)
    min_time = min_clock_dfs[-1].time

    big_particle_initial_position = np.array(runs[0][0].data[runs[0][0].data['mass'] == 2][["x", "y"]].values.flatten())

    DCs = []
    for dfs in clock_dfs:
        big_particle_positions = np.array(list(map(lambda df: (
            df.data[df.data['mass'] == 2][["x", "y"]].values.flatten()
        ), dfs)))
        DCs.append(np.linalg.norm(big_particle_positions[:min_len] - big_particle_initial_position, axis=1))

    DCM = np.mean(DCs, axis=0)
    x_values = np.arange(0, min_time, time_step)
    error = np.std(DCs, axis=0)

    model = LinearRegression().fit(x_values.reshape(-1, 1), DCM.reshape(-1, 1))

    fig = go.Figure(
        data=[
            go.Scatter(
                x=x_values,
                y=DCM,
                mode='lines',
                showlegend=False
            ),
            go.Scatter(
                x=np.concatenate((x_values, x_values[::-1])),
                y=np.concatenate((DCM + error, (DCM - error)[::-1])),
                fill='toself',
                fillcolor='rgba(0,100,80,0.2)',
                line=dict(color='rgba(255,255,255,0)'),
                hoverinfo="skip",
                showlegend=False
            ),
            go.Scatter(
                x=x_values,
                y=x_values * model.coef_[0] + model.intercept_[0],
                mode='lines',
                showlegend=False
            )
        ],
        layout=go.Layout(
            title=dict(text=f'Big Particle DCM', x=0.5),
            xaxis=dict(title='Tiempo (s)', dtick=10, tick0=0),
            yaxis=dict(title='DCM'),
            font=dict(
                family="Arial",
                size=22,
            )
        )
    )

    # Set figure size
    fig.update_layout(width=1800, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/results4BIG/static.txt'
    results_dir = '../../results/results4BIG/'
    big_particle_DCM(static_file, results_dir)
