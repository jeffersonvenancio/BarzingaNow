import json

import datetime

from flask import Blueprint, request, session
from flask_principal import Permission, RoleNeed
from werkzeug.exceptions import HTTPException, Forbidden

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
    user_email = user_email.split('@')[0] + '@dextra-sw.com'
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

@credit.route('/credits_all', methods=['GET'], strict_slashes=True)
@credit.route('/credits_all/<string:start>/<string:end>', methods=['GET'], strict_slashes=True)
def credits_all(start=None, end=None):
    if start is None or end is None:
        credits = Credit.query().fetch()
    else:
        splitStart = start.split('-')
        from_date = datetime.datetime(year=int(splitStart[2]), month=int(splitStart[1]), day=int(splitStart[0]))
        splitEnd = end.split('-')
        to_date = datetime.datetime(year=int(splitEnd[2]), month=int(splitEnd[1]), day=int(splitEnd[0]))

        credits = Credit.query().filter(Credit.date <= to_date, Credit.date >= from_date).fetch()

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

