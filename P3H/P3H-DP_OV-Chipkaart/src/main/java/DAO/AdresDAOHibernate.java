package DAO;

import domain.Adres;
import domain.Reiziger;

import javax.persistence.*;
import java.util.List;

public class AdresDAOHibernate implements AdresDAO {

    private final EntityManagerFactory emf;

    public AdresDAOHibernate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public boolean save(Adres adres) {
        if (adres == null || adres.getReiziger() == null) {
            throw new IllegalArgumentException("Adres.save: adres en adres.reiziger moeten gezet zijn.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            adres.getReiziger().setAdres(adres);

            em.persist(adres);
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
    public boolean update(Adres adres) {
        if (adres == null || adres.getReiziger() == null) {
            throw new IllegalArgumentException("Adres.update: adres en adres.reiziger moeten gezet zijn.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            adres.getReiziger().setAdres(adres);

            em.merge(adres);
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
    public boolean delete(Adres adres) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Adres managed = em.find(Adres.class, adres.getId());
            if (managed != null) {

                Reiziger r = managed.getReiziger();
                if (r != null) r.setAdres(null);

                em.remove(managed);
            }

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
    public Adres findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Adres.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Adres> q = em.createQuery(
                    "SELECT a FROM Adres a WHERE a.reiziger.id = :rid",
                    Adres.class
            );
            q.setParameter("rid", reiziger.getId());
            List<Adres> res = q.getResultList();
            return res.isEmpty() ? null : res.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Adres> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Adres a ORDER BY a.id", Adres.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
