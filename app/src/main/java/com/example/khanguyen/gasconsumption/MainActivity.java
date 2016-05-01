package com.example.khanguyen.gasconsumption;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Activities to open
    Intent addDataAct;
    Intent showLogAct;

    //Declare buttons holders for onClickListener
    Button addDataBtn;
    Button showLogBtn;

    //MainActivity's Componets
    TextView lastestTV;
    TextView lowestTV;
    TextView highestTV;
    TextView averageTV;
    TextView monthDistanceTV;

    String displayUnit;

    /*
     * Data handling
     */

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init

        /*
         * Buttons and Activities initialization
         */

        addDataBtn = (Button) findViewById(R.id.addData);
        showLogBtn = (Button) findViewById(R.id.showLog);
        addDataAct = new Intent(this, addData.class);
        showLogAct = new Intent(this, showLog.class);


        /*
         * Initialize onClick Event
         */

        addDataBtn.setOnClickListener(this);
        showLogBtn.setOnClickListener(this);

        /*
         *Activiti's components initialization
         */

        lastestTV = (TextView) findViewById(R.id.latest);
        highestTV = (TextView) findViewById(R.id.highest);
        lowestTV = (TextView) findViewById(R.id.lowest);
        averageTV = (TextView) findViewById(R.id.average);
        monthDistanceTV = (TextView) findViewById(R.id.distanceMonth);
        displayUnit = Constants.LIT_PER_KM;

        /*
         * Data handling
         */
        //create file on first launch
        //load file on second launch and soforth
        preferences = getSharedPreferences(Constants.MAIN_DATA, Activity.MODE_PRIVATE);
        editor = preferences.edit();

//        editor.clear();
//        editor.putFloat(Constants.MAX_ODM_KEY, 0);
//        editor.putString(Constants.MAIN_DATA_KEY, "");
//        editor.commit();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        dataHandler();
    }

    //Upon finish the Add_Data activity, we have to recalculate all values

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Recalculate result;
        dataHandler();
    }

    /*
     * Recalculate data upon finish adding one
     */

    private void dataHandler() {
        String wholeData = preferences.getString(Constants.MAIN_DATA_KEY, "");
        DecimalFormat round = new DecimalFormat("#.##");
        //One string rules them all
        //Divide to conquer
        /*
         * Data format : Date + black space + Distance + black space + Volumme
         */
        String[] separatedData = wholeData.split(" ");

        if (separatedData.length >= 6) {
            List<String> date = new LinkedList<String>();
            List<Double> volume = new ArrayList<>();
            List<Double> distance = new LinkedList<Double>();
            List<Double> result = new ArrayList<Double>();

            for (int i = 0; i < separatedData.length - 2; i = i + 3) {
                volume.add(Double.parseDouble(separatedData[i + 2]));
                date.add(separatedData[i]);
                distance.add(Double.parseDouble(separatedData[i + 1]));
            }
            for (int i = 0; i < distance.size() - 1; i++) {
                double value = volume.get(i + 1) / (0.01 * (distance.get(i + 1) - distance.get(i)));
                result.add(value);
            }
            String showLatest;
            // I check the unit that the user chose and take the latest input accordingly
            if (Constants.LIT_PER_KM.equals(displayUnit)) {
                showLatest = String.valueOf(round.format(result.get(result.size() - 1))) + " (l/100km)";
            } else {
                showLatest = String.valueOf(round.format(235 / result.get(result.size() - 1))) + " (mpg)";
            }

            Collections.sort(result);
            //calculate the average by calculate the sum of every element in 2 units in the result array
            double sumLPKM = 0; //sum in unit l/100km
            double sumMPG = 0;  //sum in unit mpg
            for (double e : result) {
                sumLPKM += e;
                sumMPG += 235 / e;
            }
            double average;
            double highest;
            double lowest;
            double totalDistanceMonth;
            String showHighest;
            String showAverage;
            String showLowest;
            String showDistanceMonth;

            //I check unit that the user chose and calculate the average, highest, and lowest accordingly
            if (Constants.LIT_PER_KM.equals(displayUnit)) {

                average = sumLPKM / result.size();
                //get the last element of the sorted result array which is the highest
                highest = result.get(result.size() - 1);
                //get the first element of the sorted result array which is the lowest
                lowest = result.get(0);
                // calculate the travel distance this month
                totalDistanceMonth = totalDistanceMonth(date, distance);
                showHighest = String.valueOf(round.format(highest)) + "(l/100km)";
                showAverage = String.valueOf(round.format(average)) + "(l/100km)";
                showLowest = String.valueOf(round.format(lowest)) + "(l/100km)";
                showDistanceMonth = String.valueOf(round.format(totalDistanceMonth)) + "(km)";
            } else {
                average = sumMPG / result.size();
                /* because the value in Mpg unit is in the inversed order with the l/100km so the highest
                value in l/100km value corresponds to the lowest value in mpg and vice versa
                 */
                highest = 235 / result.get(0);
                lowest = 235 / result.get(result.size() - 1);
                //calculate the travel distance this month in miles
                totalDistanceMonth = totalDistanceMonth(date, distance) * 0.621371;
                showHighest = String.valueOf(round.format(highest)) + "(mpg)";
                showAverage = String.valueOf(round.format(average)) + "(mpg)";
                showLowest = String.valueOf(round.format(lowest)) + "(mpg)";
                showDistanceMonth = String.valueOf(round.format(totalDistanceMonth)) + "(mile)";
            }
            //show all the result
            highestTV.setText("Highest: " + showHighest);
            averageTV.setText("Average: " + showAverage);
            lowestTV.setText("Lowest: " + showLowest);
            lastestTV.setText("Latest: " + showLatest);
            monthDistanceTV.setText("Travel distance in this month: " + showDistanceMonth);
        } else {
            if (separatedData.length == 3){
                double volumeTemp = Double.parseDouble(separatedData[2]);
                double distanceTemp = Double.parseDouble(separatedData[1]);
                double temp = volumeTemp*100/distanceTemp;
                String temp1 = new Double(temp).toString();
                highestTV.setText("Highest: " + temp1);
                averageTV.setText("Average: " + temp1);
                lowestTV.setText("Lowest: " + temp1);
                lastestTV.setText("Latest: " + temp1);
                monthDistanceTV.setText("Travel distance in this month: " + separatedData[1]);

            }
            else {
                highestTV.setText("Highest: N/A");
                averageTV.setText("Average: N/A");
                lowestTV.setText("Lowest: N/A");
                lastestTV.setText("Latest: N/A");
                monthDistanceTV.setText("Travel distance in this month: N/A");
            }
        }
    }



    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.galon:
                displayUnit = Constants.MPG;
                break;
            case R.id.Liter:
                displayUnit = Constants.LIT_PER_KM;
                dataHandler();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addData:
                startActivityForResult(addDataAct, 2);
                break;
            case R.id.showLog:
                startActivityForResult(showLogAct, 2);
        }
    }


    public double totalDistanceMonth(List<String> date, List<Double> distance){
        //take the current date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        Date currentDate = new Date();
        String dateText = dateFormat.format(currentDate);
        ArrayList<Double> monthDistance=new ArrayList<>();
        int postition=0;
        //add all odometer valueadded in this month by compare the current month with the month that data was added
        for(String var:date){
            if(var.substring(3,10).equals(dateText.substring(3,10))){
                monthDistance.add(distance.get(postition));
            }
            postition++;
        }
        // if there is no data added in this month, return 0
        if(monthDistance.isEmpty()){
            return 0;
        }
        Collections.sort(monthDistance);
        // calculate the travel distance by subtracting the last element of the array by the first
        return monthDistance.get(monthDistance.size()-1)-monthDistance.get(0);
    }
}
