let express = require('express')
let bodyParser = require('body-parser')
let app = express()

// For test proposes, the "database" is described bellow
const ingredients = {
	1: {id: 1, name: 'Alface', price: 0.40, image: 'https://goo.gl/9DhCgk'},
	2: {id: 2, name: 'Bacon', price: 2.00, image: 'https://goo.gl/8qkVH0'},
	3: {id: 3, name: 'Hamburguer de Carne', price: 3.00, image: 'https://goo.gl/U01SnT'},
	4: {id: 4, name: 'Ovo', price: 0.80, image: 'https://goo.gl/weL1Rj'},
	5: {id: 5, name: 'Queijo', price: 1.50, image: 'https://goo.gl/D69Ow2'},
	6: {id: 6, name: 'Pão com gergelim', price: 1.00, image: 'https://goo.gl/evgjyj'}
}

const sandwiches = {
	1: {id: 1, name: 'X-Bacon', ingredients: [2, 3, 5, 6], image: 'https://goo.gl/W9WdaF'},
	2: {id: 2, name: 'X-Burger', ingredients: [3, 5, 6], image: 'https://goo.gl/Cjfxi9'},
	3: {id: 3, name: 'X-Egg', ingredients: [3, 4, 5, 6], image: 'https://goo.gl/x4rNIq'},
	4: {id: 4, name: 'X-Egg Bacon', ingredients: [1, 2, 3, 4, 5, 6], image: 'https://goo.gl/2L0lqg'}
}

const promos = {
	1: {id: 1, name: 'Light', description: 'Se o lanche tem alface e não tem bacon, ganha 10% de desconto.'},
	2: {id: 2, name: 'Muita carne', description: 'A cada 3 porções de carne só paga 2. Se o lanche tiver 6 porções, pagará 4. Assim por diante'},
	3: {id: 3, name: 'Muito queijo', description: 'A cada 3 porções de queijo só paga 2. Se o lanche tiver 6 porções, pagará 4. Assim por diante'}
}

let orders = {}

// Basic configuration
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))
app.listen(8080)

// Business logic
const map_to_array = (map) => {
	let list = []
	Object.keys(map).forEach(key => list.push(map[key]))
	return list
}

const list_endpoints = (res) =>	res.send('Valid REST routes:\n/api/pedido\n/api/ingrediente\n/api/lanche\n/api/promocao')
const list_sandwiches = (res) => res.json(map_to_array(sandwiches))
const list_ingredients = (res) => res.json(map_to_array(ingredients))
const list_promos = (res) => res.json(map_to_array(promos))
const list_orders = (res) => res.json(map_to_array(orders))

const get_sandwich = (id, res) => {
	if (!sandwiches[id]) {
		return res.status(404).send('Id not found')
	}

	res.json(sandwiches[id])
}

const list_ingredients_of_sandwich = (id, res) => {
	if (!sandwiches[id]) {
		return res.status(404).send('Id not found')
	}

	let i = []
	sandwiches[id].ingredients.forEach(id => i.push(ingredients[id]))
	res.json(i)
}

const add_order = (id_sandwich, extras, res) => {
	if (!sandwiches[id_sandwich]) {
		return res.status(400).send('Invalid id')
	}

	extras = extras ? JSON.parse(extras) : []
	let [last_id] = Object.keys(orders).slice(-1)
	let id = last_id ? parseInt(last_id) + 1 : 1
	let order = {id, id_sandwich, extras, date: new Date().getTime()}
	orders[id] = order
	res.json(order)
}

// Routing
app.get('/', (req, res) => list_endpoints(res))
app.get('/api', (req, res) => list_endpoints(res))

app.get('/api/lanche', (req, res) => list_sandwiches(res))
app.get('/api/lanche/:id_sandwich', (req, res) => get_sandwich(req.params.id_sandwich, res))

app.get('/api/ingrediente', (req, res) => list_ingredients(res))
app.get('/api/ingrediente/de/:id_sandwich', (req, res) => list_ingredients_of_sandwich(req.params.id_sandwich, res))

app.get('/api/promocao', (req, res) => list_promos(res))

app.get('/api/pedido', (req, res) => list_orders(res))
app.put('/api/pedido/:id_sandwich', (req, res) => add_order(req.params.id_sandwich, req.body.extras, res))
