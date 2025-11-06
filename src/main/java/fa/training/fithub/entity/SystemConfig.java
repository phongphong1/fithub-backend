package fa.training.fithub.entity;

import fa.training.fithub.enums.DataType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "system_config", schema = "fithub")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @NotNull
    @Lob
    @Column(name = "config_value", nullable = false, columnDefinition = "text")
    private String configValue;

    @Enumerated(EnumType.STRING)  // đảm bảo Hibernate dùng VARCHAR
    @Column(name = "data_type", length = 255)
    private DataType dataType;

    @Size(max = 50)
    @NotNull
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable = true;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}