import DAO.*;
import domain.Adres;
import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main
{
    public static void main(String[] args)
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ovchip");

        AdresDAO adresDAO = new AdresDAOHibernate(emf);
        ReizigerDAO reizigerDAO = new ReizigerDAOHibernate(emf);
        OVChipkaartDAO ovChipkaartDAO = new OVChipkaartDAOHibernate(emf);
        ProductDAO productDAO = new ProductDAOHibernate(emf);

        testReizigerDAO(reizigerDAO, adresDAO);
        testAdresDAO(adresDAO, reizigerDAO);
        testOVChipkaartDAO(ovChipkaartDAO, reizigerDAO);
        testProductDAO(productDAO, ovChipkaartDAO, reizigerDAO);
        testProductViaReizigerDAO(productDAO, reizigerDAO);

        emf.close();
    }

    /**
     * P4H. Reiziger DAO: persistentie van klassen met een een-op-een- en een-op-veel-relatie
     *
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     */
    private static void testReizigerDAO(ReizigerDAO rdao, AdresDAO adao)
    {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers)
        {
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
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", Date.valueOf(gbdatum));
        Adres sietskeAdres = new Adres(nieuwAdresId, "3511LX", "37", "Nieuwegracht", "Utrecht", sietske);
        sietske.setAdres(sietskeAdres);

        OVChipkaart kaart1 = new OVChipkaart(11101, Date.valueOf("2028-12-31"), 2,
                new BigDecimal("25.00"), sietske);
        OVChipkaart kaart2 = new OVChipkaart(11102, Date.valueOf("2027-12-31"), 1,
                new BigDecimal("10.00"), sietske);
        sietske.addOvChipkaart(kaart1);
        sietske.addOvChipkaart(kaart2);

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
        Reiziger bijgewerkt = rdao.findById(77);
        System.out.println("Na update: " + bijgewerkt);
        for (OVChipkaart kaart : bijgewerkt.getOvChipkaarten())
        {
            System.out.println("Na update OV-chipkaart: " + kaart);
        }
        System.out.println();

        System.out.print("[Test] Aantal reizigers voor delete: " + rdao.findAll().size() + ", na ReizigerDAO.delete(): ");
        rdao.delete(sietske);
        System.out.println(rdao.findAll().size());
    }

    /**
     * P3H. Adres DAO: persistentie van een klasse + 1-op-1 relatie
     *
     * Deze methode test de CRUD-functionaliteit van de Adres DAO
     */
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao)
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

        reiziger.setAdres(null);
        rdao.delete(reiziger);
    }

    /**
     * P4H. OV-chipkaart DAO: persistentie van een klasse + veel-op-een-relatie
     *
     * Deze methode test de CRUD-functionaliteit van de OV-chipkaart DAO
     */
    private static void testOVChipkaartDAO(OVChipkaartDAO kdao, ReizigerDAO rdao)
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
        reiziger.addOvChipkaart(kaart);

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
        System.out.println("Na delete: " + kdao.findById(kaartNummer));

        reiziger.removeOvChipkaart(kaart);
        rdao.delete(reiziger);
    }

    /**
     * P5H. Product DAO: persistentie van twee klassen met een veel-op-veel-relatie
     *
     * Deze methode test de CRUD-functionaliteit van de Product DAO
     */
    private static void testProductDAO(ProductDAO pdao, OVChipkaartDAO kdao, ReizigerDAO rdao)
    {
        System.out.println("\n---------- Test ProductDAO -------------");

        int reizigerId = 80;
        int kaartNummer1 = 33303;
        int kaartNummer2 = 33304;
        int productNummer1 = 9998;
        int productNummer2 = 9999;

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

        OVChipkaart bestaandeKaart1 = kdao.findById(kaartNummer1);
        if (bestaandeKaart1 != null)
        {
            kdao.delete(bestaandeKaart1);
        }
        OVChipkaart bestaandeKaart2 = kdao.findById(kaartNummer2);
        if (bestaandeKaart2 != null)
        {
            kdao.delete(bestaandeKaart2);
        }

        Reiziger bestaandeReiziger = rdao.findById(reizigerId);
        if (bestaandeReiziger != null)
        {
            rdao.delete(bestaandeReiziger);
        }

        Reiziger reiziger = new Reiziger(reizigerId, "P", "de", "ProductTester", Date.valueOf("1992-01-01"));
        rdao.save(reiziger);

        OVChipkaart kaart1 = new OVChipkaart(kaartNummer1, Date.valueOf("2030-12-31"), 2,
                new BigDecimal("15.00"), reiziger);
        reiziger.addOvChipkaart(kaart1);
        System.out.println("[Test] OVChipkaartDAO.save(kaart1): " + kdao.save(kaart1));

        Product product1 = new Product(productNummer1, "Weekend Vrij", "Onbeperkt reizen in het weekend",
                new BigDecimal("34.95"));
        Product product2 = new Product(productNummer2, "Dal Voordeel", "Korting tijdens daluren",
                new BigDecimal("5.60"));
        product1.addOvChipkaart(kaart1);

        System.out.println("[Test] ProductDAO.save(product1 met kaart1): " + pdao.save(product1));
        System.out.println("[Test] ProductDAO.save(product2): " + pdao.save(product2));

        OVChipkaart kaart2 = new OVChipkaart(kaartNummer2, Date.valueOf("2031-12-31"), 1,
                new BigDecimal("30.00"), reiziger);
        reiziger.addOvChipkaart(kaart2);
        kaart2.addProduct(product2);
        System.out.println("[Test] OVChipkaartDAO.save(kaart2 met product2): " + kdao.save(kaart2));

        System.out.println("Kaart 1 na ProductDAO.save(): " + kdao.findById(kaartNummer1).getProducten());
        System.out.println("Kaart 2 na OVChipkaartDAO.save(): " + kdao.findById(kaartNummer2).getProducten());
        System.out.println("[Test] ProductDAO.findByProductNummer(): " +
                pdao.findByProductNummer(productNummer1));

        System.out.println("[Test] ProductDAO.findByOVChipkaart(kaart1):");
        for (Product product : pdao.findByOVChipkaart(kaart1))
        {
            System.out.println(product);
        }

        Product gewijzigdProduct = pdao.findByProductNummer(productNummer1);
        OVChipkaart oudeKaart = gewijzigdProduct.getOvChipkaarten().iterator().next();
        gewijzigdProduct.removeOvChipkaart(oudeKaart);
        gewijzigdProduct.addOvChipkaart(kdao.findById(kaartNummer2));
        gewijzigdProduct.setNaam("Weekend Vrij Plus");
        gewijzigdProduct.setPrijs(new BigDecimal("39.95"));
        System.out.println("[Test] ProductDAO.update(): " + pdao.update(gewijzigdProduct));
        System.out.println("Product na update: " + pdao.findByProductNummer(productNummer1));

        OVChipkaart gewijzigdeKaart = kdao.findById(kaartNummer2);
        Product productVanKaart = pdao.findByProductNummer(productNummer2);
        gewijzigdeKaart.removeProduct(productVanKaart);
        System.out.println("[Test] OVChipkaartDAO.update() wijzigt producten: " + kdao.update(gewijzigdeKaart));
        System.out.println("Kaart 2 na update: " + kdao.findById(kaartNummer2).getProducten());

        System.out.println("[Test] ProductDAO.findAll():");
        for (Product product : pdao.findAll())
        {
            System.out.println(product + " OV-chipkaarten=" + product.getOvChipkaarten());
        }

        System.out.println("[Test] ProductDAO.delete(product2): " + pdao.delete(product2));
        System.out.println("Product 2 na delete: " + pdao.findByProductNummer(productNummer2));

        System.out.println("[Test] ProductDAO.delete(product1): " + pdao.delete(gewijzigdProduct));
        System.out.println("Product 1 na delete: " + pdao.findByProductNummer(productNummer1));
        System.out.println("Kaart 2 na product-delete: " + kdao.findById(kaartNummer2));

        kdao.delete(kaart1);
        kdao.delete(kaart2);
        reiziger.removeOvChipkaart(kaart1);
        reiziger.removeOvChipkaart(kaart2);
        rdao.delete(reiziger);
    }

    /**
     * P5H. Product opslaan en wijzigen via de relaties van Reiziger
     */
    private static void testProductViaReizigerDAO(ProductDAO pdao, ReizigerDAO rdao)
    {
        System.out.println("\n---------- Test Product via ReizigerDAO -------------");

        int reizigerId = 81;
        int kaartNummer = 33305;
        int productNummer = 9997;

        Reiziger bestaandeReiziger = rdao.findById(reizigerId);
        if (bestaandeReiziger != null)
        {
            rdao.delete(bestaandeReiziger);
        }

        Product bestaandProduct = pdao.findByProductNummer(productNummer);
        if (bestaandProduct != null)
        {
            pdao.delete(bestaandProduct);
        }

        Reiziger reiziger = new Reiziger(reizigerId, "R", "de", "RelatieTester", Date.valueOf("1993-01-01"));
        OVChipkaart kaart = new OVChipkaart(kaartNummer, Date.valueOf("2032-12-31"), 2,
                new BigDecimal("20.00"), reiziger);
        Product product = new Product(productNummer, "Altijd Voordeel", "Korting op iedere reis",
                new BigDecimal("25.00"));

        reiziger.addOvChipkaart(kaart);
        kaart.addProduct(product);

        System.out.println("[Test] ReizigerDAO.save() slaat kaart en product op: " + rdao.save(reiziger));
        Reiziger gevondenReiziger = rdao.findById(reizigerId);
        System.out.println("Product via gevonden reiziger: " +
                gevondenReiziger.getOvChipkaarten().get(0).getProducten());

        Product gevondenProduct = gevondenReiziger.getOvChipkaarten().get(0).getProducten().iterator().next();
        gevondenProduct.setNaam("Altijd Voordeel Plus");
        gevondenProduct.setPrijs(new BigDecimal("29.95"));
        System.out.println("[Test] ReizigerDAO.update() wijzigt product: " + rdao.update(gevondenReiziger));
        System.out.println("Product na update via reiziger: " + pdao.findByProductNummer(productNummer));

        System.out.println("[Test] ReizigerDAO.delete() verwijdert reiziger maar niet product: " +
                rdao.delete(gevondenReiziger));
        System.out.println("Product na reiziger-delete: " + pdao.findByProductNummer(productNummer));

        pdao.delete(product);
    }
}
