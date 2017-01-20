package udacity.com.capstone.adapters;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.com.capstone.R;
import udacity.com.capstone.data.Contract;
import udacity.com.capstone.utils.Constants;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.StockViewHolder> {

    final private Context context;
    private Cursor cursor;
    private MedialPlayClickListener clickHandler;

    public RecordAdapter(Context context, MedialPlayClickListener clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

  public  void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public String getPathAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(Contract.Record.COLUMN_PATH));
    }

    public String getNameAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(Contract.Record.COLUMN_NAME));
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        cursor.moveToPosition(position);
        int column_one = cursor.getColumnIndex(Contract.Record.COLUMN_NAME);
        holder.txtName.setText(cursor.getString(column_one));

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }


    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.txt_item)
        TextView txtName;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int pathColumn = cursor.getColumnIndex(Contract.Record.COLUMN_PATH);
            clickHandler.onPlayButtonClick(cursor, adapterPosition);

        }


    }

    public interface MedialPlayClickListener {
        void onPlayButtonClick(Cursor cursor, int position);
    }
}
