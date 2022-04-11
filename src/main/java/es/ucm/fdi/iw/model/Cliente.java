package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/** 
 * Un usuario registrado con el rol CLIENTE.
 * Por defecto, cuando un usuario se registre en la app se le asignará este rol.
 */
@Entity
@Data
@DiscriminatorValue("CLIENTE")
public class Cliente extends User{
    private String direccion;
    private String infoPago;
    @OneToMany
    private List<Pedido> pedidos = new ArrayList<>();
}
