package domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "ov_chipkaart_product",
            joinColumns = @JoinColumn(name = "kaart_nummer"),
            inverseJoinColumns = @JoinColumn(name = "product_nummer"))
    private Set<Product> producten = new HashSet<>();

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

    public Set<Product> getProducten() { return producten; }

    public void addProduct(Product product) {
        if (product != null && producten.add(product)) {
            product.getOvChipkaarten().add(this);
        }
    }

    public void removeProduct(Product product) {
        if (product != null && producten.remove(product)) {
            product.getOvChipkaarten().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OVChipkaart)) return false;
        OVChipkaart kaart = (OVChipkaart) o;
        return kaartNummer == kaart.kaartNummer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kaartNummer);
    }

    @Override
    public String toString() {
        return "OVChipkaart {#" + kaartNummer + " klasse=" + klasse +
                " saldo=" + saldo + " geldigTot=" + geldigTot + "}";
    }
}
