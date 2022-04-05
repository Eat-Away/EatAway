package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Entity
@Data
@DiscriminatorValue("REPARTIDOR")
public class Repartidor extends User{
    @OneToOne
    private Pedido pedido;
    
    private double valoracion;
}
