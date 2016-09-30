import json

from flask import Blueprint, request

from product.model import Product

product = Blueprint('product', __name__)

@product.route('/', methods=['GET'])
def get_all():
    return json.dumps(Product.query().fetch())

@product.route('/<int:product_id>', methods=['GET'])
def get_by_id(product_id):
    return json.dumps(Product.get_by_id(product_id).to_dict())

@product.route('/', methods=['POST'])
def add():
    description = request.form['description']
    price = float(request.form['price'])
    qtd_available = int(request.form['qtd'])

    p = Product(description=description, price=price, qtd_available=qtd_available)
    p.put()

    return '', 204

@product.route('/<string:product_id>', methods=['PUT'])
def modify(product_id):
    new_product = request.form['product']

    product = Product.get_by_id(product_id)
    
    if not product:
        return 'Product id %s not found' % (product_id), 404

    product.update(new_product)
    product.put()

    return '', 204