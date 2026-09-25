package domain;

import java.math.BigDecimal;
import java.sql.Date;

public class OVChipkaart {
    private int kaartNummer;
    private Date geldigTot;
    private int klasse;
    private BigDecimal saldo;
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
        String reizigerStr = reiziger == null
                ? "geen"
                : "#" + reiziger.getId() + " " + reiziger.getNaam();

        return "OVChipkaart {#" + kaartNummer + " klasse=" + klasse +
                " saldo=" + saldo + " geldigTot=" + geldigTot +
                " reiziger=" + reizigerStr + "}";
    }
}
