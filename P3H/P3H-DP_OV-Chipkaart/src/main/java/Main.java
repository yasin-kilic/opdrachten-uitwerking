import DAO.*;
import domain.Adres;
import domain.Reiziger;

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

        testReizigerDAO(reizigerDAO, adresDAO);
        testAdresDAO(adresDAO, reizigerDAO);

        emf.close();
    }

    /**
     * P3H. Reiziger DAO: persistentie van twee klassen met een een-op-een-relatie
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
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        Adres sietskeAdres = new Adres(nieuwAdresId, "3511LX", "37", "Nieuwegracht", "Utrecht", sietske);
        sietske.setAdres(sietskeAdres);

        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        System.out.println("[Test] ReizigerDAO.findById(77) geeft: ");
        Reiziger gevonden = rdao.findById(77);
        System.out.println(gevonden);
        System.out.println();

        System.out.println("[Test] ReizigerDAO.findByGbdatum(\"" + gbdatum + "\") geeft de volgende reizigers: ");
        List<Reiziger> reizigersOpGbdatum = rdao.findByGbdatum(Date.valueOf(gbdatum));
        for (Reiziger r : reizigersOpGbdatum)
        {
            System.out.println(r);
        }
        System.out.println();

        System.out.println("[Test] ReizigerDAO.update() wijzigt achternaam van Sietske naar 'Boersma'");
        sietske.setAchternaam("Boersma");
        rdao.update(sietske);
        System.out.println("Na update: " + rdao.findById(77));
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
}
