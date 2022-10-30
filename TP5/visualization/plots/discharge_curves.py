import glob
import re
from datetime import datetime

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_states


# En una figura mostrar las curvas de descarga (Nro. de partículas que salieron en función del
# tiempo, las cuales se obtienen del output que guarda sólo los tiempos de salida de cada partícula con
# la mayor precisión dada por el dt de integración) de todos los casos.
def plot_discharge_curves(position_per_date_folder: str):
    exit_time_files = glob.glob(position_per_date_folder + '/exit*')

    get_filename_frequency = lambda f: int(re.search("\d+", f.split('/')[-1]).group())

    exit_time_files.sort(key=lambda f: get_filename_frequency(f))

    data = []
    flows = []
    flow_errors = []
    ws = []

    for i, file in enumerate(exit_time_files):
        w = get_filename_frequency(file)
        ws.append(w)

        print(f'''{datetime.now().strftime("%H:%M:%S")} - File {i + 1} w = {w}''')

        dfs = get_particles_states(file)
        dfs_accum_count = np.cumsum(np.array(list(map(lambda df: len(df.data.id), dfs))))
        dfs_time = np.array(list(map(lambda df: df.time, dfs)))

        # Discharge curves
        data.append(
            go.Scatter(
                # mode="markers+lines",
                mode="lines",
                marker=dict(size=10),
                name=f'w = {w} Hz',
                x=dfs_time,
                y=dfs_accum_count)
        )

        initial_m = (dfs_accum_count[-1] - dfs_accum_count[0]) / (dfs_time[-1] - dfs_time[0])
        step = 0.00005
        offset = 0.05
        m_values = np.arange(initial_m - offset, initial_m + offset + step, step)
        bs = []

        ECMs = []
        errors = []
        for index, m_val in enumerate(m_values):
            mx = m_val * dfs_time
            b = 0
            bs.append(b)
            errors.append((1 / len(dfs_accum_count)) * (dfs_accum_count - (mx + b)) ** 2)
            ECMs.append((index, sum(errors[-1])))

        best_m_tuple = min(ECMs, key=lambda t: t[1])
        lowest_error = best_m_tuple[1]

        best_m = m_values[best_m_tuple[0]]
        flows.append(best_m)

        flow_errors.append(lowest_error)

        fig = go.Figure(
            data=[
                go.Scatter(
                    x=m_values,
                    y=list(map(lambda t: t[1], ECMs)),
                    mode='lines',
                    showlegend=False
                ),
            ],
            layout=go.Layout(
                title=dict(text=f'Error - Best Flow = {best_m} - w = {w}', x=0.5),
                xaxis=dict(title=r'$\Large{\text{Pendiente }(\frac{\text{1}}{\text{s}})}$',
                           linecolor="#000000", ticks="outside",
                           tickwidth=2, tickcolor='black', ticklen=10),
                yaxis=dict(title=r'$\Large{\text{Error}}$', exponentformat="power",
                           linecolor="#000000", ticks="outside",
                           tickwidth=2, tickcolor='black', ticklen=10),
                plot_bgcolor='rgba(0,0,0,0)',
                font=dict(
                    family="Computer Modern",
                    size=26,
                )
            )
        )
        fig.update_layout(width=1000, height=1000)

        fig.show()

        fig = go.Figure(
            data=[
                go.Scatter(
                    x=dfs_time,
                    y=dfs_accum_count,
                    mode='lines',
                    showlegend=False
                ),
                go.Scatter(
                    x=dfs_time,
                    y=dfs_time * best_m + bs[best_m_tuple[0]],
                    line=dict(color='red'),
                    mode='lines',
                    showlegend=False
                ),
            ],
            layout=go.Layout(
                title=dict(text=f'Flow Lineal Approximation - Lowest error = {lowest_error} - w = {w}', x=0.5),
                xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$',
                           linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
                yaxis=dict(title=r'$\Large{\text{ECM}}$',
                           linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
                plot_bgcolor='rgba(0,0,0,0)',
                font=dict(
                    family="Computer Modern",
                    size=22,
                )
            )
        )

        fig.update_layout(width=1000, height=1000)

        fig.show()

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Discharge curves D = 3', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Número de partículas}}$', exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=26,
            ),
            plot_bgcolor='rgba(0,0,0,0)',
        )

    )

    fig.update_layout(width=1300, height=1000)

    fig.show()

    fig = go.Figure(
        data=go.Scatter(x=ws,
                        y=flows,
                        error_y=dict(type='data', array=flow_errors),
                        mode="markers+lines",
                        marker=dict(size=10),
                        showlegend=False
                        ),
        layout=go.Layout(
            title=dict(text=f'Flow errors - D = 3', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Frecuencia }(Hz)}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Caudal }(\frac{\text{1}}{\text{s}})}$',
                       exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=26,
            ),
            plot_bgcolor='rgba(0,0,0,0)',
        )

    )

    fig.update_layout(width=1200, height=1000)

    fig.show()


if __name__ == "__main__":
    dates_folder = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/frequencies'
    plot_discharge_curves(dates_folder)
