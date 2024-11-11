package sv.edu.catolica.rememhub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TareaPapelera {
    private String nombre;
    private String categoria;
    private String fecha;

    public TareaPapelera(String nombre, String categoria, String fecha) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
    }


    // Método para convertir la fecha de String a Date
    public Date getFechaCumplimiento() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(fecha);  // Convierte el String a Date
        } catch (ParseException e) {
            e.printStackTrace();
            return null;  // En caso de error, retorna null
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFecha() {
        return fecha;
    }
}

