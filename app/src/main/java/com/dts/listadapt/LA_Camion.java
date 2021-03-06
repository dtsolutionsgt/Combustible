package com.dts.listadapt;

import android.content.Context;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dts.base.AppMethods;
import com.dts.base.DateUtils;
import com.dts.base.clsClasses;
import com.dts.base.MiscUtils;
import com.dts.combust.PBase;
import com.dts.base.appGlobals;
import com.dts.combust.R;

public class LA_Camion extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsPipa> items = new ArrayList<clsClasses.clsPipa>();

    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_Camion(Context context, PBase owner, ArrayList<clsClasses.clsPipa> results) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;

        mu = owner.mu;
        du = owner.du;
        app = owner.app;
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

            convertView = l_Inflater.inflate(R.layout.lv_camion, null);
            holder = new ViewHolder();

            holder.lbl1 = (TextView) convertView.findViewById(R.id.lblV);
            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.img1 = (ImageView) convertView.findViewById(R.id.imageView6);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl2.setText("" + items.get(position).nombre);
        holder.lbl1.setText("" + items.get(position).pipaid);
        holder.img1.setImageResource(R.drawable.cisterna);

        if (selectedIndex != -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26, 138, 198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl1, lbl2;
        ImageView img1;
    }

}
