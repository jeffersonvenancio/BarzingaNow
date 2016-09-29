from flask import Blueprint

produto = Blueprint('produto', __name__)

@produto.route('/')
def get_all():
	return "PRODUTO"