package com.example.opencvdemo.http;

import android.content.Context;

public class Constant {
    public final static String API_URL = "http://10.1.8.6:5090/";
    public final static String APP_VERSION = "0.5";

    public enum API {
        OBJECT_DETECT("/obj/predict"),
        TRACK_DETECT("/obj/track");
        public final String url;

        public final String getUrl(Context context) {
            return API_URL + url;
        }

        API(String url) {
            this.url = url;
        }
    }

    public final static String SCHEDULE_STATUS_STANDBY = "0";
    public final static String SCHEDULE_STATUS_PREPARE = "1";
    public final static String SCHEDULE_STATUS_SURGERY = "2";
    public final static String SCHEDULE_STATUS_CLEAN = "3";

}
