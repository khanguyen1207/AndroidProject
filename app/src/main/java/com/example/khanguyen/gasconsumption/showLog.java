package com.example.khanguyen.gasconsumption;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class showLog extends AppCompatActivity implements View.OnClickListener{

    //Data access
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //Activity's components
    Button clearBtn;
    ListView lvLog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_log);
        preferences = getSharedPreferences(Constants.MAIN_DATA, Activity.MODE_PRIVATE);
        editor = preferences.edit();

        // Initialize component
        clearBtn = (Button) findViewById(R.id.clrBtn);
        lvLog = (ListView) findViewById(R.id.lvLog);

        //Read data
        String wholeData = preferences.getString(Constants.MAIN_DATA_KEY, "");
        String[] list=wholeData.split(" ");

        // create 4 arrays for the custom listview
        String arrayDate[]=new String[list.length/3];
        String arrayDistance[]=new String[list.length/3];
        String arrayVolume[]=new String[list.length/3];
        String arrayResult[]=new String[list.length/3];

        analyzeStringToAddInputToArrays(wholeData, arrayDate, arrayDistance, arrayVolume, arrayResult);
        CustomAdapter adapter= new CustomAdapter(this,arrayDate,arrayDistance,arrayVolume,arrayResult);
        lvLog.setAdapter(adapter);
    }




     void analyzeStringToAddInputToArrays(String store, String arrayDate[], String arrayDistance[], String arrayVolume[], String arrayResult[]){

        //split the string,add input to 3 separate arraylist
        String[] list=store.split(" ");
        int n=0;
        for(int i=0; i<list.length-2; i+=3){
            //add the \n because of the limited space for date
            arrayDate[n]=list[i].substring(0,10)+"\n"+list[i].substring(11);
            arrayDistance[n]=list[i+1];
            arrayVolume[n]=list[i+2];
            n++;
        }
        // if n(the number of times that the user added input) >2 then we calculate the gas comsumption
        if(n>=2){
            DecimalFormat df = new DecimalFormat("#.##");
            //there is no information to calculate the first rate of the first time that user added input
            arrayResult[0]="No info";
            //calculate the rate
            String var;
            for(int i=1; i<n; i++){
                var=String.valueOf(df.format(Double.parseDouble(arrayVolume[i])/(0.01*(Double.parseDouble(arrayDistance[i])-Double.parseDouble(arrayDistance[i-1])))));
                arrayResult[i]=var;

            }
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clrBtn :
                editor.clear();
                editor.putFloat(Constants.MAX_ODM_KEY, 0);
                editor.putString(Constants.MAIN_DATA_KEY, "");
                editor.commit();
//                String arrayDate[]={};
//                String arrayDistance[]={};
//                String arrayVolume[]={};
//                String arrayResult[]={};

                String array[] = {};
                CustomAdapter adapter= new CustomAdapter(this,array, array, array, array);
                lvLog.setAdapter(adapter);
                break;

        }
    }
}
