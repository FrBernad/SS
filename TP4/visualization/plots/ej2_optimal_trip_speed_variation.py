import glob
import re

from datetime import datetime
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


def plot_speed_variation(static_files: str, position_per_date_folder: str):
    position_per_date_files = glob.glob(position_per_date_folder + '/*')

    position_per_date_files.sort(key=lambda f: float(f.split("/")[-1].split()[-1]))

    min_distances = []

    # speeds = np.arange(7.990, 8.010 + 0.001, 0.001)
    # speeds = np.arange(4.390, 4.410 + 0.001, 0.001)
    # speeds = np.arange(2, 8 + 0.1, 0.1)
    speeds = np.arange(4, 12 + 0.1, 0.1)

    orbit_len = 1500 + 6052

    for i, file in enumerate(position_per_date_files):
        print(f'''{datetime.now().strftime("%H:%M:%S")} - Parsing {speeds[i]}''')

        dfs = get_particles_data(static_files, file)

        dfs_data = np.array(list(map(lambda df: df.data, dfs)))
        distances = np.sqrt((dfs_data[:, 2, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 2, 2] - dfs_data[:, 3, 2]) ** 2)
        # distances = np.sqrt((dfs_data[:, 1, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 1, 2] - dfs_data[:, 3, 2]) ** 2)
        min_distance = min(distances)
        min_distances.append(min_distance)

        step = dfs[1].time - dfs[0].time
        print(
            f'''  Min distance: {min_distance}km'''
            f''' - Time: {np.where(distances == min_distance)[0][0] * step / (60 * 60 * 24)}''')
        if min_distance < orbit_len:
            print(f'''  Inside Orbit!!''')

    data = go.Scatter(
        # mode="markers+lines",
        mode="lines",
        marker=dict(size=10),
        x=speeds,
        y=min_distances)

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Min Distance to venus per launch date', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Rapidez }(\frac{\text{km}}{\text{s}})}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Distancia mínima (km)}}$', exponentformat="power", type="log",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10, dtick=1),
            # yaxis=dict(title=r'$\Large{\text{Distancia mínima (km)}}$', exponentformat="power",
            #            linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
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
    # dates_folder = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP4/results/ej2/multipleRuns/toEarth/speed'
    dates_folder = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP4/results/ej2/multipleRuns/toVenus/speed'
    plot_speed_variation(static_file, dates_folder)
