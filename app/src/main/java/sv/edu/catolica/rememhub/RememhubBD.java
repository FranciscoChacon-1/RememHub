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
    private static final int DATABASE_VERSION = 2; // Cambia de 1 a 2 para forzar la actualización
    private static final String DATABASE_NAME = "rememhub.db";
    public static final String TABLE_CATEGORIAS = "Categorias";
    public static final String TABLE_TAREAS = "Tareas";
    public static final String TABLE_DIAS_RECORDATORIO = "DiasRecordatorio";
    public static final String TABLE_PAPELERA = "Papelera";

    // Crear tabla de tareas
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
            "repetir TEXT," +
            "repeticion_activa INTEGER DEFAULT 0," +
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

    // Crear tabla de papelera
    private static final String CREATE_TABLE_PAPELERA = "CREATE TABLE Papelera (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "titulo TEXT," +
            "descripcion TEXT," +
            "categoria_id INTEGER," +
            "fecha_creacion TEXT," +
            "fecha_cumplimiento TEXT," +
            "hora_cumplimiento TEXT," +
            "hora_recordatorio TEXT," +
            "estado INTEGER," +
            "FOREIGN KEY (categoria_id) REFERENCES Categorias(id) ON DELETE CASCADE" +
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
        db.execSQL(CREATE_TABLE_PAPELERA);

        // Insertar solo categorías
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAPELERA);
        onCreate(db);
    }


    // Obtener tareas con información adicional (como fecha de cumplimiento, hora, etc.)
    public List<Tarea> obtenerTareas() {
        List<Tarea> tareas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta con JOIN para obtener datos de Tareas y Categorias
        Cursor cursor = db.rawQuery("SELECT Tareas.id, Tareas.titulo, Tareas.fecha_creacion, Tareas.fecha_cumplimiento, " +
                "Tareas.hora_cumplimiento, Tareas.hora_recordatorio, Categorias.nombre AS categoria " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id", null);

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

    // Recuperar días de recordatorio para una tarea específica
    @SuppressLint("Range")
    public List<String> obtenerDiasRecordatorio(int tareaId) {
        List<String> dias = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT dia FROM DiasRecordatorio WHERE tarea_id = ?", new String[]{String.valueOf(tareaId)});

        if (cursor.moveToFirst()) {
            do {
                dias.add(cursor.getString(cursor.getColumnIndex("dia")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dias;
    }

    // Insertar días de recordatorio para una tarea
    public void insertarDiaRecordatorio(int tareaId, String dia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tarea_id", tareaId);
        values.put("dia", dia);
        db.insert("DiasRecordatorio", null, values);
        db.close();
    }

    // Actualizar días de recordatorio de una tarea
    public void actualizarDiasRecordatorio(int tareaId, List<String> dias) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Eliminar los días existentes
        db.delete("DiasRecordatorio", "tarea_id = ?", new String[]{String.valueOf(tareaId)});

        // Insertar los nuevos días
        for (String dia : dias) {
            ContentValues values = new ContentValues();
            values.put("tarea_id", tareaId);
            values.put("dia", dia);
            db.insert("DiasRecordatorio", null, values);
        }
        db.close();
    }

    // Insertar una nueva tarea
    public void insertarTarea(String titulo, String descripcion, int categoriaId,  long fechaCreacion,
                              String fechaCumplimiento, String horaCumplimiento, String horaRecordatorio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("titulo", titulo);
        values.put("descripcion", descripcion);
        values.put("categoria_id", categoriaId);
        values.put("fecha_creacion", fechaCreacion);
        values.put("fecha_cumplimiento", fechaCumplimiento);
        values.put("hora_cumplimiento", horaCumplimiento);
        values.put("hora_recordatorio", horaRecordatorio);
        values.put("estado", 0); // Tarea por defecto está pendiente
        db.insert("Tareas", null, values);
        db.close();
    }

    // Método para restaurar una tarea desde la papelera
    @SuppressLint("Range")
    public void restaurarTareaDesdePapelera(int tareaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Recuperar la tarea desde la papelera
        Cursor cursor = db.rawQuery("SELECT * FROM Papelera WHERE id = ?", new String[]{String.valueOf(tareaId)});
        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("titulo", cursor.getString(cursor.getColumnIndex("titulo")));
            values.put("descripcion", cursor.getString(cursor.getColumnIndex("descripcion")));
            values.put("categoria_id", cursor.getInt(cursor.getColumnIndex("categoria_id")));
            values.put("fecha_creacion", cursor.getString(cursor.getColumnIndex("fecha_creacion")));
            values.put("fecha_cumplimiento", cursor.getString(cursor.getColumnIndex("fecha_cumplimiento")));
            values.put("hora_cumplimiento", cursor.getString(cursor.getColumnIndex("hora_cumplimiento")));
            values.put("hora_recordatorio", cursor.getString(cursor.getColumnIndex("hora_recordatorio")));
            values.put("estado", cursor.getInt(cursor.getColumnIndex("estado")));
            db.insert("Tareas", null, values);
        }
        cursor.close();
        db.close();
    }
}
