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
import com.example.android.FavoriteProduct.FavoriteProduct;
import com.example.android.FavoriteProduct.FavoriteProductAdapter;
import com.example.android.LoginSystem.LoginActivity;
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

public class FavoriteProductActivity extends AppCompatActivity implements FavoriteProductAdapter.OnItemClicked {

    private myDbAdapter helper;
    private SessionManager session;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private List<FavoriteProduct> favoriteProductsList;
    private RecyclerView recyclerView;
    private FavoriteProductAdapter adapter;
    private LinearLayout linearLayoutEmptyList;
    private Button shoppingBtn;
    public static FavoriteProductActivity reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_product);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("المفضلة");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        reference = this;

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(FavoriteProductActivity.this, LoginActivity.class));
            finish();
        }

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);
        linearLayoutEmptyList = findViewById(R.id.linearLayout_showFavorite_EmptyList);
        shoppingBtn = findViewById(R.id.button_showFavorite_shopping);
        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        favoriteProductsList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.favoriteProductRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteProductAdapter(this, favoriteProductsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(FavoriteProductActivity.this); // Bind the listener

        getFavoriteProducts();


    }


    public void getFavoriteProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_FAVORITE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                progressBar.setVisibility(View.GONE);
                                linearLayoutEmptyList.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                FavoriteProduct item;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    item = new FavoriteProduct();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Product ID.
                                    item.setId(jsonObject.getInt("ID"));

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
                                    item.setQuantity(jsonObject.getInt("quantity"));

                                    //Adding Product Image Path.
                                    item.setImagePath(jsonObject.getString("img_path"));

                                    //Adding Product Badge.
                                    item.setProductBadge(jsonObject.getString("product_badge"));

                                    //Adding Product Category Id.
                                    item.setCatId(jsonObject.getInt("cat_id"));

                                    //Adding Product Is Delivery Free.
                                    if (jsonObject.getString("is_delivery_free").equals("yes"))
                                        item.setDeliveryFree(true);
                                    else item.setDeliveryFree(false);

                                    //Adding Product Is Favorite.
                                    item.setFavorite(true);


                                    favoriteProductsList.add(item);

                                }

                                progressBar.setVisibility(View.GONE);
                                linearLayoutEmptyList.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FavoriteProductActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                linearLayoutEmptyList.setVisibility(View.GONE);
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
        progressBar.setVisibility(View.GONE);
        linearLayoutEmptyList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        Intent intent = new Intent(FavoriteProductActivity.this, ShowSingleProductActivity.class);
        // Sending ListView clicked value using intent.
        intent.putExtra("id", favoriteProductsList.get(position).getId());
        intent.putExtra("name", favoriteProductsList.get(position).getName());
        intent.putExtra("description", favoriteProductsList.get(position).getDescription());
        intent.putExtra("weight", favoriteProductsList.get(position).getWeight());
        intent.putExtra("oldPrice", favoriteProductsList.get(position).getOldPrice());
        intent.putExtra("price", favoriteProductsList.get(position).getPrice());
        intent.putExtra("quantity", favoriteProductsList.get(position).getQuantity());
        intent.putExtra("imagePath", favoriteProductsList.get(position).getImagePath());
        intent.putExtra("productBadge", favoriteProductsList.get(position).getProductBadge());
        intent.putExtra("isDeliveryFree", favoriteProductsList.get(position).getDeliveryFree());
        intent.putExtra("catId", favoriteProductsList.get(position).getCatId());
        intent.putExtra("isFavorite", favoriteProductsList.get(position).isFavorite());
        startActivity(intent);
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
            startActivity(new Intent(FavoriteProductActivity.this, ShowCartActivity.class));
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
