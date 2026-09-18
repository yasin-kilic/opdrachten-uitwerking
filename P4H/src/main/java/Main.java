import DAO.*;
import domain.Adres;
import domain.OVChipkaart;
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

        testReizigerDAO(reizigerDAO, adresDAO);
        testAdresDAO(adresDAO, reizigerDAO);
        testOVChipkaartDAO(ovChipkaartDAO, reizigerDAO);

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
}
