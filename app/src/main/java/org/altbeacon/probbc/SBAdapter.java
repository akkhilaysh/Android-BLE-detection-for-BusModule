package org.altbeacon.probbc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshad Shinde on 29-01-2016.
 */

public class SBAdapter extends BaseAdapter {

    private  Activity context;
    private  ArrayList<SelectBatchModel> list;

    public SBAdapter(Context c,ArrayList<SelectBatchModel> list) {
        context = (Activity) c;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return this.list.size();
    }
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_layout, null, true);


        TextView name = (TextView) rowView.findViewById(R.id.row_batch);
        TextView status = (TextView) rowView.findViewById(R.id.row_schoolName);
        name.setText(list.get(position).getBatchName());
        status.setText(list.get(position).getSchoolName());

        return rowView;
    }

}
