package es.ucm.fdi.iw.controller;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import es.ucm.fdi.iw.model.Cliente;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Controller
/**
 * > This class is responsible for handling the registration of new users
 */
public class RegisterController {
    @Autowired
    private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

    /**
     * This function is used to display the registration form
     * 
     * @param model This is the model that will be passed to the view.
     * @return A String with the name of the view to be rendered.
     */
    @GetMapping("/registro")
    public String registro(Model model){
        model.addAttribute("usuario", new Cliente());
        return "registro";
    }

    /**
     * It registers a new user of Cliente type to the system
     * 
     * @param usuario The object that will be used to store the data entered by the user.
     * @param model This is the model that will be passed to the view.
     * @return Redirects to a welcome view to the new user
     */
    @Transactional
    @PostMapping("/registro")
    public String hacerRegistro(@ModelAttribute Cliente usuario ,Model model){
        usuario.setEnabled(true);
        usuario.setRoles("USER");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        entityManager.persist(usuario);
        entityManager.flush();
        model.addAttribute("usuario", usuario);
        return "finRegistro";
    }
}
