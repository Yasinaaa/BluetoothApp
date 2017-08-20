package ru.android.bluetooth.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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
    private Context mContext;
    private OnItemClicked onClick;
    private SparseBooleanArray selectedItems;

    public interface OnItemClicked {
        void onItemClick(String text);
    }

    public DeviceAdapter(List<String> list, OnItemClicked onClick) {
        this.mList = list;
        mFilteredList = list;
        this.onClick = onClick;
        selectedItems = new SparseBooleanArray();
    }

    public void add(String item){
        if (!mList.contains(item)) {
            mList.add(item);
            //mFilteredList.add(item);
            notifyItemChanged(mList.size()-1);
            //notifyItemChanged(mFilteredList.size()-1);
            notifyDataSetChanged();
        }

    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView =
                LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false);
        return new DeviceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DeviceHolder holder, final int position) {
        holder.title.setText(mFilteredList.get(position));
        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    holder.itemView.setSelected(false);
                }
                else {
                    selectedItems.put(position, true);
                    holder.itemView.setSelected(true);
                }
                onClick.onItemClick(mFilteredList.get(position));
            }
        });
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
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

    public class DeviceHolder extends RecyclerView.ViewHolder{

        public TextView title;

        public DeviceHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_device_title);
        }
    }
}
