from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from pandas import DataFrame


def get_frame_particles(df: DataFrame):
    particles = od.Particles()
    particles.create_property('Particle Identifier', data=df.id)
    particles.create_property('Position',
                              data=np.array((df.x, df.y, np.zeros(len(df.x)))).T)
    particles.create_property('Radius', data=df.radius)
    particles.create_property('angle', data=df.angle)
    particles.create_property('Force', data=np.array((np.cos(df.angle) * df.speed, np.sin(df.angle) * df.speed,
                                                      np.zeros(len(df.x)))).T)

    return particles


def get_particles_data(static_file: str, results_file: str) -> List[DataFrame]:
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "prop"])

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
