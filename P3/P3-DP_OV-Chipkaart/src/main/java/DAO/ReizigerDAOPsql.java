package DAO;

import domain.Adres;
import domain.Reiziger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO
{
    private final Connection _connection;
    private AdresDAO adresDAO;

    public ReizigerDAOPsql(Connection connection)
    {
        this._connection = connection;
    }

    public void setAdresDAO(AdresDAO adresDAO)
    {
        this.adresDAO = adresDAO;
    }

    @Override
    public boolean save(Reiziger reiziger)
    {
        String sql = """
        INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = _connection.prepareStatement(sql))
        {
            ps.setInt(1, reiziger.getId());
            ps.setString(2, reiziger.getVoorletters());

            if (reiziger.getTussenvoegsel() == null || reiziger.getTussenvoegsel().isBlank())
            {
                ps.setNull(3, Types.VARCHAR);
            }
            else
            {
                ps.setString(3, reiziger.getTussenvoegsel());
            }

            ps.setString(4, reiziger.getAchternaam());
            ps.setDate(5, reiziger.getGeboortedatum());

            boolean ok = ps.executeUpdate() == 1;

            if (ok && adresDAO != null && reiziger.getAdres() != null)
            {
                Adres a = reiziger.getAdres();
                a.setReiziger(reiziger);
                ok = adresDAO.save(a);
            }

            return ok;
        }
        catch (SQLException e)
        {
            System.err.println("Error in save(Reiziger): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Reiziger reiziger)
    {
        String sql = """
        UPDATE reiziger
        SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ?
        WHERE reiziger_id = ?
        """;

        try (PreparedStatement ps = _connection.prepareStatement(sql))
        {
            ps.setString(1, reiziger.getVoorletters());

            if (reiziger.getTussenvoegsel() == null || reiziger.getTussenvoegsel().isBlank())
            {
                ps.setNull(2, Types.VARCHAR);
            }
            else
            {
                ps.setString(2, reiziger.getTussenvoegsel());
            }

            ps.setString(3, reiziger.getAchternaam());
            ps.setDate(4, reiziger.getGeboortedatum());
            ps.setInt(5, reiziger.getId());

            boolean ok = ps.executeUpdate() == 1;

            if (ok && adresDAO != null)
            {
                Adres bestaand = adresDAO.findByReiziger(reiziger);

                if (reiziger.getAdres() == null)
                {
                    if (bestaand != null) ok = adresDAO.delete(bestaand);
                }
                else
                {
                    Adres a = reiziger.getAdres();
                    a.setReiziger(reiziger);

                    if (bestaand == null)
                    {
                        ok = adresDAO.save(a);
                    }
                    else
                    {
                        a.setId(bestaand.getId());
                        ok = adresDAO.update(a);
                    }
                }
            }

            return ok;
        }
        catch (SQLException e)
        {
            System.err.println("Error in update(Reiziger): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Reiziger reiziger)
    {
        String sql = "DELETE FROM reiziger WHERE reiziger_id = ?";

        try
        {
            if (adresDAO != null)
            {
                Adres a = adresDAO.findByReiziger(reiziger);
                if (a != null) adresDAO.delete(a);
            }

            try (PreparedStatement ps = _connection.prepareStatement(sql))
            {
                ps.setInt(1, reiziger.getId());
                return ps.executeUpdate() == 1;
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in delete(Reiziger): " + e.getMessage());
            return false;
        }
    }

    @Override
    public Reiziger findById(int id)
    {
        String sql = """
        SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum
        FROM reiziger WHERE reiziger_id = ?
        """;

        try (PreparedStatement ps = _connection.prepareStatement(sql))
        {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    int reizigerId = rs.getInt("reiziger_id");
                    String voorletters = rs.getString("voorletters");
                    String tussenvoegsel = rs.getString("tussenvoegsel");
                    String achternaam = rs.getString("achternaam");
                    Date geboortedatum = rs.getDate("geboortedatum");

                    Reiziger r = new Reiziger(reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum);

                    if (adresDAO != null)
                    {
                        r.setAdres(adresDAO.findByReiziger(r));
                    }

                    return r;
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in findById(int): " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Reiziger> findByGbdatum(Date datum)
    {
        String sql = """
        SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum
        FROM reiziger 
        WHERE geboortedatum = ?
        """;

        List<Reiziger> result = new ArrayList<>();

        try (PreparedStatement ps = _connection.prepareStatement(sql))
        {
            ps.setDate(1, datum);

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    int reizigerId = rs.getInt("reiziger_id");
                    String voorletters = rs.getString("voorletters");
                    String tussenvoegsel = rs.getString("tussenvoegsel");
                    String achternaam = rs.getString("achternaam");
                    Date geboortedatum = rs.getDate("geboortedatum");

                    Reiziger r = new Reiziger(reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum);

                    if (adresDAO != null)
                    {
                        r.setAdres(adresDAO.findByReiziger(r));
                    }

                    result.add(r);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in findByGbdatum(Date): " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Reiziger> findAll()
    {
        String sql = """
        SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum
        FROM reiziger
        """;

        List<Reiziger> result = new ArrayList<>();

        try (PreparedStatement ps = _connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                int reizigerId = rs.getInt("reiziger_id");
                String voorletters = rs.getString("voorletters");
                String tussenvoegsel = rs.getString("tussenvoegsel");
                String achternaam = rs.getString("achternaam");
                Date geboortedatum = rs.getDate("geboortedatum");

                Reiziger r = new Reiziger(reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum);

                if (adresDAO != null)
                {
                    r.setAdres(adresDAO.findByReiziger(r));
                }

                result.add(r);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error in findAll(): " + e.getMessage());
        }
        return result;
    }
}
