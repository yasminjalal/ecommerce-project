package com.example.android.Address;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.example.android.R;
import com.example.android.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {

    private SessionManager session;
    private RequestQueue queue;
    private Context context;
    List<Address> AddressList;
    //declare interface
    private OnItemClicked onClick;

    public AddressAdapter(Context context, List<Address> AddressList) {
        this.AddressList = AddressList;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        session = new SessionManager(context);
    }


    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_address_item, parent, false);
        AddressHolder holder = new AddressHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AddressHolder holder, final int position) {

        final Address address = AddressList.get(position);
        holder.addressName.setText(address.getName());
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAddress(address.getId(), position);
            }
        });
        if (address.isDefault()) {
            holder.isDefault.setChecked(true);
        } else {
            holder.isDefault.setChecked(false);
        }
        holder.isDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isDefault.isChecked()) EditAddress(address, true, "yes", holder.isDefault);
                else EditAddress(address, false, "no", holder.isDefault);
            }

        });

    }

    @Override
    public int getItemCount() {
        return AddressList.size();
    }

    class AddressHolder extends RecyclerView.ViewHolder {
        TextView addressName;
        Switch isDefault;
        ImageView deleteIcon;


        public AddressHolder(View itemView) {
            super(itemView);
            addressName = (TextView) itemView.findViewById(R.id.textView_recycle_view_address_item_name);
            isDefault = (Switch) itemView.findViewById(R.id.switch_recycle_view_address_item_delete);
            deleteIcon = (ImageView) itemView.findViewById(R.id.imageView_recycle_view_address_item_delete);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    private void EditAddress(final Address address, final boolean isDefaultBoolean, final String isDefaultString, final Switch isDefaultSwitch) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {
                            Toast.makeText(context, "تم تعديل العنوان الافتراضي بنجاح", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                        }
                        ShowAddressActivity.reference.refresh();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("adress_id",address.getId()+"");
                Params.put("Address_1",address.getAddress1());
                Params.put("Address_2",address.getAddress2());
                Params.put("city",address.getCity());
                Params.put("post_code",address.getPostCode());
                Params.put("coutry",address.getCountry());
                Params.put("region",address.getRegion());
                Params.put("is_default",isDefaultString);
                Params.put("user_id",session.getUserId()+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

    private void DeleteAddress(final int addressId, final int position) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DELETE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) {
                                ShowAddressActivity.reference.allItemDeleted();
                            } else if (jsonArray.length() < AddressList.size()) {
                                Toast.makeText(context, "تم حذف العنوان بنجاح", Toast.LENGTH_LONG).show();
                                ShowAddressActivity.reference.setProgressBarVisible();
                                Handler handle = new Handler();
                                handle.postDelayed(new Runnable() {
                                    public void run() {
                                        AddressList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, AddressList.size());
                                        ShowAddressActivity.reference.setProgressBarGone();
                                    }
                                }, 1000);

                            } else {
                                Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> Params = new HashMap<>();
                Params.put("address_id",addressId+"");
                Params.put("user_id",session.getUserId()+"");
                return Params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();

    }

}



