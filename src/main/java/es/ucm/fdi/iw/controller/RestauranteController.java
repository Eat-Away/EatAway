package es.ucm.fdi.iw.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import javax.servlet.http.HttpSession;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.controller.RootController.PermisoDenegadoException;
import es.ucm.fdi.iw.model.Extra;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.Restaurador;
import es.ucm.fdi.iw.model.Restaurante;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Pedido.Estado;

@Controller
@RequestMapping("restaurante")
public class RestauranteController {
    
    @Autowired
    private EntityManager entityManager;
	
    private static final Logger log = LogManager.getLogger(AdminController.class);
	
    @Autowired
    private LocalData localData;
    
    /**
     * It returns the profile of a restaurant owner
     * 
     * @param model This is the model that will be passed to the view.
     * @param session the session object
     * @param id the id of the restaurant
     * @return Redirects to perfilRestaurante.html with a list of Restaurante objects owned by the user and a Restaurador object with the owner info
     */
    @GetMapping("{id}")
    public String perfilRestaurante(Model model, HttpSession session, @PathVariable long id){
        User u =(User)session.getAttribute("u");
        Restaurador owner = entityManager.find(Restaurador.class, u.getId());
        model.addAttribute("availableRestaurants", owner.getRestaurantes());
        model.addAttribute("propietario", owner);
        return "perfilRestaurante";
    }

    /**
     * It returns the adminRestaurante.html page, which is the page where the restaurant owner can see the
     * orders that have been made to his restaurant
     * 
     * @param model The model is a Map that is used to pass values from the controller to the view.
     * @param session The session object.
     * @param id The id of the restaurant.
     * @return Returns to adminRestaurante.html with the Restaurante found and a list of Pedidos
     */
    @GetMapping("/{id}/adminRestaurante")
    public String adminRestaurante(Model model, HttpSession session, @RequestParam("id") long id){
        User u = (User) session.getAttribute("u");
        Restaurante r = entityManager.find(Restaurante.class, id);
        if(u.getId() != r.getPropietario().getId()){
            return "index";
        }
        String query = "SELECT X FROM Pedido X WHERE X.estado >= 1 AND X.estado <= 4 AND X.restaurante.id ="+r.getId();
        List<Pedido> p = (List<Pedido>) entityManager.createQuery(query, Pedido.class).getResultList();
        model.addAttribute("restaurante", r);
        model.addAttribute("pedidos", p);
        model.addAttribute("admin", 0);
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
        new FileInputStream(f) : RestauranteController.defaultPic());
        //return os -> FileCopyUtils.copy(new FileInputStream(f), os);
        return os -> FileCopyUtils.copy(in, os);
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
        new FileInputStream(f) : RestauranteController.defaultPic());
        //return os -> FileCopyUtils.copy(new FileInputStream(f), os);
        return os -> FileCopyUtils.copy(in, os);
    }

    //GESTION DE RESTAURANTES

    /**
     * It redirects to addRestaurante.html, a form where the owner of restaurants can add a new restaurant
     * 
     * @param model This is the model that will be passed to the view.
     * @param id The id of the user that is logged in.
     * @return A form to add a restaurant to the user's profile.
     */
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

    /**
     * It receives a restaurant object, a session object, a model object and two arrays of multipart
     * files. It then persists the restaurant object, uploads the multipart files to the server and
     * returns the user's profile page
     * 
     * @param restaurante The restaurant object that will be created.
     * @param session The session object.
     * @param model The model object that is used to pass data to the view.
     * @param photo The logo of the restaurant the user is creating.
     * @param carouselPhotos An array of MultipartFile objects that will be used to upload the carousel
     * photos.
     * @return The method returns the view of the profile of the restaurant owner.
     */
    @Transactional
    @PostMapping("/addRestaurante")
    public String procesaFormulario(@ModelAttribute Restaurante restaurante, HttpSession session, Model model, @RequestParam("photo") MultipartFile photo, @RequestParam("carousel") MultipartFile[] carouselPhotos){
        log.traceEntry("Ha entrado al procesado de formulario {}", restaurante);
        User u =(User)session.getAttribute("u");
        restaurante.setPropietario(u);
        restaurante.setValoracion(0.0);
        entityManager.persist(restaurante);
        entityManager.flush();
        //Segmento encargado de la subida de imagenes
        //Subida del logo
        if(!photo.isEmpty()){
            log.info("Updating photo for restaurant {}", u.getId());
            if(uploadPhoto("restaurante/"+restaurante.getId(), restaurante.getId()+"Logo", photo)){
                log.info("Successfully uploaded photo!");
            }else{
                log.warn("Error uploading photo");
            }
        }else{
            log.info("Not photo selected to upload");
        }
        //Subida de fotos del carrusel
        if(carouselPhotos.length != 0){
            int arrayLength = carouselPhotos.length;
            if(arrayLength > 2){//Si se suben mas de dos ficheros se cargan solo los dos primeros del array
                arrayLength = 2;
            }
            for(int i=0;i<arrayLength;i++){
                int n = i + 1;
                if(uploadPhoto("restaurante/"+restaurante.getId(), restaurante.getId()+"Carousel"+n, carouselPhotos[i])){
                    log.info("Successfully uploaded photo!");
                }else{
                    log.warn("Error uploading photo");
                }
            }
        }else{
            log.info("No carousel photos selected to upload");
        }
        //Si todo ha ido bien dara el mensaje de exito
        model.addAttribute("message", "Se ha creado con éxito el restaurante nuevo");
        return perfilRestaurante(model, session, u.getId());
    }

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
        if(rest == null || rest.getPropietario().getId() != u.getId()){
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
        return perfilRestaurante(model, session, u.getId());
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
    @GetMapping("/{id}/editRestaurante")
    public String editarRestaurante(@RequestParam long id, Model model){
        Restaurante rest = entityManager.find(Restaurante.class, id);
        model.addAttribute("restaurante", rest);
        model.addAttribute("admin", 0);
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
        restaurante.setPropietario(u);
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
    @GetMapping("/{id}/addExtra")
    public String altaExtra(Model model, @RequestParam long id){
        Plato listaPlatos = entityManager.find(Plato.class, id);
        model.addAttribute("extra", new Extra());
        model.addAttribute("plato", listaPlatos);
        model.addAttribute("admin", 0);
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
        if(p == null || p.getRestaurante().getPropietario().getId() != u.getId()){
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
        if(ex == null || ex.getPlato().getRestaurante().getPropietario().getId() != u.getId()){
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
    @GetMapping("/{id}/editExtra")
    public String editarExtra(Model model, @RequestParam long id){
        Extra extra = entityManager.find(Extra.class, id);
        model.addAttribute("extra", extra);
        model.addAttribute("nombrePlato", extra.getPlato().getNombre());
        model.addAttribute("admin", 0);
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
    @GetMapping("/{id}/addPlato")
    public String altaPlato(Model model, @RequestParam long id){
        Restaurante r = entityManager.find(Restaurante.class, id);
        model.addAttribute("plato", new Plato());
        model.addAttribute("restaurante", r);
        model.addAttribute("admin", 0);
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
        if (r == null || r.getPropietario().getId() != u.getId()) {
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
        if(p == null || p.getRestaurante().getPropietario().getId() != u.getId()){
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
    @GetMapping("/{id}/editPlato")
    public String editarPlato(@RequestParam long id, Model model){
        Plato plato = entityManager.find(Plato.class, id);
        model.addAttribute("plato", plato);
        model.addAttribute("admin", 0);
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
        if(u.getId() != r.getPropietario().getId()){
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
