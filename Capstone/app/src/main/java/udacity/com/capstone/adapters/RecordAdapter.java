package udacity.com.capstone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.com.capstone.R;
import udacity.com.capstone.data.Record;

/**
 * Created by techjini on 24/12/15.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.StylistViewHolder> implements Filterable {
    private List<Record> list = Collections.emptyList();
    Context context;
    private ItemFilter mFilter = new ItemFilter();
    private List<Record> filteredData = null;
    MedialPlayClickListener mListener;

    public RecordAdapter(List<Record> data, MedialPlayClickListener listener) {
        this.list = data;
        this.filteredData = data;
        this.mListener = listener;
    }


    @Override
    public void onViewAttachedToWindow(StylistViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public StylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new StylistViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecordAdapter.StylistViewHolder holder, final int position) {
        final Record item = filteredData.get(position);
        holder.txtName.setText(item.getName());
    }


    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public class StylistViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_item)
        TextView txtName;
        @BindView(R.id.play_icon)
        ImageView imgPlayIcon;

        public StylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPlayButtonClick(filteredData.get(getLayoutPosition()).getAudioPath());
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            filterString = filterString.toLowerCase(Locale.US);
            FilterResults results = new FilterResults();

            int count = list.size();
            final ArrayList<Record> nlist = new ArrayList<Record>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName().toLowerCase(Locale.US);
                if (filterableString.toLowerCase(Locale.US).contains(
                        filterString)) {
                    nlist.add(list.get(i));
                    Log.e("her ","her "+list.get(i).getName());
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      Filter.FilterResults results) {
            filteredData = (ArrayList<Record>) results.values;
            Log.e("result","sie "+filteredData.size());
            notifyDataSetChanged();
        }
    }

    public interface MedialPlayClickListener {
        void onPlayButtonClick(String path);
    }
}
