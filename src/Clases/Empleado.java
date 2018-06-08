/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 *
 * @author Ale
 */
@Entity
public class Empleado extends Cliente implements Serializable {

    @Expose
    private String[] especialidades;
    @Expose
    private String tipo;
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<HorarioAtencion> horariosAtencions;
    
    public void agregarHA (HorarioAtencion ha) {
        if (horariosAtencions == null)
            horariosAtencions = new ArrayList<>();
        
        horariosAtencions.add (ha);
    }

    public String[] getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String[] especialidades) {
        this.especialidades = especialidades;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<HorarioAtencion> getHorariosAtencions() {
        return horariosAtencions;
    }

    public void setHorariosAtencions(List<HorarioAtencion> horariosAtencions) {
        this.horariosAtencions = horariosAtencions;
    }

}
