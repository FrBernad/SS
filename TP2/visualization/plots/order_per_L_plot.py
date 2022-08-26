import pandas as pd
import plotly.graph_objects as go


def make_order_per_noise_plot(df: pd.DataFrame, N: int, Eta: int, R: float, iters: int):
    l_step = 1
    iteration_step = 1

    final = len(df.values[0])
    final_L = 25
    it = list(range(1, iters - 1))[0:final:iteration_step]
    aux = df.values[0:final_L:l_step, 1:final:iteration_step]
    data = []
    L = df.values[:final_L:l_step, 0]
    for i in range(len(aux)):
        data.append(go.Scatter(
            x=it, y=aux[i],
            mode='lines',
            name=L[i]
        ))

    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(text=f'Order parameter per iteration [ N={N} - L={Eta} - Rc={R} - iters={iters}]', x=0.5),
            xaxis=dict(title='iteration'),
            yaxis=dict(title='Order parameter'),
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    names = ['N', 'Eta', 'R', 'iters']
    parameters = pd.read_csv('../../results/orderParameters.txt', sep=" ", nrows=1, names=names)

    names = ['L'] + [f'''iter {i}''' for i in range(1, parameters.iters[0] + 1)]
    df = pd.read_csv('../../results/orderParameters.txt', sep=" ", names=names, skiprows=1)

    make_order_per_noise_plot(df, parameters.N[0], parameters.Eta[0], parameters.R[0], parameters.iters[0])
