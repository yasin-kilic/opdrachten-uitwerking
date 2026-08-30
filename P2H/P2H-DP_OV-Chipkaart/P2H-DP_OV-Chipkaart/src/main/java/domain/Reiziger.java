package domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "reiziger")
public class Reiziger {

    @Id
    @Column(name="reiziger_id", columnDefinition="numeric(10,0)")
    private int id;

    @Column(name = "voorletters", length = 10, nullable = false)
    private String voorletters;

    @Column(name = "tussenvoegsel", length = 10, nullable = true)
    private String tussenvoegsel;

    @Column(name = "achternaam", length = 255, nullable = false)
    private String achternaam;

    @Column(name = "geboortedatum", nullable = true)
    private Date geboortedatum;

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

    @Override
    public String toString() {
        String tv = (tussenvoegsel != null && !tussenvoegsel.isBlank()) ? (" " + tussenvoegsel) : "";
        return String.format("Reiziger #%d %s%s %s (%s)", id, voorletters, tv, achternaam, geboortedatum);
    }
}
