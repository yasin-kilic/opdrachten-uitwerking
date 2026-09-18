package DAO;

import domain.OVChipkaart;
import domain.Reiziger;

import javax.persistence.*;
import java.util.List;

public class OVChipkaartDAOHibernate implements OVChipkaartDAO {

    private final EntityManagerFactory emf;

    public OVChipkaartDAOHibernate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public boolean save(OVChipkaart kaart) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            kaart.getReiziger().addOvChipkaart(kaart);
            em.persist(kaart);
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
    public boolean update(OVChipkaart kaart) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            kaart.getReiziger().addOvChipkaart(kaart);
            em.merge(kaart);
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
    public boolean delete(OVChipkaart kaart) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            OVChipkaart managedKaart = em.merge(kaart);
            em.remove(managedKaart);
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
    public OVChipkaart findById(int kaartNummer) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(OVChipkaart.class, kaartNummer);
        } finally {
            em.close();
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OVChipkaart> q = em.createQuery(
                    "SELECT k FROM OVChipkaart k WHERE k.reiziger.id = :rid ORDER BY k.kaartNummer",
                    OVChipkaart.class
            );
            q.setParameter("rid", reiziger.getId());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<OVChipkaart> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT k FROM OVChipkaart k ORDER BY k.kaartNummer", OVChipkaart.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
