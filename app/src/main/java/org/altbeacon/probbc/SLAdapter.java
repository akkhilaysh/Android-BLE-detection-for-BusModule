package org.altbeacon.probbc;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
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
 * Created by Admin-PC on 2/29/2016.
 */
public class SLAdapter  extends BaseAdapter {
    private Activity context;
    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<StudentListProvider> list;

    public SLAdapter(Context c, ArrayList<StudentListProvider> list) {
        context = (Activity) c;
        this.list = list;
    }
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
    public void remove(SLAdapter object) {
        list.remove(object);
        notifyDataSetChanged();
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
        View rowView = inflater.inflate(R.layout.row_layout2, null, true);


        TextView name = (TextView) rowView.findViewById(R.id.s_name);
        TextView status = (TextView) rowView.findViewById(R.id.s_childStatus);
        name.setText(list.get(position).getName());
        status.setText(list.get(position).getStatus());

        return rowView;
    }

}

