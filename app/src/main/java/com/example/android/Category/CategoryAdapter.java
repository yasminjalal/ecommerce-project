package com.example.android.Category;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    private Context context;
    List<Category> CategoryList;
    //declare interface
    private OnItemClicked onClick;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.CategoryList = categoryList;
        this.context = context;
    }


    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_category_item, parent, false);
        CategoryHolder holder = new CategoryHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, final int position) {

        Category category = CategoryList.get(position);
        holder.categoryTitle.setText(category.getName());
        Glide.with(context).load(category.getImagePath()).error(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.categoryImage);
        holder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        ImageView categoryImage;

        public CategoryHolder(View itemView) {
            super(itemView);
            categoryTitle = (TextView) itemView.findViewById(R.id.textView_recycle_view_category_item);
            categoryImage = (ImageView) itemView.findViewById(R.id.imageView_recycle_view_category_item);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            Bitmap imageRounded = Bitmap.createBitmap(result.getWidth(), result.getHeight(), result.getConfig());
            Canvas canvas = new Canvas(imageRounded);
            Paint mpaint = new Paint();
            mpaint.setAntiAlias(true);
            mpaint.setShader(new BitmapShader(result, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0, 0, result.getWidth(), result.getHeight())), 30, 30, mpaint);// Round Image Corner 100 100 100 100
            bmImage.setImageBitmap(imageRounded);
        }
    }
}



