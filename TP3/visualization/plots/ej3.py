import numpy as np
import plotly.graph_objects as go

from utils.parser_utils import get_particles_data


# Graficar la trayectoria de la partícula grande para distintas temperaturas.
# Cómo se cambia la temperatura en el sistema simulado?
# (No es necesario calcular esta temperatura en grados, solo interesan cambios relativos
# de temperatura dados por alguno de los inputs del sistema).
def big_particle_trajectory(static_file: str, results_file: str):
    dfs = get_particles_data(static_file, results_file)

    big_particle_positions = np.array(list(map(lambda df: (
        df.data[df.data['mass'] == 2][["x", "y"]].values.flatten()
    ), dfs)))

    kinetic_energy = np.sum((1 / 2) * dfs[0].data["mass"] * (np.linalg.norm(dfs[0].data[["vx", "vy"]], axis=1) ** 2))

    # Graficar la distribución de probabilidades de dichos tiempos
    fig = go.Figure(
        data=go.Scatter(
            x=big_particle_positions[:, 0],
            y=big_particle_positions[:, 1],
        ),
        layout=go.Layout(
            title=dict(text=f'Big Particle Position - N={len(dfs[0].data) - 1} - K={kinetic_energy}', x=0.5),
            xaxis=dict(title='X', range=[0, 6]),
            yaxis=dict(title='Y', range=[0, 6]),
            font=dict(
                family="Arial",
                size=22,
            )
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    static_file = '../../assets/Static.txt'
    results_file = '../../results/results.txt'
    # TODO: Agregar el intervalo de velocidades
    big_particle_trajectory(static_file, results_file)
