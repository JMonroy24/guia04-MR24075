package sv.edu.ues.occ.ingenieria.ppi115_2026.guias.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.guias.entity.Pelicula;

/**
 *
 * @author jmonroy
 */
@Stateless
public class PeliculaRepository implements Serializable {

    @PersistenceContext(unitName = "Peliculas-PU")
    private EntityManager em;

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
}
