import yaml
from pydantic import BaseModel, ValidationError

DYNAMIC_FILE = "../results/Dynamic100.txt"
STATIC_FILE = "../results/Static100.txt"
FLOCKS_FILE_FMT = "../results/flocks_*"


class Config(BaseModel):
    dynamic_file: str = DYNAMIC_FILE
    static_file: str = STATIC_FILE
    flocks_files_fmt: str = FLOCKS_FILE_FMT
    particle_R: float
    Rc: float


def get_config(config_file: str) -> Config:
    with open(config_file) as cf:
        config = yaml.safe_load(cf)["config"]
        try:
            return Config(**config)
        except ValidationError as e:
            print(e.json())
