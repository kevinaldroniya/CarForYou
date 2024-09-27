package com.car.foryou.dto.item;

public enum Grade {
    A("a"),
    B("b"),
    C("c"),
    D("d");

    private final String value;

    Grade(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static Grade grade(String text){
        for (Grade grade : Grade.values()){
            if (grade.value.equalsIgnoreCase(text)){
                return grade;
            }
        }
        throw new IllegalArgumentException("No constant found with value : " + text);
    }


    @Override
    public String toString() {
        return value;
    }
}
