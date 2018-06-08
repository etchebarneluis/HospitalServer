/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import Controladores.Singleton;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author Brian
 */
@Entity
public class HorarioAtencion implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    private Empleado empleado;

    @ManyToOne(cascade = CascadeType.ALL)
    private Hospital hospital;

    @OneToMany(mappedBy = "horarioAtencion")
    private List<Turno> turnos;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private Long id;
    @Expose
    private String dia;
    @Expose
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaInicio;
    @Expose
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaFin;
    @Expose
    private TipoTurno tipo;
    @Expose
    private int clientesMax;
    @Expose
    private int clienteActual;
    @Expose
    private boolean desactivado;
    @Expose
    private boolean eliminado;

    @Expose
    private EstadoTurno estado;

    public TipoTurno getTipo() {
        return tipo;
    }

    public void setTipo(TipoTurno tipo) {
        this.tipo = tipo;
    }

    public void eliminar () {
        for (Turno t : turnos)
            if (t.getEstado () != EstadoTurno.FINALIZADO) {
                desactivado = true;
                eliminado = false;
                Singleton.getInstance ().merge (this);
                return;
            }
        
        desactivado = eliminado = true;
        Singleton.getInstance ().merge (this);
    }

    public boolean isDesactivado() {
        return desactivado;
    }

    public void setDesactivado(boolean desactivado) {
        this.desactivado = desactivado;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    
    public HorarioAtencion() {
        estado = EstadoTurno.PENDIENTE;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<Turno> turnos) {
        this.turnos = turnos;
    }

    public int getClientesMax() {
        return clientesMax;
    }

    public void setClientesMax(int clientesMax) {
        this.clientesMax = clientesMax;
    }

    public int getClienteActual() {
        return clienteActual;
    }

    public void setClienteActual(int clienteActual) {
        this.clienteActual = clienteActual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void agregarTurno(Turno h) {
        if (turnos == null) {
            turnos = new ArrayList<>();
        }

        turnos.add(h);
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
        if (!(object instanceof HorarioAtencion)) {
            return false;
        }
        HorarioAtencion other = (HorarioAtencion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Logica.HorarioAtencion[ id=" + id + " ]";
    }

}
