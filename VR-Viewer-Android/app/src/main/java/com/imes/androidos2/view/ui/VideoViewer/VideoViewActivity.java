package com.imes.androidos2.view.ui.VideoViewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.imes.androidos2.R;
import com.imes.androidos2.base.BasicVideoViewer.BasicVideoViewerActivity;
import com.imes.androidos2.base.BasicVideoViewer.BasicVideoViewerActivityInterface;
import com.imes.androidos2.restapi.EdgexRestInterface;
import com.imes.androidos2.restapi.EventBodyItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoViewActivity extends BasicVideoViewerActivity implements BasicVideoViewerActivityInterface {
    /*
    Variables for UI
     */
    @BindView(R.id.vrVideoView_main)
    VrVideoView vrVideoView;
    @BindView(R.id.seekBar_duration)
    SeekBar seekBar_duration;
    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_filename)
    TextView txt_filename;

    /*
    Variables for Networking
     */
    @BindString(R.string.edgex_coredata_rest) String EDGEX_COREDATA_IP;
    @BindString(R.string.edgex_event_rest) String EDGEX_EVENT_IP;
    @BindString(R.string.dashboard_addressable_ip) String DASHBOARD_SERVER_IP;
    @BindString(R.string.dashboard_addressable_port) String DASHBOARD_SERVER_PORT;
    @BindString(R.string.dashboard_addressable_protocol) String DASHBOARD_SERVER_PROTOCOL;
    Retrofit retrofit;
    Retrofit retrofit48080;
    EdgexRestInterface retrofitService;
    EdgexRestInterface retrofitService48080;
    private boolean isEdgeXServerIsOn = false;

    /*
    Variables for some stuffs
     */
    String file_name;
    String video_name;
    int video_type;
    Intent intent_got;
    private final String LOG_TAG = "VR Viewer LOG";
    private boolean isVideoPaused = false;
    private float[] yawAndPitch = new float[2];
    ArrayList<ArrayList<HashMap<String, Object>>> readings_list = new ArrayList<>();
    JsonObject json_event;

    /*
    Thread instance that logging the gyro(FoV) data
     */
    private GyroLogThread gyroThread = new GyroLogThread(500);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoviewer);
        ButterKnife.bind(this);

        intent_got = getIntent();
        file_name = intent_got.getStringExtra("FILE_NAME");
        video_name = intent_got.getStringExtra("VIDEO_NAME");
        video_type = intent_got.getIntExtra("VIDEO_TYPE", VrVideoView.Options.FORMAT_DEFAULT);

        initializeNetworkInterface();
    }

    @Override
    public void initializeNetworkInterface() {
        super.initializeNetworkInterface();

        retrofit = new Retrofit.Builder()
                .baseUrl(EDGEX_COREDATA_IP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit48080 = new Retrofit.Builder()
                .baseUrl(EDGEX_EVENT_IP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitService48080 = retrofit48080.create(EdgexRestInterface.class);
        retrofitService = retrofit.create(EdgexRestInterface.class);

        Call<ResponseBody> request = retrofitService.getServerstatus();
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    assert response.body() != null;
                    if (response.body() != null) {
                        if (response.message().contains("OK")) {
                            Toast.makeText(VideoViewActivity.this, "EdgeX Server is Running", Toast.LENGTH_SHORT).show();
                            isEdgeXServerIsOn = true;

                            /* Set basic information(addressable, video profile, etc) to edgeX server */
//                            initializeVideoInformationWithServer();

                            /* init UIs when EdgeX server is running */
                            initializeUI();

                            /* init and start the gyro values(yaw, pitch) checker thread */
                            initializeGyroThread();
                        }else {
                            Toast.makeText(VideoViewActivity.this, "EdgeX Server is Down!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(VideoViewActivity.this, "EdgeX Server is Down!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(VideoViewActivity.this, "EdgeX Server is Down!!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(VideoViewActivity.this, "EdgeX Server is Down!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    /* Not Used */
    private void initializeVideoInformationWithServer() {
        // TODO : Add addressable, device(video) service, profile and info <- Useless!!!! (Run it from server)
    }

    @Override
    public void initializeUI() {
        super.initializeUI();

        /*
        Initialize default views.
         */
        txt_filename.setText(file_name);
        seekBar_duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean changedByUser) {
                if (changedByUser) {
                    vrVideoView.seekTo(progress);
                    updateStatusText();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        vrVideoView.setEventListener(new VrVideoEventListener() {
            @Override
            public void onLoadSuccess() {
                super.onLoadSuccess();
                Log.i(LOG_TAG, "Successfully loaded video " + vrVideoView.getDuration());
                seekBar_duration.setMax((int)vrVideoView.getDuration());
                seekBar_duration.setEnabled(true);
                updateStatusText();
            }

            @Override
            public void onLoadError(String errorMessage) {
                super.onLoadError(errorMessage);
                Toast.makeText(VideoViewActivity.this, "Error while loading video", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error while loading video : " + errorMessage);
            }

            @Override
            public void onClick() {
                super.onClick();
                if (isVideoPaused) {
                    vrVideoView.playVideo();
                    Log.w(LOG_TAG, "Video playing");
                }else{
                    vrVideoView.pauseVideo();
                    Log.w(LOG_TAG, "Video paused");
//                    sendFovData();
                }

                isVideoPaused = !isVideoPaused;
                updateStatusText();
            }

            @Override
            public void onNewFrame() {
                super.onNewFrame();
                updateStatusText();
                seekBar_duration.setProgress((int)vrVideoView.getCurrentPosition());
            }

            @Override
            public void onCompletion() {
                super.onCompletion();
                Log.w(LOG_TAG, "Video Ended");

                vrVideoView.pauseVideo();
                isVideoPaused = !isVideoPaused;
                updateStatusText();

                // TODO : add function call that send the fov data to edgex server
                sendFovData();
            }
        });
    }

    private void sendFovData() {
        for (ArrayList<HashMap<String, Object>> item : readings_list){
            EventBodyItem fovData = new EventBodyItem(1000000000, video_name, item);
            Log.d("WTFWTF", (String)item.get(0).get("name"));
            Call<ResponseBody> request = retrofitService48080.postEvent(fovData);
            request.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.d("RESPONSE WTF111", response.body().string());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Log.d("RESPONSE WTF333", response.errorBody().string());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    assert response.body() != null;
                    if (response.body() != null) {
                        try {
                            Toast.makeText(VideoViewActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        
    }

    @Override
    public void initializeGyroThread() {
        super.initializeGyroThread();
        gyroThread.start();
    }

    /* Update the status textview with status of video viewer */
    private void updateStatusText() {
        String str_status;

        if (isVideoPaused){
            str_status = "Paused: ";
        }else {
            str_status = "Playing: ";
        }
        str_status += String.format(Locale.getDefault(), "%.2f", vrVideoView.getCurrentPosition() / 1000f) +
                " / " +
                vrVideoView.getDuration() / 1000f +
                " seconds.";
        txt_status.setText(str_status);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LOG_TAG, "onResume");
        vrVideoView.resumeRendering();
        if (vrVideoView.getDuration() <= 0){
            try{
                VrVideoView.Options options = new VrVideoView.Options();
                options.inputType = video_type;
                options.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
                vrVideoView.loadVideoFromAsset(file_name, options);
            }catch (IOException e){
                Log.e(LOG_TAG, "onResume-IOException : "+e.getLocalizedMessage());
            }
        }
        updateStatusText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LOG_TAG, "onPause");
        vrVideoView.pauseVideo();
        isVideoPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(LOG_TAG, "onDestory");
        gyroThread.interrupt();
        vrVideoView.pauseRendering();
        vrVideoView.shutdown();
    }


    private class GyroLogThread extends Thread {
        int sleep_time;

        GyroLogThread(int sleep_time) {
            this.sleep_time = sleep_time;
        }

        @Override
        public void run() {
            super.run();
            readings_list = new ArrayList<>();
            while (true){
                if (!isVideoPaused) {
                    try {
                        String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                        long currentPosition = vrVideoView.getCurrentPosition();
                        String logMsg;
                        float yaw, pitch;

                        vrVideoView.getHeadRotation(yawAndPitch);
                        yaw = yawAndPitch[0];
                        pitch = yawAndPitch[1];

                        logMsg = "[TimeStamp] : "+currentPosition
                                +  "     [Date] : "+currentTime
                                +  "      [Yaw] : "+yaw
                                +  "    [Pitch] : "+pitch;
                        ArrayList<HashMap<String, Object>> reading_arr = new ArrayList<>();

                        for (int i = 0; i < 3; i++){
                            HashMap<String, Object> reading_data = new HashMap<>();
                            switch (i){
                                case 0:
                                    reading_data.put("origin", 1000000000);
                                    reading_data.put("name", "timestamp for "+video_name);
                                    reading_data.put("value", currentPosition+"");
                                    break;
                                case 1:
                                    reading_data.put("origin", 1000000000);
                                    reading_data.put("name", "yaw for "+video_name);
                                    reading_data.put("value", yaw+"");
                                    break;
                                case 2:
                                    reading_data.put("origin", 1000000000);
                                    reading_data.put("name", "pitch for "+video_name);
                                    reading_data.put("value", pitch+"");
                                    break;
                            }
                            reading_arr.add(reading_data);
                        }

                        readings_list.add(reading_arr);

                        Log.d(LOG_TAG, logMsg);
                        sleep(sleep_time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
