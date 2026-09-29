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
import java.util.ArrayList;
import java.util.Date;
import org.primefaces.model.SortOrder;

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
    private String filtroTitulo;
    private List<Date> rangoFechasEstreno = new ArrayList<>();
    private int first;

    private LazyDataModel<Pelicula> lazyModel;

    @PostConstruct
    public void init() {
        lazyModel = new LazyDataModel<Pelicula>() {
            @Override
public int count(Map<String, FilterMeta> filterBy) {
    return peliculaRepository.contarFiltradas(
            filtroTitulo,
            obtenerFechaInicio(),
            obtenerFechaFin());
}

@Override
public List<Pelicula> load(int offset, int pageSize,
        Map<String, SortMeta> sortBy,
        Map<String, FilterMeta> filterBy) {
    SortMeta orden = sortBy == null || sortBy.isEmpty()
            ? null
            : sortBy.values().iterator().next();

    String campoOrdenamiento = orden == null
            ? "idPelicula"
            : orden.getField();

    boolean ascendente = orden == null
            || orden.getOrder() != SortOrder.DESCENDING;

    return peliculaRepository.listarFiltradas(
            offset,
            pageSize,
            filtroTitulo,
            obtenerFechaInicio(),
            obtenerFechaFin(),
            campoOrdenamiento,
            ascendente);
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
public String getFiltroTitulo() {
    return filtroTitulo;
}

public void setFiltroTitulo(String filtroTitulo) {
    this.filtroTitulo = filtroTitulo;
}

public List<Date> getRangoFechasEstreno() {
    return rangoFechasEstreno;
}

public void setRangoFechasEstreno(List<Date> rangoFechasEstreno) {
    this.rangoFechasEstreno = rangoFechasEstreno;
}

public int getFirst() {
    return first;
}

public void setFirst(int first) {
    this.first = first;
}

public void aplicarFiltros() {
    first = 0;
}

public void limpiarFiltros() {
    filtroTitulo = null;
    rangoFechasEstreno = new ArrayList<>();
    first = 0;
}

private Date obtenerFechaInicio() {
    if (rangoFechasEstreno == null || rangoFechasEstreno.size() < 2) {
        return null;
    }
    return rangoFechasEstreno.get(0);
}

private Date obtenerFechaFin() {
    if (rangoFechasEstreno == null || rangoFechasEstreno.size() < 2) {
        return null;
    }
    return rangoFechasEstreno.get(1);
   }
}
