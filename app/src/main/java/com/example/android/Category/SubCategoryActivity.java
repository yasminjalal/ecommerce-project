package com.example.android.Category;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
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
import com.example.android.ProductRecycleView.Product;
import com.example.android.ProductRecycleView.ShowProductActivity;
import com.example.android.R;
import com.example.android.SearchForProductActivity;
import com.example.android.SessionManager;
import com.example.android.ShowSingleProductActivity;
import com.example.android.SliderAdapter;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

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

public class SubCategoryActivity extends AppCompatActivity implements CategoryAdapter.OnItemClicked {

    private myDbAdapter helper;
    private SessionManager session;
    private SliderView sliderView;
    private SliderAdapter sliderAdapter;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private RequestQueue queue;
    private List<Category> categoryList;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private SearchView simpleSearchView;
    private List<Product> searchedProductList;
    private List<String> suggestions;
    private int categoryIdHolder;
    private String categoryNameHolder;
    private ArrayList<Integer> subCategoryId;
    private boolean flagCategoryHasSimilarProduct = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        session = new SessionManager(SubCategoryActivity.this);

        categoryIdHolder = this.getIntent().getIntExtra("catId", 0);
        categoryNameHolder = this.getIntent().getStringExtra("catName");
        Title.setText(categoryNameHolder + "");

        sliderView = findViewById(R.id.subCategoryImageSlider);
        sliderAdapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(sliderAdapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewSubCategoryEmpty);
        subCategoryId = new ArrayList<>();
        categoryList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.subCategoryRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(SubCategoryActivity.this); // Bind the listener

        searchedProductList = new ArrayList<>();
        simpleSearchView = (SearchView) findViewById(R.id.subCategorySearchView); // inititate a search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        simpleSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        suggestions = new ArrayList<>();

        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        simpleSearchView.setSuggestionsAdapter(suggestionAdapter);

        simpleSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Intent intent = new Intent(SubCategoryActivity.this, ShowSingleProductActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("id", searchedProductList.get(position).getId());
                intent.putExtra("name", searchedProductList.get(position).getName());
                intent.putExtra("description", searchedProductList.get(position).getDescription());
                intent.putExtra("weight", searchedProductList.get(position).getWeight());
                intent.putExtra("price", searchedProductList.get(position).getPrice());
                intent.putExtra("oldPrice", searchedProductList.get(position).getOldPrice());
                intent.putExtra("quantity", searchedProductList.get(position).getQuantity());
                intent.putExtra("imagePath", searchedProductList.get(position).getImagePath());
                intent.putExtra("productBadge", searchedProductList.get(position).getProductBadge());
                intent.putExtra("catId", searchedProductList.get(position).getCatId());
                intent.putExtra("isDeliveryFree", searchedProductList.get(position).getDeliveryFree());
                intent.putExtra("isFavorite", searchedProductList.get(position).isFavorite());
                startActivity(intent);

                //    simpleSearchView.setQuery(suggestions.get(position), true);
                simpleSearchView.clearFocus();
                return true;
            }
        });
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SubCategoryActivity.this, SearchForProductActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("query", query);
                startActivity(intent);
                return true; //false if you want implicit call to searchable activity
                // or true if you want to handle submit yourself
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Hit the network and take all the suggestions and store them in List 'suggestions'
                SearchForProduct(newText);
                String[] columns = {BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                };
                MatrixCursor cursor = new MatrixCursor(columns);
                for (int i = 0; i < suggestions.size(); i++) {
                    String[] tmp = {Integer.toString(i), suggestions.get(i), suggestions.get(i)};
                    cursor.addRow(tmp);
                }
                suggestionAdapter.swapCursor(cursor);
                return true;
            }
        });

        simpleSearchView.setFocusable(false);
        getSubCategories();
    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        subCategoryId.clear();

        if (categoryList.get(position).subCategory.size() == 0) {
            Intent intent = new Intent(SubCategoryActivity.this, ShowProductActivity.class);
            // Sending ListView clicked value using intent.
            intent.putExtra("catId", categoryList.get(position).getId());
            intent.putExtra("catName", categoryList.get(position).getName());
            intent.putIntegerArrayListExtra("subCategoryId", subCategoryId);
            startActivity(intent);
        } else if (categoryList.get(position).subCategory.size() != 0) {
            for (int i = 0; i < categoryList.get(position).subCategory.size(); i++) {
                flagCategoryHasSimilarProduct = categoryList.get(position).subCategory.get(i).getHasSimilarProducts();
                subCategoryId.add(categoryList.get(position).subCategory.get(i).getId());
            }
            if (flagCategoryHasSimilarProduct) {
                Intent intent = new Intent(SubCategoryActivity.this, ShowProductActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("catId", categoryList.get(position).getId());
                intent.putExtra("catName", categoryList.get(position).getName());
                intent.putIntegerArrayListExtra("subCategoryId", subCategoryId);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SubCategoryActivity.this, SubCategoryActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("catId", categoryList.get(position).getId());
                intent.putExtra("catName", categoryList.get(position).getName());
                startActivity(intent);
            }
        }
    }

    public void getSubCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_CATEGORIES,
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

                                Category item;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    item = new Category();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Category ID.
                                    item.setId(Integer.valueOf(jsonObject.getString("ID")));

                                    //Adding Category Title.
                                    item.setName(jsonObject.getString("name"));

                                    //Adding Category Image Path.
                                    item.setImagePath(jsonObject.getString("img_path"));

                                    //Adding Category Description.
                                    item.setDescription(jsonObject.getString("description"));

                                    //Adding Category Has Similar Product.
                                    if (jsonObject.getString("has_similar_products").equals("yes"))
                                        item.setHasSimilarProducts(true);
                                    else
                                        item.setHasSimilarProducts(false);

                                    //Adding Category Original Type.
                                    item.setOriginalType(jsonObject.getInt("original_type"));

                                    //Adding Category Type.
                                    item.setType(jsonObject.getString("type"));

                                    if (item.getType().equals("main")) ;
                                    else if (item.getType().equals(categoryIdHolder + ""))
                                        categoryList.add(item);
                                    else {
                                        for (int j = 0; j < categoryList.size(); j++) {
                                            if (categoryList.get(j).getId() == Integer.valueOf(item.getType())) {
                                                categoryList.get(j).subCategory.add(item);
                                                break;
                                            }
                                        }
                                    }

                                }
                                if (categoryList.size() == 0) {
                                    progressBar.setVisibility(View.GONE);
                                    textViewEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    textViewEmpty.setVisibility(View.GONE);
                                    sliderView.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SubCategoryActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                textViewEmpty.setText("لا يوجد اتصال بالإنترنت");
                textViewEmpty.setVisibility(View.VISIBLE);
            }
        });
        queue.add(stringRequest);


    }

    public void SearchForProduct(final String keyWord) {
        final List<String> suggestionsFlags = new ArrayList<>();
        final List<Product> searchedProductListFlags = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH_FOR_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {

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


                                    if (!suggestionsFlags.contains(item.getName())) {
                                        suggestionsFlags.add(item.getName());
                                        searchedProductListFlags.add(item);
                                    }
                                }
                                searchedProductList.clear();
                                suggestions.clear();
                                searchedProductList.addAll(searchedProductListFlags);
                                suggestions.addAll(suggestionsFlags);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SubCategoryActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("keyword", keyWord);
                return Params;
            }
        };
        queue.add(stringRequest);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            startActivity(new Intent(SubCategoryActivity.this, ShowCartActivity.class));
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
