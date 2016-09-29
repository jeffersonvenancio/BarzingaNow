from flask import Flask, render_template

from usuario.controller import usuario as usuario_controller
from produto.controller import produto as produto_controller
from transacao.controller import transacao as transacao_controller

app = Flask(__name__, template_folder='web')

app.register_blueprint(usuario_controller, url_prefix='/usuario')
app.register_blueprint(produto_controller, url_prefix='/produto')
app.register_blueprint(transacao_controller, url_prefix='/transacao')

@app.route('/')
def hello():
	# return 'sadasda'
	return render_template('index.html')

@app.errorhandler(404)
def page_not_found(e):
	return 'Sorry, Nothing at this URL.', 404

@app.errorhandler(500)
def application_error(e):
	return 'Sorry, unexpected error: {}'.format(e), 500