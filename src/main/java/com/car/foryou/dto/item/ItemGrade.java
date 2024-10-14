package com.car.foryou.dto.item;

public enum ItemGrade {
    A("a"),
    B("b"),
    C("c"),
    D("d");

    private final String value;

    ItemGrade(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static ItemGrade grade(String text){
        for (ItemGrade itemGrade : com.car.foryou.dto.item.ItemGrade.values()){
            if (itemGrade.value.equalsIgnoreCase(text)){
                return itemGrade;
            }
        }
        throw new IllegalArgumentException("No constant found with value : " + text);
    }


    @Override
    public String toString() {
        return value;
    }
}
