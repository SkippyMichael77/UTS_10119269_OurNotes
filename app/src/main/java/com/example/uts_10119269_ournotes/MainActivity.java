package com.example.uts_10119269_ournotes;

//10119269, Zuhair Rasyid Wafi, IF7

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity{

    private NotesAdapter notesAdapter;
    private NotesDB database;
    private LinearLayout linearEditNotes, linearDeleteNotes;
    private Button btnBatalPopup;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PENTING
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        //inisialisasi
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    return true;
                case R.id.nav_mahasiswa:
                    startActivity(new Intent(getApplicationContext()
                            ,Mahasiswa.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_about:
                    startActivity(new Intent(getApplicationContext()
                            , About.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        //db inisialisasi
        database = new NotesDB(this);

        RecyclerView rvNotes = findViewById(R.id.rvNotes);
        FloatingActionButton fabAddNotes = findViewById(R.id.fabAddNotes);

        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(this, database.getAllNotes());
        rvNotes.setAdapter(notesAdapter);
        notesAdapter.swapCursor(database.getAllNotes());

        fabAddNotes.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNotes.class)));
        
        notesAdapter.setOnClickListenerNotes(id -> {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.popup_notes, null);
            linearEditNotes = view.findViewById(R.id.linearEditNotes);
            linearDeleteNotes = view.findViewById(R.id.linearDeleteNotes);
            btnBatalPopup = view.findViewById(R.id.btnBatalPopup);

            Dialog popupNotes = new Dialog(MainActivity.this);
            popupNotes.setContentView(view);
            popupNotes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupNotes.setOnShowListener(dialog -> {
                linearEditNotes.setOnClickListener(v -> {
                    Intent editNotes = new Intent(MainActivity.this, AddNotes.class);
                    editNotes.putExtra(NotesDB.id_notes, id);
                    startActivity(editNotes);
                    popupNotes.dismiss();
                });
                linearDeleteNotes.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Konfirmasi");
                    builder.setMessage("Apakah anda yakin ingin menghapus data ini?");
                    builder.setPositiveButton("Ya", (dialog1, which) -> {
                        database.deleteNotes(id);
                        popupNotes.dismiss();
                        notesAdapter.swapCursor(database.getAllNotes());
                    }).setNegativeButton("Tidak", (dialog1, which) -> popupNotes.dismiss());
                    AlertDialog popupKonfirmasi = builder.create();
                    popupKonfirmasi.show();
                });
                btnBatalPopup.setOnClickListener(v -> popupNotes.dismiss());
            });
            popupNotes.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        notesAdapter.swapCursor(database.getAllNotes());
    }

}