import math
from math import pi
from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from pandas import DataFrame


def get_frame_particles(df: DataFrame):
    ids = np.arange(1, len(df.x) + 1)
    particles = od.Particles()
    particles.create_property('Particle Identifier', data=ids)
    particles.create_property('Position',
                              data=np.array((df.x, df.y, np.zeros(len(df.x)))).T)
    particles.create_property('Radius', data=df.radius)
    particles.create_property('Angle', data=df.angle)
    particles.create_property('Force', data=np.array((np.cos(df.angle) * df.speed, np.sin(df.angle) * df.speed,
                                                      np.zeros(len(df.x)))).T)

    return particles


def _generate_radius(R: float, x_offset: float, y_offset: float, n: int = 500):
    return [[x_offset + math.cos(2 * pi / n * x) * R, y_offset + math.sin(2 * pi / n * x) * R, 0] for x in
            range(0, n + 1)]


def get_particles_data(static_file: str, flocks_file: str, particle_R: float) -> List[DataFrame]:
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "prop"])
    static_df['radius'].replace([0], particle_R, inplace=True)

    dfs = []
    with open(flocks_file, "r") as flocks:
        next(flocks)
        current_frame = []
        for line in flocks:
            float_vals = list(map(lambda v: float(v), line.split()))
            if len(float_vals) > 1:
                current_frame.append(float_vals)
            elif len(float_vals) == 1:
                df = pd.DataFrame(np.array(current_frame), columns=["id", "x", "y", "speed", "angle"])
                dfs.append(pd.concat([df, static_df], axis=1))
                current_frame = []
        df = pd.DataFrame(np.array(current_frame), columns=["id", "x", "y", "speed", "angle"])
        dfs.append(pd.concat([df, static_df], axis=1))

    return dfs
