package com.olgag.currencyconverter.model;

/**
 * Created by olgag on 14/11/2017.
 */

public class Coin {
    private long id;
    private String  coinId,coinName;

    public Coin(long id, String coinId, String coinName) {
        this.id = id;
        this.coinId = coinId;
        this.coinName = coinName;
    }

    public Coin(String currencyId, String currencyName) {
        this.coinId = currencyId;
        this.coinName = currencyName;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCurrencyId() {
        return coinId;
    }

    public void setCurrencyId(String currencyId) {
        this.coinId = currencyId;
    }

    public String getCurrencyName() {
        return coinName;
    }

    public void setCurrencyName(String currencyName) {
        this.coinName = currencyName;
    }

    @Override
    public String toString() {
        return coinName;
    }
}

