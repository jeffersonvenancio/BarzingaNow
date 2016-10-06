from flask import Blueprint

transaction = Blueprint('transaction', __name__)

@transaction.route('/')
def get_all():
    return "TRANSACAO"