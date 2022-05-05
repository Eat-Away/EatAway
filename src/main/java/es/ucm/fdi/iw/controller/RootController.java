package es.ucm.fdi.iw.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Label;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.Restaurante;


/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {
	
    @Autowired
    private LocalData localData;

    @Autowired
	private EntityManager entityManager;

	//private static final Logger log = LogManager.getLogger(RootController.class);

    /**
     * It gets the logo image of a restaurant
     * 
     * @param id the id of the restaurant
     * @return The logo of the restaurant
     */
    @GetMapping("/rimg/{id}")
    public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
        File f = localData.getFile("restaurante/"+id, ""+id+"Logo");
        return os -> FileCopyUtils.copy(new FileInputStream(f), os);
    }

    /**
     * It returns a photo of a dish
     * 
     * @param idR restaurant id
     * @param id The id of the restaurant
     * @return The image of the dish
     */
    @GetMapping("/rimg/{idR}/plato/{id}")
    public StreamingResponseBody getFotoPlato(@PathVariable long idR, @PathVariable long id) throws IOException {
        File f = localData.getFile("restaurante/"+idR+"/plato", ""+id);
        return os -> FileCopyUtils.copy(new FileInputStream(f), os);
    }

    /**
     * It takes a restaurant id and a number, and returns a carousel photo of the restaurant
     * 
     * @param id the id of the restaurant
     * @param n the number of the carousel image
     * @return A StreamingResponseBody object.
     */
    @GetMapping("/rimg/{id}/carousel{n}")
    public StreamingResponseBody getFotoCarousel(@PathVariable long id, @PathVariable long n) throws IOException {
        File f = localData.getFile("restaurante/"+id, ""+id+"Carousel"+n);
        return os -> FileCopyUtils.copy(new FileInputStream(f), os);
    }

	/**
     * This class is a RuntimeException that is thrown when a user tries to access a resource that they
     * don't have permission to access.
     */
    @ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="Alto ahí, no tienes permiso para hacer esto")  // 403
    public static class PermisoDenegadoException extends RuntimeException {}

    /**
     * It takes a restaurant id, finds the restaurant in the database, and then returns a page with the
     * restaurant's name and a list of its dishes
     * 
     * @param model This is the model that will be passed to the view.
     * @param id The id of the restaurant we want to display.
     * @return Redirects to a page with the info of the restaurant and its dishes
     */
    @GetMapping("/restaurante")
    public String restaurante(Model model, @RequestParam long id) {
        Restaurante restaur = entityManager.find(Restaurante.class, id);
        List<Plato> platos = restaur.getPlatos();

        model.addAttribute("restaurante", restaur);
        model.addAttribute("platos", platos);
        return "restaurante";
    }

    /**
     * It takes a long id as a parameter, finds the corresponding Plato object in the database, and adds it
     * to the model
     * 
     * @param model This is the model that will be passed to the view.
     * @param id The id of the plato to be displayed.
     * @return Redirects to a page with the dish info and a button to add it to the cart.
     */
    @GetMapping("/platos")
    public String platos(Model model, @RequestParam long id) {
        Plato plato = entityManager.find(Plato.class, id);
        model.addAttribute("plato", plato);
        return "platos";
    }


    /**
     * It returns the login.html page
     * 
     * @param model The model is a Map that is used to store the data that will be displayed on the view
     * page.
     * @return A string that is the name of the view.
     */
	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

	/**
     * It gets all the restaurants from the database and puts them in a list, then it puts that list in
     * the model
     * 
     * @param model This is the model that will be passed to the view.
     * @return Redirects to the home page where its listed all the restaurants from the database
     */
    @GetMapping("/")
    public String index(Model model) {
        String query = "SELECT x FROM Restaurante x"; 
        List<Restaurante> availableRestaurants = entityManager.createQuery(query, Restaurante.class).getResultList();
        model.addAttribute("availableRestaurants", availableRestaurants);

        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("Sin Filtro");
        filterOptions.add("Favoritos");
        filterOptions.add("Precio Ascendente");
        filterOptions.add("Precio Descendente");
        filterOptions.add("Populares");
        
        query = "Select x from Label x";
        List<Label> labelOptions = entityManager.createQuery(query, Label.class).getResultList();

        model.addAttribute("filterOptions",filterOptions);
        model.addAttribute("labelOptions",labelOptions);
        return "index";
    }
}
