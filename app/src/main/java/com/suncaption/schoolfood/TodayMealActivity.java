package com.suncaption.schoolfood;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.squareup.timessquare.CalendarPickerView;
import com.suncaption.schoolfood.api.School;
import com.suncaption.schoolfood.api.SchoolMenu;
import com.suncaption.schoolfood.api.SchoolSchedule;
import com.suncaption.schoolfood.api.SchoolTimetable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;

public class TodayMealActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener {

    private ProgressDialog progDialog;
    private CalendarPickerView calendar;
    private TextView schedule;
    Context context;
    //MealWindowFragment mealfrag;
    //MealWindowFragment mealfrag_allergy;

    private int t_year, t_month, t_day;

    ArrayList<mealData> mealDatas;
    //    private final String MSG_PARSE_ERROR = "무엇인가 잘못되었다. 콜미";
    private final String MSG_NETWORK_SUCKS = "네트워크 상태가 올바르지 않습니다.";
    private final String MSG_FIRST_NETWORK_SUCKS = "네트워크 상태가 올바르지 않습니다. 처음 접근은 네트워크가 필요합니다.";
    private final String MSG_ALREADY_EXISTS = "더 이상 갱신할 정보가 없습니다.";
    private final String MSG_UPDATE = "최신 정보를 갱신하였습니다.";
    private final String MSG_NO_MEAL = "해당 날짜의 급식 정보가 없습니다.";
    private final String MSG_NO_SHOW = "보여줄 급식정보가 없습니다.";

    SchoolListItem itemGetInfo;


    //Todo DB에서 날짜 부르는 범위 정하기(-1 0 +1),
    // 예외 또 있음!
    // 1월 급식 있고 2월 급식 없고 3월 급식있고 지금 2월 중순이면 어떻게 될까요오?


    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;

    public TodayMealActivity(Context context) {
        this.context = context;
    }

    public TodayMealActivity() {

    }

    public Context getContext() {
        return context;
    }

    boolean isOnRange(int date) {
        int y = intToYear(date);
        int m = intToMonth(date);
        if (m == 1) {
            if (y == t_year - 1 && m == 12) {
                return true;
            }
            if (y == t_year && m == 2) {
                return true;
            }
        } else if (m == 12) {
            if (y == t_year + 1 && m == 1) {
                return true;
            }
            if (y == t_year && m == 11) {
                return true;
            }

        } else {
            if (y == t_year) {
                if (m - t_month == 1 || m - t_month == -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public int intToYear(int d) {
        return d / 10000;
    }

    public int intToMonth(int d) {
        return (d % 10000) / 100;
    }

    public int intToDay(int d) {
        return d % 100;
    }

    public int ymdToInt(int y, int m, int d) {
        return y * 10000 + m * 100 + d;
    }

    public int dateToInt(Date date) {
        return ymdToInt(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = TodayMealActivity.this;
        setContentView(R.layout.activity_today_meal);

        itemGetInfo = (SchoolListItem) getIntent().getParcelableExtra("schoolInfo");
        Log.e("todaymeal [ITEM] 이름 :", itemGetInfo.getKraOrgNm());

        //mealfrag = new MealWindowFragment();
        //mealfrag_allergy = new MealWindowFragment();

        //mealfrag_allergy.setAllergyflag();

        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.

/*
            mealfrag.setparam("","",MSG_NO_MEAL);
            mealfrag_allergy.setparam("","",MSG_NO_MEAL);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.mealwindow_container, mealfrag, null)
                    .commit();
            */
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        getFragmentManager().addOnBackStackChangedListener(this);

        schedule = (TextView) findViewById(R.id.schedule_today_meal);
        schedule.setVisibility(View.GONE);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        CardView cardview = (CardView) findViewById(R.id.mealwindow_container);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

        progDialog = new ProgressDialog(this);

        // Get a support ActionBar corresponding to this toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_today_meal);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        GregorianCalendar gcalendar = new GregorianCalendar();
        t_year = gcalendar.get(Calendar.YEAR); // index 1
        t_month = gcalendar.get(Calendar.MONTH) + 1; // index 1
        t_day = gcalendar.get(Calendar.DAY_OF_MONTH); // index 1

        drawNullCalendar();
        Log.i("info", "오늘의 날짜 : " + t_year + "/" + t_month + "/" + t_day);


        //급식 선택 달력에 표시되는 날짜의 범위를 설정한다.
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                // public void setText(date)참조;
                setCellInfo(date, mealDatas);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        mealDatas = new ArrayList<>();


        drawCalendar();

    }

    public void gotoParse(final int y, final int m, final ArrayList<mealData> mealDatas) {

        new Thread() {
            public void run() {
                try {
                    String school_name = itemGetInfo.getKraOrgNm();
                    String schoolType = itemGetInfo.getSchoolType();
                    School.Region region = School.Region.values()[Integer.parseInt(schoolType) - 1];
                    School school = School.find(region, school_name);

                    List<SchoolMenu> menu = school.getMonthlyMenu(y, m);
                    //List<SchoolSchedule> schedule = school.getMonthlySchedule(2019, 7);


                    //List<SchoolTimetable> timetable = school.getMonthlyTimetable("북중"
                    //       , 0,2,6,"week","");

                    //주단위로 가져 온다. //화요일
                    //Log.d("entrv", "run: " + timetable.get(1));

                    // 2019년 1월 2일 점심 급식 식단표
                    System.out.println(menu.get(1).lunch);

                    // 2018년 12월 5일 학사일정
                    //System.out.println(schedule.get(4));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void drawNullCalendar() {
        // 달력에 그릴 범위를 계산해온다.
        Log.i("info", "그린다 달력!");

        int startYear, startMonth, startDay;
        int endYear, endMonth, endDay;


        //급식 캘린더의 시작 날짜를
        // 초기화한다.
        DateTimeZone.setDefault(DateTimeZone.forID("Asia/Seoul"));
        DateTime dateTime = new DateTime();

        startYear = dateTime.getYear();
        startMonth = dateTime.getMonthOfYear();
        startDay = dateTime.getDayOfMonth();

        Calendar calStart = Calendar.getInstance(Locale.KOREAN);
        calStart.set(startYear, --startMonth, startDay);

        //급식 캘린더의 끝 날짜를 초기화한다.
        dateTime = dateTime.plusMonths(1);
        endYear = dateTime.getYear();
        endMonth = dateTime.getMonthOfYear();
        endDay = dateTime.getDayOfMonth();

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(endYear, --endMonth, endDay);
        calStart.getTime();
        calEnd.getTime();

        //급식 선택 달력에 표시되는 날짜의 범위를 설정한다.
        calendar.init(calStart.getTime(), calEnd.getTime());


        gotoParse(startYear, --startMonth,  mealDatas);
    }

    private void drawCalendar() {
        // 달력에 그릴 범위를 계산해온다.
        Log.i("info", "그린다 달력!");

        int startYear, startMonth, startDay;
        int endYear, endMonth, endDay;

        /*DBdate dbdate = new DBdate();

        dbdate.analyze();
        //급식 캘린더의 시작 날짜를
        // 초기화한다.
        startYear = intToYear(dbdate.getStartDate());
        startMonth = intToMonth(dbdate.getStartDate());
        startDay = intToDay(dbdate.getStartDate());

        Calendar calStart = Calendar.getInstance();
        calStart.set(startYear, --startMonth, startDay);

        //급식 캘린더의 끝 날짜를 초기화한다.

        endYear = intToYear(dbdate.getEndDate());
        endMonth = intToMonth(dbdate.getEndDate());
        endDay = intToDay(dbdate.getEndDate());

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(endYear, --endMonth, ++endDay);

        //급식 선택 달력에 표시되는 날짜의 범위를 설정한다.
        calendar.init(calStart.getTime(), calEnd.getTime());

        getMealInfo(dbdate.getStartDate(), dbdate.getEndDate(), mealDatas);

        try {
            calendar.selectDate(new Date());
        }catch (IllegalArgumentException exception){
        }
        calendar.scrollToDate(new Date());
        setCellInfo(new Date(), mealDatas);*/
    }

    public void getMealInfo(int startDate, int endDate, ArrayList<mealData> mealDatas) {
        // mealinfo 에 데이터에 저장하고 하이라이트 할거는 하이라이트 한다.

        boolean flag = false;
        Collection<Date> highdates = new ArrayList<Date>(200); // 하이라이트 할 날짜 목록

        if (startDate == 0 && endDate == 0) {
            Log.i("info", "메인 엑티비티에서 손님이 오셨네!");
            flag = true;
        } else {
            calendar.clearHighlightedDates();
        }
        int date;


        /*DBHelper helper = new DBHelper(getContext(), DBHelper.DB_FILE_NAME, null, 1, DBHelper.TODAYMEAL_TABLE);
        Date today = new Date();

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + DBHelper.TODAYMEAL_TABLE_NAME, null);

        mealDatas.clear();


        while (cursor.moveToNext()) {
            date = cursor.getInt(1);
            if((startDate <= date && date <= endDate) || flag == false) {
//                mealinfo.setTemplate(intToYear(date)+"년 "+intToMonth(date)+"월 "+ intToDay(date)+"일\n [중식]\n!lunch!\n\n[저녁]\n!dinner!");

                Log.i("info", "호잇호잇"+date+" | "+cursor.getString(2));
                mealDatas.add(new mealData(date, cursor.getString(2), cursor.getString(3)));

                if(flag == false) {
                    Date date2 = new Date();
                    date2.setYear(intToYear(date) - 1900);
                    date2.setMonth(intToMonth(date) - 1);
                    date2.setDate(intToDay(date));

                    if (dateToInt(today) != date) {
                        highdates.add(date2); // 오늘은 하이라이트 노노해
                        Log.i("info", "highlight : " + dateToInt(date2));
                    }
                }
            }
        }


        if(flag == false) {
            calendar.highlightDates(highdates);
        }
        // 오늘 날짜를 선택한다.
        helper.close();*/

    }

    private void setCellInfo(Date date, ArrayList<mealData> mealdatas) {
        Log.i("info", dateToInt(date) + " 날짜가 선택되었당.");

        if (mShowingBack) {
            getFragmentManager().popBackStack();
        }

       /* mealfrag.setparam("","",MSG_NO_MEAL);
        mealfrag_allergy.setparam("","",MSG_NO_MEAL);

        for(int i=0;i<mealdatas.size();i++){
            if(mealdatas.get(i).getDate() == dateToInt(date)){
                String str = mealdatas.get(i).getMealData();
                Log.i("info", "선택된 날짜의 정보 : "+str);

                // 알레르기 정보를 표기 할것인가?
                // 이건 프리퍼런스에서 변수를 설정한다음에 그 후에 처리하기
                mealfrag.setparam(mealdatas.get(i).getLunch(), mealdatas.get(i).getDinner(), "");
                mealfrag_allergy.setparam(mealdatas.get(i).getAllergyLunch(), mealdatas.get(i).getAllergyDinner(), "");

                break;
            }
        }

        getFragmentManager().beginTransaction().
        detach(mealfrag).
                attach(mealfrag).
                commit();
*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

        // When the back stack changes, invalidate the options menu (action bar).
        //invalidateOptionsMenu();
    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.
        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.
        /*getFragmentManager()
                .beginTransaction()
                        // Replace the default fragment animations with animator resources
                        // representing rotations when switching to the back of the card, as
                        // well as animator resources representing rotations when flipping
                        // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)

                        // Replace any fragments currently in the container view with a
                        // fragment representing the next page (indicated by the
                        // just-incremented currentPage variable).
                .replace(R.id.mealwindow_container, mealfrag_allergy)
                        // Add this transaction to the back stack, allowing users to press
                        // Back to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();*/
    }

}
