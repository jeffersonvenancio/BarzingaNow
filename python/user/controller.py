import os
import json
import cloudstorage as gcs
import datetime

from flask import Blueprint, request, session
from google.appengine.api import search

from google.appengine.api import app_identity

from credit.model import Credit
from transaction.model import Transaction
from user.model import User

user = Blueprint('user', __name__)

@user.route('/', methods=['GET'])
def get_all():
    users = [u.to_dict() for u in User.query().fetch()]
    return json.dumps(users)

@user.route('/<int:user_id>', methods=['GET'])
def get_by_id(user_id):
    user = User.get_by_id(user_id).to_dict()
    return json.dumps(user)

@user.route('/email/<string:email>', methods=['GET'])
def get_by_email(email):
    email = email.split('@')[0] + '@dextra-sw.com'
    userClient = User.query().filter(User.email == email).get()

    return json.dumps(userClient.to_dict())

@user.route('/logged', methods=['GET'])
def get_logged():
    user_json = session['barzinga_user']
    user = User.query().filter(User.email == user_json["email"]).get()
    return json.dumps(user.to_dict())

@user.route('/filter', methods=['POST'])
def filter():
    name = request.form['name']

    index = search.Index(name='user')
    users = [[f.__dict__ for f in user.fields] for user in index.search(name)]

    user_list = []
    for u in users:
        user_list.append({
                'name': u[0]['_value'],
                'email': u[1]['_value']
            })

    return json.dumps(user_list)

@user.route('/', methods=['POST'], strict_slashes=False)
def add():
    name = request.form['name']
    email = request.form['email']
    rfid = request.form['rfid']

    user = User(name=name, email=email, admin=False, photo_url='', money=0, rfid=rfid, active = True)
    user.put()

    user_document = search.Document(
        fields=[
            search.TextField(name='name', value=user.name),
            search.TextField(name='email', value=user.email)
        ])

    search.Index(name='user').put(user_document)

    return '', 204

@user.route('/pin', methods=['POST'], strict_slashes=False)
def put_pin():

    user_json = session['barzinga_user']
    user = User.query().filter(User.email == user_json["email"]).get()
    pin = request.form['pin']
    if user:
        user.pin = pin

    user.put()

    return '', 204

@user.route('/rfid', methods=['PUT'], strict_slashes=False)
def put_rfid():
    user = User.query().filter(User.email == request.form['email']).get()
    rfid = request.form['rfid']
    name = request.form['name']
    if user:
        user.rfid = rfid
        user.name = name
        user.put()
        return '', 204

    return '', 404

@user.route('/rfid/<string:rfid>', methods=['GET'], strict_slashes=False)
def get_by_rfid(rfid):
    user = User.query().filter(User.rfid == rfid).get()
    if user:
        user_json = {
            'name' : user.name,
            'email' : user.email,
            'money' : user.money,
            'photo_url' : user.photo_url,
            'id' : user.key.id()
        }
        return json.dumps(user_json)

    return '', 404

@user.route('/index', methods=['DELETE'])
def remove_indexes():
    delete_all_in_index(search.Index(name='user'))
    return '', 204

def delete_all_in_index(index):
    while True:
        document_ids = [
            document.doc_id
            for document
            in index.get_range(ids_only=True)]

        if not document_ids:
            break

        index.delete(document_ids)


@user.route('/cron/week', methods=['GET'], strict_slashes=False)
def weekDebit():
    users = User.query().filter(User.money < -0.01).fetch()

    usersJson = 'email;valor \n'

    for u in users:
        usersJson += str(u.email)+';'+str("%.2f" % round(u.money,2))+' \n'

    make_blob_public(usersJson, 'weekly/', datetime.datetime.now().strftime("%d_%m_%y"))

    #ENVIAR EMAIL PARA OS DEVEDORES

    return json.dumps(usersJson)



@user.route('/cron/monthly', methods=['GET'], strict_slashes=False)
def monthlyBalance():
    users = User.query().fetch()

    usersCSV = 'email;valor \n'

    for u in users:
        usersCSV += str(u.email)+';'+str("%.2f" % round(u.money,2))+' \n'

    make_blob_public(usersCSV, 'monthly/', 'credit_balance_'+datetime.datetime.now().strftime("%d_%m_%y"))

    return json.dumps(usersCSV)

def make_blob_public(usersJson, subpath, fileName):
    bucket_name = os.environ.get('BUCKET_NAME', app_identity.get_default_gcs_bucket_name())
    write_retry_params = gcs.RetryParams(backoff_factor=1.1)
    fullPath = '/' + bucket_name + '/00_Reports/'+subpath+fileName+'.csv'
    gcs_file = gcs.open(fullPath, 'w', content_type='csv', retry_params=write_retry_params)
    gcs_file.write(usersJson)
    gcs_file.close()


@user.route('/deactivate', methods=['PUT'], strict_slashes=False)
def deactivate():
    user = User.query().filter(User.email == request.form['email']).get()

    if user:
        user.active = False
        user.put()
        return '', 204

    return '', 404


# @user.route('/cron/gerarodejulho', methods=['GET'], strict_slashes=False)
# def gerarodejulho():
#     users = User.query().fetch()
#
#     from_date = datetime.datetime(year=2019, month=8, day=1)
#     to_date = datetime.datetime(year=2019, month=8, day=6)
#
#     transactions = Transaction.query().filter(Transaction.date <= to_date, Transaction.date >= from_date).fetch()
#     credits = Credit.query().filter(Credit.date <= to_date, Credit.date >= from_date).fetch()
#
#     usersCSV = 'email;valor \n'
#
#     for u in users:
#         saldo = u.money
#
#         for t in transactions:
#             if t.user.get().email == u.email:
#                 saldo+=t.value
#
#         for c in credits:
#             if c.user_email == u.email:
#                 saldo-=c.value
#
#         usersCSV += str(u.email)+';'+str("%.2f" % round(saldo,2))+' \n'
#
#     make_blob_public(usersCSV, 'monthly/', 'credit_balance_01_08_2019')
#
#     return json.dumps(usersCSV)