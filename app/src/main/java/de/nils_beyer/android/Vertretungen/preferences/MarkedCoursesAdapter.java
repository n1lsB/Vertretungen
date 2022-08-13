package de.nils_beyer.android.Vertretungen.preferences;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.nils_beyer.android.Vertretungen.R;


public class MarkedCoursesAdapter extends RecyclerView.Adapter<MarkedCoursesAdapter.ViewHolder> {

    private ArrayList<String> markedCourses;
    private Context c;

    public MarkedCoursesAdapter(Context context) {
        c = context;
        markedCourses = new ArrayList<>(MarkedCourses.returnList(context));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button delete;

        ViewHolder(View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.markedcourses_list_name);
            delete = (Button) v.findViewById(R.id.markedcourses_list_delete);
        }

        void bind(String _name) {
            this.name.setText(_name);
            this.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MarkedCourses.deleteEntry(MarkedCoursesAdapter.this.c, ViewHolder.this.name.getText().toString());
                    markedCourses.remove(ViewHolder.this.name.getText().toString());
                    MarkedCoursesAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(MarkedCoursesAdapter.ViewHolder holder, int position) {
        holder.bind(markedCourses.get(position));
    }

    @Override
    public int getItemCount() {
        return markedCourses.size();
    }

    @Override
    public MarkedCoursesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recyclerview_markedcourses_layout, parent, false);

        return new ViewHolder(v);
    }

    public void add(String entry) {
        markedCourses.add(entry);
        notifyDataSetChanged();
    }

    public void clear() {
        markedCourses.clear();
        notifyDataSetChanged();
    }
}