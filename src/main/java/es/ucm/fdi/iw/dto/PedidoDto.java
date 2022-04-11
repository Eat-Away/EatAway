package es.ucm.fdi.iw.dto;

import lombok.Data;


@Data
public class PedidoDto{

    public PedidoDto(double lat, double lng,long id,String dirEntrega,String firstName,String lastName,String nombre,String direccion){
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.dirEntrega = dirEntrega;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nombre = nombre;
        this.direccion = direccion;
    }
    //restaurante
    private String nombre;
    private String direccion;
    
    //pedido
    private long id;
    private String dirEntrega;
    private double lat;
    private double lng;
    
    //cliente
    private String firstName;
    private String lastName;

}