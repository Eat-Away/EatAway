package es.ucm.fdi.iw.model;

import java.util.List;

import javax.persistence.*;
import lombok.Data;

/**
 * Clase que sirve para relacionar un plato con un pedido
 */
@Entity
@Data
public class PlatoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private int cantidad;
    @ManyToOne
    private Plato plato;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="plato_pedido_id")
    private List<Extra> extras;
    @ManyToOne
    private Pedido pedido;
}

