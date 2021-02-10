package com.example.android.FavoriteProduct;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.AppConfig;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.ProductRecycleView.Product;
import com.example.android.ProductRecycleView.ShowProductActivity;
import com.example.android.R;
import com.example.android.SessionManager;
import com.example.android.SlideMenu.FavoriteProductActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.FavoriteProductHolder>
        implements Filterable {

    private myDbAdapter helper;
    private SessionManager session;
    private RequestQueue queue;
    private Context context;
    private List<FavoriteProduct> ProductList;
    private List<FavoriteProduct> ProductListFiltered;
    private ProductAdapterListener listener;
    private OnItemClicked onClick;


    public FavoriteProductAdapter(Context context, List<FavoriteProduct> ProductList) {
        this.ProductList = ProductList;
        this.ProductListFiltered = ProductList;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        session = new SessionManager(context);
        helper = new myDbAdapter(context);
    }

    @Override
    public FavoriteProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_favorite_product_item, parent, false);
        FavoriteProductHolder holder = new FavoriteProductHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FavoriteProductHolder holder, final int position) {

        final FavoriteProduct product = ProductListFiltered.get(position);
        holder.productTitle.setText(product.getName());
        holder.productWeight.setText(product.getWeight() + " كيلو");
        holder.productPrice.setText(Double.valueOf(product.getPrice()) + " ر.س");
        Glide.with(context).load(product.getImagePath()).error(R.drawable.logo).placeholder(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.productImage);
        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
        holder.linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.favoriteIcon.setVisibility(View.GONE);
                holder.favouriteProgressBar.setVisibility(View.VISIBLE);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    public void run() {
                        product.setFavorite(false);
                        holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border_accent_24dp);
                        AddToFavorite(product, position, holder.favoriteIcon, holder.favouriteProgressBar);
                    }
                }, 750);
            }
        });

        holder.linearLayoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                holder.cartIcon.setVisibility(View.GONE);
                holder.cartProgressBar.setVisibility(View.VISIBLE);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    public void run() {
                        if (session.isLoggedIn()) {
                            AddToCart(product, holder.cartIcon, holder.cartProgressBar);
                        } else {
                            helper.insertData(product.getName(), product.getDescription(), product.getPrice(), product.getWeight(), product.getImagePath(), product.getCatId(), product.getId(), 0, 1, product.getDeliveryFree());
                            Toast.makeText(context, "تم إضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                            ((ShowProductActivity) context).invalidateOptionsMenu();
                            holder.cartProgressBar.setVisibility(View.GONE);
                            holder.cartIcon.setVisibility(View.VISIBLE);
                        }
                    }
                }, 750);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ProductListFiltered.size();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                String[] splitFilterValue = charString.split("-");
                double min = Integer.valueOf(splitFilterValue[0]);
                double max = Integer.valueOf(splitFilterValue[1]);
                if (charString.isEmpty()) {
                    ProductListFiltered = ProductList;
                } else {
                    List<FavoriteProduct> filteredList = new ArrayList<>();
                    for (FavoriteProduct row : ProductList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        Double price = Double.valueOf(row.getPrice());
                        if (price >= min && price <= max) {
                            filteredList.add(row);
                        }
                    }

                    ProductListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ProductListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ProductListFiltered = (ArrayList<FavoriteProduct>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void AddToFavorite(final FavoriteProduct product, final int position, final ImageView favoriteIcon, final ProgressBar favouriteProgressBar) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_TO_FAVORITE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("تم إضافة المنتج الى المفضلة")) {
                            Toast.makeText(context, "" + response, Toast.LENGTH_LONG).show();
                        } else if (response.equals("تم حذف المنتج من المفضلة")) {
                            Toast.makeText(context, "" + response, Toast.LENGTH_LONG).show();
                            ProductList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, ProductList.size());
                            if (ProductList.size() == 0) {
                                FavoriteProductActivity.reference.allItemDeleted();
                            }
                        } else {
                            Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                        favouriteProgressBar.setVisibility(View.GONE);
                        favoriteIcon.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                favouriteProgressBar.setVisibility(View.GONE);
                favoriteIcon.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id",product.getId()+"");
                Params.put("userID",session.getUserId()+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    private void AddToCart(final FavoriteProduct product, final ImageView cartIcon, final ProgressBar cartProgressBar) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            helper.insertData(product.getName(), product.getDescription(), product.getPrice(), product.getWeight(), product.getImagePath(), product.getCatId(), product.getId(), session.getUserId(), 1, product.getDeliveryFree());
                            Toast.makeText(context, "تم إضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                            ((FavoriteProductActivity) context).invalidateOptionsMenu();
                        } else {
                            Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                        cartProgressBar.setVisibility(View.GONE);
                        cartIcon.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                cartProgressBar.setVisibility(View.GONE);
                cartIcon.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id",product.getId()+"");
                Params.put("userID",session.getUserId()+"");
                Params.put("quantity","1");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public interface ProductAdapterListener {
        void onContactSelected(Product product);
    }

    class FavoriteProductHolder extends RecyclerView.ViewHolder {
        TextView productTitle;
        TextView productWeight;
        TextView productPrice;
        ImageView favoriteIcon;
        ImageView cartIcon;
        ImageView productImage;
        LinearLayout linearLayout;
        ProgressBar cartProgressBar;
        ProgressBar favouriteProgressBar;
        LinearLayout linearLayoutCart;
        LinearLayout linearLayoutLike;

        public FavoriteProductHolder(View itemView) {
            super(itemView);
            productTitle = (TextView) itemView.findViewById(R.id.textView_recycle_view_favorite_product_item_title);
            productWeight = (TextView) itemView.findViewById(R.id.textView_recycle_view_favorite_product_item_weight);
            productPrice = (TextView) itemView.findViewById(R.id.textView_recycle_view_favorite_product_item_price);
            favoriteIcon = (ImageView) itemView.findViewById(R.id.imageView_recycle_view_favorite_product_item_like);
            cartIcon = (ImageView) itemView.findViewById(R.id.imageView_recycle_view_favorite_product_item_cart);
            productImage = (ImageView) itemView.findViewById(R.id.imageView_recycle_view_favorite_product_item);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_recycle_view_favorite_product_item);
            cartProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBarCart);
            favouriteProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBarFavourite);
            linearLayoutCart = (LinearLayout) itemView.findViewById(R.id.linearLayout_recycle_view_favorite_product_item_cart);
            linearLayoutLike = (LinearLayout) itemView.findViewById(R.id.linearLayout_recycle_view_favorite_product_item_like);


        }
    }

}



