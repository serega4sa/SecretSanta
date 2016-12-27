package com.sergeychmihun.secretsanta.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.sergeychmihun.secretsanta.R;

import java.util.ArrayList;

/**
 * Created by Sergey.Chmihun on 12/23/2016.
 */
public class CustomListAdapter extends BaseAdapter{
    private ArrayList<ReceiverInfo> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<ReceiverInfo> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_receiver, null);
            holder = new ViewHolder();
            holder.name = (EditText) convertView.findViewById(R.id.editName);
            holder.email = (EditText) convertView.findViewById(R.id.editEmail);
            holder.membNum = (TextView) convertView.findViewById(R.id.membNum);
            holder.membNum.setText(String.valueOf(getMember(position).getNumber()));

            /** Save holder into receiver object to get access to input fields later */
            getMember(position).setHolder(holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        EditText name;
        EditText email;
        TextView membNum;
    }

    ReceiverInfo getMember(int position) {
        return ((ReceiverInfo) getItem(position));
    }
}
