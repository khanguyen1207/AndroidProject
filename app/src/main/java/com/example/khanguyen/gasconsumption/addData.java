package com.example.khanguyen.gasconsumption;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class addData extends AppCompatActivity implements View.OnClickListener {

    // Initialize activity's components
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");


    EditText distanceEdt;
    EditText litterEdt;
    Button addBtn;
    Button backBtn;
    TextView warningDis ;
    TextView warningLit;


    //Get database
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //Get latest odo
    //SharedPreferences preferencesLastestOdo = getSharedPreferences(Constants.LATEST_ODM, Activity.MODE_PRIVATE);
    //SharedPreferences.Editor editorLastestOdo = preferencesLastestOdo.edit();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        distanceEdt = (EditText) findViewById(R.id.distance);
        litterEdt = (EditText) findViewById(R.id.volume);
        addBtn = (Button) findViewById(R.id.addBtn);
        backBtn = (Button) findViewById(R.id.backBtn);
        warningDis = (TextView) findViewById(R.id.warnOdo);
        warningLit = (TextView) findViewById(R.id.warnLitter);
        preferences = getSharedPreferences(Constants.MAIN_DATA, Activity.MODE_PRIVATE);
        editor = preferences.edit();
        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn :
                finish();
                break;
            case R.id.addBtn :
                // Get data from edittext
                String distance = distanceEdt.getText().toString();
                String litter = litterEdt.getText().toString();

                if (!DOUBLE_PATTERN.matcher(distance).matches()) {
                    warningDis.setText("You did not enter valid distance");
                    break;
                }

                if (!DOUBLE_PATTERN.matcher(litter).matches()) {
                    warningLit.setText("You did not enter valid litter");
                    break;
                }

                double highestOdo = preferences.getFloat(Constants.MAX_ODM_KEY, 0);
                if (Double.parseDouble(distance) > highestOdo) {

                    editor.putFloat(Constants.MAX_ODM_KEY,(float) highestOdo);

                    warningLit.setText("");
                    warningDis.setText("");

                    //Date object
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
                    String dateText = dateFormat.format(date);

                    String dataToSave = preferences.getString(Constants.MAIN_DATA_KEY, "") + dateText + " " + distance + " " + litter + " ";
//                    int duration = Toast.LENGTH_LONG;
//
//                    Toast toast = Toast.makeText(getApplicationContext(), dataToSave, duration);
//                    toast.show();
                    //Write data
                    editor.putString(Constants.MAIN_DATA_KEY, dataToSave);
                    editor.commit();
                    finish();
                }
                else {
                    warningDis.setText("Your new odometer mus be higher than the old one");
                }





        }
    }
}
