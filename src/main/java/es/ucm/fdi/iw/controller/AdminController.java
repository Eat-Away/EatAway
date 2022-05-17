package es.ucm.fdi.iw.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.io.FileInputStream;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Restaurante;
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

	private static final Logger log = LogManager.getLogger(AdminController.class);
    @Autowired
	private EntityManager entityManager;
    @Autowired
    private LocalData localData;

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
        String query = "SELECT X FROM User X";
        List<User> listaUsuarios = entityManager.createQuery(query, User.class).getResultList();
        query = "SELECT X FROM Restaurante X";
        List<Restaurante> listaRestaurantes = entityManager.createQuery(query, Restaurante.class).getResultList();
        User u = (User) session.getAttribute("u");
        model.addAttribute("user", u);
        model.addAttribute("usuarios", listaUsuarios);
        model.addAttribute("restaurantes", listaRestaurantes);
        return "admin";
    }
    
    /**
     * It returns an InputStream of the default picture
     * 
     * @return A stream of bytes that represent the image.
     */
    private static InputStream defaultPic() {
	    return new BufferedInputStream(Objects.requireNonNull(
            RestauranteController.class.getClassLoader().getResourceAsStream(
                "static/img/default-pic.jpg")));
    }
    /**
     * It returns a logo of the restaurant with the given id
     * 
     * @param id the id of the restaurant
     * @return The logo of the restaurant
     */
    @GetMapping("/rimg/{id}")
    public StreamingResponseBody getRestaurantePic(@PathVariable long id) throws IOException {
        File f = localData.getFile("restaurante/"+id, ""+id+"Logo");
        
        InputStream in = new BufferedInputStream(f.exists() ?
        new FileInputStream(f) : AdminController.defaultPic());
        //return os -> FileCopyUtils.copy(new FileInputStream(f), os);
        return os -> FileCopyUtils.copy(in, os);
    }

    /**
	 * Downloads a profile pic for a user id
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@GetMapping("{id}/conf")
	public StreamingResponseBody getUserPic(@PathVariable long id) throws IOException {
		File f = localData.getFile("user", ""+id+".jpg");
		InputStream in = new BufferedInputStream(f.exists() ?
			new FileInputStream(f) : AdminController.defaultPic());
		return os -> FileCopyUtils.copy(in, os);
	}
}
