package com.example.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.ProductRecycleView.Product;
import com.example.android.RelatedProduct.RelatedProductAdapter;

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

public class ShowSingleProductActivity extends AppCompatActivity implements RelatedProductAdapter.OnItemClicked {

    private myDbAdapter helper;
    private SessionManager session;
    private RelatedProductAdapter adapter;
    private ImageView imageViewProductImage;
    private TextView textViewTitle;
    private TextView textViewPrice;
    private TextView textViewOldPrice;
    private TextView textViewWeight;
    private TextView textViewBadge;
    private TextView textViewDescription;
    private ImageButton btnFavorite;
    private Button btnCart;
    private ProgressBar progressBar;
    private ProgressBar favouriteProgressBar;
    private RequestQueue queue;
    private int productIdHolder;
    private int productCatIdHolder;
    private String productNameHolder;
    private String productDescriptionHolder;
    private Double productWeightHolder;
    private Double productPriceHolder;
    private String productOldPriceHolder;
    private String productImagePathHolder;
    private String productBadgeHolder;
    private Boolean productIsDeliveryFree;
    private boolean productIsFavoriteHolder;
    private List<Product> relatedProductsList;
    private List<Product> similarProductsList;
    private RecyclerView recyclerView;
    private AlertDialog alertDialogChooseWeight;
    private int[] weightId;
    private String[] weightName;
    private Boolean getWeightCompleted = false;
    private int initialWeightIndex = -1;
    private Boolean flagRefreshProductList = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_product);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        session = new SessionManager(this);
        queue = Volley.newRequestQueue(this);

        similarProductsList = new ArrayList<>();
        relatedProductsList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.showSingleProductRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RelatedProductAdapter(this, relatedProductsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(ShowSingleProductActivity.this); // Bind the listener

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        favouriteProgressBar = (ProgressBar) findViewById(R.id.progressBarFavourite);
        imageViewProductImage = findViewById(R.id.imageView_showSingleProduct_image);
        textViewTitle = findViewById(R.id.textView_showSingleProduct_title);
        textViewOldPrice = findViewById(R.id.textView_showSingleProduct_oldPrice);
        textViewPrice = findViewById(R.id.textView_showSingleProduct_price);
        textViewWeight = findViewById(R.id.textView_showSingleProduct_weight);
        textViewBadge = findViewById(R.id.textView_showSingleProduct_badge);
        textViewDescription = findViewById(R.id.textView_showSingleProduct_description);
        btnFavorite = findViewById(R.id.btn_showSingleProduct_AddToFavorite);
        btnCart = findViewById(R.id.btn_showSingleProduct_AddToCart);

        textViewWeight.setFocusable(false);
        textViewWeight.setClickable(true);
        textViewWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getWeightCompleted) {
                    if (weightId.length == 0) {
                        textViewWeight.setClickable(false);
                        //    textViewWeight.setText("لا يوجد لديك عنوان، قم بإضافة عنوان أولاً");

                    } else {
                        CreateAlertDialogWithRadioButtonGroup();
                    }
                }
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.isLoggedIn()) {
                    btnFavorite.setImageDrawable(null);
                    btnFavorite.setEnabled(false);
                    favouriteProgressBar.setVisibility(View.VISIBLE);
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        public void run() {
                            AddToFavorite();
                        }
                    }, 1000);
                }
                else
                    Toast.makeText(ShowSingleProductActivity.this, "يرجى تسجيل الدخول لتتمكن من إضافة منتجات الى قائمة المفضلة لديك", Toast.LENGTH_LONG).show();
            }
        });


        productIdHolder = this.getIntent().getIntExtra("id", 0);
        productNameHolder = this.getIntent().getStringExtra("name");
        productDescriptionHolder = this.getIntent().getStringExtra("description");
        productWeightHolder = this.getIntent().getDoubleExtra("weight", 0);
        productPriceHolder = this.getIntent().getDoubleExtra("price", 0);
        productOldPriceHolder = this.getIntent().getStringExtra("oldPrice");
        productImagePathHolder = this.getIntent().getStringExtra("imagePath");
        productBadgeHolder = this.getIntent().getStringExtra("productBadge");
        productIsDeliveryFree = this.getIntent().getBooleanExtra("isDeliveryFree", false);
        productCatIdHolder = this.getIntent().getIntExtra("catId", 0);
        productIsFavoriteHolder = this.getIntent().getBooleanExtra("isFavorite", false);

        getSimilarProductsType();

        Title.setText("" + productNameHolder);
        Glide.with(this).load(productImagePathHolder).error(R.drawable.logo).placeholder(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageViewProductImage);
        textViewTitle.setText(productNameHolder);
        textViewPrice.setText(productPriceHolder + " ر.س");
        if (!productOldPriceHolder.equals("none")) {
            textViewOldPrice.setText(productOldPriceHolder + " ر.س");
            textViewOldPrice.setPaintFlags(textViewOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textViewOldPrice.setVisibility(View.VISIBLE);
        }
        textViewWeight.setText(productWeightHolder + " كيلو");
        if (productBadgeHolder.isEmpty() && productIsDeliveryFree) {
            textViewBadge.setText("شحن مجاني");
        } else if ((productBadgeHolder.isEmpty() || productBadgeHolder.equals(" ")) && !productIsDeliveryFree) {
            textViewBadge.setVisibility(View.GONE);
        } else if (!productBadgeHolder.isEmpty() && productIsDeliveryFree) {
            textViewBadge.setText(productBadgeHolder + " + شحن مجاني");
        } else {
            textViewBadge.setText(productBadgeHolder);
        }
        textViewDescription.setText(productDescriptionHolder);
        if (productIsFavoriteHolder) {
            btnFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_accent_24dp));

        }


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCart.setText("");
                btnCart.setEnabled(false);
             progressBar.setVisibility(View.VISIBLE);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    public void run() {
                        if (session.isLoggedIn()) {
                            AddToCart();
                        } else {
                            helper.insertData(productNameHolder, productDescriptionHolder, productPriceHolder, productWeightHolder, productImagePathHolder, productCatIdHolder, productIdHolder, 0, 1, productIsDeliveryFree);
                            Toast.makeText(getApplicationContext(), "تم إضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                            invalidateOptionsMenu();
                        }
                        progressBar.setVisibility(View.GONE);
                        btnCart.setEnabled(true);
                        btnCart.setText("أضف للسلة");
                    }
                }, 1000);
            }

        });

        getRelatedProducts();

        imageViewProductImage.requestFocus();

    }

    @Override
    public void onItemClick(int position) {
        // The onClick implementation of the RecyclerView item click
        Intent intent = new Intent(ShowSingleProductActivity.this, ShowSingleProductActivity.class);
        // Sending ListView clicked value using intent.
        intent.putExtra("id", relatedProductsList.get(position).getId());
        intent.putExtra("name", relatedProductsList.get(position).getName());
        intent.putExtra("description", relatedProductsList.get(position).getDescription());
        intent.putExtra("weight", relatedProductsList.get(position).getWeight());
        intent.putExtra("oldPrice", relatedProductsList.get(position).getOldPrice());
        intent.putExtra("price", relatedProductsList.get(position).getPrice());
        intent.putExtra("quantity", relatedProductsList.get(position).getQuantity());
        intent.putExtra("imagePath", relatedProductsList.get(position).getImagePath());
        intent.putExtra("productBadge", relatedProductsList.get(position).getProductBadge());
        intent.putExtra("isDeliveryFree", relatedProductsList.get(position).getDeliveryFree());
        intent.putExtra("catId", relatedProductsList.get(position).getCatId());
        intent.putExtra("isFavorite", relatedProductsList.get(position).isFavorite());
        startActivity(intent);

    }


    public void getRelatedProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_RELATED_PRODUCTS,
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

                                    relatedProductsList.add(item);

                                }

                                adapter.notifyDataSetChanged();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowSingleProductActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id", productIdHolder+"");
                Params.put("user_id", session.getUserId()+"");
                return Params;
            }
        };
        queue.add(stringRequest);


    }

    private void AddToFavorite() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_TO_FAVORITE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("تم إضافة المنتج الى المفضلة")) {
                            Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_LONG).show();
                            productIsFavoriteHolder = true;
                            btnFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_accent_24dp));
                            flagRefreshProductList = true;
                        } else if (response.equals("تم حذف المنتج من المفضلة")) {
                            Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_LONG).show();
                            productIsFavoriteHolder = false;
                            btnFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_accent_24dp));
                            flagRefreshProductList = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                        favouriteProgressBar.setVisibility(View.GONE);
                        btnFavorite.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                if(productIsFavoriteHolder)
                    btnFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_accent_24dp));
else
                btnFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_accent_24dp));
                favouriteProgressBar.setVisibility(View.GONE);
                btnFavorite.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id",productIdHolder+"");
                Params.put("userID",session.getUserId()+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    private void AddToCart() {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            Toast.makeText(getApplicationContext(), "تم إضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                            helper.insertData(productNameHolder, productDescriptionHolder, productPriceHolder, productWeightHolder, productImagePathHolder, productCatIdHolder, productIdHolder, session.getUserId(), 1, productIsDeliveryFree);
                            invalidateOptionsMenu();
                        } else {
                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id",productIdHolder+"");
                Params.put("userID",session.getUserId()+"");
                Params.put("quantity","1");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }


    public void getSimilarProductsType() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_SIMILAR_PRODUCT_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            if (response.equals("none")) {
                                weightId = new int[0];
                                weightName = new String[0];
                            } else {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Product item;
                                weightId = new int[jsonArray.length()];
                                weightName = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    item = new Product();


                                    jsonObject = jsonArray.getJSONObject(i);

                                    weightId[i] = jsonObject.getInt("ID");
                                    weightName[i] = jsonObject.getString("name") + " " + jsonObject.getString("weight") + " كيلو";

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

                                    if (item.getId() == productIdHolder) initialWeightIndex = i;

                                    similarProductsList.add(item);

                                }

                                getWeightCompleted = true;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowSingleProductActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("prod_id", productIdHolder+"");
                Params.put("user_id", session.getUserId()+"");
                return Params;
            }
        };
        queue.add(stringRequest);


    }

    private void CreateAlertDialogWithRadioButtonGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowSingleProductActivity.this);

        builder.setTitle("");
        builder.setSingleChoiceItems(weightName, initialWeightIndex, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (weightId[item] == productIdHolder) {
                    alertDialogChooseWeight.dismiss();
                } else {
                    alertDialogChooseWeight.dismiss();
                    Intent intent = new Intent(ShowSingleProductActivity.this, ShowSingleProductActivity.class);
                    // Sending ListView clicked value using intent.
                    intent.putExtra("id", similarProductsList.get(item).getId());
                    intent.putExtra("name", similarProductsList.get(item).getName());
                    intent.putExtra("description", similarProductsList.get(item).getDescription());
                    intent.putExtra("weight", similarProductsList.get(item).getWeight());
                    intent.putExtra("oldPrice", similarProductsList.get(item).getOldPrice());
                    intent.putExtra("price", similarProductsList.get(item).getPrice());
                    intent.putExtra("quantity", similarProductsList.get(item).getQuantity());
                    intent.putExtra("imagePath", similarProductsList.get(item).getImagePath());
                    intent.putExtra("productBadge", similarProductsList.get(item).getProductBadge());
                    intent.putExtra("isDeliveryFree", similarProductsList.get(item).getDeliveryFree());
                    intent.putExtra("catId", similarProductsList.get(item).getCatId());
                    intent.putExtra("isFavorite", similarProductsList.get(item).isFavorite());
                    startActivity(intent);
                    finish();
                }
            }
        });

        alertDialogChooseWeight = builder.create();
        alertDialogChooseWeight.show();

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("flagIsRefresh",flagRefreshProductList);  // insert your extras here
        setResult(1212, i);
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
            startActivity(new Intent(ShowSingleProductActivity.this, ShowCartActivity.class));
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
