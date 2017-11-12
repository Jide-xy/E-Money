package com.example.babajidemustapha.e_money;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashMap;

public class CardSetup extends AppCompatActivity {

    RequestQueue queue;
    Spinner spinner;
    CardStore cardStore;
    HashMap<String,String> map = new HashMap<>();
    String[] currency_names;
    Boolean loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cardStore = new CardStore(this);
        loading = false;
        String[] stringArray = getResources().getStringArray(R.array.currencies);
        currency_names = new String[stringArray.length];
        for (int i = 0; i<stringArray.length;i++){
            Currency currency = Currency.getInstance(stringArray[i]);
            currency_names[i] = currency.getDisplayName();
            map.put(currency_names[i],stringArray[i]);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = (Spinner) findViewById(R.id.cur_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,currency_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        queue = Volley.newRequestQueue(this);
    }

    public void createCard(View view) {
        if(!loading) {
            loading = true;
            final String tsym = map.get(spinner.getSelectedItem().toString());
            String query = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=" + tsym;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, query, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    loading = false;
                    if (!response.toString().toLowerCase().contains("error")) {
                        try {
                            Card card = new Card(tsym, response.getJSONObject("BTC").getDouble(tsym) + "", response.getJSONObject("ETH").getDouble(tsym) + "");
                            if (cardStore.addCard(card)) {
                                Toast.makeText(CardSetup.this, "Card added", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CardSetup.this, "Card already exists", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CardSetup.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CardSetup.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        Log.e("error", response.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading = false;
                    Toast.makeText(CardSetup.this, "Connection error", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }

    }
}
