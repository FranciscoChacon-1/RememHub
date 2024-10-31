package sv.edu.catolica.rememhub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RememhubBD extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rememhub.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statements for table creation
    private static final String CREATE_TABLE_TAREAS = "CREATE TABLE Tareas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "titulo TEXT NOT NULL," +
            "descripcion TEXT," +
            "categoria_id INTEGER," +
            "fecha_creacion TEXT," +
            "fecha_cumplimiento TEXT," +
            "hora_cumplimiento TEXT," +
            "hora_recordatorio TEXT," +
            "estado INTEGER DEFAULT 0," + // 0: incompleta, 1: completa
            "FOREIGN KEY (categoria_id) REFERENCES Categorias(id)" +
            ");";

    private static final String CREATE_TABLE_CATEGORIAS = "CREATE TABLE Categorias (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL" +
            ");";

    private static final String CREATE_TABLE_DIAS_RECORDATORIO = "CREATE TABLE DiasRecordatorio (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "tarea_id INTEGER," +
            "dia TEXT NOT NULL," + // Ejemplo de valores: "Lunes", "Martes", etc.
            "FOREIGN KEY (tarea_id) REFERENCES Tareas(id)" +
            ");";

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
            "FOREIGN KEY (categoria_id) REFERENCES Categorias(id)" +
            ");";

    public RememhubBD(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating tables
        db.execSQL(CREATE_TABLE_CATEGORIAS);
        db.execSQL(CREATE_TABLE_TAREAS);
        db.execSQL(CREATE_TABLE_DIAS_RECORDATORIO);
        db.execSQL(CREATE_TABLE_PAPELERA);

        // Insert static data into Categorias
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Escuela');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Trabajo');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Ejercicio');");
        db.execSQL("INSERT INTO Categorias (nombre) VALUES ('Compras');");

        // Insert static data for days of the week into DiasRecordatorio
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Lunes');");
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Martes');");
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Miércoles');");
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Jueves');");
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Viernes');");
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Sábado');");
        db.execSQL("INSERT INTO DiasRecordatorio (dia) VALUES ('Domingo');");

        // Insert two example tasks
        db.execSQL("INSERT INTO Tareas (titulo, descripcion, categoria_id, fecha_creacion, fecha_cumplimiento, hora_cumplimiento, hora_recordatorio, estado) " +
                "VALUES ('Estudiar para examen', 'Revisar los capítulos 1 a 5', 1, '2024-10-01', '2024-10-03', '10:00', '09:00', 0);");
        db.execSQL("INSERT INTO Tareas (titulo, descripcion, categoria_id, fecha_creacion, fecha_cumplimiento, hora_cumplimiento, hora_recordatorio, estado) " +
                "VALUES ('Comprar comestibles', 'Ir al supermercado para la despensa semanal', 4, '2024-10-01', '2024-10-05', '17:00', '16:00', 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Tareas");
        db.execSQL("DROP TABLE IF EXISTS Categorias");
        db.execSQL("DROP TABLE IF EXISTS DiasRecordatorio");
        db.execSQL("DROP TABLE IF EXISTS Papelera");
        onCreate(db);
    }
}
