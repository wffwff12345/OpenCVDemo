#include <jni.h>

// empty

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_opencvdemo_DetectionBasedTracker_nativeCreateObject(JNIEnv *env, jclass clazz,
                                                                     jstring cascade_name,
                                                                     jint min_face_size) {
    // TODO: implement nativeCreateObject()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencvdemo_DetectionBasedTracker_nativeDestroyObject(JNIEnv *env, jclass clazz,
                                                                      jlong thiz) {
    // TODO: implement nativeDestroyObject()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencvdemo_DetectionBasedTracker_nativeStart(JNIEnv *env, jclass clazz,
                                                              jlong thiz) {
    // TODO: implement nativeStart()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencvdemo_DetectionBasedTracker_nativeStop(JNIEnv *env, jclass clazz,
                                                             jlong thiz) {
    // TODO: implement nativeStop()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencvdemo_DetectionBasedTracker_nativeSetFaceSize(JNIEnv *env, jclass clazz,
                                                                    jlong thiz, jint size) {
    // TODO: implement nativeSetFaceSize()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencvdemo_DetectionBasedTracker_nativeDetect(JNIEnv *env, jclass clazz,
                                                               jlong thiz, jlong input_image,
                                                               jlong faces) {
    // TODO: implement nativeDetect()
}