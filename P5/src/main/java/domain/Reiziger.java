package domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Reiziger {

    private int id;

    private String voorletters;

    private String tussenvoegsel;

    private String achternaam;

    private Date geboortedatum;

    private Adres adres;

    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Reiziger() {}

    public Reiziger(int id, String voorletters, String tussenvoegsel, String achternaam, Date geboortedatum) {
        this.id = id;
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVoorletters() { return voorletters; }
    public void setVoorletters(String voorletters) { this.voorletters = voorletters; }

    public String getTussenvoegsel() { return tussenvoegsel; }
    public void setTussenvoegsel(String tussenvoegsel) { this.tussenvoegsel = tussenvoegsel; }

    public String getAchternaam() { return achternaam; }
    public void setAchternaam(String achternaam) { this.achternaam = achternaam; }

    public Date getGeboortedatum() { return geboortedatum; }
    public void setGeboortedatum(Date geboortedatum) { this.geboortedatum = geboortedatum; }

    public String getNaam() {
        if (tussenvoegsel == null || tussenvoegsel.isBlank()) return voorletters + " " + achternaam;
        return voorletters + " " + tussenvoegsel + " " + achternaam;
    }

    public Adres getAdres() { return adres; }
    public void setAdres(Adres adres) { this.adres = adres; }

    public List<OVChipkaart> getOvChipkaarten() { return ovChipkaarten; }
    public void setOvChipkaarten(List<OVChipkaart> ovChipkaarten) {
        List<OVChipkaart> nieuweKaarten = ovChipkaarten == null
                ? new ArrayList<>()
                : new ArrayList<>(ovChipkaarten);

        for (OVChipkaart kaart : new ArrayList<>(this.ovChipkaarten)) {
            verwijderOVChipkaart(kaart);
        }
        for (OVChipkaart kaart : nieuweKaarten) {
            voegToeOVChipkaart(kaart);
        }
    }

    public boolean voegToeOVChipkaart(OVChipkaart kaart) {
        if (kaart == null || ovChipkaarten.contains(kaart)) return false;

        boolean toegevoegd = ovChipkaarten.add(kaart);
        if (toegevoegd) kaart.setReiziger(this);
        return toegevoegd;
    }

    public boolean verwijderOVChipkaart(OVChipkaart kaart) {
        if (kaart == null || !ovChipkaarten.remove(kaart)) return false;

        if (kaart.getReiziger() == this) kaart.setReiziger(null);
        return true;
    }

    @Override
    public String toString() {
        String tv = (tussenvoegsel != null && !tussenvoegsel.isBlank()) ? (" " + tussenvoegsel) : "";
        String init = (voorletters != null && !voorletters.endsWith(".")) ? (voorletters + ".") : voorletters;

        String adresStr = (adres != null) ? (", " + adres) : "";
        return String.format("Reiziger {#%d %s%s %s, geb. %s%s, OV-chipkaarten=%s}",
                id, init, tv, achternaam, geboortedatum, adresStr, ovChipkaarten);
    }
}
