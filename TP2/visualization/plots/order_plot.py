import pandas as pd
import plotly.graph_objects as go


def make_order_plot(df: pd.DataFrame, N: int, L: int, R: float, eta: float, iters: int):
    fig = go.Figure(
        data=go.Scatter(
            x=list(range(1, iters + 1)), y=df.order,
            mode='  lines',
        ),
        layout=go.Layout(
            title=dict(text=f'Order parameter per iteration [N={N} - L={L} - Rc={R} - eta={eta}]', x=0.5),
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
    names = ['N', 'L', 'R', 'eta', 'iters']
    parameters = pd.read_csv('../../results/order.txt', sep=" ", nrows=1, names=names)

    names = ['order']
    df = pd.read_csv('../../results/order.txt', sep=" ", names=names, skiprows=1)

    make_order_plot(df, parameters.N[0], parameters.L[0], parameters.R[0], parameters.eta[0], parameters.iters[0])
