import sys

from plots.particles_plot import make_particles_plot
from utils.argument_parser import parse_arguments
from utils.config import get_config
from utils.parser_utils import get_particles_data, get_neighbors_data


def visualization_plotly(config_file: str):
    config = get_config(config_file)

    df = get_particles_data(config.dynamic_file, config.static_file)
    neighbors = get_neighbors_data(config.neighbors_file)
    make_particles_plot(df, neighbors, config.M, config.L, config.R)


if __name__ == "__main__":
    arguments = parse_arguments(sys.argv[1:])

    config_file = arguments['config_file']

    try:
        visualization_plotly(config_file)
    except FileNotFoundError as e:
        print("File not found")
        print(e)
    except OSError:
        print("Error occurred.")
    except KeyboardInterrupt:
        print('Program interrupted by user.')
    except Exception as e:
        print(e)
