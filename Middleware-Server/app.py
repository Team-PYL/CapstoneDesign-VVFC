# https://plot.ly/python/bar-charts/
from flask import Flask, render_template, request

import json
import plotly
import plotly.graph_objs as go

import pandas as pd
import numpy as np

from Helper.DataHelper import EdgeXInitHelper
from Helper.RestHelper.EdgeXRestHelper import VideoFovRestHelper

app = Flask(__name__)
app.debug = True


@app.route('/')
def index():
    # mappingFovToTile(3, 3, 95, -100)
    videos = ['congo_2048', 'paris-by-diego']
    videos_types = ['3x3', '3x3']
    videos_models = ['OVER_UNDER', 'EQUALRECT']

    EdgeXInitHelper.initEdgeXWithVideoInformations(videos=videos, videos_types=videos_types, videos_models=videos_models,
                                                   edgex_host='http://localhost:48081/api/v1/')

    return render_template('layouts/index.html',
                           videos=videos)


@app.route('/dashboard', methods=['GET', 'POST'])
def dashboard():
    selected_video_name = request.form.get('selected_video')

    fovRestHelper = VideoFovRestHelper(API_HOST='http://localhost:48080/api/v1/')
    event_list = fovRestHelper.get_events(video_name=selected_video_name)

    tile_row = 3
    tile_col = 3

    video_timeSlice = list(range(0, 15000, 500))
    video_tile = list()
    video_tile_boxplot = list()

    for idx in range(0, len(video_timeSlice)):
        video_tile.append(0)
        video_tile_boxplot.append(list())

    for each_event in event_list:
        each_ts = each_event['timestamp']
        each_yaw = each_event['yaw']
        each_pitch = each_event['pitch']

        tile_num = mappingFovToTile(tile_rows=tile_row, tile_cols=tile_col, yaw=each_yaw, pitch=each_pitch)

        idx = int(video_timeSlice.index(int(each_ts)))
        # print(tile_num)
        if video_tile[idx] is 0:
            video_tile[idx] = tile_num
        else:
            video_tile[idx] = round(video_tile[idx] + tile_num) / 2

        video_tile_boxplot[idx].append(tile_num)

    traces = []

    for xd, yd in zip(video_timeSlice, video_tile_boxplot):
        traces.append(go.Box(
            x=xd,
            y=yd,
            name=str(xd)
        ))

    # for each_tile in video_tile:
    #     print(each_tile)

    graphs = [
        dict(
            data=[
                dict(
                    x=video_timeSlice,  # timestamp
                    y=video_tile,  # tile number
                    type='bar'
                ),
            ],
            layout=dict(
                title="Average tile numbers of "+str(selected_video_name),
                plot_bgcolor='#F5F7FA'
            )
        ),

        dict(
            data=traces,
            layout=dict(
                title="Boxplot graph for " + str(selected_video_name),
                plot_bgcolor='#F5F7FA'
            )
        ),
    ]

    # Add "ids" to each of the graphs to pass up to the client
    # for templating
    ids = ['Bar graph', 'Boxplot graph']

    # Convert the figures to JSON
    # PlotlyJSONEncoder appropriately converts pandas, datetime, etc
    # objects to their JSON equivalents
    graphJSON = json.dumps(graphs, cls=plotly.utils.PlotlyJSONEncoder)

    return render_template('layouts/dashboard.html',
                           video_name=selected_video_name,
                           ids=ids,
                           graphJSON=graphJSON)


def mappingFovToTile(tile_rows, tile_cols, yaw, pitch):
    fov_range_tile_rows = list()
    fov_range_tile_cols = list()
    range_start = -180
    range_step_row = int(360 / tile_rows)
    range_step_cols = int(360 / tile_cols)

    for idx in range(0, tile_rows):
        fov_range_tile_rows.append(list(range(range_start, range_start+range_step_row)))
        range_start += range_step_row

    for item in fov_range_tile_rows:
        if int(str(yaw).split('.')[0]) in item:
            tile_num_row = int(fov_range_tile_rows.index(item))+1

    range_start = 180
    for idx in range(0, tile_cols):
        fov_range_tile_cols.append(list(range(range_start, range_start-range_step_cols, -1)))
        range_start -= range_step_cols

    for item in fov_range_tile_cols:
        if int(str(pitch).split('.')[0]) in item:
            tile_num_col = int(fov_range_tile_cols.index(item))+1

    tile_num = tile_rows*(tile_num_col-1) + tile_num_row

    return tile_num


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9999, debug=True)

