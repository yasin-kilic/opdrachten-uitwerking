package domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "ov_chipkaart")
public class OVChipkaart {

    @Id
    @Column(name = "kaart_nummer")
    private int kaartNummer;

    @Column(name = "geldig_tot", nullable = false)
    private Date geldigTot;

    @Column(nullable = false)
    private int klasse;

    @Column(nullable = false)
    private BigDecimal saldo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reiziger_id", nullable = false)
    private Reiziger reiziger;

    public OVChipkaart() {}

    public OVChipkaart(int kaartNummer, Date geldigTot, int klasse, BigDecimal saldo, Reiziger reiziger) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }

    public int getKaartNummer() { return kaartNummer; }
    public void setKaartNummer(int kaartNummer) { this.kaartNummer = kaartNummer; }

    public Date getGeldigTot() { return geldigTot; }
    public void setGeldigTot(Date geldigTot) { this.geldigTot = geldigTot; }

    public int getKlasse() { return klasse; }
    public void setKlasse(int klasse) { this.klasse = klasse; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public Reiziger getReiziger() { return reiziger; }
    public void setReiziger(Reiziger reiziger) { this.reiziger = reiziger; }

    @Override
    public String toString() {
        return "OVChipkaart {#" + kaartNummer + " klasse=" + klasse +
                " saldo=" + saldo + " geldigTot=" + geldigTot + "}";
    }
}
