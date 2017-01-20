package udacity.com.capstone.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import udacity.com.capstone.BaseFragment;
import udacity.com.capstone.R;
import udacity.com.capstone.activities.HomeActivity;
import udacity.com.capstone.adapters.RecordAdapter;
import udacity.com.capstone.data.Record;
import udacity.com.capstone.data.provider.RecordContract;
import udacity.com.capstone.databinding.FragmentReminderBinding;

public class ReminderListFragment extends BaseFragment implements RecordAdapter.MedialPlayClickListener, SearchView.OnQueryTextListener {

    private FragmentReminderBinding mBinding;

    public static ReminderListFragment newInstance() {
        ReminderListFragment fragment = new ReminderListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.no_data);
        setHasOptionsMenu(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.listView.setLayoutManager(layoutManager);
        //insertRecordingData();
        fillList();
        return mBinding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
    }


    private void insertRecordingData() {
//        fillList();
//        Log.d(getClass().getSimpleName(), "----------");
        ContentValues values = new ContentValues();
        values.clear();
        values.put(RecordContract.NAME, "wallet");
        values.put(RecordContract.AUDIO_PATH, "/file/0/emulated/wallet.3gp");
        getActivity().getContentResolver().insert(RecordContract.CONTENT_URI, values);

        values = new ContentValues();
        values.clear();
        values.put(RecordContract.NAME, "car key");
        values.put(RecordContract.AUDIO_PATH, "/file/0/emulated/car.3gp");
        getActivity().getContentResolver().insert(RecordContract.CONTENT_URI, values);
//
//        // cr.delete(EarthquakeProvider.CONTENT_URI, "_id=?", String.valueOf(_id)));
//        fillList();
//        Log.d(getClass().getSimpleName(), "----------");
//        int re = getActivity().getContentResolver().delete(RecordContract.CONTENT_URI, "name=?", new String[]{"wallet"});
//        Log.d(getClass().getSimpleName(), "re " + re);
//        Log.d(getClass().getSimpleName(), "----------");
//        fillList();


    }

    public void fillList() {
//        Cursor c = getActivity().getContentResolver().query(RecordContract.CONTENT_URI, null, null, null, null);
//        if (c != null) {
//            c.moveToFirst();
//            do {
//                for (int i = 0; i < c.getCount(); i++) {
//                    Log.d(getClass().getSimpleName(), c.getColumnName(i) + " : " + c.getString(i));
//                }
//            } while (c.moveToNext());
//            c.close();
//
//        }
        ArrayList<Record> items = new ArrayList<>();

        Cursor cursor = getActivity().getContentResolver().query(RecordContract.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            try {
                int columne_one = cursor.getColumnIndex(RecordContract.NAME);
                int columne_two = cursor.getColumnIndex(RecordContract.AUDIO_PATH);
                while (cursor.moveToNext()) {

                    Record record = new Record(cursor.getString(columne_one), cursor.getString(columne_two));
                    Log.d(getClass().getSimpleName(), "er " + record.getName() + ":" + record.getAudioPath());
                    items.add(record);
                }
            } finally {
                cursor.close();
                Log.d(getClass().getSimpleName(), "cursor closed");
            }
        }
        mBinding.setSize(items.size());
        RecordAdapter adapter = new RecordAdapter(items, ReminderListFragment.this);
        mBinding.listView.setAdapter(adapter);

    }


    public void playPath(String path) {
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            ((HomeActivity) getActivity()).showSnackbar(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayButtonClick(String path) {
        playPath(path);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ((RecordAdapter) mBinding.listView.getAdapter()).getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((RecordAdapter) mBinding.listView.getAdapter()).getFilter().filter(newText);
        return false;
    }
}
