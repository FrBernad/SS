import plotly.colors
import plotly.graph_objects as go
from pandas import DataFrame


def make_particles_plot(df: DataFrame):
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
                      line=dict(width=0))

    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()
