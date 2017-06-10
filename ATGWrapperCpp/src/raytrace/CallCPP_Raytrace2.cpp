/*
 * CallCPP_Raytrace.cpp for surfaceShade
 *
 * @author Zhang Yifan
 */

#include "cn_nju_seg_atg_callCPP_CallCPP.h"

#include <malloc.h>
#include <float.h>

#include <iostream>
#include <vector>
#include <limits>
#include <iomanip>
#include <fstream>
#include <cstring>
#include <cmath>
// #include <cfloat>

#include <jni.h>

using namespace std;

static std::ofstream* bFilePtr = nullptr;
static constexpr size_t kUnknowId = 0;

/**
 * @author Zhang Yifan
 */
static inline int instExpression(std::ofstream& bFile, const char* functionName, size_t nodeId, size_t expressionId, int expr) {
  bFile << "node" << nodeId << '@' << functionName << ' ' // output node name
      << expr << ' ' // output expr result
      << "expression@" << expressionId << '\n'; // output expression name
  return expr;
}

/**
 * @author Zhang Yifan
 */
static inline void instNode(std::ofstream& bFile, const char* functionName, size_t nodeId) {
  bFile << "node" << nodeId << '@' << functionName << '\n'; // output node name
}

/**
 * @author Zhang Yifan
 */
static inline void instFunctionCall(std::ofstream& bFile, const char* functionName) {
  // bFile << "call@" << functionName << '\n'; // output function call node name
}

char* jstringTostring(JNIEnv* env, jstring jstr) {
  char* rtn = NULL;
  jclass clsstring = env->FindClass("java/lang/String");
  jstring strencode = env->NewStringUTF("utf-8");
  jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
  jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
  jsize alen = env->GetArrayLength(barr);
  jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
  if (alen > 0) {
    rtn = (char*) malloc(alen + 1);
    memcpy(rtn, ba, alen);
    rtn[alen] = 0;
  }
  env->ReleaseByteArrayElements(barr, ba, 0);
  return rtn;
}

static void skip() {
}

// Code for raytrace

// use unnamed (anonymous) namespace to avoid conflict
// @see http://en.cppreference.com/w/cpp/language/namespace#Unnamed_namespaces
namespace {

class Color final {
public:
  float r_, g_, b_; // use tail underscore for class members

  Color(float r, float g, float b) :
      r_(r), g_(g), b_(b) {
//    r_ = r;
//    g_ = g;
//    b_ = b;
  }

  float getRed() {
    return r_;
  }
  float getGreen() {
    return g_;
  }
  float getBlue() {
    return b_;
  }

};

// A simple vector class
class Vector3D final {
public:
  float x_, y_, z_;

  // constructors
  Vector3D(float x, float y, float z) :
      x_(x), y_(y), z_(z) {
  }

  Vector3D() :
      Vector3D(0.0, 0.0, 0.0) {
  }

  Vector3D(const Vector3D& v) :
      Vector3D(v.x_, v.y_, v.z_) {
  }

  // methods
  float dot(const Vector3D& B) const {
    return (x_ * B.x_ + y_ * B.y_ + z_ * B.z_);
  }

  float dot(float Bx, float By, float Bz) {
    return (x_ * Bx + y_ * By + z_ * Bz);
  }

  static float dot(const Vector3D& A, const Vector3D& B) {
    return (A.x_ * B.x_ + A.y_ * B.y_ + A.z_ * B.z_);
  }

  Vector3D cross(const Vector3D& B) const {
    return Vector3D(y_ * B.z_ - z_ * B.y_, z_ * B.x_ - x_ * B.z_, x_ * B.y_ - y_ * B.x_);
  }

  Vector3D cross(float Bx, float By, float Bz) const {
    return Vector3D(y_ * Bz - z_ * By, z_ * Bx - x_ * Bz, x_ * By - y_ * Bx);
  }

  static Vector3D cross(const Vector3D& A, const Vector3D& B) {
    return Vector3D(A.y_ * B.z_ - A.z_ * B.y_, A.z_ * B.x_ - A.x_ * B.z_, A.x_ * B.y_ - A.y_ * B.x_);
  }

  float length() const {
    return (float) std::sqrt(x_ * x_ + y_ * y_ + z_ * z_);
  }

  static float length(const Vector3D& a) {
    // return (float) Math.sqrt(A.x * A.x + A.y * A.y + A.z * A.z);
    return a.length();
  }

  void normalize() {
    float t = x_ * x_ + y_ * y_ + z_ * z_;
    if (t != 0 && t != 1) {
      t = (float) (1.0 / std::sqrt(t));
    }
    x_ *= t;
    y_ *= t;
    z_ *= t;
  }

  static Vector3D normalize(const Vector3D& a) {
//    float t = A.x * A.x + A.y * A.y + A.z * A.z;
//    if (t != 0 && t != 1)
//      t = (float) (1 / Math.sqrt(t));
//    return new Vector3D(A.x * t, A.y * t, A.z * t);

    Vector3D tempVector3D(a);
    tempVector3D.normalize();
    return tempVector3D;
  }

//  String toString() {
//    return new String("[" + x + ", " + y + ", " + z + "]");
//  }
};

extern class Vector;
extern class Ray;

// An object must implement a Renderable interface in order to
// be ray traced. Using this interface it is straight forward
// to add new objects
class Renderable {
public:
  virtual bool intersect(const Ray& r) const = 0;
  virtual Color Shade(const Ray& r, const Vector& lights, const Vector& objects, const Color& bgnd) const = 0;
//    public String toString();
}

class Ray final {
public:
  static constexpr float kMaxT = FLT_MAX;
  Vector3D origin;
  Vector3D direction;
  float t;
  Renderable object;

public Ray(Vector3D eye, Vector3D dir) {
    origin = new Vector3D(eye);
    direction = Vector3D.normalize(dir);
  }

public boolean trace(Vector objects) {
    Enumeration objList = objects.elements();
    t = MAX_T;
    object = null;
    while (objList.hasMoreElements()) {
      Renderable object = (Renderable) objList.nextElement();
      object.intersect(this);
    }
    return (object != null);
  }

  // The following method is not strictly needed, and most likely
  // adds unnecessary overhead, but I prefered the syntax
  //
  //            ray.Shade(...)
  // to
  //            ray.object.Shade(ray, ...)
  //
public final Color Shade(Vector lights, Vector objects, Color bgnd) {
    return object.Shade(this, lights, objects, bgnd);
  }

public String toString() {
    return ("ray origin = "+origin+"  direction = "+direction+"  t = "+t);
  }
}

}

// Code for raytrace test methods

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callVector3DNormalize
 * Signature: (DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callVector3DNormalize
(JNIEnv *, jobject, jdouble, jdouble, jdouble, jstring) {

}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callSurfaceShade
 * Signature: (DDDDDDDDDDDDDDDDDDDIDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callSurfaceShade
(JNIEnv *, jobject, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jint, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jstring) {

}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callRayTrace
 * Signature: (DDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callRayTrace
(JNIEnv *, jobject, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jstring) {

}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callSphereIntersect
 * Signature: (DDDDDDDDDDDDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callSphereIntersect
(JNIEnv *, jobject, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jstring) {

}

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callSphereShade
 * Signature: (DDDDDDDDDDDDDDDDDDDDIDDDDDDDDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callSphereShade
(JNIEnv *, jobject, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jint, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jdouble, jstring) {

}

///*
// * Class:     cn_nju_seg_atg_callCPP_CallCPP
// * Method:    callTcasRun
// * Signature: (IIIIIIIIIIIILjava/lang/String;)V
// */
//JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTcasRun(JNIEnv *env, jobject,
//    jint cur_vertical_sep, jint high_confidence, jint two_of_three_reports_valid, jint own_tracked_alt,
//    jint own_tracked_alt_rate, jint other_tracked_alt, jint alt_layer_value, jint up_separation,
//    jint down_separation, jint other_rac, jint other_capability, jint climb_inhibit,
//    jstring pathFile) {
//
//  char* path = jstringTostring(env, pathFile);
//  std::ofstream bFile(path);
//  bFilePtr = &bFile;
//
//  bFile<<"node1@tcasRun\n";
//
//  instFunctionCall(bFile, "tcasRun");
//  Tcas::start_symbolic(cur_vertical_sep,high_confidence,two_of_three_reports_valid,
//      own_tracked_alt,own_tracked_alt_rate,other_tracked_alt,alt_layer_value,
//      up_separation,down_separation,other_rac,other_capability,climb_inhibit,
//      bFile);
//
//  bFile<<"exit@tcasRun\n";
//
//  delete []path;
//  // bFile.close();
//  bFilePtr = 0;
//
//  return;
//}
