package com.imes.androidos2.view.ui.Home.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imes.androidos2.R;

import java.util.ArrayList;

public class VideoListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<VideoListItem> data;
    private int layout;

    public VideoListAdapter(LayoutInflater inflater, ArrayList<VideoListItem> data, int layout) {
        this.inflater = inflater;
        this.data = data;
        this.layout = layout;
    }

    public VideoListAdapter (Context context, int layout, ArrayList<VideoListItem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int i) {
        return data.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }

        VideoListItem item = data.get(i);

        TextView name = view.findViewById(R.id.textview_name);
        name.setText(item.getName());

        return view;
    }
}
