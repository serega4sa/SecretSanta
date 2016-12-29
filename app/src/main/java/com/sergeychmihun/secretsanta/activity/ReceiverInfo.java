package com.sergeychmihun.secretsanta.activity;

import java.io.Serializable;

/**
 * Created by Sergey.Chmihun on 12/23/2016.
 */
public class ReceiverInfo implements Serializable{
    private String name;
    private String email;
    private int number;
    private CustomListAdapter.ViewHolder holder;

    public void setHolder(CustomListAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public CustomListAdapter.ViewHolder getHolder() {
        return holder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
