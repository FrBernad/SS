from typing import Dict


class GeneratorConfig:
    def __init__(self, **config):
        self.generate = config.get('generate')
        self.seed = config.get('seed')
        self.static_file = config.get('static_file')
        self.dynamic_file = config.get('dynamic_file')
        self.N = config.get('N')
        self.L = config.get('L')
        self.W = config.get('W')
        self.mass = config.get('mass')
        self.w = config.get('w')
        self.r0 = config.get('r0')
        self.A = config.get('A')
        self.min_theta = config.get('min_theta')
        self.max_theta = config.get('max_theta')
        self.vx = config.get('vx')
        self.vy = config.get('vy')



class SimulatorConfig:
    def __init__(self, **config):
        self.seed = config.get('seed')
        self.static_file = config.get('static_file')
        self.dynamic_file = config.get('dynamic_file')
        self.results_file = config.get('results_file')
        self.exit_time_file = config.get('exit_time_file')
        self.L = config.get('L')
        self.W = config.get('W')
        self.vx = config.get('vx')
        self.vy = config.get('vy')
        self.dt = config.get('dt')
        self.dt2 = config.get('dt2')
        self.tf = config.get('tf')
        self.D = config.get('D')
        self.A = config.get('A')
        self.r0 = config.get('r0')
        self.w = config.get('w')
        self.kn = config.get('kn')
        self.kt = config.get('kt')
        self.reenter_min_height = config.get('reenter_min_height')
        self.reenter_max_height = config.get('reenter_max_height')
        self.exit_distance = config.get('exit_distance')
        self.vd = config.get('vd')
        self.tau = config.get('tau')


class Config:

    def __init__(self, config_dict, generator_config: GeneratorConfig, simulator_config: SimulatorConfig):
        self.config_dict = config_dict
        self.generator_config = generator_config
        self.simulator_config = simulator_config

    @staticmethod
    def generate(config_dict: Dict) -> 'Config':
        return Config(
            config_dict,
            Config._get_generator_config(config_dict['generator']),
            Config._get_simulator_config(config_dict['simulator']))

    @staticmethod
    def _get_generator_config(generator: Dict) -> GeneratorConfig:
        seed = generator.get('seed')

        try:
            generate = bool(generator['generate'])

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing static file")

        try:
            static_file = generator['static_file']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing static file")

        try:
            dynamic_file = generator['dynamic_file']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing static file")

        try:
            N = generator['N']
            if N <= 0:
                raise ConfigGeneratorException("Invalid or missing N")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing N")

        try:
            L = generator['L']
            if L <= 0:
                raise ConfigGeneratorException("Invalid or missing L")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing L")

        try:
            W = generator['W']

            if W <= 0:
                raise ConfigGeneratorException("Invalid or missing W")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing W")

        try:
            mass = generator['mass']
            if mass <= 0:
                raise ConfigGeneratorException("Invalid or missing mass")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing mass")

        try:
            r0 = generator['r0']
            if r0 <= 0:
                raise ConfigGeneratorException("Invalid or missing r0")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing r0")

        try:
            w = generator['w']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing w")

        try:
            A = generator['A']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing A")

        try:
            min_theta = generator['min_theta']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing min_theta")

        try:
            max_theta = generator['max_theta']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing max_theta")

        try:
            vx = generator['vx']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing vx")

        try:
            vy = generator['vy']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing vy")

        return GeneratorConfig(generate=generate,
                               seed=seed,
                               static_file=static_file,
                               dynamic_file=dynamic_file,
                               r0=r0,
                               w=w,
                               A=A,
                               min_theta=min_theta,
                               max_theta=max_theta,
                               N=N,
                               L=L,
                               W=W,
                               mass=mass,
                               vx=vx,
                               vy=vy,
                               )

    @staticmethod
    def _get_simulator_config(simulator: Dict) -> SimulatorConfig:
        seed = simulator.get('seed')

        try:
            static_file = simulator['static_file']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing static file")

        try:
            dynamic_file = simulator['dynamic_file']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing static file")

        try:
            results_file = simulator['results_file']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing results file")

        try:
            exit_time_file = simulator['exit_time_file']

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing exit time file")

        try:
            dt = simulator['dt']
            if dt <= 0:
                raise ConfigGeneratorException("Invalid or missing dt")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing dt")

        try:
            dt2 = simulator['dt2']
            if dt2 <= 0:
                raise ConfigGeneratorException("Invalid or missing dt2")
        except KeyError:
            raise ConfigGeneratorException("Invalid or missing dt2")

        try:
            tf = simulator['tf']
            if tf <= 0:
                raise ConfigGeneratorException("Invalid or missing tf")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing tf")

        try:
            D = simulator['D']
            if D < 0:
                raise ConfigGeneratorException("Invalid or missing D")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing D")

        try:
            W = simulator['W']
            if W <= 0:
                raise ConfigGeneratorException("Invalid or missing W")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing W")

        try:
            L = simulator['L']
            if L <= 0:
                raise ConfigGeneratorException("Invalid or missing L")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing L")

        try:
            A = simulator['A']
            if A <= 0:
                raise ConfigGeneratorException("Invalid or missing A")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing A")

        try:
            w = simulator['w']
            if w <= 0:
                raise ConfigGeneratorException("Invalid or missing w")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing w")

        try:
            r0 = simulator['r0']
        except KeyError:
            raise ConfigGeneratorException("Invalid or missing r0")

        try:
            kn = simulator['kn']
            if kn <= 0:
                raise ConfigGeneratorException("Invalid or missing kn")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing kn")

        try:
            kt = simulator['kt']
            if kt <= 0:
                raise ConfigGeneratorException("Invalid or missing kt")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing kt")

        try:
            reenter_min_height = simulator['reenter_min_height']
            if reenter_min_height <= 0:
                raise ConfigGeneratorException("Invalid or missing reenter_min_height")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing reenter_min_height")

        try:
            reenter_max_height = simulator['reenter_max_height']
            if reenter_max_height <= 0:
                raise ConfigGeneratorException("Invalid or missing reenter_max_height")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing reenter_max_height")

        try:
            exit_distance = simulator['exit_distance']
            if exit_distance <= 0:
                raise ConfigGeneratorException("Invalid or missing exit_distance")

        except KeyError:
            raise ConfigGeneratorException("Invalid or missing exit_distance")

        try:
            vx = simulator['vx']
        except KeyError:
            raise ConfigGeneratorException("Invalid or missing vx")

        try:
            vy = simulator['vy']
        except KeyError:
            raise ConfigGeneratorException("Invalid or missing vy")

        try:
            vd = simulator['vd']
        except KeyError:
            raise ConfigGeneratorException("Invalid or missing vd")

        try:
            tau = simulator['tau']
        except KeyError:
            raise ConfigGeneratorException("Invalid or missing vd")

        return SimulatorConfig(seed=seed,
                               static_file=static_file,
                               dynamic_file=dynamic_file,
                               results_file=results_file,
                               exit_time_file=exit_time_file,
                               dt=dt,
                               dt2=dt2,
                               tf=tf,
                               D=D,
                               W=W,
                               L=L,
                               A=A,
                               w=w,
                               r0=r0,
                               kn=kn,
                               kt=kt,
                               reenter_min_height=reenter_min_height,
                               reenter_max_height=reenter_max_height,
                               exit_distance=exit_distance,
                               vx=vx,
                               vy=vy,
                               vd=vd,
                               tau=tau
                               )


class ConfigGeneratorException(Exception):
    pass
