package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.*;

import lombok.Data;

/**
 * Información de un pedido
 */
@Entity
@Data
@Table(name="Pedido")
public class Pedido {

    public enum Estado{
        NO_CONFIRMADO,  // -> 0
        PENDIENTE,      // -> 1
        PREPARANDO,     // -> 2
        LISTORECOGIDA,  // -> 3
        REPARTO,        // -> 4
        ENTREGADO,      // -> 5
        CANCELADO       // -> 6
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private String dirEntrega;
    private Estado estado;
    private double precioEntrega;
    private double precioServicio;
    private LocalDateTime fechaPedido;
    private Double valoracion; //Double para que pueda ser null
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="pedido_id")
    private List<PlatoPedido> contenidoPedido = new ArrayList<>();
    @ManyToOne
    private Cliente cliente;
    @ManyToOne
    private Restaurante restaurante;
    @ManyToOne
    private Repartidor repartidor;

    private double lat;
    private double lng;
}
