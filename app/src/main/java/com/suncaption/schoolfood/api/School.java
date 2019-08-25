package com.suncaption.schoolfood.api;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.suncaption.schoolfood.MainActivity;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * NEIS API
 * 전국 교육청 소속 교육기관의 학사일정, 메뉴를 간단히 불러올 수 있습니다.
 *
 * @author HyunJun Kim
 * @version 3.1
 */
public class School {

    public enum TypeKorean {

        /* 병설유치원 */ KINDERGARTEN("병설유치원"),
        /* 초등학교 */ ELEMENTARY("초등학교"),
        /* 중학교 */ MIDDLE("중학교"),
        /* 고등학교 */ HIGH("고등학교");

        private String id;
        public String getId() {
            return id;
        }
        TypeKorean(String id) {
            this.id = id;
        }
    }
    /**
     * 불러올 교육청 소속 교육기관의 종류
     */
    public enum Type {
        /* 병설유치원 */ KINDERGARTEN("1"),
        /* 초등학교 */ ELEMENTARY("2"),
        /* 중학교 */ MIDDLE("3"),
        /* 고등학교 */ HIGH("4");

        private String id;

        Type(String id) {
            this.id = id;
        }
    }

    /**
     * 불러올 교육기관의 관할 지역 (교육청)
     */
    public enum Region {

        /* 서울 */ SEOUL("sen.go.kr"),
        /* 인천 */ INCHEON("ice.go.kr"),
        /* 부산 */ BUSAN("pen.go.kr"),
        /* 광주 */ GWANGJU("gen.go.kr"),
        /* 대전 */ DAEJEON("dje.go.kr"),
        /* 대구 */ DAEGU("dge.go.kr"),
        /* 세종 */ SEJONG("sje.go.kr"),
        /* 울산 */ ULSAN("use.go.kr"),
        /* 경기 */ GYEONGGI("goe.go.kr"),
        /* 강원 */ KANGWON("kwe.go.kr"),
        /* 충북 */ CHUNGBUK("cbe.go.kr"),
        /* 충남 */ CHUNGNAM("cne.go.kr"),
        /* 경북 */ GYEONGBUK("gbe.go.kr"),
        /* 경남 */ GYEONGNAM("gne.go.kr"),
        /* 전북 */ JEONBUK("jbe.go.kr"),
        /* 전남 */ JEONNAM("jne.go.kr"),
        /* 제주 */ JEJU("jje.go.kr");

        private String url;

        Region(String url) {
            this.url = url;
        }
    }

    private static final String MONTHLY_MENU_URL = "sts_sci_md00_001.do";
    private static final String SCHEDULE_URL = "sts_sci_sf01_001.do";
    private static final String SCHOOL_CODE_URL = "spr_ccm_cm01_100.do";

    /**
     * 교육기관의 종류
     */
    public Type type;

    /**
     * 교육기관 관할 지역
     */
    public Region region;

    /**
     * 교육기관 고유 코드
     */
    public String code;

    /**
     * 캐시용 해시맵
     */
    private Map<Integer, List<SchoolMenu>> monthlyMenuCache;
    private Map<Integer, List<SchoolSchedule>> monthlyScheduleCache;

    /**
     * 불러올 학교 정보를 설정합니다.
     *
     * @param type   교육기관의 종류입니다. (School.Type 에서 병설유치원, 초등학교, 중학교, 고등학교 중 선택)
     * @param region 관할 교육청의 위치입니다. (School.Region 에서 선택)
     * @param code   교육기관의 고유 코드입니다.
     */
    public School(Type type, Region region, String code) {
        this.type = type;
        this.region = region;
        this.code = code;
        this.monthlyMenuCache = new HashMap<>();
        this.monthlyScheduleCache = new HashMap<>();
    }

    public School() {

    }

    /**
     * 월간 급식 메뉴를 불러옵니다.
     *
     * @param year  해당 년도를 yyyy 형식으로 입력. (ex. 2016)
     * @param month 해당 월을 m 형식으로 입력. (ex. 3, 12)
     * @return 각 일자별 급식메뉴 리스트
     */
    public List<SchoolMenu> getMonthlyMenu(int year, int month) throws SchoolException {

        int cacheKey = year * 12 + month;

        if (this.monthlyMenuCache.containsKey(cacheKey))
            return this.monthlyMenuCache.get(cacheKey);

        StringBuilder targetUrl = new StringBuilder();

        targetUrl.append("https://stu.").append(region.url).append("/").append(MONTHLY_MENU_URL);
        targetUrl.append("?schulCode=").append(code);
        targetUrl.append("&schulCrseScCode=").append(type.id);
        targetUrl.append("&schulKndScCode=0").append(type.id);
        targetUrl.append("&schYm=").append(year).append(String.format("%02d", month));
        targetUrl.append("&");

        try {
            String content = getContentFromUrl(new URL(targetUrl.toString()));
            content = Utils.before(Utils.after(content, "<tbody>"), "</tbody>");

            // 리턴하기 전 캐시에 데이터를 저장합니다.
            List<SchoolMenu> monthlyMenu = SchoolMenuParser.parse(content);
            this.monthlyMenuCache.put(cacheKey, monthlyMenu);

            return monthlyMenu;

        } catch (MalformedURLException e) {
            throw new SchoolException("교육청 접속 주소가 올바르지 않습니다.");
        }
    }

    /**
     * 월간 학사 일정을 불러옵니다.
     *
     * @param year  해당 년도를 yyyy 형식으로 입력. (ex. 2016)
     * @param month 해당 월을 m 형식으로 입력. (ex. 3, 12)
     * @return 각 일자별 학사일정 리스트
     */
    public List<SchoolSchedule> getMonthlySchedule(int year, int month) throws SchoolException {

        int cacheKey = year * 12 + month;

        if (this.monthlyScheduleCache.containsKey(cacheKey))
            return this.monthlyScheduleCache.get(cacheKey);

        StringBuilder targetUrl = new StringBuilder();

        targetUrl.append("https://stu.").append(region.url).append("/").append(SCHEDULE_URL);
        targetUrl.append("?schulCode=").append(code);
        targetUrl.append("&schulCrseScCode=").append(type.id);
        targetUrl.append("&schulKndScCode=0").append(type.id);
        targetUrl.append("&ay=").append(year);
        targetUrl.append("&mm=").append(String.format("%02d", month));
        targetUrl.append("&");

        try {
            String content = getContentFromUrl(new URL(targetUrl.toString()));
            content = Utils.before(Utils.after(content, "<tbody>"), "</tbody>");

            List<SchoolSchedule> monthlySchedule = SchoolScheduleParser.parse(content);
            this.monthlyScheduleCache.put(cacheKey, monthlySchedule);

            return monthlySchedule;

        } catch (MalformedURLException e) {
            throw new SchoolException("교육청 접속 주소가 올바르지 않습니다.");
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
    public List<SchoolTimetable> getMonthlyTimetable(
            String schoolName
            ,int result_code
            , int gradeNumber
            , int classNumber
            , String resultType /* today, date, tomorrow, week */
            , String ttDate

    ) throws SchoolException , Exception {
            /*schoolName = "교하고";
            result_code = 0;
            gradeNumber = 2;
            classNumber = 6;
            resultType= "week";
            ttDate = "2019-09-30";*/
            int day = 0;
            DateTime dateTimeTarget = null;
            String tt_code = "";

            if (schoolName.length() < 2) {
                //schoolName is too short
                throw new SchoolException("학교 이름이 너무 짧습니다.");
            } else if (schoolName.length() > 100) {
                //schoolName is to long
                throw new SchoolException("학교 이름이 너무 깁니다.");
            }

            if (gradeNumber == 0) {
//gradeNumber field is empty
                throw new SchoolException("학년 이 없습니다..");
            } else if (classNumber == 0) {
//classNumber field is empty
                throw new SchoolException("반 이 없습니다..");
            }

            if (gradeNumber > 99) {
//gradeNumber is too long
                throw new SchoolException("학년 이 너무 깁니다..");
            } else if (classNumber > 999) {
                //classNumber is too long
                throw new SchoolException("반 너무 깁니다..");
            }

            DateTimeZone.setDefault(DateTimeZone.forID("Asia/Seoul"));
            DateTime dateTime = new DateTime();


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
                    throw new SchoolException("날짜로 검색 인데 날짜가 없습니다..");

                }
            } else if (resultType.equals("")) {
                //resultType field is empty
                throw new SchoolException("검색결과 항목이 없습니다..");

            } else {
                //resultType field is empty
                throw new SchoolException("검색결과 항목이 없습니다..");

            }




            String url_code = "http://comci.kr:4081/98372?92744l" +
                    URLEncoder.encode(schoolName, "euc_kr");
            String content = getContentFromUrl(new URL(url_code.toString()));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray items  = (JSONArray) jsonObject.getJSONArray("학교검색");
            JSONArray items2  = (JSONArray) items.get(0);
            result_code  = (int) items2.get(3);



            if (result_code == 0) {
                throw new SchoolException("학교 검색 결과가 없습니다..");
            }





            if (dateTimeTarget != null ) {
                DateTime dateTimeDay = new DateTime();
                dateTimeDay = dateTimeDay.withDayOfWeek(DateTimeConstants.SUNDAY);
                long dateTimeDayMillis = dateTimeDay.getMillis() /  1000L;
                long dateTargetDayMillis = dateTimeTarget.getMillis() /  1000L;
                if (dateTargetDayMillis > dateTimeDayMillis) {
                    tt_code =  "34739_" + result_code + "_0_2";  //다음주
                } else {
                    tt_code =  "34739_" + result_code + "_0_1";  //이번주
                }
            } else {
                tt_code =  "34739_" + result_code + "_0_1";
            }

            if (tt_code.equals("")) {
                //ttcode is empty
                throw new SchoolException("학교 검색 코드 결과가 없습니다..");

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
        List<SchoolTimetable> monthlyTimetable = new ArrayList<>();

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
                int time_6 = 0;

                if (day == 0) {
                    time_init = 24 ;
                    time_2 = 48 ;
                    time_3 = 72 ;
                    time_4 = 96 ;
                    time_5 = 120 ;
                    time_6 = 144 ;
                } else if (day == 1 ) {
                    time_init = 0 ;
                    time_2 = 24 ;
                    time_3 = 48 ;
                    time_4 = 72 ;
                    time_5 = 96 ;
                    time_6 = 120 ;
                } else if (day == 2 ) {
                    time_init = -24 ;
                    time_2 = 0 ;
                    time_3 = 24 ;
                    time_4 = 48 ;
                    time_5 = 72 ;
                    time_6 = 96 ;
                }else if (day == 3 ) {
                    time_init = -48 ;
                    time_2 = -24 ;
                    time_3 = 0 ;
                    time_4 = 24 ;
                    time_5 = 48 ;
                    time_6 = 72 ;
                }else if (day == 4 ) {
                    time_init =-72 ;
                    time_2 = -48 ;
                    time_3 = -24 ;
                    time_4 = 0 ;
                    time_5 = 24 ;
                    time_6 = 48 ;
                }else if (day == 5 ) {
                    time_init = -96 ;
                    time_2 = -72 ;
                    time_3 = -48 ;
                    time_4 = -24 ;
                    time_5 = 0 ;
                    time_6 = 24 ;
                }else if (day == 6 ) {
                    time_init = -120 ;
                    time_2 = -96 ;
                    time_3 = -72 ;
                    time_4 = -48 ;
                    time_5 = -24 ;
                    time_6 = 0 ;
                }

                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy.MM.dd");

                timetable.append(0 +"~");
                DateTime dummyDate = null;
                DateTime dateTable = new DateTime();
                if (time_init >= 0 ) {
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
                dummyDate = null;
                dateTable = new DateTime();
                if (time_2 >= 0 ) {
                    dummyDate = dateTable.plusHours(time_2);
                } else {
                    dummyDate = dateTable.minusHours(time_2 * -1);
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
                dummyDate = null;
                dateTable = new DateTime();
                if (time_3 >= 0 ) {
                    dummyDate = dateTable.plusHours(time_3);
                } else {
                    dummyDate = dateTable.minusHours(time_3 * -1);
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
                dummyDate = null;
                dateTable = new DateTime();
                if (time_4 >= 0 ) {
                    dummyDate = dateTable.plusHours(time_4);
                } else {
                    dummyDate = dateTable.minusHours(time_4*-1);
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
                dummyDate = null;
                dateTable = new DateTime();
                if (time_5 >= 0 ) {
                    dummyDate = dateTable.plusHours(time_5);
                } else {
                    dummyDate = dateTable.minusHours(time_5*-1);
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

                timetable.append(5 +"~");
                dummyDate = null;
                dateTable = new DateTime();
                if (time_6 >= 0 ) {
                    dummyDate = dateTable.plusHours(time_6);
                } else {
                    dummyDate = dateTable.minusHours(time_6*-1);
                }
                table_date = dummyDate.toString("yyyy.MM.dd");
                table_day = dayKorean(dummyDate.getDayOfWeek());
                class01 = result(1, result_tt, result_teacher, result_subject, 6);
                class02 = result(2, result_tt, result_teacher, result_subject, 6);
                class03 = result(3, result_tt, result_teacher, result_subject, 6);
                class04 = result(4, result_tt, result_teacher, result_subject, 6);
                class05 = result(5, result_tt, result_teacher, result_subject, 6);
                class06 = result(6, result_tt, result_teacher, result_subject, 6);
                class07 = result(7, result_tt, result_teacher, result_subject, 6);
                class08 = result(8, result_tt, result_teacher, result_subject, 6);
                class09 = result(9, result_tt, result_teacher, result_subject, 6);
                class10 = result(10, result_tt, result_teacher, result_subject,6);
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



                String[] timetable_array = timetable.toString().split("\\|\\|");
                for(String a : timetable_array) {
                    Log.d("entrv", "getMonthlyTimetable: " + a);
                    String[] kkk = a.split("~");

                    monthlyTimetable.add(new SchoolTimetable(kkk[1].toString()));


                }
            }



            Log.d("entrv", "run: " + monthlyTimetable);
            //$result_code = $array_code['학교검색'][0][3];


        //List<SchoolTimetable> monthlyTimetable = SchoolTimetableParser.parse(timetable);
        return monthlyTimetable;

    }

    public void clearCache() {
        this.monthlyScheduleCache.clear();
        this.monthlyMenuCache.clear();
    }

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

    public static School find(Region region, String name) throws SchoolException {
        try {
            StringBuilder targetUrl = new StringBuilder();

            targetUrl.append("https://par.").append(region.url).append("/").append(SCHOOL_CODE_URL);
            targetUrl.append("?kraOrgNm=").append(URLEncoder.encode(name, "utf-8"));
            targetUrl.append("&");

            // 원본 데이터는 JSON형식으로 이루어져 있습니다.
            String content = getContentFromUrl(new URL(targetUrl.toString()));
            content = Utils.before(Utils.after(content, "orgCode"), "schulCrseScCodeNm");

            // 기관 종류와 코드를 구합니다.
            String schoolCode = content.substring(3, 13);
            String schoolType = Utils.before(Utils.after(content,"schulCrseScCode\":\""), "\"");

            return new School(Type.values()[Integer.parseInt(schoolType) - 1], region, schoolCode);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SchoolException("학교를 찾을 수 없습니다.");
        }
    }
}
