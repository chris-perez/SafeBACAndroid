package com.example.chris.safebacandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steel on 4/25/16.
 */
public class Custom_List_Adapter extends BaseAdapter implements Filterable {


    List<drink> data;
    List<drink> filtered_data;
    private LayoutInflater inflater;
    private ItemFilter mFilter = new ItemFilter();

    private class ViewHolder{
        TextView unit_name;
        TextView unit_abv;
        ImageView unit_check;
    }

    public Custom_List_Adapter(Context context, List<drink> data){

        inflater = LayoutInflater.from(context);
        this.data = data;
        this.filtered_data = data;

    }

    @Override
    public int getCount(){
        return filtered_data.size();
    }

    @Override
    public drink getItem(int position){
        return filtered_data.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list, null);
            holder.unit_name = (TextView)convertView.findViewById(R.id.unit_name);
            holder.unit_abv = (TextView)convertView.findViewById(R.id.unit_abv);
            holder.unit_check = (ImageView)convertView.findViewById(R.id.unit_check);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.unit_name.setText(filtered_data.get(position).name);
        holder.unit_abv.setText(filtered_data.get(position).abv+"%");

        return convertView;
    }

    public Filter getFilter(){
        return mFilter;

    }

    private class ItemFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint){

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            List<drink> temp_list = data;

            int count = temp_list.size();
            List<drink> nlist = new ArrayList<drink>(count);
            String filterable;

            for (int i = 0; i < count; i++){
                filterable = temp_list.get(i).name + " " + temp_list.get(i).abv;
                if(filterable.toLowerCase().contains(filterString)){
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
            filtered_data = (List<drink>) results.values;
            notifyDataSetChanged();
        }


    }




}
