package com.drg.ironvault;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.drg.ironvault.entity.Pwd;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<Pwd> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pwdTitle;
        private TextView pwdUsername;
        private ImageButton copyButton;
        private LinearLayout linearLayoutClickable;
        private LinearLayout firstLetterLinearLayout;
        private TextView firstLetterCircle;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            pwdTitle = view.findViewById(R.id.pwdTitle);
            pwdUsername = view.findViewById(R.id.pwdUsername);
            copyButton = view.findViewById(R.id.copyButton);
            linearLayoutClickable = view.findViewById(R.id.linearLayoutClickable);
            firstLetterLinearLayout = view.findViewById(R.id.firstLetterLinearLayout);
            firstLetterCircle = view.findViewById(R.id.firstLetterCircle);

        }

        public TextView getPwdTitle() {
            return pwdTitle;
        }

        public TextView getPwdUsername() {
            return pwdUsername;
        }

        public ImageButton getCopyButton() { return copyButton; }

        public LinearLayout getLinearLayoutClickable() {
            return linearLayoutClickable;
        }

        public LinearLayout getFirstLetterLinearLayout() {
            return firstLetterLinearLayout;
        }

        public TextView getFirstLetterCircle() {
            return firstLetterCircle;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet List<> containing the data to populate views to be used
     * by RecyclerView
     */
    public CustomAdapter(List<Pwd> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_pwd, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getPwdTitle().setText(localDataSet.get(position).getTitle());
        viewHolder.getPwdUsername().setText(localDataSet.get(position).getUsername());

        viewHolder.getCopyButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Password", localDataSet.get(viewHolder.getAdapterPosition()).getPassword());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(view.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.getLinearLayoutClickable().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditPwdActivity.class);
                intent.putExtra("pwdId", localDataSet.get(viewHolder.getAdapterPosition()).getId());
                view.getContext().startActivity(intent);
            }
        });

        viewHolder.getFirstLetterLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditPwdActivity.class);
                intent.putExtra("pwdId", localDataSet.get(viewHolder.getAdapterPosition()).getId());
                view.getContext().startActivity(intent);
            }
        });

        TextView firstLetterCircle = viewHolder.getFirstLetterCircle();
        TextView pwdTitle = viewHolder.getPwdTitle();

        String title = pwdTitle.getText().toString();
        if (title != null && !title.isEmpty()) {
            firstLetterCircle.setText(String.valueOf(title.charAt(0)).toUpperCase());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet == null ? 0 : localDataSet.size();
    }
}
