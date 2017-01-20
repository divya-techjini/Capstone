package udacity.com.capstone.fragments;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import udacity.com.capstone.BaseFragment;
import udacity.com.capstone.R;
import udacity.com.capstone.activities.HomeActivity;
import udacity.com.capstone.adapters.RecordAdapter;
import udacity.com.capstone.data.Contract;
import udacity.com.capstone.databinding.FragmentReminderBinding;

public class ReminderListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        RecordAdapter.MedialPlayClickListener, SearchView.OnQueryTextListener {

    private static final int RECORD_LOADER = 0;
    private FragmentReminderBinding mBinding;
    private RecordAdapter adapter;
    private String mCurFilter;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static ReminderListFragment newInstance() {
        return new ReminderListFragment();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mBinding.setSize(1);
        mBinding.setMessage(R.string.no_data);
        setHasOptionsMenu(true);

        adapter = new RecordAdapter(getActivity(), this);
        mBinding.listView.setAdapter(adapter);
        mBinding.listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getActivity().getSupportLoaderManager().initLoader(RECORD_LOADER, null, this);

        return mBinding.getRoot();
    }

    public void fillList() {
        getActivity().getSupportLoaderManager().initLoader(RECORD_LOADER, null, this);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
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
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onPlayButtonClick(Cursor cursor, int position) {
        if (cursor.moveToPosition(position)) {
            String path = adapter.getPathAtPosition(position);
            playPath(path);
            String id = adapter.getNameAtPosition(position);
            logEvent(id, path);

        }


    }

    private void logEvent(String id, String path) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, path);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "audio");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri = Contract.Record.uri;

        if (mCurFilter != null) {
            String SELECTION = Contract.Record.COLUMN_NAME + " LIKE '%" + mCurFilter + "%'";

            return new CursorLoader(getActivity(),
                    baseUri, null, SELECTION, null, null);
        }

        return new CursorLoader(getActivity(),
                baseUri,
                null,
                null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBinding.setSize(data.getCount());
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
    }
}
