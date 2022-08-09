import math
from math import pi
from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from ovito.pipeline import StaticSource
from pandas import DataFrame


def get_particles_static_source(df: DataFrame, neighbors: List[int], particle_id: int, R: float):
    # Create a Particles object containing two particles:
    data = od.DataCollection()

    cell = od.SimulationCell(pbc=(False, False, False), is2D=True)
    cell[:, 0] = (4, 0, 0)
    cell[:, 1] = (0, 2, 0)
    cell[:, 2] = (0, 0, 2)
    data.objects.append(cell)

    radius_points = _generate_radius(R+df.radius[particle_id - 1], df.x[particle_id - 1], df.y[particle_id - 1])

    ids = np.arange(1, len(df.x) + len(radius_points) + 1)
    particles = od.Particles()
    particles.create_property('Particle Identifier', data=ids)
    particles.create_property('Position',
                              data=np.concatenate((np.array((df.x, df.y, np.zeros(len(df.x)))).T, radius_points)))
    particles.create_property('Radius', data=np.concatenate((df.radius, np.full(len(radius_points), 0.2))))
    particles.create_property('Neighbor', data=[1 if i in neighbors or i == particle_id
                                                else 2 if i >= len(radius_points) else 0 for i in ids])
    data.objects.append(particles)

    return StaticSource(data=data)


def _generate_radius(R: float, x_offset: float, y_offset: float):
    return [[x_offset + math.cos(2 * pi / 100 * x) * R, y_offset + math.sin(2 * pi / 100 * x) * R, 0] for x in
            range(0, 100 + 1)]


def get_particles_data(dynamic_file: str, static_file: str) -> DataFrame:
    dynamic_df = pd.read_csv(dynamic_file, skiprows=1, sep=" ", names=["x", "y"])
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "prop"])

    return pd.concat([dynamic_df, static_df], axis=1)


def get_neighbors_data(neighbors_file: str) -> dict:
    neighbors = dict()
    with open(neighbors_file) as f:
        for line in f.readlines():
            aux = [int(i) for i in line.split(" ")[:-1]]
            neighbors[aux[0]] = aux[1:]

    return neighbors
