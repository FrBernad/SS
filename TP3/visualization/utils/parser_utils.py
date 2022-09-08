from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from pandas import DataFrame


def get_frame_particles(df: DataFrame):
    particles = od.Particles()

    square_points = _generate_square(6)

    particles.create_property('Particle Identifier',
                              data=np.concatenate((df.id, np.arange(len(df.x), len(df.x) + len(square_points)))))
    particles.create_property('Position',
                              data=np.concatenate((np.array((df.x, df.y, np.zeros(len(df.x)))).T, square_points)))
    particles.create_property('Radius', data=np.concatenate((df.radius, np.full(len(square_points), 0.03))))
    particles.create_property('Type',
                              data=np.concatenate(([0], np.full(len(df.x) - 1, 1), np.full(len(square_points), 2))))
    particles.create_property('angle', data=np.concatenate((df.angle, np.zeros(len(square_points)))))
    particles.create_property('Force',
                              data=np.concatenate((np.array((np.cos(df.angle) * df.speed, np.sin(df.angle) * df.speed,
                                                             np.zeros(len(df.x)))).T,
                                                   np.zeros((len(square_points), 3))))
                              )

    return particles


def _generate_square(L: int):
    square_points = []
    for x in np.arange(0, L + 0.05, 0.05):
        square_points.append([x, L, 0])
        square_points.append([x, 0, 0])
        square_points.append([L, x, 0])
        square_points.append([0, x, 0])

    return np.array(square_points)


def get_particles_data(static_file: str, results_file: str) -> List[DataFrame]:
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "mass"])

    dfs = []
    with open(results_file, "r") as results:
        next(results)
        current_frame = []
        for line in results:
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
