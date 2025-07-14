package io.github.alansilva.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.naming.Name;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text")
    private String text;

    @Column(name = "dateTime")
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private  User user;

    @PrePersist
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }
}
