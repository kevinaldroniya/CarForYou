package com.car.foryou.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "model")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CarModel extends BaseModel {

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "carModel")
    private List<Variant> variants;

    @Override
    public String toString() {
        return "CarModel{" +
                "name='" + name + '\'' +
                ", brand=" + brand +
                '}';
    }
}
