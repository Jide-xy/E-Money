package com.example.babajidemustapha.e_money;

/**
 * Created by Babajide Mustapha on 10/24/2017.
 */

public class Card {
    private int card_id;
    private String currency_code;
    private String btc;
    private String eth;

    public Card(int card_id, String currency_code, String btc, String eth){
        this.card_id = card_id;
        this.currency_code = currency_code;
        this.btc = btc;
        this.eth = eth;
    }
    public Card( String currency_code, String btc, String eth){
        this.currency_code = currency_code;
        this.btc = btc;
        this.eth = eth;
    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getBtc() {
        return btc;
    }

    public void setBtc(String btc) {
        this.btc = btc;
    }

    public String getEth() {
        return eth;
    }

    public void setEth(String eth) {
        this.eth = eth;
    }
}
