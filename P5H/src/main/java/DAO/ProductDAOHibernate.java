package DAO;

import domain.OVChipkaart;
import domain.Product;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDAOHibernate implements ProductDAO {

    private final EntityManagerFactory emf;

    public ProductDAOHibernate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public boolean save(Product product) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Set<OVChipkaart> kaarten = new HashSet<>(product.getOvChipkaarten());
            product.getOvChipkaarten().clear();
            em.persist(product);

            for (OVChipkaart kaart : kaarten) {
                OVChipkaart managedKaart = em.find(OVChipkaart.class, kaart.getKaartNummer());
                if (managedKaart == null) {
                    throw new IllegalArgumentException("OV-chipkaart " + kaart.getKaartNummer() + " bestaat niet.");
                }
                managedKaart.addProduct(product);
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
    public boolean update(Product product) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Product managedProduct = em.find(Product.class, product.getProductNummer());
            if (managedProduct == null) {
                tx.rollback();
                return false;
            }

            managedProduct.setNaam(product.getNaam());
            managedProduct.setBeschrijving(product.getBeschrijving());
            managedProduct.setPrijs(product.getPrijs());

            Set<Integer> gewensteKaarten = new HashSet<>();
            for (OVChipkaart kaart : product.getOvChipkaarten()) {
                gewensteKaarten.add(kaart.getKaartNummer());
            }

            for (OVChipkaart kaart : new HashSet<>(managedProduct.getOvChipkaarten())) {
                if (!gewensteKaarten.contains(kaart.getKaartNummer())) {
                    kaart.removeProduct(managedProduct);
                }
            }

            for (Integer kaartNummer : gewensteKaarten) {
                OVChipkaart managedKaart = em.find(OVChipkaart.class, kaartNummer);
                if (managedKaart == null) {
                    throw new IllegalArgumentException("OV-chipkaart " + kaartNummer + " bestaat niet.");
                }
                managedKaart.addProduct(managedProduct);
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
    public boolean delete(Product product) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            em.createNativeQuery(
                    "DELETE FROM ov_chipkaart_product WHERE product_nummer = :id"
            )
                    .setParameter("id", product.getProductNummer())
                    .executeUpdate();

            int resultaat = em.createQuery(
                    "DELETE FROM Product p WHERE p.productNummer = :id"
            )
                    .setParameter("id", product.getProductNummer())
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
    public Product findByProductNummer(int productNummer) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Product> q = em.createQuery(
                    "SELECT DISTINCT p FROM Product p " +
                            "LEFT JOIN FETCH p.ovChipkaarten k " +
                            "LEFT JOIN FETCH k.producten " +
                            "WHERE p.productNummer = :pn",
                    Product.class
            );
            q.setParameter("pn", productNummer);
            List<Product> producten = q.getResultList();
            return producten.isEmpty() ? null : producten.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart kaart) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Product p " +
                            "JOIN p.ovChipkaarten gezochteKaart " +
                            "LEFT JOIN FETCH p.ovChipkaarten k " +
                            "LEFT JOIN FETCH k.producten " +
                            "WHERE gezochteKaart.kaartNummer = :kn " +
                                    "ORDER BY p.productNummer",
                            Product.class
                    )
                    .setParameter("kn", kaart.getKaartNummer())
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT p FROM Product p " +
                                    "LEFT JOIN FETCH p.ovChipkaarten k " +
                                    "LEFT JOIN FETCH k.producten " +
                                    "ORDER BY p.productNummer",
                            Product.class
                    )
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
