package com.absolutely.tobuylist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absolutely.tobuylist.ListActivity;
import com.absolutely.tobuylist.MainActivity;
import com.absolutely.tobuylist.R;
import com.absolutely.tobuylist.data.DatabaseHandler;
import com.absolutely.tobuylist.model.Item;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.itemName.setText(MessageFormat.format("Item: {0}", item.getItemName()));
        holder.itemQuantity.setText(MessageFormat.format("Quantity: {0}", String.valueOf(item.getItemQuantity())));
        holder.itemColor.setText(MessageFormat.format("Color: {0}", item.getItemColor()));
        holder.itemSize.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getItemSize())));
        holder.createdAt.setText(item.getDateItemAdded());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int id;
        private TextView itemName;
        private TextView itemQuantity;
        private TextView itemColor;
        private TextView itemSize;
        private TextView createdAt;
        private Button editButton;
        private Button deleteButton;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemColor = itemView.findViewById(R.id.item_color);
            itemSize = itemView.findViewById(R.id.item_size);
            createdAt = itemView.findViewById(R.id.item_date);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Item item = itemList.get(position);
            switch (view.getId()) {
                case R.id.editButton:
                    editItem(item);
                    break;
                case R.id.deleteButton:
                    deleteItem(item.getId());
                    break;
                default:
                    break;
            }
        }

        private void editItem(final Item item) {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup, null);

            final TextView name = view.findViewById(R.id.name);
            final TextView quantity = view.findViewById(R.id.quantity);
            final TextView color = view.findViewById(R.id.color);
            final TextView size = view.findViewById(R.id.size);
            final Button update = view.findViewById(R.id.save_button);

            name.setText(item.getItemName());
            quantity.setText(String.valueOf(item.getItemQuantity()));
            color.setText(item.getItemColor());
            size.setText(String.valueOf(item.getItemSize()));
            update.setText(R.string.update_text);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    String newItem = name.getText().toString().trim();
                    String newColor = color.getText().toString().trim();
                    int newQuantity = Integer.parseInt(quantity.getText().toString().trim());
                    int newSize = Integer.parseInt(size.getText().toString().trim());

                    item.setItemName(newItem);
                    item.setItemColor(newColor);
                    item.setItemQuantity(newQuantity);
                    item.setItemSize(newSize);

                    db.updateItem(item);
                    Snackbar.make(view, "Update successful.", Snackbar.LENGTH_SHORT).show();
                    itemList.set(getAdapterPosition(), item);
                    notifyItemChanged(getAdapterPosition());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 1200);


                }
            });
        }

        private void deleteItem(final int id) {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_popup, null);
            Button noButton = view.findViewById(R.id.confirm_no_button);
            Button yesButton = view.findViewById(R.id.confirm_yes_button);
            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    mainIfNoData(db);

                    dialog.dismiss();
                }
            });
        }

        private void mainIfNoData(DatabaseHandler db) {
            if(db.getItemCount() == 0) {
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
        }
    }


}
