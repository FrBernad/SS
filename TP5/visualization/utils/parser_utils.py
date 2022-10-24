from collections import namedtuple
from typing import List

import numpy as np
import ovito.data as od
import pandas as pd
from numpy._typing import NDArray
from pandas import DataFrame

EventData = namedtuple('EventData', ['time', 'data'])


def get_frame_particles(df: DataFrame, time_step: float):
    particles = od.Particles()

    time = np.arange(0, time_step * len(df.id), time_step)
    silo_points = _generate_silo(70, 20, 4, 0, time)

    particles.create_property('Particle Identifier',
                              data=np.concatenate((df.id, np.full(len(silo_points), max(df.id) + 1))))
    particles.create_property('Position',
                              data=np.concatenate((np.array((df.x, df.y, np.zeros(len(df.x)))).T, silo_points)))
    particles.create_property('Radius', data=np.concatenate((df.radius, np.full(len(silo_points), 0.2))))
    particles.create_property('Force',
                              data=np.concatenate((np.array((df.vx, df.vy, np.zeros(len(df.x)))).T,
                                                   np.zeros((len(silo_points), 3))))
                              )
    particles.create_property('Is Wall',
                              data=np.concatenate((np.full(len(df.id), 0.2), np.full(len(silo_points), 0)))
                              )
    return particles


def _generate_silo(L: int, W: int, w: float, D: int, time: NDArray[float]):
    y = 0.15 * np.sin(w * time)

    left_wall = [[0, y, 0] for y in np.arange(0, L, 0.5)]
    right_wall = [[W, y, 0] for y in np.arange(0, L, 0.5)]
    bottom_wall = [[x, 0, 0] for x in np.arange(0, W, 0.5)]

    return np.array(left_wall + right_wall + bottom_wall)


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
