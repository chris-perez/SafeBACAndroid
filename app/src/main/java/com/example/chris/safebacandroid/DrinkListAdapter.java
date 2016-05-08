package com.example.chris.safebacandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Steel on 5/1/16.
 */


public class DrinkListAdapter extends BaseAdapter implements Filterable{

    private LayoutInflater inflater;
    private ItemFilter filter;
    private ArrayList<Drink> completeData;
    private ArrayList<Drink> filteredData;

    /**
     * Adapter constructor
     * @param context activity containing list
     * @param data array of drinks to be applied to list
     */
    public DrinkListAdapter(Context context, ArrayList<Drink> data){

        inflater = LayoutInflater.from(context);
        filter = new ItemFilter();

        this.completeData = data;
        this.filteredData = this.completeData;
    }

    /**
     * temporary holder for list item views
     */
    private class ViewHolder{
        TextView unitName;
        TextView unitAbv;
        TextView unitId;
    }

    @Override
    public int getCount(){
        if (filteredData == null){
            return 0;
        }else{
            return filteredData.size();
        }
    }
    @Override
    public Drink getItem(int position){
        if (filteredData == null || position >= filteredData.size()){
            return null;
        }else{
            return filteredData.get(position);
        }
    }
    @Override
    public long getItemId(int position){
        Drink temp = getItem(position);
        if (temp != null) {
            return temp.getId();
        }else{
            return 0;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list, null);
                holder.unitName = (TextView) convertView.findViewById(R.id.unit_name);
                holder.unitAbv = (TextView) convertView.findViewById(R.id.unit_abv);
                holder.unitId = (TextView)convertView.findViewById(R.id.unit_id);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.unitName.setText(filteredData.get(position).getName());
            double precAbv = (float) (filteredData.get(position).getAbv() * 100);
            precAbv = Math.round(precAbv * 100.0) / 100.0;
            holder.unitAbv.setText(precAbv + "%");
            holder.unitId.setText(filteredData.get(position).getId()+"");
            return convertView;
    }

    public Filter getFilter(){
        return filter;
    }

    /**
     * Allows filtering of list by user input
     */

    private class ItemFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint){

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            ArrayList<Drink> temp_list = completeData;
            int count = temp_list.size();

            ArrayList<Drink> nlist = new ArrayList<>(count);
            String filterable;

            for (int i = 0; i < count; i++){
                filterable = temp_list.get(i).getName() + " " + temp_list.get(i).getAbv();
                if (filterable.toLowerCase().contains(filterString)) {
                    nlist.add(temp_list.get(i));
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            filteredData = (ArrayList<Drink>)results.values;
            notifyDataSetChanged();
        }
    }
}
