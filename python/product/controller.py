import json

from flask import Blueprint, request

from product.model import Product

product = Blueprint('product', __name__)

@product.route('/', methods=['GET'])
def get_all():
    products = [p.to_dict() for p in Product.query().fetch()]
    return json.dumps(products)

@product.route('/<int:product_id>', methods=['GET'])
def get_by_id(product_id):
    product = Product.get_by_id(product_id)

    if not product:
        return 'Product id %s not found' % (product_id), 404

    return json.dumps(product.to_dict())

@product.route('/', methods=['POST'])
def add():
    description = request.form['description']
    price = float(request.form['price'])
    quantity = int(request.form['quantity'])

    product = Product(description=description, price=price, quantity=quantity)
    product.put()

    return '', 204

@product.route('/<int:product_id>', methods=['PUT'])
def modify(product_id):
    product = Product.get_by_id(product_id)

    if not product:
        return 'Product id %s not found' % (product_id), 404

    try:
        product.description = request.form['description']
        product.price = float(request.form['price'])

        product.put()
    except:
        return 'Deu ruim merm\u00E3o', 400

    return '', 204

@product.route('/<int:product_id>/quantity', methods=['PUT'])
def add_quantity(product_id):
    product = Product.get_by_id(product_id)

    product.quantity += int(request.form['quantity'])
    product.put()

    return '', 204

@product.route('/<int:product_id>', methods=['DELETE'])
def delete(product_id):
    product = Product.get_by_id(product_id)

    if not product:
        return 'Product id %s not found' % (product_id), 404

    product.key.delete()

    return '', 204