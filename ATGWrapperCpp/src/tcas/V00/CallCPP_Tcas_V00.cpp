/*
 * For Tcas_V00.
 *
 * Author: Zhang Yifan
 */

#include "cn_nju_seg_atg_callCPP_CallCPP.h"

#include <math.h>
#include <malloc.h>

#include <iostream>
#include <vector>
#include <limits>
#include <iomanip>
#include <fstream>
#include <memory>
#include <cstring>

#include <jni.h>

using namespace std;

static std::ofstream* bFilePtr = 0;
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

class Const {
public:
  static const int OLEV; /* in feets/minute */
  static const int MAXALTDIFF; /* max altitude difference in feet */
  static const int MINSEP; /* min separation in feet */
  static const int NOZCROSS; /* in feet */
  static const int NO_INTENT;
  static const int DO_NOT_CLIMB;
  static const int DO_NOT_DESCEND;
  static const int TCAS_TA;
  static const int OTHER;
  static const int UNRESOLVED;
  static const int UPWARD_RA;
  static const int DOWNWARD_RA;
};

const int Const::OLEV = 600;
const int Const::MAXALTDIFF = 600;
const int Const::MINSEP = 300;
const int Const::NOZCROSS = 100;
const int Const::NO_INTENT = 0;
const int Const::DO_NOT_CLIMB = 1;
const int Const::DO_NOT_DESCEND = 2;
const int Const::TCAS_TA = 1;
const int Const::OTHER = 2;
const int Const::UNRESOLVED = 0;
const int Const::UPWARD_RA = 1;
const int Const::DOWNWARD_RA = 2;

class Tcas {
public:
  static int Cur_Vertical_Sep;
  static int High_Confidence;
  static int Two_of_Three_Reports_Valid;
  static int Own_Tracked_Alt;
  static int Own_Tracked_Alt_Rate;
  static int Other_Tracked_Alt;
  static int Alt_Layer_Value; /* 0, 1, 2, 3 */
  static int Positive_RA_Alt_Thresh[];
  static int Up_Separation;
  static int Down_Separation;

  /* state variables */
  static int Other_RAC; /* NO_INTENT, DO_NOT_CLIMB, DO_NOT_DESCEND */
  static int Other_Capability; /* TCAS_TA, OTHER */
  static int Climb_Inhibit; /* true/false */
  static int need_upward_RA;
  static int need_downward_RA;

  static void initialize() {
    Positive_RA_Alt_Thresh[0] = 400;
    Positive_RA_Alt_Thresh[1] = 500;
    Positive_RA_Alt_Thresh[2] = 640;
    Positive_RA_Alt_Thresh[3] = 740;
  }

  static inline int ALIM() {
    return Positive_RA_Alt_Thresh[Alt_Layer_Value];
  }

  static int Inhibit_Biased_Climb(std::ofstream& bFile) {
    bFile << "entry@Inhibit_Biased_Climb\n";
    if (bFile << "node1@Inhibit_Biased_Climb " << Climb_Inhibit - 1 << " expression@72\n", Climb_Inhibit == 1) {
      bFile << "node2@Inhibit_Biased_Climb\n";
      return Up_Separation + Const::MINSEP /* operand mutation NOZCROSS */;
    } else {
      bFile << "node3@Inhibit_Biased_Climb\n";
      return Up_Separation;
    }
  }

  static int Non_Crossing_Biased_Climb(std::ofstream& bFile) {
    int upward_preferred;
    int upward_crossing_situation;
    int result;

    bFile << "entry@Non_Crossing_Biased_Climb\n";
    bFile << "node1@Non_Crossing_Biased_Climb\n";

    // upward_preferred = (Inhibit_Biased_Climb(bFile) > Down_Separation) ? 1 : 0;
    instFunctionCall(bFile, "Non_Crossing_Biased_Climb");
    int temp0 = Inhibit_Biased_Climb(bFile);
    if (bFile << "node2@Non_Crossing_Biased_Climb " << temp0 - Down_Separation << " expression@52\n", temp0 > Down_Separation) {
      bFile << "node3@Non_Crossing_Biased_Climb\n";
      upward_preferred = 1;
    } else {
      bFile << "node4@Non_Crossing_Biased_Climb\n";
      upward_preferred = 0;
    }

    //    if (upward_preferred != 0) {
    //      result =
    //          (Own_Below_Threat() != 1 ||
    //              ((Own_Below_Threat() == 1) && (!(Down_Separation >= ALIM())))) ? 1 : 0;
    //    } else {
    //      result =
    //          (Own_Above_Threat() == 1 && (Cur_Vertical_Sep >= Const::MINSEP) && (Up_Separation >= ALIM())) ? 1 : 0;
    //    }
    if (instExpression(bFile, "Non_Crossing_Biased_Climb", 5, 54, upward_preferred) != 0) {
      instFunctionCall(bFile, "Non_Crossing_Biased_Climb");
      int temp1 = Own_Below_Threat();
      if (instExpression(bFile, "Non_Crossing_Biased_Climb", 6, 56, temp1 - 1) == 0
          || (instExpression(bFile, "Non_Crossing_Biased_Climb", 6, 57, temp1 - 1) != 0
              && instExpression(bFile, "Non_Crossing_Biased_Climb", 6, 58, Down_Separation - Positive_RA_Alt_Thresh[Alt_Layer_Value]) < 0)) {
        bFile << "node7@Non_Crossing_Biased_Climb\n";
        result = 1;
      } else {
        bFile << "node8@Non_Crossing_Biased_Climb\n";
        result = 0;
      }
    } else {
      instFunctionCall(bFile, "Non_Crossing_Biased_Climb");
      int temp2 = Own_Above_Threat();
      if (instExpression(bFile, "Non_Crossing_Biased_Climb", 9, 63, temp2 - 1) == 0
          && instExpression(bFile, "Non_Crossing_Biased_Climb", 9, 64, Cur_Vertical_Sep - Const::MINSEP) >= 0
          && instExpression(bFile, "Non_Crossing_Biased_Climb", 9, 65, Up_Separation - Positive_RA_Alt_Thresh[Alt_Layer_Value]) >= 0) {
        bFile << "node10@Non_Crossing_Biased_Climb\n";
        result = 1;
      } else {
        bFile << "node11@Non_Crossing_Biased_Climb\n";
        result = 0;
      }
    }

    bFile << "node12@Non_Crossing_Biased_Climb\n";
    return result;
  }

  static int Non_Crossing_Biased_Descend(std::ofstream& bFile) {
    int upward_preferred;
    int upward_crossing_situation;
    int result;

    bFile << "entry@Non_Crossing_Biased_Descend\n";
    bFile << "node1@Non_Crossing_Biased_Descend\n";

    // upward_preferred = (Inhibit_Biased_Climb(bFile) > Down_Separation) ? 1 : 0;
    instFunctionCall(bFile, "Non_Crossing_Biased_Descend");
    int temp0 = Inhibit_Biased_Climb(bFile) - Down_Separation;
    if (instExpression(bFile, "Non_Crossing_Biased_Descend", 2, 93, temp0) > 0) { // can be treat as `temp0 > 0`
      bFile << "node3@Non_Crossing_Biased_Descend\n";
      upward_preferred = 1;
    } else {
      bFile << "node4@Non_Crossing_Biased_Descend\n";
      upward_preferred = 0;
    }

//    if (upward_preferred != 0) {
//      result =
//          (Own_Below_Threat() == 1 && (Cur_Vertical_Sep >= Const::MINSEP) && (Down_Separation >= ALIM())) ? 1 : 0;
//    } else {
//      result =
//          (Own_Above_Threat() != 1
//              || ((Own_Above_Threat() == 1) && (Up_Separation >= ALIM()))) ? 1 : 0;
//    }
    if (instExpression(bFile, "Non_Crossing_Biased_Descend", 5, 95, upward_preferred) != 0) {
      instFunctionCall(bFile, "Non_Crossing_Biased_Descend");
      int temp1 = Own_Below_Threat();
      if (instExpression(bFile, "Non_Crossing_Biased_Descend", 6, 97, temp1 - 1) == 0
          && instExpression(bFile, "Non_Crossing_Biased_Descend", 6, 98, Cur_Vertical_Sep - Const::MINSEP) >= 0
          && instExpression(bFile, "Non_Crossing_Biased_Descend", 6, 99, Down_Separation - Positive_RA_Alt_Thresh[Alt_Layer_Value]) >= 0) {
        bFile << "node7@Non_Crossing_Biased_Descend\n";
        result = 1;
      } else {
        bFile << "node8@Non_Crossing_Biased_Descend\n";
        result = 0;
      }
    } else {
      instFunctionCall(bFile, "Non_Crossing_Biased_Descend");
      int temp2 = Own_Above_Threat();
      if (instExpression(bFile, "Non_Crossing_Biased_Descend", 9, 104, temp2 - 1) != 0
          || (instExpression(bFile, "Non_Crossing_Biased_Descend", 9, 105, temp2 - 1) == 0
              && instExpression(bFile, "Non_Crossing_Biased_Descend", 9, 106, Up_Separation - Positive_RA_Alt_Thresh[Alt_Layer_Value]) >= 0)) {
        bFile << "node10@Non_Crossing_Biased_Descend\n";
        result = 1;
      } else {
        bFile << "node11@Non_Crossing_Biased_Descend\n";
        result = 0;
      }
    }

    bFile << "node12@Non_Crossing_Biased_Descend\n";
    return result;
  }

  static int Own_Below_Threat() {
    // return ((Own_Tracked_Alt < Other_Tracked_Alt) ? 1 : 0);

    std::ostream& bFile = *bFilePtr;
    bFile << "entry@Own_Below_Threat\n";

    int temp0 = Own_Tracked_Alt - Other_Tracked_Alt;
    if (bFile << "node1@Own_Below_Threat " << temp0 << " expression@78\n", temp0 < 0) {
      bFile << "node2@Own_Below_Threat\n";
      return 1;
    } else {
      bFile << "node3@Own_Below_Threat\n";
      return 0;
    }
  }

  static int Own_Above_Threat() {
    // return ((Other_Tracked_Alt < Own_Tracked_Alt) ? 1 : 0);

    std::ostream& bFile = *bFilePtr;
    bFile << "entry@Own_Above_Threat\n";

    int temp0 = Other_Tracked_Alt - Own_Tracked_Alt;
    if (bFile << "node1@Own_Above_Threat " << temp0 << " expression@85\n", temp0 < 0) {
      bFile << "node2@Own_Above_Threat\n";
      return 1;
    } else {
      bFile << "node3@Own_Above_Threat\n";
      return 0;
    }
  }

  /**
   * @assert
   * LOCATION[RAsComputed] noRAconflict:
   * !(need_upward_RA == 1 && need_downward_RA==1);
   */
  static int alt_sep_test(std::ofstream& bFile) {
    int enabled, tcas_equipped, intent_not_known;
    int alt_sep;

    bFile << "entry@alt_sep_test\n";
    bFile << "node1@alt_sep_test\n";

    // enabled = (High_Confidence == 1 && (Own_Tracked_Alt_Rate <= Const::OLEV) && (Cur_Vertical_Sep > Const::MAXALTDIFF)) ? 1 : 0;
    if (instExpression(bFile, "alt_sep_test", 2, 9, High_Confidence - 1) == 0
        && instExpression(bFile, "alt_sep_test", 2, 10, Own_Tracked_Alt_Rate - Const::OLEV) <= 0
        && instExpression(bFile, "alt_sep_test", 2, 11, Cur_Vertical_Sep - Const::MAXALTDIFF) > 0) {
      bFile << "node3@alt_sep_test\n";
      enabled = 1;
    } else {
      bFile << "node4@alt_sep_test\n";
      enabled = 0;
    }

    // tcas_equipped = (Other_Capability == Const::TCAS_TA) ? 1 : 0;
    if (instExpression(bFile, "alt_sep_test", 5, 13, Other_Capability - Const::TCAS_TA) == 0) {
      bFile << "node6@alt_sep_test\n";
      tcas_equipped = 1;
    } else {
      bFile << "node7@alt_sep_test\n";
      tcas_equipped = 0;
    }

    // intent_not_known = (Two_of_Three_Reports_Valid == 1 && Other_RAC == Const::NO_INTENT) ? 1 : 0;
    if (instExpression(bFile, "alt_sep_test", 8, 16, Two_of_Three_Reports_Valid - 1) == 0
        && instExpression(bFile, "alt_sep_test", 8, 17, Other_RAC - Const::NO_INTENT) == 0) {
      bFile << "node9@alt_sep_test\n";
      intent_not_known = 1;
    } else {
      bFile << "node10@alt_sep_test\n";
      intent_not_known = 0;
    }

    bFile << "node11@alt_sep_test\n";
    alt_sep = Const::UNRESOLVED;

//    if (enabled == 1 && ((tcas_equipped == 1 && intent_not_known == 1) || tcas_equipped == 0)) {
//      need_upward_RA = (Non_Crossing_Biased_Climb(bFile) == 1 && Own_Below_Threat() == 1) ? 1 : 0;
//      need_downward_RA = (Non_Crossing_Biased_Descend(bFile) == 1 && Own_Above_Threat() == 1) ? 1 : 0;
//
//      if (need_upward_RA == 1 && need_downward_RA == 1)
//        alt_sep = Const::UNRESOLVED;
//      else if (need_upward_RA == 1)
//        alt_sep = Const::UPWARD_RA;
//      else if (need_downward_RA == 1)
//        alt_sep = Const::DOWNWARD_RA;
//      else
//        alt_sep = Const::UNRESOLVED;
//    }

    // if expr : enabled == 1 && ((tcas_equipped == 1 && intent_not_known == 1) || tcas_equipped == 0)
    if (instExpression(bFile, "alt_sep_test", 12, 21, enabled - 1) == 0
        && ((instExpression(bFile, "alt_sep_test", 12, 22, tcas_equipped - 1) == 0
            && instExpression(bFile, "alt_sep_test", 12, 23, intent_not_known - 1) == 0)
            || instExpression(bFile, "alt_sep_test", 12, 24, tcas_equipped) == 0)) {

      // need_upward_RA = (Non_Crossing_Biased_Climb(bFile) == 1 && Own_Below_Threat() == 1) ? 1 : 0;
      instFunctionCall(bFile, "alt_sep_test");
      int temp0 = Non_Crossing_Biased_Climb(bFile) - 1;
      instFunctionCall(bFile, "alt_sep_test");
      int temp1 = Own_Below_Threat() - 1;
      if (instExpression(bFile, "alt_sep_test", 13, 27, temp0) == 0
          && instExpression(bFile, "alt_sep_test", 13, 28, temp1) == 0) {
        bFile << "node14@alt_sep_test\n";
        need_upward_RA = 1;
      } else {
        bFile << "node15@alt_sep_test\n";
        need_upward_RA = 0;
      }

      // need_downward_RA = (Non_Crossing_Biased_Descend(bFile) == 1 && Own_Above_Threat() == 1) ? 1 : 0;
      instFunctionCall(bFile, "alt_sep_test");
      temp0 = Non_Crossing_Biased_Descend(bFile) - 1;
      instFunctionCall(bFile, "alt_sep_test");
      temp1 = Own_Above_Threat() - 1;
      if (instExpression(bFile, "alt_sep_test", 16, 31, temp0) == 0
          && instExpression(bFile, "alt_sep_test", 16, 32, temp1) == 0) {
        bFile << "node17@alt_sep_test\n";
        need_downward_RA = 1;
      } else {
        bFile << "node18@alt_sep_test\n";
        need_downward_RA = 0;
      }

      //      if (need_upward_RA == 1 && need_downward_RA == 1)
      //        alt_sep = Const::UNRESOLVED;
      //      else if (need_upward_RA == 1)
      //        alt_sep = Const::UPWARD_RA;
      //      else if (need_downward_RA == 1)
      //        alt_sep = Const::DOWNWARD_RA;
      //      else
      //        alt_sep = Const::UNRESOLVED;
      if (instExpression(bFile, "alt_sep_test", 19, 35, need_upward_RA - 1) == 0
          && instExpression(bFile, "alt_sep_test", 19, 36, need_upward_RA - 1) == 0) {
        bFile << "node20@alt_sep_test\n";
        alt_sep = Const::UNRESOLVED;
      } else if (instExpression(bFile, "alt_sep_test", 21, 38, need_upward_RA - 1) == 0) {
        bFile << "node22@alt_sep_test\n";
        alt_sep = Const::UPWARD_RA;
      } else if (instExpression(bFile, "alt_sep_test", 23, 49, need_downward_RA - 1) == 0) {
        bFile << "node24@alt_sep_test\n";
        alt_sep = Const::DOWNWARD_RA;
      } else {
        bFile << "node25@alt_sep_test\n";
        alt_sep = Const::UNRESOLVED;
      }
    }

    bFile << "node26@alt_sep_test\n";
    return alt_sep;
  }

// Added by pdinges
  static void start_symbolic(int cur_vertical_sep, int high_confidence,
      int two_of_three_reports_valid, int own_tracked_alt,
      int own_tracked_alt_rate, int other_tracked_alt,
      int alt_layer_value, int up_separation, int down_separation,
      int other_rac, int other_capability, int climb_inhibit,
      std::ofstream& bFile) {
    bFile << "entry@start_symbolic\n";
    bFile << "node1@start_symbolic\n";

    initialize();
    Cur_Vertical_Sep = cur_vertical_sep;
    High_Confidence = high_confidence;
    Two_of_Three_Reports_Valid = two_of_three_reports_valid;
    Own_Tracked_Alt = own_tracked_alt;
    Own_Tracked_Alt_Rate = own_tracked_alt_rate;
    Other_Tracked_Alt = other_tracked_alt;

    // `Alt_Layer_Value` is only allowed to be: 0, 1, 2, 3
    if (alt_layer_value < 0 ) alt_layer_value = -alt_layer_value;
    Alt_Layer_Value = alt_layer_value % 4;

    Up_Separation = up_separation;
    Down_Separation = down_separation;
    Other_RAC = other_rac;
    Other_Capability = other_capability;
    Climb_Inhibit = climb_inhibit;

    instFunctionCall(bFile, "start_symbolic");
    alt_sep_test(bFile); // cout << (alt_sep_test()) << endl;

    bFile << "exit@start_symbolic\n";
  }

};
int Tcas::Positive_RA_Alt_Thresh[4];
int Tcas::Cur_Vertical_Sep;
int Tcas::High_Confidence;
int Tcas::Two_of_Three_Reports_Valid;
int Tcas::Own_Tracked_Alt;
int Tcas::Own_Tracked_Alt_Rate;
int Tcas::Other_Tracked_Alt;
int Tcas::Alt_Layer_Value;
int Tcas::Up_Separation;
int Tcas::Down_Separation;
int Tcas::Other_RAC;
int Tcas::Other_Capability;
int Tcas::Climb_Inhibit;
int Tcas::need_upward_RA;
int Tcas::need_downward_RA;

/*
 * Class:     cn_nju_seg_atg_callCPP_CallCPP
 * Method:    callTcasRun
 * Signature: (IIIIIIIIIIIILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callTcasRun(JNIEnv *env, jobject,
    jint cur_vertical_sep, jint high_confidence, jint two_of_three_reports_valid, jint own_tracked_alt,
    jint own_tracked_alt_rate, jint other_tracked_alt, jint alt_layer_value, jint up_separation,
    jint down_separation, jint other_rac, jint other_capability, jint climb_inhibit,
    jstring pathFile) {

  char* path = jstringTostring(env, pathFile);
  std::ofstream bFile(path);
  bFilePtr = &bFile;

  bFile<<"node1@tcasRun\n";

  instFunctionCall(bFile, "tcasRun");
  Tcas::start_symbolic(cur_vertical_sep,high_confidence,two_of_three_reports_valid,
      own_tracked_alt,own_tracked_alt_rate,other_tracked_alt,alt_layer_value,
      up_separation,down_separation,other_rac,other_capability,climb_inhibit,
      bFile);

  bFile<<"exit@tcasRun\n";

  delete []path;
  // bFile.close();
  bFilePtr = 0;

  return;
}
