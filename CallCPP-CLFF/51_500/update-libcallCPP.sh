#!/bin/bash
user=cx
bin_path=/home/$user/Workspace/'Plugin Projects'/cn.nju.seg.atg/bin
CallCPP_path=/home/$user/Desktop/test

cd "$bin_path"
javah -jni cn.nju.seg.atg.callCPP.CallCPP
echo "* compile jni header file [success]"
mv cn_nju_seg_atg_callCPP_CallCPP.h $CallCPP_path/cn_nju_seg_atg_callCPP_CallCPP.h
echo "* update jni header file [success]"
cd "/home/cx/Desktop/CallCPP"
g++ -I/user/lib/jvm/java-7-openjdk-i386/include -I/user/lib/jvm/java-7-openjdk-i386/incldue/linux -I./header -fPIC -shared -o libcallCPP.so CallCPP.cpp
echo "* compile libcallCPP.so [success]"
sudo cp libcallCPP.so /usr/lib/
echo "* update libcallCPP.so [success]"
rm cn_nju_seg_atg_callCPP_CallCPP.h libcallCPP.so
echo "* clear intermediary files [success]"
