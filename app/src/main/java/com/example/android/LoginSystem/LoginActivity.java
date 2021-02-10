package com.example.android.LoginSystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.example.android.Cart.CartItem;
import com.example.android.Cart.ShowCartActivity;
import com.example.android.Category.MainCategoryActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.NotificationManeger.FCMRegistrationService;
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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmailOrPhone;
    private TextInputEditText editTextPassword;
    private TextInputLayout layoutPassword;
    private Button btnLogin;
    private Boolean CheckEditText;
    private ProgressBar progressBarLogin;
    private SessionManager session;
    private String emailOrPhoneHolder;
    private String passwordHolder;
    private RequestQueue queue;
    private myDbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("تسجيل دخول");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        session = new SessionManager(this);

        progressBarLogin = findViewById(R.id.progressBarLogin);
        editTextEmailOrPhone = findViewById(R.id.editText_login_email_or_phone);
        editTextPassword = findViewById(R.id.editText_login_password);
        layoutPassword = findViewById(R.id.layout_login_password);
        Typeface font = ResourcesCompat.getFont(this, R.font.janna_regular);
        layoutPassword.setTypeface(font);
        editTextEmailOrPhone.requestFocus();
        editTextEmailOrPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                }
            }
        });
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editTextPassword.setError(null);
                layoutPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });

        btnLogin = findViewById(R.id.btn_login_submit);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    btnLogin.setText("");
                    btnLogin.setEnabled(false);
                    progressBarLogin.setVisibility(View.VISIBLE);
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        public void run() {
                            login();
                        }
                    }, 1000);
                } else {
                    // If EditText is empty then this block will execute .
                    Toast.makeText(LoginActivity.this, "الرجاء إدخال جميع البيانات المطلوبة", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            finish();
        }


    }

    private synchronized void login() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.equals("wrong")) {
                                Toast.makeText(LoginActivity.this, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                                progressBarLogin.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                btnLogin.setText("تسجيل دخول");
                            } else {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                int id = jsonObject.getInt("ID");
                                String firstName = jsonObject.getString("first_name");
                                String lastName = jsonObject.getString("last_name");
                                String phoneNumber = jsonObject.getString("phone_number");
                                session.setLogin(true, id, phoneNumber, "", firstName, lastName);
                                startService(new Intent(LoginActivity.this, FCMRegistrationService.class));
                                Toast.makeText(getApplicationContext(), "تم تسجيل الدخول بنجاح", Toast.LENGTH_LONG).show();
                                checkIfCartHasProduct();
                            }

                        } catch (JSONException e) {
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBarLogin.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                btnLogin.setText("تسجيل دخول");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> Params = new HashMap<>();
                Params.put("email", emailOrPhoneHolder);
                Params.put("password", passwordHolder);
                return Params;
            }
        };
        queue.add(stringRequest);
    }


    public void remember(View view) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    public void goToSignupScreen(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void CheckEditTextIsEmptyOrNot() {

        emailOrPhoneHolder = editTextEmailOrPhone.getText().toString();
        passwordHolder = editTextPassword.getText().toString();
        CheckEditText = true;
        if (TextUtils.isEmpty(emailOrPhoneHolder)) {
            CheckEditText = false;
            editTextEmailOrPhone.setError("ادخل رقم الجوال أو البريد الإلكتروني");
        }
        if (TextUtils.isEmpty(passwordHolder)) {
            CheckEditText = false;
            layoutPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextPassword.setError("ادخل كلمة المرور");
        }
    }

    private void checkIfCartHasProduct() {
        List<CartItem> flag = new ArrayList<>(helper.getData());
        if (flag.size() == 0) {
            getCartInformation();
        } else {
            for (int i = 0; i < flag.size(); i++) {
                AddToCart(flag.get(i).getProductId(), flag.get(i).getQuantity());
            }
            helper.deleteAll(0);
            getCartInformation();
        }
    }

    private void AddToCart(final int productId, final int quantity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            //  Toast.makeText(getApplicationContext(), "تم إضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                        } else {
//                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id", productId + "");
                Params.put("userID", session.getUserId() + "");
                Params.put("quantity", quantity + "");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

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
                            startActivity(new Intent(LoginActivity.this, MainCategoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
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
            startActivity(new Intent(LoginActivity.this, MainCategoryActivity.class));
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.shoppingCart) {
            startActivity(new Intent(LoginActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, MainCategoryActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
}