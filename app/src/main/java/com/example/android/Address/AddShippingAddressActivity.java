package com.example.android.Address;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
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
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.LoginSystem.LoginActivity;
import com.example.android.R;
import com.example.android.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class AddShippingAddressActivity extends AppCompatActivity {

    private TextInputEditText editTextAddressName;
    private TextInputEditText editTextAddress1;
    private TextInputEditText editTextAddress2;
    private TextInputEditText editTextCity;
    private TextInputEditText editTextPostCode;
    private TextInputEditText editTextCountry;
    private TextInputEditText editTextRegion;
    private ProgressBar progressBar;
    private Button submitAddressBtn;
    private String addressNameHolder;
    private String address1Holder;
    private String address2lHolder;
    private String cityHolder;
    private String postCodeHolder;
    private String countryHolder;
    private String regionHolder;
    private String isDefaultHolder;
    private SessionManager session;
    private Boolean CheckEditText;
    private RequestQueue queue;
    private myDbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipping_address);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("إضافة عنوان");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        session = new SessionManager(this);
        queue = Volley.newRequestQueue(this);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(AddShippingAddressActivity.this, LoginActivity.class));
            finish();
        }

        if (this.getIntent().getIntExtra("isFirstAddress", 0) == 0) isDefaultHolder = "yes";
        else isDefaultHolder = "no";

        progressBar = findViewById(R.id.progressBarRegister);
        editTextAddressName = findViewById(R.id.editTextAddAddressName);
        editTextAddress1 = findViewById(R.id.editTextAddAddressAddress1);
        editTextAddress2 = findViewById(R.id.editTextAddAddressAddress2);
        editTextCity = findViewById(R.id.editTextAddAddressCity);
        editTextPostCode = findViewById(R.id.editTextAddAddressPostCode);
        editTextCountry = findViewById(R.id.editTextAddAddressCountry);
        editTextRegion = findViewById(R.id.editTextAddAddressRegion);
        submitAddressBtn = findViewById(R.id.buttonSubmit);

        editTextAddressName.requestFocus();

        editTextAddressName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextAddress1.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextAddress2.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextCity.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextPostCode.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextCountry.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextRegion.setFilters(new InputFilter[]{new EmojiExcludeFilter()});


        editTextAddressName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextAddress1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }

            }
        });
        editTextAddress2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        submitAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking whether EditText is Empty or Not
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    submitAddressBtn.setText("");
                    submitAddressBtn.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        public void run() {
                            addAddress();
                        }
                    }, 1000);
                }
            }

        });
    }

    private void CheckEditTextIsEmptyOrNot() {

        addressNameHolder = editTextAddressName.getText().toString();
        address1Holder = editTextAddress1.getText().toString();
        address2lHolder = editTextAddress2.getText().toString();
        cityHolder = editTextCity.getText().toString();
        postCodeHolder = editTextPostCode.getText().toString();
        countryHolder = editTextCountry.getText().toString();
        regionHolder = editTextRegion.getText().toString();

        CheckEditText = true;
        if (TextUtils.isEmpty(addressNameHolder)) {
            CheckEditText = false;
            editTextAddressName.setError("أدخل اسم العنوان");
        }
        if (TextUtils.isEmpty(address1Holder)) {
            CheckEditText = false;
            editTextAddress1.setError("أدخل العنوان 1");
        }
        if (TextUtils.isEmpty(address2lHolder)) {
            CheckEditText = false;
            editTextAddress2.setError("أدخل العنوان 2");
        }
        if (TextUtils.isEmpty(cityHolder)) {
            CheckEditText = false;
            editTextCity.setError("أدخل المدينة");
        }
        if (TextUtils.isEmpty(postCodeHolder)) {
            CheckEditText = false;
            editTextPostCode.setError("أدخل رمز البريد");
        }
        if (TextUtils.isEmpty(countryHolder)) {
            CheckEditText = false;
            editTextCountry.setError("أدخل البلد");
        }
        if (TextUtils.isEmpty(regionHolder)) {
            CheckEditText = false;
            editTextRegion.setError("أدخل المنطقة");
        }
    }

    private void addAddress() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            Toast.makeText(getApplicationContext(), "تم إضافة العنوان بنجاح", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddShippingAddressActivity.this, ShowAddressActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            submitAddressBtn.setEnabled(true);
                            submitAddressBtn.setText("تسجيل");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                submitAddressBtn.setEnabled(true);
                submitAddressBtn.setText("تسجيل");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId() + "");
                Params.put("address_name", addressNameHolder);
                Params.put("Address_1", address1Holder);
                Params.put("Address_2", address2lHolder);
                Params.put("city", cityHolder);
                Params.put("post_code", postCodeHolder);
                Params.put("coutry", countryHolder);
                Params.put("region", regionHolder);
                Params.put("is_default", isDefaultHolder);
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            startActivity(new Intent(AddShippingAddressActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }
}