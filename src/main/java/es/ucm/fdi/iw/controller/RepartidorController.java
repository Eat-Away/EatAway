package es.ucm.fdi.iw.controller;


import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.dto.PedidoDto;
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

	//private static final Logger log = LogManager.getLogger(RepartidorController.class);

    @GetMapping("/{id}")
    public String index(Model model) {
        return "repartidor";
    }

    @GetMapping("/{id}/listaPedidos")
    public String listaPedidos(Model model, @PathVariable long id) {
		String query = "SELECT COUNT(X.pedido) FROM User X WHERE X.id =" + id;
		Long n = (Long)entityManager.createQuery(query).getSingleResult();
		if(n == 1){ //Si el repartidor ya tiene asignado un pedido
			return "repartidor";
		}
		else{ //Si no tiene asignado ningún pedido
			query = "SELECT new es.ucm.fdi.iw.dto.PedidoDto(Y.lat,Y.lng,Y.id,Y.dirEntrega,X.firstName,X.lastName,Z.nombre,Z.direccion)"
			+"FROM User X JOIN Pedido Y ON X.id = Y.cliente JOIN Restaurante Z ON Y.restaurante = Z.id " +
			"WHERE Y.repartidor = null";
			List<PedidoDto> pedidos = (List<PedidoDto>)entityManager.createQuery(query, PedidoDto.class).getResultList();
			model.addAttribute("pedidos", pedidos);
			model.addAttribute("idRepartidor",id);
			return "listaPedidos";
		}
	}
	@Transactional
    @PostMapping("/{id}/getPedido/{idPedido}")
    public String getPedido(Model model, @PathVariable long id,@PathVariable long idPedido) {
		Pedido pedido = entityManager.find(Pedido.class, idPedido);
		
		if(pedido.getRepartidor() != null){ //ya cogido 
			return listaPedidos(model, id);
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


			return "repartidor";
		}
	}

}
