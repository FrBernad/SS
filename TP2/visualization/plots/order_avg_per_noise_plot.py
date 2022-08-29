from typing import List

import numpy as np
import pandas as pd
import plotly.graph_objects as go


def make_order_avg_per_noise_plot(plots_data: List):
    data = []
    for pd in plots_data:
        df = pd['df']
        aux = df.values[:, 2500:]
        avg = np.average(aux, axis=1)
        std = np.std(aux, axis=1)
        data.append(
            go.Scatter(
                x=df.eta, y=avg,
                mode='markers+lines',
                error_y=dict(array=std),
                line_color="#13cc99",
                name=f'Densidad={pd["N"] / pd["L"] ** 2} (N={pd["N"]}, L={pd["L"]})'
            )
        )

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Order parameter per eta [N={plots_data[0]["N"]}, L={plots_data[0]["L"]}]', x=0.5),
            # title=dict(text=f'Order parameter per eta" x=0.5),
            xaxis=dict(title='Ruido'),
            yaxis=dict(title='Parámetro de orden promedio'),
            font=dict(
                family="Arial",
                size=22,
            ),
            legend=dict(
                yanchor="top",
                y=0.99,
                xanchor="right",
                x=0.99
            )
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    plots_data = []

    names1 = ['N', 'L', 'R', 'iters']
    parameters = pd.read_csv('../../results/orderParametersN300L5.txt', sep=" ", nrows=1, names=names1)
    names2 = ['eta'] + [f'''iter {i}''' for i in range(1, parameters.iters[0] + 1)]
    # df = pd.read_csv('../../results/orderParametersN300L5.txt', sep=" ", names=names2, skiprows=1)
    # plots_data.append(
    #     dict(
    #         N=parameters.N[0], L=parameters.L[0], R=parameters.R[0], iters=parameters.iters[0], df=df
    #     )
    # )
    #
    # parameters = pd.read_csv('../../results/orderParametersN300L10.txt', sep=" ", nrows=1, names=names1)
    # df = pd.read_csv('../../results/orderParametersN300L10.txt', sep=" ", names=names2, skiprows=1)
    # plots_data.append(
    #     dict(
    #         N=parameters.N[0], L=parameters.L[0], R=parameters.R[0], iters=parameters.iters[0], df=df
    #     )
    # )
    #
    parameters = pd.read_csv('../../results/orderParametersN300L20.txt', sep=" ", nrows=1, names=names1)
    df = pd.read_csv('../../results/orderParametersN300L20.txt', sep=" ", names=names2, skiprows=1)
    plots_data.append(
        dict(
            N=parameters.N[0], L=parameters.L[0], R=parameters.R[0], iters=parameters.iters[0], df=df
        )
    )

    make_order_avg_per_noise_plot(plots_data)
