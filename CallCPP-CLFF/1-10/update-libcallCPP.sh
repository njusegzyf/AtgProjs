#!/bin/bash
user=zy
CallCPP_path=/home/$user/workspace2/CallCPP-CLFF/'1-10'

cd "/home/zy/workspace2/CallCPP-CLFF/1-10"
g++ -I/usr/lib/java/jdk1.8.0_11/include -I/usr/lib/java/jdk1.8.0_11/include/linux -fPIC -shared -o libcallCPP.so CallCPP.cpp recipes/*.cpp
echo "* compile libcallCPP.so [success]"
sudo cp libcallCPP.so /usr/lib/
echo "* update libcallCPP.so [success]"
rm libcallCPP.so
echo "* clear intermediary files [success]"
