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
def plot_distance_to_venus(static_files: str, results_file: str):
    dfs = get_particles_data(static_files, results_file)

    dfs_data = np.array(list(map(lambda df: df.data, dfs)))
    dfs_time = np.array(list(map(lambda df: df.time, dfs)))
    speeds = np.sqrt((dfs_data[:, 2, 3] - dfs_data[:, 3, 3]) ** 2 + (dfs_data[:, 2, 4] - dfs_data[:, 3, 4]) ** 2)

    distances = np.sqrt((dfs_data[:, 2, 1] - dfs_data[:, 3, 1]) ** 2 + (dfs_data[:, 2, 2] - dfs_data[:, 3, 2]) ** 2)
    min_distance = min(distances)
    step = 300 * 6
    min_index = np.where(distances == min_distance)[0][0]

    relative_speed_venus = ((dfs_data[min_index, 2, 3] - dfs_data[min_index, 3, 3]),
                            (dfs_data[min_index, 2, 4] - dfs_data[min_index, 3, 4]))

    print(f'''Min distance: {min_distance} km\n'''
          f'''Time: {min_index * step / (60 * 60 * 24)} days / {min_index * step / (60 * 60)} hrs\n'''
          f'''Relative velocity: {relative_speed_venus} km/s''')

    data = go.Scatter(
        x=dfs_time,
        y=speeds)

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Speed optimal trip', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Tiempos (s)}}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, exponentformat="power", tickcolor='black',
                       ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Rapidez }(\frac{\text{km}}{\text{s}})}$', exponentformat="power",
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
    results_file = '../../results/ej2/multipleRuns/2022-10-26_h/2022-Oct-26 22:00:00.0000'
    plot_distance_to_venus(static_file, results_file)
