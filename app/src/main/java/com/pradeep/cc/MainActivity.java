package com.pradeep.cc;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.pradeep.cc.budget.BudgetManager;
import com.pradeep.cc.settings.SettingsActivity;
import com.pradeep.cc.transaction.TransactionAdapter;
import com.pradeep.cc.transaction.TransactionManager;
import com.pradeep.cc.transaction.Transactions;
import com.pradeep.cc.util.Date;
import com.pradeep.cc.util.PersistentStore;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.DateListener {

    private static final int REQUEST_SMS_PERMISSION = 101;
    private static final String START_DATE_PICKER = "START_DATE_PICKER";
    private static final String END_DATE_PICKER = "END_DATE_PICKER";
    private static final int JOB_ID = 1988;
    private TransactionManager transactionManager;
    private TextView lblTotalAmount;
    private RecyclerView recyclerView;
    private Date startDate;
    private Date endDate;
    private String currentTag;
    private DatePickerFragment datePickerFragment;
    private BudgetManager.BudgetAlarm budgetAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblTotalAmount = findViewById(R.id.lbl_total_amount);
        recyclerView = findViewById(R.id.lst_transaction);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionManager = new TransactionManager(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(SmsReceiver.CC_MESSAGE_RECEIVED));
        setBudgetAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasSmsPermission()) {
            readSms(transactionManager);
        }
    }

    private void readSms(TransactionManager transactionManager) {
        transactionManager.insertAllTransactions("HDFC", PersistentStore.getStore().getLastAccessedTime(this));
        PersistentStore.getStore().putLastAccessedTime(this, String.valueOf(System.currentTimeMillis()));
        showTransactions(transactionManager);
    }

    private void showTransactions(TransactionManager transactionManager) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int billingCycleDay = Integer.parseInt(prefs.getString(getString(R.string.key_billing_cycle_date), "1"));
        Transactions transactions = transactionManager.getTransactionsForBillingCycle(billingCycleDay);
        Log.i("@@@", "Total amount: " + transactionManager.getTransactionTotalForBillingCycle(billingCycleDay));
        if (transactions != null) {
            updateUi(transactions);
        }
    }

    private boolean hasSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                showPermissionNotAcceptedToast();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                        REQUEST_SMS_PERMISSION);
            }

            return false;
        }

        return true;
    }


    private void showPermissionNotAcceptedToast() {
        Toast toast = Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS_PERMISSION: {
                if (grantResults.length != 2) {
                    showPermissionNotAcceptedToast();
                    return;
                }
                readSms(transactionManager);
            }
        }
    }

    @Override
    public void onDateSelected(Date date) {
        switch (currentTag) {
            case START_DATE_PICKER:
                startDate = date;
                datePickerFragment = null;
                Bundle bundle = new Bundle();
                bundle.putString(DatePickerFragment.TITLE_KEY, "End date");
                bundle.putParcelable(DatePickerFragment.COMPARISION_DATE, startDate);
                bundle.putInt(DatePickerFragment.COMPARISION_TYPE, DatePickerFragment.LESS_THEN);
                openDateRangePicker(END_DATE_PICKER, bundle);
                break;
            case END_DATE_PICKER:
                endDate = date;
                datePickerFragment = null;
                Transactions transactions = transactionManager.getTransactionByPeriod(startDate, endDate);
                updateUi(transactions);
                break;
        }
    }

    private void updateUi(Transactions transactions) {
        if (transactions != null && transactions.getTransactions().size() > 0) {
            if (recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(new TransactionAdapter(transactions.getTransactions()));
            } else {
                ((TransactionAdapter) recyclerView.getAdapter()).updateTransactions(transactions.getTransactions());
            }
            String total = new DecimalFormat("##.##").format(transactions.getExtraInformation(Transactions.TOTAL_AMOUNT));
            setTitle("From " + String.valueOf(transactions.getExtraInformation(Transactions.DURATION)));
            lblTotalAmount.setText("Rs." + total);
        }
    }

    @Override
    public void onDialogCancel() {
        startDate = null;
        endDate = null;
        datePickerFragment = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date_range:
                Bundle bundle = new Bundle();
                bundle.putString(DatePickerFragment.TITLE_KEY, "Start date");
                openDateRangePicker(START_DATE_PICKER, bundle);
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDateRangePicker(String tag, Bundle bundle) {
        if (datePickerFragment != null && !datePickerFragment.isHidden()) {
            return;
        }
        datePickerFragment = DatePickerFragment.getInstance(bundle);
        currentTag = tag;
        datePickerFragment.show(getSupportFragmentManager(), tag);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showTransactions(transactionManager);
        }
    };

    private void setBudgetAlarm(){
        if(budgetAlarm == null) {
            budgetAlarm = new BudgetManager.BudgetAlarm(this);
        }
        budgetAlarm.set();
    }
}
