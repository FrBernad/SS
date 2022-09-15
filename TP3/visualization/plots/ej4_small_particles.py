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
def big_particle_DCM(static_file: str, results_file: str):
    dfs = get_particles_data(static_file, results_file)

    time_step = 1
    current_time = 0

    clock_dfs = []
    for df in dfs:
        if current_time <= df.time:
            clock_dfs.append(df)
            current_time += time_step

    min_time = clock_dfs[-1].time

    size = 140
    selected_particles_ids = np.random.choice(np.arange(1, len(dfs[0].data)), size=size, replace=False)

    small_particles_initial_positions = np.array(
        dfs[0].data[dfs[0].data["id"].isin(selected_particles_ids)][["x", "y"]].values)

    small_particles_positions = np.array(
        list(map(lambda df: df.data[df.data["id"].isin(selected_particles_ids)][["x", "y"]].values, clock_dfs))
    )

    DCs = np.linalg.norm(small_particles_positions - small_particles_initial_positions, axis=2)

    DCM = np.mean(DCs, axis=1)
    x_values = np.arange(0, min_time, time_step)
    error = np.std(DCs, axis=1)

    model = LinearRegression().fit(x_values.reshape(-1, 1), DCM.reshape(-1, 1))

    fig = go.Figure(
        data=[
            go.Scatter(
                x=np.arange(0, min_time, time_step),
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
            title=dict(text=f'Small Particle DCM - y = {model.coef_[0][0]}x + {model.intercept_[0]}', x=0.5),
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
    static_file = '../../assets/Static140.txt'
    results_file = '../../results/results140.txt'
    big_particle_DCM(static_file, results_file)
