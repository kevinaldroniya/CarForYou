package com.car.foryou.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ModelTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "created_at")
    private Integer createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private Integer updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private Integer deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;
}
