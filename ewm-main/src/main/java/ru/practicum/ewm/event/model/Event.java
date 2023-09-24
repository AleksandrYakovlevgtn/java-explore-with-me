package ru.practicum.ewm.event.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Entity
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    Long id;
    @Column(name = "annotation", length = 2000, nullable = false)
    String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    Category category;
    @Column(name = "confirmed_requests", columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    Long confirmedRequests;
    @Column(name = "created_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    LocalDateTime createdOn;
    @Column(name = "description", length = 7000, nullable = false)
    String description;
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @ToString.Exclude
    User initiator;
    @Column(name = "latitude", nullable = false)
    Double latitude;
    @Column(name = "longitude", nullable = false)
    Double longitude;
    @Column(name = "paid", nullable = false)
    Boolean paid;
    @Column(name = "participantLimit", columnDefinition = "INTEGER DEFAULT 0", nullable = false)
    Integer participantLimit;
    @Column(name = "publishedOn")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    EventState state;
    @Column(name = "title", length = 120, nullable = false)
    String title;
    @Transient
    Long views;
}