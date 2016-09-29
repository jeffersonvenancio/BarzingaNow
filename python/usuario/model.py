from google.appengine.ext import ndb

class Usuario(ndb.Model):
    nome = ndb.StringProperty()
    saldo = ndb.FloatProperty()