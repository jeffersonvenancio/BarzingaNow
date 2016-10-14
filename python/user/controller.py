import json

from flask import Blueprint, request, session

from user.model import User

user = Blueprint('user', __name__)

@user.route('/', methods=['GET'])
def get_all():
    return json.dumps(User.query().get())

@user.route('/<int:user_id>', methods=['GET'])
def get_by_id(user_id):
    return json.dumps(User.get_by_id(user_id))

@user.route('/logged', methods=['GET'])
def get_logged():
    user_json = session['barzinga_user']
    user = User.query().filter(User.email == user_json["email"]).get()
    return json.dumps(user.to_dict())