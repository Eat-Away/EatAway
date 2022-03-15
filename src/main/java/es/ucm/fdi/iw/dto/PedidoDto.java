package es.ucm.fdi.iw.dto;

import lombok.Data;


@Data
public class PedidoDto{

    public PedidoDto(long id,String firstName,String lastName,String nombre){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nombre = nombre;
    }
    //restaurante
    private String nombre;
    //pedido
    
    private long id;
    //cliente

    private String firstName;
    private String lastName;

}