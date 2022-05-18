package es.ucm.fdi.iw.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Cliente;
import es.ucm.fdi.iw.model.Extra;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.Repartidor;
import es.ucm.fdi.iw.model.Restaurante;
import es.ucm.fdi.iw.model.Restaurador;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Pedido.Estado;
import es.ucm.fdi.iw.model.User.Role;

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
	/**
     * This class is a RuntimeException that is thrown when a user tries to access a resource that they
     * don't have permission to access.
     */
    @ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="Alto ahí, no tienes permiso para hacer esto")  // 403
    public static class PermisoDenegadoException extends RuntimeException {}
    
	private static final Logger log = LogManager.getLogger(AdminController.class);
    @Autowired
	private EntityManager entityManager;
    @Autowired
    private LocalData localData;
	@Autowired
	private PasswordEncoder passwordEncoder;

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
        listaUsuarios.remove(0);//Elimina el admin de la lista (No bloquearnos a nosotros mismos)
        query = "SELECT X FROM Restaurante X";
        List<Restaurante> listaRestaurantes = entityManager.createQuery(query, Restaurante.class).getResultList();
        User u = (User) session.getAttribute("u");
        //Comprueba si los usuarios tienen foto o no (Para saber si se puede borrar o no)
        List<Boolean> tieneImg = new ArrayList<>();
        Iterator<User> iteraUsuarios = listaUsuarios.iterator();
        while(iteraUsuarios.hasNext()){
            User usr = iteraUsuarios.next();
            tieneImg.add(checkImg(usr.getId()));
        }
        model.addAttribute("user", u);
        model.addAttribute("usuarios", listaUsuarios);
        model.addAttribute("hasImg", tieneImg);
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
    public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
        File f = localData.getFile("restaurante/"+id, ""+id+"Logo");
        
        InputStream in = new BufferedInputStream(f.exists() ?
        new FileInputStream(f) : AdminController.defaultPic());
        //return os -> FileCopyUtils.copy(new FileInputStream(f), os);
        return os -> FileCopyUtils.copy(in, os);
    }


    /**
     * Si el archivo existe, devuelve verdadero, de lo contrario, devuelve falso.
     * 
     * @param id la identificación del usuario
     * @return Un valor booleano.
     */
    public boolean checkImg(@PathVariable long id){
        File f = localData.getFile("user", ""+id+".jpg");
        return f.exists();
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

    //FUNCIONES DE GESTION DE USUARIOS

    @GetMapping("/altaUsuario")
    public String altaUsuario(Model model, @RequestParam("rol") int rol){
        switch(rol){
            case 0://Cliente
                model.addAttribute("usuario", new Cliente());
                break;
            case 1://Repartidor
                model.addAttribute("usuario", new Repartidor());
                break;
            case 2://Propietario de restaurante
                model.addAttribute("usuario", new Restaurador());
                break;
        }
        model.addAttribute("role", rol);
        return "registroAvanzado";
    }

    /**
     * It registers a new user of Cliente type to the system
     * 
     * @param usuario The object that will be used to store the data entered by the user.
     * @param model This is the model that will be passed to the view.
     * @return Redirects to a welcome view to the new user
     */
    @Transactional
    @PostMapping("/registroCliente")
    public String registroCliente(@ModelAttribute Cliente usuario, @RequestParam("role") String rol ,Model model, HttpSession session){
        usuario.setRoles("USER");
        usuario.setEnabled(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        entityManager.persist(usuario);
        entityManager.flush();
        model.addAttribute("message", "Se ha creado el usuario nuevo. Contacte con el interesado para darle su usuario y contraseña");
        return index(model, session);
    }

    @Transactional
    @PostMapping("/registroRepartidor")
    public String registroRepartidor(@ModelAttribute Repartidor usuario, @RequestParam("role") String rol ,Model model, HttpSession session){
        usuario.setRoles("REPARTIDOR,USER");
        usuario.setEnabled(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        entityManager.persist(usuario);
        entityManager.flush();
        model.addAttribute("message", "Se ha creado el usuario nuevo. Contacte con el interesado para darle su usuario y contraseña");
        return index(model, session);
    }

    @Transactional
    @PostMapping("/registroPropietario")
    public String registroPropietario(@ModelAttribute Restaurador usuario, @RequestParam("role") String rol ,Model model, HttpSession session){
        usuario.setRoles("RESTAURANTE,USER");
        usuario.setEnabled(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        entityManager.persist(usuario);
        entityManager.flush();
        model.addAttribute("message", "Se ha creado el usuario nuevo. Contacte con el interesado para darle su usuario y contraseña");
        return index(model, session);
    }

    @Transactional
    @PostMapping("/delPic")
    public String delPic(Model model, HttpSession session, @RequestParam("idUsr") long id){
        User u = (User) session.getAttribute("u");
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        User usr = entityManager.find(User.class, id);
        File f = localData.getFolder("user/"+usr.getId()+".jpg");
        try{
            if(FileSystemUtils.deleteRecursively(f)){
                log.info("Se ha borrado la foto de perfil del usuario "+usr.getId());
            }else{
                log.warn("Error eliminando la foto de perfil del usuario. Ruta: " + f.getAbsolutePath());
            }
        }catch(Exception ex){
            log.error("Excepcion eliminando la foto de perfil del usuario. Ruta: " + f.getAbsolutePath());
            throw ex;
        }
        model.addAttribute("message", "Se ha borrado  la foto de perfil del usuario "+ usr.getUsername() + " exitosamente!");
        return index(model, session);
    }

    @Transactional
    @PostMapping("/banUser")
    public String banearUsuario(Model model, HttpSession session, @RequestParam("idUsr") long id){
        User u = (User) session.getAttribute("u");
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        User usr = entityManager.find(User.class, id);
        usr.setEnabled(false);
        usr.setPedidos(usr.getPedidos());
        usr.setReceived(usr.getReceived());
        usr.setRestaurantes(usr.getRestaurantes());
        usr.setSent(usr.getSent());
        entityManager.merge(usr);
        entityManager.flush();
        model.addAttribute("message", "Se ha bloqueado al usuario correctamente");
        return index(model, session);
    }

    @Transactional
    @PostMapping("/unbanUser")
    public String desbanearUsuario(Model model, HttpSession session, @RequestParam("idUsr") long id){
        User u = (User) session.getAttribute("u");
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        User usr = entityManager.find(User.class, id);
        usr.setEnabled(true);
        usr.setPedidos(usr.getPedidos());
        usr.setReceived(usr.getReceived());
        usr.setRestaurantes(usr.getRestaurantes());
        usr.setSent(usr.getSent());
        entityManager.merge(usr);
        entityManager.flush();
        model.addAttribute("message", "Se ha desbloqueado al usuario correctamente");
        return index(model, session);
    }

    //FUNCIONES DE GESTION DE RESTAURANTES
    /**
     * It returns the adminRestaurante.html page, which is the page where the restaurant owner can see the
     * orders that have been made to his restaurant
     * 
     * @param model The model is a Map that is used to pass values from the controller to the view.
     * @param session The session object.
     * @param id The id of the restaurant.
     * @return Returns to adminRestaurante.html with the Restaurante found and a list of Pedidos
     */
    @GetMapping("/adminRestaurante")
    public String adminRestaurante(Model model, HttpSession session, @RequestParam("id") long id){
        User u = (User) session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, id);
        if(!u.hasRole(Role.ADMIN)){
            return "index";
        }
        String query = "SELECT X FROM Pedido X WHERE X.estado >= 1 AND X.estado <= 4 AND X.restaurante.id ="+r.getId();
        List<Pedido> p = (List<Pedido>) entityManager.createQuery(query, Pedido.class).getResultList();
        model.addAttribute("restaurante", r);
        model.addAttribute("pedidos", p);
        model.addAttribute("admin", 1);
        return "adminRestaurante";
    }

    /**
     * It takes a file path, a file name, and a MultipartFile object, and writes the contents of the
     * MultipartFile object to the file path and file name
     * 
     * @param path The path to the folder where the file will be saved.
     * @param name The name of the file to be uploaded.
     * @param src The file to upload
     * @return A boolean value.
     */
    private boolean uploadPhoto(String path, String name, MultipartFile src){
        File f = localData.getFile(path, name);
        try (BufferedOutputStream stream =
                new BufferedOutputStream(new FileOutputStream(f))) {
            byte[] bytes = src.getBytes();
            stream.write(bytes);
        } catch (Exception e) {
            log.warn("Error uploading " + f.getAbsolutePath() + " ", e);
            return false;
        }
        return true;
    }
    	
    /**
     * It returns a picture of a dish
     * 
     * @param idR The id of the restaurant
     * @param id The id of the dish
     * @return The image of the dish
     */
    @GetMapping("/rimg/{idR}/plato/{id}")
    public StreamingResponseBody getFotoPlato(@PathVariable long idR, @PathVariable long id) throws IOException {
        //File f = localData.getFile("restaurante/"+idR+"/plato", ""+id);
        //return os -> FileCopyUtils.copy(new FileInputStream(f), os);

        File f = localData.getFile("restaurante/"+idR+"/plato", ""+id);
        
        InputStream in = new BufferedInputStream(f.exists() ?
        new FileInputStream(f) : AdminController.defaultPic());
        //return os -> FileCopyUtils.copy(new FileInputStream(f), os);
        return os -> FileCopyUtils.copy(in, os);
    }

    //GESTION DE RESTAURANTES

    /**
     * It deletes a restaurant.
     * 
     * @param id The id of the restaurant to be deleted.
     * @param session The session object.
     * @param model The model object that will be used to render the view.
     * @return Redirects to the page of the restaurant owner profile.
     */
    @Transactional
    @PostMapping("/delRestaurante")
    public String borraRestaurante(@RequestParam long id, HttpSession session, Model model) throws Exception{
        User u = (User)session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Restaurante rest = entityManager.find(Restaurante.class, id);
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        entityManager.remove(rest);
        entityManager.flush();
        //Elimina el fichero asociado al plato
        File f = localData.getFolder("restaurante/"+rest.getId());
        try{
            if(FileSystemUtils.deleteRecursively(f)){
                log.info("Se ha borrado la multimedia del restaurante "+rest.getId());
            }else{
                log.warn("Error eliminando la multimedia del restaurante. Ruta: " + f.getAbsolutePath());
            }
        }catch(Exception ex){
            log.error("Excepcion eliminando la multimedia del restaurante. Ruta: " + f.getAbsolutePath());
            throw ex;
        }
        model.addAttribute("message", "Se ha borrado el restaurante "+ rest.getNombre() + " exitosamente!");
        return index(model, session);
    }

    /**
     * This function is called when the user clicks on the edit button of a restaurant. It takes the id of
     * the restaurant as a parameter and returns the editRestaurante.html page with the restaurant's
     * information
     * 
     * @param id The id of the restaurant to be edited.
     * @param model This is the model that will be passed to the view.
     * @return Redirects to the form where the user can edit the restaurant info.
     */
    @GetMapping("/editRestaurante")
    public String editarRestaurante(@RequestParam long id, Model model){
        Restaurante rest = entityManager.find(Restaurante.class, id);
        model.addAttribute("restaurante", rest);
        model.addAttribute("admin", 1);
        return "editRestaurante";
    }

    /**
     * It updates the restaurant's information in the database, and uploads the new logo and carousel
     * photos to the server
     * 
     * @param restaurante The restaurant object that will be updated.
     * @param photo The logo that the user uploads.
     * @param carouselPhotos An array of MultipartFile objects that will be used to upload the carousel
     * photos.
     * @param session The session object, which is used to get the user's id.
     * @param model The model object that is used to pass data to the view.
     * @return Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/editRestaurante")
    public String procesarEditarRestaurante(@ModelAttribute Restaurante restaurante, @RequestParam MultipartFile photo,@RequestParam("carousel") MultipartFile[] carouselPhotos, HttpSession session, Model model){
        User u = (User) session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, restaurante.getId());
        restaurante.setPropietario(r.getPropietario());
        restaurante.setComentarios(r.getComentarios());
        restaurante.setPedidos(r.getPedidos());
        restaurante.setPlatos(r.getPlatos());
        restaurante.setValoracion(r.getValoracion());
        entityManager.merge(restaurante);
        entityManager.flush();
        //Subida de foto modificada para admitir subida vacia (No se ha cambiado la foto)
		if (!photo.isEmpty()) {
            log.info("Updating photo for restaurant {}", u.getId());
            if(uploadPhoto("restaurante/"+restaurante.getId(), restaurante.getId()+"Logo", photo)){
                log.info("Successfully uploaded photo!");
            }else{
				log.warn("Error uploading photo");
            }
		}
        if(carouselPhotos.length != 0){
            int arrayLength = carouselPhotos.length;
            if(arrayLength > 2){//Si se suben mas de dos ficheros se cargan solo los dos primeros del array
                arrayLength = 2;
            }
            for(int i=0;i<arrayLength;i++){
                int n = i + 1;
                if (!photo.isEmpty()) {
                    log.info("Updating photo for restaurant {}", u.getId());
                    if(uploadPhoto("restaurante/"+restaurante.getId(), restaurante.getId()+"Carousel"+n, carouselPhotos[i])){
                        log.info("Successfully uploaded photo!");
                    }else{
                        log.warn("Error uploading photo");
                    }
                }
            }
        }else{
            log.info("No carousel photos selected to upload");
        }
        model.addAttribute("message", "Se ha editado el restaurante "+restaurante.getNombre() + " exitosamente");
        return adminRestaurante(model, session, restaurante.getId());
    }

    //GESTION DE EXTRAS

    /**
     * It takes the id of a plato, finds the plato in the database, and then passes the plato to the
     * addExtra.html template, where a new extra will be added to that plato
     * 
     * @param model This is the model that will be passed to the view.
     * @param id the id of the dish
     * @return Redirects to a form where the restaurant owner can add a extra to the dish
     */
    @GetMapping("/addExtra")
    public String altaExtra(Model model, @RequestParam long id){
        Plato listaPlatos = entityManager.find(Plato.class, id);
        model.addAttribute("extra", new Extra());
        model.addAttribute("plato", listaPlatos);
        model.addAttribute("admin", 1);
        return "addExtra";
    }

    /**
     * It adds an extra to a dish
     * 
     * @param idPlato The id of the dish to which the extra will be added.
     * @param extra The object that will be created and persisted.
     * @param session The session object.
     * @param model The model object that is used to pass data to the view.
     * @return Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/addExtra")
    public String procesaAltaExtra(@RequestParam long idPlato, @ModelAttribute Extra extra, HttpSession session, Model model){
         //Obtiene el usuario que agrega el plato para al procesarse vuelva al adminRestaurante
        User u = (User)session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Plato p = entityManager.find(Plato.class, idPlato);
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        extra.setPlato(p);
        entityManager.persist(extra);
        p.getExtras().add(extra);
        model.addAttribute("message", "Se ha añadido el extra nuevo en el plato "+p.getNombre()+" para el restaurante "+p.getRestaurante().getNombre());
        return adminRestaurante(model, session, p.getRestaurante().getId());
    }

    /**
     * It deletes an extra from a dish
     * 
     * @param model The model object that will be used to render the view.
     * @param idExtra The id of the extra to be deleted
     * @param session The session object, which is used to get the user object.
     * @return  Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/delExtra")
    public String procesaBajaExtra(Model model, @RequestParam long idExtra, HttpSession session){
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Extra ex = entityManager.find(Extra.class, idExtra);
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        entityManager.remove(ex);
        entityManager.flush();
        model.addAttribute("message", "Se ha borrado el extra " + ex.getNombre() + " del plato " + ex.getPlato().getNombre() + " exitosamente");
        return adminRestaurante(model, session, ex.getPlato().getRestaurante().getId());
    }

    /**
     * It gets the extra with the given id, and then passes it to the editExtra view, where the owner can edit the extra info.
     * 
     * @param model The model is an object that will be used to pass data to the view.
     * @param id the id of the extra we want to edit
     * @return Redirects to a form where the restaurant owner can edit the info of a extra.
     */
    @GetMapping("/editExtra")
    public String editarExtra(Model model, @RequestParam long id){
        Extra extra = entityManager.find(Extra.class, id);
        model.addAttribute("extra", extra);
        model.addAttribute("nombrePlato", extra.getPlato().getNombre());
        model.addAttribute("admin", 1);
        return "editExtra";
    }

    /**
     * It takes an extra object, a session object, and a model object, and then it updates the extra object
     * in the database
     * 
     * @param extra The object that will be updated.
     * @param session The session object.
     * @param model The model object that is used to pass data to the view.
     * @return  Redirects to the page with the restaurant info.
     */
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
        return adminRestaurante(model, session, ex.getPlato().getRestaurante().getId());
    }

    //GESTION DE PLATOS

    /**
     * It takes a model and an id, finds the restaurante with that id, adds a new plato to the model, and
     * returns the addPlato view, where the restaurant owner can add a new dish to the database
     * 
     * @param model This is the model that will be passed to the view.
     * @param id the id of the restaurant
     * @return Redirects to a form where the restaurant owner can add a new dish to a restaurant.
     */
    @GetMapping("/addPlato")
    public String altaPlato(Model model, @RequestParam long id){
        Restaurante r = entityManager.find(Restaurante.class, id);
        model.addAttribute("plato", new Plato());
        model.addAttribute("restaurante", r);
        model.addAttribute("admin", 1);
        return "addPlato";
    }

    /**
     * It adds a new dish to a restaurant
     * 
     * @param idRestaurante The id of the restaurant that the dish belongs to.
     * @param photo The photo of the dish that the user uploads.
     * @param plato The object that will be persisted in the database.
     * @param session The session object, which is used to get the user that is logged in.
     * @param model The model object that will be used to render the view.
     * @return  Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/addPlato")
    public String procesaAltaPlato(@RequestParam long idRestaurante, @RequestParam MultipartFile photo, @ModelAttribute Plato plato, HttpSession session, Model model){
        User u = (User)session.getAttribute("u"); //Obtiene el usuario que agrega el plato para al procesarse vuelva al perfilRestaurante
        u = entityManager.find(User.class, u.getId());
        Restaurante r = entityManager.find(Restaurante.class, idRestaurante);
        if (!u.hasRole(Role.ADMIN)) {
            throw new PermisoDenegadoException();
        }

        plato.setRestaurante(r);
        entityManager.persist(plato);
        
        r.getPlatos().add(plato);


        if(!photo.isEmpty()){
            log.info("Updating photo for dish {}", u.getId());
            if(uploadPhoto("restaurante/"+plato.getRestaurante().getId()+"/plato", ""+plato.getId(), photo)){
                log.info("Successfully uploaded photo!");
            }else{
                log.warn("Error uploading photo");
            }
        }else{
            log.info("Not photo selected to upload");
        }

        model.addAttribute("message", "Se ha añadido el plato nuevo en "+r.getNombre());
        return adminRestaurante(model, session, r.getId());
    }

    /**
     * It deletes a dish from the database and the associated image from the file system
     * 
     * @param idPlato The id of the dish to be deleted.
     * @param model The model object that will be used to render the view.
     * @param session The session object.
     * @return Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/delPlato")
    public String procesarBorradoPlato(@RequestParam long idPlato, Model model, HttpSession session) throws IOException{
        User u = (User)session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        Plato p = entityManager.find(Plato.class, idPlato);
        // Checking if the user is the owner of the restaurant.
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        entityManager.remove(p);
        entityManager.flush();
        //Elimina el fichero asociado al plato
        File f = localData.getFile("restaurante/"+p.getRestaurante().getId()+"/plato/", ""+p.getId());
        try{
            Files.delete(f.toPath());
            log.info("Se ha borrado la imagen del plato"+p.getId());
        }catch(IOException ex){
            log.warn("Error eliminando la imagen del plato. Ruta: " + f.getAbsolutePath());
            throw ex;
        }
        model.addAttribute("message", "Se ha borrado el plato " + p.getNombre() + " exitosamente");
        return adminRestaurante(model, session, p.getRestaurante().getId());
    }

    /**
     * It takes the id of a plato, finds the plato in the database, and then passes the plato to the
     * editPlato.html template where the restaurant owner can edit it
     * 
     * @param id The id of the plato we want to edit.
     * @param model This is the model that will be passed to the view.
     * @return Redirects to a form where the restaurant owner can edit the dish
     */
    @GetMapping("/editPlato")
    public String editarPlato(@RequestParam long id, Model model){
        Plato plato = entityManager.find(Plato.class, id);
        model.addAttribute("plato", plato);
        model.addAttribute("admin", 1);
        return "editPlato";
    }

    /**
     * It updates a dish in the database
     * 
     * @param plato The object that will be updated in the database.
     * @param photo The dish photo that the user uploaded.
     * @param session The session object, which is used to get the user's id.
     * @param model The model object that is used to pass data to the view.
     * @return Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/editPlato")
    public String procesarEditarPlato(@ModelAttribute Plato plato, @RequestParam MultipartFile photo, HttpSession session, Model model){
        User u = (User) session.getAttribute("u");
        Plato p = entityManager.find(Plato.class, plato.getId());
        plato.setComentarios(p.getComentarios());
        plato.setExtras(p.getExtras());
        plato.setPlatoPedidos(p.getPlatoPedidos());
        plato.setRestaurante(p.getRestaurante());
        entityManager.merge(plato);
        entityManager.flush();
        
        //Subida de foto modificada para admitir subida vacia (No se ha cambiado la foto)
		if (!photo.isEmpty()) {
            log.info("Updating photo for dish {}", u.getId());
            if(uploadPhoto("restaurante/"+plato.getRestaurante().getId()+"/plato", ""+plato.getId(), photo)){
                log.info("Successfully uploaded photo!");
            }else{
                log.warn("Error uploading photo");
            }
		}

        model.addAttribute("message", "Se ha actualizado el plato " + p.getNombre() + " del restaurante " + plato.getRestaurante().getNombre() + " exitosamente");
        return adminRestaurante(model, session, plato.getRestaurante().getId());
    }


    /**
     * It updates the state of a given order
     * 
     * @param idR The id of the restaurant
     * @param id The id of the order
     * @param estado the new state of the order
     * @param model The model object that is used to pass data to the view.
     * @param session the session object
     * @return Redirects to the page with the restaurant info.
     */
    @Transactional
    @PostMapping("/updatePedido")
    public String updatePedido(@RequestParam("id") long idR, @RequestParam("idPed") long id, @RequestParam("sigEstado") Estado estado, Model model, HttpSession session){
        User u = (User) session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, idR);
        // Checking if the user is the owner of the restaurant.
        if(!u.hasRole(Role.ADMIN)){
            throw new PermisoDenegadoException();
        }
        Pedido p = entityManager.find(Pedido.class, id);
        p.setCliente(p.getCliente());
        p.setContenidoPedido(p.getContenidoPedido());
        p.setRestaurante(p.getRestaurante());
        p.setEstado(estado);
        entityManager.merge(p);
        entityManager.flush();
        model.addAttribute("message", "Se ha actualizado el estado del pedido " + p.getId() + " a " + p.getEstado());
        return adminRestaurante(model, session, r.getId());
    }
}
