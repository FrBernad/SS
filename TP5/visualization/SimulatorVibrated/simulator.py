import os
import sys
from datetime import datetime

import yaml

from argument_parser import parse_arguments
from config import Config


def _get_config(config_file: str) -> Config:
    with open(config_file) as cf:
        config = yaml.safe_load(cf)["config"]
        return Config.generate(config)


def main(config_file: str):
    print(f'''{datetime.now().strftime("%H:%M:%S")} - Parsing config file''')
    config = _get_config(config_file)

    if config.generator_config.generate:
        print(f'''{datetime.now().strftime("%H:%M:%S")} - Generating new particles''')
        print(f'''{datetime.now().strftime("%H:%M:%S")} - Executing Particle Generator''')
        cmd = (f'java '
               f'-DstaticFile="{config.generator_config.static_file}" '
               f'-DdynamicFile="{config.generator_config.dynamic_file}" '
               f'-DN={config.generator_config.N} '
               f'-DL={config.generator_config.L} '
               f'-DW={config.generator_config.W} '
               f'-Dmass={config.generator_config.mass} '
               f'-Dr0={config.generator_config.r0} '
               f'-Ddr={config.generator_config.dr} '
               f'-Dvx={config.generator_config.vx} '
               f'-Dvy={config.generator_config.vy} '
               f'{"-Dseed=" + str(config.generator_config.seed) + " " if config.generator_config.seed is not None else ""}'
               f'-cp jars/classes.jar ar.edu.itba.ss.simulator.ParticlesGenerator ')
        os.system(cmd)
    else:
        print(f'''{datetime.now().strftime("%H:%M:%S")} - Skipping particles generation''')

    print(f'''{datetime.now().strftime("%H:%M:%S")} - Executing Simulator''')
    cmd = (f'java '
           f'-DstaticFile="{config.simulator_config.static_file}" '
           f'-DdynamicFile="{config.simulator_config.dynamic_file}" '
           f'-DresultsFile="{config.simulator_config.results_file}" '
           f'-DexitTimeFile="{config.simulator_config.exit_time_file}" '
           f'-DL={config.simulator_config.L} '
           f'-DW={config.simulator_config.W} '
           f'-DD={config.simulator_config.D} '
           f'-Dw={config.simulator_config.w} '
           f'-Dkn={config.simulator_config.kn} '
           f'-Dkt={config.simulator_config.kt} '
           f'-DA={config.simulator_config.A} '
           f'-DexitDistance={config.simulator_config.exit_distance} '
           f'-DreenterMinHeight={config.simulator_config.reenter_min_height} '
           f'-DreenterMaxHeight={config.simulator_config.reenter_max_height} '
           f'-Dgravity={config.simulator_config.gravity} '
           f'-Ddt={config.simulator_config.dt} '
           f'-Ddt2={config.simulator_config.dt2} '
           f'-Dtf={config.simulator_config.tf} '
           f'-Dvx={config.simulator_config.vx} '
           f'-Dvy={config.simulator_config.vy} '
           f'{"-Dseed=" + str(config.generator_config.seed) + " " if config.generator_config.seed is not None else ""}'
           f'-cp jars/classes.jar ar.edu.itba.ss.simulator.Simulator ')
    os.system(cmd)


if __name__ == '__main__':
    arguments = parse_arguments(sys.argv[1:])

    config_file = arguments['config_file']

    try:
        main(config_file)
    except OSError:
        print("Error opening config file.")
    except KeyboardInterrupt:
        print('Program interrupted by user.')
    except Exception as e:
        print(e)
