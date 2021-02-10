package com.example.android.Cart;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.android.R;
import com.example.android.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartHolder> {

    List<CartItem> CartList;
    private myDbAdapter helper;
    private SessionManager session;
    private RequestQueue queue;
    private Context context;
    //declare interface
    private OnItemClicked onClick;

    public CartItemAdapter(Context context, List<CartItem> CartList) {
        this.CartList = CartList;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        session = new SessionManager(context);
        helper = new myDbAdapter(context);
    }

    @Override
    public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_cart_item, parent, false);
        CartHolder holder = new CartHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CartHolder holder, final int position) {

        final CartItem cartItem = CartList.get(position);
        holder.cartItemName.setText(cartItem.getName());
        if (cartItem.getDeliveryFree()) {
            holder.cartItemBadge.setText("شحن مجاني");
        } else {
            holder.cartItemBadge.setVisibility(View.GONE);
        }
        holder.cartItemWeight.setText(cartItem.getWeight() + " كيلو");
        holder.cartItemQuantity.setText(cartItem.getQuantity() + "");
        holder.cartItemPrice.setText(cartItem.getPrice() + " ر.س");
        holder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCartActivity.reference.setProgressBarVisible();
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    public void run() {

                        if (session.isLoggedIn()) {
                            EditCart(cartItem.getId(), cartItem.getQuantity() + 1, cartItem, holder.cartItemQuantity);
                            helper.insertData(cartItem.getName(), cartItem.getDescription(), cartItem.getPrice(), cartItem.getWeight(), cartItem.getImagePath(), cartItem.getCatId(), cartItem.getProductId(), 0, cartItem.getQuantity() + 1, cartItem.getDeliveryFree());
                        } else {
                            helper.insertData(cartItem.getName(), cartItem.getDescription(), cartItem.getPrice(), cartItem.getWeight(), cartItem.getImagePath(), cartItem.getCatId(), cartItem.getProductId(), 0, cartItem.getQuantity() + 1, cartItem.getDeliveryFree());
                            cartItem.setQuantity(cartItem.getQuantity() + 1);
                            holder.cartItemQuantity.setText(cartItem.getQuantity() + "");
                            ShowCartActivity.reference.UpdateTotalPrice();
                            ShowCartActivity.reference.setProgressBarGone();
                        }


                    }
                }, 500);
            }
        });

        holder.removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItem.getQuantity() > 1) {
                    ShowCartActivity.reference.setProgressBarVisible();
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        public void run() {

                            if (session.isLoggedIn()) {
                                EditCart(cartItem.getId(), cartItem.getQuantity() - 1, cartItem, holder.cartItemQuantity);
                                helper.removeQuantity(cartItem.getProductId(), cartItem.getQuantity() - 1);
                            } else {
                                helper.removeQuantity(cartItem.getProductId(), cartItem.getQuantity() - 1);
                                cartItem.setQuantity(cartItem.getQuantity() - 1);
                                holder.cartItemQuantity.setText(cartItem.getQuantity() + "");
                                ShowCartActivity.reference.UpdateTotalPrice();
                                ShowCartActivity.reference.setProgressBarGone();
                            }
                        }
                    }, 500);
                }
            }
        });
        Glide.with(context).load(cartItem.getImagePath()).error(R.drawable.logo).placeholder(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.cartItemImage);
        holder.cartItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowCartActivity.reference.setProgressBarVisible();
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    public void run() {
                        if (session.isLoggedIn()) {
                            DeleteFromCart(cartItem.getProductId(), cartItem.getId(), position);
                        } else {
                            helper.deleteProduct(cartItem.getProductId());
                            Toast.makeText(context, "تم حذف المنتج من السلة بنجاح", Toast.LENGTH_LONG).show();
                            CartList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, CartList.size());
                            ShowCartActivity.reference.UpdateTotalPrice();
                            if (CartList.size() == 0) {
                                ShowCartActivity.reference.allItemDeleted();
                            }
                            ShowCartActivity.reference.setProgressBarGone();
                        }

                    }
                }, 500);

            }
        });

    }

    @Override
    public int getItemCount() {
        return CartList.size();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    private void EditCart(final int cartId, final int quantity, final CartItem cartItem, final TextView textView) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            Toast.makeText(context, "تم تعديل كمية المنتج بنجاح", Toast.LENGTH_LONG).show();
                            cartItem.setQuantity(quantity);
                            textView.setText(cartItem.getQuantity() + "");
                            ShowCartActivity.reference.UpdateTotalPrice();
                        } else {
                            Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                        ShowCartActivity.reference.setProgressBarGone();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("basket_id",cartId+"");
                Params.put("userID",session.getUserId()+"");
                Params.put("quantity",quantity+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    private void DeleteFromCart(final int productId, final int cartId, final int position) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DELETE_FROM_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {

                            Toast.makeText(context, "تم حذف المنتج من السلة بنجاح", Toast.LENGTH_LONG).show();
                            CartList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, CartList.size());
                            if (CartList.size() == 0) {
                                ShowCartActivity.reference.allItemDeleted();
                            }
                            helper.deleteProduct(productId);
                            ShowCartActivity.reference.UpdateTotalPrice();
                        } else {
                            Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                        ShowCartActivity.reference.setProgressBarGone();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("basket_id",cartId+"");
                Params.put("userID",session.getUserId()+"");
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

    class CartHolder extends RecyclerView.ViewHolder {
        TextView cartItemName;
        TextView cartItemBadge;
        TextView cartItemWeight;
        TextView cartItemPrice;
        TextView cartItemQuantity;
        ImageView cartItemImage;
        LinearLayout addQuantity;
        LinearLayout removeQuantity;
        LinearLayout deleteItem;

        public CartHolder(View itemView) {
            super(itemView);
            cartItemName = (TextView) itemView.findViewById(R.id.textView_recycle_view_cart_item_name);
            cartItemBadge = (TextView) itemView.findViewById(R.id.textView_recycle_view_cart_item_badge);
            cartItemWeight = (TextView) itemView.findViewById(R.id.textView_recycle_view_cart_item_weight);
            cartItemPrice = (TextView) itemView.findViewById(R.id.textView_recycle_view_cart_item_price);
            cartItemQuantity = (TextView) itemView.findViewById(R.id.textView_recycle_view_cart_item_quantity);
            cartItemImage = (ImageView) itemView.findViewById(R.id.imageView_recycle_view_cart_item);
            addQuantity = (LinearLayout) itemView.findViewById(R.id.imageView_recycle_view_cart_item_add);
            removeQuantity = (LinearLayout) itemView.findViewById(R.id.imageView_recycle_view_cart_item_remove);
            deleteItem = (LinearLayout) itemView.findViewById(R.id.imageView_recycle_view_cart_item_delete);
        }
    }
}



