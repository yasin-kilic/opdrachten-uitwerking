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
            Reiziger managed = em.find(Reiziger.class, reiziger.getId());
            if (managed != null) em.remove(managed);
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
    public Reiziger findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reiziger.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reiziger> findByGbdatum(Date datum) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Reiziger> q = em.createQuery(
                    "SELECT r FROM Reiziger r WHERE r.geboortedatum = :d ORDER BY r.id",
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
            return em.createQuery("SELECT r FROM Reiziger r ORDER BY r.id", Reiziger.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}