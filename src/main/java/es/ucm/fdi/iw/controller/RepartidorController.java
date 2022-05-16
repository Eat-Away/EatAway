package es.ucm.fdi.iw.controller;


import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.ucm.fdi.iw.model.Cliente;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.Repartidor;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Pedido.Estado;


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
	@Autowired
	private SimpMessagingTemplate messagingTemplate;


	//private static final Logger log = LogManager.getLogger(RepartidorController.class);

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
		Repartidor target = (Repartidor)entityManager.find(User.class, id);
		model.addAttribute("user", target);
        return "repartidor";
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

	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public Message sendMessage( Message message,HttpSession session){

		return message;
	}
	
}
