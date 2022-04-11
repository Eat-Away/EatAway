package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Tipo de usuario con el rol RESTAURANTE
 * Los usuario de tipo RESTAURANTE solo podrán adquirir este rol si está autorizado
 * por un usuario con el rol ADMIN
 */
@Entity
@Data
@DiscriminatorValue("RESTAURANTE")
public class Restaurador extends User{
    @OneToMany
    private List<Restaurante> restaurantes = new ArrayList<>();
    @OneToMany
    //@JoinColumn(name="Pedidos_ids")
    @Column(name = "pedidos_restaurante")
    private List<Pedido> pedidos = new ArrayList<>();
}
