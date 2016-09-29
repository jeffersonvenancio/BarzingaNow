from flask import Blueprint

transacao = Blueprint('transacao', __name__)

@transacao.route('/')
def get_all():
	return "TRANSACAO"