package com.car.foryou.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "variant")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Variant extends ModelTemplate{

    @Column(name = "name")
    private String name;

    @Column(name = "year")
    private Integer year;

    @Column(name = "engine")
    private String engine;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "fuel")
    private String fuel;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private CarModel carModel;

    @OneToMany(mappedBy = "variant")
    private List<Item> items;
}
