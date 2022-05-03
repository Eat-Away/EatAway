package es.ucm.fdi.iw.model;

import javax.persistence.*;
import lombok.Data;

/**
 * Ingredientes extra que un cliente puede añadir a un plato concreto
 */
@Entity
@Data
public class Extra {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private String nombre;
    private double precio;
    @ManyToOne
    private Plato plato;
    @ManyToOne
    private PlatoPedido platoPedido;
}
