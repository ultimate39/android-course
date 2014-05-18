package com.ultimate39.android.androidcourse.ui.vacancy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ultimate39.android.androidcourse.R;
import com.ultimate39.android.androidcourse.core.BitmapCacheDisplayer;
import com.ultimate39.android.androidcourse.core.vacancy.Vacancy;

import java.util.ArrayList;

/**
 * Created by Влад on 15.05.14.
 */
public class CachedAdapterVacancy extends BaseAdapter {
    public ArrayList<Vacancy> mVacancies;
    private Context mContext;
    private BitmapCacheDisplayer mBitmapCacheDisplayer;
    private boolean isShowLoading = true;

    public CachedAdapterVacancy(Context context, ArrayList<Vacancy> vacancies) {
        mVacancies = vacancies;
        mContext = context;
        mBitmapCacheDisplayer = BitmapCacheDisplayer.getInstance(context, "images");
    }

    public void stopShowLoading() {
        isShowLoading = false;
    }

    @Override
    public int getCount() {
        if (isShowLoading) {
            return mVacancies.size() + 1;
        }
        return mVacancies.size();
    }

    @Override
    public Object getItem(int position) {
        return mVacancies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void stopDisplayImages() {
        mBitmapCacheDisplayer.stopDisplayImages();
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == mVacancies.size()) {
            return false;
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (position != mVacancies.size()) {
            convertView = inflater.inflate(R.layout.view_listitem_vacancy, parent, false);
            setViewHolder(convertView);
            final ViewHolder vh = (ViewHolder) convertView.getTag();
            Vacancy vacancy = mVacancies.get(position);
            vh.tvName.setText(vacancy.getName());
            vh.tvPublishedTime.setText(vacancy.getTimePublished());
            vh.tvEmployerName.setText(vacancy.getEmployerName());
            if (vacancy.getLogoUrl() != null) {
                mBitmapCacheDisplayer.displayImage(vh.ivLogo, vacancy.getLogoUrl());
            }
        } else if (isShowLoading) {
            convertView = inflater.inflate(R.layout.view_progressbar, parent, false);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvEmployerName;
        TextView tvPublishedTime;
        ImageView ivLogo;
    }

    private void setViewHolder(View convertView) {
        ViewHolder vh = new ViewHolder();
        vh.tvEmployerName = (TextView) convertView.findViewById(R.id.tv_vacancy_employer);
        vh.tvName = (TextView) convertView.findViewById(R.id.tv_vacancy_name);
        vh.tvPublishedTime = (TextView) convertView.findViewById(R.id.tv_published_time);
        vh.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
        convertView.setTag(vh);
    }

}
