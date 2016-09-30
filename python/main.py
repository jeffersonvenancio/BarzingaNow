from flask import Flask, render_template

from user.controller import user as user_controller
from product.controller import product as product_controller
from transaction.controller import transaction as transaction_controller

app = Flask(__name__, template_folder='web')

app.register_blueprint(user_controller, url_prefix='/user')
app.register_blueprint(product_controller, url_prefix='/product')
app.register_blueprint(transaction_controller, url_prefix='/transaction')

@app.route('/')
def hello():
	return render_template('index.html')

@app.errorhandler(404)
def page_not_found(e):
	return 'Sorry, Nothing at this URL.', 404

@app.errorhandler(500)
def application_error(e):
	return 'Sorry, unexpected error: {}'.format(e), 500