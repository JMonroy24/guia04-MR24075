/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.guias.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
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

    private LazyDataModel<Pelicula> lazyModel;

    @PostConstruct
    public void init() {
        lazyModel = new LazyDataModel<Pelicula>() {
            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return peliculaRepository.count();
            }

            @Override
            public List<Pelicula> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                return peliculaRepository.findRange(offset, pageSize);
            }
        };
    }

    public LazyDataModel<Pelicula> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Pelicula> lazyModel) {
        this.lazyModel = lazyModel;
    }

    
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

    public void cargarExcelentes() {
        this.peliculas = peliculaRepository.listarExcelentes();
    }

    public void cargarPorChristopherNolan() {
        this.peliculas = peliculaRepository.listarPorChristopherNolan();
    }

    public void cargarAnios2000a2010() {
        this.peliculas = peliculaRepository.listarAnios2000a2010();
    }

    public void cargarAccionPost2010() {
        this.peliculas = peliculaRepository.listarAccionPost2010();
    }

    public void cargarDramaMayorA8() {
        this.peliculas = peliculaRepository.listarDramaMayorA8();
    }
}
