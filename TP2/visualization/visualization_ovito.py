import sys

from ovito.data import DataCollection, SimulationCell
from ovito.io import export_file
from ovito.pipeline import Pipeline, StaticSource

from utils.argument_parser import parse_arguments
from utils.config import get_config
from utils.parser_utils import get_frame_particles
from utils.parser_utils import get_particles_data


def visualization_ovito(config_file: str):
    config = get_config(config_file)
    dfs = get_particles_data(config.static_file, config.flocks_file, config.particle_R)

    pipeline = Pipeline(source=StaticSource(data=DataCollection()))

    def create_particle_pos(frame, data):
        cell = SimulationCell(pbc=(False, False, False), is2D=True)
        cell[:, 0] = (4, 0, 0)
        cell[:, 1] = (0, 2, 0)
        cell[:, 2] = (0, 0, 2)
        data.objects.append(cell)

        particles = get_frame_particles(dfs[frame])
        data.objects.append(particles)

    pipeline.modifiers.append(create_particle_pos)

    export_file(pipeline, '../results/visualization.dump', 'lammps/dump',
                columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius", "Angle",
                         "Force.X", "Force.Y", "Force.Z"],
                multiple_frames=True, start_frame=0, end_frame=len(dfs) - 1)


if __name__ == "__main__":
    arguments = parse_arguments(sys.argv[1:])

    config_file = arguments['config_file']

    try:
        visualization_ovito(config_file)
    except FileNotFoundError as e:
        print("File not found")
        print(e)
    except OSError:
        print("Error occurred.")
    except KeyboardInterrupt:
        print('Program interrupted by user.')
    except Exception as e:
        print(e)
