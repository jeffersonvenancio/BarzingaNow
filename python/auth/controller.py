from flask import Blueprint, request, session, redirect
import requests
import requests.auth
from user.model import User

auth = Blueprint('auth', __name__)

CLIENT_ID = '905590247007-tmmromevhmghnve94lc1sqoh08itlhjf.apps.googleusercontent.com'
CLIENT_SECRET = 'l-p5n0NQsBQNOl_naYVxgBFd'
REDIRECT_URI = 'http://localhost:8080/api/auth/token'
HD = 'dextra-sw.com'
SCOPE = 'https://www.googleapis.com/auth/userinfo.email'
URL = 'http://localhost:8080/'



@auth.route('/', strict_slashes=False)
def hello():
	return redirect('https://accounts.google.com/AccountChooser?continue=https://accounts.google.com/o/oauth2/auth?scope%3D'+SCOPE+'%26response_type%3Dcode%26redirect_uri%3D'+REDIRECT_URI+'%26state%3Dsecurity_token%253D138r5719ru3e1%2526url%253D'+URL+'%26client_id%3D'+CLIENT_ID+'%26from_login%3D1%26as%3D-231db6e2ffa9ce49&btmpl=authsub&scc=1&oauth=1&hd='+HD)

@auth.route('/token', strict_slashes=False)
def token():
	code = request.args.get('code')
	client_auth = requests.auth.HTTPBasicAuth(CLIENT_ID, CLIENT_SECRET)
	post_data = {"grant_type": "authorization_code",
				 "code": code,
				 "redirect_uri": REDIRECT_URI}
	response = requests.post("https://accounts.google.com/o/oauth2/token",
							 auth=client_auth,
							 data=post_data)
	token_json = response.json()

	authorization_header = {"Authorization": "OAuth %s" % token_json["access_token"]}
	r = requests.get("https://www.googleapis.com/oauth2/v2/userinfo",
					 headers=authorization_header)

	session['barzinga_user'] = r.json()

	verifica_user()

	return redirect('/')

def verifica_user():
	user_json = session['barzinga_user']
	user = User.query().filter(User.email == user_json["email"]).get()
	if not user:
		user = User(name = user_json['name'], email=user_json['email'], photo_url=user_json['picture'])
		user.put()
