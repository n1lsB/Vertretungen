package de.nils_beyer.android.Vertretungen.mainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import de.nils_beyer.android.Vertretungen.R;
import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.data.TeacherGroup;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;
import de.nils_beyer.android.Vertretungen.util.DateParser;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {
    private Context context;
    private GroupCollection groupCollection;

    public OverviewAdapter(Context c, GroupCollection groupCollection) {
        context = c;
        this.groupCollection = groupCollection;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView replacementCounter;
        CardView cardView;
        ImageView marked;


        ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.overview_card);
            className = (TextView) v.findViewById(R.id.text_class_name);
            replacementCounter = (TextView) v.findViewById(R.id.text_replacement_count);
            marked = (ImageView) v.findViewById(R.id.image_class_marked);

        }

        void bind(final int position) {
            Group group = groupCollection.getGroupArrayList().get(position);
            if (group instanceof TeacherGroup) {
                className.setText(String.format(context.getString(R.string.overview_adapter_group_teacher), group.name));
            } else {
                className.setText(String.format(context.getString(R.string.overview_adapter_group_student), group.name));
            }

            if (group.replacements.size() > 1)
                replacementCounter.setText(String.format(context.getString(R.string.overview_adapter_entry_multiple), String.valueOf(group.replacements.size())));
            else
                replacementCounter.setText(String.format(context.getString(R.string.overview_adapter_entry_single), String.valueOf(group.replacements.size())));

            marked.setVisibility(group.isMarked(context) ? View.VISIBLE : View.GONE);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailActivity.startActivity(context, groupCollection, position);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.cardView.setOnClickListener(null);
            holder.replacementCounter.setVisibility(View.GONE);
            holder.className.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.cardView.setClickable(false);
            holder.marked.setVisibility(View.GONE);
            if (groupCollection.getImmediacity() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format_only_time));
                holder.className.setText(String.format(
                        context.getString(R.string.overview_adapter_immediacity),
                        DateParser.parseDateToShortString(context, groupCollection.getImmediacity()),
                        simpleDateFormat.format(groupCollection.getImmediacity()
                )));
            }
            else {
                holder.cardView.setVisibility(View.GONE);
            }
        } else {
            holder.cardView.setClickable(true);
            holder.cardView.setVisibility(View.VISIBLE);
            holder.replacementCounter.setVisibility(View.VISIBLE);
            holder.marked.setVisibility(View.VISIBLE);
            holder.className.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.bind(position - 1);


        }

    }

    @Override
    public int getItemCount() {
        if (groupCollection.getGroupArrayList().size() == 0) {
            return 0;
        } else {
            return groupCollection.getGroupArrayList().size() + 1;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_overview_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void update(@Nullable GroupCollection collection) {
        if (collection == null) {
            return;
        }
        this.groupCollection = collection;
        notifyDataSetChanged();
    }
}