package com.car.foryou.model;

import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "brand")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Brand extends BaseModel {
    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

//    @OneToMany(mappedBy = "brand")
//    private List<CarModel> carModels;

    @Override
    public String toString() {
        return "Brand{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
