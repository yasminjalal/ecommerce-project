package com.example.android.LoginSystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.R;
import com.example.android.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class RegisterActivity extends AppCompatActivity {

    private RequestQueue queue;
    private SessionManager session;
    private ProgressBar progressBarRegister;
    private TextInputEditText editTextFirstName;
    private TextInputEditText editTextLastName;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextConfirmPassword;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmPassword;
    private TextView textViewTerms;
    private Button submitRegister;
    private Boolean CheckEditText;
    private String phoneHolder;
    private String firstNameHolder;
    private String lastNameHolder;
    private String passwordHolder;
    private String confirmHolderHolder;
    private String emailHolder;
    private myDbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("تسجيل جديد");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            finish();
        }

        progressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);
        editTextFirstName = findViewById(R.id.editText_register_firstName);
        editTextLastName = findViewById(R.id.editText_register_lastName);
        editTextPhone = findViewById(R.id.editText_register_phone);
        editTextEmail = findViewById(R.id.editText_register_email);
        editTextPassword = findViewById(R.id.editText_register_password);
        editTextConfirmPassword = findViewById(R.id.editText_register_confirmPassword);
        submitRegister = findViewById(R.id.buttonSubmitRegister);
        textViewTerms = findViewById(R.id.textView_register_terms);
        layoutPassword = findViewById(R.id.layout_register_password);
        layoutConfirmPassword = findViewById(R.id.layout_register_confirmPassword);
        Typeface font = ResourcesCompat.getFont(this, R.font.janna_regular);
        layoutPassword.setTypeface(font);
        layoutConfirmPassword.setTypeface(font);
        editTextFirstName.requestFocus();

        editTextFirstName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextLastName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        editTextEmail.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        submitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    CheckEditTextIsEmptyOrNot();
                    if (CheckEditText) {
                        submitRegister.setText("");
                        submitRegister.setEnabled(false);
                        progressBarRegister.setVisibility(View.VISIBLE);
                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {
                            public void run() {
                                RegisterUser();
                            }
                        }, 1000);
                } else {
                        // If EditText is empty then this block will execute .
                        Toast.makeText(RegisterActivity.this, "الرجاء إدخال جميع البيانات المطلوبة", Toast.LENGTH_LONG).show();
                }
            }
        });

        textViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"));
                startActivity(browserIntent);
            }
        });

        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                if (hasFocus) {
                    editTextPassword.setError(" يجب أن يكون طول كلمة المرور ٨ خانات أو أكثر", null);
                } else {
                    hideKeyboard(v);
                }
            }
        });
        editTextConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editTextConfirmPassword.setError(null);
                layoutConfirmPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });

    }

    private void CheckEditTextIsEmptyOrNot() {

        phoneHolder = editTextPhone.getText().toString();
        passwordHolder = editTextPassword.getText().toString();
        confirmHolderHolder = editTextConfirmPassword.getText().toString();
        firstNameHolder = editTextFirstName.getText().toString();
        lastNameHolder = editTextLastName.getText().toString();
        emailHolder = editTextEmail.getText().toString();
        int numDigits = getNumberDigits(passwordHolder);

        CheckEditText = true;
        if (TextUtils.isEmpty(phoneHolder)) {
            CheckEditText = false;
            editTextPhone.setError("ادخل رقم الجوال");
        }
        if (TextUtils.isEmpty(firstNameHolder)) {
            CheckEditText = false;
            editTextFirstName.setError("ادخل الاسم الأول");
        }
        if (TextUtils.isEmpty(lastNameHolder)) {
            CheckEditText = false;
            editTextLastName.setError("ادخل الاسم الأخير");
        }
        if (TextUtils.isEmpty(emailHolder)) {
            CheckEditText = false;
            editTextEmail.setError("ادخل البريد الإلكتروني");
        }
        if (TextUtils.isEmpty(confirmHolderHolder)) {
            CheckEditText = false;
            layoutConfirmPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextConfirmPassword.setError("ادخل تأكيد كلمة المرور ");
        } else if (!passwordHolder.equals(confirmHolderHolder)) {
            CheckEditText = false;
            layoutConfirmPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextConfirmPassword.setError("تأكيد كلمة المرور غير متطابق");
        }
        if (TextUtils.isEmpty(passwordHolder)) {
            CheckEditText = false;
            layoutPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextPassword.setError("ادخل كلمة المرور ");
        } else if (passwordHolder.length() < 8) {
            CheckEditText = false;
            layoutPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextPassword.setError(" يجب أن يكون طول كلمة المرور ٨ خانات أو أكثر");
        } else if (numDigits < 0) {
            CheckEditText = false;
            layoutPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextPassword.setError("يجب أن تحتوي كلمة المرور على رقم واحد على الأقل");
        } else if (numDigits == passwordHolder.length()) {
            CheckEditText = false;
            layoutPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextPassword.setError("يجب أن تحتوي كلمة المرور على حرف واحد على الأقل");
        }

    }


    public int getNumberDigits(String inString) {
        if (inString.isEmpty()) {
            return 0;
        }
        int numDigits = 0;
        int length = inString.length();
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void RegisterUser() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("no_user")) {
                            Toast.makeText(getApplicationContext(), "خطأ: البريد الإلكتروني أو رقم الجوال مسجل مسبقاً", Toast.LENGTH_LONG).show();
                            progressBarRegister.setVisibility(View.GONE);
                            submitRegister.setEnabled(true);
                            submitRegister.setText("تسجيل");
                        } else {
                            Toast.makeText(getApplicationContext(), "تم التسجيل بنجاح، قم بتفعيل حسابك عن طريق الرابط المرسل على بريدك الإلكتروني", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBarRegister.setVisibility(View.GONE);
                submitRegister.setEnabled(true);
                submitRegister.setText("تسجيل");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("first_name", firstNameHolder);
                Params.put("last_name", lastNameHolder);
                Params.put("phone_number", phoneHolder);
                Params.put("password", passwordHolder);
                Params.put("email", emailHolder);
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

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
            startActivity(new Intent(RegisterActivity.this, ShowCartActivity.class));
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