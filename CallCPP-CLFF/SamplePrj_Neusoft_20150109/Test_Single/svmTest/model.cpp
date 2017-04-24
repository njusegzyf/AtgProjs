/*****************************************************************************
*
* Neusoft Confidential Proprietary
*
* Copyright (c) 2013 Neusoft AAC;
* All Rights Reserved
*
*****************************************************************************
*
* THIS SOFTWARE IS PROVIDED BY NEUSOFT "AS IS" AND ANY EXPRESSED OR
* IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
* IN NO EVENT SHALL NEUSOFT OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
* INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
* HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
* STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
* IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
* THE POSSIBILITY OF SUCH DAMAGE.
*
****************************************************************************/
/**
* \brief	Model used for Pedestrain Detection
*
* \author	2013/04/04, zhanglk : created.\n
****************************************************************************/

#include "stdafx.h"
#include "feature.h"


double b_valueUP = 0.294123;
double weightListUP[] ={0.018587,0.010648,0.002514,0.003799,0.001245,0.000678,-0.008678,-0.002518,-0.029767,-0.013343,0.011686,0.010409,0.010686,0.016143,0.010792,0.003252,-0.002246,-0.019188,0.017605,0.003569,0.004749,0.013150,-0.001796,-0.007740,0.000632,0.000112,0.000064,-0.001158,0.005481,0.003357,-0.003640,-0.003443,-0.001911,-0.000897,-0.003224,0.003453,0.017605,0.003569,0.004749,0.013150,-0.001796,-0.007740,0.000632,0.000112,0.000064,-0.001158,0.005481,0.003357,-0.003640,-0.003443,-0.001911,-0.000897,-0.003224,0.003453,0.012739,0.011075,0.008613,0.007554,-0.000993,0.002316,0.005661,-0.003815,-0.019408,-0.000672,0.008280,0.000281,0.003504,-0.005452,-0.001312,0.013549,0.014958,-0.014497,0.012739,0.011075,0.008613,0.007554,-0.000993,0.002316,0.005661,-0.003815,-0.019408,-0.000672,0.008280,0.000281,0.003504,-0.005452,-0.001312,0.013549,0.014958,-0.014497,0.014831,0.013273,0.010514,0.010560,-0.011858,-0.006771,0.009950,0.002400,-0.010220,0.002590,0.001238,0.006591,0.005496,-0.005722,-0.007192,0.004021,-0.003162,-0.010934,-0.013343,0.011686,0.010409,0.010686,0.016143,0.010792,0.003252,-0.002246,-0.019188,-0.015591,0.023432,0.020505,0.012553,0.007683,0.006125,0.003130,-0.002020,0.000026,-0.001158,0.005481,0.003357,-0.003640,-0.003443,-0.001911,-0.000897,-0.003224,0.003453,-0.013401,0.017015,0.009896,0.005910,-0.012035,-0.011010,-0.000592,0.003936,0.010325,-0.001158,0.005481,0.003357,-0.003640,-0.003443,-0.001911,-0.000897,-0.003224,0.003453,-0.013401,0.017015,0.009896,0.005910,-0.012035,-0.011010,-0.000592,0.003936,0.010325,-0.000672,0.008280,0.000281,0.003504,-0.005452,-0.001312,0.013549,0.014958,-0.014497,-0.002280,0.010806,0.003127,0.002665,-0.009790,-0.004684,0.008878,0.011209,-0.006163,-0.000672,0.008280,0.000281,0.003504,-0.005452,-0.001312,0.013549,0.014958,-0.014497,-0.002280,0.010806,0.003127,0.002665,-0.009790,-0.004684,0.008878,0.011209,-0.006163,0.002590,0.001238,0.006591,0.005496,-0.005722,-0.007192,0.004021,-0.003162,-0.010934,-0.007441,-0.002380,0.006323,0.005215,-0.003694,-0.014375,0.004083,0.001442,-0.000487,-0.015591,0.023432,0.020505,0.012553,0.007683,0.006125,0.003130,-0.002020,0.000026,-0.015946,0.012623,0.002454,0.003748,0.004663,0.006446,0.004922,0.007029,0.002153,-0.013401,0.017015,0.009896,0.005910,-0.012035,-0.011010,-0.000592,0.003936,0.010325,-0.008930,0.007765,0.004617,0.009355,0.008568,-0.002181,0.003235,0.000049,-0.001691,-0.013401,0.017015,0.009896,0.005910,-0.012035,-0.011010,-0.000592,0.003936,0.010325,-0.008930,0.007765,0.004617,0.009355,0.008568,-0.002181,0.003235,0.000049,-0.001691,-0.002280,0.010806,0.003127,0.002665,-0.009790,-0.004684,0.008878,0.011209,-0.006163,0.002660,0.015043,0.014730,0.010920,-0.005410,-0.009819,0.003871,0.015344,0.009951,-0.002280,0.010806,0.003127,0.002665,-0.009790,-0.004684,0.008878,0.011209,-0.006163,0.002660,0.015043,0.014730,0.010920,-0.005410,-0.009819,0.003871,0.015344,0.009951,-0.007441,-0.002380,0.006323,0.005215,-0.003694,-0.014375,0.004083,0.001442,-0.000487,-0.006921,0.001184,-0.004363,-0.003024,0.004238,-0.002359,0.000798,0.003409,0.004965,-0.015946,0.012623,0.002454,0.003748,0.004663,0.006446,0.004922,0.007029,0.002153,-0.000553,0.015029,-0.008672,-0.000969,-0.011579,-0.007509,0.006028,0.008473,0.011896,-0.008930,0.007765,0.004617,0.009355,0.008568,-0.002181,0.003235,0.000049,-0.001691,-0.002990,0.004696,0.003270,0.007227,-0.011093,-0.017053,0.016147,0.011318,0.007037,-0.008930,0.007765,0.004617,0.009355,0.008568,-0.002181,0.003235,0.000049,-0.001691,-0.002990,0.004696,0.003270,0.007227,-0.011093,-0.017053,0.016147,0.011318,0.007037,0.002660,0.015043,0.014730,0.010920,-0.005410,-0.009819,0.003871,0.015344,0.009951,-0.007763,0.017186,0.015049,0.015052,-0.007411,-0.020143,0.010003,0.008009,0.013965,0.002660,0.015043,0.014730,0.010920,-0.005410,-0.009819,0.003871,0.015344,0.009951,-0.007763,0.017186,0.015049,0.015052,-0.007411,-0.020143,0.010003,0.008009,0.013965,-0.006921,0.001184,-0.004363,-0.003024,0.004238,-0.002359,0.000798,0.003409,0.004965,-0.012199,-0.000418,-0.001268,-0.000294,-0.013753,-0.012259,0.001048,-0.000147,0.001602,-0.000553,0.015029,-0.008672,-0.000969,-0.011579,-0.007509,0.006028,0.008473,0.011896,0.021320,0.008249,0.007570,0.012047,0.001607,0.005048,0.010358,0.009276,0.014891,-0.002990,0.004696,0.003270,0.007227,-0.011093,-0.017053,0.016147,0.011318,0.007037,0.025847,0.016373,0.011947,0.005705,-0.008455,-0.013666,0.010505,0.020103,0.025672,-0.002990,0.004696,0.003270,0.007227,-0.011093,-0.017053,0.016147,0.011318,0.007037,0.025847,0.016373,0.011947,0.005705,-0.008455,-0.013666,0.010505,0.020103,0.025672,-0.007763,0.017186,0.015049,0.015052,-0.007411,-0.020143,0.010003,0.008009,0.013965,0.033545,0.018151,0.013259,0.010235,-0.007008,-0.001305,0.012750,0.013363,0.019915,-0.007763,0.017186,0.015049,0.015052,-0.007411,-0.020143,0.010003,0.008009,0.013965,0.033545,0.018151,0.013259,0.010235,-0.007008,-0.001305,0.012750,0.013363,0.019915,-0.012199,-0.000418,-0.001268,-0.000294,-0.013753,-0.012259,0.001048,-0.000147,0.001602,0.020624,0.007207,0.005864,0.005879,-0.003375,0.004554,0.005939,0.006706,0.011687,0.021320,0.008249,0.007570,0.012047,0.001607,0.005048,0.010358,0.009276,0.014891,0.022297,0.013321,0.022365,0.016457,0.035678,0.033299,0.007313,0.005603,0.014618,0.025847,0.016373,0.011947,0.005705,-0.008455,-0.013666,0.010505,0.020103,0.025672,0.033227,0.025458,0.022003,0.013409,0.019679,0.017982,0.010183,0.012069,0.024688,0.025847,0.016373,0.011947,0.005705,-0.008455,-0.013666,0.010505,0.020103,0.025672,0.033227,0.025458,0.022003,0.013409,0.019679,0.017982,0.010183,0.012069,0.024688,0.033545,0.018151,0.013259,0.010235,-0.007008,-0.001305,0.012750,0.013363,0.019915,0.038830,0.019257,0.011251,0.009999,0.018619,0.021608,0.017178,0.021551,0.020834,0.033545,0.018151,0.013259,0.010235,-0.007008,-0.001305,0.012750,0.013363,0.019915,0.038830,0.019257,0.011251,0.009999,0.018619,0.021608,0.017178,0.021551,0.020834,0.020624,0.007207,0.005864,0.005879,-0.003375,0.004554,0.005939,0.006706,0.011687,0.025673,0.009683,0.005155,0.004846,0.024700,0.028886,0.014881,0.020237,0.020358,0.022297,0.013321,0.022365,0.016457,0.035678,0.033299,0.007313,0.005603,0.014618,0.023532,0.016340,0.015356,0.015059,0.033850,0.045264,0.022066,0.013409,0.013480,0.033227,0.025458,0.022003,0.013409,0.019679,0.017982,0.010183,0.012069,0.024688,0.034084,0.017065,0.019820,0.014515,0.034625,0.040850,0.016949,0.011106,0.018127,0.033227,0.025458,0.022003,0.013409,0.019679,0.017982,0.010183,0.012069,0.024688,0.034084,0.017065,0.019820,0.014515,0.034625,0.040850,0.016949,0.011106,0.018127,0.038830,0.019257,0.011251,0.009999,0.018619,0.021608,0.017178,0.021551,0.020834,0.027366,0.013826,0.011517,0.014179,0.035779,0.038756,0.020187,0.018116,0.014725,0.038830,0.019257,0.011251,0.009999,0.018619,0.021608,0.017178,0.021551,0.020834,0.027366,0.013826,0.011517,0.014179,0.035779,0.038756,0.020187,0.018116,0.014725,0.025673,0.009683,0.005155,0.004846,0.024700,0.028886,0.014881,0.020237,0.020358,0.018334,0.014904,0.013292,0.010953,0.033729,0.036464,0.015099,0.011130,0.012206,0.003261,-0.000561,-0.000597,0.000106,-0.006881,-0.003820,-0.005986,-0.004258,-0.008758,-0.004178,-0.006363,0.001542,-0.000009,-0.004253,-0.000689,-0.002704,0.001093,0.001730,0.000611,-0.013064,-0.002774,-0.005186,0.004269,0.001815,0.003745,0.003061,0.001806,0.001427,0.000284,0.001767,0.001437,0.008642,0.001367,-0.000911,-0.000763,-0.005167,0.003028,0.004202,0.006941,0.002736,-0.000130,-0.000058,0.003290,0.002612,0.001084,-0.001201,-0.001098,0.009240,0.003161,0.000978,0.000507,0.002766,-0.000318,0.002653,0.001188,0.000918,0.002597,0.000298,0.007154,0.001773,-0.000760,0.000152,0.000367,-0.000317,-0.002774,-0.003149,-0.001499,-0.004502,-0.004202,-0.001633,0.000913,0.000823,-0.003076,0.002755,-0.002598,0.001114,0.002232,0.000284,-0.023702,0.001225,-0.000242,0.003263,0.002346,0.004122,0.003744,0.001715,0.000357,0.000213,0.001111,0.001302,0.004989,0.004067,0.000667,0.000031,-0.000107,0.002027,0.004708,0.004700,0.002166,-0.000226,0.000658,0.000718,0.006599,0.001875,-0.000152,-0.000659,0.011055,0.004041,0.001248,0.000821,0.002342,-0.000490,0.001107,0.000940,0.000527,0.001333,0.001380,0.008270,0.010093,0.000268,0.000933,0.000819,0.002623,-0.000965,-0.000046,-0.000170,-0.001247,-0.005395,0.000137,0.000505,0.000670,-0.003450,0.002091,-0.000704,0.001012,0.001362,0.002040,-0.020728,-0.000030,-0.000495,0.002329,0.002563,0.002267,0.002503,0.001149,-0.000314,0.000305,0.000805,0.001319,0.003115,0.004481,0.002166,0.001116,0.001721,0.001104,0.002738,0.004001,0.001936,0.000228,0.000115,-0.000681,0.005963,0.002729,0.000833,0.000345,0.005346,0.002216,0.000589,0.000534,0.000722,0.000523,0.001111,-0.000160,0.000055,0.000303,0.001505,0.005778,-0.003308,-0.001070,0.000200,0.002208,-0.006729,-0.002277,-0.001266,-0.002801,-0.006040,-0.000373,-0.000275,0.001668,0.000175,-0.000800,0.001773,-0.001225,0.000393,0.001479,-0.001406,-0.005909,-0.000386,-0.003854,0.001416,0.001869,0.003527,0.003185,0.000409,0.001800,-0.000158,0.001444,0.001248,0.007167,0.008186,0.001790,0.000251,-0.006929,0.003289,0.003843,0.005542,0.002602,0.000166,-0.000123,0.006402,0.001740,0.001997,-0.000722,-0.000863,0.008798,0.002745,0.002410,0.000763,0.003355,-0.001993,0.000828,0.001463,0.000739,0.001715,0.000686,0.001268,-0.008622,-0.002515,-0.000437,0.000539,-0.001855,-0.002679,-0.001946,-0.002150,-0.002524,-0.002261,-0.000746,0.000812,0.001335,0.001522,0.003668,-0.003596,0.000216,0.002507,-0.000733,-0.015431,0.000702,-0.000561,-0.000687,0.001923,0.003579,0.009107,0.001265,0.000343,-0.000386,-0.000094,0.000544,0.006894,0.004910,0.000456,0.000208,-0.002377,0.001272,0.004738,0.005370,0.001794,-0.000968,0.000146,0.004272,0.003000,0.002107,-0.000406,-0.001834,0.010399,0.003020,0.001825,0.000697,0.002592,-0.001226,0.000362,0.001137,0.000418,0.000004,-0.000149,0.002025,0.004833,0.000579,0.000394,0.000866,0.001001,-0.000942,-0.000671,-0.001189,-0.000635,-0.001090,0.002043,-0.000460,0.000476,-0.002142,0.002937,-0.001938,0.000749,0.000984,-0.000926,-0.016970,0.000120,-0.001871,0.000285,0.001105,0.001087,0.007543,0.000807,-0.000654,0.000423,-0.000818,0.000896,0.004948,0.002256,0.000844,0.001099,-0.001055,-0.000317,0.003383,0.005666,0.001699,-0.000323,-0.000209,0.001264,0.005691,0.002206,0.000566,0.002050,0.006427,0.001382,0.000310,0.000192,0.001286,0.000492,0.000521,0.000199,0.000045,-0.000312,0.001688,0.004084,-0.006869,0.000526,-0.001016,0.001356,-0.003333,-0.000890,0.004708,0.000295,0.000126,0.000497,0.001535,0.003427,0.000028,0.002175,0.002832,-0.000758,0.000429,0.000957,-0.004888,-0.001020,0.001181,-0.001701,-0.002693,0.001051,0.002855,0.000547,-0.000979,0.000876,0.000349,-0.000550,0.001069,0.006352,0.014287,0.003739,0.000445,-0.001888,0.001304,0.001698,0.003737,0.003932,0.000839,0.000078,0.003637,0.000689,0.003223,0.001040,-0.000591,0.005841,0.000577,0.000583,0.000288,0.001943,-0.001808,-0.001026,0.000792,-0.000159,0.000442,-0.001850,-0.003323,-0.006793,-0.002875,-0.002284,-0.000379,-0.001305,-0.001710,0.001832,-0.000505,0.000768,-0.002268,-0.000474,0.001708,0.002537,0.003236,0.002311,-0.001999,0.000320,0.003729,-0.002660,-0.004774,0.001977,-0.001511,-0.003720,0.001529,0.003900,0.008477,-0.000448,0.001141,0.000159,-0.003053,0.000170,0.007076,0.007017,0.001771,-0.000452,-0.001148,-0.001106,0.002161,0.006169,0.002252,-0.000612,-0.000062,-0.006861,-0.005575,0.002594,-0.000021,-0.002652,0.006262,-0.003489,0.000193,-0.000237,0.002551,0.000266,0.000870,0.001610,0.000331,-0.001657,-0.001242,-0.001063,0.005040,-0.000038,-0.000925,0.000489,-0.001219,-0.000472,-0.000565,-0.001592,-0.000797,0.000645,0.002958,0.000096,0.001169,0.000722,0.002616,-0.000764,-0.000653,0.001353,-0.006091,-0.011381,0.002059,-0.000980,-0.000598,0.000042,0.000986,0.007303,-0.000667,0.000724,0.000340,-0.001606,0.001616,0.005998,0.002240,0.000878,0.000645,-0.002692,-0.001398,0.004118,0.006098,0.001574,-0.000333,0.000198,-0.009384,-0.005332,0.002260,0.001003,0.001114,0.003338,-0.004281,0.000096,0.000057,0.003192,0.000411,0.001191,0.001140,0.000511,-0.000903,0.001129,0.007068,-0.011136,0.001932,-0.000934,0.000563,-0.001233,0.001161,0.002518,0.002552,0.000924,0.001303,0.003810,0.004028,0.001414,0.001562,0.001439,0.001182,0.000168,0.001768,-0.002194,0.002633,0.000596,-0.001249,-0.003711,0.001337,0.002896,0.001752,0.000023,0.000909,-0.000079,-0.001303,0.000655,0.004360,0.007758,0.002325,0.000257,0.000460,-0.000144,0.002350,0.003395,0.003145,0.000798,0.000384,-0.002187,0.003231,0.002913,0.000459,0.001185,0.004777,-0.000482,-0.000507,-0.000580,0.001474,-0.001084,-0.000699,0.000630,-0.000459,0.001670,-0.001714,-0.000319,-0.007624,-0.000679,-0.001879,0.000723,0.000555,0.001481,0.005380,0.000972,0.001220,0.003063,0.004595,0.001765,0.001640,0.003212,0.001973,0.000355,0.000260,0.003043,-0.000994,0.001367,0.003032,-0.000369,-0.002432,0.002575,0.004278,0.003248,-0.001619,0.001078,0.000605,-0.002057,0.001398,0.005216,0.008243,0.002622,-0.000883,-0.000026,-0.001552,0.001618,0.006037,0.003072,0.000541,-0.000241,-0.014606,-0.005118,0.002522,0.000223,-0.000851,0.005771,-0.006600,-0.000668,-0.000348,0.001926,0.000437,0.000509,0.001347,0.000322,-0.000241,-0.000264,0.000976,0.002391,0.002461,-0.000165,0.001241,-0.001214,0.001308,0.003845,-0.001744,-0.000430,0.006982,0.005895,0.001689,0.000258,0.000787,0.003425,0.000720,-0.001353,-0.000112,-0.009194,-0.005440,0.002642,0.000529,-0.000069,0.001501,-0.000196,-0.000924,-0.004709,0.000485,0.000256,-0.000009,0.002427,0.005466,0.005609,0.002818,0.000466,-0.001209,-0.000418,0.003417,0.005462,0.002244,-0.000272,-0.000205,-0.015922,-0.009564,0.002293,0.002082,0.000622,0.003505,-0.006046,0.000408,0.000394,0.002668,-0.000710,0.000239,0.001017,0.000558,0.001035,0.001386,0.009360,-0.036447,0.004242,0.001169,0.001629,0.001851,0.003000,0.002366,0.002802,0.002766,0.008992,0.007323,0.005872,0.003119,0.003614,0.007325,0.003449,0.000777,0.002868,0.005962,0.008615,0.002353,0.000605,0.000074,0.002718,0.003421,0.006662,0.003511,0.001753,0.000332,0.001067,0.000795,0.005204,0.003591,0.002458,0.000841,0.001590,0.000774,0.003579,0.004905,0.001722,0.001244,0.000550,-0.001288,0.006081,0.002341,0.000194,0.004265,0.007966,0.001534,0.000498,-0.000166,0.003005,0.000605,0.000639,0.001234,0.000787,0.004380,0.005002,0.026896,-0.044348,0.001802,0.001495,0.004255,0.002426,0.003420,0.006755,0.002497,0.002507,0.008523,0.009297,0.003691,0.003025,0.004811,0.006608,0.002247,0.001046,0.002584,0.008921,0.008812,0.004141,0.001059,0.001773,0.002553,0.003288,0.004664,0.002361,0.001335,0.000969,0.001286,0.002292,0.009463,0.009401,0.004105,0.000043,0.001551,0.001104,0.004687,0.009891,0.003020,0.001657,0.000828,-0.005332,0.003707,0.002861,0.001195,0.002178,0.005815,0.000369,0.000503,0.000316,0.002401,0.000486,0.000555,0.001089,0.000590,0.001643,0.004647,0.026566,-0.027786,0.003917,0.001639,0.004006,0.000717,0.002450,0.006132,0.000353,0.000824,0.011588,0.009857,0.003025,0.000960,0.001550,0.006950,0.002453,0.000385,-0.000453,0.001748,0.001454,0.003262,0.000823,0.001767,0.001986,0.000169,-0.000167,-0.001188,0.000805,0.000611,0.001500,0.003516,0.008538,0.008643,0.003863,0.000799,0.000884,0.001630,0.004703,0.008943,0.003456,0.000851,0.000350,-0.004983,0.002820,0.002735,0.002292,0.002569,0.004717,0.000383,0.000879,0.000734,0.001154,-0.000221,0.000198,0.000548,-0.000088,0.002221,0.003703,0.022316,-0.040766,0.004563,0.001800,0.001699,0.001473,0.002178,0.003535,0.002519,0.003538,0.010679,0.010576,0.006849,0.002565,0.004583,0.010406,0.003369,0.001678,0.003065,0.006774,0.011028,0.003140,0.001132,0.001779,0.003775,0.004153,0.007740,0.003711,0.001455,0.000912,0.001942,0.001791,0.005134,0.006414,0.003115,0.000685,0.001535,0.001533,0.001787,0.004549,0.001921,0.001523,0.000737,0.005130,0.004912,0.001989,0.000484,0.004357,0.006828,0.002411,0.000896,0.000445,0.003885,0.000854,0.001041,0.001388,0.000770,0.003645,0.005882,0.039509,-0.052196,0.003359,0.002561,0.004138,0.002905,0.003229,0.005693,0.002864,0.002576,0.008338,0.009910,0.005222,0.002946,0.006551,0.009749,0.002819,0.001575,0.002992,0.011355,0.013680,0.004411,0.001224,0.002385,0.002958,0.003083,0.006637,0.003180,0.001179,0.001072,0.001889,0.003879,0.009464,0.013041,0.004613,0.000960,0.002457,0.002576,0.006526,0.010441,0.003101,0.001914,0.001419,0.001922,0.006264,0.003260,0.001203,0.003286,0.002920,0.002323,0.000979,0.000526,0.002597,0.000697,0.001191,0.000842,0.000647,0.002934,0.007104,0.041991,-0.032915,0.004196,0.002163,0.003995,0.001745,0.002983,0.004377,0.001375,0.001122,0.008505,0.007945,0.003248,0.001761,0.003421,0.009662,0.002486,0.001044,0.000524,0.005790,0.005160,0.003410,0.000677,0.002282,0.001873,0.001181,0.001780,0.001152,0.000836,0.000425,0.002004,0.004023,0.008398,0.008665,0.003366,0.001264,0.001799,0.003760,0.008665,0.012849,0.003691,0.001706,0.000765,0.003800,0.008535,0.003853,0.001526,0.003677,0.002080,0.002603,0.000957,0.000430,0.000741,0.000825,0.001038,0.000423,0.000252,0.003192,0.005190,0.030814,-0.030007,0.002869,0.001704,0.001276,0.000928,0.001706,0.002436,0.002777,0.003427,0.012611,0.009530,0.005243,0.002151,0.003518,0.012456,0.003438,0.002213,0.003033,0.004911,0.007794,0.004123,0.001313,0.002236,0.003394,0.004177,0.006520,0.002579,0.002045,0.001216,0.002276,0.002075,0.002135,0.005832,0.003354,0.000454,0.001603,0.001965,0.001546,0.002840,0.001427,0.001137,0.000766,0.006913,0.005812,0.001514,0.000534,0.002867,0.005312,0.002200,0.000617,0.000508,0.003937,0.001407,0.000744,0.001949,0.000643,0.003312,0.004667,0.034266,-0.039338,0.003651,0.002652,0.002270,0.002131,0.003298,0.004614,0.002135,0.002372,0.015404,0.010693,0.003796,0.001538,0.005099,0.013178,0.003455,0.002169,0.002826,0.007899,0.010081,0.004257,0.001171,0.002958,0.002890,0.003807,0.005674,0.002355,0.001402,0.000769,0.002787,0.002758,0.004163,0.009907,0.004810,0.001538,0.002843,0.002549,0.004735,0.005677,0.002523,0.001352,0.001237,0.005636,0.007133,0.002786,0.000709,0.003115,0.003745,0.001892,0.000740,0.000496,0.002371,0.000909,0.001430,0.001419,0.000452,0.003121,0.005849,0.039043,-0.026914,0.003679,0.002260,0.001815,0.001606,0.002773,0.003581,0.001028,0.001646,0.014852,0.007992,0.002113,0.001371,0.003342,0.010716,0.002740,0.001578,0.001083,0.003446,0.004112,0.002546,0.000903,0.002394,0.001913,0.001458,0.002106,0.000992,0.000787,0.000439,0.002056,0.002301,0.004700,0.005832,0.003324,0.001427,0.001862,0.003342,0.006987,0.009107,0.002902,0.002031,0.000799,0.004611,0.007232,0.003675,0.001096,0.003481,0.002667,0.001358,0.000608,0.000442,0.000814,0.000720,0.001323,0.000532,0.000329,0.002409,0.004543,0.029184,};
