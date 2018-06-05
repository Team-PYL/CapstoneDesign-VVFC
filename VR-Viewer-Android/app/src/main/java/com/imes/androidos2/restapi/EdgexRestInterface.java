package com.imes.androidos2.restapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface EdgexRestInterface {

    @GET("api/v1/ping")
    Call<ResponseBody> getServerstatus();

    @POST("api/v1/addressable")
    Call<ResponseBody> postAddressable(@Body RequestBody params);

    @POST("api/v1/event")
    Call<ResponseBody> postEvent(@Body EventBodyItem item);
    /*
    {"origin":1000000000000,
"device":"UUID1",
"readings":[

	{"origin":1000000000000,
"name":"fov timestamp",
"value":"0"},

{"origin":1000000000000,
"name":"fov pitch",
"value":"0"},

{"origin":1000000000000,
"name":"fov yaw",
"value":"0"}

]}
     */
}
