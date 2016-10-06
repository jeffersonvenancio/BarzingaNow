import json

from flask import Blueprint, request

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
    user = User.get_by_id(int(request.form['user_id']))

    if not user:
        return 'User id %s not found' % (user_id), 404

    if 'product_id' in request.form:
        product = Product.get_by_id(int(request.form['product_id']))
        value = None
    else:
        value = request.form['value']

    transaction = Transaction(user=user, product=product, value=value)
    transaction.put()

    return '', 204