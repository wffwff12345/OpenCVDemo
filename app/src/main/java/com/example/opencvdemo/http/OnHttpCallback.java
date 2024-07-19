package com.example.opencvdemo.http;

import com.example.opencvdemo.model.Result;

import java.io.IOException;

public interface OnHttpCallback {
    void onFailure(IOException e, int id, int error);

    void onResponse(Result result, int id);
}