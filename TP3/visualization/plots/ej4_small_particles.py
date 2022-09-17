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

    size = 60
    selected_particles_ids = np.random.choice(np.arange(1, len(dfs[0].data)), size=size, replace=False)

    small_particles_initial_positions = np.array(
        dfs[0].data[dfs[0].data["id"].isin(selected_particles_ids)][["x", "y"]].values)

    small_particles_positions = np.array(
        list(map(lambda df: df.data[df.data["id"].isin(selected_particles_ids)][["x", "y"]].values, clock_dfs))
    )

    DCs = np.linalg.norm(small_particles_positions - small_particles_initial_positions, axis=2)

    DCM = np.mean(DCs, axis=1)
    x_values = np.arange(0, min_time, time_step)
    start_time = 10
    start_index = int(start_time/time_step)
    error = np.std(DCs, axis=1)

    initial_m = (DCM[-1] - DCM[start_index]) / (x_values[-1] - x_values[start_index])
    step = 0.00005
    offset = 0.01
    m_values = np.arange(initial_m - offset, initial_m + offset + step, step)
    bs = []

    e = []
    for index, m_val in enumerate(m_values):
        mx = m_val * x_values[start_index:]
        b = -m_val * start_time + DCM[start_index]
        bs.append(b)
        e.append((index, sum((DCM[start_index:] - (mx + b)) ** 2)))

    best_m_tuple = min(e, key=lambda t: t[1])
    lowest_error = best_m_tuple[1]
    best_m = m_values[best_m_tuple[0]]

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
            )
        ],
        layout=go.Layout(
            xaxis=dict(title=r'$\large{\text{Tiempo (s)}}$', dtick=5, tick0=0, linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\large{\text{DCM } (\text{m}^{\text{2}})}$', linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            plot_bgcolor='rgba(0,0,0,0)',
            font=dict(
                family="Computer Modern",
                size=22,
            )
        )
    )

    # Set figure size
    fig.update_layout(width=1800, height=1000)

    fig.show()

    fig = go.Figure(
        data=[
            go.Scatter(
                x=m_values,
                y=list(map(lambda t: t[1], e)),
                mode='lines',
                showlegend=False
            ),
        ],
        layout=go.Layout(
            title=dict(text=f'Error - Best M = {best_m}', x=0.5),
            xaxis=dict(title=r'$\large{\text{Pendiente }(\frac{\text{m}^{\text{2}}}{\text{s}})}$',
                       linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\large{\text{Error }(\text{m}^{\text{2}})}$', exponentformat="power",
                       linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            plot_bgcolor='rgba(0,0,0,0)',
            font=dict(
                family="Computer Modern",
                size=22,
            )
        )
    )
    # Set figure size
    fig.update_layout(width=1400, height=1000)

    fig.show()

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
                x=x_values[start_index:],
                y=x_values[start_index:] * best_m + bs[best_m_tuple[0]],
                line=dict(color='red'),
                mode='lines',
                showlegend=False
            ),
            # go.Scatter(
            #     x=x_values[start_index:],
            #     y=x_values[start_index:] * m_values[20] + bs[20],
            #     line=dict(color='red'),
            #     mode='lines',
            #     showlegend=False
            # ),
            # go.Scatter(
            #     x=x_values[start_index:],
            #     y=x_values[start_index:] * m_values[-20] + bs[-20],
            #     line=dict(color='red'),
            #     mode='lines',
            #     showlegend=False
            # ),
        ],
        layout=go.Layout(
            title=dict(text=f'Big Particle DCM - Lowest error = {lowest_error}', x=0.5),
            xaxis=dict(title=r'$\large{\text{Tiempo (s)}}$', dtick=5, tick0=0, linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\large{\text{DCM } (\text{m}^{\text{2}})}$', linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            plot_bgcolor='rgba(0,0,0,0)',
            font=dict(
                family="Computer Modern",
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
