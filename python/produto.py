from google.appengine.ext import ndb
from status import Status
from google.appengine.ext.ndb import msgprop


class Produto(ndb.Model):
    descricao = ndb.StringProperty()
    valor = ndb.FloatProperty()
