import json
from flask import Blueprint, request, session

from credit.model import Credit
from user.model import User

credit = Blueprint('credit', __name__)

@credit.route('/add', methods=['POST'], strict_slashes=False)
def add():
    user_logged = session['barzinga_user']
    if user_logged["email"]:
        user_email = request.form['user']
        value = float(request.form['value'])

        userClient = User.query().filter(User.email == user_email).get()
        if userClient :
            userClient.credit(value=value)
            userClient.put()

            user = User.query().filter(User.email == user_logged["email"]).get()

            credit = Credit(user_email=user_email, value=value, operator=user.email)
            credit.put()
            return 'Barzingas creditados', 204
        else :
            return 'Usuario invalido', 406
    else :
        return 'Precisa ser admin para incluir credito', 401