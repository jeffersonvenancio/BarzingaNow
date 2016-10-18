from flask import Blueprint, request, session, redirect
import requests
import requests.auth
from user.model import User
from util import find_prop
import json

auth = Blueprint('auth', __name__)

redirect_uri = find_prop('REDIRECT_URI')
scope = find_prop('SCOPE')
client_id = find_prop('CLIENT_ID')
hd = find_prop('HD')
client_id = find_prop('CLIENT_ID')
client_secret = find_prop('CLIENT_SECRET')

@auth.route('/', strict_slashes=False)
def hello():
	if 'barzinganow.appspot.com' in request.url :
		host_url = find_prop('HOST_URL_PROD')
	else :
		host_url = find_prop('HOST_URL_DEV')

	url = 'https://accounts.google.com/AccountChooser?continue=https://accounts.google.com/o/oauth2/auth?scope%3D'+scope+'%26response_type%3Dcode%26redirect_uri%3D'+host_url+redirect_uri+'%26state%3Dsecurity_token%253D138r5719ru3e1%2526url%253D'+host_url+'%26client_id%3D'+client_id+'%26from_login%3D1%26as%3D-231db6e2ffa9ce49&btmpl=authsub&scc=1&oauth=1&hd='+hd
	return redirect(url)

@auth.route('/token', strict_slashes=False)
def token():
	if 'barzinganow.appspot.com' in request.url :
		host_url = find_prop('HOST_URL_PROD')
	else :
		host_url = find_prop('HOST_URL_DEV')

	code = request.args.get('code')
	client_auth = requests.auth.HTTPBasicAuth(client_id, client_secret)
	post_data = {"grant_type": "authorization_code",
				 "code": code,
				 "redirect_uri": host_url+redirect_uri}
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
		user = User(name = user_json['name'], email=user_json['email'], photo_url=user_json['picture'], money=0.0)
		user.put()

