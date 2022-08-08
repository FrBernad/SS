from ovito.io import export_file
from ovito.pipeline import Pipeline

from utils.parser_utils import get_particles_static_source

DYNAMIC_FILE = "../assets/Dynamic100.txt"
STATIC_FILE = "../assets/Static100.txt"

if __name__ == '__main__':
    static_source = get_particles_static_source(DYNAMIC_FILE, STATIC_FILE)
    pipeline = source = Pipeline(source=static_source)

    export_file(pipeline, '../results/visualization.dump', 'lammps/dump',
                columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius"])
