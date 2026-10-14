package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

public class Cliente {
    private int id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private boolean activo;
    public Cliente() {
    }

    public Cliente(String nombres, String apellidos, String telefono, String correo, boolean activo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.activo = activo;
    }


    public Cliente(int id, String nombres, String apellidos, String telefono, String correo, boolean activo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente otro = (Cliente) o;
        return id != 0 && id == otro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}