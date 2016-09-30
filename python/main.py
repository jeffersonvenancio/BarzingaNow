from flask import Flask, request, session, abort, render_template
from werkzeug.utils import redirect
import requests

from user.controller import user as user_controller
from product.controller import product as product_controller
from transaction.controller import transaction as transaction_controller

app = Flask(__name__, template_folder='web')
app.secret_key = 'super secret key'
app.config['SESSION_TYPE'] = 'filesystem'

app.register_blueprint(user_controller, url_prefix='/user')
app.register_blueprint(product_controller, url_prefix='/product')
app.register_blueprint(transaction_controller, url_prefix='/transaction')

@app.route('/')
def hello():
    session['session_state'] = None
    return redirect('https://accounts.google.com/AccountChooser?continue=https://accounts.google.com/o/oauth2/auth?scope%3Dhttps://www.googleapis.com/auth/contacts.readonly%26response_type%3Dcode%26redirect_uri%3Dhttp://localhost:8080/token%26state%3Dsecurity_token%253D138r5719ru3e1%2526url%253Dhttps://oauth2-login-demo.example.com/myHome%26client_id%3D905590247007-tmmromevhmghnve94lc1sqoh08itlhjf.apps.googleusercontent.com%26from_login%3D1%26as%3D-231db6e2ffa9ce49&btmpl=authsub&scc=1&oauth=1')

@app.route('/token')
def token():
    # error = request.args.get('error', '')
    # if error:
    #     return "Error: " + error
    # state = request.args.get('state', '')
    # if not is_valid_state(state):
    #     abort(403)
    # code = request.args.get('code')
    #
    # return "got an access token! %s" % get_token(code)

    return render_template('index.html')

def get_token(code):
    print code
    post_data = {"grant_type": "authorization_code",
                 "code": code,
                 "redirect_uri": 'http://localhost:8080/token',
                 "client_id" : '905590247007-tmmromevhmghnve94lc1sqoh08itlhjf.apps.googleusercontent.com',
                 "client_secret" : 'l-p5n0NQsBQNOl_naYVxgBFd' }

    response = requests.post("https://accounts.google.com/o/oauth2/token?grant_type=authorization_code&code="+code+"&redirect_uri=http://localhost:8080/token&client_id=905590247007-tmmromevhmghnve94lc1sqoh08itlhjf.apps.googleusercontent.com&client_secret=l-p5n0NQsBQNOl_naYVxgBFd")
    print response

    token_json = response.json()
    return token_json["access_token"]

def save_created_state(state):
    pass
def is_valid_state(state):
    return True

@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, Nothing at this URL.', 404

@app.errorhandler(500)
def application_error(e):
    return 'Sorry, unexpected error: {}'.format(e), 500