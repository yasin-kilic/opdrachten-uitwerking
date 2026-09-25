package domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaart {
    private int kaartNummer;
    private Date geldigTot;
    private int klasse;
    private BigDecimal saldo;
    private Reiziger reiziger;
    private List<Product> producten = new ArrayList<>();

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

    public List<Product> getProducten() { return producten; }

    public boolean voegToeProduct(Product product) {
        if (product == null || producten.contains(product)) return false;

        producten.add(product);
        if (!product.getOvChipkaarten().contains(this)) {
            product.getOvChipkaarten().add(this);
        }
        return true;
    }

    public boolean verwijderProduct(Product product) {
        if (product == null) return false;

        int index = producten.indexOf(product);
        if (index == -1) return false;

        Product verwijderdProduct = producten.remove(index);
        verwijderdProduct.getOvChipkaarten().remove(this);
        return true;
    }

    @Override
    public String toString() {
        String reizigerStr = reiziger == null
                ? "geen"
                : "#" + reiziger.getId() + " " + reiziger.getNaam();
        List<Integer> productNummers = new ArrayList<>();
        for (Product product : producten) {
            productNummers.add(product.getProductNummer());
        }

        return "OVChipkaart {#" + kaartNummer + " klasse=" + klasse +
                " saldo=" + saldo + " geldigTot=" + geldigTot +
                " reiziger=" + reizigerStr + " producten=" + productNummers + "}";
    }
}
