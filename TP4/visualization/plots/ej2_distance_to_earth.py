import glob
import re

from datetime import datetime
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


def plot_distance_to_earth(static_files: str, position_per_date_folder: str):
    position_per_date_files = glob.glob(position_per_date_folder + '/*')

    filename_to_date = lambda f: datetime.strptime(f.split("/")[-1], '%Y-%b-%d %H:%M:%S.%f')

    position_per_date_files.sort(key=lambda f: filename_to_date(f))

    min_distances = []
    date_strs = []

    orbit_len = 1500 + 6371

    for i, file in enumerate(position_per_date_files):
        date_str = filename_to_date(file).strftime("%d-%m-%Y %H:%M")

        print(f'''{datetime.now().strftime("%H:%M:%S")} - File {i + 1} {date_str}''')
        date_strs.append(date_str)

        dfs = get_particles_data(static_files, file)
        step = dfs[1].time - dfs[0].time

        dfs_data = np.array(list(map(lambda df: df.data, dfs)))
        distances = np.sqrt((dfs_data[:, 1, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 1, 2] - dfs_data[:, 3, 2]) ** 2)
        min_distance = min(distances)
        min_distances.append(min_distance)

        print(
            f'''  Min distance: {min_distance}km\n'''
            f'''  Time: {np.where(distances == min_distance)[0][0] * step / (60 * 60 * 24)}''')
        if min_distance < orbit_len:
            print(f'''  Inside Orbit!!''')

    data = go.Scatter(
        x=date_strs,
        y=min_distances)

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Min Distance to earth per launch date', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Día de salida}}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Distancia (km)}}$', exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=22,
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
    dates_folder = '../../results/ej2/multipleRuns/toEarth/2023-05-22_1030_5mins'
    plot_distance_to_earth(static_file, dates_folder)
