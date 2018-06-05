package com.imes.androidos2.view.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.vr.sdk.widgets.video.VrVideoView;
import com.imes.androidos2.R;
import com.imes.androidos2.base.BasicAcitivty.BasicActivity;
import com.imes.androidos2.view.ui.Home.listview.VideoListAdapter;
import com.imes.androidos2.view.ui.Home.listview.VideoListItem;
import com.imes.androidos2.view.ui.VideoViewer.VideoViewActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BasicActivity {

    @BindView(R.id.videoListView) ListView listView_video;

    VideoListAdapter videoListAdapter;
    ArrayList<VideoListItem> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initializeUI();
    }

    @Override
    public void initializeUI() {
        super.initializeUI();

        data.clear();
        data.add(new VideoListItem("congo_2048", "congo_2048.mp4", VrVideoView.Options.TYPE_STEREO_OVER_UNDER, VrVideoView.Options.FORMAT_DEFAULT));
        data.add(new VideoListItem("paris-by-diego", "paris-by-diego.mp4", VrVideoView.Options.TYPE_MONO, VrVideoView.Options.FORMAT_DEFAULT));

        videoListAdapter = new VideoListAdapter(this, R.layout.item_listview_videoview, data);

        listView_video.setAdapter(videoListAdapter);
        listView_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int idx, long l) {
                Intent intent = new Intent(MainActivity.this, VideoViewActivity.class);
                intent.putExtra("FILE_NAME", data.get(idx).getFile_name());
                intent.putExtra("VIDEO_NAME", data.get(idx).getName());
                intent.putExtra("VIDEO_TYPE", data.get(idx).getVideo_type());
                startActivity(intent);
            }
        });
    }
}
