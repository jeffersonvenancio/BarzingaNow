import os
import json
import cloudstorage as gcs

from flask import Blueprint, request
from google.appengine.api import app_identity
from google.appengine.api.images import get_serving_url
from google.appengine.ext import blobstore

from product.model import Product

my_default_retry_params = gcs.RetryParams(initial_delay=0.2, max_delay=5.0, backoff_factor=2, max_retry_period=15)
gcs.set_default_retry_params(my_default_retry_params)

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
    bucket_name = os.environ.get('BUCKET_NAME', app_identity.get_default_gcs_bucket_name())

    description = request.form['description']
    price = float(request.form['price'])
    quantity = int(request.form['quantity'])

    image = request.files['image']

    write_retry_params = gcs.RetryParams(backoff_factor=1.1)
    filename = '/' + bucket_name + '/' + image.filename
    gcs_file = gcs.open(filename, 'w', content_type=image.content_type, retry_params=write_retry_params)
    gcs_file.write(image.read())
    gcs_file.close()

    blobstore_filename = '/gs' + filename
    key = blobstore.create_gs_key(blobstore_filename)

    image_url =  get_serving_url(key)

    product = Product(description=description, price=price, quantity=quantity, image_url=image_url)
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