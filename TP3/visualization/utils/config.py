import yaml
from pydantic import BaseModel, ValidationError

RESULTS_FILE = "../results/results.txt"
STATIC_FILE = "../results/Static.txt"


class Config(BaseModel):
    results_file: str = RESULTS_FILE
    static_file: str = STATIC_FILE


def get_config(config_file: str) -> Config:
    with open(config_file) as cf:
        config = yaml.safe_load(cf)["config"]
        try:
            return Config(**config)
        except ValidationError as e:
            print(e.json())
