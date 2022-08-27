import numpy as np
import pandas as pd
import plotly.graph_objects as go


def make_order_per_noise_plot(df: pd.DataFrame, L: int, Eta: int, R: float, iters: int):
    iteration_step = 50
    # aux_list = []
    aux_list = [20, 50, 100, 300, 800, 1200]  # 2, 12, 20, 50, 75, 80, 100, 150, 200, 300, 500, 800, 1200
    values = np.array([num for num in df.values if int(num[0]) in aux_list])
    final = len(values[0])
    it = list(range(1, iters - 1))[0:final:iteration_step]
    aux = values[:, 1:final:iteration_step]
    data = []
    N = values[:, 0]
    for i in range(len(aux)):
        data.append(go.Scatter(
            x=it, y=aux[i],
            mode='lines',
            name='Density:' + str(N[i] / L ** 2)
        ))

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Order parameter per iteration [ L={L} - Eta={Eta} - Rc={R} - iters={iters}]', x=0.5),
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
    file = '../../results/orderParametersL_1.txt'

    names = ['L', 'Eta', 'R', 'iters']
    parameters = pd.read_csv(file, sep=" ", nrows=1, names=names)

    names = ['N'] + [f'''iter {i}''' for i in range(1, parameters.iters[0] + 1)]
    df = pd.read_csv(file, sep=" ", names=names, skiprows=1)

    make_order_per_noise_plot(df, parameters.L[0], parameters.Eta[0], parameters.R[0], parameters.iters[0])
