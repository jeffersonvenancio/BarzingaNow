import json

from flask import Blueprint, request

from produto.model import Produto

produto = Blueprint('produto', __name__)

@produto.route('/', methods=['GET'])
def get_all():
	return json.dumps(Produto.query().get())

@produto.route('/<int:produto_id>', methods=['GET'])
def get_by_id(produto_id):
	return json.dumps(Produto.get_by_id(produto_id))

@produto.route('/', methods=['POST'])
def add():
	descricao = request.form['descricao']
	valor = float(request.form['valor'])

	p = Produto(descricao=descricao, valor=valor)
	p.put()

	return '', 204