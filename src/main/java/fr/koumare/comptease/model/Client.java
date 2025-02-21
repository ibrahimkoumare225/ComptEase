package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(unique = true, length = 20)
    private String contact;

    public Client() {
    }

    public Client(String lastName, String firstName, String contact) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.contact = contact;
    }
}
