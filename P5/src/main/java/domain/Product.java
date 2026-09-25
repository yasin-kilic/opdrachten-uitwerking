package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product {
    private int productNummer;
    private String naam;
    private String beschrijving;
    private BigDecimal prijs;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Product() {}

    public Product(int productNummer, String naam, String beschrijving, BigDecimal prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public int getProductNummer() { return productNummer; }
    public void setProductNummer(int productNummer) { this.productNummer = productNummer; }

    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }

    public String getBeschrijving() { return beschrijving; }
    public void setBeschrijving(String beschrijving) { this.beschrijving = beschrijving; }

    public BigDecimal getPrijs() { return prijs; }
    public void setPrijs(BigDecimal prijs) { this.prijs = prijs; }

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

        ovChipkaarten.add(kaart);
        if (!kaart.getProducten().contains(this)) {
            kaart.getProducten().add(this);
        }
        return true;
    }

    public boolean verwijderOVChipkaart(OVChipkaart kaart) {
        if (kaart == null || !ovChipkaarten.remove(kaart)) return false;

        kaart.getProducten().remove(this);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return productNummer == product.productNummer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productNummer);
    }

    @Override
    public String toString() {
        List<Integer> kaartNummers = new ArrayList<>();
        for (OVChipkaart kaart : ovChipkaarten) {
            kaartNummers.add(kaart.getKaartNummer());
        }

        return "Product {#" + productNummer + " " + naam +
                " beschrijving=" + beschrijving + " prijs=" + prijs +
                " OV-chipkaarten=" + kaartNummers + "}";
    }
}
