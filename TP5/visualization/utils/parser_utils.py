from collections import namedtuple
from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from pandas import DataFrame

EventData = namedtuple('EventData', ['time', 'data'])


def get_frame_particles(df: DataFrame):
    particles = od.Particles()

    particles.create_property('Particle Identifier',
                              data=np.concatenate((df.id, np.arange(len(df.x), len(df.x)))))
    particles.create_property('Position',
                              data=np.array((df.x, df.y, np.zeros(len(df.x)))).T)
    particles.create_property('Radius', data=df.radius)
    particles.create_property('Force',
                              data=np.array((df.vx, df.vy, np.zeros(len(df.x)))).T
                              )
    return particles


def get_particles_data(static_file: str, results_file: str) -> List[EventData]:
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "mass"])

    dfs = []
    with open(results_file, "r") as results:
        current_frame_time = float(next(results))
        current_frame = []
        for line in results:
            float_vals = list(map(lambda v: float(v), line.split()))
            if len(float_vals) > 1:
                current_frame.append(float_vals)
            elif len(float_vals) == 1:
                df = pd.DataFrame(np.array(current_frame), columns=["id", "x", "y", "vx", "vy"])
                dfs.append(EventData(current_frame_time, pd.concat([df, static_df], axis=1)))
                current_frame = []
                current_frame_time = float_vals[0]
        df = pd.DataFrame(np.array(current_frame), columns=["id", "x", "y", "vx", "vy"])
        dfs.append(EventData(current_frame_time, pd.concat([df, static_df], axis=1)))

    return dfs


def get_particles_initial_data(static_file: str, dynamic_file: str) -> List[DataFrame]:
    dynamic_df = pd.read_csv(dynamic_file, skiprows=1, sep=" ", names=["x", "y", "vx", "vy"])
    static_df = pd.read_csv(static_file, skiprows=2, sep=" ", names=["radius", "mass"])

    return pd.concat([dynamic_df, static_df], axis=1)
