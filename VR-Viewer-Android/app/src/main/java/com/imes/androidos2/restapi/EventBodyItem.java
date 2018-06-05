package com.imes.androidos2.restapi;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventBodyItem {
    Integer origin;
    String device;
    ArrayList<HashMap<String, Object>> readings;

    public EventBodyItem(Integer origin, String device, ArrayList<HashMap<String, Object>> readings) {
        this.origin = origin;
        this.device = device;
        this.readings = readings;
    }

//    public HashMap getBody() {
//        return {
//
//        }
//    }

    public HashMap<String, Object> getBody(){
        HashMap<String, Object> body_map = new HashMap<>();
        body_map.put("origin", this.origin);
        body_map.put("device", this.device);
        body_map.put("readings", this.readings);

        return body_map;
    }
}
