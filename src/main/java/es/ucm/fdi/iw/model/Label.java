package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

/**
 * Etiqueta que describe el tipo de comida que ofrece un restaurante. 
 * Cada restaurante puede tener una o varias.
 */
@Entity
@Data
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private Long id;
    private String nombre;

    // @ManyToOne
    // private List<Restaurante> restaurantes = new ArrayList<>();
}
