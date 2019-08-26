package com.suncaption.schoolfood;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class SchoolInfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Context mContext;
    SchoolListItem itemGetInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_info);

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


        CardView cardView = (CardView) findViewById(R.id.card_today_meal);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TodayMealActivity.class);
                intent.putExtra("schoolInfo", itemGetInfo);
                //startActivity(intent);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                // the context of the activity
                SchoolInfoActivity.this,
                        // For each shared element, add to this method a new Pair item,
                        // which contains the reference of the view we are transitioning *from*,
                        // and the value of the transitionName attribute
                        new Pair<View, String>(v.findViewById(R.id.card_today_meal),
                                getString(R.string.transition_name_today_meal_card)),
                        new Pair<View, String>(v.findViewById(R.id.text_today_meal),
                                getString(R.string.transition_name_today_meal_text))
                );
                //오늘의 급식으로 진입한다.
                ActivityCompat.startActivity(SchoolInfoActivity.this, intent, options.toBundle());
            }
        });

       /* cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TodayMealActivity.class);
                intent.putExtra("schoolInfo", itemGetInfo);
                startActivity(intent);
                *//*ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        // the context of the activity
                        SchoolInfoActivity.this,
                        // For each shared element, add to this method a new Pair item,
                        // which contains the reference of the view we are transitioning *from*,
                        // and the value of the transitionName attribute
                        new Pair<View, String>(v.findViewById(R.id.card_today_meal),
                                getString(R.string.transition_name_today_meal_card)),
                        new Pair<View, String>(v.findViewById(R.id.text_today_meal),
                                getString(R.string.transition_name_today_meal_text))
                );
                //오늘의 급식으로 진입한다.
                ActivityCompat.startActivity(SchoolInfoActivity.this, intent, options.toBundle());*//*
            }
        });*/

        CardView cardView2 = (CardView) findViewById(R.id.card_schedule);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(mContext, SCalendar.class);


                //학사 일정으로 진입한다.
                //startActivity(intent);
            }
        });

        CardView cardView3 = (CardView) findViewById(R.id.card_time_table);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(mContext, TimeTable.class);
                //오늘의 시간표로 진입한다.
                //startActivity(intent);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        itemGetInfo = (SchoolListItem) getIntent().getParcelableExtra("schoolInfo");
        Log.e("[ITEM] 이름 :", itemGetInfo.getKraOrgNm());

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
