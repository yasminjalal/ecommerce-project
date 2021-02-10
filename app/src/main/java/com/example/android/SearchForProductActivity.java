package com.example.android;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.ProductRecycleView.Product;
import com.example.android.ProductRecycleView.ProductAdapter;

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

public class SearchForProductActivity extends AppCompatActivity implements ProductAdapter.OnItemClicked {

    private myDbAdapter helper;
    private SessionManager session;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private LinearLayout linearLayoutPriceFilter;
    private RequestQueue queue;
    private List<Product> productList;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private CrystalRangeSeekbar rangeSeekbar;
    private TextView tvMin;
    private TextView tvMax;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_product);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("نتيجة البحث");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        query = this.getIntent().getStringExtra("query");

        helper = new myDbAdapter(this);
        session = new SessionManager(this);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewSearchForProductEmpty);
        linearLayoutPriceFilter = findViewById(R.id.linearLayoutSearchForProduct);

        productList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.searchForProductRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(SearchForProductActivity.this); // Bind the listener

        rangeSeekbar = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekBar);
        tvMin = (TextView) findViewById(R.id.seekBarMinValue);
        tvMax = (TextView) findViewById(R.id.seekBarMaxValue);
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                adapter.getFilter().filter(minValue + "-" + maxValue);
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));
            }
        });
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
//                Log.d("CRS=>", String.valueOf(minValue));
            }
        });

        SearchForProduct();


    }


    public void SearchForProduct() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH_FOR_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                Log.e("i", "Empty");
                                progressBar.setVisibility(View.GONE);
                                textViewEmpty.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Product item;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    item = new Product();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Product ID.
                                    item.setId(Integer.valueOf(jsonObject.getString("ID")));

                                    //Adding Product Name.
                                    item.setName(jsonObject.getString("name"));

                                    //Adding Product Description.
                                    item.setDescription(jsonObject.getString("description"));

                                    //Adding Product Price.
                                    item.setPrice(jsonObject.getDouble("price"));

                                    //Adding Product Old Price.
                                    item.setOldPrice(jsonObject.getString("old_price"));

                                    //Adding Product Weight.
                                    item.setWeight(jsonObject.getDouble("weight"));

                                    //Adding Product Quantity.
                                    item.setQuantity(Integer.valueOf(jsonObject.getString("quantity")));

                                    //Adding Product Image Path.
                                    item.setImagePath(jsonObject.getString("img_path"));

                                    //Adding Product Badge.
                                    item.setProductBadge(jsonObject.getString("product_badge"));

                                    //Adding Product Is Delivery Free.
                                    if (jsonObject.getString("is_delivery_free").equals("yes")) item.setDeliveryFree(true);
                                    else item.setDeliveryFree(false);

                                    //Adding Product Is Favorite.
                                    if (jsonObject.getString("is_favorite").equals("yes")) item.setFavorite(true);
                                    else item.setFavorite(false);

                                    //Adding Product Category Id.
                                    item.setCatId(Integer.valueOf(jsonObject.getString("cat_id")));

                                    productList.add(item);

                                }

                                progressBar.setVisibility(View.GONE);
                                textViewEmpty.setVisibility(View.GONE);
                                linearLayoutPriceFilter.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                                adapter.getFilter().filter("1-1000");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchForProductActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                textViewEmpty.setText("لا يوجد اتصال بالإنترنت");
                textViewEmpty.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("keyword", query);
                return Params;
            }
        };
        queue.add(stringRequest);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem itemCart = menu.findItem(R.id.shoppingCart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        AppConfig.setBadgeCount(this, icon,helper.getCount() +"");
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
            startActivity(new Intent(SearchForProductActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        Intent intent = new Intent(SearchForProductActivity.this, ShowSingleProductActivity.class);
        // Sending ListView clicked value using intent.
        intent.putExtra("id", productList.get(position).getId());
        intent.putExtra("name", productList.get(position).getName());
        intent.putExtra("description", productList.get(position).getDescription());
        intent.putExtra("weight", productList.get(position).getWeight());
        intent.putExtra("price", productList.get(position).getPrice());
        intent.putExtra("oldPrice", productList.get(position).getOldPrice());
        intent.putExtra("quantity", productList.get(position).getQuantity());
        intent.putExtra("imagePath", productList.get(position).getImagePath());
        intent.putExtra("productBadge", productList.get(position).getProductBadge());
        intent.putExtra("isDeliveryFree", productList.get(position).getDeliveryFree());
        intent.putExtra("catId", productList.get(position).getCatId());
        intent.putExtra("isFavorite", productList.get(position).isFavorite());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
}
