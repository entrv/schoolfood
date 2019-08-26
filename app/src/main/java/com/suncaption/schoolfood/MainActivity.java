package com.suncaption.schoolfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.suncaption.schoolfood.api.School;
import com.suncaption.schoolfood.api.SchoolException;
import com.suncaption.schoolfood.api.SchoolMenu;
import com.suncaption.schoolfood.api.SchoolSchedule;
import com.suncaption.schoolfood.api.SchoolTimetable;


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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context mContext;
    SchoolListRecyclerAdapter mAdapter;
    RecyclerView recyclerView;
    private GestureDetector gestureDetector;
    ArrayList<SchoolListItem> rowsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new Thread() {

            public void run() {
                /*try {
                    School school = new School();

                    List<SchoolTimetable> timetable = school.getMonthlyTimetable(""
                            , 0,0,0,"week","");

                    //주단위로 가져 온다. //화요일
                    Log.d("entrv", "run: " + timetable.get(1));

                } catch (Exception e) {
                                             e.printStackTrace();
                }*/
            }
        }.start();
        new Thread() {
            public void run() {
                try {
                    School school = School.find(School.Region.SEOUL, "북중");

                    List<SchoolMenu> menu = school.getMonthlyMenu(2019, 8);
                    List<SchoolSchedule> schedule = school.getMonthlySchedule(2019, 7);



                    List<SchoolTimetable> timetable = school.getMonthlyTimetable("북중"
                            , 0,2,6,"week","");

                    //주단위로 가져 온다. //화요일
                    Log.d("entrv", "run: " + timetable.get(1));

                    // 2019년 1월 2일 점심 급식 식단표
                    System.out.println(menu.get(1).lunch);

                    // 2018년 12월 5일 학사일정
                    System.out.println(schedule.get(4));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();




        gestureDetector = new GestureDetector(getApplicationContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    //누르고 뗄 때 한번만 인식하도록 하기위해서
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        rowsArrayList = new ArrayList<>();
        initScrollListener();
    }

    private void initScrollListener() {
        recyclerView = findViewById(R.id.school_main_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),
                        new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        mAdapter = new SchoolListRecyclerAdapter(rowsArrayList, Glide.with(this));
        RecyclerView.OnItemTouchListener onItemTouchListener = new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //손으로 터치한 곳의 좌표를 토대로 해당 Item의 View를 가져옴
                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                //터치한 곳의 View가 RecyclerView 안의 아이템이고 그 아이템의 View가 null이 아니라
                //정확한 Item의 View를 가져왔고, gestureDetector에서 한번만 누르면 true를 넘기게 구현했으니
                //한번만 눌려서 그 값이 true가 넘어왔다면
                if (childView != null && gestureDetector.onTouchEvent(e)) {

                    try {
                        //현재 터치된 곳의 position을 가져오고
                        int currentPosition = rv.getChildAdapterPosition(childView);

                        //해당 위치의 Data를 가져옴
                        SchoolListItem currentItemSchoolList = rowsArrayList.get(currentPosition);

                        Intent intent = new Intent(getApplicationContext(), SchoolInfoActivity.class);
                        intent.putExtra("schoolInfo", currentItemSchoolList);
                        startActivity(intent);

                        //getMovieUrl(currentItemMovie.getTexthref(), childView, currentItemMovie.getTextMovieTitle());
                        //Toast.makeText(MainActivity.this, "현재 터치한 Item의 Student Name은 " + currentItemStudent.getTextMovieTitle(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        recyclerView.addOnItemTouchListener(onItemTouchListener);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


            }
        });




    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("entrv", "onResume: AddFragment");
        rowsArrayList.clear();
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("mySchoolList", Activity.MODE_PRIVATE);
        Map<String, ?> memoryMap = preferences.getAll();

        for (Map.Entry<String,?> entry : memoryMap.entrySet()){
            String[] school_item = entry.getValue().toString().split(",");
            //연북중학교,1,1,3,B100001946
            //String kraOrgNm, String schoolType, String schoolCode
            //            , String schoolGrade , String schoolClass
            SchoolListItem selectedSchoolListItem = new SchoolListItem(
                    school_item[0], school_item[3], school_item[4], school_item[1],school_item[2]);
            rowsArrayList.add(selectedSchoolListItem);
            mAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.shcool_home) {

        } else if (id == R.id.school_add) {
            item.setCheckable(false);
            Intent intent = new Intent(mContext, SchoolAddActivity.class);
            //설정으로 진입한다.
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
