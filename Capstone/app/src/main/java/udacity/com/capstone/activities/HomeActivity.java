package udacity.com.capstone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.ExtractedText;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.com.capstone.R;
import udacity.com.capstone.RecordingActivity;
import udacity.com.capstone.fragments.ReminderListFragment;

/**
 * A login screen that offers login via email/password.
 */
public class HomeActivity extends BaseDemoActivity {

    @BindView(R.id.toolbar)
    public Toolbar mToolBar;
    @BindView(R.id.coordiantelayout)
    CoordinatorLayout coordinatorLayout;
    private static final int EXTRA_REQUEST_CODE = 234;
    ReminderListFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        fragment = ReminderListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RecordingActivity.class);
                startActivityForResult(intent, EXTRA_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onPause() {
        super.onPause();
        disConnectClient();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_backup) {
            showLoadingDialog();
            connectClient();
        }
        else if (item.getItemId() == R.id.action_tutorial) {
            Intent intent = new Intent(this, IntroductionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public  void showSnackbar(String msg){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        hideLoadingDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EXTRA_REQUEST_CODE && resultCode == RESULT_OK) {
            fragment.fillList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

