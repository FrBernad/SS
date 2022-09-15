from typing import List, Tuple

import numpy as np
import plotly
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# Graficar la trayectoria de la partícula grande para distintas temperaturas.
# Cómo se cambia la temperatura en el sistema simulado?
# (No es necesario calcular esta temperatura en grados, solo interesan cambios relativos
# de temperatura dados por alguno de los inputs del sistema).
def big_particle_trajectory(run_files: List[Tuple]):
    fig = go.Figure(
        layout=go.Layout(
            xaxis=dict(title='X', range=[0, 6]),
            yaxis=dict(title='Y', range=[0, 6]),
            font=dict(
                family="Arial",
                size=22,
            ),
            legend=dict(
                yanchor="top",
                y=0.99,
                xanchor="right",
                x=0.99
            )
        )
    )
    speeds = ["v=[0, 1] m/s", "v=[1, 2] m/s", "v=[2, 4] m/s"]
    colors = plotly.colors.n_colors("rgb(0,0,205)", "rgb(135,206,235)", len(run_files), colortype="rgb")

    for i, files in enumerate(run_files):
        dfs = get_particles_data(files[0], files[1])

        big_particle_positions = np.array(list(map(lambda df: (
            df.data[df.data['mass'] == 2][["x", "y"]].values.flatten()
        ), dfs)))

        kinetic_energy = np.sum(
            (1 / 2) * dfs[0].data["mass"] * (np.linalg.norm(dfs[0].data[["vx", "vy"]], axis=1) ** 2))

        fig.add_trace(go.Scatter(
            x=big_particle_positions[:, 0],
            y=big_particle_positions[:, 1],
            name=speeds[i],
            mode="lines",
            line=dict(color=colors[i - 1])
        ))

        x = big_particle_positions[-1, 0]
        y = big_particle_positions[-1, 1]
        radius = dfs[0].data["radius"][0]
        x0 = x - radius
        x1 = x + radius
        y0 = y - radius
        y1 = y + radius
        # Add circles
        fig.add_shape(type="circle",
                      xref="x", yref="y",
                      x0=x0, y0=y0, x1=x1, y1=y1,
                      fillcolor=colors[i - 1],
                      opacity=0.2,
                      line=dict(width=0))

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file_01 = '../../assets/Static01_120.txt'
    static_file_12 = '../../assets/Static12_120.txt'
    static_file_24 = '../../assets/Static24_120.txt'
    results_file_01 = '../../results/results3/results01_120.txt'
    results_file_12 = '../../results/results3/results12_120.txt'
    results_file_24 = '../../results/results3/results24_120.txt'
    big_particle_trajectory(
        [
            (static_file_01, results_file_01),
            (static_file_12, results_file_12),
            (static_file_24, results_file_24)
        ]
    )
