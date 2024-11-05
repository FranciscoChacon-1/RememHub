package sv.edu.catolica.rememhub.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import sv.edu.catolica.rememhub.Categorias;

public class DbCategorias extends DbHelper {
    Context context;


    public DbCategorias(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarCategoria(String nombre) {
        long id = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombre", nombre);

            id = db.insert(TABLE_CATEGORIAS, null, values);
        } catch (Exception ex) {
            ex.toString();

        }
        return id;

    }

    public ArrayList<Categorias> mostrarContactos() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Categorias> listaCategorias = new ArrayList<>();
        Categorias categorias = null;
        Cursor cursorContactos = null;

        cursorContactos = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIAS, null);

        if (cursorContactos.moveToFirst()){
            do {
                categorias = new Categorias();
                categorias.setId(cursorContactos.getInt(0));
                categorias.setNombre(cursorContactos.getString(1));
                listaCategorias.add(categorias);

            }while (cursorContactos.moveToNext());
        }
        cursorContactos.close();

        return listaCategorias;
    }

    public Categorias verCategorias(int id) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Categorias categorias = null;
        Cursor cursorContactos;

        cursorContactos = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIAS+" WHERE id = "  + id +  " LIMIT 1", null);

        if (cursorContactos.moveToFirst()){
            categorias = new Categorias();
            categorias.setId(cursorContactos.getInt(0));
            categorias.setNombre(cursorContactos.getString(1));
        }
        cursorContactos.close();

        return categorias;
    }

    public boolean editarCategoria(int id ,String nombre) {
        boolean correcto = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_CATEGORIAS + " SET nombre = '" + nombre + "' WHERE id = " + id+" ");
            correcto = true;


        }catch (Exception ex){
            ex.toString();
            correcto = false;
        }finally {
            db.close();
        }
        return correcto;

    }

    public boolean ElimiarCategoria(int id ) {
        boolean correcto = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("Delete FROM " + TABLE_CATEGORIAS + " WHERE id = " + id+" ");
            correcto = true;


        }catch (Exception ex){
            ex.toString();
            correcto = false;
        }finally {
            db.close();
        }
        return correcto;

    }






}
