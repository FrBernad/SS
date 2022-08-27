import numpy as np
import pandas as pd
import plotly.graph_objects as go


def make_order_per_noise_plot(df: pd.DataFrame, L: int, Eta: int, R: float, iters: int):
    aux = df.values[:, 10000:]
    step = 1
    final = len(aux)
    aux = aux[0:final:step]
    avg = np.average(aux, axis=1)
    std = np.std(aux, axis=1)
    density = (df.N / L ** 2)
    density = density[0:final:step]

    fig = go.Figure(
        data=go.Scatter(
            x=density, y=avg,
            mode='markers+lines',
            error_y=dict(array=std),
        ),
        layout=go.Layout(
            title=dict(text=f'Order parameter per N [ L={L} - Eta={Eta} - Rc={R} - iters={iters}]', x=0.5),
            xaxis=dict(title='Density', type='log'),
            yaxis=dict(title='Order parameter'),
            font=dict(
                family="Arial",
                size=22,
            )
        )

    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    names = ['L', 'Eta', 'R', 'iters']
    file = '../../results/orderParametersL_1.txt'

    parameters = pd.read_csv(file, sep=" ", nrows=1, names=names)

    names = ['N'] + [f'''iter {i}''' for i in range(1, parameters.iters[0] + 1)]
    df = pd.read_csv(file, sep=" ", names=names, skiprows=1)

    make_order_per_noise_plot(df, parameters.L[0], parameters.Eta[0], parameters.R[0], parameters.iters[0])
