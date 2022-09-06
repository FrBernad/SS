import plotly.graph_objects as go
import pandas as pd
import numpy as np


def make_time_plot(df: pd.DataFrame, N: int, L: int, R: float, periodic: bool):
    aux = df.values[:, 1:] * (10 ** (-3))
    avg = np.average(aux, axis=1)
    std = np.std(aux, axis=1)

    fig = go.Figure(
        data=go.Scatter(
            x=df.M, y=avg,
            mode='markers+lines',
            error_y=dict(array=std),
        ),
        layout=go.Layout(
            title=dict(text=f'Average Time per M [ N={N} - L={L} - Rc={R} - Periodic={periodic} ]', x=0.5),
            xaxis=dict(title='M'),
            yaxis=dict(title='Average time (µs)'),
        )
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()


if __name__ == "__main__":
    names = ['M'] + [f'''run {i}''' for i in range(1, 151)]
    df = pd.read_csv('../../results/timePlot.txt', sep=" ", names=names)

    make_time_plot(df, 150, 20, 1, False)
