from google.appengine.ext import ndb

class Produto(ndb.Model):
    descricao = ndb.StringProperty()
    valor = ndb.FloatProperty()
