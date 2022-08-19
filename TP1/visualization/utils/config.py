import yaml
from pydantic import BaseModel, ValidationError

DYNAMIC_FILE = "../results/Dynamic100.txt"
STATIC_FILE = "../results/Static100.txt"
NEIGHBORS_FILE = "../results/neighbors.txt"


class Config(BaseModel):
    dynamic_file: str = DYNAMIC_FILE
    static_file: str = STATIC_FILE
    neighbors_file: str = NEIGHBORS_FILE
    L: int
    M: int
    R: float
    particle: int


def get_config(config_file: str) -> Config:
    with open(config_file) as cf:
        config = yaml.safe_load(cf)["config"]
        try:
            return Config(**config)
        except ValidationError as e:
            print(e.json())
