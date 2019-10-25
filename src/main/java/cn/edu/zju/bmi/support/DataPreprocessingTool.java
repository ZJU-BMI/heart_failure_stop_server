package cn.edu.zju.bmi.support;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class DataPreprocessingTool {
    // To Be Done
    private static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
    private static Date timeFormatTrans(String time){
        Boolean translateFlag = false;
        Date dateTime = null;
        try {
            if (time.length() < 5) {
                System.out.println(0);
            } else if (time.length() < 12) {
                dateTime = simpleDateFormat1.parse(time);
            } else {
                dateTime = simpleDateFormat1.parse(time);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    public static void main(String... args){
        String time1 = "2006/1/1";
        String time2 = "2006/01/1";
        String time3 = "2016/01/01";
        String time4 = "2016/1/01";
        String time5 = "2016/1/1 12:12:12";
        String time6 = "2016/01/1 12:12:12";
        String time7 = "2016/01/01 12:12:12";
        String time8 = "2016/1/01 12:12:12";

        String[] timeList = new String[8];
        timeList[0] = time1;
        timeList[1] = time2;
        timeList[2] = time3;
        timeList[3] = time4;
        timeList[4] = time5;
        timeList[5] = time6;
        timeList[6] = time7;
        timeList[7] = time8;

        for (String s : timeList) {
            Date time = timeFormatTrans(s);
            System.out.println(time.toString());
        }
    }
}
