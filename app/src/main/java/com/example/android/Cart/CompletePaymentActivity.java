package com.example.android.Cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.android.Category.MainCategoryActivity;
import com.example.android.DataBase.myDbAdapter;
import com.example.android.LoginSystem.LoginActivity;
import com.example.android.R;
import com.example.android.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class CompletePaymentActivity extends AppCompatActivity {

    private RequestQueue queue;
    private myDbAdapter helper;
    private List<CartItem> cartItemList;
    private Double totalPriceToProductWithVoDeliveryFree = 0.0;
    private Boolean ProductWithVoDeliveryFreeFlag = false;
    private String shipmentReference;
    private Button replacePointsBtn;
    private Boolean isUserWantToReplacePoints = false;
    private TextView textViewShowPoints;
    private LinearLayout linearLayoutShowPoints;
    private LinearLayout linearLayoutPointsDiscounts;
    private TextView textViewPointsDiscounts;
    private int pointsHolder = 0;
    private int replacedPoints = 0;
    private Double discountPoint = 0.0;
    private Button addressBtn;
    private Button paymentTypeBtn;
    private EditText editTextCoupon;
    private Button checkCouponBtn;
    private Button cancelCouponBtn;
    private Button submitBtn;
    private String addressHolder;
    private String paymentTypeHolder = "";
    private Boolean CheckEditText;
    private AlertDialog alertDialogChoosePaymentType;
    private SessionManager session;
    private AlertDialog alertDialogChooseAddressName;
    private int[] addressId;
    private String[] addressName;
    private Boolean getUserAddressCompleted = false;
    private TextView textViewTotal;
    private TextView textViewShippingFees;
    private TextView textViewCashOnDeliveryFees;
    private TextView textViewVAT;
    private TextView textViewTotalAfterDiscount;
    private TextView textViewBill;
    private TextView textViewTotalWithoutVat;
    private LinearLayout linearLayoutBill;
    private LinearLayout linearLayoutCashOnDelivery;
    private Double cashOnDeliveryFees = 00.00;
    private Double minFreeShipping = 00.00;
    private Double shippingFees = 00.00;
    private Double VAT = 00.00;
    private Double pointsChangeMoney = 00.00;
    private Double totalPriceHolder = 00.00;
    private Double totalWithOutVATHolder = 00.00;
    private Double totalAfterDiscountHolder = 00.00;
    private Double VATHolder = 00.00;
    private Double BillHolder = 00.00;
    // Coupon Variable
    private boolean isValidCoupon = false;
    private int couponId;
    private Double couponValue;
    private Double minValue;
    private int couponPercentage;
    private Double couponFinalValue = 0.0;
    private String couponType;
    private LinearLayout linearLayoutCoupon;
    private TextView textViewCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_payment);

        //Adding a tool bar with button Back
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        TextView Title = myToolbar.findViewById(R.id.toolbar_title);
        Title.setText("إتمام الدفع");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);


        queue = Volley.newRequestQueue(this);
        helper = new myDbAdapter(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(CompletePaymentActivity.this, LoginActivity.class));
            finish();
        }

        getUserAddress();

        textViewTotal = findViewById(R.id.textViewCompletePaymentTotal);
        textViewShippingFees = findViewById(R.id.textViewCompletePaymentShippingFees);
        textViewCashOnDeliveryFees = findViewById(R.id.textViewCompletePaymentCashOnDeliveryFees);
        textViewVAT = findViewById(R.id.textViewCompletePaymentVAT);
        textViewTotalAfterDiscount = findViewById(R.id.textViewCompletePaymentTotalAfterDiscount);
        textViewBill = findViewById(R.id.textViewCompletePaymentBill);
        textViewTotalWithoutVat = findViewById(R.id.textViewCompletePaymentTotalWithoutVat);
        linearLayoutCashOnDelivery = findViewById(R.id.linearLayoutCompletePaymentCashOnDelivery);
        linearLayoutBill = findViewById(R.id.linearLayoutCompletePayment);
        linearLayoutCoupon = findViewById(R.id.linearLayoutCompletePaymentCoupon);
        textViewCoupon = findViewById(R.id.textViewCompletePaymentCoupon);

        getCartInformation();
        shipmentReference = "android" + session.getUserId() + "" + System.currentTimeMillis();

        totalPriceHolder = this.getIntent().getDoubleExtra("total", 0);
        cartItemList = new ArrayList<>();
        replacePointsBtn = findViewById(R.id.buttonCompletePaymentReplacePoints);
        textViewShowPoints = findViewById(R.id.editTextCompletePaymentShowPoints);
        linearLayoutShowPoints = findViewById(R.id.linearLayoutShowPoints);
        textViewPointsDiscounts = findViewById(R.id.textViewCompletePaymentPointsDiscounts);
        linearLayoutPointsDiscounts = findViewById(R.id.linearLayoutCompletePaymentPointsDiscounts);
        replacePointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pointsHolder < pointsChangeMoney) {
                    Toast.makeText(CompletePaymentActivity.this, "الحد الأدنى لاستبدال النقاط هو " + pointsChangeMoney + " نقطة", Toast.LENGTH_LONG).show();
                } else if (totalPriceHolder < (pointsHolder / pointsChangeMoney)) {
                    replacedPoints = pointsHolder - totalPriceHolder.intValue() * pointsChangeMoney.intValue();
                    isUserWantToReplacePoints = true;
                    textViewShowPoints.setText("0 نقطة");
                    discountPoint = (Double.valueOf(replacedPoints) / pointsChangeMoney);
                    linearLayoutPointsDiscounts.setVisibility(View.VISIBLE);
                    textViewPointsDiscounts.setText(String.format(Locale.CANADA,"%.2f", discountPoint) + " ر.س");
                    calculateBill();
                } else {
                    replacedPoints = pointsHolder;
                    isUserWantToReplacePoints = true;
                    textViewShowPoints.setText("0 نقطة");
                    discountPoint = (Double.valueOf(replacedPoints) / pointsChangeMoney);
                    linearLayoutPointsDiscounts.setVisibility(View.VISIBLE);
                    textViewPointsDiscounts.setText(String.format(Locale.CANADA,"%.2f", discountPoint) + " ر.س");
                    calculateBill();
                }

            }
        });

        getPoints();

        editTextCoupon = findViewById(R.id.editTextCompletePaymentCoupon);
        checkCouponBtn = findViewById(R.id.buttonCompletePaymentCheckCoupon);
        cancelCouponBtn = findViewById(R.id.buttonCompletePaymentCancelCoupon);
        paymentTypeBtn = findViewById(R.id.buttonCompletePaymentType);
        submitBtn = findViewById(R.id.buttonSubmit);
        addressBtn = findViewById(R.id.buttonCompletePaymentAddress);
        addressBtn.setFocusable(false);
        addressBtn.setClickable(true);
        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getUserAddressCompleted) {
                    if (addressId.length == 0) {
                        addressBtn.setClickable(false);
                        addressBtn.setText("لا يوجد لديك عنوان، قم بإضافة عنوان أولاً");

                    } else {
                        CreateAlertDialogWithRadioButtonGroup();
                    }
                }
            }
        });
        paymentTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePaymentTypeAlertDialogWithRadioButtonGroup();
            }
        });
        checkCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextCoupon.getText().toString().isEmpty()) {
                    editTextCoupon.setError("قم بإدخال الكوبون");
                    Toast.makeText(getApplicationContext(), "قم بإدخال الكوبون", Toast.LENGTH_LONG).show();
                } else {
                    getCouponInfo(editTextCoupon.getText().toString());
                }
            }
        });
        cancelCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponId = 0;
                couponType = "";
                couponPercentage = 0;
                couponValue = 0.0;
                minValue = 0.0;
                isValidCoupon = false;
                couponFinalValue = 0.0;
                editTextCoupon.setText("");
                checkCouponBtn.setVisibility(View.VISIBLE);
                cancelCouponBtn.setVisibility(View.GONE);
                linearLayoutCoupon.setVisibility(View.GONE);
                textViewCoupon.setText(couponFinalValue + " ر.س");
                calculateBill();

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking whether EditText is Empty or Not
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    submitBtn.setEnabled(false);
                    if (isUserWantToReplacePoints) replacePoints();
                    insertNewShipmentReference();
                }
            }
        });

        textViewTotal = findViewById(R.id.textViewCompletePaymentTotal);
        textViewShippingFees = findViewById(R.id.textViewCompletePaymentShippingFees);
        textViewCashOnDeliveryFees = findViewById(R.id.textViewCompletePaymentCashOnDeliveryFees);
        textViewVAT = findViewById(R.id.textViewCompletePaymentVAT);
        textViewBill = findViewById(R.id.textViewCompletePaymentBill);
        linearLayoutCashOnDelivery = findViewById(R.id.linearLayoutCompletePaymentCashOnDelivery);
        linearLayoutBill = findViewById(R.id.linearLayoutCompletePayment);
        linearLayoutCoupon = findViewById(R.id.linearLayoutCompletePaymentCoupon);
        textViewCoupon = findViewById(R.id.textViewCompletePaymentCoupon);


    }

    private void replacePoints() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REPLACE_POINTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            Log.e("i", "تم استبدال النقاط بنجاح - " + replacedPoints);
                            //   Toast.makeText(CompletePaymentActivity.this, "تم استبدال النقاط بنجاح", Toast.LENGTH_LONG).show();
//                            replacedPoints = Integer.valueOf(pointsHolder);
                            pointsHolder = pointsHolder - replacedPoints;
                        } else {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CompletePaymentActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId()+"");
                Params.put("number_points", replacedPoints+"");
                return Params;
            }
        };
        queue.add(stringRequest);
    }

    private void getPoints() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_POINTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            if (response.equals("")) {
                                Log.e("i", "Empty");
                                pointsHolder = 0;
                                linearLayoutShowPoints.setVisibility(View.GONE);
                            } else {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                jsonObject = jsonArray.getJSONObject(0);
                                pointsHolder = jsonObject.getInt("points");
                                if (pointsHolder == 0) {
                                    linearLayoutShowPoints.setVisibility(View.GONE);
                                } else {
                                    textViewShowPoints.setText(pointsHolder + " نقطة");
                                    linearLayoutShowPoints.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CompletePaymentActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
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

    public void getCartInformation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_CART_INFORMATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                Log.e("i", "Empty");

                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                CartItem item;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    item = new CartItem();

                                    jsonObject = jsonArray.getJSONObject(i);

                                    //Adding Product Name.
                                    item.setName(jsonObject.getString("name"));

                                    //Adding Product Description.
                                    item.setDescription(jsonObject.getString("description"));

                                    //Adding Product Price.
                                    item.setPrice(jsonObject.getDouble("price"));

                                    //Adding Product Weight.
                                    item.setWeight(jsonObject.getDouble("weight"));

                                    //Adding Product Image Path.
                                    item.setImagePath(jsonObject.getString("img_path"));

                                    //Adding Product Category Id.
                                    item.setCatId(jsonObject.getInt("cat_id"));

                                    //Adding Cart ID.
                                    item.setId(Integer.valueOf(jsonObject.getString("ID")));

                                    //Adding Product ID.
                                    item.setProductId(jsonObject.getInt("product_id"));

                                    //Adding Product ID.
                                    item.setUserId(jsonObject.getInt("user_ID"));

                                    //Adding Product Quantity.
                                    item.setQuantity(jsonObject.getInt("quantity"));

                                    //Adding Product Is Delivery Free.
                                    if (jsonObject.getString("is_delivery_free").equals("yes")) {
                                        item.setDeliveryFree(true);
                                        ProductWithVoDeliveryFreeFlag = true;
                                    } else {
                                        item.setDeliveryFree(false);
                                        totalPriceToProductWithVoDeliveryFree += (item.getPrice() * item.getQuantity());
                                    }

                                    cartItemList.add(item);
                                }

                                getSettings();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CompletePaymentActivity.this, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
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

    private void CheckEditTextIsEmptyOrNot() {

        CheckEditText = true;
        if (TextUtils.isEmpty(addressHolder)) {
            CheckEditText = false;
            addressBtn.setError("اختر العنوان");
        }
        if (TextUtils.isEmpty(paymentTypeHolder)) {
            CheckEditText = false;
            paymentTypeBtn.setError("اختر طريقة الدفع");
        }

    }

    private void CreatePaymentTypeAlertDialogWithRadioButtonGroup() {


        AlertDialog.Builder builder = new AlertDialog.Builder(CompletePaymentActivity.this);

        builder.setTitle("قم باختيار طريقة الدفع");

        builder.setSingleChoiceItems(R.array.paymentType, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        paymentTypeHolder = "ordinary";
                        paymentTypeBtn.setText("دفع عند الاستلام");
                        textViewCashOnDeliveryFees.setText(String.format(Locale.CANADA,"%.2f", cashOnDeliveryFees) + " ر.س");
                        linearLayoutCashOnDelivery.setVisibility(View.VISIBLE);
                        calculateBill();
                        break;
                    case 1:
                        paymentTypeHolder = "mada";
                        paymentTypeBtn.setText("مدى");
                        calculateBill();
                        break;
                    case 2:
                        paymentTypeHolder = "visa";
                        paymentTypeBtn.setText("visa");
                        calculateBill();
                        break;
                }

                alertDialogChoosePaymentType.dismiss();
                paymentTypeBtn.setError(null);
            }
        });
        alertDialogChoosePaymentType = builder.create();
        alertDialogChoosePaymentType.show();

    }

    private void calculateBill() {
        totalWithOutVATHolder = totalPriceHolder / (1+VAT);
        totalAfterDiscountHolder = totalWithOutVATHolder - couponFinalValue - discountPoint;
        VATHolder =  totalAfterDiscountHolder * VAT;
        if (paymentTypeHolder.equals("ordinary"))
            BillHolder = ((totalWithOutVATHolder + VATHolder + cashOnDeliveryFees + shippingFees) - couponFinalValue) - discountPoint;
        else
            BillHolder = ((totalWithOutVATHolder + VATHolder + shippingFees) - couponFinalValue) - discountPoint;
        textViewTotalAfterDiscount.setText(String.format(Locale.CANADA,"%.2f", totalAfterDiscountHolder) + " ر.س");
        textViewTotalWithoutVat.setText(String.format(Locale.CANADA,"%.2f", totalWithOutVATHolder) + " ر.س");
        textViewVAT.setText(String.format(Locale.CANADA,"%.2f", VATHolder) + " ر.س");
        textViewTotal.setText(String.format(Locale.CANADA,"%.2f", totalPriceHolder) + " ر.س");
        textViewBill.setText(String.format(Locale.CANADA,"%.2f", BillHolder) + " ر.س");
        linearLayoutBill.setVisibility(View.VISIBLE);
    }

    private void CreateAlertDialogWithRadioButtonGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CompletePaymentActivity.this);

        builder.setTitle("قم باختيار العنوان الخاص بك :");
        builder.setSingleChoiceItems(addressName, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                addressHolder = String.valueOf(addressId[item]);
                alertDialogChooseAddressName.dismiss();
                addressBtn.setError(null);
                addressBtn.setText("" + addressName[item]);
            }
        });
        alertDialogChooseAddressName = builder.create();
        alertDialogChooseAddressName.show();

    }

    private void getUserAddress() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                addressId = new int[0];
                                addressName = new String[0];
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                addressId = new int[jsonArray.length()];
                                addressName = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    addressId[i] = jsonObject.getInt("ID");
                                    addressName[i] = jsonObject.getString("address_name");
                                }

                            }
                            getUserAddressCompleted = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
                addressId = new int[0];
                addressName = new String[0];
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

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getSettings() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_SETTINGS,
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
                                    if (jsonObject.getString("type").equals("cashOnDeliveryFees")) {
                                        cashOnDeliveryFees = jsonObject.getDouble("value");
                                    }
                                    if (jsonObject.getString("type").equals("minFreeShipping")) {
                                        minFreeShipping = jsonObject.getDouble("value");
                                    }
                                    if (jsonObject.getString("type").equals("shippingFees")) {
                                        shippingFees = jsonObject.getDouble("value");
                                    }
                                    if (jsonObject.getString("type").equals("tax")) {
                                        VAT = jsonObject.getDouble("value");
                                    }
                                    if (jsonObject.getString("type").equals("points_change_money")) {
                                        pointsChangeMoney = jsonObject.getDouble("value");
                                    }
                                }
                            }
                            checkDeliveryFree();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void checkDeliveryFree() {
        if (ProductWithVoDeliveryFreeFlag){
            shippingFees = 0.0;
            textViewShippingFees.setText("00.00 (الشحن مجاني)");
        } else if ((totalPriceToProductWithVoDeliveryFree != 0 ) && (totalPriceToProductWithVoDeliveryFree < minFreeShipping)) {
            textViewShippingFees.setText(shippingFees + " ر.س");
        } else if ((totalPriceToProductWithVoDeliveryFree != 0 ) && (totalPriceToProductWithVoDeliveryFree >= minFreeShipping)) {
            shippingFees = 0.0;
            textViewShippingFees.setText("00.00 (الشحن مجاني)");
        } else {
            shippingFees = 0.0;
            textViewShippingFees.setText("00.00 (الشحن مجاني)");
        }
    }

    private void getCouponInfo(final String coupon) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_COUPON_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            if (response.length() == 0) {
                            } else if (response.equals("false")) {
                                Toast.makeText(getApplicationContext(), "الكوبون المدخل غير صحيح", Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                jsonObject = jsonArray.getJSONObject(0);
                                couponId = jsonObject.getInt("ID");
                                couponType = jsonObject.getString("type");
                                if (couponType.equals("value")) {
                                    couponValue = jsonObject.getDouble("value_co");
                                    minValue = jsonObject.getDouble("min_shopping");
                                    couponPercentage = 0;
                                } else {
                                    couponPercentage = jsonObject.getInt("percantage");
                                    couponValue = 0.0;
                                    minValue = 0.0;
                                }
                                isValidCoupon = true;
                                calculateCoupon();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("coupon_code", coupon);
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void calculateCoupon() {
        if (couponType.equals("value")) {
            if (totalPriceHolder < minValue) {
                Toast.makeText(getApplicationContext(), "يجب أن تتجاوز مشترياتك مبلغ " + minValue + " ر.س ليتم تفعيله", Toast.LENGTH_LONG).show();
                couponFinalValue = 0.0;
                return;
            } else {
                couponFinalValue = couponValue;
            }
        } else {
            couponFinalValue = ((totalPriceHolder / (1+VAT)) * couponPercentage) / 100;
        }
        checkCouponBtn.setVisibility(View.GONE);
        cancelCouponBtn.setVisibility(View.VISIBLE);
        linearLayoutCoupon.setVisibility(View.VISIBLE);
        textViewCoupon.setText(String.format(Locale.CANADA,"%.2f", couponFinalValue) + " ر.س");
        calculateBill();
    }


    private void insertNewShipmentReference() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_INSERT_NEW_SHIPMENT_REFERENCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            insertNewOrder();
                        } else {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("shipment_ref", shipmentReference);
                Params.put("user_id", session.getUserId()+"");
                Params.put("coupon_value", couponFinalValue.toString());
                Params.put("copoun_id", couponId+"");
                Params.put("numberOfPoints", replacedPoints+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void insertNewOrderWebService(final int productId, final int productQuantity, final Double productPrice, final String paymentType, final String paymentDocument, final String shipmentRef, final String addressId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_INSERT_NEW_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("your request was successfully submitted")) {
//                            Log.e("i", "insertNewOrderWebService True");
                        } else {
//                            Log.e("i", "insertNewOrderWebService false");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("product_id", productId+"");
                Params.put("user_id", session.getUserId()+"");
                Params.put("quantity", productQuantity+"");
                Params.put("price", productPrice+"");
                Params.put("payment_type", paymentType);
                Params.put("payment_document", paymentDocument);
                Params.put("shipment_ref", shipmentRef);
                Params.put("address_id", addressId);
                return Params;
            }
        };
        queue.add(stringRequest);
    }

    // This method finish this activity and back to previous activity Main Activity (with saved status) when back up button pressed.
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertNewOrder() {
        for (int i = 0; i < cartItemList.size(); i++) {
            insertNewOrderWebService(cartItemList.get(i).getProductId(), cartItemList.get(i).getQuantity(), (cartItemList.get(i).getQuantity() * cartItemList.get(i).getPrice()), paymentTypeHolder, "", shipmentReference, addressHolder);
        }
        //generateInvoice();
        addPoints();
    }

    private void generateInvoice() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GENERATE_INVOICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("")) {
//                            Log.e("i", "generateInvoice false");
                        } else {
//                            Log.e("i", "generateInvoice True");
                            Toast.makeText(getApplicationContext(), "تم تسجيل طلبك بنجاح ... نشكر لك تسوقك مع أتماري", Toast.LENGTH_LONG).show();
                            helper.deleteAll(session.getUserId()); // clear cart.
                        }
                        startActivity(new Intent(CompletePaymentActivity.this, MainCategoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("shipment_ref", shipmentReference);
                Params.put("user_id", session.getUserId()+"");
                return Params;
            }
        };
        queue.add(stringRequest);
    }

    private void addPoints() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_POINTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
//                            Log.e("i", "Add Points True");
                            Toast.makeText(CompletePaymentActivity.this, "تمت إضافة " + totalAfterDiscountHolder.intValue() + " إلى رصيدك من النقاط", Toast.LENGTH_LONG).show();
                        } else {
//                            Log.e("i", "Add Points false");
                        }
                        generateInvoice();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("user_id", session.getUserId()+"");
                Params.put("total_price", totalAfterDiscountHolder.intValue()+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}