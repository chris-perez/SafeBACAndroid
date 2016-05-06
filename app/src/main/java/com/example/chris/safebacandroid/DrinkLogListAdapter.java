package com.example.chris.safebacandroid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 5/4/2016.
 */
public class DrinkLogListAdapter extends ArrayAdapter<DrinkLogItem> {
  Context context;
  List<DrinkLogItem> drinkLogItems;
  int layoutResID;

  public DrinkLogListAdapter(Context context, int resource, List<DrinkLogItem> listItems) {
    super(context, resource, listItems);
    this.context = context;
    this.layoutResID = resource;
    this.drinkLogItems = listItems;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    DrinkItemHolder drinkItemHolder;
    View view = convertView;

    if (view == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      drinkItemHolder = new DrinkItemHolder();
      view = inflater.inflate(layoutResID, parent, false);

      drinkItemHolder.name = (TextView) view.findViewById(R.id.drink_name);
      drinkItemHolder.volume = (TextView) view.findViewById(R.id.drink_volume);
      drinkItemHolder.date = (TextView) view.findViewById(R.id.drink_date);

      view.setTag(drinkItemHolder);
    } else {
      drinkItemHolder = (DrinkItemHolder) view.getTag();
    }

    DrinkLogItem dItem = (DrinkLogItem) this.drinkLogItems.get(position);
    drinkItemHolder.name.setText(dItem.getName());
    drinkItemHolder.volume.setText(dItem.getVolume()+"");

    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd 'at' h:mm a", Locale.US);
    String date = dateFormatter.format(new Date(dItem.getDate()));
    drinkItemHolder.date.setText(date);

    return view;
  }

  private static class DrinkItemHolder {
    TextView name;
    TextView volume;
    TextView date;
  }
}
