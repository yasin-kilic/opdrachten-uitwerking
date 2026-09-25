package DAO;

import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            Reiziger managedReiziger = em.getReference(Reiziger.class, kaart.getReiziger().getId());
            kaart.setReiziger(managedReiziger);

            Set<Product> producten = new HashSet<>(kaart.getProducten());
            kaart.getProducten().clear();
            for (Product product : producten) {
                Product managedProduct = em.find(Product.class, product.getProductNummer());
                if (managedProduct == null) {
                    throw new IllegalArgumentException("Product " + product.getProductNummer() + " bestaat niet.");
                }
                kaart.addProduct(managedProduct);
            }

            managedReiziger.addOvChipkaart(kaart);
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

            OVChipkaart managedKaart = em.merge(kaart);
            managedKaart.getReiziger().addOvChipkaart(managedKaart);

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
            em.createNativeQuery(
                    "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = :id"
            )
                    .setParameter("id", kaart.getKaartNummer())
                    .executeUpdate();

            int resultaat = em.createQuery(
                    "DELETE FROM OVChipkaart k WHERE k.kaartNummer = :id"
            )
                    .setParameter("id", kaart.getKaartNummer())
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
    public OVChipkaart findById(int kaartNummer) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OVChipkaart> q = em.createQuery(
                    "SELECT DISTINCT k FROM OVChipkaart k " +
                            "JOIN FETCH k.reiziger " +
                            "LEFT JOIN FETCH k.producten p " +
                            "LEFT JOIN FETCH p.ovChipkaarten " +
                            "WHERE k.kaartNummer = :kn",
                    OVChipkaart.class
            );
            q.setParameter("kn", kaartNummer);
            List<OVChipkaart> kaarten = q.getResultList();
            return kaarten.isEmpty() ? null : kaarten.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OVChipkaart> q = em.createQuery(
                    "SELECT DISTINCT k FROM OVChipkaart k " +
                            "JOIN FETCH k.reiziger r " +
                            "LEFT JOIN FETCH k.producten p " +
                            "LEFT JOIN FETCH p.ovChipkaarten " +
                            "WHERE r.id = :rid ORDER BY k.kaartNummer",
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
            return em.createQuery(
                            "SELECT DISTINCT k FROM OVChipkaart k " +
                                    "JOIN FETCH k.reiziger " +
                                    "LEFT JOIN FETCH k.producten p " +
                                    "LEFT JOIN FETCH p.ovChipkaarten " +
                                    "ORDER BY k.kaartNummer",
                            OVChipkaart.class
                    )
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
