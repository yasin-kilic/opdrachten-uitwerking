package DAO;

import domain.OVChipkaart;
import domain.Reiziger;

import java.util.List;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart kaart);
    boolean update(OVChipkaart kaart);
    boolean delete(OVChipkaart kaart);

    OVChipkaart findById(int kaartNummer);
    List<OVChipkaart> findByReiziger(Reiziger reiziger);
    List<OVChipkaart> findAll();
}
