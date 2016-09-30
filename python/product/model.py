from google.appengine.ext import ndb

class Product(ndb.Model):
    description = ndb.StringProperty()
    price = ndb.FloatProperty()
    qtd_available = ndb.IntegerProperty()
