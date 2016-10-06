from google.appengine.ext import ndb
from user import User
from product import Product

class Transaction(ndb.Model):
    description = ndb.StringProperty()

    user = ndb.ReferenceProperty(User)
    product = ndb.ReferenceProperty(Product)