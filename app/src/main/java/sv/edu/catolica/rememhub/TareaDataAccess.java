package sv.edu.catolica.rememhub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TareaDataAccess {

    private SQLiteDatabase db;
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
    RememhubBD dbHelper;

    public TareaDataAccess(Context context) {
        dbHelper = new RememhubBD(context);
        this.db = dbHelper.getWritableDatabase();
    }

    // Obtener tareas no completadas con fecha futura (semana)
    public List<Tarea> obtenerTareasSemana() {
        List<Tarea> listaTareasFuturas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.fecha_cumplimiento >= date('now') AND Tareas.estado = 0 AND Tareas.papelera = 0"; // No en papelera

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareasFuturas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasFuturas;
    }



    // Obtener tarea por nombre
    public Tarea obtenerTareaPorNombre(String nombreTarea) {
        Tarea tarea = null;
        String query = "SELECT Tareas.titulo, Tareas.descripcion, Categorias.nombre AS categoria, " +
                "Tareas.fecha_creacion, Tareas.fecha_cumplimiento, Tareas.estado, " +
                "Tareas.hora_cumplimiento, Tareas.hora_recordatorio " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.titulo = ? AND Tareas.papelera = 0";  // Solo tareas no eliminadas

        Cursor cursor = db.rawQuery(query, new String[]{nombreTarea});

        if (cursor.moveToFirst()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
            String fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"));
            String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
            String horaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("hora_cumplimiento"));
            String horaRecordatorio = cursor.getString(cursor.getColumnIndexOrThrow("hora_recordatorio"));
            boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

            tarea = new Tarea(titulo, descripcion, categoria, fechaCreacion, fechaCumplimiento, horaCumplimiento, horaRecordatorio, completada);
        }
        cursor.close();
        return tarea;
    }

    // Método para eliminar tarea físicamente (si fuera necesario)
    public void eliminarTarea(Tarea tarea) {
        String whereClause = "titulo = ? AND fecha_cumplimiento = ?";
        String[] whereArgs = {tarea.getTitulo(), tarea.getFecha()};

        db.delete("Tareas", whereClause, whereArgs);
    }

    // Marcar una tarea como eliminada (no la elimina, solo la marca como eliminada)
    public void marcarTareaComoEliminada(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put("estado", 1); // 1: completada o eliminada
        values.put("papelera", 1); // Marcamos como movida a la papelera
        db.update("Tareas", values, "titulo = ? AND fecha_cumplimiento = ?",
                new String[]{tarea.getTitulo(), tarea.getFecha()});
    }

    // Mover tarea a la tabla Papelera
    public boolean moverTareaAPapelera(Tarea tarea) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("papelera", 1);
        int rowsAffected = db.update("Tareas", values, "id = ?", new String[]{String.valueOf(tarea.getId())});
        db.close();
        return rowsAffected > 0; // Devuelve true si la operación fue exitosa
    }

    // Método general para obtener tareas filtradas basado en el cumplimiento de la fecha
    public List<Tarea> obtenerTareasFiltradas(boolean proximas) {
        List<Tarea> tareasFiltradas = new ArrayList<>();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        // Consulta dependiendo del filtro (próximas o expiradas)
        String query = "SELECT Tareas.id, Tareas.titulo, Tareas.fecha_creacion, Tareas.fecha_cumplimiento, " +
                "Tareas.hora_cumplimiento, Tareas.hora_recordatorio, Categorias.nombre AS categoria " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.papelera = 0 AND Tareas.estado = 0 " +
                (proximas ? "AND Tareas.fecha_cumplimiento >= ?" : "AND Tareas.fecha_cumplimiento < ?");

        try (Cursor cursor = db.rawQuery(query, new String[]{fechaActual})) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                    String fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"));
                    String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                    String horaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("hora_cumplimiento"));
                    String horaRecordatorio = cursor.getString(cursor.getColumnIndexOrThrow("hora_recordatorio"));
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));

                    Tarea tarea = new Tarea(id, titulo, categoria, fechaCreacion, fechaCumplimiento, horaCumplimiento, horaRecordatorio);
                    tareasFiltradas.add(tarea);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("TareaDataAccess", "Error al obtener tareas filtradas", e);
        }

        return tareasFiltradas;
    }



    // Obtener tareas eliminadas,osea en papelera
    public List<Tarea> obtenerTareasEliminadas() {
        List<Tarea> listaTareasEliminadas = new ArrayList<>();
        String query = "SELECT Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.papelera = 1"; // Ahora usamos el campo papelera para obtener las tareas eliminadas

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                listaTareasEliminadas.add(new Tarea(titulo, categoria, fecha, completada));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasEliminadas;
    }



    // Restaurar tarea desde papelera a tareas
    public boolean restaurarTarea(Tarea tarea) {
        SQLiteDatabase db = this.db;
        ContentValues values = new ContentValues();
        values.put("papelera", 0);  // Cambiar a estado "no en papelera"

        try {
            int rowsAffected = db.update("Tareas", values, "id = ?", new String[]{String.valueOf(tarea.getId())});
            if (rowsAffected > 0) {
                Log.d("Restauracion", "Tarea restaurada correctamente: " + tarea.getTitulo());
            }
            return rowsAffected > 0;  // Si al menos una fila fue actualizada
        } catch (Exception e) {
            Log.e("TareaDataAccess", "Error al restaurar tarea: ", e);
            return false;  // Si ocurrió algún error
        }
    }




    // Mover tarea de la papelera de vuelta a tareas
    public void moverTareaDePapeleraATareas(Tarea tarea) {
        ContentValues values = new ContentValues();
        values.put("titulo", tarea.getTitulo());
        values.put("descripcion", tarea.getDescripcion());
        values.put("categoria_id", tarea.getCategoriaId());
        values.put("fecha_creacion", tarea.getFechaCreacion());
        values.put("fecha_cumplimiento", tarea.getFecha());
        values.put("hora_cumplimiento", tarea.getHoraCumplimiento());
        values.put("hora_recordatorio", tarea.getHoraRecordatorio());
        values.put("estado", tarea.isCompletada() ? 1 : 0);
        values.put("papelera", 0); // Marcar como tarea activa

        db.insert("Tareas", null, values);
    }

    // Eliminar tarea de la papelera
    public void eliminarTareaDePapelera(Tarea tarea) {
        SQLiteDatabase db = this.db;
        db.delete("tareas", "id = ?", new String[]{String.valueOf(tarea.getId())});
        db.close();
    }

    // Vaciar la papelera: elimina todas las tareas con papelera = 1
    public void vaciarPapelera() {
        db.delete("Tareas", "papelera = ?", new String[]{"1"});
    }

    // Eliminar una tarea individual de la papelera
    public void eliminarTareaIndividual(Tarea tarea) {
        db.delete("Tareas", "id = ?", new String[]{String.valueOf(tarea.getId())});
    }


    // Obtener tareas próximas
    public List<Tarea> obtenerTareasProximas() {
        List<Tarea> tareasProximas = new ArrayList<>();

        // Fecha y hora actual en formato completo
        String fechaHoraActual = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
        Log.d("FechaHora_actual", "Fecha y hora actual: " + fechaHoraActual);

        String query = "SELECT Tareas.id, Tareas.titulo, Tareas.fecha_cumplimiento, Tareas.estado, " +
                "Tareas.hora_cumplimiento, Tareas.hora_recordatorio, Categorias.nombre AS categoria " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.papelera = 0 AND Tareas.estado = 0 " +
                "AND (Tareas.fecha_cumplimiento || ' ' || Tareas.hora_cumplimiento) >= ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{fechaHoraActual})) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                    String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                    String horaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("hora_cumplimiento"));
                    String horaRecordatorio = cursor.getString(cursor.getColumnIndexOrThrow("hora_recordatorio"));


                    Log.d("FechaHora_proximas", "Fecha y hora actual: " + fechaHoraActual +
                            ", Fecha y hora de la tarea: " + fechaCumplimiento + " " + horaCumplimiento);

                    Tarea tarea = new Tarea(id, titulo, fechaCumplimiento, categoria, fechaCumplimiento, horaCumplimiento, horaRecordatorio);
                    tareasProximas.add(tarea);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("TareaDataAccess", "Error al obtener tareas próximas", e);
        }

        return tareasProximas;
    }



    // Obtener tareas de la papelera (tareas marcadas como eliminadas, es decir, papelera = 1)
    public List<Tarea> obtenerTareasPapelera() {
        List<Tarea> listaTareasPapelera = new ArrayList<>();
        String query = "SELECT Tareas.id, Tareas.titulo, Categorias.nombre AS categoria, Tareas.fecha_cumplimiento, Tareas.estado " +
                "FROM Tareas " +
                "JOIN Categorias ON Tareas.categoria_id = Categorias.id " +
                "WHERE Tareas.papelera = 1";  // Solo selecciona tareas en la papelera

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                String fechaCumplimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cumplimiento"));
                boolean completada = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1;

                // Crea una nueva instancia de Tarea y agrega a la lista
                Tarea tarea = new Tarea(id, titulo, categoria, fechaCumplimiento, completada);
                listaTareasPapelera.add(tarea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareasPapelera;
    }


    // Método para eliminar todas las tareas en papelera y sus días de recordatorio
    public void eliminarTareasPapelera() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM DiasRecordatorio WHERE tarea_id IN (SELECT id FROM Tareas WHERE papelera = 1)");
        db.delete("Tareas", "papelera = 1", null); // Elimina todas las tareas en papelera
        db.close();
    }

    // Método para eliminar una tarea y sus días de recordatorio
    public void eliminarTareaConRecordatorio(int tareaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("DiasRecordatorio", "tarea_id = ?", new String[]{String.valueOf(tareaId)});
        db.delete("Tareas", "id = ?", new String[]{String.valueOf(tareaId)});
        db.close();
    }


}
