from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from ovito.pipeline import StaticSource
from pandas import DataFrame


def get_particles_static_source(dynamic_file: str, static_file: str, neighbors: List[int], particle_id: int):
    dynamic_df = pd.read_csv(dynamic_file, skiprows=1, sep=" ", names=["x", "y"])
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "prop"])

    # Create a Particles object containing two particles:
    data = od.DataCollection()

    cell = od.SimulationCell(pbc=(False, False, False), is2D=True)
    cell[:, 0] = (4, 0, 0)
    cell[:, 1] = (0, 2, 0)
    cell[:, 2] = (0, 0, 2)
    data.objects.append(cell)

    ids = np.arange(1, len(dynamic_df.x) + 1)
    particles = od.Particles()
    particles.create_property('Particle Identifier', data=ids)
    particles.create_property('Position', data=np.array((dynamic_df.x, dynamic_df.y, np.zeros(len(dynamic_df.x)))).T)
    particles.create_property('Radius', data=static_df.radius)
    particles.create_property('Neighbor', data=[1 if i in neighbors or i == particle_id else 0 for i in ids])
    data.objects.append(particles)

    return StaticSource(data=data)


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
