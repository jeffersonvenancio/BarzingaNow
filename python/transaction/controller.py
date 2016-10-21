import json

from flask import Blueprint, request, session
from google.appengine.ext import ndb

from transaction.model import Transaction
from user.model import User
from product.model import Product

transaction = Blueprint('transaction', __name__)

@transaction.route('/', methods=['GET'])
def get_all():
    transactions = [t.to_dict() for t in Transaction.query().fetch()]
    return json.dumps(transactions)

@transaction.route('/<int:transaction_id>', methods=['GET'])
def get_by_id(transaction_id):
    transaction = Transaction.get_by_id(transaction_id)

    if not transaction:
        return 'Transaction id %s not found' % (transaction_id), 404

    return json.dumps(transaction.to_dict())

@transaction.route('/', methods=['POST'])
def add():
    logged_user = session['barzinga_user']
    logged_user = User.query().filter(User.email == logged_user["email"]).get()

    products = json.loads(request.form['products'])

    ids_list = []
    quantity_table = {}

    for product in products:
        quantity_table[product['id']] = product['quantity']
        ids_list.append(product['id'])

    products = ndb.get_multi([ndb.Key(Product, k) for k in ids_list])

    try:
        transaction = Transaction.new(logged_user, products, quantity_table)
        transaction.put()
    except Exception as e:
        return str(e), 400

    return '', 204