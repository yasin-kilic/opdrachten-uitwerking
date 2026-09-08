package domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "adres")
public class Adres {

    @Id
    @Column(name = "adres_id")
    private int id;

    @Column(nullable = false)
    private String postcode;

    @Column(nullable = false)
    private String huisnummer;

    @Column(nullable = false)
    private String straat;

    @Column(nullable = false)
    private String woonplaats;

    @OneToOne(optional = false)
    @JoinColumn(name = "reiziger_id", unique = true, nullable = false)
    private Reiziger reiziger;

    public Adres() {}

    public Adres(int id, String postcode, String huisnummer, String straat, String woonplaats, Reiziger reiziger) {
        this.id = id;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.reiziger = reiziger;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getHuisnummer() { return huisnummer; }
    public void setHuisnummer(String huisnummer) { this.huisnummer = huisnummer; }

    public String getStraat() { return straat; }
    public void setStraat(String straat) { this.straat = straat; }

    public String getWoonplaats() { return woonplaats; }
    public void setWoonplaats(String woonplaats) { this.woonplaats = woonplaats; }

    public Reiziger getReiziger() { return reiziger; }
    public void setReiziger(Reiziger reiziger) { this.reiziger = reiziger; }

    @Override
    public String toString() {
        return String.format("Adres {#%d %s %s-%s, reiziger #%d}",
                id, postcode.substring(0, 4), postcode.substring(4),
                huisnummer, reiziger.getId());
    }
}
