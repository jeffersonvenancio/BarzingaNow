import json

from flask import Blueprint, request

from product.model import Product

product = Blueprint('product', __name__)

@product.route('/', methods=['GET'])
def get_all():
	return json.dumps(Product.query().get())

@product.route('/<int:product_id>', methods=['GET'])
def get_by_id(product_id):
	return json.dumps(Product.get_by_id(product_id))

@product.route('/', methods=['POST'])
def add():
	description = request.form['description']
	price = float(request.form['price'])
	qtd_available = int(request.form['qtd'])

	p = Product(description=description, price=price, qtd_available=qtd_available)
	p.put()

	return '', 204