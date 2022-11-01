import glob
import re
from datetime import datetime

import numpy as np
import plotly.graph_objects as go
from scipy.stats import linregress

from utils.parser_utils import get_particles_states_phase


# En una figura mostrar las curvas de descarga (Nro. de partículas que salieron en función del
# tiempo, las cuales se obtienen del output que guarda sólo los tiempos de salida de cada partícula con
# la mayor precisión dada por el dt de integración) de todos los casos.
def plot_discharge_curves(position_per_date_folder: str):
    exit_time_files = glob.glob(position_per_date_folder + '/exit*')

    get_filename_frequency = lambda f: int(re.search("\d+", f.split('/')[-1]).group())

    exit_time_files.sort(key=lambda f: get_filename_frequency(f))

    data = []
    flows = []
    flow_stds = []
    ws = []

    for i, file in enumerate(exit_time_files):
        w = get_filename_frequency(file)
        ws.append(w)

        print(f'''{datetime.now().strftime("%H:%M:%S")} - File {i + 1} w = {w}''')

        dfs = get_particles_states_phase(file)
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

        regression = linregress(dfs_time, y=dfs_accum_count)
        flow_stds.append(regression.stderr)
        flows.append(regression.slope)

        ECM = (1 / len(dfs_time)) * sum((dfs_accum_count - dfs_time * regression.slope + regression.intercept) ** 2)

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
                    y=dfs_time * regression.slope + regression.intercept,
                    line=dict(color='red'),
                    mode='lines',
                    showlegend=False
                ),
            ],
            layout=go.Layout(
                title=dict(text=f'ECM = {ECM} - Q = {regression.slope} - w = {w}', x=0.5),
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
                        error_y=dict(type='data', array=flow_stds),
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
    dates_folder = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/frequenciesPhase'
    plot_discharge_curves(dates_folder)
