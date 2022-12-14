import plotly.colors
import plotly.graph_objects as go
from pandas import DataFrame


def make_particles_plot(df: DataFrame, neighbors: dict, M: int, L: int, R: float):
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
        customdata=list(neighbors.values()),
        hovertemplate='Position: (%{x:.2f}, %{y:.2f})<br>Neighbors: %{customdata}<extra></extra>',
        textfont=dict(
            color="black",
            size=12,
            family="Arial",
        ),
    ))

    fig.update_shapes(opacity=0.4, xref="x", yref="y")
    # CAMBIAR DENJEVO A CIRCULOS O VER Q SE PONGA EN TAMANO
    # RECIBIR COSAS DE LA CELDA ARCHIVO CONFIG

    fig.update_yaxes(tick0=0, dtick=L / M)

    fig.update_xaxes(tick0=0, dtick=L / M)
    # Set figure size
    fig.update_layout(width=1000, height=1000)

    fig.show()
