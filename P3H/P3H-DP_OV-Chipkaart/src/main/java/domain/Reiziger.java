package domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import java.sql.Date;

@Entity
@Table(name = "reiziger")
public class Reiziger {

    @Id
    @Column(name = "reiziger_id")
    private int id;

    @Column(nullable = false)
    private String voorletters;

    private String tussenvoegsel;

    @Column(nullable = false)
    private String achternaam;

    private Date geboortedatum;

    @OneToOne(mappedBy = "reiziger",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    private Adres adres;

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

    public Adres getAdres() { return adres; }

    public void setAdres(Adres adres) {
        this.adres = adres;
        if (adres != null && adres.getReiziger() != this) {
            adres.setReiziger(this);
        }
    }

    @Override
    public String toString() {
        String tv = (tussenvoegsel == null || tussenvoegsel.isBlank()) ? "" : (" " + tussenvoegsel);
        return "Reiziger {#" + id + " " + voorletters + "." + tv + " " + achternaam +
                ", geb. " + geboortedatum +
                (adres == null ? "" : (", " + adres)) + "}";
    }

}
