import sys
from datetime import datetime

from ovito.data import DataCollection, SimulationCell
from ovito.io import export_file
from ovito.pipeline import Pipeline, StaticSource

from utils.argument_parser import parse_arguments
from utils.config import get_config
from utils.parser_utils import get_frame_particles, get_particles_data_phase


def visualization_ovito(config_file: str):
    print("Visualization Starting ...")

    config = get_config(config_file)

    print(f'''{datetime.now().strftime("%H:%M:%S")} - Getting Particles Data ...''')
    dfs = get_particles_data_phase(config.static_file, config.results_file)
    pipeline = Pipeline(source=StaticSource(data=DataCollection()))

    def create_particle_pos(frame, data):
        cell = SimulationCell(pbc=(False, False, False), is2D=True)
        cell[:, 0] = (4, 0, 0)
        cell[:, 1] = (0, 2, 0)
        cell[:, 2] = (0, 0, 2)
        data.objects.append(cell)

        if frame % 1000 == 0:
            print(f'''{datetime.now().strftime("%H:%M:%S")} - Parsing frame {frame}''')

        particles = get_frame_particles(dfs[frame])
        data.objects.append(particles)

    pipeline.modifiers.append(create_particle_pos)

    export_file(pipeline, '../results/visualization.dump', 'lammps/dump',
                columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius",
                         "Force.X", "Force.Y", "Force.Z", "Is Wall"],
                multiple_frames=True, start_frame=0, end_frame=len(dfs) - 1)

    print(f'''{datetime.now().strftime("%H:%M:%S")} - Exporting File ...''')
    print(f'''{datetime.now().strftime("%H:%M:%S")} - Done!''')


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
