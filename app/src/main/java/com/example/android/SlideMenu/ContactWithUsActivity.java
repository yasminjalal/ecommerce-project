package com.example.android.SlideMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class ContactWithUsActivity extends AppCompatActivity {

    private myDbAdapter helper;
    private SessionManager session;
    private ImageView imageViewTwitter;
    private ImageView imageViewInstagram;
    private ImageView imageViewSnapChat;
    private ProgressBar progressBarContactWithUs;
    private EditText txtSubject;
    private EditText txtEmail;
    private EditText txtMessage;
    private Button buttonSubmit;
    private RequestQueue queue;
    private String subjectHolder;
    private String emailHolder;
    private String messageHolder;
    private Boolean CheckEditText;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_with_us);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("تواصل معنا");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        helper = new myDbAdapter(this);
        session = new SessionManager(this);
        queue = Volley.newRequestQueue(this);
        progressBarContactWithUs = (ProgressBar) findViewById(R.id.progressBarContactWithUs);
        imageViewTwitter = findViewById(R.id.imageView_contactUs_twitter);
        imageViewInstagram = findViewById(R.id.imageView_contactUs_instagram);
        imageViewSnapChat = findViewById(R.id.imageView_contactUs_snapChat);
        txtSubject = findViewById(R.id.editTextContactWithUsSubject);
        txtEmail = findViewById(R.id.editTextContactWithUsEmail);
        txtMessage = findViewById(R.id.editTextContactWithUsMessage);
        buttonSubmit = findViewById(R.id.buttonContactWithUsSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    buttonSubmit.setText("");
                    buttonSubmit.setEnabled(false);
                    progressBarContactWithUs.setVisibility(View.VISIBLE);
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        public void run() {
                            SendMessage();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "قم بإكمال البيانات", Toast.LENGTH_LONG).show();
                }
            }
        });

        txtSubject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

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

    private void CheckEditTextIsEmptyOrNot() {

        subjectHolder = txtSubject.getText().toString();
        emailHolder = txtEmail.getText().toString();
        messageHolder = txtMessage.getText().toString();
        CheckEditText = true;
        if (TextUtils.isEmpty(subjectHolder)) {
            CheckEditText = false;
            txtSubject.setError("ادخل الموضوع");
        }
        if (TextUtils.isEmpty(emailHolder)) {
            CheckEditText = false;
            txtEmail.setError("ادخل البريد الإلكتروني");
        } else if (!emailHolder.trim().matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "البريد الإلكتروني غير صحيح", Toast.LENGTH_SHORT).show();
            txtEmail.setError("البريد الإلكتروني غير صحيح");
        }
        if (TextUtils.isEmpty(messageHolder)) {
            CheckEditText = false;
            txtMessage.setError("ادخل نص الرسالة");
        }
    }

    private void SendMessage() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Message could not be sent. Mailer Error: Message body empty")) {
                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        } else if (response.contains("Invalid address")) {
                            Toast.makeText(getApplicationContext(), "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "تم إرسال رسالتك بنجاح .. نشكر لك تواصلك", Toast.LENGTH_LONG).show();
                        }
                        progressBarContactWithUs.setVisibility(View.GONE);
                        buttonSubmit.setEnabled(true);
                        buttonSubmit.setText("إرسال الرسالة");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                buttonSubmit.setEnabled(true);
//                 Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                progressBarContactWithUs.setVisibility(View.GONE);
                buttonSubmit.setEnabled(true);
                buttonSubmit.setText("إرسال الرسالة");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("body",messageHolder);
                Params.put("subject",subjectHolder);
                Params.put("email",emailHolder);
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
            startActivity(new Intent(ContactWithUsActivity.this, ShowCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
