package com.imes.androidos2.view.ui.Home.listview;

public class VideoListItem {
    private String name;
    private String file_name;
    private int video_type;
    private int video_format;

    public VideoListItem(String name, String file_name, int video_type, int video_format) {
        this.name = name;
        this.file_name = file_name;
        this.video_type = video_type;
        this.video_format = video_format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getVideo_type() {
        return video_type;
    }

    public void setVideo_type(int video_type) {
        this.video_type = video_type;
    }

    public int getVideo_format() {
        return video_format;
    }

    public void setVideo_format(int video_format) {
        this.video_format = video_format;
    }
}
