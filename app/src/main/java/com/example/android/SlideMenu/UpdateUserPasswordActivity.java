package com.example.android.SlideMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.android.Cart.ShowCartActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.LoginSystem.ForgetPasswordActivity;
import com.example.android.LoginSystem.LoginActivity;
import com.example.android.R;
import com.example.android.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class UpdateUserPasswordActivity extends AppCompatActivity {

    private myDbAdapter helper;
    private RequestQueue queue;
    private SessionManager session;
    private TextInputEditText editTextOldPassword;
    private TextInputEditText editTextNewPassword;
    private TextInputEditText editTextConfirmNewPassword;
    private TextInputLayout layoutOldPassword;
    private TextInputLayout layoutNewPassword;
    private TextInputLayout layoutConfirmNewPassword;
    private Button btnSubmit;
    private Boolean CheckEditText;
    private String oldPasswordHolder;
    private String newPasswordHolder;
    private String confirmNewPasswordHolder;
    private ProgressBar progressBarUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_password);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("تعديل كلمة المرور");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(UpdateUserPasswordActivity.this, LoginActivity.class));
            finish();
        }

        helper = new myDbAdapter(this);
        queue = Volley.newRequestQueue(this);
        progressBarUpdatePassword = findViewById(R.id.progressBarUpdatePassword);
        editTextOldPassword = findViewById(R.id.editText_updatePassword_oldPassword);
        editTextNewPassword = findViewById(R.id.editText_updatePassword_newPassword);
        editTextConfirmNewPassword = findViewById(R.id.editText_updatePassword_confirmNewPassword);
        layoutOldPassword = findViewById(R.id.layout_updatePassword_oldPassword);
        layoutNewPassword = findViewById(R.id.layout_updatePassword_newPassword);
        layoutConfirmNewPassword = findViewById(R.id.layout_updatePassword_confirmNewPassword);
        Typeface font = ResourcesCompat.getFont(this, R.font.janna_regular);
        layoutOldPassword.setTypeface(font);
        layoutNewPassword.setTypeface(font);
        layoutConfirmNewPassword.setTypeface(font);
        btnSubmit = findViewById(R.id.btn_updatePassword_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateUserPasswordActivity.this);
                    // Setting Dialog Title
                    alertDialog.setTitle("تأكيد تعديل كلمة المرور");
                    // Setting Dialog Message
                    alertDialog.setMessage("هل أنت متأكد من أنك تريد تغيير كلمة المرور الخاصة بك ؟");
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("متأكد", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            btnSubmit.setText("");
                            btnSubmit.setEnabled(false);
                            progressBarUpdatePassword.setVisibility(View.VISIBLE);
                            Handler handle = new Handler();
                            handle.postDelayed(new Runnable() {
                                public void run() {
                                    UpdatePassword();
                                }
                            }, 1000);
                        }
                    });
                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }
            }
        });

        editTextOldPassword.requestFocus();
        editTextOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextConfirmNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editTextOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editTextOldPassword.setError(null);
                layoutOldPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });
        editTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editTextNewPassword.setError(null);
                layoutNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });
        editTextConfirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editTextConfirmNewPassword.setError(null);
                layoutConfirmNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.MULTIPLY);
            }
        });

    }


    private void UpdatePassword() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            Toast.makeText(getApplicationContext(), "تم تحديث كلمة المرور بنجاح", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء التأكد من كلمة المرور الحالية", Toast.LENGTH_LONG).show();
                            progressBarUpdatePassword.setVisibility(View.GONE);
                            btnSubmit.setEnabled(true);
                            btnSubmit.setText("حفظ كلمة المرور");
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                btnSubmit.setEnabled(true);
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBarUpdatePassword.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                btnSubmit.setText("حفظ كلمة المرور");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId() + "");
                Params.put("new_password", newPasswordHolder);
                Params.put("current_password", oldPasswordHolder);
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void CheckEditTextIsEmptyOrNot() {

        oldPasswordHolder = editTextOldPassword.getText().toString();
        newPasswordHolder = editTextNewPassword.getText().toString();
        confirmNewPasswordHolder = editTextConfirmNewPassword.getText().toString();
        int numDigits = getNumberDigits(newPasswordHolder);
        CheckEditText = true;

        if (TextUtils.isEmpty(oldPasswordHolder)) {
            CheckEditText = false;
            layoutOldPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextOldPassword.setError("أدخل كلمة المرور الحالية");
        }
        if (TextUtils.isEmpty(confirmNewPasswordHolder)) {
            CheckEditText = false;
            layoutConfirmNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextConfirmNewPassword.setError("أدخل تأكيد كلمة المرور الجديدة");
        } else if (!newPasswordHolder.equals(confirmNewPasswordHolder)) {
            CheckEditText = false;
            layoutConfirmNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextConfirmNewPassword.setError("تأكيد كلمة المرور غير متطابق");
        }
        if (TextUtils.isEmpty(newPasswordHolder)) {
            CheckEditText = false;
            layoutNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextNewPassword.setError("أدخل كلمة المرور الجديدة");
        } else if (newPasswordHolder.length() < 8) {
            CheckEditText = false;
            layoutNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextNewPassword.setError(" يجب أن يكون طول كلمة المرور ٨ خانات أو أكثر");
        } else if (numDigits < 0) {
            CheckEditText = false;
            layoutNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextNewPassword.setError("يجب أن تحتوي كلمة المرور على رقم واحد على الأقل");
        } else if (numDigits == newPasswordHolder.length()) {
            CheckEditText = false;
            layoutNewPassword.setPasswordVisibilityToggleTintMode(PorterDuff.Mode.CLEAR);
            editTextNewPassword.setError("يجب أن تحتوي كلمة المرور على حرف واحد على الأقل");
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

    public void remember(View view) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
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
            startActivity(new Intent(UpdateUserPasswordActivity.this, ShowCartActivity.class));
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
