import json

from flask import Blueprint

from usuario.model import Usuario

usuario = Blueprint('usuario', __name__)

@usuario.route('/', methods=['GET'])
def get_all():
	return json.dumps(Usuario.query().get())

@usuario.route('/<int:user_id>', methods=['GET'])
def get_by_id(user_id):
	return json.dumps(Usuario.get_by_id(user_id))