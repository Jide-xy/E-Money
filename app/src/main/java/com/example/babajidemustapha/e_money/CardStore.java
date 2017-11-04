package com.example.babajidemustapha.e_money;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babajide Mustapha on 10/23/2017.
 */

public class CardStore extends SQLiteOpenHelper {
    private final String CARDS_TABLE  = "CARD";
    private final String CARD_ID  = "CARD_ID";
    private final String CURRENCY_CODE  = "CURRENCY_CODE";
    private final String BTC  = "BTC_VALUE";
    private final String ETH  = "ETH_VALUE";
    private final String CREATE_TABLE_QUERY  = "CREATE TABLE "+ CARDS_TABLE+"("+ CARD_ID +" INTEGER PRIMARY KEY,"+ CURRENCY_CODE+
            " TEXT,"+ BTC+" TEXT, "+ETH+" TEXT)";
    public CardStore(Context context){
        super(context,"CardStore",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
     sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public List<Card> getCards(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Card> cards = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ CARDS_TABLE, null);
        while(cursor.moveToNext()){
            cards.add(new Card(cursor.getInt(cursor.getColumnIndex(CARD_ID)),
                    cursor.getString(cursor.getColumnIndex(CURRENCY_CODE)),
                    cursor.getString(cursor.getColumnIndex(BTC)),
                    cursor.getString(cursor.getColumnIndex(ETH))));
        }
        return cards;
    }
    public Card getCard(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ CARDS_TABLE+" WHERE "+CARD_ID+ " = ?", new String[] {id+""});
       cursor.moveToFirst();
        return new Card(id,cursor.getString(cursor.getColumnIndex(CURRENCY_CODE)),
                cursor.getString(cursor.getColumnIndex(BTC)),
                cursor.getString(cursor.getColumnIndex(ETH)));
    }
    public boolean addCard(Card card){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT("+CURRENCY_CODE+") AS COUNT FROM "+ CARDS_TABLE+" WHERE "+CURRENCY_CODE+" = ?", new String[] {card.getCurrency_code()});
        cursor.moveToFirst();
        if(cursor.getInt(cursor.getColumnIndex("COUNT"))==0){
            ContentValues values = new ContentValues();
            values.put(CURRENCY_CODE,card.getCurrency_code());
            values.put(BTC,card.getBtc());
            values.put(ETH,card.getEth());
            db = this.getWritableDatabase();
           Log.e("card id", db.insert(CARDS_TABLE,null,values)+"");
            return true;
        }
        else{
            return false;
        }

    }
    public void updateCard(Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BTC,card.getBtc());
        values.put(ETH,card.getEth());
        db.update(CARDS_TABLE,values,CURRENCY_CODE+" = ?",new String[] {card.getCurrency_code()});
    }
    public void deleteCard(Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CARDS_TABLE,CURRENCY_CODE+" = ?", new String[] {card.getCurrency_code()});
    }
}
