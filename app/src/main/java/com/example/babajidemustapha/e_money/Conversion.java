package com.example.babajidemustapha.e_money;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Conversion extends AppCompatActivity {

    CardStore cardStore;
    Spinner spinner;
    EditText editText;
    TextView result;
    TextView date;
    TextView cur1;
    TextView cur2;
    SharedPreferences preferences;
    Card card;
    String[] conversion_types;
    Currency currency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cardStore = new CardStore(this);
        spinner = (Spinner) findViewById(R.id.conversion_type);
        result = (TextView) findViewById(R.id.result);
        editText = (EditText) findViewById(R.id.value);
        date = (TextView) findViewById(R.id.conversion_date);
        cur1 = (TextView) findViewById(R.id.cur1);
        cur2 = (TextView) findViewById(R.id.cur2);
        preferences = getSharedPreferences("last_update", MODE_PRIVATE);
        date.setText("Conversion will be performed with Exchange rate as at "+preferences.getString("date",null));
        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("ID");
        card = cardStore.getCard(id);
        currency = Currency.getInstance(card.getCurrency_code());
        conversion_types = new String[] {currency.getDisplayName()+" - BTC",currency.getDisplayName()+" - ETH",
                "BTC - "+currency.getDisplayName(),"ETH - "+currency.getDisplayName()};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,conversion_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("dropdown changed", "we here");
                editText.setText("");
                String s = adapter.getItem(i);
                if (s.equals(conversion_types[0]) || s.equals(conversion_types[1])) {
                    cur1.setText(currency.getSymbol());
                } else if (s.equals(conversion_types[2])) {
                    cur1.setText("BTC");
                } else if (s.equals(conversion_types[3])) {
                    cur1.setText("ETH");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setAdapter(adapter);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()){
                    Log.e("text changed", "we here");
                    result.setText("");
                    cur2.setText("");

                    return;
                }
                String s = spinner.getSelectedItem().toString();
                if (s.equals(conversion_types[0])) {
                   double rootValue = Double.parseDouble(card.getBtc());
                    double valueToConvert =  Double.parseDouble(editable.toString());
                    double ans =  valueToConvert/rootValue;
                    String formatted_ans = NumberFormat.getNumberInstance(Locale.UK).format(BigDecimal.valueOf(ans).setScale(2, BigDecimal.ROUND_HALF_UP));
                    result.setText(formatted_ans);
                    cur1.setText(currency.getSymbol());
                    cur2.setText("BTC");
                } else if (s.equals(conversion_types[1])) {
                    double rootValue = Double.parseDouble(card.getEth());
                    double valueToConvert =  Double.parseDouble(editable.toString());
                    double ans = valueToConvert/rootValue;
                    String formatted_ans = NumberFormat.getNumberInstance(Locale.UK).format(BigDecimal.valueOf(ans).setScale(2, BigDecimal.ROUND_HALF_UP));
                    result.setText(formatted_ans);
                    cur2.setText("ETH");
                    cur1.setText(currency.getSymbol());
                } else if (s.equals(conversion_types[2])) {
                    double rootValue = Double.parseDouble(card.getBtc());
                    double valueToConvert =  Double.parseDouble(editable.toString());
                    double ans = valueToConvert*rootValue;
                    String formatted_ans = NumberFormat.getNumberInstance(Locale.UK).format(BigDecimal.valueOf(ans).setScale(2, BigDecimal.ROUND_HALF_UP));
                    result.setText(formatted_ans);
                    cur1.setText("BTC");
                    cur2.setText(currency.getSymbol());
                } else if (s.equals(conversion_types[3])) {
                    double rootValue = Double.parseDouble(card.getEth());
                    double valueToConvert =  Double.parseDouble(editable.toString());;
                    double ans = valueToConvert*rootValue;
                    String formatted_ans = NumberFormat.getNumberInstance(Locale.US).format(BigDecimal.valueOf(ans).setScale(2, BigDecimal.ROUND_HALF_UP));
                    result.setText(formatted_ans);
                    cur1.setText("ETH");
                    cur2.setText(currency.getSymbol());
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
        editText.setText(card.getBtc());
       // editText.setText("56");

        Log.e("yyy", card.getBtc());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
