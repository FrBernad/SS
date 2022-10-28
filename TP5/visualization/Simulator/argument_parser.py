import getopt
import sys
from typing import List

DEFAULT_CONFIG_FILE = 'config.yaml'


def parse_arguments(argv: List[str]) -> dict:
    arguments = {
        'config_file': DEFAULT_CONFIG_FILE,
    }

    try:
        opts, args = getopt.getopt(argv, "hc:")
    except getopt.GetoptError:
        _help()
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            _help()
            sys.exit()
        elif opt in "-c":
            arguments['config_file'] = arg

    return arguments


def _help():
    print(
        f'''NAME
\t-h
\t\tShow this help message and exit\n
\t-c config_file
\t\tSpecifies the .yaml configuration file. Defaults to config.yaml.\n
         ''')
