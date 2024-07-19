package com.example.opencvdemo.utils;

import java.io.File;

import ws.schild.jave.*;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

public class AviToMp4Utils {

    public static void convert(File source, File target) {

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame"); //音频编码格式
        audio.setBitRate(new Integer(64000));
        audio.setChannels(new Integer(1));

        VideoAttributes video = new VideoAttributes();
        video.setCodec("libx264");//视频编码格式
        video.setBitRate(new Integer(180000));
        video.setFrameRate(new Integer(15));


        EncodingAttributes encode = new EncodingAttributes();
        encode.setOutputFormat("mp4");
        encode.setAudioAttributes(audio);
        encode.setVideoAttributes(video);

        Encoder encoder = new Encoder();
        MultimediaObject multimediaObject = new MultimediaObject(source);
        try {
            encoder.encode(multimediaObject, target, encode);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }

    }
}
