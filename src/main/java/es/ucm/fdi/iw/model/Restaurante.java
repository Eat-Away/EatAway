package es.ucm.fdi.iw.model;

import lombok.Data;
import javax.persistence.*;

import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Descripción de un restaurante
 */
@Entity
@Data
public class Restaurante{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    private User propietario;

    private String nombre;
    private String descripcion;
    private String horario;
    private String direccion;
    private double valoracion;
    
    @Lob
    @Nullable
    private byte[] foto;

    @OneToMany
    @JoinColumn(name="Restaurante_id")
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany
    @JoinColumn(name="Restaurante_id")
    private List<Label> labels = new ArrayList<>();

    @OneToMany
    @JoinColumn(name="Restaurante_id")
    private List<Plato> platos = new ArrayList<>();
}