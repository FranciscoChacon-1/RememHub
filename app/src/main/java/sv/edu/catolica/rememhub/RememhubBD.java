package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RememhubBD extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3; // Aumentamos el número de versión para la migración
    private static final String DATABASE_NAME = "rememhub.db";
    public static final String TABLE_CATEGORIAS = "Categorias";
    public static final String TABLE_TAREAS = "Tareas";
    public static final String TABLE_DIAS_RECORDATORIO = "DiasRecordatorio";

    // Crear tabla de tareas con campo para papelera
    private static final String CREATE_TABLE_TAREAS = "CREATE TABLE Tareas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "titulo TEXT NOT NULL," +
            "descripcion TEXT," +
            "categoria_id INTEGER," +
            "fecha_creacion TEXT," +
            "fecha_cumplimiento TEXT," +
            "hora_cumplimiento TEXT," +
            "hora_recordatorio TEXT," +
            "estado INTEGER DEFAULT 0," +
            "repeticion_activa INTEGER DEFAULT 0," +
            "papelera INTEGER DEFAULT 0, " +  // Agregado el campo papelera
            "FOREIGN KEY (categoria_id) REFERENCES Categorias(id) ON DELETE CASCADE" +
            ");";

    // Crear tabla de categorías
    private static final String CREATE_TABLE_CATEGORIAS = "CREATE TABLE Categorias (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL" +
            ");";

    // Crear tabla de días de recordatorio
    private static final String CREATE_TABLE_DIAS_RECORDATORIO = "CREATE TABLE DiasRecordatorio (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "tarea_id INTEGER," +
            "dia TEXT NOT NULL," +
            "FOREIGN KEY (tarea_id) REFERENCES Tareas(id) ON DELETE CASCADE" +
            ");";

    public RememhubBD(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(CREATE_TABLE_CATEGORIAS);
        db.execSQL(CREATE_TABLE_TAREAS);
        db.execSQL(CREATE_TABLE_DIAS_RECORDATORIO);

        // Insertar categorías por defecto
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Escuela');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Trabajo');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Ejercicio');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Compras');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIAS_RECORDATORIO);
        onCreate(db);
    }


    // Obtener tareas que no están en la papelera
    public List<Tarea> obtenerTareas() {
        List<Tarea> tareas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta con JOIN para obtener tareas que no están en la papelera
        Cursor cursor = db.rawQuery("SELECT Tareas.id, Tareas.titulo, Tareas.fecha_creacion, Tareas.fecha_cumplimiento, " +
                "Tareas.hora_cumplimiento, Tareas.hora_recordatorio, Categorias.nombre AS categoria " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.papelera = 0", null);  // Solo tareas no en la papelera

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex("titulo"));
                @SuppressLint("Range") String fecha = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
                @SuppressLint("Range") String fechaCumplimiento = cursor.getString(cursor.getColumnIndex("fecha_cumplimiento"));
                @SuppressLint("Range") String horaCumplimiento = cursor.getString(cursor.getColumnIndex("hora_cumplimiento"));
                @SuppressLint("Range") String horaRecordatorio = cursor.getString(cursor.getColumnIndex("hora_recordatorio"));
                @SuppressLint("Range") String categoria = cursor.getString(cursor.getColumnIndex("categoria"));

                Tarea tarea = new Tarea(id, nombre, categoria, fecha, fechaCumplimiento, horaCumplimiento, horaRecordatorio);
                tareas.add(tarea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tareas;
    }

    // Método para mover tarea a la papelera
    public void moverTareaAPapelera(int tareaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("papelera", 1);  // Establecer papelera = 1

        // Actualiza la tarea para que esté en la papelera
        db.update("Tareas", values, "id = ?", new String[]{String.valueOf(tareaId)});
        db.close();
    }

    // Método para restaurar tarea desde la papelera
    public void restaurarTareaDesdePapelera(int tareaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("papelera", 0);  // Establecer papelera = 0 para restaurar

        // Actualiza la tarea para que ya no esté en la papelera
        db.update("Tareas", values, "id = ?", new String[]{String.valueOf(tareaId)});
        db.close();
    }
}
