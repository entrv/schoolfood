package com.suncaption.schoolfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import com.suncaption.schoolfood.api.SchoolException;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static String getContentFromUrl(URL url) throws SchoolException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder buffer = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine);
            }
            reader.close();
            return buffer.toString();

        } catch (IOException e) {
            throw new SchoolException("교육청 서버에 접속하지 못하였습니다.");
        }
    }

    public String dayKorean(int day){
        String day_kr = "";
        switch(day){
            case 0: day_kr = "일요일"; break;
            case 1: day_kr = "월요일"; break;
            case 2: day_kr = "화요일"; break;
            case 3: day_kr = "수요일"; break;
            case 4: day_kr = "목요일"; break;
            case 5: day_kr = "금요일"; break;
            case 6: day_kr = "토요일"; break;
        }
        return day_kr;
    }

    public String result(int class_int, JSONArray result_tt,JSONArray result_teacher
            ,JSONArray result_subject, int day ) {
        String result = "";
        try {
            JSONArray class_info_array = (JSONArray) result_tt.get(day);
            String class_info = (String) class_info_array.get(class_int).toString();
            String dummy = "";
            String subject = class_info.substring( class_info.length() -2, class_info.length());
            int subject_int = Integer.parseInt(subject);
            String subject_final = (String) result_subject.get(subject_int);
            String teacher = "";
            /*if (class_info.length() == 4){
                 teacher = class_info.substring(0, class_info.length() - 2);
            } else if (class_info.length() == 3){

            }*/
            teacher = class_info.substring(0, class_info.length() - 2);
            int teacher_int  = Integer.parseInt(teacher);
            String teacher_final = (String) result_teacher.get(teacher_int);


        /*[$class];
        $subject = substr($class_info, -2, 2);
        $subject = (int)$subject;
        $subject_final = $result_subject[$subject];
        $teacher = substr($class_info, -4, -2);
        $teacher = (int)$teacher;
        $teacher_final = $result_teacher[$teacher];*/
             result = subject_final + '(' + teacher_final + ')';
            if (result.equals("(  *)")) {
                return "";
            } else if (result.equals("()")) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);

        new Thread() {

            public void run() {
                try {
                    String schoolName = "교하고";
                    int result_code = 0;
                    int gradeNumber = 2;
                    int classNumber = 6;
                    String resultType= "date";
                    String ttDate = "2019-09-30";
                    int day = 0;
                    DateTime dateTimeTarget = null;
                    String tt_code = "";

                    if (schoolName.length() < 3) {
                        //schoolName is too short
                    } else if (schoolName.length() > 100) {
                        //schoolName is to long
                    }

                    if (gradeNumber == 0) {
//gradeNumber field is empty
                    } else if (classNumber == 0) {
//classNumber field is empty
                    }

                    if (gradeNumber > 99) {
//gradeNumber is too long
                    } else if (classNumber > 999) {
                        //classNumber is too long
                    }
                    TimeZone seoul = TimeZone.getTimeZone("Asia/Seoul");
                    DateTimeZone.setDefault(DateTimeZone.forID("Asia/Seoul"));
                    DateTime dateTime = new DateTime();
                    long timestamp = dateTime.getMillis();

                    if (resultType.equals("today")) {
                        day = dateTime.getDayOfWeek(); // ISO 8601 standard says Monday is 1.
                    } else if (resultType.equals("tomorrow")) {
                        day = dateTime.plusDays(1).getDayOfWeek();
                    } else if (resultType.equals("week")) {
                        day = 0;
                    } else if (resultType.equals("date")) {
                        if (!ttDate.equals("")) {
                            dateTimeTarget = new DateTime(ttDate);
                            day = dateTimeTarget.getDayOfWeek();
                        } else {
                            //ttDate field is empty;
                            return;
                        }
                    } else if (resultType.equals("")) {
                        //resultType field is empty
                        return;
                    } else {
                        //resultType field is empty
                        return;
                    }




                    String url_code = "http://comci.kr:4081/98372?92744l" +
                            URLEncoder.encode(schoolName, "euc_kr");
                    String content = getContentFromUrl(new URL(url_code.toString()));
                    JSONObject jsonObject = new JSONObject(content);
                    JSONArray items  = (JSONArray) jsonObject.getJSONArray("학교검색");
                    JSONArray items2  = (JSONArray) items.get(0);
                    result_code  = (int) items2.get(3);






                    if (result_code == 0) {
                        Toast.makeText(MainActivity.this, "학교검색 결과가 없습니다."
                                , Toast.LENGTH_SHORT).show();
                        return;
                    }





                    if (dateTimeTarget != null ) {
                        DateTime dateTimeDay = new DateTime();
                        dateTimeDay = dateTimeDay.withDayOfWeek(DateTimeConstants.SUNDAY);
                        long dateTimeDayMillis = dateTimeDay.getMillis() /  1000L;
                        long dateTargetDayMillis = dateTimeTarget.getMillis() /  1000L;
                        if (dateTargetDayMillis > dateTimeDayMillis) {
                            tt_code =  "34739_" + result_code + "_0_2";
                        } else {
                            tt_code =  "34739_" + result_code + "_0_2";
                        }
                    } else {
                        tt_code =  "34739_" + result_code + "_0_2";
                    }

                    if (tt_code.equals("")) {
                        //ttcode is empty
                        return;
                    }

                    String ttcode_base64 = Base64.encodeToString(tt_code.getBytes(),0 ).trim();
                    String url_tt = "http://comci.kr:4081/98372?" + ttcode_base64;

                    content = getContentFromUrl(new URL(url_tt.toString()));
                    jsonObject = new JSONObject(content);
                    JSONArray jsonArray2 = jsonObject.getJSONArray("자료14");
                    JSONArray jsonArray3 = (JSONArray) jsonArray2.get(gradeNumber);
                    JSONArray result_tt = (JSONArray) jsonArray3.get(classNumber);
                    JSONArray result_teacher = (JSONArray)  jsonObject.getJSONArray("자료46");
                    JSONArray result_subject = (JSONArray)  jsonObject.getJSONArray("긴자료92");
                    StringBuilder timetable = new StringBuilder();
                    String table_date,table_day,class01,class02,class03,class04
                            ,class05,class06,class07,class08,class09,class10;
                    if (resultType.equals("date")) {


                        table_date = dateTimeTarget.toString("yyyy.MM.dd");
                        table_day = dayKorean(dateTimeTarget.getDayOfWeek());
                        class01 = result(1, result_tt, result_teacher, result_subject, day);
                        class02 = result(2, result_tt, result_teacher, result_subject, day);
                        class03 = result(3, result_tt, result_teacher, result_subject, day);
                        class04 = result(4, result_tt, result_teacher, result_subject, day);
                        class05 = result(5, result_tt, result_teacher, result_subject, day);
                        class06 = result(6, result_tt, result_teacher, result_subject, day);
                        class07 = result(7, result_tt, result_teacher, result_subject, day);
                        class08 = result(8, result_tt, result_teacher, result_subject, day);
                        class09 = result(9, result_tt, result_teacher, result_subject, day);
                        class10 = result(10, result_tt, result_teacher, result_subject,day);
                        timetable.append(table_date + "\n");
                        timetable.append(table_day + "\n");
                        timetable.append(class01 + "\n");
                        timetable.append(class02 + "\n");
                        timetable.append(class03 + "\n");
                        timetable.append(class04 + "\n");
                        timetable.append(class05 + "\n");
                        timetable.append(class06 + "\n");
                        timetable.append(class07 + "\n");
                        timetable.append(class08 + "\n");
                        timetable.append(class09 + "\n");
                        timetable.append(class10 + "\n");
                    }
                    if (resultType.equals("today")) {
                        DateTime dummyDate = null;
                        DateTime dateTable = new DateTime();

                        dummyDate = dateTable.plusHours(0);

                        table_date = dummyDate.toString("yyyy.MM.dd");
                        table_day = dayKorean(dummyDate.getDayOfWeek());
                        class01 = result(1, result_tt, result_teacher, result_subject, day);
                        class02 = result(2, result_tt, result_teacher, result_subject, day);
                        class03 = result(3, result_tt, result_teacher, result_subject, day);
                        class04 = result(4, result_tt, result_teacher, result_subject, day);
                        class05 = result(5, result_tt, result_teacher, result_subject, day);
                        class06 = result(6, result_tt, result_teacher, result_subject, day);
                        class07 = result(7, result_tt, result_teacher, result_subject, day);
                        class08 = result(8, result_tt, result_teacher, result_subject, day);
                        class09 = result(9, result_tt, result_teacher, result_subject, day);
                        class10 = result(10, result_tt, result_teacher, result_subject,day);
                        timetable.append(table_date + "\n");
                        timetable.append(table_day + "\n");
                        timetable.append(class01 + "\n");
                        timetable.append(class02 + "\n");
                        timetable.append(class03 + "\n");
                        timetable.append(class04 + "\n");
                        timetable.append(class05 + "\n");
                        timetable.append(class06 + "\n");
                        timetable.append(class07 + "\n");
                        timetable.append(class08 + "\n");
                        timetable.append(class09 + "\n");
                        timetable.append(class10 + "\n");
                    }
                    if (resultType.equals("tomorrow")) {
                        DateTime dummyDate = null;
                        DateTime dateTable = new DateTime();

                            dummyDate = dateTable.plusHours(24);

                        table_date = dummyDate.toString("yyyy.MM.dd");
                        table_day = dayKorean(dummyDate.getDayOfWeek());
                        class01 = result(1, result_tt, result_teacher, result_subject, day);
                        class02 = result(2, result_tt, result_teacher, result_subject, day);
                        class03 = result(3, result_tt, result_teacher, result_subject, day);
                        class04 = result(4, result_tt, result_teacher, result_subject, day);
                        class05 = result(5, result_tt, result_teacher, result_subject, day);
                        class06 = result(6, result_tt, result_teacher, result_subject, day);
                        class07 = result(7, result_tt, result_teacher, result_subject, day);
                        class08 = result(8, result_tt, result_teacher, result_subject, day);
                        class09 = result(9, result_tt, result_teacher, result_subject, day);
                        class10 = result(10, result_tt, result_teacher, result_subject,day);
                        timetable.append(table_date + "\n");
                        timetable.append(table_day + "\n");
                        timetable.append(class01 + "\n");
                        timetable.append(class02 + "\n");
                        timetable.append(class03 + "\n");
                        timetable.append(class04 + "\n");
                        timetable.append(class05 + "\n");
                        timetable.append(class06 + "\n");
                        timetable.append(class07 + "\n");
                        timetable.append(class08 + "\n");
                        timetable.append(class09 + "\n");
                        timetable.append(class10 + "\n");
                    }
                    if (resultType.equals("week")) {
                       day = dateTime.getDayOfWeek();

                       int time_init = 0 ;
                       int time_2 = 0 ;
                       int time_3 = 0 ;
                       int time_4 = 0 ;
                       int time_5 = 0 ;

                       if (day == 0) {
                            time_init = 24 ;
                            time_2 = 48 ;
                            time_3 = 72 ;
                            time_4 = 96 ;
                            time_5 = 120 ;
                       } else if (day == 1 ) {
                           time_init = 0 ;
                           time_2 = 24 ;
                           time_3 = 48 ;
                           time_4 = 72 ;
                           time_5 = 96 ;
                       } else if (day == 2 ) {
                           time_init = -24 ;
                           time_2 = 0 ;
                           time_3 = 24 ;
                           time_4 = 48 ;
                           time_5 = 72 ;
                       }else if (day == 3 ) {
                           time_init = -48 ;
                           time_2 = -24 ;
                           time_3 = 0 ;
                           time_4 = 24 ;
                           time_5 = 48 ;
                       }else if (day == 4 ) {
                           time_init =-72 ;
                           time_2 = -48 ;
                           time_3 = -24 ;
                           time_4 = 0 ;
                           time_5 = 24 ;
                       }else if (day == 5 ) {
                           time_init = -96 ;
                           time_2 = -72 ;
                           time_3 = -48 ;
                           time_4 = -24 ;
                           time_5 = 0 ;
                       }else if (day == 6 ) {
                           time_init = -120 ;
                           time_2 = -96 ;
                           time_3 = -72 ;
                           time_4 = -48 ;
                           time_5 = -24 ;
                       }

                       DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy.MM.dd");

                       timetable.append(0 +"~");
                       DateTime dummyDate = null;
                       DateTime dateTable = new DateTime();
                       if (time_init > 0 ) {
                           dummyDate = dateTable.plusHours(time_init);
                       } else {
                           dummyDate = dateTable.minusHours(time_init*-1);
                       }

                        table_date = dummyDate.toString("yyyy.MM.dd");
                        table_day = dayKorean(dummyDate.getDayOfWeek());
                        class01 = result(1, result_tt, result_teacher, result_subject, 1);
                        class02 = result(2, result_tt, result_teacher, result_subject, 1);
                        class03 = result(3, result_tt, result_teacher, result_subject, 1);
                        class04 = result(4, result_tt, result_teacher, result_subject, 1);
                        class05 = result(5, result_tt, result_teacher, result_subject, 1);
                        class06 = result(6, result_tt, result_teacher, result_subject, 1);
                        class07 = result(7, result_tt, result_teacher, result_subject, 1);
                        class08 = result(8, result_tt, result_teacher, result_subject, 1);
                        class09 = result(9, result_tt, result_teacher, result_subject, 1);
                        class10 = result(10, result_tt, result_teacher, result_subject, 1);
                       timetable.append(table_date + "\n");
                       timetable.append(table_day + "\n");
                       timetable.append(class01 + "\n");
                       timetable.append(class02 + "\n");
                       timetable.append(class03 + "\n");
                       timetable.append(class04 + "\n");
                       timetable.append(class05 + "\n");
                       timetable.append(class06 + "\n");
                       timetable.append(class07 + "\n");
                       timetable.append(class08 + "\n");
                       timetable.append(class09 + "\n");
                       timetable.append(class10 + "\n");
                       timetable.append(class10 + "||");

                       timetable.append(1 +"~");
                       dateTable = new DateTime();
                       if (time_2 > 0 ) {
                           dummyDate = dateTable.plusHours(time_2);
                       } else {
                           dummyDate = dateTable.minusHours(time_2);
                       }
                       table_date = dummyDate.toString("yyyy.MM.dd");
                       table_day = dayKorean(dummyDate.getDayOfWeek());
                        class01 = result(1, result_tt, result_teacher, result_subject, 2);
                        class02 = result(2, result_tt, result_teacher, result_subject, 2);
                        class03 = result(3, result_tt, result_teacher, result_subject, 2);
                        class04 = result(4, result_tt, result_teacher, result_subject, 2);
                        class05 = result(5, result_tt, result_teacher, result_subject, 2);
                        class06 = result(6, result_tt, result_teacher, result_subject, 2);
                        class07 = result(7, result_tt, result_teacher, result_subject, 2);
                        class08 = result(8, result_tt, result_teacher, result_subject, 2);
                        class09 = result(0, result_tt, result_teacher, result_subject, 2);
                        class10 = result(10, result_tt, result_teacher, result_subject, 2);
                       timetable.append(table_date + "\n");
                       timetable.append(table_day + "\n");
                       timetable.append(class01 + "\n");
                       timetable.append(class02 + "\n");
                       timetable.append(class03 + "\n");
                       timetable.append(class04 + "\n");
                       timetable.append(class05 + "\n");
                       timetable.append(class06 + "\n");
                       timetable.append(class07 + "\n");
                       timetable.append(class08 + "\n");
                       timetable.append(class09 + "\n");
                       timetable.append(class10 + "\n");
                       timetable.append(class10 + "||");


                       timetable.append(2 +"~");
                       dateTable = new DateTime();
                       if (time_3 > 0 ) {
                           dummyDate = dateTable.plusHours(time_3);
                       } else {
                           dummyDate = dateTable.minusHours(time_3);
                       }
                       table_date = dummyDate.toString("yyyy.MM.dd");
                       table_day = dayKorean(dummyDate.getDayOfWeek());
                       class01 = result(1, result_tt, result_teacher, result_subject, 3);
                       class02 = result(2, result_tt, result_teacher, result_subject, 3);
                       class03 = result(3, result_tt, result_teacher, result_subject, 3);
                       class04 = result(4, result_tt, result_teacher, result_subject, 3);
                       class05 = result(5, result_tt, result_teacher, result_subject, 3);
                       class06 = result(6, result_tt, result_teacher, result_subject, 3);
                       class07 = result(7, result_tt, result_teacher, result_subject, 3);
                       class08 = result(8, result_tt, result_teacher, result_subject, 3);
                       class09 = result(9, result_tt, result_teacher, result_subject, 3);
                       class10 = result(10, result_tt, result_teacher, result_subject, 3);
                       timetable.append(table_date + "\n");
                       timetable.append(table_day + "\n");
                       timetable.append(class01 + "\n");
                       timetable.append(class02 + "\n");
                       timetable.append(class03 + "\n");
                       timetable.append(class04 + "\n");
                       timetable.append(class05 + "\n");
                       timetable.append(class06 + "\n");
                       timetable.append(class07 + "\n");
                       timetable.append(class08 + "\n");
                       timetable.append(class09 + "\n");
                       timetable.append(class10 + "\n");
                       timetable.append(class10 + "||");

                       timetable.append(3 +"~");
                       dateTable = new DateTime();
                       if (time_4 > 0 ) {
                           dummyDate = dateTable.plusHours(time_4);
                       } else {
                           dummyDate = dateTable.minusHours(time_4);
                       }
                       table_date = dummyDate.toString("yyyy.MM.dd");
                       table_day = dayKorean(dummyDate.getDayOfWeek());
                       class01 = result(1, result_tt, result_teacher, result_subject, 4);
                       class02 = result(2, result_tt, result_teacher, result_subject, 4);
                       class03 = result(3, result_tt, result_teacher, result_subject, 4);
                       class04 = result(4, result_tt, result_teacher, result_subject, 4);
                       class05 = result(5, result_tt, result_teacher, result_subject, 4);
                       class06 = result(6, result_tt, result_teacher, result_subject, 4);
                       class07 = result(7, result_tt, result_teacher, result_subject, 4);
                       class08 = result(8, result_tt, result_teacher, result_subject, 4);
                       class09 = result(9, result_tt, result_teacher, result_subject, 4);
                       class10 = result(10, result_tt, result_teacher, result_subject, 4);
                       timetable.append(table_date + "\n");
                       timetable.append(table_day + "\n");
                       timetable.append(class01 + "\n");
                       timetable.append(class02 + "\n");
                       timetable.append(class03 + "\n");
                       timetable.append(class04 + "\n");
                       timetable.append(class05 + "\n");
                       timetable.append(class06 + "\n");
                       timetable.append(class07 + "\n");
                       timetable.append(class08 + "\n");
                       timetable.append(class09 + "\n");
                       timetable.append(class10 + "\n");
                       timetable.append(class10 + "||");


                       timetable.append(4 +"~");
                       dateTable = new DateTime();
                       if (time_5 > 0 ) {
                           dummyDate = dateTable.plusHours(time_5);
                       } else {
                           dummyDate = dateTable.minusHours(time_5);
                       }
                       table_date = dummyDate.toString("yyyy.MM.dd");
                       table_day = dayKorean(dummyDate.getDayOfWeek());
                       class01 = result(1, result_tt, result_teacher, result_subject, 5);
                       class02 = result(2, result_tt, result_teacher, result_subject, 5);
                       class03 = result(3, result_tt, result_teacher, result_subject, 5);
                       class04 = result(4, result_tt, result_teacher, result_subject, 5);
                       class05 = result(5, result_tt, result_teacher, result_subject, 5);
                       class06 = result(6, result_tt, result_teacher, result_subject, 5);
                       class07 = result(7, result_tt, result_teacher, result_subject, 5);
                       class08 = result(8, result_tt, result_teacher, result_subject, 5);
                       class09 = result(9, result_tt, result_teacher, result_subject, 5);
                       class10 = result(10, result_tt, result_teacher, result_subject, 5);
                       timetable.append(table_date + "\n");
                       timetable.append(table_day + "\n");
                       timetable.append(class01 + "\n");
                       timetable.append(class02 + "\n");
                       timetable.append(class03 + "\n");
                       timetable.append(class04 + "\n");
                       timetable.append(class05 + "\n");
                       timetable.append(class06 + "\n");
                       timetable.append(class07 + "\n");
                       timetable.append(class08 + "\n");
                       timetable.append(class09 + "\n");
                       timetable.append(class10 + "\n");
                       timetable.append(class10 + "||");



                   }



                    Log.d("entrv", "run: " + timetable);
                    //$result_code = $array_code['학교검색'][0][3];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        /*new Thread() {
            public void run() {
                try {
                    School school = School.find(School.Region.SEOUL, "선덕고등학교");

                    List<SchoolMenu> menu = school.getMonthlyMenu(2019, 8);
                    List<SchoolSchedule> schedule = school.getMonthlySchedule(2019, 7);

                    // 2019년 1월 2일 점심 급식 식단표
                    System.out.println(menu.get(1).lunch);

                    // 2018년 12월 5일 학사일정
                    System.out.println(schedule.get(4));

                } catch (SchoolException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/




    }
}
