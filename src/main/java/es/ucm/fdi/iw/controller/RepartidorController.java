package es.ucm.fdi.iw.controller;


import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import es.ucm.fdi.iw.model.Cliente;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Pedido;
import es.ucm.fdi.iw.model.Repartidor;


/**
 *  Gestión de usuarios de tipo repartidor.
 *
 *  Access to this end-point is authenticated.
 */
@Controller()
@RequestMapping("repartidor")
public class RepartidorController {
    @Autowired
	private EntityManager entityManager;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;


	//private static final Logger log = LogManager.getLogger(RepartidorController.class);

    @GetMapping("/{id}")
    public String index(Model model,HttpSession session, @PathVariable long id) {
        return "repartidor";
    }
	
    @GetMapping("/{id}/listaPedidos")
    public String listaPedidos(Model model,HttpSession session, @PathVariable long id) {
		Repartidor u =(Repartidor)session.getAttribute("u");
		u = entityManager.find(Repartidor.class,u.getId());

		if(u.getPedido() != null){ //Si el repartidor ya tiene asignado un pedido
			return "chatRepartidor";
		}
		else{ //Si no tiene asignado ningún pedido
			String query = "SELECT X FROM Pedido X WHERE X.repartidor = null";
			List<Pedido> pedidos = (List<Pedido>)entityManager.createQuery(query, Pedido.class).getResultList();

			model.addAttribute("pedidos", pedidos);
			model.addAttribute("idRepartidor",id);
			return "listaPedidos";
		}
	}
	@Transactional
    @PostMapping("/{id}/getPedido/{idPedido}")
    public String getPedido(Model model, @PathVariable long id,@PathVariable long idPedido,HttpSession session) {
		Pedido pedido = entityManager.find(Pedido.class, idPedido);
		
		if(pedido.getRepartidor() != null){ //ya cogido 
			return listaPedidos(model,session,id);
		}
		else{
			//query = "UPDATE Pedido Y SET Y.repartidor=" + id + "WHERE Y.id=" + idPedido;
			
			Pedido x = entityManager.find(Pedido.class, idPedido);
			Repartidor y = entityManager.find(Repartidor.class,id);
			x.setRepartidor(y);
			entityManager.persist(x);
			entityManager.flush();
			//query = "UPDATE User X SET X.pedido=" + idPedido + "WHERE X.id =" + id;
			Pedido x2 = entityManager.find(Pedido.class, idPedido);
			y.setPedido(x2);
			entityManager.persist(y);
			entityManager.flush();


			return "chatRepartidor";
		}
	}

	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public Message sendMessage( Message message,HttpSession session){

		return message;
	}
	
}
