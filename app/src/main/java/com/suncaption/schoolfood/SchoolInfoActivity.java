package com.suncaption.schoolfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class SchoolInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_info);

        SchoolListItem item = (SchoolListItem) getIntent().getParcelableExtra("schoolInfo");
        Log.e("[ITEM] 이름 :", item.getKraOrgNm());

    }
}
