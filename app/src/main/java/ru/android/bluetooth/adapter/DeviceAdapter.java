package ru.android.bluetooth.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 01.08.17.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> implements Filterable{

    private List<String> mList;
    private List<String> mFilteredList;

    public DeviceAdapter(List<String> list) {
        this.mList = list;
        mFilteredList = list;
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        holder.title.setText(mFilteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mList;
                } else {

                    ArrayList<String> filteredList = new ArrayList<String>();
                    for (String deviceTitle : mList) {
                        if (deviceTitle.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(deviceTitle);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults.values != null) {
                    mFilteredList = (ArrayList<String>) filterResults.values;
                }
                notifyDataSetChanged();
            }
        };
    }

    public void setUsualList(){
        mFilteredList = mList;
        notifyDataSetChanged();
    }

    public static class DeviceHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public DeviceHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_device_title);
        }
    }
}
