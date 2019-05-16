package com.dts.listadapt;

import android.content.Context;
import java.util.ArrayList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dts.base.AppMethods;
import com.dts.base.DateUtils;
import com.dts.base.MiscUtils;
import com.dts.base.clsClasses;
import com.dts.combust.PBase;
import com.dts.combust.R;

public class LA_Proyecto  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsProyecto> items= new ArrayList<clsClasses.clsProyecto>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_Proyecto(Context context, PBase owner, ArrayList<clsClasses.clsProyecto> results) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;

        mu=owner.mu;
        du=owner.du;
        app=owner.app;
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    public void refreshItems() {
        notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.lv_proyecto, null);
            holder = new ViewHolder();

            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl2.setText(""+items.get(position).nombre);

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl2;
    }

}

