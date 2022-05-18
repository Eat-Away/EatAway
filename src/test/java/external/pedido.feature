Feature: realizar un pedido

Scenario: elegir un plato de un restaurante
  Given call read('login.feature@login_b')
  When click('a.nav-logo')
  Then waitForUrl(baseUrl)
  When click("a.restaurante1")
  Then waitForUrl(baseUrl + '/restaurante?id=1')
  When click("a.plato1")
  Then waitForUrl(baseUrl + '/platos?id=1')
  Given url baseUrl + '/addToCart'
  And request {'cantidad': 1, 'id': 1}
  When submit().click("button.add-carrito")
  # Then waitForUrl(baseUrl + '/addToCart')
  Then match text('div.addToCart-alert') contains 'Se ha añadido el producto al carrito'
  
  