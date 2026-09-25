package DAO;

import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {

    private final Connection conn;
    private ReizigerDAO reizigerDAO;
    private ProductDAO productDAO;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public boolean save(OVChipkaart kaart) {
        String sql = """
            INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean opgeslagen;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, kaart.getKaartNummer());
                ps.setDate(2, kaart.getGeldigTot());
                ps.setInt(3, kaart.getKlasse());
                ps.setBigDecimal(4, kaart.getSaldo());

                if (kaart.getReiziger() == null) {
                    throw new IllegalArgumentException("OVChipkaart.save: kaart.reiziger moet gezet zijn (met id).");
                }
                ps.setInt(5, kaart.getReiziger().getId());

                opgeslagen = ps.executeUpdate() == 1;
            }

            if (opgeslagen) {
                for (Product product : kaart.getProducten()) {
                    insertKoppeling(kaart.getKaartNummer(), product.getProductNummer());
                }
                conn.commit();
            } else {
                conn.rollback();
            }
            return opgeslagen;
        } catch (SQLException | IllegalArgumentException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            System.err.println("Error in OVChipkaart.save: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) {}
        }
    }

    @Override
    public boolean update(OVChipkaart kaart) {
        String sql = """
            UPDATE ov_chipkaart
            SET geldig_tot=?, klasse=?, saldo=?, reiziger_id=?
            WHERE kaart_nummer=?
        """;
        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean bijgewerkt;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, kaart.getGeldigTot());
                ps.setInt(2, kaart.getKlasse());
                ps.setBigDecimal(3, kaart.getSaldo());

                if (kaart.getReiziger() == null) {
                    throw new IllegalArgumentException("OVChipkaart.update: kaart.reiziger moet gezet zijn (met id).");
                }
                ps.setInt(4, kaart.getReiziger().getId());

                ps.setInt(5, kaart.getKaartNummer());
                bijgewerkt = ps.executeUpdate() == 1;
            }

            if (bijgewerkt) {
                Set<Integer> bestaandeProducten = findProductNummers(kaart.getKaartNummer());
                Set<Integer> nieuweProducten = new HashSet<>();
                for (Product product : kaart.getProducten()) {
                    nieuweProducten.add(product.getProductNummer());
                }

                for (Integer productNummer : bestaandeProducten) {
                    if (!nieuweProducten.contains(productNummer)) {
                        deleteKoppeling(kaart.getKaartNummer(), productNummer);
                    }
                }
                for (Integer productNummer : nieuweProducten) {
                    if (!bestaandeProducten.contains(productNummer)) {
                        insertKoppeling(kaart.getKaartNummer(), productNummer);
                    }
                }
                conn.commit();
            } else {
                conn.rollback();
            }
            return bijgewerkt;
        } catch (SQLException | IllegalArgumentException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            System.err.println("Error in OVChipkaart.update: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) {}
        }
    }

    @Override
    public boolean delete(OVChipkaart kaart) {
        String sql = "DELETE FROM ov_chipkaart WHERE kaart_nummer=?";
        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ov_chipkaart_product WHERE kaart_nummer=?")) {
                ps.setInt(1, kaart.getKaartNummer());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, kaart.getKaartNummer());
                boolean verwijderd = ps.executeUpdate() == 1;

                if (verwijderd) conn.commit();
                else conn.rollback();
                return verwijderd;
            }
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            System.err.println("Error in OVChipkaart.delete: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) {}
        }
    }

    @Override
    public OVChipkaart findById(int kaartNummer) {
        String sql = """
            SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
            FROM ov_chipkaart
            WHERE kaart_nummer=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaartNummer);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reiziger reiziger = reizigerDAO.findById(rs.getInt("reiziger_id"));
                OVChipkaart kaart = mapOVChipkaart(rs, reiziger);
                vulProducten(kaart);
                return kaart;
            }
        } catch (SQLException e) {
            System.err.println("Error in OVChipkaart.findById: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        String sql = """
            SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
            FROM ov_chipkaart
            WHERE reiziger_id=?
            ORDER BY kaart_nummer
        """;
        List<OVChipkaart> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reiziger.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OVChipkaart kaart = mapOVChipkaart(rs, reiziger);
                    vulProducten(kaart);
                    result.add(kaart);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in OVChipkaart.findByReiziger: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<OVChipkaart> findAll() {
        List<OVChipkaart> result = new ArrayList<>();
        for (Reiziger reiziger : reizigerDAO.findAll()) {
            result.addAll(reiziger.getOvChipkaarten());
        }
        return result;
    }

    private OVChipkaart mapOVChipkaart(ResultSet rs, Reiziger reiziger) throws SQLException {
        int kaartNummer = rs.getInt("kaart_nummer");
        Date geldigTot = rs.getDate("geldig_tot");
        int klasse = rs.getInt("klasse");
        BigDecimal saldo = rs.getBigDecimal("saldo");

        return new OVChipkaart(kaartNummer, geldigTot, klasse, saldo, reiziger);
    }

    private void vulProducten(OVChipkaart kaart) {
        if (productDAO == null) return;

        for (Product product : productDAO.findByOVChipkaart(kaart)) {
            kaart.voegToeProduct(product);
        }
    }

    private void insertKoppeling(int kaartNummer, int productNummer) throws SQLException {
        String sql = """
            INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaartNummer);
            ps.setInt(2, productNummer);
            ps.executeUpdate();
        }
    }

    private void deleteKoppeling(int kaartNummer, int productNummer) throws SQLException {
        String sql = """
            DELETE FROM ov_chipkaart_product
            WHERE kaart_nummer=? AND product_nummer=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaartNummer);
            ps.setInt(2, productNummer);
            ps.executeUpdate();
        }
    }

    private Set<Integer> findProductNummers(int kaartNummer) throws SQLException {
        String sql = """
            SELECT product_nummer
            FROM ov_chipkaart_product
            WHERE kaart_nummer=?
        """;
        Set<Integer> productNummers = new HashSet<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaartNummer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productNummers.add(rs.getInt("product_nummer"));
                }
            }
        }
        return productNummers;
    }
}
