package cn.nju.seg.atg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatisticsScript {
  public static void main(String[] args) {
    // String path = "/home/zy/Desktop/2014_3_11/Example Program/result";
    // File content = new File(path);
    // File[] files = content.listFiles();
    // for(int i = 0;i<files.length;i++){
    // if(files[i].getName().contains("result")){
    // double[] coverage = new double[10];
    // double[] time = new double[10];
    // try {
    // BufferedReader reader = new BufferedReader(new FileReader(files[i]));
    // String line;
    // int j = 0;
    // while((line=reader.readLine())!=null && j < 10){
    // String[] strs = line.split("\t");
    // time[j] = Double.parseDouble(strs[4]);
    // coverage[j] = Double.parseDouble(strs[1]);
    // j++;
    // }
    // reader.close();
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // System.out.println(files[i].getName()+"\tmean:"+MathFunc.getAverage(coverage)+"\tstdevpa:"+MathFunc.getSTDEVPA(coverage)
    // +"\tmean:"+MathFunc.getAverage(time)+"\tstdevpa:"+MathFunc.getSTDEVPA(time));
    // }
    // }

    // int programNum = 65;
    // double[][] coral = new double[programNum][7];
    double[] coverage = new double[7];
    for (int i = 0; i < 7; i++)
      coverage[i] = 0;
    String path = "/home/zy/Desktop/CLFF-ATG(FSE)/N(4)/coral";
    File content = new File(path);
    File[] files = content.listFiles();

    List<String> lists = new ArrayList<String>();

    for (int i = 0; i < files.length; i++) {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(files[i]));
        String line;
        // int j = 0;
        while ((line = reader.readLine()) != null) {
          // if(line.contains("total time")){
          // String[] strs = line.split(" ");
          // coral[i][j] = Double.parseDouble(strs[2]);
          // j++;
          // }
          if (line.contains("coverage result:	[")) {
            String result = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            result = result.replace(",", "");
            String[] rarray = result.split(" ");
            // for(int r = 0;r<7;r++){
            // if(rarray[r].contains("Y"))
            // coverage[r] = coverage[r]+1;
            // }
            int count = 0;
            for (int r = 0; r < 7; r++) {
              if (rarray[r].contains("N")) {
                count++;
              }
            }
            if (count == 7) {
              lists.add(files[i].getName());
            }
          }

        }
        reader.close();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    for (String file : lists)
      System.out.println(file);

    // for(int j = 0;j<7;j++){
    // double time = 0;
    // for(int i = 0;i<programNum;i++){
    // time += coral[i][j];
    // }
    // System.out.println("run"+(j+1)+":"+time);
    // }
    //
    // for(int j = 0;j<7;j++){
    // System.out.println("coverage"+(j+1)+":"+coverage[j]/programNum);
    // }
  }
}
