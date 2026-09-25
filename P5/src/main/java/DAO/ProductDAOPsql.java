package DAO;

import domain.OVChipkaart;
import domain.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private final Connection conn;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Product product) {
        String sql = """
            INSERT INTO product (product_nummer, naam, beschrijving, prijs)
            VALUES (?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProductNummer());
            ps.setString(2, product.getNaam());
            ps.setString(3, product.getBeschrijving());
            ps.setBigDecimal(4, product.getPrijs());

            if (ps.executeUpdate() != 1) return false;

            saveRelationships(product);
            return true;
        } catch (SQLException e) {
            System.err.println("Error in Product.save: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = """
            UPDATE product
            SET naam=?, beschrijving=?, prijs=?
            WHERE product_nummer=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getNaam());
            ps.setString(2, product.getBeschrijving());
            ps.setBigDecimal(3, product.getPrijs());
            ps.setInt(4, product.getProductNummer());

            if (ps.executeUpdate() != 1) return false;

            deleteRelationships(product);
            saveRelationships(product);
            return true;
        } catch (SQLException e) {
            System.err.println("Error in Product.update: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        String sql = "DELETE FROM product WHERE product_nummer=?";
        try {
            deleteRelationships(product);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, product.getProductNummer());
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error in Product.delete: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Product findByProductNummer(int productNummer) {
        String sql = """
            SELECT product_nummer, naam, beschrijving, prijs
            FROM product
            WHERE product_nummer=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productNummer);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Product product = mapProduct(rs);
                laadOVChipkaarten(product);
                return product;
            }
        } catch (SQLException e) {
            System.err.println("Error in Product.findByProductNummer: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart kaart) {
        String sql = """
            SELECT p.product_nummer, p.naam, p.beschrijving, p.prijs
            FROM product p
            JOIN ov_chipkaart_product okp ON okp.product_nummer = p.product_nummer
            WHERE okp.kaart_nummer = ?
            ORDER BY p.product_nummer
        """;
        List<Product> producten = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaart.getKaartNummer());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = mapProduct(rs);
                    product.voegToeOVChipkaart(kaart);
                    producten.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in Product.findByOVChipkaart: " + e.getMessage());
        }
        return producten;
    }

    @Override
    public List<Product> findAll() {
        String sql = """
            SELECT product_nummer, naam, beschrijving, prijs
            FROM product
            ORDER BY product_nummer
        """;
        List<Product> producten = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                producten.add(mapProduct(rs));
            }

            for (Product product : producten) {
                laadOVChipkaarten(product);
            }
        } catch (SQLException e) {
            System.err.println("Error in Product.findAll: " + e.getMessage());
        }
        return producten;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        int productNummer = rs.getInt("product_nummer");
        String naam = rs.getString("naam");
        String beschrijving = rs.getString("beschrijving");
        BigDecimal prijs = rs.getBigDecimal("prijs");

        return new Product(productNummer, naam, beschrijving, prijs);
    }

    private void laadOVChipkaarten(Product product) throws SQLException {
        String sql = """
            SELECT k.kaart_nummer, k.geldig_tot, k.klasse, k.saldo
            FROM ov_chipkaart k
            JOIN ov_chipkaart_product okp ON okp.kaart_nummer = k.kaart_nummer
            WHERE okp.product_nummer = ?
            ORDER BY k.kaart_nummer
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProductNummer());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OVChipkaart kaart = new OVChipkaart(
                            rs.getInt("kaart_nummer"),
                            rs.getDate("geldig_tot"),
                            rs.getInt("klasse"),
                            rs.getBigDecimal("saldo"),
                            null
                    );
                    product.voegToeOVChipkaart(kaart);
                }
            }
        }
    }

    private void saveRelationships(Product product) throws SQLException {
        String sql = """
            INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer)
            VALUES (?, ?)
        """;

        for (OVChipkaart kaart : product.getOvChipkaarten()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, kaart.getKaartNummer());
                ps.setInt(2, product.getProductNummer());
                ps.executeUpdate();
            }
        }
    }

    private void deleteRelationships(Product product) throws SQLException {
        String sql = "DELETE FROM ov_chipkaart_product WHERE product_nummer=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProductNummer());
            ps.executeUpdate();
        }
    }
}
