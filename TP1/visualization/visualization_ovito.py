import sys

from ovito.io import export_file
from ovito.pipeline import Pipeline

from utils.argument_parser import parse_arguments
from utils.config import get_config
from utils.parser_utils import get_neighbors_data
from utils.parser_utils import get_particles_static_source


def visualization_ovito(config_file: str):
    config = get_config(config_file)

    static_source = get_particles_static_source(config.dynamic_file, config.static_file,
                                                get_neighbors_data(config.neighbors_file).get(config.particle),
                                                config.particle)

    pipeline = Pipeline(source=static_source)

    export_file(pipeline, '../results/visualization.dump', 'lammps/dump',
                columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius", "Neighbor"])


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
