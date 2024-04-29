package com.example.shopshoesspring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "_mr")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Mr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mr_id", nullable = false)
    private Long id;

    @Column(name = "mr_name", nullable = false, length = 700)
    private String mrName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mr_type_id", nullable = false)
    private MrType mrType;

    @Column(name = "mr_description", nullable = false, length = 800)
    private String mrDescription;

    @Column(name = "mr_date_start", nullable = false)
    private Instant mrDateStart;

    @Column(name = "mr_date_end", nullable = false)
    private Instant mrDateEnd;

    @OneToMany(mappedBy = "mr")
    private Set<MrInfo> mrInfos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "mr")
    private Set<MrUser> mrUsers = new LinkedHashSet<>();

}