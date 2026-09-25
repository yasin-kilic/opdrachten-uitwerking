package DAO;

import domain.Reiziger;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

public class ReizigerDAOHibernate implements ReizigerDAO {

    private final EntityManagerFactory emf;

    public ReizigerDAOHibernate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(reiziger);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(Reiziger reiziger) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(reiziger);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            em.createNativeQuery(
                    "DELETE FROM ov_chipkaart_product " +
                            "WHERE kaart_nummer IN " +
                            "(SELECT kaart_nummer FROM ov_chipkaart WHERE reiziger_id = :id)"
            )
                    .setParameter("id", reiziger.getId())
                    .executeUpdate();

            em.createQuery(
                    "DELETE FROM OVChipkaart k WHERE k.reiziger.id = :id"
            )
                    .setParameter("id", reiziger.getId())
                    .executeUpdate();

            em.createQuery(
                    "DELETE FROM Adres a WHERE a.reiziger.id = :id"
            )
                    .setParameter("id", reiziger.getId())
                    .executeUpdate();

            int resultaat = em.createQuery(
                    "DELETE FROM Reiziger r WHERE r.id = :id"
            )
                    .setParameter("id", reiziger.getId())
                    .executeUpdate();
            tx.commit();
            return resultaat > 0;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public Reiziger findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Reiziger> q = em.createQuery(
                    "SELECT DISTINCT r FROM Reiziger r " +
                            "LEFT JOIN FETCH r.adres " +
                            "LEFT JOIN FETCH r.ovChipkaarten k " +
                            "LEFT JOIN FETCH k.producten " +
                            "WHERE r.id = :id",
                    Reiziger.class
            );
            q.setParameter("id", id);
            List<Reiziger> res = q.getResultList();
            return res.isEmpty() ? null : res.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reiziger> findByGbdatum(Date datum) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Reiziger> q = em.createQuery(
                    "SELECT DISTINCT r FROM Reiziger r " +
                            "LEFT JOIN FETCH r.adres " +
                            "LEFT JOIN FETCH r.ovChipkaarten " +
                            "WHERE r.geboortedatum = :d ORDER BY r.id",
                    Reiziger.class
            );
            q.setParameter("d", datum);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reiziger> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT r FROM Reiziger r " +
                                    "LEFT JOIN FETCH r.adres " +
                                    "LEFT JOIN FETCH r.ovChipkaarten " +
                                    "ORDER BY r.id",
                            Reiziger.class
                    )
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
