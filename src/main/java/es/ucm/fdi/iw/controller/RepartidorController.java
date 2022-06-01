package es.ucm.fdi.iw.controller;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
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
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.Repartidor;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Pedido.Estado;
import es.ucm.fdi.iw.model.User.Role;


/**
 *  Gestión de usuarios de tipo repartidor.
 *
 *  Access to this end-point is authenticated.
 */
@Controller()
@RequestMapping("repartidor")
/**
 * > This class is a controller that handles requests for the Repartidor entity
 */
public class RepartidorController {
    @Autowired
	private EntityManager entityManager;

	//@Autowired
	//private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private LocalData localData;

	private static final Logger log = LogManager.getLogger(RepartidorController.class);

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
	 * It returns the repartidor page.
	 * 
	 * @param model This is the model object that will be used to pass data to the view.
	 * @param session The HttpSession object.
	 * @param id The id of the user
	 * @return Redirects to the repartidor page.
	 */
    @GetMapping("/{id}")
    public String index(Model model,HttpSession session, @PathVariable long id) {
		Repartidor target = entityManager.find(Repartidor.class, id);
		model.addAttribute("repartidor", target);
        return "repartidor";
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
			new FileInputStream(f) : RepartidorController.defaultPic());
		return os -> FileCopyUtils.copy(in, os);
	}

	/**
	 * Actualiza la información de perfil y la foto de perfil del usuario.
	 * 
	 * @param repartidor El objeto que se actualizará.
	 * @param photo El archivo que sube el usuario.
	 * @param id la identificación del usuario que se actualizará
	 * @param response El objeto de respuesta, que se utiliza para establecer el código de estado de la
	 * respuesta.
	 * @param session el objeto de sesión, que se utiliza para obtener el objeto de usuario.
	 * @param model El modelo es un mapa que se pasa a la vista. Contiene los datos que se mostrarán en la
	 * vista.
	 * @return Una cadena con el nombre de la vista a representar.
	 */
	@PostMapping("/{id}/conf")
	@Transactional
	public String setConf(@ModelAttribute Repartidor repartidor, @RequestParam MultipartFile photo, @PathVariable long id, 
	HttpServletResponse response, HttpSession session, Model model) throws IOException{

		Repartidor target = entityManager.find(Repartidor.class, id);
		model.addAttribute("repartidor", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			throw new NoEsTuPerfilException();
		}

		target.setFirstName(repartidor.getFirstName());
		target.setLastName(repartidor.getLastName());

		entityManager.merge(target);
		entityManager.flush();

		//Profile pic update
		if(!photo.isEmpty()){
			log.info("Updating photo for repartidor {}", id);
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
		return index(model, session, id);
	}
	
	/**
	 * It returns a list of all the orders that don't have a delivery man assigned to them
	 * 
	 * @param model The model is a Map that is used to store the data that will be displayed on the view
	 * page.
	 * @param session The session object.
	 * @param id The id of the repartidor
	 * @return Returns the page with a list of Pedidos that are not assigned to any Repartidor.
	 */
    @GetMapping("/{id}/listaPedidos")
    public String listaPedidos(Model model,HttpSession session, @PathVariable long id) {
		Repartidor u =(Repartidor)session.getAttribute("u");
		u = entityManager.find(Repartidor.class,u.getId());
		long idRepartidor = u.getId();
		if(u.getPedido() != null){ //Si el repartidor ya tiene asignado un pedido
			model.addAttribute("idRepartidor",u);
			return "chatRepartidor";
		}
		else{ //Si no tiene asignado ningún pedido y los pedidos estan listos para recoger
			String query = "SELECT X FROM Pedido X WHERE X.repartidor = null AND X.estado = 3";
			List<Pedido> pedidos = (List<Pedido>)entityManager.createQuery(query, Pedido.class).getResultList();
			model.addAttribute("pedidos", pedidos);
			model.addAttribute("idRepartidor",idRepartidor);
			//Obtiene la lista de pedidos que esten a punto de caducar
			List<String> caducidad = new ArrayList<>();
			List<String> fechaPedido = new ArrayList<>();
			Iterator<Pedido> pedIterator = pedidos.iterator();
			while(pedIterator.hasNext()){
				Pedido p = pedIterator.next();
				caducidad.add(p.getFechaPedido().plusMinutes(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString());
				fechaPedido.add(p.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")).toString());
			}
			model.addAttribute("caducidad", caducidad);
			model.addAttribute("fechaPedido", fechaPedido);
			return "listaPedidos";
		}
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
	 * It takes the id of the repartidor and the id of the pedido, and if the pedido is not taken, it takes
	 * it and redirects to the chatRepartidor page
	 * 
	 * @param model The model object that is used to store data that will be used by the view.
	 * @param id the id of the logged in user
	 * @param idPedido the id of the order that the user wants to take
	 * @param session the session object
	 * @return Redirects to a chat with the client who ordered the order taken by the delivery man.
	 */
	@Transactional
    @PostMapping("/{id}/getPedido/{idPedido}")
    public String getPedido(Model model, @PathVariable long id,@PathVariable long idPedido,HttpSession session) {
		Pedido pedido = entityManager.find(Pedido.class, idPedido);
		
		if(pedido.getRepartidor() == null){ //no cogido 

			//query = "UPDATE Pedido Y SET Y.repartidor=" + id + "WHERE Y.id=" + idPedido;
			//y modificar el estado a en REPARTO
			Pedido x = entityManager.find(Pedido.class, idPedido);
			Repartidor y = entityManager.find(Repartidor.class,id);
			x.setRepartidor(y);
			x.setEstado(Estado.REPARTO);
			entityManager.persist(x);
			entityManager.flush();
			//query = "UPDATE User X SET X.pedido=" + idPedido + "WHERE X.id =" + id;
			Pedido x2 = entityManager.find(Pedido.class, idPedido);
			y.setPedido(x2);
			entityManager.persist(y);
			entityManager.flush();
		}
		return listaPedidos(model,session,id);
	}

	/**
	 * Marca el pedido asignado al repartidor como entregado
	 * 
	 * @param model El objeto modelo que se usará para pasar datos a la vista.
	 * @param session El objeto de sesión.
	 * @param idPed El id de la orden.
	 * @param estado El estado del pedido.
	 * @return Una cuerda.
	 */
	@Transactional
	@PostMapping("/{id}/deliver")
	public String entregaPedido(Model model, HttpSession session, @RequestParam("idPedido") long idPed, @RequestParam("sigEstado") Estado estado){
		User u = (User) session.getAttribute("u");
		Repartidor rep = entityManager.find(Repartidor.class, u.getId());
		Pedido p = entityManager.find(Pedido.class, idPed);
		// Checking if the user is the owner of the restaurant.
		if(rep.getId() != p.getRepartidor().getId()){
			throw new PermisoDenegadoException();
		}
		p.setRepartidor(null);
        p.setCliente(p.getCliente());
        p.setContenidoPedido(p.getContenidoPedido());
        p.setRestaurante(p.getRestaurante());
        p.setEstado(estado);
        entityManager.merge(p);
		rep.setPedido(null);
        entityManager.flush();
		return index(model, session, rep.getId());
	}

	/**
	 * La función toma un objeto de mensaje como parámetro y devuelve un objeto de mensaje
	 * 
	 * @param message El objeto de mensaje que se envía desde el cliente.
	 * @param session La HttpSession asociada con la solicitud.
	 * @return El objeto de mensaje está siendo devuelto.
	 */
	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public Message sendMessage( Message message,HttpSession session){

		return message;
	}
	
}
