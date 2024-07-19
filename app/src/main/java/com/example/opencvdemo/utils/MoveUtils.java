package com.example.opencvdemo.utils;



import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_YUV420P;

import android.graphics.Bitmap;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.opencv.core.Mat;

public class MoveUtils {
    final double frameRate = 30;//表示1秒30张照片，
    private static final String TAG = "MoveUtils";
    public FFmpegFrameRecorder getRecorderInstance(String fileName, Mat mat) {
        return new FFmpegFrameRecorder(fileName, mat.cols(), mat.rows(), 1);
    }

    public void init(FFmpegFrameRecorder recorder) {
        if (recorder != null) {
            recorder.setFormat("mp4");
            recorder.setSampleRate(44100);
            recorder.setFrameRate(frameRate);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
            recorder.setVideoOption("crf", "23");
            recorder.setVideoOption("preset", "superfast");
            recorder.setVideoOption("tune", "zerolatency");

//            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13

//            recorder.setAudioCodec(AV_CODEC_ID_AAC);
            // 录像帧率
//            recorder.setVideoBitrate(10 * 1024 * 1024);
        }
    }

    // 录像
    public void record(FFmpegFrameRecorder recorder, Bitmap bitmap) {
        try {
            AndroidFrameConverter converter = new AndroidFrameConverter();
            Frame frame = converter.convert(bitmap);
            recorder.record(frame,AV_PIX_FMT_YUV420P);
//            recorder.record(frame);
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }

    }

}


