import DAO.*;
import domain.Adres;
import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main
{
    private static final String _url = "jdbc:postgresql://localhost:5432/ovchip";
    private static final String _gebruikersnaam = "postgres";
    private static final String _wachtwoord = "postgres";

    private static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(_url, _gebruikersnaam, _wachtwoord);
    }

    public static void main(String[] args)
    {
        try (Connection conn = getConnection())
        {
            AdresDAOPsql adresDAO = new AdresDAOPsql(conn);
            ProductDAOPsql productDAO = new ProductDAOPsql(conn);
            OVChipkaartDAOPsql ovChipkaartDAO = new OVChipkaartDAOPsql(conn);
            ReizigerDAOPsql reizigerDAO = new ReizigerDAOPsql(conn);
            adresDAO.setReizigerDAO(reizigerDAO);
            ovChipkaartDAO.setReizigerDAO(reizigerDAO);
            ovChipkaartDAO.setProductDAO(productDAO);
            reizigerDAO.setAdresDAO(adresDAO);
            reizigerDAO.setOVChipkaartDAO(ovChipkaartDAO);

            testReizigerDAO(reizigerDAO, adresDAO);
            testAdresDAO(adresDAO, reizigerDAO);
            testOVChipkaartDAO(ovChipkaartDAO, reizigerDAO);
            testProductDAO(productDAO, ovChipkaartDAO, reizigerDAO);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * P4. Reiziger DAO: persistentie van twee klassen met een een-op-veel-relatie
     *
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException
     */
    private static void testReizigerDAO(ReizigerDAO rdao, AdresDAO adao) throws SQLException
    {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        Reiziger bestaand = rdao.findById(77);
        if (bestaand != null)
        {
            rdao.delete(bestaand);
        }

        int nieuwAdresId = 777;
        Adres bestaandAdres = adao.findById(nieuwAdresId);
        if (bestaandAdres != null)
        {
            adao.delete(bestaandAdres);
        }

        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        Adres sietskeAdres = new Adres(nieuwAdresId, "3511LX", "37", "Nieuwegracht", "Utrecht", sietske);
        sietske.setAdres(sietskeAdres);

        OVChipkaart kaart1 = new OVChipkaart(11101, Date.valueOf("2028-12-31"), 2,
                new BigDecimal("25.00"), sietske);
        OVChipkaart kaart2 = new OVChipkaart(11102, Date.valueOf("2027-12-31"), 1,
                new BigDecimal("10.00"), sietske);
        sietske.voegToeOVChipkaart(kaart1);
        sietske.voegToeOVChipkaart(kaart2);

        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        System.out.println("[Test] ReizigerDAO.findById(77) geeft: ");
        Reiziger gevonden = rdao.findById(77);
        System.out.println(gevonden);
        for (OVChipkaart kaart : gevonden.getOvChipkaarten())
        {
            System.out.println(kaart);
        }
        System.out.println();

        System.out.println("[Test] ReizigerDAO.findByGbdatum(\"" + gbdatum + "\") geeft de volgende reizigers: ");
        List<Reiziger> reizigersOpGbdatum = rdao.findByGbdatum(Date.valueOf(gbdatum));
        for (Reiziger r : reizigersOpGbdatum)
        {
            System.out.println(r);
        }
        System.out.println();

        System.out.println("[Test] ReizigerDAO.update() wijzigt achternaam en OV-chipkaart van Sietske");
        sietske.setAchternaam("Boersma");
        kaart1.setSaldo(new BigDecimal("30.00"));
        kaart1.setKlasse(1);
        rdao.update(sietske);
        System.out.println("Na update: " + rdao.findById(77));
        System.out.println("Na update OV-chipkaart: " + rdao.findById(77).getOvChipkaarten().get(0));
        System.out.println();

        System.out.print("[Test] Aantal reizigers voor delete: " + rdao.findAll().size() + ", na ReizigerDAO.delete(): ");
        rdao.delete(sietske);
        System.out.println(rdao.findAll().size());
    }

    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException
    {
        System.out.println("\n---------- Test AdresDAO -------------");

        int reizigerId = 78;
        int adresId = 778;

        Reiziger bestaandeReiziger = rdao.findById(reizigerId);
        if (bestaandeReiziger != null)
        {
            rdao.delete(bestaandeReiziger);
        }

        Adres bestaandAdres = adao.findById(adresId);
        if (bestaandAdres != null)
        {
            adao.delete(bestaandAdres);
        }

        Reiziger reiziger = new Reiziger(reizigerId, "T", "de", "Tester", Date.valueOf("1990-01-01"));
        rdao.save(reiziger);

        Adres adres = new Adres(adresId, "3511LX", "37", "Nieuwegracht", "Utrecht", reiziger);
        reiziger.setAdres(adres);

        System.out.println("[Test] AdresDAO.save(): " + adao.save(adres));
        System.out.println("[Test] AdresDAO.findById(" + adresId + "): " + adao.findById(adresId));
        System.out.println("[Test] AdresDAO.findByReiziger(): " + adao.findByReiziger(reiziger));

        System.out.println("[Test] AdresDAO.findAll():");
        for (Adres a : adao.findAll())
        {
            System.out.println(a);
        }

        adres.setHuisnummer("38");
        System.out.println("[Test] AdresDAO.update(): " + adao.update(adres));
        System.out.println("Na update: " + adao.findById(adresId));

        System.out.println("[Test] AdresDAO.delete(): " + adao.delete(adres));
        System.out.println("Na delete: " + adao.findById(adresId));

        rdao.delete(reiziger);
    }

    private static void testOVChipkaartDAO(OVChipkaartDAO kdao, ReizigerDAO rdao) throws SQLException
    {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");

        int reizigerId = 79;
        int kaartNummer = 22202;

        Reiziger bestaandeReiziger = rdao.findById(reizigerId);
        if (bestaandeReiziger != null)
        {
            rdao.delete(bestaandeReiziger);
        }

        OVChipkaart bestaandeKaart = kdao.findById(kaartNummer);
        if (bestaandeKaart != null)
        {
            kdao.delete(bestaandeKaart);
        }

        Reiziger reiziger = new Reiziger(reizigerId, "Y", "de", "KTester", Date.valueOf("1991-01-01"));
        rdao.save(reiziger);

        OVChipkaart kaart = new OVChipkaart(kaartNummer, Date.valueOf("2029-12-31"), 2,
                new BigDecimal("20.00"), reiziger);
        reiziger.voegToeOVChipkaart(kaart);

        System.out.println("[Test] OVChipkaartDAO.save(): " + kdao.save(kaart));
        System.out.println("[Test] OVChipkaartDAO.findById(" + kaartNummer + "): " + kdao.findById(kaartNummer));

        System.out.println("[Test] OVChipkaartDAO.findByReiziger():");
        for (OVChipkaart k : kdao.findByReiziger(reiziger))
        {
            System.out.println(k);
        }

        System.out.println("[Test] OVChipkaartDAO.findAll():");
        for (OVChipkaart k : kdao.findAll())
        {
            System.out.println(k);
        }

        kaart.setSaldo(new BigDecimal("25.00"));
        kaart.setKlasse(1);
        System.out.println("[Test] OVChipkaartDAO.update(): " + kdao.update(kaart));
        System.out.println("Na update: " + kdao.findById(kaartNummer));

        System.out.println("[Test] OVChipkaartDAO.delete(): " + kdao.delete(kaart));
        reiziger.verwijderOVChipkaart(kaart);
        System.out.println("Na delete: " + kdao.findById(kaartNummer));

        rdao.delete(reiziger);
    }

    private static void testProductDAO(ProductDAO pdao, OVChipkaartDAO kdao, ReizigerDAO rdao) throws SQLException
    {
        System.out.println("\n---------- Test ProductDAO -------------");

        int reizigerId = 80;
        int kaartNummer1 = 33303;
        int kaartNummer2 = 33304;
        int productNummer1 = 9998;
        int productNummer2 = 9999;

        Reiziger bestaandeReiziger = rdao.findById(reizigerId);
        if (bestaandeReiziger != null)
        {
            rdao.delete(bestaandeReiziger);
        }

        Product bestaandProduct1 = pdao.findByProductNummer(productNummer1);
        if (bestaandProduct1 != null)
        {
            pdao.delete(bestaandProduct1);
        }
        Product bestaandProduct2 = pdao.findByProductNummer(productNummer2);
        if (bestaandProduct2 != null)
        {
            pdao.delete(bestaandProduct2);
        }

        Reiziger reiziger = new Reiziger(reizigerId, "P", "de", "ProductTester", Date.valueOf("1992-01-01"));
        rdao.save(reiziger);

        OVChipkaart kaart1 = new OVChipkaart(kaartNummer1, Date.valueOf("2030-12-31"), 2,
                new BigDecimal("15.00"), reiziger);
        OVChipkaart kaart2 = new OVChipkaart(kaartNummer2, Date.valueOf("2031-12-31"), 1,
                new BigDecimal("30.00"), reiziger);
        reiziger.voegToeOVChipkaart(kaart1);
        reiziger.voegToeOVChipkaart(kaart2);

        System.out.println("[Test] OVChipkaartDAO.save(kaart1): " + kdao.save(kaart1));

        Product product1 = new Product(productNummer1, "Weekend Vrij", "Onbeperkt reizen in het weekend",
                new BigDecimal("34.95"));
        Product product2 = new Product(productNummer2, "Dal Voordeel", "Korting tijdens daluren",
                new BigDecimal("5.60"));
        product1.voegToeOVChipkaart(kaart1);

        System.out.println("[Test] ProductDAO.save(product1 met kaart1): " + pdao.save(product1));
        System.out.println("[Test] ProductDAO.save(product2): " + pdao.save(product2));

        kaart2.voegToeProduct(product2);
        System.out.println("[Test] OVChipkaartDAO.save(kaart2 met product2): " + kdao.save(kaart2));
        System.out.println("Kaart 1 na ProductDAO.save(): " + kdao.findById(kaartNummer1));
        System.out.println("Kaart 2 na OVChipkaartDAO.save(): " + kdao.findById(kaartNummer2));

        System.out.println("[Test] ProductDAO.findByOVChipkaart(kaart1):");
        for (Product product : pdao.findByOVChipkaart(kaart1))
        {
            System.out.println(product);
        }

        Product gewijzigdProduct = pdao.findByProductNummer(productNummer1);
        gewijzigdProduct.setNaam("Weekend Vrij Plus");
        gewijzigdProduct.setPrijs(new BigDecimal("39.95"));
        gewijzigdProduct.verwijderOVChipkaart(gewijzigdProduct.getOvChipkaarten().get(0));
        gewijzigdProduct.voegToeOVChipkaart(kaart2);
        System.out.println("[Test] ProductDAO.update(): " + pdao.update(gewijzigdProduct));
        System.out.println("Product na update: " + pdao.findByProductNummer(productNummer1));

        OVChipkaart gewijzigdeKaart = kdao.findById(kaartNummer2);
        Product productVanKaart = pdao.findByProductNummer(productNummer2);
        gewijzigdeKaart.verwijderProduct(productVanKaart);
        System.out.println("[Test] OVChipkaartDAO.update() wijzigt producten: " + kdao.update(gewijzigdeKaart));
        System.out.println("Kaart 2 na update: " + kdao.findById(kaartNummer2));

        System.out.println("[Test] ProductDAO.findAll():");
        for (Product product : pdao.findAll())
        {
            System.out.println(product);
        }

        System.out.println("[Test] ProductDAO.delete(product2): " + pdao.delete(product2));
        System.out.println("Product 2 na delete: " + pdao.findByProductNummer(productNummer2));
        System.out.println("Kaart 2 na product-delete: " + kdao.findById(kaartNummer2));

        System.out.println("[Test] ProductDAO.delete(product1): " + pdao.delete(gewijzigdProduct));
        System.out.println("Product 1 na delete: " + pdao.findByProductNummer(productNummer1));

        kdao.delete(kaart1);
        kdao.delete(kaart2);
        reiziger.verwijderOVChipkaart(kaart1);
        reiziger.verwijderOVChipkaart(kaart2);
        rdao.delete(reiziger);
    }
}
