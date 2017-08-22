package ru.android.bluetooth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
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
import ru.android.bluetooth.schedule.helper.MinuteParser;
import ru.android.bluetooth.schedule.helper.OneDayModel;

/**
 * Created by yasina on 22.08.17.
 */

public class OnOffAdapter extends RecyclerView.Adapter<OnOffAdapter.OnOffHolder>{

    private List<OneDayModel> mList;
    private Context mContext;

    public OnOffAdapter(List<OneDayModel> list) {
        this.mList = list;
    }

    public void add(OneDayModel item){
        if (!mList.contains(item)) {
            mList.add(item);
            notifyItemChanged(mList.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public OnOffAdapter.OnOffHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView =
                LayoutInflater.from(mContext).inflate(R.layout.item_day_info, parent, false);


        return new OnOffAdapter.OnOffHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OnOffAdapter.OnOffHolder holder, final int position) {
        holder.tvDay.setText(mList.get(position).getDay());
        MinuteParser minuteParser = new MinuteParser(mList.get(position));
        String[] minutes = minuteParser.parseToStr();
        holder.tvOn.setText(minutes[0]);
        holder.tvOff.setText(minutes[1]);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class OnOffHolder extends RecyclerView.ViewHolder{

        public TextView tvDay;
        public TextView tvOn;
        public TextView tvOff;

        public OnOffHolder(View itemView) {
            super(itemView);
            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvOn = (TextView) itemView.findViewById(R.id.tv_on);
            tvOff = (TextView) itemView.findViewById(R.id.tv_off);
        }
    }
}
