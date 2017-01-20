package udacity.com.capstone.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.com.capstone.R;
import udacity.com.capstone.data.Contract;
import udacity.com.capstone.fragments.WhatToRememberFragment;
import udacity.com.capstone.fragments.WhereItIsFragment;

/**
 * A login screen that offers login via email/password.
 */
public class RecordingActivity extends AppCompatActivity implements WhatToRememberFragment.RecordComplete {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.txt_two)
    TextView txtTwo;
    boolean isOnSecStep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setTitle(R.string.title_what);
        setBackEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                WhatToRememberFragment.newInstance()).commit();
    }


    @Override
    public void onRecordComplete(String what) {
        if (isExistsAlready(what)) {
            showErrorDialog();
            return;
        }
        txtTwo.setBackgroundResource(R.drawable.background_green);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                WhereItIsFragment.newInstance(what)).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_backup) {
          onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }



    public void showErrorDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RecordingActivity.this, R.style.AppTheme_AlertDialogStyle);

        dialog.setTitle(getString(R.string.app_name));
        dialog.setMessage(R.string.lbl_item_exists);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog dd = dialog.show();

        dd.show();

    }


    protected void setBackEnabled(boolean flag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(flag);
            getSupportActionBar().setHomeButtonEnabled(flag);
        }
    }

    private boolean isExistsAlready(String what) {
        setTitle(R.string.title_where);
        what.trim();
        Cursor cursor = getContentResolver().query(Contract.Record.uri, null, "name=?", new String[]{what}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        txtTwo.setBackgroundResource(R.drawable.background_grey);
        super.onBackPressed();
    }
}

