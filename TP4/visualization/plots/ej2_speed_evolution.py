import glob
import re

from datetime import datetime
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


def plot_speed_evolution(static_files: str, results_file: str):
    dfs = get_particles_data(static_files, results_file)

    dfs_data = np.array(list(map(lambda df: df.data, dfs)))
    dfs_time = np.array(list(map(lambda df: df.time, dfs)))

    # to venus

    # speeds = np.sqrt((dfs_data[:, 2, 3] - dfs_data[:, 3, 3]) ** 2 + (dfs_data[:, 2, 4] - dfs_data[:, 3, 4]) ** 2)
    #
    # distances = np.sqrt((dfs_data[:, 2, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 2, 2] - dfs_data[:, 3, 2]) ** 2)
    # min_distance = min(distances)
    # step = dfs_time[1] - dfs_time[0]
    #
    # min_index = np.where(distances == min_distance)[0][0]
    #
    # relative_speed_venus = ((dfs_data[min_index, 2, 3] - dfs_data[min_index, 3, 3]),
    #                         (dfs_data[min_index, 2, 4] - dfs_data[min_index, 3, 4]))

    # to earth
    speeds = np.sqrt((dfs_data[:, 1, 3] - dfs_data[:, 3, 3]) ** 2 + (dfs_data[:, 1, 4] - dfs_data[:, 3, 4]) ** 2)

    distances = np.sqrt((dfs_data[:, 1, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 1, 2] - dfs_data[:, 3, 2]) ** 2)
    min_distance = min(distances)
    step = dfs_time[1] - dfs_time[0]

    min_index = np.where(distances == min_distance)[0][0]

    relative_speed_venus = ((dfs_data[min_index, 1, 3] - dfs_data[min_index, 3, 3]),
                            (dfs_data[min_index, 1, 4] - dfs_data[min_index, 3, 4]))

    print(f'''Min distance: {min_distance} km\n'''
          f'''Time: {min_index * step / (60 * 60 * 24)} days / {min_index * step / (60 * 60)} hrs\n'''
          f'''Relative velocity: {relative_speed_venus} km/s - {np.linalg.norm(relative_speed_venus)}''')

    speed_step = int(1 * 60 * 60 / step)  # steps de 1 hora

    data = go.Scatter(
        x=dfs_time[:min_index:speed_step],
        y=speeds[:min_index:speed_step])

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Speed optimal trip', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, exponentformat="power", tickcolor='black',
                       ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Rapidez }(\frac{\text{km}}{\text{s}})}$', exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=26,
            ),
            plot_bgcolor='rgba(0,0,0,0)',
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
    static_file = '../../assets/ej2/StaticPlanets'
    # results_file = '../../results/ej2/multipleRuns/toVenus/2023-05-12_1045_5mins/2023-May-12 11:15:00.0000'
    results_file = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP4/results/ej2/multipleRuns/toEarth/2025-01-04_1920_5mins/2025-Jan-04 20:10:00.0000'
    plot_speed_evolution(static_file, results_file)
