import glob
import re
from datetime import datetime

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_states


def plot_linear_approximation(position_per_date_folder: str):
    exit_time_files = glob.glob(position_per_date_folder + '/exit*')

    get_filename_frequency = lambda f: int(re.search("\d+", f.split('/')[-1]).group())

    exit_time_files.sort(key=lambda f: get_filename_frequency(f))

    data = []

    for i, file in enumerate(exit_time_files):
        w = get_filename_frequency(file)
        print(f'''{datetime.now().strftime("%H:%M:%S")} - File {i + 1} w = {w}''')

        dfs = get_particles_states(file)
        dfs_data_accum_count = np.cumsum(np.array(list(map(lambda df: len(df.data.id), dfs))))
        dfs_time = np.array(list(map(lambda df: df.time, dfs)))

        data.append(
            go.Scatter(
                # mode="markers+lines",
                mode="lines",
                marker=dict(size=10),
                name=f'''w = {w}''',
                x=dfs_time,
                y=dfs_data_accum_count)
        )

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
    dates_folder = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/frequencies'
    plot_linear_approximation(dates_folder)
