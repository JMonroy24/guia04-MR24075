package sv.edu.ues.occ.ingenieria.ppi115_2026.guias.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.guias.entity.Pelicula;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jmonroy
 */
@Stateless
public class PeliculaRepository implements Serializable {

    @PersistenceContext(unitName = "Peliculas-PU")
    private EntityManager em;

    public int count() {
        return ((Long) em.createQuery("SELECT COUNT(p) FROM Pelicula p").getSingleResult()).intValue();
    }

    public List<Pelicula> findRange(int first, int max) {
        return em.createQuery("SELECT DISTINCT p FROM Pelicula p LEFT JOIN FETCH p.idDirector LEFT JOIN FETCH p.generoList ORDER BY p.idPelicula ASC", Pelicula.class)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

public int contarFiltradas(String titulo, java.util.Date fechaInicio,
        java.util.Date fechaFin) {
    String jpql = "SELECT COUNT(p) FROM Pelicula p"
            + construirCondiciones(titulo, fechaInicio, fechaFin);

    Query consulta = em.createQuery(jpql);
    asignarParametros(consulta, titulo, fechaInicio, fechaFin);

    return Math.toIntExact((Long) consulta.getSingleResult());
}

public List<Pelicula> listarFiltradas(int offset, int pageSize,
        String titulo, java.util.Date fechaInicio, java.util.Date fechaFin,
        String campoOrdenamiento, boolean ascendente) {
    String campo = switch (campoOrdenamiento == null ? "" : campoOrdenamiento) {
        case "titulo" -> "p.titulo";
        case "calificacion" -> "p.calificacion";
        default -> "p.idPelicula";
    };

    String direccion = ascendente ? "ASC" : "DESC";
    String jpqlIds = "SELECT p.idPelicula FROM Pelicula p"
            + construirCondiciones(titulo, fechaInicio, fechaFin)
            + " ORDER BY " + campo + " " + direccion
            + ", p.idPelicula ASC";

    TypedQuery<Integer> consultaIds = em.createQuery(jpqlIds, Integer.class);
    asignarParametros(consultaIds, titulo, fechaInicio, fechaFin);

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
    public List<Pelicula> listarTodasOrdenASC() {
        return em.createQuery("SELECT DISTINCT p FROM Pelicula p LEFT JOIN FETCH p.idDirector LEFT JOIN FETCH p.generoList ORDER BY p.titulo ASC", Pelicula.class)
                .getResultList();
    }

    public List<Pelicula> listarCienciaFiccionYSuspense() {
        return em.createQuery("SELECT p FROM Pelicula p "
                + "WHERE EXISTS (SELECT g1 FROM p.generoList g1 WHERE g1.nombre = 'Ciencia Ficción') "
                + "AND EXISTS (SELECT g2 FROM p.generoList g2 WHERE g2.nombre = 'Suspense')", Pelicula.class)
                .getResultList();
    }

    public List<Pelicula> listarExcelentes() {
        return em.createQuery(
                "SELECT DISTINCT p FROM Pelicula p "
                + "JOIN FETCH p.idDirector "
                + "LEFT JOIN FETCH p.generoList "
                + "WHERE p.calificacion >= :calificacionMinima "
                + "ORDER BY p.calificacion DESC",
                Pelicula.class)
                .setParameter(
                        "calificacionMinima",
                        new BigDecimal("8.5"))
                .getResultList();
    }

    public List<Pelicula> listarPorChristopherNolan() {
        return em.createQuery(
                "SELECT DISTINCT p FROM Pelicula p "
                + "JOIN FETCH p.idDirector d "
                + "LEFT JOIN FETCH p.generoList "
                + "WHERE d.nombres = :nombres "
                + "AND d.apellidos = :apellidos "
                + "ORDER BY p.fechaEstreno DESC",
                Pelicula.class)
                .setParameter("nombres", "Christopher")
                .setParameter("apellidos", "Nolan")
                .getResultList();
    }

    public List<Pelicula> listarAnios2000a2010() {
        return em.createQuery(
                "SELECT DISTINCT p FROM Pelicula p "
                + "JOIN FETCH p.idDirector "
                + "LEFT JOIN FETCH p.generoList "
                + "WHERE p.fechaEstreno BETWEEN :fechaInicio AND :fechaFin",
                Pelicula.class)
                .setParameter(
                        "fechaInicio",
                        Date.valueOf("2000-01-01"))
                .setParameter(
                        "fechaFin",
                        Date.valueOf("2010-12-31"))
                .getResultList();
    }

        public List<Pelicula> listarAccionPost2010() {
        return em.createQuery(
                "SELECT DISTINCT p FROM Pelicula p "
                + "JOIN FETCH p.idDirector "
                + "LEFT JOIN FETCH p.generoList "
                + "WHERE EXISTS (SELECT g FROM p.generoList g WHERE g.nombre = 'Acción') "
                + "AND p.fechaEstreno > :fechaLimite "
                + "ORDER BY p.fechaEstreno DESC",
                Pelicula.class)
                .setParameter("fechaLimite", Date.valueOf("2010-12-31"))
                .getResultList();
    }

    public List<Pelicula> listarDramaMayorA8() {
        return em.createQuery(
                "SELECT DISTINCT p FROM Pelicula p "
                + "JOIN FETCH p.idDirector "
                + "LEFT JOIN FETCH p.generoList "
                + "WHERE EXISTS (SELECT g FROM p.generoList g WHERE g.nombre = 'Drama') "
                + "AND p.calificacion > :calificacionMinima "
                + "ORDER BY p.calificacion DESC",
                Pelicula.class)
                .setParameter("calificacionMinima", new BigDecimal("8.0"))
                .getResultList();
    }
private String construirCondiciones(String titulo,
        java.util.Date fechaInicio, java.util.Date fechaFin) {
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

    return condiciones.toString();
}

private Query asignarParametros(Query consulta, String titulo,
        java.util.Date fechaInicio, java.util.Date fechaFin) {
    if (titulo != null && !titulo.isBlank()) {
        consulta.setParameter("titulo", "%" + titulo.trim().toLowerCase() + "%");
    }
    if (fechaInicio != null) {
        consulta.setParameter("fechaInicio", fechaInicio);
    }
    if (fechaFin != null) {
        consulta.setParameter("fechaFin", fechaFin);
    }

    return consulta;
    }
}
