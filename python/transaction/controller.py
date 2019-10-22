import json
from flask import Blueprint, request, session
from google.appengine.ext import ndb

from product.model import Product
from transaction.model import Transaction
from user.model import User

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
    # return 'Compra nao permitida', 403
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

@transaction.route('/app', methods=['POST'])
def add_app():

    json_data = request.get_json()

    user_json = json_data.get('user')

    user = User.query().filter(User.email == user_json.get('email')).get()

    products = json_data.get('products')

    products_list = []
    quantity_table = {}

    for product in products:
        id = int(product['id'])
        quantity_table[id] = product['quantity']
        products_list.append(ndb.Key(Product, id).get())

    try:
        transaction = Transaction.new(user, products_list, quantity_table)
        transaction.put()
    except Exception as e:
        return str(e), 400

    return str('ok'), 200

@transaction.route('/extract', methods=['GET'])
def transactions_user():
    logged_user = session['barzinga_user']
    logged_user = User.query().filter(User.email == logged_user["email"]).get()

    transactions = Transaction.query().filter(Transaction.user == logged_user.key).order(-Transaction.date).fetch(20)

    trans = [];

    for t in transactions:
        transact = {}
        transact['id'] = str(t.key)
        transact['user'] = logged_user.name.encode('utf-8').strip()
        transact['value'] = str(t.value)
        transact['date'] = str(t.date.strftime('%d/%m/%y - %H:%M'))
        itens = []
        for it in t.items :
            item = {}
            transaction_item = it.get()

            prod = transaction_item.product.get()
            item['product'] = 'Nao Existe Mais'

            if prod:
                item['product'] = prod.description


            item['quantity'] = str(transaction_item.quantity)
            itens.append(item)

        transact['itens'] = itens
        trans.append(transact)

    return json.dumps(trans)
