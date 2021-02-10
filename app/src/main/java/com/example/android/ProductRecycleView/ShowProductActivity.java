package com.example.android.ProductRecycleView;

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
import com.example.android.AppConfig;
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.R;
import com.example.android.SessionManager;
import com.example.android.ShowSingleProductActivity;

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

public class ShowProductActivity extends AppCompatActivity implements ProductAdapter.OnItemClicked {

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
    private int catIdHolder;
    private String catNameHolder;
    private int catOriginalTypeHolder;
    private  ArrayList<Integer> subCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        catIdHolder = this.getIntent().getIntExtra("catId", 0);
        catNameHolder = this.getIntent().getStringExtra("catName");
        subCategoryId = getIntent().getIntegerArrayListExtra("subCategoryId");
        Title.setText("" + catNameHolder);

        helper = new myDbAdapter(this);
        session = new SessionManager(this);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewShowProductEmpty);
        linearLayoutPriceFilter = findViewById(R.id.linearLayoutShowProduct);

        productList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.showProductRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(ShowProductActivity.this); // Bind the listener

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

        getProducts();


    }


    public void getProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_PRODUCTS,
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

                                    //Adding Product Old Price.
                                    item.setOldPrice(jsonObject.getString("old_price"));

                                    //Adding Product Price.
                                    item.setPrice(jsonObject.getDouble("price"));

                                    //Adding Product Weight.
                                    item.setWeight(jsonObject.getDouble("weight"));

                                    //Adding Product Quantity.
                                    item.setQuantity(Integer.valueOf(jsonObject.getString("quantity")));

                                    //Adding Product Image Path.
                                    item.setImagePath(jsonObject.getString("img_path"));

                                    //Adding Product Badge.
                                    item.setProductBadge(jsonObject.getString("product_badge"));

                                    //Adding Product Category Id.
                                    item.setCatId(Integer.valueOf(jsonObject.getString("cat_id")));

                                    //Adding Product Is Delivery Free.
                                    if (jsonObject.getString("is_delivery_free").equals("yes"))
                                        item.setDeliveryFree(true);
                                    else item.setDeliveryFree(false);

                                    //Adding Product Is Favorite.
                                    if (jsonObject.getString("is_favorite").equals("yes"))
                                        item.setFavorite(true);
                                    else item.setFavorite(false);

                                    if ((item.getCatId() == catIdHolder) || subCategoryId.contains(item.getCatId()))
                                    productList.add(item);

                                }

                                if (productList.size() == 0) {
                                    progressBar.setVisibility(View.GONE);
                                    textViewEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    textViewEmpty.setVisibility(View.GONE);
                                    linearLayoutPriceFilter.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                    adapter.getFilter().filter("1-1000");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowProductActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                textViewEmpty.setText("لا يوجد اتصال بالإنترنت");
                textViewEmpty.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
//                Params.put("cat_id", catIdHolder + "");
                Params.put("user_id", session.getUserId() + "");

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
            startActivity(new Intent(ShowProductActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click

        Intent i = new Intent(new Intent(ShowProductActivity.this, ShowSingleProductActivity.class));
        i.putExtra("id", productList.get(position).getId());
        i.putExtra("name", productList.get(position).getName());
        i.putExtra("description", productList.get(position).getDescription());
        i.putExtra("weight", productList.get(position).getWeight());
        i.putExtra("oldPrice", productList.get(position).getOldPrice());
        i.putExtra("price", productList.get(position).getPrice());
        i.putExtra("quantity", productList.get(position).getQuantity());
        i.putExtra("imagePath", productList.get(position).getImagePath());
        i.putExtra("productBadge", productList.get(position).getProductBadge());
        i.putExtra("isDeliveryFree", productList.get(position).getDeliveryFree());
        i.putExtra("catId", productList.get(position).getCatId());
        i.putExtra("isFavorite", productList.get(position).isFavorite());
        startActivityForResult(i, 1212);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    // For Refresh Lis if Favourite Change in Single Product Activity.
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1212) {
            if (resultCode == 1212) {
                if (data.getBooleanExtra("flagIsRefresh", false)) {
                    productList.clear();
                    adapter.notifyDataSetChanged();
                    getProducts();
                }
            }
        }
    }
}