from flask import Blueprint

usuario = Blueprint('usuario', __name__)

@usuario.route('/')
def get_all():
	return "USUARIO"