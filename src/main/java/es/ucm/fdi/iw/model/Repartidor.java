package es.ucm.fdi.iw.model;

import javax.persistence.*;

import lombok.Data;

/**
 * Tipo de usuario con el rol REPARTIDOR.
 * Los usuario de tipo REPARTIDOR solo podrán adquirir este rol si está autorizado
 * por un usuario con el rol ADMIN
 */
@Entity
@Data
@DiscriminatorValue("REPARTIDOR")
public class Repartidor extends User{
    @OneToOne
    private Pedido pedido;
    //private double valoracion;
}
