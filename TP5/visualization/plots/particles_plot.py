import numpy as np
import plotly.colors
import plotly.graph_objects as go
from pandas import DataFrame


def make_particles_plot(df: DataFrame, M: int, N: int, dx: float, dy: float, R: float):
    indexes = range(1, len(df.x) + 1)

    fig = go.Figure()

    colors = plotly.colors.n_colors("rgb(0,0,205)", "rgb(135,206,235)", len(df.x), colortype="rgb")

    for i in indexes:
        x = df.x[i - 1]
        y = df.y[i - 1]
        radius = df.radius[i - 1]
        x0 = x - radius
        x1 = x + radius
        y0 = y - radius
        y1 = y + radius
        # Add circles
        fig.add_shape(type="circle",
                      xref="x", yref="y",
                      x0=x0, y0=y0, x1=x1, y1=y1,
                      fillcolor=colors[i - 1],
                      line=dict(color=colors[i - 1]),
                      )
        fig.add_shape(type="circle",
                      xref="x", yref="y",
                      x0=x0 - R, y0=y0 - R, x1=x1 + R, y1=y1 + R,
                      line=dict(color=colors[i - 1]),
                      )

    # Create scatter trace of text labels
    fig.add_trace(go.Scatter(
        x=df.x,
        y=df.y,
        text=list(indexes),
        mode='text',
        textfont=dict(
            color="black",
            size=12,
            family="Arial",
        ),
    ))

    fig.update_shapes(opacity=0.4, xref="x", yref="y")

    for i in np.arange(0, int(M * dy) + dy, dy):
        fig.add_hline(y=i, line_width=1, line_dash='dash')

    for i in np.arange(0, int(N * dx) + dx, dx):
        fig.add_vline(x=i, line_width=1, line_dash='dash')

    fig.update_yaxes(
        scaleanchor="x",
        scaleratio=1,
    )

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()
