# Eat Away

¿No te apetece cocinar?¿Tienes la nevera vacía?¿Te apetece probar sabores nuevos? ¿Quieres impresionar a tu cita pero la cocina no es lo tuyo? EatAway resolverá todos estos problemas y más. Selecciona tus platos preferidos y en un abrir y cerrar de ojos tendrás tu comida en casa o dónde prefieras.

> "Come donde, cuando y como quieras"

## Funcionalidad
Con EatAway podrás elegir entre diferentes restaurantes. Estos estarán clasificados, por ejemplo, por el tipo de comida (hamburguesas, pizzas, etc) ó directamente también aparecen todos los disponibles.

Seleccionando un restaurante podrás ver información sobre éste, como las opiniones, valoración, dirección, horario... 

A su vez, podrás elegir un plato del menú del restaurante para ver su descripción, el precio, elegir algún elemento extra (como una guarnición, el punto de la carne, salsas...) y añadirlo al carrito, seleccionando el número de unidades que desees.


## Usuarios
Disponemos de 4 tipos de usuarios. Dependiendo de cuál se trate, se podrá acceder a diferentes vistas de la aplicación. Más abajo explicamos mejor que puede hacer cada Usuario.

* **Usuario no registrado**: 
    - Podrá visitar los restaurantes y hacer pedidos. 
    - Al no disponer de una cuenta de usuario, deberá rellenar un formulario con sus datos cada vez que haga un pedido.
    - No dispondrá de ofertas ni rebajas.
    - No podrá valorar al repartidor de su pedido ni al restaurante.

 - **Usuario registrario**:
    - Podrá realizar pedidos y hablar por el chat con el repartidor una vez mandado el pedido. 
    - Podrá acceder a una vista del pedido, donde podrá ver su estado, su información, el precio de éste, si queremos añadir propina. También aparecerá un chat con el repartidor por si queremos ponernos en contacto con él. Además, habrá un mapa donde podremos localizar al repartidor.
    - Dispondrá de un perfil de usuario donde estarán almacenados los datos necesarios para poder realizar un pedido sin la necesidad de rellenar un formulario cada vez (nombre, apellidos, dirección, información de contacto). En su perfil, también tendrá un historial de pedidos y una zona ajustes para poder realizar cambios en su cuenta.

* **Restaurante**: 
    - Podrá gestionar todos sus locales, ver la carta que tiene y a su vez modificarla cuando existan cambios en ella. Aceptará los pedidos que un usuario (registrado o no registrao) haga en dicho restaurante.. También tendrá opción de poder ver las opiniones y valoraciones que hay sobre él. 

- **Repartidor**: 
    - El repartidor podrá ver desde su vista los pedidos que que los restaurantes aceptan. Podrá ver el estado del pedido (para saber cuándo están listos para recoger) así como asignarse un pedido para ir a recogerlo al restaurante y posteriormente entregarlo al cliente. 
    -Podrá revisar las valoraciones hechas por los clientes a los que ha repartido algún pedido.
    - Tendrá acceso a un chat para hablar con los clientes junto con un mapa para saber dónde hay que realizar la entrega. 


* **Administrador**: 
    - Tiene total autorización para modificar la base de datos de la aplicación. Tendrá la opción de  bloquear cuentas de usuario por un mal uso, crear nuevos usuarios y ver todos los que existen y a su vez, administrar los restaurantes.

## Vistas

- **[Página de inicio:](http://localhost:8080/)** 
    - En esta página se verán las diferentes comidas y restaurantes inscritos en la aplicación web, y se podrá seleccionar la comida y restaurante que uno desee ordenar. Para los usuarios "avanzados" (Administrador, Dueño del restaurante y Repartidor) la página de inicio corresponderá con una vista especial para éstos, con las opciones que cada uno tenga habilitadas.
* **[Información del Restaurante:](http://localhost:8080/restaurante?id=1)** 
    - Aquí se va a ver la información detallada del restaurante desde la perspectiva de un cliente que quiere ordenarle comida a ellos. Podrá consultar la carta de este restaurante, su valoración media, entre otras cosas.
- **[Información del Plato:](http://localhost:8080/platos?id=1)** 
    - Aquí se podrá ver información detallada de cada plato que haya en la carta de un determinado restaurante, pudiendo ver sus ingredientes, cantidad a comprar, extras posibles, etc.
* **[Lista de Pedidos Disponibles (Para el Repartidor):](http://localhost:8080/user/3/listaPedidos)** 
    - Aquí el repartidor podrá ver la lista de pedidos que han sido pedidos por los clientes en la aplicación, para que puedan "designarse" un pedido para recogerlo en el restaurante que lo está preparando, y entregarselo al cliente una vez esté preparado. Tiene acceso a un pequeño mapa donde puede ver la dirección del restaurante y de entrega, y un chat para hablar con el cliente.
- **[Pedido del cliente](http://localhost:8080/pedidoCliente)**
    - Aquí el cliente podrá ver el estado actual de su pedido más reciente. Dispondrá de un resumen del pedido, un mapa que indica por dónde va el repartidor y un chat con el cual el cliente puede ponerse en contacto con el repartidor.
* **[Perfil del cliente](http://localhost:8080/user/2)**
    - Aquí el cliente podrá acceder su historial de pedidos y a los ajustes de su cuenta, que también podrá modificar.
- **[Perfil del Dueño del Restaurante:](http://localhost:8080/perfilRestaurante?id=4)** 
    - Aquí el dueño de un restaurante podrá visualizar las últimas valoraciones y pedidos que se le han hecho a su restaurante, así como actualizar su carta, información de contacto, y hacer otras gestiones administrativas.
* **[Carrito:](http://localhost:8080/carrito)**
    - En esta vista el cliente podrá ver los productos que ha ido añadiendo a su pedido, y cuando esté preparado para pagar, efectuar la compra y supervisar el estado de su pedido, así como hablar con el repartidor que vaya a entregarle su comida

## Cuentas precreadas y credenciales de acceso

Dentro del import.sql que crea la base de datos inicial de la aplicación contamos con varios usuarios precreados para acceder a los diferentes roles de la aplicación. Estos son los respectivos usuarios con su rol y contraseña:

| Nombre de Usuario | Contraseña | Rol | Vistas Exclusivas del rol |
| --------- | --------- | --------- | --------- |
| a | aa | Administrador | admin.html |
| b | aa | Usuario Normal | user.html, pedidoCliente.html |
| repartidor | aa | Usuario Repartidor | repartidor.html, listaPedidos.html |
| restaurante | aa | Propietario de Restaurantes | addExtra.html, addPlato.html, addRestaurante.html, delExtra.html, editExtra.html, editPlato.html, editRestaurante.html, perfilRestaurante.html |

## Vistas en detalle

Si entramos con el rol de dueño de los restaurantes encontraremos:

La pagina principal *perfilRestaurante.html* de este rol una vez iniciado sesión es una tabla con los diferentes restaurantes, su descripción, dirección, horario, valoración media y las acciones disponibles que será "Administrar".
Si pulsamos dicho botón, nos manda a la vista *adminRestaurante.html* de ese restaurante con toda su información y donde encontraremos una configuración para "Añadir Plato" (*addPlato.html*), "Editar restaurante"(*editRestaurante.html*) y "Eliminar Restaurante".  Para los dos primeros nos redirige a un formulario con varios campos a rellenar para hacer la funcionalidad. 
Dentro de dicha vista, podemos encontrar los platos que dispone dicho Restaurante con su propia información y con varias acciones disponibles para los extras (Editar *editExtra.html*, Eliminar los ya existentes) ó para el propio plato (Añadir extras *addExtra.html*, editar el plato *editPlato.html* o eliminarlo).
Además, al final de la lista de platos encontramos otra tabla con la información sobre los pedidos activos de dicho restaurante. Y con ello puede cambiar el estado del pedido o cancelarlo.

En la pagina principal también dispone de un botón para añadir un restaurante nuevo que si pulsamos, nos manda a un formulario *addRestaurante.html* donde se deben rellenar diferentes campos para poder realizar la función que el propio botón con su nombre indica.En dicho formulario una vez rellenado los diferentes datos y si pulsamos registrar, añade el nuevo Restaurante. 



Si entramos con el rol de admin encontraremos:

Su página principal *admin.html* podemos realizar diferentes acciones como es el dar de alta a diferentes usuarios, encontrando respectivos botones para ello y si pulsamos, nos manda a la vista de *registroAvanzado.html*. 
Además, encontramos una tabla con los diferentes usuarios que hay en la aplicación y toda su información. Con ellos el administrador puede bloquearlos y borrar su foto.
También encontramos otra tabla con toda la información de los restaurantes y el admin podrá también administrarlos, que si pulsamos éste botón, nos manda a la vista *adminRestaurante.html* y podrá realizar las mismas funciones que el rol de restaurante explicadas ya anteriormente.



Si entramos sin registrarnos, accedemos a la página principal donde podemos filtrar los restaurantes en categorías en función de lo que estemos buscando. Si buscamos Hamburguesas y pulsamos éste boton, nos aparecerán los restaurantes que contengan Hamburguesas. Y así con el resto de categorías que aparecen.
Si pulsamos uno de los restaurantes, nos redirige a su página *restaurante.html*, donde podremos ver una pequeña descripción de éste,y sus platos principales.  Si pulsamos en uno de ellos nos redirige a *platos.html* donde vemos información e ingredientes del plato, añadir extras, si queremos mas de un plato o si queremos añadir al pedido.
Si añadimos al pedido al no estar registrados, nos manda al *login.html* para que lo hagamos. 


Entrariamos ahora al usuario registrado, si añadimos el pedido nos sale un mensaje de éxito que se ha añadido. 
Para poder pagar tenemos que pulsar al emoticono del carrito que hay arriba a la derecha, que nos manda a la vista de *carrito.html*.
En dicha vista rellenaremos los campos que nosotros creamos, podremos nuestros articulos a pedir y un botón de Hacer Pedido. Si pulsamos nos lleva a la página principal de nuevo de la aplicación saliendo un mensaje de éxito sobre el pedido.


Si entramos como repartidor encontramos:

La vista *repartidor.html* donde tenemos un botón configuración por si queremos cambiar algún campo de nuestra informacion de usuario y otro de A TRABAJAR, que si pulsamos nos manda a los pedidos disponibles *listaPedidos.html* . Podremos pulsar un botón de Info donde si pulsamos nos accede al Google Maps y nos da la información del restaurante y del cliente. Si le damos a recoger, nos manda a *chatCliente.htm* para poder hablar con él y marcar como entregado el pedido.




La vista de *registro.html* es para poder registrarnos como usuarios nuevos en la aplicación rellenando los diferentes campos que aparecen.

En la vista *user.html* podemos encontrar la información relacionada al usuario donde podremos valorar pedidos anteriores. También tenemos una configuración por si queremos cambiar algo de nuestro perfil.  Además encontramos un botón de Hablar con el Repartidor que nos manda al chat con el repartidor *listaChats.html* y poder ver los pedidos en reparto y en espera, junto con su estado y para poder hablar. Si le damos a hablar nos manda a *chatRepartidor.html* para poder comunicarnos con él en caso de que un usuario Cliente tenga un pedido pendiente.

La vista *finRegistro.html* nos sale cuando nos hemos registrado correctamente como nuevo usuario en la aplicación.

## Mejoras Post-Examen

### Participantes

Todos los miembros del grupo hemos participado en las mejoras propuestas

### Mejoras

* Arreglos en el addToCart
* Implementación de WebSockets
* Mejora del seguimiento de pedidos
* Mejora de la usabilidad y botón de repetir pedido: Para esta mejora en el front-end de la página hemos añadido en el perfil del cliente un botón nuevo para cada pedido que haya hecho anteriormente en el que se puede repetir el pedido, de modo que lo que hará será limpiar su carrito actual (si tenía uno), buscar el pedido hecho anteriormente, y hacer una copia del contenido de ese pedido, asegurandose de tener los precios correctos, y redirigiendole a la ventana donde podrá confirmar su información y procesar el pedido