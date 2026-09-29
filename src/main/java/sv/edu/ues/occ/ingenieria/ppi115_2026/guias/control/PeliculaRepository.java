package sv.edu.ues.occ.ingenieria.ppi115_2026.guias.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sv.edu.ues.occ.ingenieria.ppi115_2026.guias.entity.Genero;
import sv.edu.ues.occ.ingenieria.ppi115_2026.guias.entity.Pelicula;

@Stateless
public class PeliculaRepository implements Serializable {

    @PersistenceContext(unitName = "Peliculas-PU")
    private EntityManager em;

    public int count() {
        return ((Long) em.createQuery("SELECT COUNT(p) FROM Pelicula p").getSingleResult()).intValue();
    }

    /**
     * Obtiene todos los géneros para llenar el menú desplegable (p:selectCheckboxMenu).
     */
    public List<Genero> obtenerTodosLosGeneros() {
        return em.createQuery("SELECT g FROM Genero g ORDER BY g.nombre ASC", Genero.class).getResultList();
    }

    public List<Pelicula> findRange(int first, int max) {
        return em.createQuery("SELECT DISTINCT p FROM Pelicula p LEFT JOIN FETCH p.idDirector LEFT JOIN FETCH p.generoList ORDER BY p.idPelicula ASC", Pelicula.class)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public int contarFiltradas(String titulo, java.util.Date fechaInicio,
            java.util.Date fechaFin, List<Integer> generosSeleccionados, String filtroDirector) {
        String jpql = "SELECT COUNT(p) FROM Pelicula p"
                + construirCondiciones(titulo, fechaInicio, fechaFin, generosSeleccionados, filtroDirector);

        Query consulta = em.createQuery(jpql);
        asignarParametros(consulta, titulo, fechaInicio, fechaFin, generosSeleccionados, filtroDirector);

        return Math.toIntExact((Long) consulta.getSingleResult());
    }

    public List<Pelicula> listarFiltradas(int offset, int pageSize,
            String titulo, java.util.Date fechaInicio, java.util.Date fechaFin,
            List<Integer> generosSeleccionados, String filtroDirector,
            String campoOrdenamiento, boolean ascendente) {
        
        String campo = switch (campoOrdenamiento == null ? "" : campoOrdenamiento) {
            case "titulo" -> "p.titulo";
            case "calificacion" -> "p.calificacion";
            default -> "p.idPelicula";
        };

        String direccion = ascendente ? "ASC" : "DESC";
        String jpqlIds = "SELECT p.idPelicula FROM Pelicula p"
                + construirCondiciones(titulo, fechaInicio, fechaFin, generosSeleccionados, filtroDirector)
                + " ORDER BY " + campo + " " + direccion
                + ", p.idPelicula ASC";

        TypedQuery<Integer> consultaIds = em.createQuery(jpqlIds, Integer.class);
        asignarParametros(consultaIds, titulo, fechaInicio, fechaFin, generosSeleccionados, filtroDirector);

        List<Integer> ids = consultaIds
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Pelicula> resultados = em.createQuery(
                "SELECT DISTINCT p FROM Pelicula p "
                + "LEFT JOIN FETCH p.idDirector "
                + "LEFT JOIN FETCH p.generoList "
                + "WHERE p.idPelicula IN :ids",
                Pelicula.class)
                .setParameter("ids", ids)
                .getResultList();

        Map<Integer, Pelicula> porId = new HashMap<>();
        for (Pelicula pelicula : resultados) {
            porId.put(pelicula.getIdPelicula(), pelicula);
        }

        List<Pelicula> ordenados = new ArrayList<>();
        for (Integer id : ids) {
            Pelicula pelicula = porId.get(id);
            if (pelicula != null) {
                ordenados.add(pelicula);
            }
        }

        return ordenados;
    }

    private String construirCondiciones(String titulo,
            java.util.Date fechaInicio, java.util.Date fechaFin,
            List<Integer> generosSeleccionados, String filtroDirector) {
        
        StringBuilder condiciones = new StringBuilder(" WHERE 1 = 1");

        if (titulo != null && !titulo.isBlank()) {
            condiciones.append(" AND LOWER(p.titulo) LIKE :titulo");
        }
        if (fechaInicio != null) {
            condiciones.append(" AND p.fechaEstreno >= :fechaInicio");
        }
        if (fechaFin != null) {
            condiciones.append(" AND p.fechaEstreno <= :fechaFin");
        }
        // ASIGNACIÓN 1: Filtro por Género (Relación @ManyToMany)
        if (generosSeleccionados != null && !generosSeleccionados.isEmpty()) {
            condiciones.append(" AND EXISTS (SELECT g FROM p.generoList g WHERE g.idGenero IN :generosSeleccionados)");
        }
        // ASIGNACIÓN 2: Filtro por Director (Concatenación de nombres y apellidos)
        if (filtroDirector != null && !filtroDirector.isBlank()) {
            condiciones.append(" AND (LOWER(CONCAT(p.idDirector.nombres, ' ', p.idDirector.apellidos)) LIKE :filtroDirector "
                             + "OR LOWER(CONCAT(p.idDirector.apellidos, ' ', p.idDirector.nombres)) LIKE :filtroDirector)");
        }

        return condiciones.toString();
    }

    private Query asignarParametros(Query consulta, String titulo,
            java.util.Date fechaInicio, java.util.Date fechaFin,
            List<Integer> generosSeleccionados, String filtroDirector) {
        
        if (titulo != null && !titulo.isBlank()) {
            consulta.setParameter("titulo", "%" + titulo.trim().toLowerCase() + "%");
        }
        if (fechaInicio != null) {
            consulta.setParameter("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            consulta.setParameter("fechaFin", fechaFin);
        }
        if (generosSeleccionados != null && !generosSeleccionados.isEmpty()) {
            consulta.setParameter("generosSeleccionados", generosSeleccionados);
        }
        if (filtroDirector != null && !filtroDirector.isBlank()) {
            consulta.setParameter("filtroDirector", "%" + filtroDirector.trim().toLowerCase() + "%");
        }

        return consulta;
    }

    public List<Pelicula> listarTodasOrdenASC() {
        return em.createQuery("SELECT DISTINCT p FROM Pelicula p LEFT JOIN FETCH p.idDirector LEFT JOIN FETCH p.generoList ORDER BY p.titulo ASC", Pelicula.class)
                .getResultList();
    }
}