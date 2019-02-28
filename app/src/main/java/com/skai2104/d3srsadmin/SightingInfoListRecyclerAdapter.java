package com.skai2104.d3srsadmin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SightingInfoListRecyclerAdapter extends RecyclerView.Adapter<SightingInfoListRecyclerAdapter.ViewHolder> {
    private List<SightingInfo> mSightingInfoList;
    private Context mContext;
    private String mMissingPersonName;
    private String mMissingPersonId;

    public SightingInfoListRecyclerAdapter(Context context, List<SightingInfo> sightingInfoList, String missingPersonName, String missingPersonId) {
        mSightingInfoList = sightingInfoList;
        mContext = context;
        mMissingPersonName = missingPersonName;
        mMissingPersonId = missingPersonId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sighting_info_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final String datetime = mSightingInfoList.get(position).getDateTime();
        final String content = mSightingInfoList.get(position).getContent();
        final String reportPersonName = mSightingInfoList.get(position).getReportPersonName();
        final String reportPersonId = mSightingInfoList.get(position).getReportPersonId();
        final String docId = mSightingInfoList.get(position).getDocId();
        final String location = mSightingInfoList.get(position).getLocation();

        viewHolder.mDateTimeTV.setText(datetime);
        viewHolder.mContentTV.setText(content);
        viewHolder.mLocationTV.setText(location);

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, SightingInfoDetailsActivity.class);
                i.putExtra("datetime", datetime);
                i.putExtra("content", content);
                i.putExtra("reportPersonName", reportPersonName);
                i.putExtra("reportPersonId", reportPersonId);
                i.putExtra("docId", docId);
                i.putExtra("location", location);
                i.putExtra("name", mMissingPersonName);
                i.putExtra("id", mMissingPersonId);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSightingInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mDateTimeTV, mContentTV, mLocationTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            mDateTimeTV = mView.findViewById(R.id.datetimeTV);
            mContentTV = mView.findViewById(R.id.contentTV);
            mLocationTV = mView.findViewById(R.id.locationTV);
        }
    }
}
