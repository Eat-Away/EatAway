package es.ucm.fdi.iw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.ucm.fdi.iw.model.Label;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.Restaurante;


/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    @Autowired
	private EntityManager entityManager;

	//private static final Logger log = LogManager.getLogger(RootController.class);

    @ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="Alto ahí, no tienes permiso para hacer esto")  // 403
	public static class PermisoDenegadoException extends RuntimeException {}

    @GetMapping("/restaurante")
    public String restaurante(Model model, @RequestParam long id) {
        Restaurante restaur = entityManager.find(Restaurante.class, id);
        List<Plato> platos = restaur.getPlatos();

        //String query = "Select x From Restaurante x Where id = " + id;
        //Restaurante restaur = (Restaurante) entityManager.createQuery(query).getSingleResult();
        //query = "SELECT x FROM Plato x WHERE RESTAURANTE_ID = " + id;
        //List<Plato> platos = (List<Plato>)entityManager.createQuery(query).getResultList();
        model.addAttribute("restaurante", restaur);
        model.addAttribute("platos", platos);
        return "restaurante";
    }

    @GetMapping("/platos")
    public String platos(Model model, @RequestParam long id) {
        String query = "Select x From Plato x Where id = " + id;
        Plato plato = (Plato) entityManager.createQuery(query).getSingleResult();
        //Restaurante restaurante = entityManager.find(Restaurante.class, id);
        model.addAttribute("plato", plato);
        //model.addAttribute("restaurante", restaurante);
        return "platos";
    }
    

    /* @GetMapping("/perfilRestaurante")
    public String perfilRestaurante(Model model, HttpSession session, @RequestParam long id){
        User u =(User)session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, id);

        if (u.getId() != r.getPropietario().getId()) {
            return "index";
        }

        model.addAttribute("restaurante", r);
        model.addAttribute("availableRestaurants", r.getPropietario().getRestaurantes());
        model.addAttribute("propietario", r.getPropietario());
        return "perfilRestaurante";
    } */


	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/carrito")
    public String carrito(Model model) {
        return "carrito";
    }

	@GetMapping("/")
    public String index(Model model) {
        
        List<Restaurante> availableRestaurants = entityManager.createQuery("SELECT x FROM Restaurante x").getResultList();
        model.addAttribute("availableRestaurants", availableRestaurants);

        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("Sin Filtro");
        filterOptions.add("Favoritos");
        filterOptions.add("Precio Ascendente");
        filterOptions.add("Precio Descendente");
        filterOptions.add("Populares");
        
        List<Label> labelOptions = entityManager.createQuery("Select x from Label x").getResultList();

        model.addAttribute("filterOptions",filterOptions);
        model.addAttribute("labelOptions",labelOptions);
        return "index";
    }
}
