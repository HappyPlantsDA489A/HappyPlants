package com.happyplants.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class WateredPlantId implements Serializable {
    private static final long serialVersionUID = -3339368335397519544L;

    @NotNull
    @Column(name = "users_plants_id", nullable = false)
    private UUID usersPlantsId;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "occured_at", nullable = false)
    private OffsetDateTime occuredAt;
}