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

    private static final String DATABASE_NAME = "rememhub.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_CATEGORIAS = "Categorias";
    public static final String TABLE_TAREAS = "Tareas";

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
        // Habilitar claves foráneas en SQLite
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Crear tablas
        db.execSQL(CREATE_TABLE_CATEGORIAS);
        db.execSQL(CREATE_TABLE_TAREAS);
        db.execSQL(CREATE_TABLE_DIAS_RECORDATORIO);
        db.execSQL(CREATE_TABLE_PAPELERA);

        // Insertar datos estáticos en Categorias
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Escuela');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Trabajo');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Ejercicio');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Compras');");

        // Insertar ejemplos en Tareas
        db.execSQL("INSERT INTO Tareas (titulo, descripcion, categoria_id, fecha_creacion, fecha_cumplimiento, hora_cumplimiento, hora_recordatorio, estado) " +
                "VALUES ('Estudiar para examen', 'Revisar los capítulos 1 a 5', 1, '2024-10-01', '2024-10-03', '10:00', '09:00', 0);");
        db.execSQL("INSERT INTO Tareas (titulo, descripcion, categoria_id, fecha_creacion, fecha_cumplimiento, hora_cumplimiento, hora_recordatorio, estado) " +
                "VALUES ('Comprar comestibles', 'Ir al supermercado', 4, '2024-10-01', '2024-10-05', '17:00', '16:00', 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Tareas");
        db.execSQL("DROP TABLE IF EXISTS Categorias");
        db.execSQL("DROP TABLE IF EXISTS DiasRecordatorio");
        db.execSQL("DROP TABLE IF EXISTS Papelera");
        onCreate(db);
    }

    public List<Tarea> obtenerTareas() {
        List<Tarea> tareas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta con JOIN para obtener datos de Tareas y Categorias
        Cursor cursor = db.rawQuery("SELECT Tareas.titulo, Tareas.fecha_creacion, Categorias.nombre AS categoria " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex("titulo"));
                @SuppressLint("Range") String fecha = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
                @SuppressLint("Range") String categoria = cursor.getString(cursor.getColumnIndex("categoria"));

                Tarea tarea = new Tarea(nombre, categoria, fecha, false);
                tareas.add(tarea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tareas;
    }

    //Recuperar dias
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
    // Insertar
    public void insertarDiaRecordatorio(int tareaId, String dia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tarea_id", tareaId);
        values.put("dia", dia);
        db.insert("DiasRecordatorio", null, values);
        db.close();
    }


}
