import sys

from plots.particles_plot import make_particles_plot
from utils.argument_parser import parse_arguments
from utils.config import get_config
from utils.parser_utils import get_particles_initial_data


def visualization_plotly(config_file: str):
    config = get_config(config_file)

    df = get_particles_initial_data(config.static_file, config.dynamic_file)

    make_particles_plot(df, M=16, N=8, dx=2.5, dy=2.5, R=0.1)


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
