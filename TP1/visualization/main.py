from ovito.io import export_file
from ovito.pipeline import StaticSource, Pipeline

from utils.parser_utils import parse_particles

TEMP_FILE = "temp.dump"
DYNAMIC_FILE = "../assets/Dynamic100.txt"
STATIC_FILE = "../assets/Static100.txt"

if __name__ == '__main__':
    data = parse_particles(DYNAMIC_FILE, STATIC_FILE, TEMP_FILE)
    pipeline = source = Pipeline(source=StaticSource(data=data))

    export_file(pipeline, '../results/visualization.dump', 'lammps/dump',
                columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius"])
