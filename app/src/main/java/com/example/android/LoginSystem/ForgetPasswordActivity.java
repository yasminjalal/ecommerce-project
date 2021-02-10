package com.example.android.LoginSystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.example.android.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Yasmin Jalal - 2020.
 */

public class ForgetPasswordActivity extends AppCompatActivity {

    private myDbAdapter helper;
    private ProgressBar progressBarForgetPassword;
    private TextInputEditText editTextEmail;
    private Button buttonSubmit;
    private String emailHolder;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("نسيان كلمة المرور");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);

        progressBarForgetPassword = findViewById(R.id.progressBarForgetPassword);
        editTextEmail = findViewById(R.id.editText_forgetPassword_email);
        editTextEmail.requestFocus();
        buttonSubmit = findViewById(R.id.button_forgetPassword_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailHolder = editTextEmail.getText().toString();
                if (emailHolder.isEmpty()) {
                    editTextEmail.setError("ادخل البريد الإلكتروني");
                } else if (!emailHolder.trim().matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "البريد الإلكتروني غير صحيح", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("البريد الإلكتروني غير صحيح");
                } else { //If email is not Empty.

                    buttonSubmit.setText("");
                    buttonSubmit.setEnabled(false);
                    progressBarForgetPassword.setVisibility(View.VISIBLE);
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        public void run() {
                            forgetPassword();
                        }
                    }, 1000);
                }
            }
        });
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                }
            }
        });
    }

    public void forgetPassword() {


            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FORGET_PASSWORD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("no user with the provided email address")) {
                                buttonSubmit.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "خطأ: البريد الإلكتروني غير مسجل لدينا", Toast.LENGTH_LONG).show();
                                progressBarForgetPassword.setVisibility(View.GONE);
                                buttonSubmit.setEnabled(true);
                                buttonSubmit.setText("إرسال");
                            } else {
                                Toast.makeText(getApplicationContext(), "تم إرسال كلمة المرور الجديدة إلى البريد الإلكتروني", Toast.LENGTH_LONG).show();
                                progressBarForgetPassword.setVisibility(View.GONE);
                                buttonSubmit.setEnabled(true);
                                buttonSubmit.setText("إرسال");
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    buttonSubmit.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    super.getParams();
                    Map<String, String> Params = new HashMap<>();
                        Params.put("email", emailHolder);
                    return Params;
                }
            };
            queue.add(stringRequest);

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
            startActivity(new Intent(ForgetPasswordActivity.this, ShowCartActivity.class));
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
