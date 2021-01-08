package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items; // = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
    Button addBtn;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();
//        items.add("Buy milk");
//        items.add("go to bank");
//        items.add("talk to Tyler");

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position){
                //Delete the item from the model
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), items.get(position) + " was removed", Toast.LENGTH_SHORT).show();
                items.remove(position);
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                // Log.d("MainActivity", "Single click at position #" + position);
                //Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //Pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //Display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };


        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                //Add item to the model
                items.add(todoItem);

                //Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), todoItem + " was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }


    //Handle the result of the edit activity
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            //Retrieve updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //Extract the original position of edit item
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //Update model at the right position with new item text
            items.set(position, itemText);

            // Notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }



    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    //This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    //This function saves items by writing them into the data file
    private void saveItems(){
        try{
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e){
            Log.e("MainActivity", "Error writing items", e);
        }
    }


}