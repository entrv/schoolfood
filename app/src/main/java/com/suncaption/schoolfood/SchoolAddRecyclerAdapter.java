package com.suncaption.schoolfood;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

public class SchoolAddRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<SchoolAddItem> mItemList;
    private RequestManager glide;

    public SchoolAddRecyclerAdapter(List<SchoolAddItem> itemList, RequestManager glide) {
        //Log.d("entrv","RecyclerViewAdapter");
        mItemList = itemList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_school_add
                    , parent, false);

            return new ItemViewHolder(view);
        } else {
            Log.d("entrv", "onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading,
                    parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        //Log.d("entrv","onBindViewHolder");
        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {
        //Log.d("entrv",String.valueOf(mItemList.size()));
        return mItemList == null ? 0 : mItemList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textMoviePlayViews, textMoviePlayTime, textMovieTitle;
        //ImageView imageMovie;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textMoviePlayViews = itemView.findViewById(R.id.textMoviePlayViews);
            textMoviePlayTime = itemView.findViewById(R.id.textMoviePlayTime);
            textMovieTitle = itemView.findViewById(R.id.textMovieTitle);
            //imageMovie = itemView.findViewById(R.id.imageMovie);

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(ItemViewHolder viewHolder, final int position) {

        SchoolAddItem item = mItemList.get(position);
        //viewHolder.textMoviePlayViews.setText("Views : " + item.getSchoolType());
        viewHolder.textMoviePlayViews.setText("");
        //viewHolder.textMoviePlayTime.setText("PlayTime : " + item.getSchoolCode());
        viewHolder.textMoviePlayTime.setText("학교 주소 : " + item.getZipAdres());
        viewHolder.textMovieTitle.setText("학교명 :" + item.getKraOrgNm());
        //viewHolder.imageMovie.setText(item.getImageMovie());
        //String imageUrl = item.getImageMovie();

        //this.glide.load(imageUrl).into(viewHolder.imageMovie);

//        viewHolder.textMovieTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//                Toast.makeText(context, position +"", Toast.LENGTH_LONG).show();
//            }
//        });

    }


}

