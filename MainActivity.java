package com.example.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        EditText username = findViewById(R.id.etInlognaam);
        EditText password = findViewById(R.id.etWachtwoord);
        String gebruiker = username.getText().toString();
        String ww = password.getText().toString();

        SQLiteDatabase gebruikersDB = this.openOrCreateDatabase("gebruikersDB", MODE_PRIVATE, null);

        File dbFile = getDatabasePath("gebruikersDB");
        if (dbFile.exists()) {
            Log.i("database", "bestaat");
            Log.i("Path to database ", dbFile.getPath().toString());
        } else {
            Log.i("database", "bestaat NIET");
        }

        gebruikersDB.execSQL("CREATE TABLE IF NOT EXISTS"
                + " gegevens "
                + "(inlognaam VARCHAR, wachtwoord VARCHAR) ;"
        );

        int viewId = view.getId();

        if (viewId == R.id.registreren) {
            Log.d("test", "registreren button geklikt");
            if (!checkIfUserExists(gebruiker)) {
                // Gebruiker bestaat nog niet, dus voeg toe aan de database
                String sql = "INSERT or REPLACE INTO gegevens (inlognaam, wachtwoord) VALUES ('" + gebruiker + "', '" + ww + "')";
                gebruikersDB.execSQL(sql);
            } else {
                Log.d("test", "Gebruiker bestaat al");

                // Melding aan de gebruiker tonen met een Toast
                Toast.makeText(this, "Gebruiker bestaat al", Toast.LENGTH_SHORT).show();
            }

        } else if (viewId == R.id.inloggen) {
            Log.d("test", "inloggen button geklikt");
            String table = "gegevens";
            String[] columnsToReturn = {"wachtwoord"};
            String selection = "inlognaam = ?";
            String[] selectionArgs = {gebruiker};
            try {
                Cursor cursor = gebruikersDB.query(table, columnsToReturn, selection, selectionArgs, null, null, null);
                cursor.moveToLast();
                String column1 = cursor.getString(0);
                if (column1.equals(ww)) {
                    Log.d(column1, "Inloggen button geklikt");
                    Intent intent = new Intent(this, MainActivity2.class);
                    startActivity(intent); // Start MainActivity2
                } else {
                    Log.d(column1, "Inloggen mislukt");
                }
                cursor.close();
            } catch (android.database.CursorIndexOutOfBoundsException e) {
                Log.d(gebruiker, " bestaat waarschijnlijk niet ");
            }
        }
    }

    private boolean checkIfUserExists(String gebruiker) {
        SQLiteDatabase gebruikersDB = this.openOrCreateDatabase("gebruikersDB", MODE_PRIVATE, null);
        String table = "gegevens";
        String[] columnsToReturn = {"inlognaam"};
        String selection = "inlognaam = ?";
        String[] selectionArgs = {gebruiker};

        Cursor cursor = gebruikersDB.query(table, columnsToReturn, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void logGegevensTabel(View view) {
        SQLiteDatabase gebruikersDB = this.openOrCreateDatabase("gebruikersDB", MODE_PRIVATE, null);
        Cursor c = gebruikersDB.rawQuery("SELECT rowid, * FROM gegevens", null);
        int Column0 = c.getColumnIndex("rowid");
        int Column1 = c.getColumnIndex("inlognaam");
        int Column2 = c.getColumnIndex("wachtwoord");
        for (int row = 0; row < c.getCount(); row++) {
            c.moveToPosition(row);
            String id = c.getString(Column0);
            String name = c.getString(Column1);
            String ww = c.getString(Column2);
            Log.i(Integer.toString(row), "Row " + Integer.toString(row) + " : " + id + "  " + name + "  " + ww);
        }
        c.close();
    }
}
