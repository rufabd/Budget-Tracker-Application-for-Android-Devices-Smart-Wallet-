package com.example.app_dev_money_tracking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class PlannedPaymentsAdapter extends RecyclerView.Adapter<PlannedPaymentsAdapter.ViewHolder> {
    Context context;
    List<PlannedPaymentsModel> paymentsList;
    RecyclerView rvPayments;
    final View.OnClickListener onClickListener = new MyOnClickListener();
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView note;
        TextView amount;
        TextView status;
        TextView date;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tv_plannedPaymentList_title);
            note = itemView.findViewById(R.id.tv_plannedPaymentList_note);
            amount = itemView.findViewById(R.id.tv_plannedPaymentList_amount);
            status = itemView.findViewById(R.id.tv_plannedPaymentList_status);
            date = itemView.findViewById(R.id.tv_plannedPaymentList_date);
            image = itemView.findViewById(R.id.tv_plannedPaymentList_Image);
        }
    }

    public PlannedPaymentsAdapter(Context context, List<PlannedPaymentsModel> paymentsList, RecyclerView rvPayments) {
        this.context = context;
        this.paymentsList = paymentsList;
        this.rvPayments = rvPayments;
    }

    @NonNull
    @Override
    public PlannedPaymentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.planned_single_item, parent, false);
        view.setOnClickListener(onClickListener);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlannedPaymentsAdapter.ViewHolder holder, int position) {
        Database db = new Database(context);
        PlannedPaymentsModel plannedPayment = paymentsList.get(position);
        holder.categoryName.setText(db.getCategoryById(plannedPayment.getCategoryId()).getCategoryName());
        holder.image.setBackgroundResource(db.getCategoryById(plannedPayment.getCategoryId()).getCategoryImg());
        holder.note.setText(plannedPayment.getNote());
        holder.amount.setText(plannedPayment.getCurrency() + " " + String.valueOf(plannedPayment.getAmount()));
        holder.status.setText(plannedPayment.getStatus());
        if(holder.status.getText().toString() == "Done") {
            holder.status.setTextColor(Color.GREEN);
        }
        holder.date.setText(plannedPayment.getDate());
    }

    @Override
    public int getItemCount() {
        return paymentsList == null ? 0 :  paymentsList.size();
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            int itemPosition = rvPayments.getChildLayoutPosition(view);
//            String itemString = String.valueOf(paymentsList.get(itemPosition).getId());
//            Database db = new Database(context);
//            new AlertDialog.Builder(context).setTitle("Confirmation")
//                    .setMessage("Would you like to remove item?")
//                    .setPositiveButton("Yes",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    boolean deleted = db.deletePlanned(itemString);
//                                    if(deleted == true) {
//                                        paymentsList.remove(itemPosition);
//                                        rvPayments.removeViewAt(itemPosition);
//                                        notifyItemRemoved(itemPosition);
//                                        notifyItemChanged(itemPosition, paymentsList.size());
//                                        notifyDataSetChanged();
//                                        Toast.makeText(context, "Item was deleted", Toast.LENGTH_SHORT).show();
//
//                                    } else {
//                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    })
//                    .create()
//                    .show();
        }
    }
}