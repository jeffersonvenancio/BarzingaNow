import os
import json
import cloudstorage as gcs

from flask import Blueprint, request, session
from flask_principal import Permission, RoleNeed

from google.appengine.api import app_identity
from google.appengine.api.images import get_serving_url
from google.appengine.ext import blobstore

from product.model import Product
from user.model import User

from werkzeug.exceptions import HTTPException, Forbidden

admin_permission = Permission(RoleNeed('admin'))

my_default_retry_params = gcs.RetryParams(initial_delay=0.2, max_delay=5.0, backoff_factor=2, max_retry_period=15)
gcs.set_default_retry_params(my_default_retry_params)

product = Blueprint('product', __name__)

@product.route('/category', defaults={'category': None}, methods=['GET'], strict_slashes=False)
@product.route('/category/<string:category>/', methods=['GET'])
def get_all(category=None):
    print category
    if category:
        if category == 'POSSOCOMPRAR':
            user_logged = session['barzinga_user']
            user = User.query().filter(User.email == user_logged["email"]).get()
            products = [p.to_dict() for p in Product.query(Product.price <= user.money).fetch()]
        else:
            products = [p.to_dict() for p in Product.query(Product.category == category).fetch()]
    else :
        products = [p.to_dict() for p in Product.fetch()]

    return json.dumps(products)

@product.route('/<int:product_id>', methods=['GET'])
def get_by_id(product_id):
    product = Product.get_by_id(product_id)

    if not product:
        return 'Product id %s not found' % (product_id), 404

    return json.dumps(product.to_dict())

@product.route('/', methods=['POST'])
@admin_permission.require(http_exception=Forbidden())
def add():
    user_logged = session['barzinga_user']
    user_operator = User.query().filter(User.email == user_logged["email"]).get()
    if user_operator.admin:
        bucket_name = os.environ.get('BUCKET_NAME', app_identity.get_default_gcs_bucket_name())
        description = request.form['description']
        category = request.form['category']
        price = float(request.form['price'])
        quantity = int(request.form['quantity'])

        image = request.files['image']
        image_url = None

        if image:
            write_retry_params = gcs.RetryParams(backoff_factor=1.1)
            filename = '/' + bucket_name + '/' + image.filename
            gcs_file = gcs.open(filename, 'w', content_type=image.content_type, retry_params=write_retry_params)
            gcs_file.write(image.read())
            gcs_file.close()

            blobstore_filename = '/gs' + filename
            key = blobstore.create_gs_key(blobstore_filename)

            image_url =  get_serving_url(key)

        product = Product(description=description, price=price, quantity=quantity, category=category, image_url=image_url, bar_code=str(''))
        product.put()

        return '', 204
    else :
        return 'Precisa ser admin para cadastrar produtos', 401

@product.route('/<int:product_id>', methods=['PUT'])
def modify(product_id):
    print product_id
    product = Product.get_by_id(product_id)

    if not product:
        return 'Product id %s not found' % (product_id), 404

    try:
        product.description = request.form['description']
        product.price = float(request.form['price'])
        product.quantity = int(request.form['quantity'])

        product.put()
    except:
        return 'Deu ruim merm\u00E3o', 400

    return '', 204

@product.route('/<int:product_id>/quantity', methods=['PUT'])
def update_quantity(product_id):
    print product_id
    product = Product.get_by_id(product_id)

    product.quantity = int(request.form['quantity'])
    product.put()

    return '', 204

@product.route('/<int:product_id>/add', methods=['PUT'])
def add_quantity(product_id):
    print product_id
    product = Product.get_by_id(product_id)

    product.quantity += int(request.form['quantity'])
    product.put()

    return '', 204

@product.route('/<int:product_id>/bar_code', methods=['PUT'])
def update_bar_code(product_id):
    print product_id
    product = Product.get_by_id(product_id)

    product.bar_code = str(request.form['bar_code'])
    product.put()

    return '', 204

@product.route('/<int:product_id>', methods=['DELETE'])
def delete(product_id):
    product = Product.get_by_id(product_id)

    if not product:
        return 'Product id %s not found' % (product_id), 404

    product.key.delete()

    return '', 204
