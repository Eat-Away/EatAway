package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Cliente;
import es.ucm.fdi.iw.model.Extra;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.PlatoPedido;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Pedido.Estado;
import es.ucm.fdi.iw.model.User.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  User management.
 *
 *  Access to this end-point is authenticated.
 */
@Controller()
@RequestMapping("user")
public class UserController {
	/**
	* This class is a RuntimeException that is thrown when a user tries to access a resource that they
	* don't have permission to access.
	*/
   @ResponseStatus(
	   value=HttpStatus.FORBIDDEN, 
	   reason="Alto ahí, no tienes permiso para hacer esto")  // 403
   public static class PermisoDenegadoException extends RuntimeException {}

	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Exception to use when denying access to unauthorized users.
	 * 
	 * In general, admins are always authorized, but users cannot modify
	 * each other's profiles.
	 */
	@ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="No eres administrador, y éste no es tu perfil")  // 403
	public static class NoEsTuPerfilException extends RuntimeException {}

	/**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 * for example, a possible encoding of "test" is 
	 * {bcrypt}$2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	/**
	 * Generates random tokens. From https://stackoverflow.com/a/44227131/15472
	 * @param byteLength
	 * @return
	 */
	public static String generateRandomBase64Token(int byteLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] token = new byte[byteLength];
		secureRandom.nextBytes(token);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(token); //base64 encoding
	}

	/**
	 * Landing page for a user profile
	 */
	@GetMapping("{id}")
	public String index(@PathVariable long id, Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		return "user";
	}
	/**
	 * Devuelve una lista de chats que el usuario tiene con otros usuarios
	 * 
	 * @param id La identificación del usuario del que desea ver los chats.
	 * @param model Este es el objeto que se pasa a la vista.
	 * @param session El objeto de sesión se utiliza para almacenar la información del usuario.
	 * @return Una lista de conversaciones
	 */
	@GetMapping("/{id}/chats")
	public String chats(@PathVariable long id,Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		if(target.getPedidos().size() != 0){
			String query = "SELECT X FROM Pedido X WHERE X.cliente =" +id +" AND X.estado = 4";
			List<Pedido> pedidos = (List<Pedido>)entityManager.createQuery(query, Pedido.class).getResultList();
			query = "SELECT X FROM Pedido X WHERE X.estado < 4 AND X.estado > 0 AND X.cliente ="+id;
			List<Pedido> pedidosEspera = (List<Pedido>)entityManager.createQuery(query, Pedido.class).getResultList();
			//Obtiene la lista de pedidos que esten a punto de caducar
			List<String> caducidad = new ArrayList<>();
			List<String> fechaPedido = new ArrayList<>();
			Iterator<Pedido> pedIterator = pedidos.iterator();
			while(pedIterator.hasNext()){
				Pedido p = pedIterator.next();
				caducidad.add(p.getFechaPedido().plusMinutes(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString());
				fechaPedido.add(p.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString());
			}
			//Obtiene la lista de pedidos que esten a punto de caducar en espera
			List<String> caducidadEspera = new ArrayList<>();
			List<String> fechaPedidoEspera = new ArrayList<>();
			Iterator<Pedido> pedIteratorEsp = pedidosEspera.iterator();
			while(pedIteratorEsp.hasNext()){
				Pedido p = pedIteratorEsp.next();
				caducidadEspera.add(p.getFechaPedido().plusMinutes(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString());
				fechaPedidoEspera.add(p.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString());
			}
			model.addAttribute("caducidad", caducidad);
			model.addAttribute("fechaPedido", fechaPedido);
			model.addAttribute("caducidadEspera", caducidadEspera);
			model.addAttribute("fechaPedidoEspera", fechaPedidoEspera);
			model.addAttribute("pedidosEspera", pedidosEspera);
			model.addAttribute("pedidos", pedidos);
		}
		return "listaChats";
	}
	/**
	 * Devuelve la página chatCliente.html, que es la página de chat para el cliente
	 * 
	 * @param id La identificación del usuario con el que desea chatear.
	 * @param model Este es el modelo que se pasará a la vista.
	 * @param session El objeto de sesión se utiliza para almacenar los datos de la sesión del usuario.
	 * @param idPedido La identificación del pedido sobre el que el cliente quiere chatear.
	 * @return Se está devolviendo la página chatCliente.html.
	 */
	@GetMapping("/{id}/chat/{idPedido}")
	public String chat(@PathVariable long id,Model model, HttpSession session,@PathVariable long idPedido){
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		Pedido pedido = entityManager.find(Pedido.class, idPedido);
		long idRepartidor = pedido.getRepartidor().getId();
		model.addAttribute("idRepartidor", idRepartidor);
		model.addAttribute("idPedido", idPedido);
			return "chatCliente";
	}

	/**
	 * Actualiza la valoración de un pedido hecho por un usuario una vez este ya ha finalizado
	 * 
	 * @param model El objeto de modelo que se usará para representar la vista.
	 * @param session el objeto de sesión
	 * @param idPedido el id de la orden a calificar
	 * @param val el valor de la calificación
	 * @return La vista de perfil del cliente
	 */
	@Transactional
	@PostMapping("/{id}/valorar")
	public String valorarPedido(Model model, HttpSession session, @RequestParam("idPedido") long idPedido, @RequestParam("valoracion") double val ){
		User u = (User) session.getAttribute("u");
		Cliente cli = entityManager.find(Cliente.class, u.getId());
		Pedido p = entityManager.find(Pedido.class, idPedido);
		if(cli.getId() != p.getCliente().getId()){
			throw new PermisoDenegadoException();
		}
        p.setCliente(p.getCliente());
        p.setContenidoPedido(p.getContenidoPedido());
        p.setRestaurante(p.getRestaurante());
		p.setValoracion(val);
        entityManager.merge(p);
        entityManager.flush();
		return index(cli.getId(), model, session);
	}

	/**
	 * Alter or create a user
	 */
	@PostMapping("/{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable long id, 
			@ModelAttribute User edited, 
			@RequestParam(required=false) String pass2,
			Model model, HttpSession session) throws IOException {

		User requester = (User)session.getAttribute("u");
		User target = null;
		if (id == -1 && requester.hasRole(Role.ADMIN)) {
			// create new user with random password
			target = new User();
			target.setPassword(encodePassword(generateRandomBase64Token(12)));
			target.setEnabled(true);
			entityManager.persist(target);
			entityManager.flush(); // forces DB to add user & assign valid id
			id = target.getId();   // retrieve assigned id from DB
		}
		
		// retrieve requested user
		target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			throw new NoEsTuPerfilException();
		}
		
		if (edited.getPassword() != null) {
			if ( ! edited.getPassword().equals(pass2)) {
				throw new NoEsTuPerfilException();
			} else {
				// save encoded version of password
				target.setPassword(encodePassword(edited.getPassword()));
			}
		}		
		target.setUsername(edited.getUsername());
		target.setFirstName(edited.getFirstName());
		target.setLastName(edited.getLastName());

		// update user session so that changes are persisted in the session, too
		if (requester.getId() == target.getId()) {
			session.setAttribute("u", target);
		}

		return "user";
	}	

	/**
	 * Returns the default profile pic
	 * 
	 * @return
	 */
	private static InputStream defaultPic() {
		return new BufferedInputStream(Objects.requireNonNull(
			UserController.class.getClassLoader().getResourceAsStream(
				"static/img/default-pic.jpg")));
	}

	/**
	 * Downloads a profile pic for a user id
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@GetMapping("{id}/conf")
	public StreamingResponseBody getConf(@PathVariable long id) throws IOException {
		File f = localData.getFile("user", ""+id+".jpg");
		InputStream in = new BufferedInputStream(f.exists() ?
			new FileInputStream(f) : UserController.defaultPic());
		return os -> FileCopyUtils.copy(in, os);
	}

	/**
	 * Actualiza la información de perfil y la foto de perfil del usuario.
	 * 
	 * @param user Este es el objeto de usuario que se actualizará.
	 * @param photo El archivo que sube el usuario.
	 * @param id El id del usuario a actualizar.
	 * @param response El objeto de respuesta que se devolverá al cliente.
	 * @param session El objeto de sesión se utiliza para almacenar los datos de la sesión del usuario.
	 * @param model El modelo es un mapa que se pasa a la vista. Contiene los datos que se mostrarán en la
	 * vista.
	 * @return El perfil del usuario
	 */
	@PostMapping("/{id}/conf")
	@Transactional
	public String setConf(@ModelAttribute User user, @RequestParam MultipartFile photo, @PathVariable long id, 
	HttpServletResponse response, HttpSession session, Model model) throws IOException{

		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			throw new NoEsTuPerfilException();
		}

		target.setFirstName(user.getFirstName());
		target.setLastName(user.getLastName());
		target.setDireccion(user.getDireccion());

		entityManager.merge(target);
		entityManager.flush();

		//Profile pic update
		if(!photo.isEmpty()){
			log.info("Updating photo for user {}", id);
			File f = localData.getFile("user/", ""+id+".jpg");

			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
				log.info("Uploaded new configuration for {} into {}!", id, f.getAbsolutePath());
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				log.warn("Error uploading " + id + " ", e);
			}
		}
		//return "{\"status\":\"configuration uploaded correctly\"}";
		return index(id, model, session);
	}

	
	/**
	 * Obtiene el carrito del usuario y se lo muestra al usuario.
	 * 
	 * @param model El modelo es un mapa que se utiliza para almacenar los datos que se mostrarán en la
	 * página de visualización.
	 * @param id la identificación del usuario
	 * @return La vista de carrito del usuario
	 */
	@GetMapping("{id}/carrito")
	public String carrito(Model model, @PathVariable long id) {
		User user = entityManager.find(User.class, id);
		model.addAttribute("user", user);

		List<Pedido> pedidos = user.getPedidos();
		Pedido pedido = null;
		for(int i = 0; i < pedidos.size(); i++){
			if(pedidos.get(i).getEstado() == Estado.NO_CONFIRMADO){
				pedido = pedidos.get(i);
				break;
			}
		}
		model.addAttribute("pedido", pedido);

		if(pedido != null) { 
			List<PlatoPedido> platos = pedido.getContenidoPedido(); 
			Double precio = 0.0;
			for(int i = 0; i < platos.size(); i++){
				Double precio_aux = platos.get(i).getPlato().getPrecio();
				List<Extra> extras = platos.get(i).getExtras();
				for(int j = 0; j < extras.size(); j++){
					precio += extras.get(j).getPrecio();
				}
				precio_aux *= platos.get(i).getCantidad();
				precio += precio_aux;
			}
			model.addAttribute("precio", precio);

			Double total = pedido.getPrecioEntrega() + pedido.getPrecioServicio();
		
			model.addAttribute("total", total);
		}

		return "carrito";
	}

    /**
     * Toma un objeto Pedido, obtiene la dirección de él y luego usa la API de Google Maps para obtener la
     * latitud y longitud de esa dirección.
     * 
     * @param cart El objeto del carrito que vamos a actualizar con las coordenadas.
     * @return Un objeto de Pedido con las coordenadas de la dirección.
     */
    private Pedido getCoords(Pedido cart){
        GeoApiContext geoContext = new GeoApiContext.Builder()
		.apiKey("AIzaSyA6OCoRWbqZwfRgHV6z9b4C36fB252sEvI")
		.build();
        try{
            log.debug("Buscando coordenadas de la direccion: " + cart.getDirEntrega());
            GeocodingResult[] resultado = GeocodingApi.geocode(geoContext, cart.getDirEntrega()).await();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            log.debug(gson.toJson(resultado));
            double lat = resultado[0].geometry.location.lat;
            double lng = resultado[0].geometry.location.lng;
            cart.setLat(lat);
            cart.setLng(lng);
            log.warn(gson.toJson(resultado[0].geometry.location));
        }catch(Exception ex){
            log.error("Error obteniendo las coordenadas de la dirección especificada", ex);
            cart.setLat(40.4527696);
            cart.setLng(-3.7357);
        }
        return cart;
    }


	/**
     * Crea un nuevo pedido con los mismos datos que el pedido que está repitiendo
	 * 
     * @param model El objeto de modelo que se usará para representar la vista.
     * @param session El objeto de sesión se utiliza para almacenar la información del usuario.
	 * @param id El id del usuario
     * @param idPed El id del pedido a procesar.
     * @return La vista de carrito
     */
	@Transactional
	@PostMapping("/{id}/repitePedido")
	public String repitePedido(Model model, @PathVariable long id, @RequestParam("idPed") long idPed){
		Pedido ped = entityManager.find(Pedido.class, idPed);
		Cliente cliente = entityManager.find(Cliente.class, id);
		if(cliente.getId() != ped.getCliente().getId()){
			throw new PermisoDenegadoException();
		}
		
		//Si tenia un carrito anteriormente lo elimina de la BD
		String query = "SELECT X FROM Pedido X WHERE X.cliente="+cliente.getId()+" AND X.estado=0";
		try{
			Pedido previo = entityManager.createQuery(query, Pedido.class).getSingleResult();
			entityManager.remove(previo);
			entityManager.flush();
		}catch(NoResultException ex){
			log.info("No habia un carrito anteriormente para el usuario, no se ha purgado nada");
		}finally{
			//Una vez vaciado su carrito se llena con los productos del pedido anterior
			Pedido nuevoPed = new Pedido();
			nuevoPed.setCliente(ped.getCliente());
			nuevoPed.setDirEntrega(ped.getDirEntrega());
			nuevoPed.setEstado(Estado.NO_CONFIRMADO);
			nuevoPed.setFechaPedido(LocalDateTime.now());
			nuevoPed = getCoords(nuevoPed); //Obtenemos la latitud y longitud con la API de Google Maps y lo inyectamos en el carrito
			nuevoPed.setPrecioEntrega(3.54);
			nuevoPed.setRestaurante(ped.getRestaurante());
			nuevoPed.setValoracion(0.0);
	
			List<PlatoPedido> platosPedido = new ArrayList<PlatoPedido>();
			PlatoPedido platoPedido;
			for(int i = 0; i < ped.getContenidoPedido().size(); i++){
				PlatoPedido nuevoPlatoPedido = new PlatoPedido();
				platoPedido = ped.getContenidoPedido().get(i);
	
				nuevoPlatoPedido.setCantidad(platoPedido.getCantidad());
				nuevoPlatoPedido.setPlato(platoPedido.getPlato());
				nuevoPlatoPedido.setPedido(nuevoPed);
				nuevoPed.setPrecioServicio(nuevoPed.getPrecioServicio() + ( platoPedido.getPlato().getPrecio() * platoPedido.getCantidad() ));
	
				List<Extra> nuevoExtras = new ArrayList<>();
				Extra nuevoExtra;
				for(int j = 0; j < platoPedido.getExtras().size(); j++){
					nuevoExtra = entityManager.find(Extra.class, platoPedido.getExtras().get(j).getId());
					nuevoExtras.add(nuevoExtra);
					nuevoPed.setPrecioServicio(nuevoPed.getPrecioServicio() + nuevoExtra.getPrecio());
				}
				nuevoPlatoPedido.setExtras(nuevoExtras);
				
				platosPedido.add(nuevoPlatoPedido);
			}
			
			nuevoPed.setContenidoPedido(platosPedido);
			
			//Una vez terminado de cargar los datos se persiste el carrito en la BD
			entityManager.persist(nuevoPed);
			entityManager.flush();	
		}
		return carrito(model, id);
	}


	/**
	 * Devuelve la vista con la información del pedido del usuario
	 * 
	 * @param model El modelo es un mapa que se utiliza para almacenar los datos que se mostrarán en la
	 * página de visualización.
	 * @return Vista con la información del pedido
	 */
	@GetMapping("/{id}/pedidoCliente")
	public String pedidoCliente(Model model) {
		return "pedidoCliente";
	}

	/**
	 * Returns JSON with all received messages
	 */
	@GetMapping(path = "received", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Message.Transfer> retrieveMessages(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		User u = entityManager.find(User.class, userId);
		log.info("Generating message list for user {} ({} messages)", 
				u.getUsername(), u.getReceived().size());
		return  u.getReceived().stream().map(Transferable::toTransfer).collect(Collectors.toList());
	}	
	
	/**
	 * Returns JSON with count of unread messages 
	 */
	@GetMapping(path = "unread", produces = "application/json")
	@ResponseBody
	public String checkUnread(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		long unread = entityManager.createNamedQuery("Message.countUnread", Long.class)
			.setParameter("userId", userId)
			.getSingleResult();
		session.setAttribute("unread", unread);
		return "{\"unread\": " + unread + "}";
	}
	
	/**
	 * Posts a message to a user.
	 * @param id of target user (source user is from ID)
	 * @param o JSON-ized message, similar to {"message": "text goes here"}
	 * @throws JsonProcessingException
	 */
	@PostMapping("/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("message").asText();
		User u = entityManager.find(User.class, id);
		User sender = entityManager.find(
				User.class, ((User)session.getAttribute("u")).getId());
		model.addAttribute("user", u);
		
		// construye mensaje, lo guarda en BD
		Message m = new Message();
		m.setRecipient(u);
		m.setSender(sender);
		m.setDateSent(LocalDateTime.now());
		m.setText(text);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());
		String json = mapper.writeValueAsString(rootNode);
		
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	
}
