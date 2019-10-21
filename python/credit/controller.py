import os
import json
import cloudstorage as gcs
import datetime

from flask import Blueprint, request, session
from flask_principal import Permission, RoleNeed
from werkzeug.exceptions import HTTPException, Forbidden

from google.appengine.api import app_identity
from google.appengine.api import mail

from credit.model import Credit
from user.model import User

credit = Blueprint('credit', __name__)

admin_permission = Permission(RoleNeed('admin'))

@credit.route('/add', methods=['POST'], strict_slashes=False)
@admin_permission.require(http_exception=Forbidden())
def add():
    user_logged = session['barzinga_user']
    user_operator = User.query().filter(User.email == user_logged['email']).get()

    user_email = request.form['user']
    value = float(request.form['value'])
    if '@' not in user_email:
        user_email = user_email + '@dextra-sw.com'
    userClient = User.query().filter(User.email == user_email).get()
    if userClient :
        userClient.credit(value=value)
        userClient.put()
        credit = Credit(user_email=user_email, value=value, operator=user_operator.email)
        credit.put()
        return 'Barzingas creditados', 204
    else :
        return 'Usuario invalido', 406

@credit.route('/all', methods=['GET'], strict_slashes=False)
def all():
    user_logged = session['barzinga_user']
    if user_logged :
        credits = Credit.query().filter(Credit.user_email == user_logged['email']).order(-Credit.date).fetch(10)

        creditsJson = []

        for c in credits:
            creditJson = {}
            if c.date is not None:
                creditJson['date'] = str(c.date.strftime('%d/%m/%y - %H:%M'))
            else:
                creditJson['date'] = ''
            creditJson['value'] = str(c.value)
            creditJson['operator'] = str(c.operator).split('@')[0]
            creditJson['user'] = str(c.user_email).split('@')[0]

            creditsJson.append(creditJson)

        return json.dumps(creditsJson)
    else :
        return 'Usuario invalido', 406


