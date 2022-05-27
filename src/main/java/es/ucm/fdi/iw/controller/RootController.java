package es.ucm.fdi.iw.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Cliente;
import es.ucm.fdi.iw.model.Extra;
import es.ucm.fdi.iw.model.Label;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.Plato;
import es.ucm.fdi.iw.model.PlatoPedido;
import es.ucm.fdi.iw.model.Restaurante;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Pedido.Estado;


/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {
	
    @Autowired
    private LocalData localData;

    @Autowired
	private EntityManager entityManager;

	private static final Logger log = LogManager.getLogger(RootController.class);

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
     * Procesa el carrito de un usuario, pasando su estado a Pendiente para que el restaurante lo gestione
     * 
     * @param model El objeto de modelo que se usará para representar la vista.
     * @param session El objeto de sesión se utiliza para almacenar la información del usuario.
     * @param id El id del pedido a procesar.
     * @return Una cuerda
     */
	@Transactional
	@PostMapping("/procesaPedido")
	public String procesaPedido(Model model, HttpSession session, @RequestParam("id") long id){
		Pedido ped = entityManager.find(Pedido.class, id);
		User u = (User) session.getAttribute("u");
		Cliente cliente = entityManager.find(Cliente.class, u.getId());
		if(cliente.getId() != ped.getCliente().getId()){
			throw new PermisoDenegadoException();
		}
		ped.setEstado(Estado.PENDIENTE);
		model.addAttribute("message", "El pedido se ha procesado correctamente");
		return index(model, null);
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
    public String index(Model model, @RequestParam(required = false) Long categoria_id) {
        String query = "SELECT x FROM Restaurante x"; 
        List<Restaurante> availableRestaurants = entityManager.createQuery(query, Restaurante.class).getResultList();

        query = "Select x from Label x";
        List<Label> labelOptions = entityManager.createQuery(query, Label.class).getResultList();

        if(categoria_id == null){ //Se muestran todos los restaurantes
            model.addAttribute("availableRestaurants", availableRestaurants);
        }

        else{
            Label cat = entityManager.find(Label.class, categoria_id);
            List<Restaurante> restaurantesCategoria = new ArrayList<>();
            for(int i = 0; i < availableRestaurants.size(); i++){
                if(availableRestaurants.get(i).getLabels().contains(cat)){
                    restaurantesCategoria.add(availableRestaurants.get(i));
                }
            }
            model.addAttribute("availableRestaurants", restaurantesCategoria);
        }

        model.addAttribute("labelOptions",labelOptions);
        return "index";
    }

    private Pedido getCoords(Pedido cart, GeoApiContext geoContext){
        try{
            GeocodingResult[] resultado = GeocodingApi.geocode(geoContext, cart.getDirEntrega()).await();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            log.warn(gson.toJson(resultado[0].addressComponents));
        }catch(Exception ex){
            log.error("Error obteniendo las coordenadas de la dirección especificada", ex);
            cart.setLat(40.4527696);
            cart.setLng(-3.7357);
        }
        return cart;
    }
    
	    
		/**
         * Añade un plato al carrito.
         * 
         * @param model El objeto de modelo que se usará para representar la vista.
         * @param session El objeto de sesión, utilizado para obtener la identificación del usuario.
         * @param extras Una serie de extras que el usuario quiere añadir al plato.
         * @param id El id del plato que se agregará al carrito.
         * @param amount La cantidad del producto que el usuario quiere añadir al carrito.
         * @return Una cadena con el nombre de la vista que se representará.
         */
		@Transactional
		@PostMapping("/addToCart")
		public String addToCart(GeoApiContext geoContext, Model model, HttpSession session,@RequestParam(value = "extras[]", required = false) long[] extras, @RequestParam("id") long id, @RequestParam("cantidad") int amount){
			User u = (User) session.getAttribute("u");
			Cliente cliente = entityManager.find(Cliente.class, u.getId());
			String query = "SELECT X FROM Pedido X WHERE X.cliente="+u.getId()+" AND X.estado=0";
			Pedido cart;
            if(extras == null){
                log.warn("Extras cargados: 0");
            }else{
                log.warn("Extras cargados: "+ extras.length);
            }
			// Comprobando si el usuario tiene un carrito. Si no, crea uno nuevo.
			try{
				cart = (Pedido) entityManager.createQuery(query).getSingleResult();
				Plato plato = entityManager.find(Plato.class, id);
				PlatoPedido platoPed = new PlatoPedido();
				platoPed.setPlato(plato);
				platoPed.setCantidad(amount);
                //Cargamos los extras
                if(!(extras == null)){
                    List<Extra> lExtras = new ArrayList<>();
                    for(int i = 0; i < extras.length;i++){
                        Extra ex = entityManager.find(Extra.class, extras[i]);
                        lExtras.add(ex);
                        cart.setPrecioServicio(cart.getPrecioServicio() + ex.getPrecio());
                    }
                    platoPed.setExtras(lExtras);
                }
                //Actualizamos el resto del pedido
				List<PlatoPedido> contenidoPedido = cart.getContenidoPedido();
				contenidoPedido.add(platoPed);
				cart.setContenidoPedido(contenidoPedido);
				cart.setPrecioServicio(cart.getPrecioServicio() + (platoPed.getCantidad() * plato.getPrecio()));
				entityManager.merge(cart);
				entityManager.flush();
			}catch(NoResultException ex){
				//Crea el carrito nuevo, asignando la informacion basica
				cart = new Pedido();
				cart.setCliente(cliente);
				cart.setDirEntrega(u.getDireccion());
				cart.setEstado(Estado.NO_CONFIRMADO);
				cart.setFechaPedido(LocalDateTime.now());
                cart = getCoords(cart, geoContext); //Obtenemos la latitud y longitud con la API de Google Maps y lo inyectamos en el carrito
				cart.setPrecioEntrega(3.54); //Precio Fijo de envío
				//Busca la informacion relativa al plato que se esta agregando y la agrega al carrito
				Plato plato = entityManager.find(Plato.class, id);
				cart.setRestaurante(plato.getRestaurante());
				//Carga el plato y su informacion en un platoPedido
				PlatoPedido platoPed = new PlatoPedido();
				platoPed.setPlato(plato);
				platoPed.setCantidad(amount);
                //Cargamos los extras
                if(!(extras == null)){
                    List<Extra> lExtras = new ArrayList<>();
                    for(int i = 0; i < extras.length;i++){
                        Extra ext = entityManager.find(Extra.class, extras[i]);
                        lExtras.add(ext);
                        cart.setPrecioServicio(cart.getPrecioServicio() + ext.getPrecio());
                    }
                    platoPed.setExtras(lExtras);
                }
				List<PlatoPedido> contenidoPedido = new ArrayList<>();
				contenidoPedido.add(platoPed);
				cart.setContenidoPedido(contenidoPedido);
				cart.setPrecioServicio(plato.getPrecio() * platoPed.getCantidad());
				//Una vez terminado de cargar los datos se persiste el carrito en la BD
				entityManager.persist(cart);
				entityManager.flush();
			}
            model.addAttribute("message", "Se ha añadido el producto al carrito");
			return platos(model, id);
		 }
}
