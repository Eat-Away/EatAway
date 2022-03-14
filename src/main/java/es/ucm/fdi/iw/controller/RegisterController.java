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
public class RegisterController {
    @Autowired
    private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String registro(Model model){
        model.addAttribute("usuario", new Cliente());
        return "registro";
    }
    @Transactional
    @PostMapping("/registro")
    public String hacerRegistro(@ModelAttribute Cliente usuario ,Model model){
        usuario.setRoles("USER");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        entityManager.persist(usuario);
        entityManager.flush();
        model.addAttribute("usuario", usuario);
        return "finRegistro";
    }
}
