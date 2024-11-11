package sv.edu.catolica.rememhub.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import sv.edu.catolica.rememhub.Categorias;
import sv.edu.catolica.rememhub.RememhubBD;

public class DbCategorias extends RememhubBD {
    Context context;


    public DbCategorias(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarCategoria(String nombre) {
        long id = 0;
        try {
            RememhubBD rememhubBD =  new  RememhubBD(context);
            SQLiteDatabase db = rememhubBD.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombre", nombre);

            id = db.insert(TABLE_CATEGORIAS, null, values);
        } catch (Exception ex) {
            ex.toString();

        }
        return id;

    }

    public ArrayList<Categorias> mostrarContactos() {
        RememhubBD rememhubBD = new RememhubBD(context);
        SQLiteDatabase db = rememhubBD.getWritableDatabase();

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
        RememhubBD rememhubBD = new RememhubBD (context);
        SQLiteDatabase db = rememhubBD.getWritableDatabase();

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
        RememhubBD rememhubBD = new RememhubBD(context);
        SQLiteDatabase db =  rememhubBD.getWritableDatabase();

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
        RememhubBD rememhubBD = new RememhubBD(context);
        SQLiteDatabase db = rememhubBD.getWritableDatabase();

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

    public Cursor selecionarcategoria() {
        try {
            SQLiteDatabase bd = this.getReadableDatabase();
            Cursor filas = bd.rawQuery("SELECT * FROM " + TABLE_CATEGORIAS + " ORDER BY nombre ASC ", null);
            if (filas.moveToFirst()) {
                return filas;
            } else {
                return null;
            }

        } catch (Exception ex) {
            return null;

        }

    }

    public Cursor obtenerCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIAS, null);  // Asegúrate de que este método retorne el cursor directamente
    }



}