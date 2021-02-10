package com.example.android.TrackingShipment;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.R;

import java.util.List;

/**
 * Created by Yasmin Jalal - 2019.
 */

public class TrackingShipmentAdapter extends RecyclerView.Adapter<TrackingShipmentAdapter.TrackingShipmentHolder> {

    private Context context;
    List<TrackingShipment> trackingShipmentsList;
    //declare interface
    private OnItemClicked onClick;

    public TrackingShipmentAdapter(Context context, List<TrackingShipment> trackingShipmentsList) {
        this.trackingShipmentsList = trackingShipmentsList;
        this.context = context;
    }


    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    @Override
    public TrackingShipmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tracking_shipment_item, parent, false);
        TrackingShipmentHolder holder = new TrackingShipmentHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TrackingShipmentHolder holder, final int position) {

        final TrackingShipment shipment = trackingShipmentsList.get(position);
        holder.shipmentId.setText(shipment.getShipmentRef() + "");
        holder.shipmentDate.setText(shipment.getDateTime() + "");
        holder.shipmentInfo.setText(shipment.getInfo());

    }

    @Override
    public int getItemCount() {
        return trackingShipmentsList.size();
    }

    class TrackingShipmentHolder extends RecyclerView.ViewHolder {
        TextView shipmentId;
        TextView shipmentDate;
        TextView shipmentInfo;

        public TrackingShipmentHolder(View itemView) {
            super(itemView);
            shipmentId = (TextView) itemView.findViewById(R.id.textView_recycle_view_tracking_shipment_item_id);
            shipmentDate = (TextView) itemView.findViewById(R.id.textView_recycle_view_tracking_shipment_item_date);
            shipmentInfo = (TextView) itemView.findViewById(R.id.textView_recycle_view_tracking_shipment_item_info);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

}



