import numpy as np
import pandas as pd
import plotly.graph_objects as go


def make_order_per_noise_plot(df: pd.DataFrame, N: int, L: int, R: float, iters: int, cut: int):
    eta_step = 8
    iteration_step = 30

    final = len(df.values[0])
    it = list(range(1, iters - 1))[0:final:iteration_step]
    aux = df.values[::eta_step, 1:final:iteration_step]
    data = []
    eta = df.values[::eta_step, 0]
    for i in range(len(aux)):
        data.append(go.Scatter(
            x=it, y=aux[i],
            mode='lines',
            name=f'ruido = {eta[i]}'
        ))

    data.append(
        go.Scatter(x=[cut, cut], y=[0, 1],
                   mode='lines',
                   showlegend=False,
                   line=dict(color='firebrick', width=4, dash='dash'))
    )

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Order parameter per iteration [ N={N} - L={L} - Rc={R} - iters={iters}]', x=0.5),
            xaxis=dict(title='Iteración'),
            yaxis=dict(title='Parámetro de orden'),
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
    file = '../../results/orderParametersN300L20.txt'

    names = ['N', 'L', 'R', 'iters']
    parameters = pd.read_csv(file, sep=" ", nrows=1, names=names)

    names = ['eta'] + [f'''iter {i}''' for i in range(1, parameters.iters[0] + 1)]
    df = pd.read_csv(file, sep=" ", names=names, skiprows=1)

    make_order_per_noise_plot(df, parameters.N[0], parameters.L[0], parameters.R[0], parameters.iters[0], 2500)
