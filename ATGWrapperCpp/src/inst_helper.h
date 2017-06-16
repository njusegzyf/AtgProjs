#include <iostream>

using std::cout;

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
static inline float instExpression(std::ofstream& bFile, const char* functionName, size_t nodeId, size_t expressionId, float expr) {
  bFile << "node" << nodeId << '@' << functionName << ' ' // output node name
      << expr << ' ' // output expr result
      << "expression@" << expressionId << '\n'; // output expression name
  return expr;
}

/**
 * @author Zhang Yifan
 */
static inline double instExpression(std::ofstream& bFile, const char* functionName, size_t nodeId, size_t expressionId, double expr) {
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

static inline char* jstringTostring(JNIEnv* env, jstring jstr) {
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
