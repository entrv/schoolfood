package com.suncaption.schoolfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.suncaption.schoolfood.api.School;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchoolAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_add);



        final List<School.Type> school_type_list = new ArrayList<>(Arrays.asList(School.Type.values()));

        List<String> school_type_list_korean =  new ArrayList<String>();

        for (School.TypeKorean el :  School.TypeKorean.values()) {
            Log.d("entrv", "onCreate: " + el.getId());
            school_type_list_korean.add(el.getId());
        }
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> schoolTypeAdapter = new ArrayAdapter<>(
                this,android.R.layout.simple_spinner_dropdown_item,school_type_list_korean);
        schoolTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner schoolTypeSpinner = (Spinner)findViewById(R.id.spinner_school_type);

        schoolTypeSpinner.setAdapter(schoolTypeAdapter);


        Button button_school_find = findViewById(R.id.button_school_find);
        button_school_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected = ((School.Type)schoolTypeSpinner.getSelectedItem()).name();
                String aa = "1";
                //String text = spinner.getSelectedItem().toString();
            }
        });



    }
}
