package com.example.android;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class AppConfig {
    
    // Server url
    private static final String URL_SERVER = "";

    // Server Get Categories url
    public static final String URL_GET_CATEGORIES = URL_SERVER + "";

    // Server Get Product Based On Category url
    public static final String URL_GET_PRODUCT_BASED_ON_CAT = URL_SERVER + "";

    // Server Get Product Based On ID url
    public static final String URL_GET_PRODUCT_BASED_ON_ID = URL_SERVER + "";

    // Server Get Products url
    public static final String URL_GET_PRODUCTS = URL_SERVER + "";

    // Server Add To Favorite url
    public static final String URL_ADD_TO_FAVORITE = URL_SERVER + "";

    // Server Add To Cart url
    public static final String URL_ADD_TO_CART = URL_SERVER + "";

    // Server Get Cart Information url
    public static final String URL_GET_CART_INFORMATION = URL_SERVER + "";

    // Server Clear Cart url
    public static final String URL_CLEAR_CART = URL_SERVER + "";

    // Server Edit Cart url
    public static final String URL_EDIT_CART = URL_SERVER + "";

    // Server Delete From Cart url
    public static final String URL_DELETE_FROM_CART = URL_SERVER + "";

    // Server Get Favorite url
    public static final String URL_GET_FAVORITE = URL_SERVER + "";

    // Server Register User url
    public static final String URL_REGISTER_USER = URL_SERVER + "";

    // Server Login url
    public static final String URL_LOGIN = URL_SERVER + "";

    // Server Forget Password url
    public static final String URL_FORGET_PASSWORD = URL_SERVER + "";

    // Server Update Password url
    public static final String URL_UPDATE_PASSWORD = URL_SERVER + "";

    // Server Update User Profile url
    public static final String URL_UPDATE_USER_PROFILE = URL_SERVER + "";

    // Server Get User Profile url
    public static final String URL_GET_USER_PROFILE = URL_SERVER + "";

    // Server Add Address url
    public static final String URL_ADD_ADDRESS = URL_SERVER + "";

    // Server Edit Address url
    public static final String URL_EDIT_ADDRESS = URL_SERVER + "";

    // Server Delete Address url
    public static final String URL_DELETE_ADDRESS = URL_SERVER + "";

    // Server Get Address url
    public static final String URL_GET_ADDRESS = URL_SERVER + "";

    // Server Search For Product url
    public static final String URL_SEARCH_FOR_PRODUCT = URL_SERVER + "";

    // Server Get Tracking Shipment url
    public static final String URL_GET_TRACKING_SHIPMENT = URL_SERVER + "";

    // Server Get Order History url
    public static final String URL_GET_ORDER_HISTORY = URL_SERVER + "";

    // Server Cancel Order url
    public static final String URL_CANCEL_ORDER = URL_SERVER + "";

    // Server Send Message url
    public static final String URL_SEND_MESSAGE = URL_SERVER + "";

    // Server Get Points url
    public static final String URL_GET_POINTS = URL_SERVER + "";

    // Server Get Slider Images url
    public static final String URL_GET_SLIDER_IMAGES = URL_SERVER + "";

    // Server Get Similar Product Type url
    public static final String URL_GET_SIMILAR_PRODUCT_TYPE = URL_SERVER + "";

    // Server Get Related Products url
    public static final String URL_GET_RELATED_PRODUCTS = URL_SERVER + "";

    // Server Insert New Order url
    public static final String URL_INSERT_NEW_ORDER = URL_SERVER + "";

    // Server Register Device url
    public static final String URL_REGISTER_DEVICE = URL_SERVER + "";

    // Server Get Settings url
    public static final String URL_GET_SETTINGS = URL_SERVER + "";

    // Server Get Coupon Info url
    public static final String URL_GET_COUPON_INFO = URL_SERVER + "";

    // Server Insert New Shipment Reference url
    public static final String URL_INSERT_NEW_SHIPMENT_REFERENCE = URL_SERVER + "";

    // Server Generate Invoice url
    public static final String URL_GENERATE_INVOICE = URL_SERVER + "";

    // Server Add Points url
    public static final String URL_ADD_POINTS = URL_SERVER + "";

    // Server Replace Points url
    public static final String URL_REPLACE_POINTS = URL_SERVER + "";

    // Server Get Is Active url
    public static final String URL_GET_IS_ACTIVE = URL_SERVER + "";

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }


}
