package com.hero.witchery_rewitched.util.capabilities.witchery_data;

public interface IWitcheryData {
    int getCovenSize();
    void setCovenSize(int size);

    int getEntChance();
    void setEntChance(int chance);
    void copyData(IWitcheryData data);
//    Implement curses like rituals
//    List<AbstractCurse> getCurses();
//    boolean addCurse(String curse, int intensity);
//    boolean removeCurse();
//    List<UUID> getCatFamiliars();
//    List<UUID> getOwlFamiliars();
//    List<UUID> getToadFamiliars();

}
