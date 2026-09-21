/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.guias.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.guias.control.PeliculaRepository;
import sv.edu.ues.occ.ingenieria.ppi115_2026.guias.entity.Pelicula;

/**
 *
 * @author jmonroy
 */
@Named
@ViewScoped
public class PeliculaBean implements Serializable {
    
    @Inject
    private PeliculaRepository peliculaRepository;
    
    private List<Pelicula> peliculas;
    
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }
    
    public void cargarTodasOrdenASC() {
        this.peliculas = peliculaRepository.listarTodasOrdenASC();
    }
    
    public void cargarCienciaFiccionYSuspense() {
        this.peliculas = peliculaRepository.listarCienciaFiccionYSuspense();
    }
}
