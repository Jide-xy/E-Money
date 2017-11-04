package com.example.babajidemustapha.e_money;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    CardStore cardStore;
    List<Card> cards;
    TextView placeholder;
    TextView date;
    LinearLayout list;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RequestQueue queue;
    SharedPreferences lastUpdate;
    Boolean isRefreshing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(this);
        placeholder = (TextView) findViewById(R.id.emptyPlaceholder);
        list = (LinearLayout) findViewById(R.id.ll);
        date = (TextView) findViewById(R.id.date);
        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        lastUpdate = getSharedPreferences("last_update",MODE_PRIVATE);
        cardStore = new CardStore(this);
        isRefreshing = false;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCards();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,CardSetup.class);
                startActivity(intent);
            }
        });
       loadCards();
    }

    public void loadCards(){
        cards = cardStore.getCards();
        if(cards.isEmpty()){
            list.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
            return;
        }
        placeholder.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        //  recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,10,true));
        recyclerView.setAdapter(new CustomAdapter1(cards));
        date.setText("Last Updated: "+ lastUpdate.getString("date",null));
        if(isRefreshing){
            return;
        }
        else {
            refreshRates();
        }
    }

    public void updateCards(JSONObject jsonObject, List<Card> cards) throws JSONException{
        for(int i=0;i<cards.size();i++){
             String cur_code = cards.get(i).getCurrency_code();
            cardStore.updateCard(new Card(cur_code,jsonObject.getJSONObject("BTC").getDouble(cur_code)+"", jsonObject.getJSONObject("ETH").getDouble(cur_code)+""));
        }
    }

    public void refreshRates(){
        isRefreshing = true;
            String curr = "";
            for(int i=0;i<cards.size();i++){
                if(i==cards.size()-1){
                    curr+=cards.get(i).getCurrency_code();
                }
                else{
                    curr+=cards.get(i).getCurrency_code()+",";
                }
            }
            final String tsym = curr;
            String query = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=" + tsym;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, query, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (!response.toString().toLowerCase().contains("error")) {
                        try {
                           updateCards(response,cards);
                            setLastUpdated();
                            cards = cardStore.getCards();
                            recyclerView.setAdapter(new CustomAdapter1(cards));
                            Toast.makeText(MainActivity.this,"Rates refreshed",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                              Toast.makeText(MainActivity.this,"Unknown error",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                          Toast.makeText(MainActivity.this,"An error occurred while refreshing rates",Toast.LENGTH_SHORT).show();
                        Log.e("error", response.toString());
                    }
                    isRefreshing = false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                     Toast.makeText(MainActivity.this,"Connection error.",Toast.LENGTH_SHORT).show();
                    isRefreshing = false;
                }
            });
            queue.add(request);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setLastUpdated(){
        String datee = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date());
       SharedPreferences.Editor edit = lastUpdate.edit();
        edit.putString("date",datee);
        edit.commit();
        date.setText("Last Updated: "+ lastUpdate.getString("date",null));
    }

    private class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.ViewHolder> {
        List<Card> source;

        private CustomAdapter1(List<Card> source) {
            this.source = source;
        }

        private void add(Card card) {
            source.add(card);
        }

        @Override
        public CustomAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomAdapter1.ViewHolder(getLayoutInflater().inflate(R.layout.card_item_layout,parent, false));
        }

        @Override
        public void onBindViewHolder(CustomAdapter1.ViewHolder holder, int position) {
            Log.e("id",source.get(position).getCard_id()+"");
            holder.cur_code.setText(source.get(position).getCurrency_code());
            holder.btc.setText("BTC: "+source.get(position).getBtc());
            holder.eth.setText("ETH: "+source.get(position).getEth());
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView cur_code;
            TextView btc;
            TextView eth;

            private ViewHolder(View itemView) {
                super(itemView);
                cur_code = (TextView) itemView.findViewById(R.id.cur_code);
                btc = (TextView) itemView.findViewById(R.id.btc);
                eth = (TextView) itemView.findViewById(R.id.eth);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Conversion.class);
                intent.putExtra("ID",source.get(getAdapterPosition()).getCard_id());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.card_action_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                cardStore.deleteCard(source.get(getAdapterPosition()));
                                source.remove(getAdapterPosition());
                                notifyDataSetChanged();
                                if(source.size()==0){
                                    loadCards();
                                }
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            }
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onResume() {
        super.onResume();
      loadCards();
    }
}


