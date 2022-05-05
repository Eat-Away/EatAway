package es.ucm.fdi.iw.controller;

import javax.servlet.http.HttpSession;

/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.User;

/**
 *  Site administration.
 *
 *  Access to this end-point is authenticated.
 */
@Controller
@RequestMapping("admin")
/**
 * > This class is a controller that handles requests for the admin page
 */
public class AdminController {

	//private static final Logger log = LogManager.getLogger(AdminController.class);

    /**
     * This function is called when the user navigates to the admin page. It checks if the user is logged
     * in, and if so, it displays the admin page
     * 
     * @param model This is the model object that is used to pass data to the view.
     * @param session The session object is used to store data in the session.
     * @return The admin.html page
     */
	@GetMapping("/")
    public String index(Model model, HttpSession session) {
        User u = (User) session.getAttribute("u");
        model.addAttribute("user", u);
        return "admin";
    }
}
