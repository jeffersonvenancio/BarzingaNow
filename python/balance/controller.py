import cloudstorage as gcs
import datetime
import json
import os
from flask import Blueprint
from google.appengine.api import app_identity

from google.appengine.api import mail

from transaction.model import Transaction
from user.model import User
from credit.model import Credit

balance = Blueprint('balance', __name__)

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
    users = User.query().filter(User.money < -0.01 and User.active == True).fetch()
    users_email_list = []

    usersJson = 'email;valor;active \n'

    for u in users:
        usersJson += str(u.email)+';'+str("%.2f" % round(u.money,2))+';'+str(u.active)+' \n'
        users_email_list.append(str(u.email))

    make_blob_public(usersJson, period, 'user_positions_'+datetime.datetime.now().strftime("%d_%m_%y"))

    if (period == 'weekly' && len(users_email_list) != 0):
        mail.EmailMessage(sender = 'financeiro@dextra-sw.com',
                        bcc = users_email_list,
                        subject = 'Barzinga: Saldo da conta',
                        html = """\ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:v="urn:schemas-microsoft-com:vml"><head><meta content="text/html; charset=utf-8" http-equiv="Content-Type"/><meta content="width=device-width" name="viewport"/><meta content="IE=edge" http-equiv="X-UA-Compatible"/><title></title><style type="text/css">body{margin: 0;padding: 0;}table,td,tr{vertical-align: top;border-collapse: collapse;}*{line-height: inherit;}a[x-apple-data-detectors=true]{color: inherit !important;text-decoration: none !important;}</style><style id="media-query" type="text/css">@media (max-width: 920px){.block-grid,.col{min-width: 320px !important;max-width: 100% !important;display: block !important;}.block-grid{width: 100% !important;}.col{width: 100% !important;}.col>div{margin: 0 auto;}img.fullwidth,img.fullwidthOnMobile{max-width: 100% !important;}.no-stack .col{min-width: 0 !important;display: table-cell !important;}.no-stack.two-up .col{width: 50% !important;}.no-stack .col.num4{width: 33% !important;}.no-stack .col.num8{width: 66% !important;}.no-stack .col.num4{width: 33% !important;}.no-stack .col.num3{width: 25% !important;}.no-stack .col.num6{width: 50% !important;}.no-stack .col.num9{width: 75% !important;}.video-block{max-width: none !important;}.mobile_hide{min-height: 0px;max-height: 0px;max-width: 0px;display: none;overflow: hidden;font-size: 0px;}.desktop_hide{display: block !important;max-height: none !important;}}</style></head><body class="clean-body" style="margin: 0; padding: 0; -webkit-text-size-adjust: 100%; background-color: transparent;"><table bgcolor="transparent" cellpadding="0" cellspacing="0" class="nl-container" role="presentation" style="table-layout: fixed; vertical-align: top; min-width: 320px; Margin: 0 auto; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: transparent; width: 100%;" valign="top" width="100%"><tbody><tr style="vertical-align: top;" valign="top"><td style="word-break: break-word; vertical-align: top;" valign="top"><div style="background-color:transparent;"><div class="block-grid" style="Margin: 0 auto; min-width: 320px; max-width: 900px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;"><div style="border-collapse: collapse;display: table;width: 100%;background-color:transparent;"><div class="col num12" style="min-width: 320px; max-width: 900px; display: table-cell; vertical-align: top; width: 900px;"><div style="background-color:#F4F4F4;width:100% !important;"><div style="border-top:0px solid transparent; border-left:0px solid transparent; border-bottom:0px solid transparent; border-right:0px solid transparent; padding-top:15px; padding-bottom:15px; padding-right: 15px; padding-left: 15px;"><div align="center" class="img-container center fixedwidth" style="padding-right: 0px;padding-left: 0px;"><img align="center" alt="Image" border="0" class="center fixedwidth" src="https://i.imgur.com/bHxH9mX.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 172px; display: block;" title="Image" width="172"/></div></div></div></div></div></div></div><div style="background-color:transparent;"><div class="block-grid" style="Margin: 0 auto; min-width: 320px; max-width: 900px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: #FFFFFF;"><div style="border-collapse: collapse;display: table;width: 100%;background-color:#FFFFFF;"><div class="col num12" style="min-width: 320px; max-width: 900px; display: table-cell; vertical-align: top; width: 900px;"><div style="width:100% !important;"><div style="border-top:0px solid transparent; border-left:0px solid transparent; border-bottom:0px solid transparent; border-right:0px solid transparent; padding-top:25px; padding-bottom:35px; padding-right: 0px; padding-left: 0px;"><div style="color:#555555;font-family:Arial, 'Helvetica Neue', Helvetica, sans-serif;line-height:1.5;padding-top:5px;padding-right:20px;padding-bottom:10px;padding-left:20px;"><div style="font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 12px; line-height: 1.5; color: #555555; mso-line-height-alt: 18px;"><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">Olá, Dextran@!</p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;"> </p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">Percebemos que seu saldo no Barzinga! está um pouco negativo.</p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">Por isso, gostaríamos de pedir que nos procure para se regularizar!</p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">(* ^ ‿ ^ *)</p></div></div><div align="center" class="img-container center fixedwidth" style="padding-right: 5px;padding-left: 5px;"><div style="font-size:1px;line-height:20px"> </div><img align="center" alt="Image" border="0" class="center fixedwidth" src="https://i.imgur.com/gkpa8Gt.gif" style="text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 360px; display: block;" title="Image" width="360"/></div></div></div></div></div></div></div><div style="background-color:transparent;"><div class="block-grid" style="Margin: 0 auto; min-width: 320px; max-width: 900px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;"><div style="border-collapse: collapse;display: table;width: 100%;background-color:transparent;"><div class="col num12" style="min-width: 320px; max-width: 900px; display: table-cell; vertical-align: top; width: 900px;"><div style="background-color:#f3f7fa;width:100% !important;"><div style="border-top:0px solid transparent; border-left:0px solid transparent; border-bottom:0px solid transparent; border-right:0px solid transparent; padding-top:15px; padding-bottom:5px; padding-right: 0px; padding-left: 0px;"><div align="center" class="img-container center fixedwidth" style="padding-right: 5px;padding-left: 5px;"><div style="font-size:1px;line-height:5px"> </div><img align="center" alt="Image" border="0" class="center fixedwidth" src="https://i.imgur.com/qGd2R2z.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 178px; display: block;" title="Image" width="178"/><div style="font-size:1px;line-height:5px"> </div></div><table border="0" cellpadding="0" cellspacing="0" class="divider" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;" valign="top" width="100%"><tbody><tr style="vertical-align: top;" valign="top"><td class="divider_inner" style="word-break: break-word; vertical-align: top; min-width: 100%; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; padding-top: 10px; padding-right: 10px; padding-bottom: 10px; padding-left: 10px;" valign="top"><table align="center" border="0" cellpadding="0" cellspacing="0" class="divider_content" height="0" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-top: 1px solid #BBBBBB; height: 0px; width: 80%;" valign="top" width="80%"><tbody><tr style="vertical-align: top;" valign="top"><td height="0" style="word-break: break-word; vertical-align: top; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;" valign="top"><span></span></td></tr></tbody></table></td></tr></tbody></table><div style="color:#555555;font-family:Arial, 'Helvetica Neue', Helvetica, sans-serif;line-height:1.2;padding-top:5px;padding-right:5px;padding-bottom:5px;padding-left:5px;"><div style="font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 12px; line-height: 1.2; color: #555555; mso-line-height-alt: 14px;"><p style="font-size: 10px; line-height: 1.2; text-align: center; mso-line-height-alt: 12px; margin: 0;"><span style="font-size: 10px; color: #999999;">Este e-mail é confidencial. Para mais informações, clique <a href="https://dextra.com.br/confidencial/" rel="noopener" style="text-decoration: underline; color: #0068A5;" target="_blank">aqui</a>.</span></p><p style="font-size: 10px; line-height: 1.2; text-align: center; mso-line-height-alt: 12px; margin: 0;"><span style="font-size: 10px; color: #999999;">Polis II de Alta Tecnologia - R. Dr. Ricardo Benetton Martins, 1000 - Prédio 11 - Bosque das Palmeiras, Campinas - SP, 13086-902</span></p></div></div><table cellpadding="0" cellspacing="0" class="social_icons" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt;" valign="top" width="100%"><tbody><tr style="vertical-align: top;" valign="top"><td style="word-break: break-word; vertical-align: top; padding-top: 5px; padding-right: 5px; padding-bottom: 10px; padding-left: 5px;" valign="top"><table activate="activate" align="center" alignment="alignment" cellpadding="0" cellspacing="0" class="social_table" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: undefined; mso-table-tspace: 0; mso-table-rspace: 0; mso-table-bspace: 0; mso-table-lspace: 0;" to="to" valign="top"><tbody><tr align="center" style="vertical-align: top; display: inline-block; text-align: center;" valign="top"><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://www.facebook.com/dextrasis" target="_blank"><img alt="Facebook" height="32" src="https://i.imgur.com/Ftl2iCe.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="Facebook" width="32"/></a></td><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://www.instagram.com/dextra_digital/" target="_blank"><img alt="Instagram" height="32" src="https://i.imgur.com/8P9Zmjj.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="Instagram" width="32"/></a></td><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://twitter.com/dextra_digital" target="_blank"><img alt="Twitter" height="32" src="https://i.imgur.com/x7vajxc.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="Twitter" width="32"/></a></td><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://www.linkedin.com/company/dextra-digital" target="_blank"><img alt="LinkedIn" height="32" src="https://i.imgur.com/cvz1c8M.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="LinkedIn" width="32"/></a></td></tr></tbody></table></td></tr></tbody></table></div></div></div></div></div></div></td></tr></tbody></table></body></html> """).Send()
    users_email_list.clear()

    return json.dumps(usersJson)

@user.route('/cron/exceeded-debit', methods=['GET'], strict_slashes=False)
def dailyDebitExceeded():
    users = User.query().filter(User.money < -40.01 and User.active == True).fetch()
    users_email_list = []

    usersJson = 'email;valor \n'

    for u in users:
        usersJson += str(u.email)+';'+str("%.2f" % round(u.money,2))+' \n'
        users_email_list.append(str(u.email))

    make_blob_public(usersJson, 'debitExceeded/', datetime.datetime.now().strftime("%d_%m_%y"))

    if (len(users_email_list) != 0):
        mail.EmailMessage(sender = 'financeiro@dextra-sw.com',
                        bcc = users_email_list,
                        subject = 'Barzinga: Saldo em débito excedido',
                        html = """\ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:v="urn:schemas-microsoft-com:vml"><head><meta content="text/html; charset=utf-8" http-equiv="Content-Type"/><meta content="width=device-width" name="viewport"/><meta content="IE=edge" http-equiv="X-UA-Compatible"/><title></title><style type="text/css">body{margin: 0;padding: 0;}table,td,tr{vertical-align: top;border-collapse: collapse;}*{line-height: inherit;}a[x-apple-data-detectors=true]{color: inherit !important;text-decoration: none !important;}</style><style id="media-query" type="text/css">@media (max-width: 920px){.block-grid,.col{min-width: 320px !important;max-width: 100% !important;display: block !important;}.block-grid{width: 100% !important;}.col{width: 100% !important;}.col>div{margin: 0 auto;}img.fullwidth,img.fullwidthOnMobile{max-width: 100% !important;}.no-stack .col{min-width: 0 !important;display: table-cell !important;}.no-stack.two-up .col{width: 50% !important;}.no-stack .col.num4{width: 33% !important;}.no-stack .col.num8{width: 66% !important;}.no-stack .col.num4{width: 33% !important;}.no-stack .col.num3{width: 25% !important;}.no-stack .col.num6{width: 50% !important;}.no-stack .col.num9{width: 75% !important;}.video-block{max-width: none !important;}.mobile_hide{min-height: 0px;max-height: 0px;max-width: 0px;display: none;overflow: hidden;font-size: 0px;}.desktop_hide{display: block !important;max-height: none !important;}}</style></head><body class="clean-body" style="margin: 0; padding: 0; -webkit-text-size-adjust: 100%; background-color: transparent;"><table bgcolor="transparent" cellpadding="0" cellspacing="0" class="nl-container" role="presentation" style="table-layout: fixed; vertical-align: top; min-width: 320px; Margin: 0 auto; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: transparent; width: 100%;" valign="top" width="100%"><tbody><tr style="vertical-align: top;" valign="top"><td style="word-break: break-word; vertical-align: top;" valign="top"><div style="background-color:transparent;"><div class="block-grid" style="Margin: 0 auto; min-width: 320px; max-width: 900px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;"><div style="border-collapse: collapse;display: table;width: 100%;background-color:transparent;"><div class="col num12" style="min-width: 320px; max-width: 900px; display: table-cell; vertical-align: top; width: 900px;"><div style="background-color:#F4F4F4;width:100% !important;"><div style="border-top:0px solid transparent; border-left:0px solid transparent; border-bottom:0px solid transparent; border-right:0px solid transparent; padding-top:15px; padding-bottom:15px; padding-right: 15px; padding-left: 15px;"><div align="center" class="img-container center fixedwidth" style="padding-right: 0px;padding-left: 0px;"><img align="center" alt="Image" border="0" class="center fixedwidth" src="https://i.imgur.com/bHxH9mX.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 172px; display: block;" title="Image" width="172"/></div></div></div></div></div></div></div><div style="background-color:transparent;"><div class="block-grid" style="Margin: 0 auto; min-width: 320px; max-width: 900px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: #FFFFFF;"><div style="border-collapse: collapse;display: table;width: 100%;background-color:#FFFFFF;"><div class="col num12" style="min-width: 320px; max-width: 900px; display: table-cell; vertical-align: top; width: 900px;"><div style="width:100% !important;"><div style="border-top:0px solid transparent; border-left:0px solid transparent; border-bottom:0px solid transparent; border-right:0px solid transparent; padding-top:25px; padding-bottom:35px; padding-right: 0px; padding-left: 0px;"><div style="color:#555555;font-family:Arial, 'Helvetica Neue', Helvetica, sans-serif;line-height:1.5;padding-top:5px;padding-right:20px;padding-bottom:10px;padding-left:20px;"><div style="font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 12px; line-height: 1.5; color: #555555; mso-line-height-alt: 18px;"><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">Olá, Dextran@!</p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;"> </p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">Nossos sistemas perceberam que sua conta no Barzinga! encontra-se muito negativa.</p><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">(╥ ﹏ ╥)</p></div></div><div align="center" class="img-container center fixedwidth" style="padding-right: 5px;padding-left: 5px;"><div style="font-size:1px;line-height:20px"> </div><img align="center" alt="Image" border="0" class="center fixedwidth" src="https://media.giphy.com/media/S6MfYgFHs00HFijzlG/giphy.gif" style="text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 360px; display: block;" title="Image" width="360"/></div><div style="color:#555555;font-family:Arial, 'Helvetica Neue', Helvetica, sans-serif;line-height:1.5;padding-top:30px;padding-right:20px;padding-bottom:10px;padding-left:20px;"><div style="font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 12px; line-height: 1.5; color: #555555; mso-line-height-alt: 18px;"><p style="font-size: 14px; line-height: 1.5; text-align: center; mso-line-height-alt: 21px; margin: 0;">Por isso, gostaríamos de pedir que nos procure para se regularizar, urgentemente!<br/>(‘• ω • `) ♡</p></div></div></div></div></div></div></div></div><div style="background-color:transparent;"><div class="block-grid" style="Margin: 0 auto; min-width: 320px; max-width: 900px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;"><div style="border-collapse: collapse;display: table;width: 100%;background-color:transparent;"><div class="col num12" style="min-width: 320px; max-width: 900px; display: table-cell; vertical-align: top; width: 900px;"><div style="background-color:#f3f7fa;width:100% !important;"><div style="border-top:0px solid transparent; border-left:0px solid transparent; border-bottom:0px solid transparent; border-right:0px solid transparent; padding-top:15px; padding-bottom:5px; padding-right: 0px; padding-left: 0px;"><div align="center" class="img-container center fixedwidth" style="padding-right: 5px;padding-left: 5px;"><div style="font-size:1px;line-height:5px"> </div><img align="center" alt="Image" border="0" class="center fixedwidth" src="https://i.imgur.com/qGd2R2z.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 178px; display: block;" title="Image" width="178"/><div style="font-size:1px;line-height:5px"> </div></div><table border="0" cellpadding="0" cellspacing="0" class="divider" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;" valign="top" width="100%"><tbody><tr style="vertical-align: top;" valign="top"><td class="divider_inner" style="word-break: break-word; vertical-align: top; min-width: 100%; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; padding-top: 10px; padding-right: 10px; padding-bottom: 10px; padding-left: 10px;" valign="top"><table align="center" border="0" cellpadding="0" cellspacing="0" class="divider_content" height="0" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-top: 1px solid #BBBBBB; height: 0px; width: 80%;" valign="top" width="80%"><tbody><tr style="vertical-align: top;" valign="top"><td height="0" style="word-break: break-word; vertical-align: top; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;" valign="top"><span></span></td></tr></tbody></table></td></tr></tbody></table><div style="color:#555555;font-family:Arial, 'Helvetica Neue', Helvetica, sans-serif;line-height:1.2;padding-top:5px;padding-right:5px;padding-bottom:5px;padding-left:5px;"><div style="font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 12px; line-height: 1.2; color: #555555; mso-line-height-alt: 14px;"><p style="font-size: 10px; line-height: 1.2; text-align: center; mso-line-height-alt: 12px; margin: 0;"><span style="font-size: 10px; color: #999999;">Este e-mail é confidencial. Para mais informações, clique <a href="https://dextra.com.br/confidencial/" rel="noopener" style="text-decoration: underline; color: #0068A5;" target="_blank">aqui</a>.</span></p><p style="font-size: 10px; line-height: 1.2; text-align: center; mso-line-height-alt: 12px; margin: 0;"><span style="font-size: 10px; color: #999999;">Polis II de Alta Tecnologia - R. Dr. Ricardo Benetton Martins, 1000 - Prédio 11 - Bosque das Palmeiras, Campinas - SP, 13086-902</span></p></div></div><table cellpadding="0" cellspacing="0" class="social_icons" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt;" valign="top" width="100%"><tbody><tr style="vertical-align: top;" valign="top"><td style="word-break: break-word; vertical-align: top; padding-top: 5px; padding-right: 5px; padding-bottom: 10px; padding-left: 5px;" valign="top"><table activate="activate" align="center" alignment="alignment" cellpadding="0" cellspacing="0" class="social_table" role="presentation" style="table-layout: fixed; vertical-align: top; border-spacing: 0; border-collapse: undefined; mso-table-tspace: 0; mso-table-rspace: 0; mso-table-bspace: 0; mso-table-lspace: 0;" to="to" valign="top"><tbody><tr align="center" style="vertical-align: top; display: inline-block; text-align: center;" valign="top"><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://www.facebook.com/dextrasis" target="_blank"><img alt="Facebook" height="32" src="https://i.imgur.com/Ftl2iCe.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="Facebook" width="32"/></a></td><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://www.instagram.com/dextra_digital/" target="_blank"><img alt="Instagram" height="32" src="https://i.imgur.com/8P9Zmjj.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="Instagram" width="32"/></a></td><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://twitter.com/dextra_digital" target="_blank"><img alt="Twitter" height="32" src="https://i.imgur.com/x7vajxc.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="Twitter" width="32"/></a></td><td style="word-break: break-word; vertical-align: top; padding-bottom: 5px; padding-right: 5px; padding-left: 5px;" valign="top"><a href="https://www.linkedin.com/company/dextra-digital" target="_blank"><img alt="LinkedIn" height="32" src="https://i.imgur.com/cvz1c8M.png" style="text-decoration: none; -ms-interpolation-mode: bicubic; height: auto; border: none; display: block;" title="LinkedIn" width="32"/></a></td></tr></tbody></table></td></tr></tbody></table></div></div></div></div></div></div></td></tr></tbody></table></body></html> """).Send()
    users_email_list.clear()

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

    mail.send_mail(sender='financeiro@dextra-sw.com',
                   to="franciane.oliveira@dextra-sw.com; juliana.oliveira@dextra-sw.com; jefferson.venancio@dextra-sw.com",
                   subject="[BarzingaNow] - Creditos do dia " + yesterday_dt.strftime('%d_%m_%y'),
                   body="Oi, total de creditos no dia "+yesterday_dt.strftime('%d_%m_%y')+" foi : "+str(total)+".")

    return "ok"
