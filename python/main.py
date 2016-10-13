from flask import Flask, request, session, url_for
from werkzeug.utils import redirect
from requests_toolbelt.adapters import appengine
from flask_cors import CORS

appengine.monkeypatch()

from auth.controller import auth as auth_controller
from user.controller import user as user_controller
from product.controller import product as product_controller
from transaction.controller import transaction as transaction_controller

app = Flask(__name__, static_folder='web')

cors = CORS(app, resources={r"/api/*": {"origins": "*"}})

app.secret_key = 'super secret key'
app.config['SESSION_TYPE'] = 'filesystem'

app.register_blueprint(auth_controller, url_prefix='/api/auth')
app.register_blueprint(user_controller, url_prefix='/api/user')
app.register_blueprint(product_controller, url_prefix='/api/product')
app.register_blueprint(transaction_controller, url_prefix='/api/transaction')

@app.route('/')
def main():
    return app.send_static_file('index.html')

@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, Nothing at this URL.', 404

@app.errorhandler(500)
def application_error(e):
    return 'Sorry, unexpected error: {}'.format(e), 500