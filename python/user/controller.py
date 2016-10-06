import json

from flask import Blueprint, request

from user.model import User

user = Blueprint('user', __name__)

@user.route('/', methods=['GET'])
def get_all():
    return json.dumps(User.query().get())

@user.route('/<int:user_id>', methods=['GET'])
def get_by_id(user_id):
    return json.dumps(User.get_by_id(user_id))

@user.route('/', methods=['POST'])
def add():
    name = request.form['name']
    money = float(request.form['money'])

    user = User(name=name, money=money)
    user.put()

    return '', 204