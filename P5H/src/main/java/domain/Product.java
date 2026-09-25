package domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_nummer")
    private int productNummer;

    @Column(nullable = false, length = 30)
    private String naam;

    @Column(length = 512)
    private String beschrijving;

    @Column(precision = 16, scale = 2, nullable = false)
    private BigDecimal prijs;

    @ManyToMany(mappedBy = "producten", fetch = FetchType.LAZY)
    private Set<OVChipkaart> ovChipkaarten = new HashSet<>();

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

    public Set<OVChipkaart> getOvChipkaarten() { return ovChipkaarten; }

    public void addOvChipkaart(OVChipkaart kaart) {
        if (kaart != null && ovChipkaarten.add(kaart)) {
            kaart.getProducten().add(this);
        }
    }

    public void removeOvChipkaart(OVChipkaart kaart) {
        if (kaart != null && ovChipkaarten.remove(kaart)) {
            kaart.getProducten().remove(this);
        }
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
        return "Product {#" + productNummer + " " + naam +
                " beschrijving=" + beschrijving + " prijs=" + prijs + "}";
    }
}
