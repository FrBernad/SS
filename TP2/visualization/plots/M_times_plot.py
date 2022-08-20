import plotly.graph_objects as go
import pandas as pd
import numpy as np


def make_time_plot(df: pd.DataFrame, N: int, L: int, R: float):
    aux = df.values[:, 1:]
    avg = np.average(aux, axis=1)
    std = np.std(aux, axis=1)

    fig = go.Figure(
        data=go.Scatter(
            x=df.eta, y=avg,
            mode='markers+lines',
            error_y=dict(array=std),
        ),
        layout=go.Layout(
            title=dict(text=f'Order parameter per eta [ N={N} - L={L} - Rc={R}]', x=0.5),
            xaxis=dict(title='M'),
            yaxis=dict(title='Order parameter'),
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    names = ['N', 'L', 'R', 'iters']
    parameters = pd.read_csv('../../results/orderParameters.txt', sep=" ", nrows=1, names=names)

    names = ['eta'] + [f'''run {i}''' for i in range(1, parameters.iters[0] + 1)]
    df = pd.read_csv('../../results/orderParameters.txt', sep=" ", names=names, skiprows=1)

    make_time_plot(df, parameters.N[0], parameters.L[0], parameters.R[0])
