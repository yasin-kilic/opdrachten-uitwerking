package DAO;

import domain.OVChipkaart;
import domain.Reiziger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {

    private final Connection conn;
    private ReizigerDAO reizigerDAO;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    @Override
    public boolean save(OVChipkaart kaart) {
        String sql = """
            INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaart.getKaartNummer());
            ps.setDate(2, kaart.getGeldigTot());
            ps.setInt(3, kaart.getKlasse());
            ps.setBigDecimal(4, kaart.getSaldo());

            if (kaart.getReiziger() == null) {
                throw new IllegalArgumentException("OVChipkaart.save: kaart.reiziger moet gezet zijn (met id).");
            }
            ps.setInt(5, kaart.getReiziger().getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error in OVChipkaart.save: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(OVChipkaart kaart) {
        String sql = """
            UPDATE ov_chipkaart
            SET geldig_tot=?, klasse=?, saldo=?, reiziger_id=?
            WHERE kaart_nummer=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, kaart.getGeldigTot());
            ps.setInt(2, kaart.getKlasse());
            ps.setBigDecimal(3, kaart.getSaldo());

            if (kaart.getReiziger() == null) {
                throw new IllegalArgumentException("OVChipkaart.update: kaart.reiziger moet gezet zijn (met id).");
            }
            ps.setInt(4, kaart.getReiziger().getId());

            ps.setInt(5, kaart.getKaartNummer());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error in OVChipkaart.update: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart kaart) {
        String sql = "DELETE FROM ov_chipkaart WHERE kaart_nummer=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kaart.getKaartNummer());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error in OVChipkaart.delete: " + e.getMessage());
            return false;
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
                return mapOVChipkaart(rs, reiziger);
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
                    result.add(mapOVChipkaart(rs, reiziger));
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
}
