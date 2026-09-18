package DAO;

import domain.Adres;
import domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {

    private final Connection conn;
    private ReizigerDAO reizigerDAO;

    public AdresDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    @Override
    public boolean save(Adres adres) {
        String sql = """
            INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adres.getId());
            ps.setString(2, adres.getPostcode());
            ps.setString(3, adres.getHuisnummer());
            ps.setString(4, adres.getStraat());
            ps.setString(5, adres.getWoonplaats());

            if (adres.getReiziger() == null) {
                throw new IllegalArgumentException("Adres.save: adres.reiziger moet gezet zijn (met id).");
            }
            ps.setInt(6, adres.getReiziger().getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error in Adres.save: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Adres adres) {
        String sql = """
            UPDATE adres
            SET postcode=?, huisnummer=?, straat=?, woonplaats=?, reiziger_id=?
            WHERE adres_id=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adres.getPostcode());
            ps.setString(2, adres.getHuisnummer());
            ps.setString(3, adres.getStraat());
            ps.setString(4, adres.getWoonplaats());

            if (adres.getReiziger() == null) {
                throw new IllegalArgumentException("Adres.update: adres.reiziger moet gezet zijn (met id).");
            }
            ps.setInt(5, adres.getReiziger().getId());

            ps.setInt(6, adres.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error in Adres.update: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        String sql = "DELETE FROM adres WHERE adres_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adres.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error in Adres.delete: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Adres findById(int id) {
        String sql = """
            SELECT adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id
            FROM adres
            WHERE adres_id=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reiziger reiziger = reizigerDAO.findById(rs.getInt("reiziger_id"));
                return mapAdres(rs, reiziger);
            }
        } catch (SQLException e) {
            System.err.println("Error in Adres.findById: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger r) {
        String sql = """
            SELECT adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id
            FROM adres
            WHERE reiziger_id=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapAdres(rs, r);
            }
        } catch (SQLException e) {
            System.err.println("Error in Adres.findByReiziger: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Adres> findAll() {
        List<Adres> result = new ArrayList<>();
        for (Reiziger reiziger : reizigerDAO.findAll()) {
            if (reiziger.getAdres() != null) {
                result.add(reiziger.getAdres());
            }
        }
        return result;
    }

    private Adres mapAdres(ResultSet rs, Reiziger reiziger) throws SQLException {
        int adresId = rs.getInt("adres_id");
        String postcode = rs.getString("postcode");
        String huisnummer = rs.getString("huisnummer");
        String straat = rs.getString("straat");
        String woonplaats = rs.getString("woonplaats");

        return new Adres(adresId, postcode, huisnummer, straat, woonplaats, reiziger);
    }
}
