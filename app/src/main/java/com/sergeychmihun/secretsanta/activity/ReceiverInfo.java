package com.sergeychmihun.secretsanta.activity;

import java.io.Serializable;

/**
 * Created by Sergey.Chmihun on 12/23/2016.
 * Holds receivers Name and Email, his number and holder with all fields
 */
class ReceiverInfo implements Serializable{
    private String name;
    private String email;
    private int number;
    transient private CustomListAdapter.ViewHolder holder;

    void setHolder(CustomListAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    CustomListAdapter.ViewHolder getHolder() {
        return holder;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    int getNumber() {
        return number;
    }

    void setNumber(int number) {
        this.number = number;
    }
}
