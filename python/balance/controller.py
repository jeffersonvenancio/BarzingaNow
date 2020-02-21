# coding=utf-8
import cloudstorage as gcs
import datetime
import json
import os
import requests
from flask import Blueprint
from google.appengine.api import app_identity
from google.appengine.api import mail
from transaction.model import Transaction
from user.model import User
from credit.model import Credit
from mailjet_rest import Client

balance = Blueprint('balance', __name__)

def send_simple_message(emailList):
    f = open('mail/template.html', 'r')  
    api_key = os.environ['API_KEY_BARZINGA']
    api_secret = os.environ['API_SECRET_BARZINGA']

    mailjet = Client(auth=(api_key, api_secret), version='v3.1')
    data = {
        'Messages': [
            {
                'From': {
                    'Email': 'financeiro@dextra.com.br',
                    'Name': 'financeiro'
                },
                'To': [{
                    'Email': 'people@dextra-sw.com'
                }],
                'Bcc': emailList,
                'Subject': '[Recarga] Barzinga',
                'HTMLPart': f.read(),
                'CustomID': 'AppGettingStartedTest'
            }
        ]
    }
    result = mailjet.send.create(data=data)
    f.close()

    return 'ok'


def make_blob_public(csv, folder, name=None):
    bucket_name = os.environ.get('BUCKET_NAME', app_identity.get_default_gcs_bucket_name())
    write_retry_params = gcs.RetryParams(backoff_factor=1.1)
    filename = '/' + bucket_name + '/00_Reports/'+folder+'/'+name+'.csv'
    gcs_file = gcs.open(filename, 'w', content_type='csv', retry_params=write_retry_params)
    gcs_file.write(csv)
    gcs_file.close()

@balance.route('/cron/lastmonthbalance', methods=['GET'], strict_slashes=True)
def transactions_last_month():
    today = datetime.date.today()
    first = today.replace(day=1)
    lastMonthEnd = first - datetime.timedelta(days=1)
    lastMonthBegin = lastMonthEnd.replace(day=1)
    transactions = transactions_all(end=lastMonthEnd.strftime("%d-%m-%Y"), start=lastMonthBegin.strftime("%d-%m-%Y"))
    users = User.query().fetch()
    credits = credits_all(end=lastMonthEnd.strftime("%d-%m-%Y"), start=lastMonthBegin.strftime("%d-%m-%Y"))

    totalTransacoesCompra = 0.00
    totalInadimplentes = 0.00
    totalCreditosEmUsuarios = 0.00
    totalCreditosComprados = 0.00

    for t in transactions:
        totalTransacoesCompra += t.value

    for u in users:
        totalCreditosEmUsuarios += u.money

        if u.money < -0.01 :
            totalInadimplentes += u.money

    for c in credits:
        totalCreditosComprados += c.value

    resultado = 'Valor total das transacoes; '+str("%.2f" % round(totalTransacoesCompra,2))+'\n'
    resultado += 'Valor total dos creditos em usuarios; '+str("%.2f" % round(totalCreditosEmUsuarios,2))+'\n'
    resultado += 'Valor total dos Usuarios Negativos; '+str("%.2f" % round(totalInadimplentes,2))+'\n'
    resultado += 'Valor total dos Creditos Adquiridos; '+str("%.2f" % round(totalCreditosComprados,2))+'\n'

    make_blob_public(str(resultado), 'monthly', 'balance_'+lastMonthBegin.strftime("%d-%m-%Y"))

    return str('ok'), 200

def transactions_all(start=None, end=None):
    if start is None or end is None:
        return Transaction.query().fetch()
    else:
        splitStart = start.split('-')
        from_date = datetime.datetime(year=int(splitStart[2]), month=int(splitStart[1]), day=int(splitStart[0]))
        splitEnd = end.split('-')
        to_date = datetime.datetime(year=int(splitEnd[2]), month=int(splitEnd[1]), day=int(splitEnd[0]))

        return Transaction.query().filter(Transaction.date <= to_date, Transaction.date >= from_date).fetch()

def credits_all(start=None, end=None):
    splitStart = start.split('-')
    from_date = datetime.datetime(year=int(splitStart[2]), month=int(splitStart[1]), day=int(splitStart[0]))
    splitEnd = end.split('-')
    to_date = datetime.datetime(year=int(splitEnd[2]), month=int(splitEnd[1]), day=int(splitEnd[0]))

    return Credit.query().filter(Credit.date <= to_date, Credit.date >= from_date).fetch()

@balance.route('/cron/user-position/<string:period>', methods=['GET'], strict_slashes=False)
def user_position(period):
    users = User.query().filter(User.money < 0.0).filter(User.active == True).fetch()
    users_email_list = []
    usersCsv = 'email;valor;active \n'

    for idx, user in enumerate(users):
        usersCsv += str(user.email)+';'+str("%.2f" % round(user.money,2))+';'+str(user.active)+' \n'
        mail = {}
        mail['Email'] = user.email
        users_email_list.append(mail)


    make_blob_public(usersCsv, period, 'user_positions_'+datetime.datetime.now().strftime("%d_%m_%y"))

    if (period == 'monthly' and len(users_email_list) != 0):
        print(users_email_list)
        response = send_simple_message(users_email_list)
        print(response)


    return json.dumps(usersCsv)

@balance.route('/cron/exceeded-debit', methods=['GET'], strict_slashes=False)
def dailyDebitExceeded():
    users = User.query().filter(User.money < -40.01 and User.active == True).fetch()
    users_email_list = []

    usersJson = 'email;valor \n'

    for u in users:
        usersJson += str(u.email)+';'+str("%.2f" % round(u.money,2))+' \n'
        users_email_list.append(str(u.email))

    make_blob_public(usersJson, 'debitExceeded/', datetime.datetime.now().strftime("%d_%m_%y"))

    if (len(users_email_list) != 0):
        print(users_email_list)
        #  mail.send_mail(sender = 'fernanda.bezerra@dextra-sw.com',
        #                bcc = users_email_list,
        #                 subject = 'Barzinga: Saldo em débito excedido',
        #                body = 'Ola, Dextrana(o)! Nossos sistemas perceberam que sua conta no Barzinga encontra-se muito negativa... Por isso, gostariamos de pedir que nos procure para se regularizar urgentemente!').Send()
    #  users_email_list.clear() - ISSO NÂO FUNCIONA

    return json.dumps(usersJson)

@balance.route('/cron/yesterday', methods=['GET'], strict_slashes=True)
def credits_yesterday():
    yesterday_dt = datetime.datetime.now() - datetime.timedelta(days = 1)

    from_date = yesterday_dt.replace(hour=0, minute=0, second=0)
    to_date = yesterday_dt.replace(hour=23, minute=59, second=59)

    credits = Credit.query().filter(Credit.date <= to_date, Credit.date >= from_date).fetch()

    credits_str = 'data;valor;operador;usuario \n'

    total = 0

    for c in credits:
        credit_str = ""
        if c.date is not None:
            credit_str += str(c.date.strftime('%d/%m/%y - %H:%M'))+';'
        else:
            credit_str += ' ;'

        credit_str += str(c.value)+';'
        credit_str += str(c.operator).split('@')[0]+';'
        credit_str += str(c.user_email).split('@')[0]+' \n'

        credits_str += credit_str

        total += c.value

    make_blob_public(credits_str, 'daily',yesterday_dt.strftime('%d_%m_%y'))

    mail.send_mail(sender='jefferson.venancio@dextra-sw.com',
                   to="franciane.oliveira@dextra-sw.com; juliana.oliveira@dextra-sw.com; jefferson.venancio@dextra-sw.com",
                   subject="[BarzingaNow] - Creditos do dia " + yesterday_dt.strftime('%d_%m_%y'),
                   body="Oi, total de creditos no dia "+yesterday_dt.strftime('%d_%m_%y')+" foi : "+str(total)+".")

    return "ok"
