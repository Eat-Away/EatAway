package es.ucm.fdi.iw.controller;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import es.ucm.fdi.iw.controller.RootController.PermisoDenegadoException;
import es.ucm.fdi.iw.model.Comentario;
import es.ucm.fdi.iw.model.Extra;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.Restaurante;
import es.ucm.fdi.iw.model.User;

@Controller
@RequestMapping("restaurante")
public class RestauranteController {
    @Autowired
    private EntityManager entityManager;
    
	private static final Logger log = LogManager.getLogger(AdminController.class);

     /**
     * Landing page for a restaurant profile
     */
	@GetMapping("{id}")
    public String perfilRestaurante(Model model, HttpSession session, @PathVariable long id){
        User u =(User)session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, id);

        if (u.getId() != r.getPropietario().getId()) {
            return "index";
        }

        //model.addAttribute("restaurante", r);
        model.addAttribute("availableRestaurants", r.getPropietario().getRestaurantes());
        model.addAttribute("propietario", r.getPropietario());
        return "perfilRestaurante";
    }

    @GetMapping("/{id}/addRestaurante")
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

    @Transactional
    @PostMapping("/delRestaurante")
    public String borraRestaurante(@RequestParam long id, HttpSession session, Model model){
        User u = (User)session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Restaurante rest = entityManager.find(Restaurante.class, id);
        if(rest == null || rest.getPropietario().getId() != u.getId()){
            throw new PermisoDenegadoException();
        }
        for(Plato p: rest.getPlatos()){
            for (Extra e : p.getExtras()){
                entityManager.remove(e);
            }
            for (Comentario c : p.getComentarios()){
                entityManager.remove(c);
            }
            entityManager.remove(p);
        }
        entityManager.remove(rest);
        entityManager.flush();
        model.addAttribute("message", "Se ha borrado el restaurante "+ rest.getNombre() + " exitosamente!");
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/{id}/addExtra")
    public String altaExtra(Model model, @RequestParam long id){
        Plato listaPlatos = entityManager.find(Plato.class, id);
        model.addAttribute("extra", new Extra());
        model.addAttribute("plato", listaPlatos);
        return "addExtra";
    }

    @Transactional
    @PostMapping("/addExtra")
    public String procesaAltaExtra(@RequestParam long idPlato, @ModelAttribute Extra extra, HttpSession session, Model model){
         //Obtiene el usuario que agrega el plato para al procesarse vuelva al perfilRestaurante
        User u = (User)session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Plato p = entityManager.find(Plato.class, idPlato);
        if(p == null || p.getRestaurante().getPropietario().getId() != u.getId()){
            throw new PermisoDenegadoException();
        }
        extra.setPlato(p);
        entityManager.persist(extra);
        p.getExtras().add(extra);
        model.addAttribute("message", "Se ha añadido el extra nuevo en el plato "+p.getNombre()+" para el restaurante "+p.getRestaurante().getNombre());
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/{id}/delExtra")
    public String bajaExtra(Model model, @RequestParam long id){
        Plato plato = entityManager.find(Plato.class, id);
        List<Extra> listaExtras = plato.getExtras();
        model.addAttribute("extras", listaExtras);
        return "delExtra";
    }

    @Transactional
    @PostMapping("/delExtra")
    public String procesaBajaExtra(Model model, @RequestParam long idExtra, HttpSession session){
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Extra ex = entityManager.find(Extra.class, idExtra);
        if(ex == null || ex.getPlato().getRestaurante().getPropietario().getId() != u.getId()){
            throw new PermisoDenegadoException();
        }
        entityManager.remove(ex);
        entityManager.flush();
        model.addAttribute("message", "Se ha borrado el extra " + ex.getNombre() + " del plato " + ex.getPlato().getNombre() + " exitosamente");
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/{id}/addPlato")
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
            throw new PermisoDenegadoException();
        }

        plato.setRestaurante(r);
        entityManager.persist(plato);
        
        r.getPlatos().add(plato);

        model.addAttribute("message", "Se ha añadido el plato nuevo en "+r.getNombre());
        return perfilRestaurante(model, session, u.getId());
    }

    @Transactional
    @PostMapping("/delPlato")
    public String procesarBorradoPlato(@RequestParam long idPlato, Model model, HttpSession session){
        User u = (User)session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Plato p = entityManager.find(Plato.class, idPlato);
        if(p == null || p.getRestaurante().getPropietario().getId() != u.getId()){
            throw new PermisoDenegadoException();
        }
        for (Extra e : p.getExtras()){
            entityManager.remove(e);
        }
        for (Comentario c : p.getComentarios()){
            entityManager.remove(c);
        }

        entityManager.remove(p);
        entityManager.flush();
        model.addAttribute("message", "Se ha borrado el plato " + p.getNombre() + " exitosamente");
        return perfilRestaurante(model, session, u.getId());
    }

}
