package com.pradeep.cc.transaction;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pradeep.cc.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions){
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TransactionViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_transaction, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder transactionViewHolder, int i) {
        Transaction transaction = transactions.get(i);
        transactionViewHolder.lblPlace.setText(transaction.getPlace());
        transactionViewHolder.lblDateAndTime.setText(getDateAndTime(Long.valueOf(transaction.getTimeStamp())));
        transactionViewHolder.lblAmount.setText("Rs."+transaction.getAmount());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView lblPlace;
        TextView lblDateAndTime;
        TextView lblAmount;

        public TransactionViewHolder(View view) {
            super(view);
            lblPlace = view.findViewById(R.id.lbl_place);
            lblDateAndTime = view.findViewById(R.id.lbl_time);
            lblAmount = view.findViewById(R.id.lbl_amount);
        }
    }

    public void updateTransactions(List<Transaction> transactions){
        this.transactions = transactions;
        notifyDataSetChanged();
    }
    private static String getDateAndTime(long epochTime){
        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd-MMMM HH:mm a");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        Log.i("@@@",formatted);
        return formatted;
    }
}
