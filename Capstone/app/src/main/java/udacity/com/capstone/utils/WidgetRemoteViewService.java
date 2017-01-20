package udacity.com.capstone.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import udacity.com.capstone.R;
import udacity.com.capstone.data.Contract;


public class WidgetRemoteViewService extends RemoteViewsService {
    private static final String LOG_TAG = WidgetRemoteViewService.class.getSimpleName();

    public WidgetRemoteViewService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetItemRemoteView(this.getApplicationContext(), intent);
    }

    class WidgetItemRemoteView implements RemoteViewsFactory {
        Context mContext;
        Cursor mCursor;
        Intent mIntent;

        public WidgetItemRemoteView(Context mContext, Intent mIntent) {
            this.mContext = mContext;
            this.mIntent = mIntent;
        }

        @Override
        public void onCreate() {
            // nothing To DO
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public void onDataSetChanged() {

            if (mCursor != null)
                mCursor.close();

            final long pId = Binder.clearCallingIdentity();

            mCursor = getContentResolver().query(
                    Contract.Record.uri,
                    null,
                    null,
                    null,
                    null
            );

            Binder.restoreCallingIdentity(pId);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            try {
                mCursor.moveToPosition(position);
                int columne_one = mCursor.getColumnIndex(Contract.Record.COLUMN_NAME);
                String name = mCursor.getString(columne_one);
                // create List Item for Widget ListView
                RemoteViews listItemRemoteView = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_record);
                listItemRemoteView.setTextViewText(R.id.txt_item, name);
                return listItemRemoteView;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mCursor.getColumnIndex(Contract.Record._ID));
        }

        @Override
        public void onDestroy() {
            if (mCursor != null)
                mCursor.close();
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
