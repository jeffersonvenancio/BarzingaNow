import json
import csv
import StringIO
import xlsxwriter
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
    print 'add credit'
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
    print 'Creditos'
    for c in credits:
        creditJson = {}
        creditJson['date'] = c.date.strftime('%d/%m/%y - %H:%M')
        creditJson['value'] = str(c.value)
        creditJson['operator'] = str(c.operator).split('@')[0]
        creditJson['user'] = str(c.user_email).split('@')[0]
        
        creditsJson.append(creditJson)

    return json.dumps(creditsJson)
