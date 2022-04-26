# Eat Away

¿No te apetece cocinar?¿Tienes la nevera vacía?¿Te apetece probar sabores nuevos? ¿Quieres impresionar a tu cita pero la cocina no es lo tuyo? EatAway resolverá todos estos problemas y más. Selecciona tus platos preferidos y en un abrir y cerrar de ojos tendrás tu comida en casa o dónde prefieras.

> "Come donde, cuando y como quieras"

## Funcionalidad
Con EatAway podrás elegir entre diferentes restaurantes. Estos estarán clasificados, por ejemplo, por el tipo de comida (hamburguesas, pizzas, etc) o por el rango de precios para hacer más sencilla la búsqueda.

Seleccionando un restaurante podrás ver información sobre éste, como las opiniones, valoración, dirección, horario... 

A su vez, podrás elegir un plato del menú del restaurante para ver su descripción, el precio, elegir algún elemento extra (como una guarnición, el punto de la carne, salsas...) y añadirlo al carrito, seleccionando el número de unidades que desees.


## Usuarios
Disponemos de 4 tipos de usuarios. Dependiendo de cuál se trate, se podrá acceder a diferentes vistas de la aplicación:

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
    - Tiene total autorización para modificar la base de datos de la aplicación. Tendrá la opción de eliminar o suspender cuentas de usuario por un mal uso, así como los comentarios que no cumplas con las normas. 

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

En la vista *user.html* podemos encontrar el uso de la base de datos para poder obtener el nombre y la dirección del usuario:             
```
<p>Nombre: <span th:text="${user.username}">Paco</span></p>
    <p>Dirección: <span th:text="${user.direccion}">C/ bbb, 9</span></p>
```  

Podemos verlo en estas dos líneas de código, teniendo Paco y C/ bbb, 9 como datos por defecto.

En la vista princiapal, *index.html*, usamos la base de datos para obtener los nombres de las etiquetas (label) para filtrar por tipo de restaurante y para listar los restaurantes con su nombre

En la vista de *restaurante.html* usamos la base de datos para poder obtener el nombre de dicho lugar y a su vez tenemos una lista de platos para poder ver los que hay en cada restaurante. En este caso disponemos de momento del restaurante Vips (id = 1), más adelante se seguirán añadiendo más platos a los diferentes restaurantes de la base de datos. 

En la vista *platos.html* usamos la base de datos para saber su nombre, la descripción y precio. Aún está por implementar en cuanto a los extras de los platos para poder añadirlos a tu pedido ya que estos no serán los mismos para los diferentes platos a elegir.

En la vista *perfilRestaurante.html* solo tendrá acceso un usuario registrado como el dueño del restaurante. Ahí podrá añadir, modificar o eliminar las ofertas, platos, restaurantes... De momento se usa la base de datos para obtener el nombre del restaurante.

En la vista *listaPedidos.html* usamos la base de datos para sacar el nombre y el id del pedido.

En *finRegistro.html* la base de datos también está siendo usada para poder sacar el nombre del usuario que ha sido registrado.

## Funcionalidades pendientes de arreglar
[comment]: <> (Habría que ir poniendo aquí que cosas fallan)
* Vista de Administración está en blanco
* Implementar funcionalidad en el carrito
* Cambiar la ruta de donde se cargan las imágenes que sean contenido de usuarios
* Implementar poder subir imágenes
* Poder listar los extras de cada plato en su vista
* Poder añadir platos con extras (si se piden) al carrito
* Procesar pedidos
* Hacer que un pedido en curso pueda pasar a estar listo para recoger (Lo hace el propietario del restaurante?)
* Que el repartidor pueda marcar un pedido como completado
* El usuario debe poder valorar un pedido una vez finalizado
* Dinamizar los últimos pedidos de los usuarios
* Darle funcionamiento a los botones de categoría, filtrado y búsqueda del index
* Añadir pruebas con Karate
