import glob
import re

from datetime import datetime
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# El momento en el futuro (fecha y cuántos días desde 23/09/2022) en el cual la nave debería partir
# para asegurar el arribo a Venus. Para ello, graficar la mínima distancia nave-Venus en función de la
# fecha de salida.
def plot_distance_to_venus(static_files: str, position_per_date_folder: str):
    position_per_date_files = glob.glob(position_per_date_folder + '/*')

    filename_to_date = lambda f: datetime.strptime(f.split("/")[-1], '%Y-%b-%d %H:%M:%S.%f')

    position_per_date_files.sort(key=lambda f: filename_to_date(f))

    min_distances = []
    date_strs = []

    orbit_len = 1500 + 6052
    step = 300 * 6

    for file in position_per_date_files:
        date_str = filename_to_date(file).strftime("%d-%m-%Y")

        print(f'''{datetime.now().strftime("%H:%M:%S")} - Parsing {date_str}''')
        date_strs.append(date_str)

        dfs = get_particles_data(static_files, file)

        dfs_data = np.array(list(map(lambda df: df.data, dfs)))
        distances = np.sqrt((dfs_data[:, 2, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 2, 2] - dfs_data[:, 3, 2]) ** 2)
        min_distance = min(distances)
        min_distances.append(min_distance)
        # 23-10-2022 - 23-05-2023 - 23-02-2024 23-07-2024 23-01-2025 23-04-2025 23-08-2025

        print(
            f'''  Min distance: {min_distance}km'''
            f''' - Time:{np.where(distances == min_distance)[0][0] * step / (60 * 60 * 24)}''')
        if min_distance < orbit_len:
            print(f'''  Inside Orbit!!''')

    data = go.Scatter(
        x=date_strs,
        y=min_distances)

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Min Distance to venus per launch date', x=0.5),
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
    dates_folder = '../../results/ej2/multipleRuns/2years'
    plot_distance_to_venus(static_file, dates_folder)
