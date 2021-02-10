package com.example.android.SlideMenu;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.android.AppConfig;
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.LoginSystem.LoginActivity;
import com.example.android.OrderHistory.Order;
import com.example.android.OrderHistory.OrderAdapter;
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

public class ShowOrdersHistoryActivity extends AppCompatActivity {

    private myDbAdapter helper;
    private SessionManager session;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private LinearLayout linearLayoutEmpty;
    private List<Order> orderList;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private Button shoppingBtn;

    public static ShowOrdersHistoryActivity reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders_history);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("طلبات الشراء");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        reference = this;

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(ShowOrdersHistoryActivity.this, LoginActivity.class));
            finish();
        }

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);

        shoppingBtn = findViewById(R.id.button_showOrderHistory_shopping);
        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        orderList = new ArrayList<>();
        recyclerView = findViewById(R.id.showOrderHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        linearLayoutEmpty = findViewById(R.id.linearLayoutShowOrderHistoryEmpty);


        getOrderHistory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem itemCart = menu.findItem(R.id.shoppingCart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        AppConfig.setBadgeCount(this, icon, helper.getCount() + "");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.shoppingCart) {
            startActivity(new Intent(ShowOrdersHistoryActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    public void getOrderHistory() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_ORDER_HISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                linearLayoutEmpty.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Order order;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    order = new Order();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Order Id.
                                    order.setId(jsonObject.getInt("ID"));

                                    //Adding Order Product Id.
                                    order.setProductId(jsonObject.getInt("product_id"));

                                    //Adding Order User Id.
                                    order.setUserId(jsonObject.getInt("user_id"));

                                    //Adding Order Quantity.
                                    order.setQuantity(jsonObject.getInt("quantity"));

                                    //Adding Order Price.
                                    order.setPrice(jsonObject.getInt("price"));

                                    //Adding Order Date Time.
                                    order.setDateTime(jsonObject.getString("date_time"));

                                    //Adding Order Payment Type.
                                    order.setPaymentType(jsonObject.getString("payment_type"));

                                    //Adding Order Payment Document.
                                    order.setPaymentDocument(jsonObject.getString("payment_document"));

                                    //Adding Order Shipment Ref.
                                    order.setShipmentRef(jsonObject.getString("shipment_ref"));

                                    //Adding Order Information.
                                    order.setInfo(jsonObject.getString("info"));

                                    orderList.add(order);

                                }

                                progressBar.setVisibility(View.GONE);
                                linearLayoutEmpty.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowOrdersHistoryActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                linearLayoutEmpty.setVisibility(View.VISIBLE);
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

    public void allItemDeleted() {
        linearLayoutEmpty.setVisibility(View.VISIBLE);
    }
}
