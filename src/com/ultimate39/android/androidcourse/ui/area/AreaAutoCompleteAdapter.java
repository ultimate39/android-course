package com.ultimate39.android.androidcourse.ui.area;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.area.Area;

import java.util.ArrayList;

public class AreaAutoCompleteAdapter extends ArrayAdapter {
    Context mContext;
    ArrayList<Area> mAreasAll;
    ArrayList<Area> mAreas;

    public AreaAutoCompleteAdapter(Context context, ArrayList<Area> areas) {
        super(context, 0, areas);
        mContext = context;
        mAreas = areas;
        mAreasAll = (ArrayList<Area>) areas.clone();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_area_autocomplete, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_area_name);
        tv.setText(mAreas.get(position).getName());
        convertView.setTag(mAreas.get(position));
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    ArrayList<Area> suggestions = new ArrayList<Area>();

    Filter nameFilter = new Filter() {

        public String convertResultToString(Object resultValue) {
            return ((Area) (resultValue)).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Area area : mAreasAll) {
                    String areaName = area.getName();
                    if (area.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(area);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Area> filteredList = (ArrayList<Area>) results.values;
            if (results != null && results.count > 0) {
                mAreas.clear();
                for (Area c : filteredList) {
                    mAreas.add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}
