package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Información de un pedido
 */
@Entity
@Data
@Table(name="Pedido")
public class Pedido implements Transferable<Pedido.Transfer>{

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
    @Getter
    @AllArgsConstructor
    public static class Transfer{
        private long id;
        private String dir;
        private String stat;
        private double precio;
        private String fecha;
        private String cliente;
        //TODO: Añadir los productos
        public Transfer(Pedido p){
            this.id = p.getId();
            this.dir = p.getDirEntrega();
            this.stat = p.getEstado().toString();
            this.precio =  p.getPrecioEntrega() + p.getPrecioServicio();
            this.fecha = p.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            this.cliente = p.getCliente().getFirstName() + " " + p.getCliente().getLastName();
        }
    }

    @Override
    public Transfer toTransfer(){
        return new Transfer(getId(), getDirEntrega(), getEstado().toString(), getPrecioEntrega() + getPrecioServicio(), getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), getCliente().getFirstName() + " " + getCliente().getLastName());
    }

}
