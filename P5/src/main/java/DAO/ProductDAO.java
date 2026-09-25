package DAO;

import domain.OVChipkaart;
import domain.Product;

import java.util.List;

public interface ProductDAO {
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(Product product);

    Product findByProductNummer(int productNummer);
    List<Product> findByOVChipkaart(OVChipkaart kaart);
    List<Product> findAll();
}