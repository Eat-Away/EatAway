package es.ucm.fdi.iw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.Extra;
import es.ucm.fdi.iw.model.Label;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.Restaurante;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.UsrRestaurante;
import es.ucm.fdi.iw.model.Extra;
/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    @Autowired
	private EntityManager entityManager;

	private static final Logger log = LogManager.getLogger(RootController.class);


    @GetMapping("/addRestaurante")
    public String formularioRegistro(Model model, @RequestParam long id){
        model.addAttribute("restaurante", new Restaurante());
        return "addRestaurante";
    }

    @Transactional
    @PostMapping("/addRestaurante")
    public String procesaFormulario(@ModelAttribute Restaurante restaurante, HttpSession session, Model model){
        log.traceEntry("Ha entrado al procesado de formulario {}", restaurante);
        User u =(User)session.getAttribute("u");
        restaurante.setPropietario(u);
        restaurante.setValoracion(0.0);
        entityManager.persist(restaurante);
        entityManager.flush();
        model.addAttribute("message", "Se ha creado con éxito el restaurante nuevo");
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/addPlato")
    public String altaPlato(Model model, @RequestParam long id){
        Restaurante r = entityManager.find(Restaurante.class, id);
        model.addAttribute("plato", new Plato());
        model.addAttribute("restaurante", r);
        return "addPlato";
    }

    @Transactional
    @PostMapping("/addPlato")
    public String procesaAltaPlato(@RequestParam long idRestaurante, @ModelAttribute Plato plato, HttpSession session, Model model){
        User u = (User)session.getAttribute("u"); //Obtiene el usuario que agrega el plato para al procesarse vuelva al perfilRestaurante
        u = entityManager.find(User.class, u.getId());
        Restaurante r = entityManager.find(Restaurante.class, idRestaurante);
        if (r == null || r.getPropietario().getId() != u.getId()) {
            // protestar
        }

        plato.setRestaurante(r);
        entityManager.persist(plato);
        
        r.getPlatos().add(plato);

        model.addAttribute("message", "Se ha añadido el plato nuevo en "+r.getNombre());
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/restaurante")
    public String restaurante(Model model, @RequestParam long id) {
        String query = "Select x From Restaurante x Where id="+id;
        Restaurante restaur = (Restaurante) entityManager.createQuery(query).getSingleResult();
        query = "SELECT x FROM Plato x WHERE RESTAURANTE_ID=" + id;
        List<Plato> platos = (List<Plato>)entityManager.createQuery(query).getResultList();
        model.addAttribute("restaurante", restaur);
        model.addAttribute("platos", platos);
        return "restaurante";
    }

    @GetMapping("/platos")
    public String platos(Model model, @RequestParam long id) {
        String query = "Select x From Plato x Where id="+id;
        Plato plato = (Plato) entityManager.createQuery(query).getSingleResult();
        query = "SELECT x FROM Extra x WHERE Plato_id=" + plato.getId();
        List<Extra> extras = (List<Extra>)entityManager.createQuery(query).getResultList();
        model.addAttribute("plato", plato);
        model.addAttribute("extras", extras);
        return "platos";
    }
    

    @GetMapping("/perfilRestaurante")
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
    }


	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/repartidor")
    public String repartidor(Model model) {
        return "repartidor";
    }


    @GetMapping("/carrito")
    public String carrito(Model model) {
        return "carrito";
    }

    @GetMapping("/pedidoCliente")
    public String pedidoCliente(Model model) {
        return "pedidoCliente";
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
