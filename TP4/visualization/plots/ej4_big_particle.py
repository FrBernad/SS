import glob

import numpy as np
import plotly.graph_objects as go

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
    for i, file in enumerate(glob.glob(f'''{results_dir}run*''')):
        print(f'''Parsing run {i}''')
        runs.append(get_particles_data(static_file, file))

    time_step = 0.2
    clock_dfs = []
    for dfs in runs:
        current_time = 0
        clock_df = []
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
    start_time = 2
    start_index = int(start_time / time_step)

    initial_m = (DCM[-1] - DCM[start_index]) / (x_values[-1] - x_values[start_index])
    init_b = -initial_m * start_time + DCM[start_index]
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
        ],
        layout=go.Layout(
            title=dict(text=f'Big Particle DCM - y = ', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$', dtick=2, tick0=0,
                       linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{DCM }(\text{m}^{\text{2}})}$',
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
            xaxis=dict(title=r'$\Large{\text{Pendiente }(\frac{\text{m}^{\text{2}}}{\text{s}})}$',
                       linecolor="#000000", ticks="outside",
                       tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Error }(\text{m}^{\text{2}})}$', exponentformat="power",
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
            xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$', dtick=2, tick0=0, linecolor="#000000",
                       ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{DCM } (\text{m}^{\text{2}})}$', linecolor="#000000",
                       ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
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
    static_file = '../../results/results4BIG/Static.txt'
    results_dir = '../../results/results4BIG/'
    big_particle_DCM(static_file, results_dir)
