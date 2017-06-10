/*
 * Author: Zhang Yifan
 */

#include "../cn_nju_seg_atg_callCPP_CallCPP.h"

#include <math.h>
#include <malloc.h>

#include <iostream>
#include <vector>
#include <limits>
#include <iomanip>
#include <fstream>
#include <cstring>

#include <jni.h>

using namespace std;

char* jstringTostring(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
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

	static int ALIM() {
		return Positive_RA_Alt_Thresh[Alt_Layer_Value];
	}

	static int Inhibit_Biased_Climb() {
		return ((Climb_Inhibit == 1) ?
				Up_Separation + Const::MINSEP /* operand mutation NOZCROSS */:
				Up_Separation);
	}

	static int Non_Crossing_Biased_Climb() {
		int upward_preferred;
		int upward_crossing_situation;
		int result;

		upward_preferred = (Inhibit_Biased_Climb() > Down_Separation) ? 1 : 0;
		if (upward_preferred != 0) {
			result =
					(Own_Below_Threat() != 1
							|| ((Own_Below_Threat() == 1)
									&& (!(Down_Separation >= ALIM())))) ? 1 : 0;
		} else {
			result =
					(Own_Above_Threat() == 1
							&& (Cur_Vertical_Sep >= Const::MINSEP)
							&& (Up_Separation >= ALIM())) ? 1 : 0;
		}
		return result;
	}

	static int Non_Crossing_Biased_Descend() {
		int upward_preferred;
		int upward_crossing_situation;
		int result;

		upward_preferred = (Inhibit_Biased_Climb() > Down_Separation) ? 1 : 0;

		if (upward_preferred != 0) {
			result =
					(Own_Below_Threat() == 1
							&& (Cur_Vertical_Sep >= Const::MINSEP)
							&& (Down_Separation >= ALIM())) ? 1 : 0;
		} else {
			result =
					(Own_Above_Threat() != 1
							|| ((Own_Above_Threat() == 1)
									&& (Up_Separation >= ALIM()))) ? 1 : 0;
		}
		return result;
	}

	static int Own_Below_Threat() {
		return ((Own_Tracked_Alt < Other_Tracked_Alt) ? 1 : 0);
	}

	static int Own_Above_Threat() {
		return ((Other_Tracked_Alt < Own_Tracked_Alt) ? 1 : 0);
	}

	/**
	 * @assert
	 * LOCATION[RAsComputed] noRAconflict:
	 * !(need_upward_RA == 1 && need_downward_RA==1);
	 */
	static int alt_sep_test() {
		int enabled, tcas_equipped, intent_not_known;
		int alt_sep;

		enabled =
				(High_Confidence == 1 && (Own_Tracked_Alt_Rate <= Const::OLEV)
						&& (Cur_Vertical_Sep > Const::MAXALTDIFF)) ? 1 : 0;
		tcas_equipped = (Other_Capability == Const::TCAS_TA) ? 1 : 0;
		intent_not_known =
				(Two_of_Three_Reports_Valid == 1
						&& Other_RAC == Const::NO_INTENT) ? 1 : 0;

		alt_sep = Const::UNRESOLVED;
		if (enabled == 1 && ((tcas_equipped == 1 && intent_not_known == 1) || tcas_equipped == 0)) {
			need_upward_RA =
					(Non_Crossing_Biased_Climb() == 1 && Own_Below_Threat() == 1) ?
							1 : 0;
			need_downward_RA =
					(Non_Crossing_Biased_Descend() == 1
							&& Own_Above_Threat() == 1) ? 1 : 0;
			if (need_upward_RA == 1 && need_downward_RA == 1)
				alt_sep = Const::UNRESOLVED;
			else if (need_upward_RA == 1)
				alt_sep = Const::UPWARD_RA;
			else if (need_downward_RA == 1)
				alt_sep = Const::DOWNWARD_RA;
			else
				alt_sep = Const::UNRESOLVED;
		}
		return alt_sep;
	}

// Added by pdinges
	static void start_symbolic(int cur_vertical_sep, int high_confidence,
			int two_of_three_reports_valid, int own_tracked_alt,
			int own_tracked_alt_rate, int other_tracked_alt,
			int alt_layer_value, int up_separation, int down_separation,
			int other_rac, int other_capability, int climb_inhibit) {
		initialize();
		Cur_Vertical_Sep = cur_vertical_sep;
		High_Confidence = high_confidence;
		Two_of_Three_Reports_Valid = two_of_three_reports_valid;
		Own_Tracked_Alt = own_tracked_alt;
		Own_Tracked_Alt_Rate = own_tracked_alt_rate;
		Other_Tracked_Alt = other_tracked_alt;
		Alt_Layer_Value = alt_layer_value;
		Up_Separation = up_separation;
		Down_Separation = down_separation;
		Other_RAC = other_rac;
		Other_Capability = other_capability;
		Climb_Inhibit = climb_inhibit;
		cout << (alt_sep_test()) << endl;
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

//void tcasRun(int cur_vertical_sep,
//        int high_confidence,
//        int two_of_three_reports_valid,
//        int own_tracked_alt,
//        int own_tracked_alt_rate,
//        int other_tracked_alt,
//        int alt_layer_value,
//        int up_separation,
//        int down_separation,
//        int other_rac,
//        int other_capability,
//        int climb_inhibit){
//	Tcas::start_symbolic(cur_vertical_sep,high_confidence,two_of_three_reports_valid,
//			own_tracked_alt,own_tracked_alt_rate,other_tracked_alt,alt_layer_value,
//			up_separation,down_separation,other_rac,other_capability,climb_inhibit);
//}

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
	ofstream bFile(path);
	bFile<<"node1@tcasRun\n";

	bFile<<"call@tcasRun\n";
	Tcas::start_symbolic(cur_vertical_sep,high_confidence,two_of_three_reports_valid,
			own_tracked_alt,own_tracked_alt_rate,other_tracked_alt,alt_layer_value,
			up_separation,down_separation,other_rac,other_capability,climb_inhibit);

	bFile<<"exit@tcasRun\n";
	return;
}

//
///*
// * Class:     cn_nju_seg_atg_callCPP_CallCPP
// * Method:    callStat
// * Signature: (ILjava/lang/String;)V
// */
//JNIEXPORT void JNICALL Java_cn_nju_seg_atg_callCPP_CallCPP_callStat
//  (JNIEnv *env, jobject, jint val, jstring pathFile){
//	char* path = jstringTostring(env, pathFile);
//	ofstream bFile(path);
//    bFile<<"node1@stat\n";
//	printf("adding value\n");
//	bFile<<"call@stat\n";
//	StatCalculator::addValue_inst(val,path,bFile);
//	bFile<<"call@stat\n";
//	StatCalculator::addValue_inst(val,path,bFile);
//	bFile<<"call@stat\n";
//    StatCalculator::addValue_inst(val,path,bFile);
//    bFile<<"call@stat\n";
//	StatCalculator::addValue_inst(val,path,bFile);
//
//	if(bFile<<"node2@stat "<<StatCalculator::getMedian()-3<<
//			" expression@2\n",StatCalculator::getMedian() == 3) {
//	    bFile<<"node3@stat\n";
//		printf("median value is 3\n");
//	} else {
//		bFile<<"node4@stat\n";
//	    printf("median value is not 3\n");
//	}
//	if(bFile<<"node5@stat "<<StatCalculator::getStandardDeviation()-0.82915619758885<<
//			" expression@4\n",StatCalculator::getStandardDeviation() <= 0.82915619758885) {
//		bFile<<"node6@stat\n";
//		printf("std deviation is .10\n");
//	} else {
//		bFile<<"node7@stat\n";
//	    printf("std deviation not found\n");
//	}
//	bFile<<"exit@stat\n";
////	StatCalculator::clear();
//}
