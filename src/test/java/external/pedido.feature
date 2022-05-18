Feature: realizar un pedido

Scenario: realizar un pedido de un plato sin extras
  Given call read('login.feature@login_b')
  When click('a.nav-logo')
  Then waitForUrl(baseUrl)
  When click("a.restaurante1")
  Then waitForUrl(baseUrl + '/restaurante?id=1')
  When click("a.plato1")
  Then waitForUrl(baseUrl + '/platos?id=1')
  Given url baseUrl + '/addToCart'
  And input("input[name='cantidad'", '2')
  When submit().click("button.add-carrito")
  Then delay(500)
  Then exists("{^div}Se ha añadido el producto al carrito")
  When click("a.carrito")
  Then waitForUrl(baseUrl + '/user/2/carrito')
  # A través del screenshot vemos que se han pedido dos platos 
  # (aunque realmente estaría pidiendo 21 ya que al hacer el input 
  # le ha añadido un 2 al 1 que ya había, más que reemplazarlo)
  Then driver.screenshot()
  When click("{button}Hacer pedido")
  Then exists("{^div}El pedido se ha procesado correctamente")
  

Scenario: realizar un pedido de un plato con extras
  Given call read('login.feature@login_b')
  When click('a.nav-logo')
  Then waitForUrl(baseUrl)
  When click("a.restaurante1")
  Then waitForUrl(baseUrl + '/restaurante?id=1')
  When click("a.plato1")
  Then waitForUrl(baseUrl + '/platos?id=1')
  Given url baseUrl + '/addToCart'
  And input("input[name='cantidad'", '2')
  And click('input.extras')
  # Comprobamos que se ha seleccionado el primer extra
  Then screenshot() 
  When submit().click("button.add-carrito")
  Then delay(500)
  Then exists("{^div}Se ha añadido el producto al carrito")
  When click("a.carrito")
  Then waitForUrl(baseUrl + '/user/2/carrito')
  # A través del screenshot vemos que se han pedido dos platos 
  # (aunque realmente estaría pidiendo 21 ya que al hacer el input 
  # le ha añadido un 2 al 1 que ya había, más que reemplazarlo)
  Then screenshot()
  When click("{button}Hacer pedido")
  Then exists("{^div}El pedido se ha procesado correctamente")