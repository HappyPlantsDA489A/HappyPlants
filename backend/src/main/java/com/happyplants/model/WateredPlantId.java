package com.happyplants.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

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

    public UUID getUsersPlantsId() {
        return usersPlantsId;
    }

    public void setUsersPlantsId(UUID usersPlantsId) {
        this.usersPlantsId = usersPlantsId;
    }

    public OffsetDateTime getOccuredAt() {
        return occuredAt;
    }

    public void setOccuredAt(OffsetDateTime occuredAt) {
        this.occuredAt = occuredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WateredPlantId entity = (WateredPlantId) o;
        return Objects.equals(this.usersPlantsId, entity.usersPlantsId) &&
                Objects.equals(this.occuredAt, entity.occuredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usersPlantsId, occuredAt);
    }
}