package com.suncaption.schoolfood;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


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
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context mContext;


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
