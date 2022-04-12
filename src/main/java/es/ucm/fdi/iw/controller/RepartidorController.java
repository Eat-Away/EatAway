package es.ucm.fdi.iw.controller;


import java.util.List;

import javax.persistence.EntityManager;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.dto.PedidoDto;

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

			return "listaPedidos";
		}
	}
    

}
