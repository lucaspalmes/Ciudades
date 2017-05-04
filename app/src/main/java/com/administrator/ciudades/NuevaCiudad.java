package com.administrator.ciudades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;

public class NuevaCiudad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_ciudad);
    }

    public void AddCity(View v) {

        EditText city = (EditText) findViewById(R.id.txtCity);
        String txtCity = city.getText().toString();

        if(txtCity.length() != 0 || txtCity.length() !=0) {

            Intent data = new Intent();
            data.putExtra("NewCity", txtCity);

            setResult(RESULT_OK, data);
            finish();

        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_addCity), Toast.LENGTH_LONG).show();
        }
    }
}
