package com.example.opencvdemo.http;

import static com.example.opencvdemo.utils.FileUtil.deleteFile;

import android.content.Context;


import com.example.opencvdemo.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Http 工具类
 */
public class HttpUtils {
    public static final int ERROR_NORMAL = 100;

    public static final String CONTAINERID = "containerId";
    public static final String CONTAINERTYPE = "containerType";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType FILE
            = MediaType.get("File/*");

    private static OkHttpClient client;
    private static HttpUtils httpUtils;

    private static OkHttpClient HttpClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static HttpUtils Instance() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        return httpUtils;
    }

    public void postFiles(String url, List<File> files, OnHttpCallback callback, int id, Context context) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (int i = 0; i < files.size(); i++) {
            builder.addFormDataPart("files", files.get(i).getName(), RequestBody.create(FILE, files.get(i)));
        }
        RequestBody requestBody = builder.setType(MultipartBody.FORM).build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        HttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e, id, ERROR_NORMAL);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                Result result = JsonUtils.toBean(response.body().string(), Result.class);
                callback.onResponse(result, id);
            }
        });
    }

    public void postFile(String url, File file, OnHttpCallback callback, int id, Context context) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("image", file.getName(), RequestBody.create(FILE, file)).addFormDataPart("ratio", String.valueOf(true));
        RequestBody requestBody = builder.setType(MultipartBody.FORM).build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        HttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e, id, ERROR_NORMAL);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                Result result = JsonUtils.toBean(response.body().string(), Result.class);
                callback.onResponse(result, id);
            }
        });
    }

    public void postFile(String url, File file, String taskId, OnHttpCallback callback, int id, Context context) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("image", file.getName(), RequestBody.create(FILE, file))
                .addFormDataPart("ratio", String.valueOf(true))
                .addFormDataPart("taskId", taskId);
        RequestBody requestBody = builder.setType(MultipartBody.FORM).build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        HttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e, id, ERROR_NORMAL);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                Result result = JsonUtils.toBean(response.body().string(), Result.class);
                callback.onResponse(result, id);
            }
        });
    }

}