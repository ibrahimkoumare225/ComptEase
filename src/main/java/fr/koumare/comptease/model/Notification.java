package fr.koumare.comptease.model;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
@Getter
@Setter


@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private Instant date;
    private boolean isRead;

    public Notification() {
    }

    public Notification(Long id, String message, Instant date, boolean isRead) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
    }
}
