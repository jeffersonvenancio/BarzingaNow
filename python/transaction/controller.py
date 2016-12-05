import json

from flask import Blueprint, request, session
from google.appengine.ext import ndb

from transaction.model import Transaction, TransactionItem
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

    products_list = []
    quantity_table = {}

    for product in products:
        quantity_table[product['id']] = product['quantity']
        products_list.append(ndb.Key(Product, product['id']).get())

    print products_list

    try:
        transaction = Transaction.new(logged_user, products_list, quantity_table)
        transaction.put()
    except Exception as e:
        return str(e), 400

    return '', 204

@transaction.route('/extract', methods=['GET'])
def transactions_user():
    logged_user = session['barzinga_user']
    logged_user = User.query().filter(User.email == logged_user["email"]).get()

    transactions = Transaction.query().filter(Transaction.user == logged_user.key).fetch()

    trans = [];

    for t in transactions:
        transa = {}
        transa['id'] = str(t.key)
        transa['user'] = logged_user.name
        transa['value'] = str(t.value)
        transa['date'] = str(t.date)
        itens = []
        for it in t.items :
            item = {}
            transaction_item = it.get()
            item['product'] = transaction_item.product.get().description
            item['quantity'] = transaction_item.quantity
            itens.append(item)

        transa['itens'] = itens
        trans.append(transa)

    return json.dumps(trans)