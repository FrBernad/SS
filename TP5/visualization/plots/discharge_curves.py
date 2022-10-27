import glob
import re
from datetime import datetime
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# En una figura mostrar las curvas de descarga (Nro. de partículas que salieron en función del
# tiempo, las cuales se obtienen del output que guarda sólo los tiempos de salida de cada partícula con
# la mayor precisión dada por el dt de integración) de todos los casos.
def plot_discharge_curves(static_files: str, position_per_date_folder: str):
    results_files = glob.glob(position_per_date_folder + '/results*')
    exit_time_files = glob.glob(position_per_date_folder + '/exit*')

    get_filename_frequency = lambda f: int(re.search("\d+", f.split('/')[-1]).group())

    results_files.sort(key=lambda f: get_filename_frequency(f))
    exit_time_files.sort(key=lambda f: get_filename_frequency(f))

    for i, file in enumerate(exit_time_files):
        print(f'''{datetime.now().strftime("%H:%M:%S")} - File {i + 1} w = {get_filename_frequency(file)}''')

        dfs = get_particles_data(static_files, file)
        dfs_data = np.array(list(map(lambda df: df.data, dfs)))

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
    static_file = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/static.txt'
    dates_folder = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP5/results/frequencies'
    plot_discharge_curves(static_file, dates_folder)
