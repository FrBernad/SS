import ovito.data as od
import pandas
import pandas as pd
from ovito.pipeline import StaticSource
from pandas import DataFrame


def get_particles_static_source(dynamic_file: str, static_file: str):
    position = []
    identifier = []
    radius = []
    with open(dynamic_file) as df:
        next(df)
        particle_index = 0
        for line in df.readlines():
            line_parts = line.split(' ')
            position.append([float(line_parts[0]), float(line_parts[1]), 0])
            identifier.append(particle_index)
            particle_index += 1

    with open(static_file) as sf:
        next(sf)
        next(sf)
        particle_index = 0
        for line in sf.readlines():
            line_parts = line.split(' ')
            radius.append(float(line_parts[0]))
            particle_index += 1

    # Create a Particles object containing two particles:
    data = od.DataCollection()

    cell = od.SimulationCell(pbc=(False, False, False), is2D=True)
    cell[:, 0] = (4, 0, 0)
    cell[:, 1] = (0, 2, 0)
    cell[:, 2] = (0, 0, 2)
    data.objects.append(cell)

    particles = od.Particles()
    particles.create_property('Particle Identifier', data=identifier)
    particles.create_property('Position', data=position)
    particles.create_property('Radius', data=radius)
    data.objects.append(particles)

    return StaticSource(data=data)


def get_particles_data(dynamic_file: str, static_file: str) -> DataFrame:
    dynamic_df = pd.read_csv(dynamic_file, skiprows=1, sep=" ", names=["x", "y"])
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "prop"])

    return pandas.concat([dynamic_df, static_df], axis=1)


def get_neighbors_data(neighbors_file: str) -> dict:
    neighbors = dict()
    with open(neighbors_file) as f:
        for line in f.readlines():
            aux = [int(i) for i in line.split(" ")[:-1]]
            neighbors[aux[0]] = aux[1:]

    return neighbors
