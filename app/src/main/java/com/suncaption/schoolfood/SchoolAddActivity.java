package com.suncaption.schoolfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.suncaption.schoolfood.api.School;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SchoolAddActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SchoolAddRecyclerAdapter mAdapter;
    String school_name;
    String school_name2;
    String school_grade;
    String school_class;
    String school_type_code;
    String school_org_code;
    private GestureDetector gestureDetector;
    ArrayList<SchoolAddItem> rowsArrayList;
    SchoolAddItem selectedSchoolAddItem = null;
    Spinner schoolGradeSpinner;
    Spinner schoolClassSpinner;
    private String TAG = "ROW";
    EditText school_name_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_add);


        final List<School.Type> school_type_list = new ArrayList<>(Arrays.asList(School.Type.values()));

        List<String> school_type_list_korean = new ArrayList<String>();

        for (School.Type el : School.Type.values()) {
            Log.d("entrv", "onCreate: " + el.getId());
            school_type_list_korean.add(el.getKoreanName());
        }
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> schoolTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, school_type_list_korean);
        schoolTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner schoolTypeSpinner = (Spinner) findViewById(R.id.spinner_school_type);

        schoolTypeSpinner.setAdapter(schoolTypeAdapter);


        final List<School.Region> school_region_list = new ArrayList<>(Arrays.asList(School.Region.values()));

        List<String> school_region_list_korean = new ArrayList<String>();

        for (School.Region el : School.Region.values()) {
            Log.d("entrv", "onCreate: " + el.getUrl());
            school_region_list_korean.add(el.getKoreanName());
        }
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> schoolRegionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, school_region_list_korean);
        schoolRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner schoolRegionSpinner = (Spinner) findViewById(R.id.spinner_school_region);

        schoolRegionSpinner.setAdapter(schoolRegionAdapter);


         school_name_edittext = findViewById(R.id.txt_school_name);



        Button button_school_class_add = findViewById(R.id.button_school_class_add);
        button_school_class_add.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View view) {
                                                           /*SharedPreferences preferences3 = getApplicationContext()
                                                                   .getSharedPreferences("mySchoolList", Activity.MODE_PRIVATE);
                                                           SharedPreferences.Editor editor2 = preferences3.edit();
                                                           editor2.clear().commit();*/
                                                           if (selectedSchoolAddItem == null) {
                                                               Toast.makeText(SchoolAddActivity.this,
                                                                       "먼저 학교를 선택 해 주세요", Toast.LENGTH_SHORT).show();
                                                               return;
                                                           }
                                                           school_name2 = selectedSchoolAddItem.getKraOrgNm();

                                                           school_grade = schoolGradeSpinner.getSelectedItem().toString();

                                                           school_class = schoolClassSpinner.getSelectedItem().toString();

                                                           school_type_code = selectedSchoolAddItem.getSchoolType();
                                                           school_org_code = selectedSchoolAddItem.getSchoolCode();

                                                           List<String> oldMySchoolList = new ArrayList<String>();
                                                           int oldMyCnt = 0;
                                                           SharedPreferences preferences = getApplicationContext()
                                                                   .getSharedPreferences("mySchoolList", Activity.MODE_PRIVATE);
                                                           Map<String, ?> memoryMap = preferences.getAll();
                                                           for (Map.Entry<String,?> entry : memoryMap.entrySet()){
                                                               Log.d("entrv", "SharedPreferences: " + entry.getValue().toString());
                                                               oldMySchoolList.add(entry.getValue().toString());
                                                               oldMyCnt++;
                                                           }


                                                           SharedPreferences preferences2 = getApplicationContext()
                                                                   .getSharedPreferences("mySchoolList", Activity.MODE_PRIVATE);
                                                           SharedPreferences.Editor editor = preferences2.edit();
                                                           editor.clear().commit();

                                                           String add_school_text_all =
                                                                   school_name2 + "," + school_grade + "," + school_class
                                                                           + "," + school_type_code +"," + school_org_code;
                                                           oldMySchoolList.add(add_school_text_all);
                                                           int k = 0;
                                                           for(String s: oldMySchoolList) {
                                                               editor.putString(TAG + k, s);
                                                               k++;
                                                           }
                                                           editor.commit();

                                                           selectedSchoolAddItem = null;
                                                           rowsArrayList.clear();
                                                           mAdapter.notifyDataSetChanged();
                                                           Toast.makeText(SchoolAddActivity.this,
                                                                   "등록되었습니다.", Toast.LENGTH_SHORT).show();

                                                       }
                                                   }
        );

        List<String> school_grade_list_korean = new ArrayList<String>();

        for (int i = 1; i < 11; i++) {

            school_grade_list_korean.add(String.valueOf(i));
        }
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> schoolGradeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, school_grade_list_korean);
        schoolGradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolGradeSpinner = (Spinner) findViewById(R.id.spinner_school_grade);

        schoolGradeSpinner.setAdapter(schoolGradeAdapter);

        List<String> school_class_list_korean = new ArrayList<String>();

        for (int i = 1; i < 11; i++) {

            school_class_list_korean.add(String.valueOf(i));
        }
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> schoolClassAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, school_class_list_korean);
        schoolClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolClassSpinner = (Spinner) findViewById(R.id.spinner_school_class);

        schoolClassSpinner.setAdapter(schoolClassAdapter);

        Button button_school_find = findViewById(R.id.button_school_find);
        button_school_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected = schoolRegionSpinner.getSelectedItemPosition();
                school_name = school_name_edittext.getText().toString();
                final School.Region sR1 = School.Region.values()[selected];
                new Thread() {
                    public void run() {

                        try {

                            School.findByNameList(sR1, school_name, SchoolAddActivity.this
                                    , new School.SchoolAddListener() {
                                        @Override
                                        public void onResultAfterSelect(
                                                String kraOrgNm
                                                , String schoolType
                                                , String schoolCode, String zipAdres) {
                                            rowsArrayList.clear();
                                            selectedSchoolAddItem = null;
                                            selectedSchoolAddItem = new SchoolAddItem(kraOrgNm, schoolType, schoolCode, zipAdres);
                                            rowsArrayList.add(selectedSchoolAddItem);
                                            SchoolAddActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            });


                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
                String aa = "1";
                //Toast.makeText(SchoolAddActivity.this, "" + selected
                //        , Toast.LENGTH_SHORT).show();
                //String text = spinner.getSelectedItem().toString();
            }
        });

        rowsArrayList = new ArrayList<>();
        initScrollListener();
        gestureDetector = new GestureDetector(getApplicationContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    //누르고 뗄 때 한번만 인식하도록 하기위해서
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
    }



    private void initScrollListener() {
        recyclerView = findViewById(R.id.school_add_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),
                        new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        mAdapter = new SchoolAddRecyclerAdapter(rowsArrayList, Glide.with(this));
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
                        SchoolAddItem currentItemSchoolAdd = rowsArrayList.get(currentPosition);
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
}
