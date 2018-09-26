import json

from flask import Blueprint, request, session
from google.appengine.api import search

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

    user = User(name=name, email=email, admin=False, photo_url='', money=0, rfid=str(''))
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
    if user:
        user.rfid = rfid
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