package com.example.android.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.AppConfig;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.LoginSystem.LoginActivity;
import com.example.android.R;
import com.example.android.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class ShowCartActivity extends AppCompatActivity implements CartItemAdapter.OnItemClicked {

    public static ShowCartActivity reference;
    private myDbAdapter helper;
    private SessionManager session;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private Button btnClearAll;
    private TextView TextViewTotalPrice;
    private Button submitBtn;
    private Button shoppingBtn;
    private LinearLayout linearLayoutEmptyCart;
    private RelativeLayout relativeLayoutShowCart;
    private double totalPrice = 00.00;
    private List<CartItem> cartItemList;
    private RecyclerView recyclerView;
    private CartItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("السلة");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        reference = this;
        helper = new myDbAdapter(this);
        session = new SessionManager(ShowCartActivity.this);
        queue = Volley.newRequestQueue(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarShowCart);

        cartItemList = new ArrayList<>();
        recyclerView = findViewById(R.id.showCartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(this, cartItemList);
        recyclerView.setAdapter(adapter);


        adapter.setOnClick(ShowCartActivity.this); // Bind the listener
        TextViewTotalPrice = findViewById(R.id.textView_showCart_totalPrice);
        btnClearAll = findViewById(R.id.btn_showCart_clearCart);
        linearLayoutEmptyCart = findViewById(R.id.linearLayout_showCart_EmptyCart);
        relativeLayoutShowCart = findViewById(R.id.RelativeLayout_showCart_RecycleView);
        submitBtn = findViewById(R.id.buttonSubmit);
        shoppingBtn = findViewById(R.id.button_showCart_shopping);


        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ShowCartActivity.reference.setProgressBarVisible();
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    public void run() {

                        btnClearAll.setClickable(false);
                        if (session.isLoggedIn()) {
                            ClearCart();
                        } else {
                            helper.deleteAll(0);
                            relativeLayoutShowCart.setVisibility(View.GONE);
                            allItemDeleted();
                            Toast.makeText(ShowCartActivity.this, "تم تفريغ السلة بنجاح", Toast.LENGTH_LONG).show();
                        }
                        invalidateOptionsMenu();
                        btnClearAll.setClickable(true);
                        ShowCartActivity.reference.setProgressBarGone();


                    }
                }, 500);


            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.isLoggedIn() && session.isActive())
                    startActivity(new Intent(ShowCartActivity.this, CompletePaymentActivity.class).putExtra("total", totalPrice));
                else if (session.isLoggedIn() && !session.isActive())
                    Toast.makeText(ShowCartActivity.this, "الحساب غير مفعل، تأكد من بريدك الإلكتروني أو قم بمراجعة خدمة العملاء", Toast.LENGTH_LONG).show();
                else {
                    startActivity(new Intent(ShowCartActivity.this, LoginActivity.class));
                    finish();
                }
            }

        });
        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        if (session.isLoggedIn()) {
            getCartInformation();
        } else {
            List<CartItem> test = new ArrayList<>(helper.getData());
            if (test.size() == 0) {
                progressBar.setVisibility(View.GONE);
                linearLayoutEmptyCart.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < test.size(); i++) {
                    cartItemList.add(test.get(i));
                }
                adapter.notifyDataSetChanged();
                UpdateTotalPrice();
                progressBar.setVisibility(View.GONE);
                linearLayoutEmptyCart.setVisibility(View.GONE);
                relativeLayoutShowCart.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click

    }


    public void getCartInformation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_CART_INFORMATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                Log.e("i", "Empty");
                                progressBar.setVisibility(View.GONE);
                                linearLayoutEmptyCart.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                CartItem item;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    item = new CartItem();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Product Name.
                                    item.setName(jsonObject.getString("name"));

                                    //Adding Product Description.
                                    item.setDescription(jsonObject.getString("description"));

                                    //Adding Product Price.
                                    item.setPrice(jsonObject.getDouble("price"));

                                    //Adding Product Weight.
                                    item.setWeight(jsonObject.getDouble("weight"));

                                    //Adding Product Image Path.
                                    item.setImagePath(jsonObject.getString("img_path"));

                                    //Adding Product Category Id.
                                    item.setCatId(jsonObject.getInt("cat_id"));

                                    //Adding Cart ID.
                                    item.setId(jsonObject.getInt("ID"));

                                    //Adding Product ID.
                                    item.setProductId(jsonObject.getInt("product_id"));

                                    //Adding Product ID.
                                    item.setUserId(jsonObject.getInt("user_ID"));

                                    //Adding Product Quantity.
                                    item.setQuantity(jsonObject.getInt("quantity"));

                                    //Adding Product Is Delivery Free.
                                    if (jsonObject.getString("is_delivery_free").equals("yes"))
                                        item.setDeliveryFree(true);
                                    else item.setDeliveryFree(false);

                                    cartItemList.add(item);
                                }

                                progressBar.setVisibility(View.GONE);
                                linearLayoutEmptyCart.setVisibility(View.GONE);
                                relativeLayoutShowCart.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                                UpdateTotalPrice();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowCartActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                linearLayoutEmptyCart.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId()+"");
                return Params;
            }
        };
        queue.add(stringRequest);


    }


    private void ClearCart() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLEAR_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            helper.deleteAll(session.getUserId());
                            relativeLayoutShowCart.setVisibility(View.GONE);
                            allItemDeleted();
                            Toast.makeText(ShowCartActivity.this, "تم تفريغ السلة بنجاح", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ShowCartActivity.this, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(ShowCartActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("userID",session.getUserId()+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    public void allItemDeleted() {
        relativeLayoutShowCart.setVisibility(View.GONE);
        linearLayoutEmptyCart.setVisibility(View.VISIBLE);
        UpdateTotalPrice();
    }

    public void UpdateTotalPrice() {
        totalPrice = 00.00;
        for (int i = 0; i < cartItemList.size(); i++) {
            totalPrice = totalPrice + (cartItemList.get(i).getPrice() * cartItemList.get(i).getQuantity());
        }
        TextViewTotalPrice.setText(totalPrice + " ر.س");
    }

    public void setProgressBarVisible() {
        findViewById(R.id.progressBarShowCart).setVisibility(View.VISIBLE);
    }

    public void setProgressBarGone() {
        findViewById(R.id.progressBarShowCart).setVisibility(View.GONE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
