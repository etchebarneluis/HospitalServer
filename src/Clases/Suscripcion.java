package Clases;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Ale
 */
@Entity
public class Suscripcion implements Serializable {

    @ManyToOne (cascade = CascadeType.ALL)
    private Hospital hospital;

    @Expose
    @ManyToOne (cascade = CascadeType.ALL)
    private Cliente cliente;

    private static final long serialVersionUID = 1L;
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Expose
    private Date fechaContratada;
    @Expose
    private Date fechaVencimiento;
    @Expose
    private EstadoSuscripcion estado;
    @Expose
    private int cantMeses;

    public Suscripcion() {
        this.estado = EstadoSuscripcion.PENDIENTE;
    }

    public EstadoSuscripcion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSuscripcion estado) {
        this.estado = estado;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public int getCantMeses() {
        return cantMeses;
    }

    public void setCantMeses(int cantMeses) {
        this.cantMeses = cantMeses;
    }
    
    

    public Date getFechaContratada() {
        return fechaContratada;
    }

    public void setFechaContratada(Date fechaContratada) {
        this.fechaContratada = fechaContratada;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Suscripcion)) {
            return false;
        }
        Suscripcion other = (Suscripcion) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Logica.Suscripcion[ id=" + id + " ]";
    }

}
