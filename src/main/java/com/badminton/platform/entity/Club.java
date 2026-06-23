package com.badminton.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import java.util.Map;

@Entity
@Table(name = "clubs")
@Getter @Setter
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
        // FK dạng cột
    @Column(name = "sport_id")
    private Long sportId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Type(JsonType.class)
    @Column(name = "name_translations", columnDefinition = "jsonb")
    private Map<String, String> nameTranslations;

    @Type(JsonType.class)
    @Column(name = "description_translations", columnDefinition = "jsonb")
    private Map<String, String> descriptionTranslations;

}

