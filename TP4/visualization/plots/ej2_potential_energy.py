import glob
import re

from datetime import datetime
from typing import Tuple, List

import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


def plot_potential_energy(static_files: str, results_dir: str):
    G = 6.693 * pow(10, -11) / pow(10, 9)

    dts_files = glob.glob(results_dir + '/*')

    dts_files.sort(key=lambda f: float(f.split("/")[-1]))

    data = []

    for i, file in enumerate(dts_files):
        dt = file.split("/")[-1]
        print(f'''{datetime.now().strftime("%H:%M:%S")} - Parsing dt {dt}''')

        dfs = get_particles_data(static_files, file)

        dfs_data = np.array(list(map(lambda df: df.data, dfs)))
        dfs_time = np.array(list(map(lambda df: df.time, dfs)))

        potential_energy = np.zeros((4, len(dfs_data)))
        kinetic_energy = np.zeros((4, len(dfs_data)))
        for i in range(4):
            speed = np.sqrt((dfs_data[:, i, 3] ** 2) + (dfs_data[:, i, 4] ** 2))
            mass = dfs_data[:, i, 6]
            kinetic_energy[i] += (mass * speed ** 2) / 2

            for j in range(4):
                if i != j:
                    mass_mult = dfs_data[:, i, 6] * dfs_data[:, j, 6]
                    distances = np.sqrt(
                        (dfs_data[:, i, 1] - dfs_data[:, j, 1]) ** 2 + (dfs_data[:, i, 2] - dfs_data[:, j, 2]) ** 2)

                    potential_energy[i] += (mass_mult * - G / distances)

        energy = kinetic_energy[3] + potential_energy[3]

        variation = (100 * (energy - energy[0])) / energy[0]

        data.append(go.Scatter(
            x=dfs_time,
            y=variation,
            name=f'''dt = {dt} s'''
        ))

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Total Energy', x=0.5),
            xaxis=dict(title=r'$\Large{\text{Tiempo (s)}}$',
                       linecolor="#000000", ticks="outside", tickwidth=2, exponentformat="power", tickcolor='black',
                       ticklen=10),
            yaxis=dict(title=r'$\Large{\text{Variación de Energía}}$',
                       exponentformat="power",
                       linecolor="#000000", ticks="outside", tickwidth=2, tickcolor='black', ticklen=10),
            font=dict(
                family="Computer Modern",
                size=22,
            ),
            plot_bgcolor='rgba(0,0,0,0)',
        )

    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/ej2/StaticPlanets'
    results_dir = '/Users/frbernad/PROGRAMMING/ITBA/SS/TPs/TP4/results/ej2/multipleRuns/toVenus/energy'
    plot_potential_energy(static_file, results_dir)
