package com.example.android.Category;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.example.android.Address.ShowAddressActivity;
import com.example.android.AppConfig;
import com.example.android.Cart.ShowCartActivity;
import com.example.android.ConnectionManager;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.LoginSystem.LoginActivity;
import com.example.android.ProductRecycleView.Product;
import com.example.android.ProductRecycleView.ShowProductActivity;
import com.example.android.R;
import com.example.android.SearchForProductActivity;
import com.example.android.SessionManager;
import com.example.android.ShowSingleProductActivity;
import com.example.android.SlideMenu.AboutUsActivity;
import com.example.android.SlideMenu.ContactWithUsActivity;
import com.example.android.SlideMenu.FavoriteProductActivity;
import com.example.android.SlideMenu.ShowOrdersHistoryActivity;
import com.example.android.SlideMenu.TrackingShipmentActivity;
import com.example.android.SlideMenu.UpdateUserPasswordActivity;
import com.example.android.SliderAdapter;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class MainCategoryActivity extends AppCompatActivity implements CategoryAdapter.OnItemClicked, NavigationView.OnNavigationItemSelectedListener {

    private myDbAdapter helper;
    private SessionManager session;
    private SliderView sliderView;
    private SliderAdapter sliderAdapter;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private List<Category> categoryList;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private SearchView simpleSearchView;
    private List<Product> searchedProductList;
    private List<String> suggestions;
    private ImageView imageViewTwitter;
    private ImageView imageViewInstagram;
    private ImageView imageViewSnapChat;
    private NavigationView navigationView;
    private Button buttonLogin;
    private View navHeader;
    private ArrayList<Integer> subCategoryId;
    private boolean flagCategoryHasSimilarProduct = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        session = new SessionManager(MainCategoryActivity.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });

        navHeader = navigationView.getHeaderView(0);
        buttonLogin = navHeader.findViewById(R.id.button_nav_header_main);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainCategoryActivity.this, LoginActivity.class));
                finish();
            }
        });
        if (session.isLoggedIn()) {
            buttonLogin.setText(session.getUserFirstName() + " " + session.getUserLastName());
            buttonLogin.setClickable(false);
            buttonLogin.setVisibility(View.VISIBLE);
            if (!session.isActive()) getIsActive();
            helper.deleteAll(0);
            getCartInformation();
        } else {
            buttonLogin.setVisibility(View.VISIBLE);
            session.setIsActive(false);
        }

        sliderView = findViewById(R.id.imageSlider);
        sliderAdapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(sliderAdapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(5); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        progressBar = findViewById(R.id.progressBar);
        subCategoryId = new ArrayList<>();
        categoryList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.productMainRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(MainCategoryActivity.this); // Bind the listener
        recyclerView.setNestedScrollingEnabled(false);

        searchedProductList = new ArrayList<>();
        simpleSearchView = (SearchView) findViewById(R.id.searchView); // inititate a search view
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
                Intent intent = new Intent(MainCategoryActivity.this, ShowSingleProductActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("id", searchedProductList.get(position).getId());
                intent.putExtra("name", searchedProductList.get(position).getName());
                intent.putExtra("description", searchedProductList.get(position).getDescription());
                intent.putExtra("weight", searchedProductList.get(position).getWeight());
                intent.putExtra("oldPrice", searchedProductList.get(position).getOldPrice());
                intent.putExtra("price", searchedProductList.get(position).getPrice());
                intent.putExtra("quantity", searchedProductList.get(position).getQuantity());
                intent.putExtra("imagePath", searchedProductList.get(position).getImagePath());
                intent.putExtra("productBadge", searchedProductList.get(position).getProductBadge());
                intent.putExtra("isDeliveryFree", searchedProductList.get(position).getDeliveryFree());
                intent.putExtra("catId", searchedProductList.get(position).getCatId());
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
                Intent intent = new Intent(MainCategoryActivity.this, SearchForProductActivity.class);
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

        if (!ConnectionManager.isOnline(MainCategoryActivity.this)) {
            ConnectionManager.displayMobileDataSettingsDialog(MainCategoryActivity.this);
            progressBar.setVisibility(View.GONE);
        } else {
            getAllCategories();
        }
    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        subCategoryId.clear();
        if (categoryList.get(position).subCategory.size() == 0) {
            Intent intent = new Intent(MainCategoryActivity.this, ShowProductActivity.class);
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
                Intent intent = new Intent(MainCategoryActivity.this, ShowProductActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("catId", categoryList.get(position).getId());
                intent.putExtra("catName", categoryList.get(position).getName());
                intent.putIntegerArrayListExtra("subCategoryId", subCategoryId);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainCategoryActivity.this, SubCategoryActivity.class);
                // Sending ListView clicked value using intent.
                intent.putExtra("catId", categoryList.get(position).getId());
                intent.putExtra("catName", categoryList.get(position).getName());
                startActivity(intent);
            }
        }
    }

    public void getAllCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                progressBar.setVisibility(View.GONE);
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
                                    if (item.getType().equals("main"))
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

                                progressBar.setVisibility(View.GONE);
                                sliderView.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();

                                final Menu menu = navigationView.getMenu();
                                menu.clear();
                                for (int i = 0; i < categoryList.size(); i++) {
                                    menu.add(R.id.category, categoryList.get(i).getId(), 0, categoryList.get(i).getName()).setIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
                                }
                                navigationView.inflateMenu(R.menu.activity_main_drawer_general);
                                if (session.isLoggedIn()) {
                                    navigationView.inflateMenu(R.menu.activity_main_drawer_control_panel);
                                }
                                navigationView.inflateMenu(R.menu.activity_main_drawer_footer);
                                navigationView.invalidate();

                            }
                            setSocialMediaSection();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("i", "ERROR - " + error.toString());
                Toast.makeText(MainCategoryActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
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
                Toast.makeText(MainCategoryActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
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

    public void getCartInformation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_CART_INFORMATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    Boolean isDeliveryFree = false;
                                    //Adding Product Is Delivery Free.
                                    if (jsonObject.getString("is_delivery_free").equals("yes"))
                                        isDeliveryFree = true;
                                    helper.insertData(jsonObject.getString("name"), jsonObject.getString("description"), jsonObject.getDouble("price"), jsonObject.getDouble("weight"), jsonObject.getString("img_path"), jsonObject.getInt("cat_id"), jsonObject.getInt("product_id"), session.getUserId(), jsonObject.getInt("quantity"), isDeliveryFree);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(LoginActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId() + "");
                return Params;
            }
        };
        queue.add(stringRequest);


    }


    private void getIsActive() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_IS_ACTIVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("")) session.setIsActive(false);
                        else if (response.equals("yes")) session.setIsActive(true);
                        else if (response.equals("no")) session.setIsActive(false);
                        else session.setIsActive(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainCategoryActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId() + "");
                return Params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            startActivity(new Intent(MainCategoryActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_orders) {
            startActivity(new Intent(MainCategoryActivity.this, ShowOrdersHistoryActivity.class));
        } else if (id == R.id.nav_shipments_track) {
            startActivity(new Intent(MainCategoryActivity.this, TrackingShipmentActivity.class));
        } else if (id == R.id.nav_shipping_address) {
            startActivity(new Intent(MainCategoryActivity.this, ShowAddressActivity.class));
        } else if (id == R.id.nav_favorite_products) {
            startActivity(new Intent(MainCategoryActivity.this, FavoriteProductActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(MainCategoryActivity.this, UpdateUserPasswordActivity.class));
        } else if (id == R.id.nav_contact_with_us) {
            startActivity(new Intent(MainCategoryActivity.this, ContactWithUsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainCategoryActivity.this, AboutUsActivity.class));
        } else if (id == R.id.nav_footer) {

        } else if (id == R.id.nav_sign_out) {
            logoutUser();
        } else {
            int position = 0;
            for (int j = 0; j < categoryList.size(); j++) {
                if (categoryList.get(j).getId() == id) {
                    position = j;
                    break;
                }
            }
            onItemClick(position);
        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logoutUser() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainCategoryActivity.this);
        alertDialog.setTitle("تسجيل خروج");
        alertDialog.setMessage("هل أنت متأكد من أنك تريد تسجيل الخروج ؟");
        alertDialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteAll(session.getUserId());
                session.setLogin(false, 0, "", "", "", "");
                Toast.makeText(MainCategoryActivity.this, "تم تسجيل الخروج", Toast.LENGTH_LONG).show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // Call your Activity where you want to land after log out
                    }
                }.execute();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                finish();
                startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        alertDialog.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void setSocialMediaSection() {

        imageViewTwitter = (ImageView) navigationView.getMenu().findItem(R.id.nav_footer).getActionView().findViewById(R.id.imageView_slideMenuFooter_twitter);
        imageViewInstagram = (ImageView) navigationView.getMenu().findItem(R.id.nav_footer).getActionView().findViewById(R.id.imageView_slideMenuFooter_instagram);
        imageViewSnapChat = (ImageView) navigationView.getMenu().findItem(R.id.nav_footer).getActionView().findViewById(R.id.imageView_slideMenuFooter_snapChat);

        imageViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter)));
                startActivity(browserIntent);
            }
        });
        imageViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram)));
                startActivity(browserIntent);
            }
        });
        imageViewSnapChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.snapChat)));
                startActivity(browserIntent);
            }
        });

    }


    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
}
