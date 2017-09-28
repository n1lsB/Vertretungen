package de.nils_beyer.android.Vertretungen.mainActivity;

import android.content.Context;
import android.content.Intent;
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
import de.nils_beyer.android.Vertretungen.data.Source;
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity;
import de.nils_beyer.android.Vertretungen.mainActivity.OverviewFragment;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;
import de.nils_beyer.android.Vertretungen.util.DateParser;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {
    private Context context;
    private Source source;

    public OverviewAdapter(Context c, Source source) {
        context = c;
        this.source = source;
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

        void bind(final Group group) {
            className.setText("Group " + group.name);
            if (group.replacements.length > 1)
                replacementCounter.setText(group.replacements.length + " Vertretungen");
            else
                replacementCounter.setText(group.replacements.length + " Vertretung");

            marked.setVisibility(MarkedKlasses.isMarked(context, group.name) ? View.VISIBLE : View.GONE);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.ARG_KLASSE_EXTRA, (Serializable) group);
                    intent.putExtra(DetailActivity.ARG_DATE_EXTRA, source.getDate());
                    context.startActivity(intent);
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
            if (source.getImmediacity() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                holder.className.setText("Stand: " + DateParser.parseDateToShortString(context, source.getImmediacity()) + " " + simpleDateFormat.format(source.getImmediacity()));
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
            holder.bind(source.getGroupArrayList().get(position - 1));


        }

    }

    @Override
    public int getItemCount() {
        if (source.getGroupArrayList().size() == 0) {
            return 0;
        } else {
            return source.getGroupArrayList().size() + 1;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_overview_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}