package com.example.shopshoesspring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "_mr_type")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class MrType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mr_type_id", nullable = false)
    private Long id;

    @Column(name = "mr_type_name", nullable = false, length = 500)
    private String mrTypeName;

    @OneToMany(mappedBy = "mrType")
    private Set<Mr> mrs = new LinkedHashSet<>();

}