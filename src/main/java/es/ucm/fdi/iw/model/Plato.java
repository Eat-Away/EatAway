package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import javax.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Descripcion de un plato de un restaurante
 */
@Entity
@Data
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String ingredientes;

    @ManyToOne
    private Restaurante restaurante;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Plato_id")
    private List<Extra> extras = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Plato_id")
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="Plato_id")
    private List<PlatoPedido> platoPedidos = new ArrayList<>();
}
