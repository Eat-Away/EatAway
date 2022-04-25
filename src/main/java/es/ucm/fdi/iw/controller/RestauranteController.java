package es.ucm.fdi.iw.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpSession;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.controller.RootController.PermisoDenegadoException;
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
	
    @Autowired
    private LocalData localData;

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

    //GESTION DE RESTAURANTES

    @GetMapping("/{id}/addRestaurante")
    public String formularioRegistro(Model model, @RequestParam long id){
        model.addAttribute("restaurante", new Restaurante());
        List<String> horarioSelect = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            for(int j = 0; j < 60; j += 30){
                String hora = String.format("%02d", i);
                String min = String.format("%02d", j);
                horarioSelect.add(hora + ":" + min);
            }
        }
        model.addAttribute("horarioSelect", horarioSelect);
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
        entityManager.remove(rest);
        entityManager.flush();
        model.addAttribute("message", "Se ha borrado el restaurante "+ rest.getNombre() + " exitosamente!");
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/{id}/editRestaurante")
    public String editarRestaurante(@RequestParam long id, Model model){
        Restaurante rest = entityManager.find(Restaurante.class, id);
        model.addAttribute("restaurante", rest);
        return "editRestaurante";
    }

    @Transactional
    @PostMapping("/editRestaurante")
    public String procesarEditarRestaurante(@ModelAttribute Restaurante restaurante, HttpSession session, Model model){
        User u = (User) session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, restaurante.getId());
        restaurante.setPropietario(u);
        restaurante.setComentarios(r.getComentarios());
        restaurante.setPedidos(r.getPedidos());
        restaurante.setPlatos(r.getPlatos());
        restaurante.setValoracion(r.getValoracion());
        entityManager.merge(restaurante);
        entityManager.flush();
        model.addAttribute("message", "Se ha editado el restaurante "+restaurante.getNombre() + " exitosamente");
        return perfilRestaurante(model, session, u.getId());
    }

    //GESTION DE EXTRAS

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

    @GetMapping("/{id}/editExtra")
    public String editarExtra(Model model, @RequestParam long id){
        Extra extra = entityManager.find(Extra.class, id);
        model.addAttribute("extra", extra);
        model.addAttribute("nombrePlato", extra.getPlato().getNombre());
        return "editExtra";
    }

    @Transactional
    @PostMapping("/editExtra")
    public String procesarEditarExtra(@ModelAttribute Extra extra, HttpSession session, Model model){
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Extra ex = entityManager.find(Extra.class, extra.getId());
        extra.setPlato(ex.getPlato());
        entityManager.merge(extra);
        entityManager.flush();
        model.addAttribute("message", "Se ha actualizado el extra " + ex.getNombre() + " del plato " + ex.getPlato().getNombre() + " de " + ex.getPlato().getRestaurante().getNombre() + " exitosamente!");
        return perfilRestaurante(model, session, u.getId());
    }

    //GESTION DE PLATOS

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
        entityManager.remove(p);
        entityManager.flush();
        model.addAttribute("message", "Se ha borrado el plato " + p.getNombre() + " exitosamente");
        return perfilRestaurante(model, session, u.getId());
    }

    @GetMapping("/{id}/editPlato")
    public String editarPlato(@RequestParam long id, Model model){
        Plato plato = entityManager.find(Plato.class, id);
        model.addAttribute("plato", plato);
        return "editPlato";
    }

    @Transactional
    @PostMapping("/editPlato")
    public String procesarEditarPlato(@ModelAttribute Plato plato, HttpSession session, Model model){
        User u = (User) session.getAttribute("u");
        Plato p = entityManager.find(Plato.class, plato.getId());
        plato.setComentarios(p.getComentarios());
        plato.setExtras(p.getExtras());
        plato.setPlatoPedidos(p.getPlatoPedidos());
        plato.setRestaurante(p.getRestaurante());
        entityManager.merge(plato);
        entityManager.flush();
        model.addAttribute("message", "Se ha actualizado el plato " + p.getNombre() + " del restaurante " + plato.getRestaurante().getNombre() + " exitosamente");
        return perfilRestaurante(model, session, u.getId());
    }
}
