package com.example.android.Address;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class ShowAddressActivity extends AppCompatActivity {

    private myDbAdapter helper;
    private SessionManager session;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private RequestQueue queue;
    private Button addAddressBtn;
    private List<Address> addressList;
    private RecyclerView recyclerView;
    private AddressAdapter adapter;

    public static ShowAddressActivity reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_address);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("عناوين الشحن");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(ShowAddressActivity.this, LoginActivity.class));
            finish();
        }

        reference = this;

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewShowAddressEmpty);

        addressList = new ArrayList<>();
        recyclerView = findViewById(R.id.showAddressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(this, addressList);
        recyclerView.setAdapter(adapter);

        addAddressBtn = findViewById(R.id.btnShowAddressAdd);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowAddressActivity.this, AddShippingAddressActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("isFirstAddress", addressList.size());
                startActivity(intent);
                finish();
            }

        });

        getAddress();
    }


    public void getAddress() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                progressBar.setVisibility(View.GONE);
                                textViewEmpty.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Address address;
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    address = new Address();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Address ID.
                                    address.setId(jsonObject.getInt("ID"));

                                    //Adding Address Name.
                                    address.setName(jsonObject.getString("address_name"));

                                    //Adding Address1.
                                    address.setAddress1(jsonObject.getString("Address_1"));

                                    //Adding Address2.
                                    address.setAddress2(jsonObject.getString("Address_2"));

                                    //Adding Address City.
                                    address.setCity(jsonObject.getString("city"));

                                    //Adding Address Post Code.
                                    address.setPostCode(jsonObject.getString("post_code"));

                                    //Adding Address Image Path.
                                    address.setCountry(jsonObject.getString("coutry"));

                                    //Adding Address Badge.
                                    address.setRegion(jsonObject.getString("region"));

                                    //Adding Address Is Default.
                                    if (jsonObject.getString("is_default").equals("yes"))
                                        address.setDefault(true);
                                    else address.setDefault(false);

                                    addressList.add(address);

                                }

                                progressBar.setVisibility(View.GONE);
                                textViewEmpty.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowAddressActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                textViewEmpty.setText("لا يوجد اتصال بالإنترنت");
                textViewEmpty.setVisibility(View.VISIBLE);
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
        recyclerView.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.VISIBLE);
    }

    public void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            public void run() {
                addressList.clear();
                getAddress();
            }
        }, 1000);
    }

    public void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarGone() {
        progressBar.setVisibility(View.GONE);
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
            startActivity(new Intent(ShowAddressActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
}

