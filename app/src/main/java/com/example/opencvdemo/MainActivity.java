package com.example.opencvdemo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.example.opencvdemo.http.Constant;
import com.example.opencvdemo.http.HttpUtils;
import com.example.opencvdemo.http.JsonUtils;
import com.example.opencvdemo.http.OnHttpCallback;
import com.example.opencvdemo.model.Bean;
import com.example.opencvdemo.model.Detect;
import com.example.opencvdemo.model.Location;
import com.example.opencvdemo.model.Result;
import com.example.opencvdemo.utils.ConfirmDialog;
import com.example.opencvdemo.utils.DialogEvent;
import com.example.opencvdemo.utils.InputTextDialog;
import com.example.opencvdemo.utils.MoveUtils;
import com.example.opencvdemo.view.RecycleViewAdapter;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
public class MainActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener, OnHttpCallback {
    private static final String TAG = "MainActivity";
    private static final int EDIT_ITEM = 1;
    private static final int DELETE_ITEM = 2;

    private JavaCameraView cameraView;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button cancel;
    private Button confirm;
    private List<Bean> objects = new ArrayList<>();
    //    private List<Bean> finalObjects = new ArrayList<>();
    private RecycleViewAdapter adapter;
    private Mat mRgba;
    private Mat mTemp;
    private Mat tmp;
    private Mat kernel;
    private CascadeClassifier classifier;
    private int fps = 0;
    private int fpsLimit = 1;
    private int locLimit = 20;
    private boolean isMoving = false;
    private String DETECT_ID = "101";
    private String TRACK_ID = "102";
    private int breakLimit = 0;
    private String videoPath = "";
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    //    private final String ROOT_PATH = getApplication().getFilesDir().getAbsolutePath() + File.separator;
    private VideoWriter videoWriter;
    private boolean recordFlag = false;

    // 0: 物体识别; 1: 物体轨迹追踪; 3: 其他
    private int flag = 0;
    private static final int DETECT_OBJECT = 1;
    private static final int TRACK_OBJECT = 2;
    private Bean bean;
    private int index;
    private List<MatOfPoint> contours = new ArrayList<>();
    private List<Detect> array = new ArrayList<>();
    private List<Detect> conArray = new ArrayList<>();
    private List<Bean> beans = new ArrayList<>();
    private AtomicInteger putSize = new AtomicInteger(0);
    private AtomicInteger fetchSize = new AtomicInteger(0);
    private BackgroundSubtractorMOG2 mog2;
    private List<Location> locations = new CopyOnWriteArrayList<>();
    private List<Location> befLocations = new CopyOnWriteArrayList<>();

    private Double height = 0.30;

    private File detectImg;
    private File trackImg;
    private String recordFileName;
    private File recordFile;

    private int flipCode = -1;
    private int rotateCode = 3;
    private AlertDialog dialog;

    private int moveFlag = 0;
    private int moveLimitFlag = 0;

    private int cameraIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        initWindowSettings();
        setContentView(R.layout.activity_main);
        initLoadOpenCV();
        initView();
        createFiles();
    }

    private void createFiles() {
        String path = ROOT_PATH + "OpenCv" + "/images/" + "detectImg" + ".jpg";
        detectImg = new File(path);
        if (!Objects.requireNonNull(detectImg.getParentFile()).exists()) {
            detectImg.getParentFile().mkdirs();
        }
        if (!Objects.requireNonNull(detectImg.exists())) {
            try {
                detectImg.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        path = ROOT_PATH + "OpenCv" + "/images/" + "trackImg" + ".jpg";
        trackImg = new File(path);
        if (!Objects.requireNonNull(trackImg.exists())) {
            try {
                trackImg.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        recordFileName = ROOT_PATH + "OpenCv" + "/videos/" + System.currentTimeMillis() + ".mp4";
        recordFile = new File(recordFileName);
        if (!Objects.requireNonNull(recordFile.getParentFile()).exists()) {
            recordFile.getParentFile().mkdirs();
        }
        if (!Objects.requireNonNull(recordFile.exists())) {
            try {
                recordFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initView() {
        EventBus.getDefault().register(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                // 翻转
                if (itemId == R.id.menu_flipCode) {
                    showListDialog(0);
                } else if (itemId == R.id.menu_rotateCode) { // 旋转
                    showListDialog(1);
                } else if (itemId == R.id.menu_cameras) {
                    showListDialog(2);
                }
                return true;
            }
        });
        cameraView = findViewById(R.id.fd_activity_surface_view);
        cameraView.setCvCameraViewListener(this);
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecycleViewAdapter(R.layout.recycler_item, objects, false);
        adapter.setOnItemClickListener((adapter, view, position) -> {
                    bean = objects.get(position);
                    index = position;
                    InputTextDialog dialog = new InputTextDialog(MainActivity.this, EDIT_ITEM, bean, "编辑");
                    dialog.show();
                }
        );
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this, DELETE_ITEM, "删除", "确定删除?", false, position);
                confirmDialog.show();
                return false;
            }
        });
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                c.drawRect(10, 10, 10, 10, paint);
            }

            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 10, 10, 10);
            }
        });
        recyclerView.setAdapter(adapter);
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        // 创建混合高斯模型用于背景建模
        mog2 = Video.createBackgroundSubtractorMOG2(100, 25, false);
    }

    @SuppressLint("MissingInflatedId")
    private void showListDialog(int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_list_dialog, null);
        ListView listView = rootView.findViewById(R.id.listView_item);
        ArrayAdapter<String> adapter = null;
        if (flag == 0) {
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getFlipCodeList());
        } else if (flag == 1) {
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getRotateCodeList());
        } else if (flag == 2) {
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getCameraList());
        }
        assert adapter != null;
        if (!adapter.isEmpty()) {
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                String[] tmp = item.split("——");
                if (tmp.length > 0) {
                    int number = Integer.parseInt(tmp[0]);
                    if (flag == 0) {
                        flipCode = number;
                    } else if (flag == 1) {
                        rotateCode = number;
                    } else if (flag == 2) {
                        cameraIndex = number;
                        if (cameraView != null) {
                            cameraView.disableView();
                        }
                        onResume();
                    }
                }
                dialog.dismiss();
            }
        });
        builder.setView(rootView);
        dialog = builder.create();
        dialog.show();
    }

    private List<String> getFlipCodeList() {
        //  0 图像向下翻转; >0 图像向右翻转; <0 图像同时向下和向右翻转
        List<String> list = new ArrayList<>();
        list.add("-1" + "——" + "图像同时向下和向右翻转");
        list.add("0" + "——" + "图像向下翻转");
        list.add("1" + "——" + "图像向右翻转");
        list.add("2" + "——" + "原镜像");
        return list;
    }

    private List<String> getRotateCodeList() {
        //  rotateCode: ROTATE_90_CLOCKWISE = 0 旋转90°; ROTATE_180 = 1 旋转180°; ROTATE_90_COUNTERCLOCKWISE = 2 旋转270°
        List<String> list = new ArrayList<>();
        list.add("0" + "——" + "旋转90°");
        list.add("1" + "——" + "旋转180°");
        list.add("2" + "——" + "旋转270°");
        list.add("3" + "——" + "旋转360°");
        return list;
    }

    private List<String> getCameraList() {
        int cameras = Camera.getNumberOfCameras();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < cameras; i++) {
            list.add(i + "——" + "摄像头");
        }
        return list;
    }

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    if (cameraView != null) {
                        // 帧率显示
                        cameraView.enableFpsMeter();
                        cameraView.enableView();
                    }
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public void initLoadOpenCV() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.d("init", "initLoadOpenCV: openCV load success");
        } else {
            Log.e("init", "initLoadOpenCV: openCV load failed");
        }
    }

    private void initWindowSettings() {
        //隐藏ActionBar
        //getSupportActionBar().hide();
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //强制横屏显示,注意这里会重启活动，导致执行两次onCreate()，
        //可以在Manifest.xml中配置android:screenOrientation="landscape"
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void initClassifier() {
        try {
            //读取存放在raw的文件
            InputStream is = getResources()
                    .openRawResource(R.raw.lbpcascade_frontalface_improved);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            //通过classifier来操作人脸检测， 在外部定义一个CascadeClassifier classifier，做全局变量使用
            classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            cascadeFile.delete();
            cascadeDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        List<CameraBridgeViewBase> list = new ArrayList<>();
        list.add(cameraView);
        return list;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mTemp = new Mat();
        tmp = new Mat();
        // 运算核, 一个3*3的矩阵(自定义卷积核--矩形， 用于形态学处理)
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mTemp.release();
        tmp.release();
        kernel.release();
    }

//    实时接收摄像头数据
//    然后调用classifier人脸检测
//    对视频每一帧进行处理
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        //flipCode: 0 图像向下翻转; >0 图像向右翻转; <0 图像同时向下和向右翻转
        if (flipCode != 2) {
            Log.e("flag", "flipCode: ");
            Core.flip(mRgba, mRgba, flipCode);
        }
        //rotateCode: ROTATE_90_CLOCKWISE = 0 旋转90°; ROTATE_180 = 1 旋转180°; ROTATE_90_COUNTERCLOCKWISE = 2 旋转270°
        if (rotateCode != 3) {
            Log.e("flag", "rotateCode: ");
            Core.rotate(mRgba, mRgba, rotateCode);
        }
        if (fps == fpsLimit) {
            if (flag == 1) {
                flag = 3;
                objectTrackDetect(mRgba);
            } else if (flag == 0) {
                if (mTemp.width() != 0) {
                    mRgba = BackgroundSubtractorMOG2(mRgba);
                    if (isMoving) {
                        flag = 3;
                        firstObjectCollect(mRgba);
                    }
                }
            }
            fps = 0;
        }
        fps++;
        mTemp = mRgba;
        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            Imgproc.rectangle(mRgba, new Point(mRgba.width() * location.getX1(), mRgba.height() * location.getY1()), new Point(mRgba.width() * location.getX2(), mRgba.height() * location.getY2()), new Scalar(0, 255, 0), 2);
        }
        return mRgba;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY, cameraIndex);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        } else {
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (cameraView != null) {
            cameraView.disableView();
        }
        if (videoWriter != null) {
            videoWriter.release();
        }
        if (!Objects.equals(videoPath, "")) {
            deleteFile(videoPath);
        }
    }

    // 光帧法
    public Mat detectMove(Mat frontMat, Mat afterMat) {
        Mat frontGray = new Mat();
        Mat afterGray = new Mat();
        Mat diffGray = new Mat();
        int i = 0;
        // 灰度处理
        Imgproc.cvtColor(frontMat, frontGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(afterMat, afterGray, Imgproc.COLOR_BGR2GRAY);
        //帧差处理 找到帧与帧之间运动的物体差异
        Core.absdiff(frontGray, afterGray, diffGray);
        //二值化：黑白分明 会产生大量白色噪点
        Imgproc.threshold(diffGray, diffGray, 25, 255, Imgproc.THRESH_BINARY);
        //腐蚀处理：去除白色噪点 噪点不能完全去除，反而主要物体会被腐蚀的图案都变得不明显
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(diffGray, diffGray, element);
        //膨胀处理：将白色区域变胖
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        Imgproc.dilate(diffGray, diffGray, element2);

        //动态物体标记
        Mat hierarchy = new Mat();
        if (contours.size() > 0) {
            contours.clear();
        }
        Imgproc.findContours(diffGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) < 2000) {
                continue;
            }
            i++;
        }
        if (i > 0) {
            isMoving = true;
        } else {
            isMoving = false;
        }
        frontGray.release();
        afterGray.release();
        diffGray.release();
        hierarchy.release();
        return afterMat;
    }

    // 混合高斯模型
    public Mat BackgroundSubtractorMOG2(Mat frame) {
        int i = 0;
        Mat hierarchy = new Mat();
        // 背景建模
        mog2.apply(frame, tmp, 0.1f);
        // 开运算（先腐蚀后膨胀), 去除噪声
        Imgproc.morphologyEx(tmp, tmp, Imgproc.MORPH_OPEN, kernel);
        List<MatOfPoint> contours = new ArrayList<>();
        // 轮廓检测, 获取最外层轮廓
        Imgproc.findContours(tmp, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) < 2000) {
                continue;
            }
            i++;
        }
        if (i > 0) {
            isMoving = true;
        } else {
            isMoving = false;
        }
        hierarchy.release();
        return frame;
    }

    public void firstObjectCollect(Mat mat) {
        // 判断收集的帧数量是否大于上限
        synchronized (MainActivity.this) {
            Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(detectImg);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush();
                    // TODO 收集的帧数量等于首次送检上限,直接发送至后台服务器
                    String url = Constant.API.OBJECT_DETECT.getUrl(this);
                    HttpUtils.Instance().postFile(url, detectImg, DETECT_ID, this, DETECT_OBJECT, MainActivity.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void objectTrackDetect(Mat mat) {
        // 判断收集的帧数量是否大于上限
        synchronized (MainActivity.this) {
            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(trackImg);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush();
                    // TODO 收集的帧数量等于首次送检上限,直接发送至后台服务器
                    String url = Constant.API.TRACK_DETECT.getUrl(this);
                    HttpUtils.Instance().postFile(url, trackImg, TRACK_ID, this, TRACK_OBJECT, MainActivity.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel) {
            if (videoPath != "") {
                deleteFile(videoPath);
            }
            flag = 0;
            recordFlag = false;
        } else if (id == R.id.confirm) {
        }
        if (array.size() > 0) {
            array.clear();
        }
        if (objects.size() > 0) {
            objects.clear();
            runOnUiThread(() -> {
                cancel.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                adapter.setNewData(objects);
                adapter.notifyDataSetChanged();
            });
        } else {
            runOnUiThread(() -> {
                cancel.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onFailure(IOException e, int id, int error) {
        Log.e(TAG, "onFailure: " + e);
    }

    @Override
    public void onResponse(Result result, int id) {
        if (locations.size() > 0) {
            locations.clear();
        }
        Log.e("detect", "onResponse: ");
        if (id == DETECT_OBJECT && result.getCode() == Result.SUCCESS) {
            Log.e("detect", "onResponse: DETECT_OBJECT");
            List<LinkedHashMap> list = (List<LinkedHashMap>) result.getData();
            if (list.size() > 0) {
                if (array.size() > 0) {
                    array.clear();
                }
                for (LinkedHashMap data : list) {
                    Detect detect = JsonUtils.toBean(data, Detect.class);
                    Double confidence = detect.getConfidence();
                    if (confidence >= 0.70) {
                        array.add(detect);
                        locations.add(detect.getBox());
                    }
                }
                if (array.size() == 0) {
                    flag = 0;
                    return;
                }
                // TODO 录像
                if (conArray.size() > 0) {
                    conArray.clear();
                }
                if (breakLimit != 0) {
                    breakLimit = 0;
                }
                flag = 1;
                recordFlag = true;
            } else {
                flag = 0;
            }
            // 持续送检物品
        } else if (id == TRACK_OBJECT && result.getCode() == Result.SUCCESS) {
            Log.e("detect", "onResponse: TRACK_OBJECT");
            List<LinkedHashMap> list = (List<LinkedHashMap>) result.getData();
            if (list.size() > 0) {
                moveFlag = 0;
                for (LinkedHashMap data : list) {
                    Detect detect = JsonUtils.toBean
                            (data, Detect.class);
                    Double confidence = detect.getConfidence();
                    if (confidence >= 0.50) {
                        locations.add(detect.getBox());
                        collectConArray(detect);
                        flag = 1;
                    } else {
                        if (breakLimit == 1) {
                            if (conArray.size() >= 3) {
                                flag = 3;
                                finishDetect();
                            } else {
                                flag = 0;
                            }
                        } else {
                            ++breakLimit;
                            flag = 1;
                        }
                        return;
                    }
                }
                if (moveFlag == 0) {
                    ++moveLimitFlag;
                    Log.e("moveFlag", "moveFlag " + false);
                } else {
                    Log.e("moveFlag", "moveFlag " + true);
                }
            } else {
                if (breakLimit == 1) {
                    if (conArray.size() >= 3) {
                        flag = 3;
                        finishDetect();
                    } else {
                        flag = 0;
                    }
                } else {
                    ++breakLimit;
                    flag = 1;
                }
            }
            if (moveLimitFlag == 2) {
                flag = 3;
                Log.e("moveFlag", "moveFlag " + "conArraySize   " + conArray.size());
                finishDetect();
            }
        }
    }

    public void collectConArray(Detect detect) {
        Detect[] detects = conArray.stream().filter(d -> (Objects.equals(d.getName(), detect.getName())) && (d.getTrackId() == detect.getTrackId())).toArray(Detect[]::new);
        if (detects.length > 0) {
            Detect d = detects[detects.length - 1];
            if (locationEquals(d, detect)) {
                conArray.add(detect);
                ++moveFlag;
            }
        } else {
            conArray.add(detect);
        }
    }

    //判断物品是否移动 移动返回true
    public boolean locationEquals(Detect d, Detect d2) {
        Location d1box = d.getBox();
        Location d2Box = d2.getBox();
        if ((doubleEquals(d1box.getX1(), d2Box.getX1()) && doubleEquals(d1box.getY1(), d2Box.getY1())) || (doubleEquals(d1box.getX2(), d2Box.getX2()) && doubleEquals(d1box.getY2(), d2Box.getY2()))) {
            return false;
        }
        return true;
    }

    public boolean doubleEquals(Double d1, Double d2) {
        double abs = Math.abs(d1 - d2);
        BigDecimal bigDecimalD1 = new BigDecimal(abs).setScale(3, RoundingMode.DOWN);
        BigDecimal bigDecimalD2 = new BigDecimal(0.002).setScale(3, RoundingMode.DOWN);
        return !(bigDecimalD1.compareTo(bigDecimalD2) > 0);
    }

    @Subscribe
    public void operationResult(DialogEvent event) {
        String resultValue = event.getResultValue();
        if (event.getWhich() == DialogAction.POSITIVE) {
            if (event.getCalledByViewId() == EDIT_ITEM) {
                bean = JsonUtils.toBean(resultValue, Bean.class);
                if (bean != null) {
                    objects.set(index, bean);
                    runOnUiThread(() -> {
                        adapter.setNewData(objects);
                        adapter.notifyDataSetChanged();
                    });
                }
            } else if (event.getCalledByViewId() == DELETE_ITEM) {
                String delIndex = event.getResultValue();
                if (!TextUtils.isEmpty(delIndex)) {
                    objects.remove(Integer.parseInt(delIndex));
                    runOnUiThread(() -> {
                        adapter.setNewData(objects);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        }
    }

    public void finishDetect() {
        Log.e("finishDetect", "finishDetect: ");
        putSize.set(0);
        fetchSize.set(0);
        Map<Integer, List<Detect>> listMap = (HashMap<Integer, List<Detect>>) conArray.stream().collect(Collectors.groupingBy(Detect::getTrackId));
        listMap.forEach((key, value) -> {
            if (value.size() <= 2) {
                return;
            }
            for (int i = 0; i < value.size() - 1; i++) {
                Detect befDetect = value.get(i);
                Detect afDetect = value.get(i + 1);
                Double bY1 = befDetect.getBox().getY1();
                Double bY2 = befDetect.getBox().getY2();
                Double aY1 = afDetect.getBox().getY1();
                Double aY2 = afDetect.getBox().getY2();
                // 放入
                if ((aY1 - bY1 > 0) && (aY2 - bY2 > 0)) {
                    fetchSize.incrementAndGet();
                }
                // 取出
                else if ((bY1 - aY1 > 0) && (bY2 - aY2 > 0)) {
                    putSize.incrementAndGet();
                }
            }
            if (putSize.get() >= fetchSize.get()) {
                bindRecycler("放入", value.get(0));
            } else {
                bindRecycler("取出", value.get(0));
            }
        });
        runOnUiThread(() -> {
            if (conArray.size() > 0) {
                conArray.clear();
            }
            if (objects.size() > 0) {
                if (cancel.getVisibility() == View.GONE) {
                    cancel.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.VISIBLE);
                }
                adapter.setNewData(objects);
                adapter.notifyDataSetChanged();
            }
            moveLimitFlag = 0;
            flag = 0;
        });
    }

    private void bindRecycler(String type, Detect detect) {
        Optional<Bean> first = objects.stream().filter(d -> d.getName().equals(detect.getName())).filter(o -> o.getType().equals(type)).findFirst();
        if (!first.isPresent()) {
            Bean bean = new Bean();
            bean.setNumber(1L);
            bean.setName(detect.getName());
            bean.setType(type);
            objects.add(bean);
        } else {
            objects.forEach(o -> {
                if ((o.getName().equals(detect.getName())) && (o.getType().equals(type))) {
                    o.setNumber(o.getNumber() + 1);
                }
            });
        }
    }

    private void bindRecycler(String type, Map<Integer, List<Detect>> listMap) {
        if (beans.size() > 0) {
            beans.clear();
        }
        listMap.forEach((key, value) -> {
            if (value.size() < 5) {
                return;
            }
            Bean bean = new Bean();
            bean.setNumber(1L);
            bean.setName(value.get(0).getName());
            bean.setType(type);
            beans.add(bean);
        });
        // 过滤名称和取放类型相同的元素
        beans.forEach(b -> {
            Optional<Bean> first = objects.stream().filter(d -> d.getName().equals(b.getName())).filter(o -> o.getType().equals(type)).findFirst();
            if (!first.isPresent()) {
                objects.add(b);
            } else {
                objects.forEach(o -> {
                    if ((o.getName().equals(b.getName())) && (o.getType().equals(type))) {
                        o.setNumber(o.getNumber() + b.getNumber());
                    }
                });
            }
        });
        runOnUiThread(() -> {
            if (cancel.getVisibility() == View.GONE) {
                cancel.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
            }
            adapter.setNewData(objects);
            adapter.notifyDataSetChanged();
            flag = 0;
        });
    }
}