#include <iostream>
using namespace std;

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

//  static int Inhibit_Biased_Climb() {
//    // return ((Climb_Inhibit == 1) ? Up_Separation + Const::MINSEP /* operand mutation NOZCROSS */ : Up_Separation);
//    if (Climb_Inhibit == 1) {
//      return Up_Separation + Const::MINSEP; /* operand mutation NOZCROSS */
//    } else {
//      return Up_Separation;
//    }
//  }
  static int Inhibit_Biased_Climb;

  static int Inhibit_Biased_Climb1() {

    // return ((Climb_Inhibit == 1) ? Up_Separation + Const::MINSEP /* operand mutation NOZCROSS */ : Up_Separation);
    if (Climb_Inhibit == 1) {
      return Up_Separation + Const::MINSEP; /* operand mutation NOZCROSS */
    } else {
      return Up_Separation;
    }
  }

  static int Inhibit_Biased_Climb2() {

    // return ((Climb_Inhibit == 1) ? Up_Separation + Const::MINSEP /* operand mutation NOZCROSS */ : Up_Separation);
    if (Climb_Inhibit == 1) {
      return Up_Separation + Const::MINSEP; /* operand mutation NOZCROSS */
    } else {
      return Up_Separation;
    }
  }

  static int Non_Crossing_Biased_Climb() {
    int upward_preferred;
    int upward_crossing_situation;
    int result;

//    upward_preferred = (Inhibit_Biased_Climb() > Down_Separation)?1:0;
//    if (upward_preferred!=0){
//        result = (Own_Below_Threat()!=1 ||
//                ((Own_Below_Threat()==1) && (!(Down_Separation >= ALIM()))))?1:0;
//    }else{
//        result = (Own_Above_Threat()==1 &&
//                (Cur_Vertical_Sep >= Const::MINSEP) && (Up_Separation >= ALIM()))?1:0;
//    }
//    return result;

// upward_preferred = (Inhibit_Biased_Climb() > Down_Separation) ? 1 : 0;
// int temp1 = Inhibit_Biased_Climb1(); // since the program can not handle function calls in the if expression, so extra a new temp variable
    if (Inhibit_Biased_Climb > Down_Separation) {
      upward_preferred = 1;
    } else {
      upward_preferred = 0;
    }

//    if (upward_preferred!=0){
//        result = (Own_Below_Threat()!=1 ||
//                ((Own_Below_Threat()==1) && (!(Down_Separation >= ALIM()))))?1:0;
//    }else{
//        result = (Own_Above_Threat()==1 &&
//                (Cur_Vertical_Sep >= Const::MINSEP) && (Up_Separation >= ALIM()))?1:0;
//    }
//    return result;
    int temp2 = Own_Below_Threat;
    int temp3 = Own_Above_Threat;
    if (upward_preferred != 0) {
      // result = (Own_Below_Threat() != 1 || ((Own_Below_Threat() == 1) && (!(Down_Separation >= ALIM())))) ? 1 : 0;
      if (temp2 != 1) {
        result = 1;
      } else {
        if (temp2 == 1) {
          // Note: current program has bug if writes as "!(Down_Separation >= ALIM())",
          // which cause a `ClassCastException` of CPPASTIdExpression cannot be cast to IASTBinaryExpression
          // see `IfStatementVisitor` line 376
          if (Down_Separation < Positive_RA_Alt_Thresh[Alt_Layer_Value]) { // Down_Separation < ALIM()
            result = 1;
          } else {
            result = 0;
          }
        } else {
          result = 0;
        }
      }
    } else {
      // result = (Own_Above_Threat() == 1 && (Cur_Vertical_Sep >= Const::MINSEP) && (Up_Separation >= ALIM())) ? 1 : 0;
      if (temp3 == 1) {
        if (Cur_Vertical_Sep >= Const::MINSEP) {
          if (Up_Separation >= Positive_RA_Alt_Thresh[Alt_Layer_Value]) { // Up_Separation >= ALIM()
            result = 1;
          } else {
            result = 0;
          }
        } else {
          result = 0;
        }
      } else {
        result = 0;
      }
    }
    return result;
  }

  static int Non_Crossing_Biased_Descend() {
    int upward_preferred;
    int upward_crossing_situation;
    int result;

//    upward_preferred = (Inhibit_Biased_Climb() > Down_Separation)?1:0;
//    if (upward_preferred!=0){
//       result = (Own_Below_Threat()==1 &&
//                (Cur_Vertical_Sep >= Const::MINSEP) && (Down_Separation >= ALIM()))?1:0;
//    }else{
//        result = (Own_Above_Threat()!=1 ||
//                ((Own_Above_Threat()==1)
//                 && (Up_Separation >= ALIM())))?1:0;
//    }
//    return result;

    // upward_preferred = (Inhibit_Biased_Climb() > Down_Separation) ? 1 : 0;
    // int temp1 = Inhibit_Biased_Climb2();
    if (Inhibit_Biased_Climb > Down_Separation) {
      upward_preferred = 1;
    } else {
      upward_preferred = 0;
    }

    int temp2 = Own_Below_Threat;
    int temp3 = Own_Above_Threat;
    if (upward_preferred != 0) {
      // result = (Own_Below_Threat() == 1 && (Cur_Vertical_Sep >= Const::MINSEP) && (Down_Separation >= ALIM())) ? 1 : 0;
      if (temp2 == 1) {
        if (Cur_Vertical_Sep >= Const::MINSEP) {
          if (Down_Separation >= Positive_RA_Alt_Thresh[Alt_Layer_Value]) { // Down_Separation >= ALIM()
            result = 1;
          } else {
            result = 0;
          }
        } else {
          result = 0;
        }
      } else {
        result = 0;
      }
    } else {
      // result = (Own_Above_Threat() != 1 || ((Own_Above_Threat() == 1) && (Up_Separation >= ALIM()))) ? 1 : 0;
      if (temp3 != 1) {
        result = 1;
      } else {
        if (temp3 == 1) {
          if (Up_Separation >= Positive_RA_Alt_Thresh[Alt_Layer_Value]) { // Up_Separation >= ALIM()
            result = 1;
          } else {
            result = 0;
          }
        } else {
          result = 0;
        }
      }
    }
    return result;
  }

//  static int Own_Below_Threat() {
//    return ((Own_Tracked_Alt < Other_Tracked_Alt) ? 1 : 0);
////    if (Own_Tracked_Alt < Other_Tracked_Alt) {
////      return 1;
////    } else {
////      return 0;
////    }
//  }
//
//  static int Own_Above_Threat() {
//    return ((Other_Tracked_Alt < Own_Tracked_Alt) ? 1 : 0);
////    if (Other_Tracked_Alt < Own_Tracked_Alt) {
////      return 1;
////    } else {
////      return 0;
////    }
//  }

  static int Own_Above_Threat; // = ((Other_Tracked_Alt < Own_Tracked_Alt) ? 1 : 0);
  static int Own_Below_Threat; // = (Own_Tracked_Alt < Other_Tracked_Alt) ? 1 : 0);

  // Added by ZYF
  static int alt_sep_test_helper1() {

    // need_upward_RA = (Non_Crossing_Biased_Climb() == 1 && Own_Below_Threat() == 1) ? 1 : 0;
    int temp1 = Non_Crossing_Biased_Climb();
    int temp2 = Own_Below_Threat;
    if (temp1 == 1) {
      if (temp2 == 1) {
        need_upward_RA = 1;
      } else {
        need_upward_RA = 0;
      }
    } else {
      need_upward_RA = 0;
    }

    // need_downward_RA = (Non_Crossing_Biased_Descend() == 1 && Own_Above_Threat() == 1) ? 1 : 0;
    int temp3 = Non_Crossing_Biased_Descend();
    int temp4 = Own_Above_Threat;
    if (temp3 == 1) {
      if (temp4 == 1) {
        need_downward_RA = 1;
      } else {
        need_downward_RA = 0;
      }
    } else {
      need_downward_RA = 0;
    }

//    if (need_upward_RA == 1 && need_downward_RA == 1)
//      return Const::UNRESOLVED;
//    else if (need_upward_RA == 1)
//      return Const::UPWARD_RA;
//    else if (need_downward_RA == 1)
//      return Const::DOWNWARD_RA;
//    else
//      return Const::UNRESOLVED;
    bool tempIfExprVar0 = false;
    if (need_upward_RA == 1) {
      if (need_downward_RA == 1) {
        tempIfExprVar0 = true;
      } else {
        tempIfExprVar0 = false;
      }
    } else {
      tempIfExprVar0 = false;
    }

    if (tempIfExprVar0 == true)
      return Const::UNRESOLVED;
    else if (need_upward_RA == 1)
      return Const::UPWARD_RA;
    else if (need_downward_RA == 1)
      return Const::DOWNWARD_RA;
    else
      return Const::UNRESOLVED;

    // Deprecated expansion
//    int returnValue = 0;
//    if (need_upward_RA == 1) {
//      if (need_downward_RA == 1) {
//        return Const::UNRESOLVED;
//      } else {
//        // leave statement for simple branch coverage
//        returnValue = Const::UNRESOLVED;
//      }
//    } else {
//      returnValue = Const::UNRESOLVED;
//    }
//
//    if (need_upward_RA == 1)
//      return Const::UPWARD_RA;
//    else if (need_downward_RA == 1)
//      return Const::DOWNWARD_RA;
//    else
//      return Const::UNRESOLVED;
  }

  /**
   * @assert
   * LOCATION[RAsComputed] noRAconflict:
   * !(need_upward_RA == 1 && need_downward_RA==1);
   */
  static int alt_sep_test() {
    int enabled, tcas_equipped, intent_not_known;
    int alt_sep;

    // enabled = (High_Confidence == 1 && (Own_Tracked_Alt_Rate <= Const::OLEV) && (Cur_Vertical_Sep > Const::MAXALTDIFF)) ? 1 : 0;
    if (High_Confidence == 1) {
      if (Own_Tracked_Alt_Rate <= Const::OLEV) {
        if (Cur_Vertical_Sep > Const::MAXALTDIFF) {
          enabled = 1;
        } else {
          enabled = 0;
        }
      } else {
        enabled = 0;
      }
    } else {
      enabled = 0;
    }

    // tcas_equipped = (Other_Capability == Const::TCAS_TA) ? 1 : 0;
    if (Other_Capability == Const::TCAS_TA) {
      tcas_equipped = 1;
    } else {
      tcas_equipped = 0;
    }

    // intent_not_known = (Two_of_Three_Reports_Valid == 1 && Other_RAC == Const::NO_INTENT) ? 1 : 0;
    if (Two_of_Three_Reports_Valid == 1) {
      if (Other_RAC == Const::NO_INTENT) {
        intent_not_known = 1;
      } else {
        intent_not_known = 0;
      }
    } else {
      intent_not_known = 0;
    }

    alt_sep = Const::UNRESOLVED;
//    if (enabled==1 && ((tcas_equipped==1 && intent_not_known==1)
//                || tcas_equipped==0)){
//        need_upward_RA = (Non_Crossing_Biased_Climb()==1
//                && Own_Below_Threat()==1)?1:0;
//        need_downward_RA = (Non_Crossing_Biased_Descend()==1
//                && Own_Above_Threat()==1)?1:0;
//        if (need_upward_RA==1 && need_downward_RA==1)
//           alt_sep = Const::UNRESOLVED;
//        else if (need_upward_RA==1) alt_sep = Const::UPWARD_RA;
//        else if (need_downward_RA==1) alt_sep = Const::DOWNWARD_RA;
//        else alt_sep = Const::UNRESOLVED;
//    }

    // Note: we change ((tcas_equipped == 1 && intent_not_known == 1) || tcas_equipped == 0) to
    // (tcas_equipped == 0 || (tcas_equipped == 1 && intent_not_known == 1)) for simple expansion
    bool tempIfExprVar0 = false;
    if (enabled == 1) {
      if (tcas_equipped == 0) {
        tempIfExprVar0 = true;
      } else {
        if (tcas_equipped == 1) {
          if (intent_not_known == 1) {
            tempIfExprVar0 = true;
          } else {
            tempIfExprVar0 = false;
          }
        } else {
          tempIfExprVar0 = false;
        }
      }
    } else {
      tempIfExprVar0 = false;
    }

    if (tempIfExprVar0 == true) {
      alt_sep = alt_sep_test_helper1();
    } else {
      alt_sep = Const::UNRESOLVED;
    }

    // Deprecated expansion
//    // Note: we change ((tcas_equipped == 1 && intent_not_known == 1) || tcas_equipped == 0) to
//    // (tcas_equipped == 0 || (tcas_equipped == 1 && intent_not_known == 1)) for simple expansion
//    if (enabled == 1) {
//      if (tcas_equipped == 0) {
//        alt_sep = alt_sep_test_helper1();
//      } else {
//        if (tcas_equipped == 1) {
//          if (intent_not_known == 1) {
//            alt_sep = alt_sep_test_helper1();
//          } else {
//            alt_sep = Const::UNRESOLVED;
//          }
//        } else {
//          alt_sep = Const::UNRESOLVED;
//        }
//      }
//    } else {
//      alt_sep = Const::UNRESOLVED;
//    }

    return alt_sep;
  }

// Added by pdinges
  static void start_symbolic(int cur_vertical_sep, int high_confidence,
      int two_of_three_reports_valid, int own_tracked_alt,
      int own_tracked_alt_rate, int other_tracked_alt, int alt_layer_value,
      int up_separation, int down_separation, int other_rac,
      int other_capability, int climb_inhibit) {
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

    Own_Above_Threat = ((Other_Tracked_Alt < Own_Tracked_Alt) ? 1 : 0);
    Own_Below_Threat = ((Own_Tracked_Alt < Other_Tracked_Alt) ? 1 : 0);
    if (Climb_Inhibit == 1) {
      Inhibit_Biased_Climb = Up_Separation + Const::MINSEP; /* operand mutation NOZCROSS */
    } else {
      Inhibit_Biased_Climb = Up_Separation;
    }

    alt_sep_test();
    // cout << (alt_sep_test()) << endl;
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

void tcasRun(int cur_vertical_sep, int high_confidence,
    int two_of_three_reports_valid, int own_tracked_alt,
    int own_tracked_alt_rate, int other_tracked_alt, int alt_layer_value,
    int up_separation, int down_separation, int other_rac, int other_capability,
    int climb_inhibit) {
  Tcas::start_symbolic(cur_vertical_sep, high_confidence,
      two_of_three_reports_valid, own_tracked_alt, own_tracked_alt_rate,
      other_tracked_alt, alt_layer_value, up_separation, down_separation,
      other_rac, other_capability, climb_inhibit);
}
