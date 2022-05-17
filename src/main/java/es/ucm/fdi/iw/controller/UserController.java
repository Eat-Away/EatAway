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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
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
	@GetMapping("/{id}/chats")
	public String chats(@PathVariable long id,Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		if(target.getPedidos().size() != 0){
			String query = "SELECT X FROM Pedido X WHERE X.cliente =" +id +" AND X.estado = 4";
			List<Pedido> pedidos = (List<Pedido>)entityManager.createQuery(query, Pedido.class).getResultList();
			query = "SELECT X FROM Pedido X WHERE X.estado < 4 AND X.estado > 0";
			List<Pedido> pedidosEspera = (List<Pedido>)entityManager.createQuery(query, Pedido.class).getResultList();
			model.addAttribute("pedidosEspera", pedidosEspera);
			model.addAttribute("pedidos", pedidos);
		}
		return "listaChats";
	}
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
		File f = localData.getFile("user", "user"+id+".jpg");
		InputStream in = new BufferedInputStream(f.exists() ?
			new FileInputStream(f) : UserController.defaultPic());
		return os -> FileCopyUtils.copy(in, os);
	}

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
			File f = localData.getFile("user/", "user"+id+".jpg");

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
