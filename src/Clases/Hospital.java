/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import Controladores.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Jorge
 */
@Entity
public class Hospital implements Serializable {

    @ManyToOne
    private Empleado empleado;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;
    private boolean publico;
    private String departamento;
    private String calle;
    private String directora;
    private String telefono;
    private String correo;
    private boolean activado;
    private int numero;
    private double latitud;
    private double longitud;
    @OneToMany(mappedBy = "hospital")
    private List<Suscripcion> suscripciones;
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Administrador> administradores;
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioAtencion> horarioAtencions;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Empleado> empleados;

    public List<Empleado> getEmpleados() {
        if (empleados == null) {
            empleados = new ArrayList<>();
        }
        return empleados;
    }

    public List<Empleado> getEmpleadosActivos() {
        List<Empleado> emp = new ArrayList<>();
        for (Empleado e : empleados) {
            if (e.isActivo()) {
                emp.add(e);
            }
        }

        return emp;
    }

    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    public List<HorarioAtencion> getHorarioAtencions() {
        return horarioAtencions;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(List<Administrador> administradores) {
        this.administradores = administradores;
    }
    
    public boolean eliminarEmpleado (String id) {
        if (empleados == null)
            return false;
        
        for (int i = empleados.size() - 1; i >= 0; i--)
            if (empleados.get(i).getId() == Long.valueOf(id)) {
                empleados.remove(i);
                Singleton.getInstance().merge(this);
                return true;
            }
        
        return false;
    }

    public void agregarAdministrador(Usuario u) {
        if (administradores == null) {
            administradores = new ArrayList<>();
        }

        Administrador a = new Administrador();
        a.setAdminGeneral(false);
        a.setUsuario(u);
        a.setHospital(this);
        administradores.add(a);
    }

    public void removerAdministrador(String ciAdmin) {
        if (administradores == null) {
            return;
        }
        for (int i = 0; i < administradores.size(); i++) {
            if (administradores.get(i).getUsuario().getCi().equals(ciAdmin)) {
                administradores.get(i).setHospital(null);
                administradores.remove(i);
                return;
            }
        }
    }

    public void setHorarioAtencions(List<HorarioAtencion> horarioAtencions) {
        this.horarioAtencions = horarioAtencions;
    }

    public List<Suscripcion> getSuscripciones() {
        return suscripciones;
    }

    public String getDirectora() {
        return directora;
    }

    public void agregarHA(HorarioAtencion ha) {
        if (horarioAtencions == null) {
            horarioAtencions = new ArrayList<>();
        }

        horarioAtencions.add(ha);
    }

    public void setDirectora(String directora) {
        this.directora = directora;
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

    public void setSuscripciones(List<Suscripcion> suscripciones) {
        this.suscripciones = suscripciones;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    public Long getId() {
        return id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public boolean isActivado() {
        return activado;
    }

    public void setActivado(boolean activado) {
        this.activado = activado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void agregarEmpleado(Empleado e) {
        if (empleados == null) {
            empleados = new ArrayList<>();
        }

        empleados.add(e);
    }

    public HorarioAtencion obtenerHorarioById(long idHorario) {
        for (HorarioAtencion hs : horarioAtencions) {
            if (hs.getId() == idHorario) {
                return hs;
            }
        }

        return null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Hospital)) {
            return false;
        }
        Hospital other = (Hospital) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Logica.Hospital[ id=" + id + " ]";
    }

}
