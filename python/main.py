from flask import Flask, jsonify, url_for, redirect
from cozinha import Mesa, Status
from user_token import UserToken
import json
import httplib

app = Flask(__name__)

@app.route('/')
def hello():
	return redirect(url_for('static', filename='main.html'))

@app.errorhandler(404)
def page_not_found(e):
	return 'Sorry, Nothing at this URL.', 404

@app.errorhandler(500)
def application_error(e):
	return 'Sorry, unexpected error: {}'.format(e), 500
