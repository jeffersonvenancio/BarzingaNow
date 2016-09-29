from google.appengine.ext import ndb
from usuario import Usuario
from produto import Produto

class Transacao(ndb.Model):
    descricao = ndb.StringProperty()

    usuario = ndb.ReferenceProperty(Usuario)
    produto = ndb.ReferenceProperty(Produto)