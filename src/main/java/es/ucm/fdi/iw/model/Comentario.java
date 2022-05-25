package es.ucm.fdi.iw.model;

import javax.persistence.*;
import lombok.Data;

/**
 * Comentario que un usuario puede dejar a un plato concreto.
 */
@Entity
@Data
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private String texto;
    private double valoracion;
    @ManyToOne
    @JoinColumn(name="User_id")
    private User autor;
    @ManyToOne
    @JoinColumn(name="Plato_id")
    private Plato plato;
}
