package com.example.android.OrderHistory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.android.R;
import com.example.android.SessionManager;

import java.util.List;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private SessionManager session;
    private RequestQueue queue;
    private Context context;
    List<Order> OrderList;
    //declare interface
    private OnItemClicked onClick;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.OrderList = orderList;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        session = new SessionManager(context);
    }


    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_order_item, parent, false);
        OrderHolder holder = new OrderHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final OrderHolder holder, final int position) {

        final Order order = OrderList.get(position);
        holder.orderId.setText("كود الطلب: " + order.getShipmentRef());
        holder.orderDate.setText(order.getDateTime());
        holder.orderQuantity.setText(order.getQuantity() + "");
        holder.orderPrice.setText(order.getPrice() + " ر.س");
        holder.orderPaymentType.setText(order.getPaymentType());
        if (!order.getInfo().isEmpty() && !order.getInfo().equals("null") && !order.getInfo().equals(" "))
            holder.orderInfo.setText(order.getInfo());
//        holder.cancelOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CancelOrder(order.getId(), position);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return OrderList.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        TextView orderId;
        TextView orderDate;
        TextView orderQuantity;
        TextView orderPrice;
        TextView orderPaymentType;
        TextView orderInfo;
//        Button cancelOrder;

        public OrderHolder(View itemView) {
            super(itemView);
            orderId = (TextView) itemView.findViewById(R.id.textView_recycle_view_order_history_item_id);
            orderDate = (TextView) itemView.findViewById(R.id.textView_recycle_view_order_history_item_date);
            orderQuantity = (TextView) itemView.findViewById(R.id.textView_recycle_view_order_history_item_quantity);
            orderPrice = (TextView) itemView.findViewById(R.id.textView_recycle_view_order_history_item_price);
            orderPaymentType = (TextView) itemView.findViewById(R.id.textView_recycle_view_order_history_item_paymentType);
            orderInfo = (TextView) itemView.findViewById(R.id.textView_recycle_view_order_history_item_info);
//            cancelOrder = (Button) itemView.findViewById(R.id.btn_recycle_view_order_history_cancel);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }


//    private void CancelOrder(int orderId, final int position) {
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CANCEL_ORDER + "?order_id=1",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        if (response.equals("your request was successfully submitted")) {
//
//                            Toast.makeText(context, "تم إلغاء الطلب", Toast.LENGTH_LONG).show();
//                            OrderList.remove(position);
//                            notifyItemRemoved(position);
//                            notifyItemRangeChanged(position, OrderList.size());
//                            if (OrderList.size() == 0) {
//                                ShowOrdersHistoryActivity.reference.allItemDeleted();
//                            }
//                        } else {
//                            Toast.makeText(context, "لقد حدث خطأ ، الرجاء إعادة المحاولة لاحقاً", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(context, "لا يوجد اتصال بالإنترنت", Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                super.getParams();
//                Map<String, String> Params = new HashMap<>();
////                Params.put("customerID",session.getUserId());
//                return Params;
//            }
//        };
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//        queue.start();
//
//    }
}



