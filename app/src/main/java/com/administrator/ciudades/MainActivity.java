package com.administrator.ciudades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.app.ListActivity;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import android.util.Log;

public class MainActivity extends AppCompatActivity  {

    public ListView lvCiudades;
    private List<Ciudad> CiudadesList = new ArrayList<>();
    private List<String> lkCities = new LinkedList<String>();
    public ArrayAdapter<String> adapter;
    public int count = 0;
    private static final String cCITY = "City_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LLenarListView();

        lvCiudades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String nombreCiudad = (String) lvCiudades.getItemAtPosition(position);
                String urlApi = getResources().getString(R.string.openweathermap_API);
                String api = urlApi.replace("CITY_NAME", nombreCiudad);
                new ReadWeather().execute(api);
            }
        });
    }

    public class ReadWeather extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.alert_wait) , getResources().getString(R.string.alert_download_weather), true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream inputStream = null;
            String result = "";
            try {
                inputStream = new URL(urls[0]).openStream();

                if(inputStream != null) {
                    BufferedReader buffer = new BufferedReader( new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = buffer.readLine()) != null)
                        result += line;

                    inputStream.close();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.alert_download_weatherError), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                // ERROR;
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }


        @Override
        protected void onPostExecute(String texto) {
            dialog.cancel();

            try {
                JSONObject json = new JSONObject(texto);

                JSONObject jsonMain = json.getJSONObject("main");

                double temperaturaK = jsonMain.getDouble("temp");

                float temperaturaKFloat = ((int)temperaturaK*100)/100;
                float temperaturaC = (float) (temperaturaKFloat-273.15);

                String message = getResources().getString(R.string.alert_download_weatherMessage) + " " + String.valueOf(temperaturaC) + " C";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.alert_download_weatherError), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addCity:
                Intent i = new Intent(this, NuevaCiudad.class);
                startActivityForResult(i, 200);
                return true;
            case R.id.clearData:
                LimpiarCiudades();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ((requestCode == 200) && (resultCode == RESULT_OK)) {
            super.onActivityResult(requestCode, resultCode, data);
            Bundle bundle = data.getExtras();
            String nombreCiudad = bundle.getString("NewCity");
            GuardarCiudad(nombreCiudad);
        }
    }

    private void LLenarListView(){
        lvCiudades = (ListView) findViewById(R.id.lvCiudades);
        CiudadesList = TraerCiudades();

        for(Ciudad ciu : CiudadesList) {
            lkCities.add(count, ciu.getNombre());
            count++;
        }

        adapter = new ArrayAdapter<>(this, R.layout.row, R.id.label, lkCities);
        lvCiudades.setAdapter(adapter);
    }

    private void LimpiarCiudades(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        CiudadesList.clear();
        lkCities.clear();
        adapter.notifyDataSetChanged();

        Toast.makeText(this, getResources().getString(R.string.alert_deleteCities), Toast.LENGTH_LONG).show();
    }

    private void GuardarCiudad(String NewCity){
        Ciudad ciudad = AgregarCiudad(NewCity);

        lkCities.add(count, ciudad.getNombre());
        adapter.notifyDataSetChanged();

        Toast.makeText(this, getResources().getString(R.string.alert_addCityOK), Toast.LENGTH_LONG).show();
    }

    private List<Ciudad> TraerCiudades(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        List<Ciudad> list = new ArrayList<>();

        for(int i = 0; i <= pref.getAll().size(); i++) {
            String city = pref.getString(cCITY + i, "");
            if(city != null && !city.equals("")){
                String city_name = pref.getString(cCITY + i, "");
                Ciudad ciudad = new Ciudad(cCITY + count, city_name);
                list.add(ciudad);
            }
        }

       return list;
    }

    private Ciudad AgregarCiudad(String NewCity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(cCITY + count, NewCity);
        editor.commit();

        Ciudad ciudad = new Ciudad(cCITY + count, NewCity);
        return  ciudad;
    }
}
