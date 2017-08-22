package ru.android.bluetooth.main;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by yasina on 22.08.17.
 */

public class AutoModePresenter {

    private Context mContext;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;

    public AutoModePresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void initRecyclerView(RecyclerView recyclerView){
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0)
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            //loading = false;
                        }
                    }
                }
            }
        });
    }
}
