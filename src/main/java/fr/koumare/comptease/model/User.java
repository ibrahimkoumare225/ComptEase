package fr.koumare.comptease.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pseudo;
    private String email;
    private String password;

    public User() {
    }

    public User(Long id, String pseudo, String email, String password) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
    }
}
