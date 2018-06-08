/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Ale
 */
@Entity
public class Vacunacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int diaVacunacion;
    private int mesVacunacion;
    private int anioVacunacion;
    private int diaVencimiento;
    private int mesVencimiento;
    private int anioVencimiento;
    @OneToMany
    List<Vacuna> vacunas;

    
    
    public List<Vacuna> getVacunas() {
        return vacunas;
    }

    public void setVacunas(List<Vacuna> vacunas) {
        this.vacunas = vacunas;
    }

    public int getDiaVacunacion() {
        return diaVacunacion;
    }

    public void setDiaVacunacion(int diaVacunacion) {
        this.diaVacunacion = diaVacunacion;
    }

    public int getMesVacunacion() {
        return mesVacunacion;
    }

    public void setMesVacunacion(int mesVacunacion) {
        this.mesVacunacion = mesVacunacion;
    }

    public int getAnioVacunacion() {
        return anioVacunacion;
    }

    public void setAnioVacunacion(int anioVacunacion) {
        this.anioVacunacion = anioVacunacion;
    }

    public int getDiaVencimiento() {
        return diaVencimiento;
    }

    public void setDiaVencimiento(int diaVencimiento) {
        this.diaVencimiento = diaVencimiento;
    }

    public int getMesVencimiento() {
        return mesVencimiento;
    }

    public void setMesVencimiento(int mesVencimiento) {
        this.mesVencimiento = mesVencimiento;
    }

    public int getAnioVencimiento() {
        return anioVencimiento;
    }

    public void setAnioVencimiento(int anioVencimiento) {
        this.anioVencimiento = anioVencimiento;
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
        if (!(object instanceof Vacunacion)) {
            return false;
        }
        Vacunacion other = (Vacunacion) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Logica.Vacunacion[ id=" + id + " ]";
    }

}
