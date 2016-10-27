import json
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
    user_operator = User.query().filter(User.email == user_logged["email"]).get()

    user_email = request.form['user']
    value = float(request.form['value'])
    user_email = user_email.split('@')[0] + "@dextra-sw.com"
    userClient = User.query().filter(User.email == user_email).get()
    if userClient :
        userClient.credit(value=value)
        userClient.put()
        credit = Credit(user_email=user_email, value=value, operator=user_operator.email)
        credit.put()
        return 'Barzingas creditados', 204
    else :
        return 'Usuario invalido', 406